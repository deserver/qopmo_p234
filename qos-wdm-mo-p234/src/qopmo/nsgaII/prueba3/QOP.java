package qopmo.nsgaII.prueba3;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionType;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.encodings.variable.Permutation;
import jmetal.util.JMException;
import qopmo.ag.Solucion;
import qopmo.wdm.Camino;
import qopmo.wdm.CanalOptico;
import qopmo.wdm.Enlace;
import qopmo.wdm.Salto;
import qopmo.wdm.qop.EsquemaRestauracion;
import qopmo.wdm.qop.Nivel;
import qopmo.wdm.qop.Servicio;

public class QOP extends Problem {
	
	
	/*
	 * Obtiene el costo total de los canales utilizados en la solución.
	 * 
	 * @return
	 */
	public int contadorCosto;
	public int cambiosLDO;
	//Valor por kilometro.
	public static double a = 0.1;
	//Valor por cambio de longitud de onda
	public static double b = 2;
	
	
	public QOP (){
		numberOfVariables_= 1;
		numberOfObjectives_ = 4;
		numberOfConstraints_ = 0; 
		problemName_ = "qop_wdm";
		
		
		solutionType_ = new RealSolutionType(this) ;
	}

	
	@Override
	public void evaluate(Solution solution) throws JMException {

		if (this.costoTotalCanales(solution)>0){
			// Costo de una Solucion
			solution.costo = this.costoTotalCanales(solution);
			// Fitness de la Solución
			solution.fitness_ = 1 / solution.costo;
			solution.setObjective(0, solution.fitness_);
			solution.evaluarProbabilidadRecuperacion();
			solution.diferenciaNiveles();	
			//solution.setObjective(3, solution.getDiferenciaNiveles());
			solution.setObjective(3, 0);
		}else{
			solution.setObjective(0, solution.fitness_);
			solution.setObjective(3, 0);
		}
		

		//return this.fitness;
	}
	
	/*
	 * Obtiene el costo total de los canales utilizados en la solución.
	 * 
	 * @return
	 */
	private double costoTotalCanales(Solution solution) {
		contadorCosto = 0;
		cambiosLDO = 0;
		solution.enlacesContado = new HashSet<Enlace>();
		/*
		 * El cálculo de las variables del costo se suman para cada gen
		 * (Servicio) del individuo (Solucion).
		 */
		for (Servicio gen : solution.genes) {

			if (gen == null || !gen.esValido())
				continue;

			// Se cuenta cada Oro que no tiene un alternativo.
			if (!gen.oroTieneAlternativo())
				solution.contadorFailOroAlternativo++;

			// Se cuenta que Nivel M que no tiene un alternativo.
			if (!gen.plataTieneAlternativo())
				solution.contadorFailPlataAlternativo++;

			/*
			 * Evaluacion Primario: Si no tiene primario se cuenta como Error.
			 * Si tiene primario se suman sus costos de Canales Opticos
			 * utilizados y se cuentan los cambios de Longitud de Onda
			 * realizados.
			 */
			Camino primario = gen.getPrimario();

			if (primario.getDestino() == null) {
				Nivel nivel = gen.getSolicitud().getNivel();
				if (nivel.esOro()) {
					solution.contadorFailOroPrimario++;
				} else if (nivel.esBronce()) {
					solution.contadorFailBroncePrimario++;
					// (nivel.ordinal() >= Nivel.Plata0.ordinal())
				} else if (nivel.esPlata()) {
					solution.contadorFailPlataPrimario++;
				}

			} else {
				// Se cuentan y suman los enlaces y cambios de longitud de onda
				// del primario.
				contadorInterno(primario.getEnlaces(), solution);
			}

			/*
			 * Evaluación Alternativo: Si tiene alternativo se suman los costos
			 * de Canales Opticos utilizados y se cuentan los cambios de
			 * Longitud de Onda realizados. Link-Oriented es un caso especial.
			 */
			if (!gen.getSolicitud().getNivel().esBronce()) {
				if (gen.getSolicitud().getEsquema() != EsquemaRestauracion.Link) {
					Camino alternativo = gen.getAlternativo();
					if (alternativo.getDestino() != null) {
						contadorInterno(alternativo.getEnlaces(),solution);
					}
				} else {
					if (gen.getAlternativoLink() != null) {
						for (Camino alternativo : gen.getAlternativoLink()) {
							if (alternativo != null) {
								contadorInterno(alternativo.getEnlaces(),solution);
							}
						}
					}
				}
			}
		}

		//Aplicacion del objetivo 1 - solicitudes bloqueadas
		if (solution.contadorFailOroPrimario!=0)
			solution.setObjective(1, solution.contadorFailOroPrimario);
		else if(solution.contadorFailPlataPrimario!=0)
			solution.setObjective(1, solution.contadorFailPlataPrimario);
		else if(solution.contadorFailBroncePrimario!=0)
			solution.setObjective(1, solution.contadorFailBroncePrimario);
		else
			solution.setObjective(1, 999);
		
		//Aplicacion del objetivo 2 - servicios sin proteccion
		if (solution.contadorFailOroAlternativo!=0)
			solution.setObjective(2, solution.contadorFailOroAlternativo);
		else if(solution.contadorFailPlataAlternativo!=0)
			solution.setObjective(2, solution.contadorFailPlataAlternativo);
		else 
			solution.setObjective(2, 999);
		
		
		// Fórmula de Costo de una Solución
		double costo = (contadorCosto * a) + (cambiosLDO * b);
		if (costo != 0)
			return costo;
		else
			return -1;
	}
	/**
	 * Cuenta la cantidad de Enlaces y los cambios de longitud de onda de los
	 * enlaces obtenidos como parámetro. Se suman a los atributos locales
	 * contadorCosto y cambiosLDO.
	 */
	private void contadorInterno(Set<Enlace> enlaces, Solution solution) {
		// Si se utiliza el auxiliar debe definirse como variable global.
		// Set<CanalOptico> auxiliar = new HashSet<CanalOptico>();
		
		Enlace e1 = null;
		Enlace e2 = null;
		int ldo1 = 0;
		int ldo2 = 0;
		boolean primero = true;
		if (enlaces == null)
			return;
		
		for (Enlace s : enlaces) {
			if (s == null)
				continue;
			CanalOptico ca = s.getCanal();

			if (primero) {
				e1 = s;
				primero = false;
			} else {
				e1 = e2;
			}
			ldo1 = e1.getLongitudDeOnda();
			e2 = s;
			ldo2 = e2.getLongitudDeOnda();

			// Si existe un cambio de longitud de onda, se suman en 1.
			if (ldo1 != ldo2)
				cambiosLDO = 0;

			// inserto es false cuando ya existía (no suma)
			// boolean inserto = auxiliar.add(ca);
			// se suman costos de Canales Opticos utilizados.
			// if (inserto)
			if (!solution.enlacesContado.contains(s)) {
				solution.enlacesContado.add(s);
				contadorCosto += ca.getCosto();
			}
		}

	}

}
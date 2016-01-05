package qopmo.ag;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import qopmo.wdm.Camino;
import qopmo.wdm.CanalOptico;
import qopmo.wdm.Enlace;
import qopmo.wdm.Salto;
import qopmo.wdm.qop.EsquemaRestauracion;
import qopmo.wdm.qop.Nivel;
import qopmo.wdm.qop.Servicio;
import qopmo.wdm.qop.Solicitud;

/**
 * Clase Solución que implementa al Individuo.
 * <p>
 * Conceptualmente esta clase es el Cromosoma del Algoritmo Genético. Tiene el
 * conjunto de genes que representan las partes de la solución: genes (Conjunto
 * de Servicios (tiene la solicitud, el camino primario y el camino
 * secundario)), su fitness y su costo.
 * </p>
 */
@Entity
@Table(name = "Solucion")
public class Solucion implements Individuo {

	@Id
	@GeneratedValue
	private long id;

	// Genes de la solución (Conjunto de Servicios)
	@ManyToMany(cascade = CascadeType.ALL)
	private Set<Servicio> genes;

	// Fitness de la Solución
	private double fitness;

	// Costo de la Solución
	private double costo;

	// Valor por kilometro.
	public static double a = 0.1;
	// Valor por cambio de longitud de onda
	public static double b = 2;

	@Transient
	private int contadorFailOroPrimario = 0;
	@Transient
	private int contadorFailPlataPrimario = 0;
	@Transient
	private int contadorFailBroncePrimario = 0;
	@Transient
	private int contadorFailOroAlternativo = 0;
	@Transient
	private int contadorFailPlataAlternativo = 0;
	@Transient
	private int diferenciaNiveles = 0;
	@Transient
	public int contadorCosto;
	@Transient
	public int cambiosLDO;
	@Transient
	private double ganancia = 0.0;
	@Transient
	private Set<Enlace> enlacesContado;

	public Solucion() {
		super();
		this.genes = new TreeSet<Servicio>();
		this.fitness = Double.MAX_VALUE;
		this.costo = Double.MAX_VALUE;

	}

	public Solucion(Individuo i) {
		Solucion s = (Solucion) i;
		genes = s.copiarGenes();
		id = s.getId();
		fitness = s.getFitness();
		costo = s.getCosto();
		contadorFailOroPrimario = s.getContadorFailOro();
		contadorFailPlataPrimario = s.getContadorFailPlata();
		contadorFailBroncePrimario = s.getContadorFailBronce();
		contadorFailOroAlternativo = s.getContadorFailOroAlternativo();
		contadorFailPlataAlternativo = s.getContadorFailPlataAlternativo();
		diferenciaNiveles = s.getDiferenciaNiveles();
		ganancia = s.getGanancia();
		contadorCosto = s.contadorCosto;
		cambiosLDO = s.cambiosLDO;
	}

	public Solucion(Set<Solicitud> solicitudes) {
		super();

		Set<Servicio> servicios = new TreeSet<Servicio>();
		for (Solicitud s : solicitudes) {
			Servicio servicio = new Servicio(s);
			servicio.setDisponible(true);
			servicios.add(servicio);
		}

		this.genes = new TreeSet<Servicio>(servicios);
		this.fitness = Double.MAX_VALUE;
		this.costo = Double.MAX_VALUE;
	}

	public double getGanancia() {
		return ganancia;
	}

	public void setGanancia(double ganancia) {
		this.ganancia = ganancia;
	}

	/**
	 * Obtener Fitness.
	 * 
	 * @return the fitness
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * Asignar Fitness.
	 * 
	 * @param fitness
	 *            de la solución
	 */
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/**
	 * @return the costo
	 */
	public double getCosto() {
		return costo;
	}

	/**
	 * @param costo
	 *            the costo to set
	 */
	public void setCosto(Double costo) {
		this.costo = costo;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TreeSet<Servicio> getGenes() {
		return (TreeSet<Servicio>) this.genes;
	}

	public void setGenes(Collection<Servicio> hijoAux) {
		this.genes = (TreeSet<Servicio>) hijoAux;
	}

	public int getContadorFailOro() {
		return contadorFailOroPrimario;
	}

	public void setContadorFailOro(int contadorFailOro) {
		this.contadorFailOroPrimario = contadorFailOro;
	}

	public int getContadorFailPlata() {
		return contadorFailPlataPrimario;
	}

	public void setContadorFailPlata(int contadorFailPlata) {
		this.contadorFailPlataPrimario = contadorFailPlata;
	}

	public int getContadorFailBronce() {
		return contadorFailBroncePrimario;
	}

	public void setContadorFailBronce(int contadorFailBronce) {
		this.contadorFailBroncePrimario = contadorFailBronce;
	}

	public int getContadorFailOroAlternativo() {
		return contadorFailOroAlternativo;
	}

	public void setContadorFailOroAlternativo(int contadorFailOroAlternativo) {
		this.contadorFailOroAlternativo = contadorFailOroAlternativo;
	}

	public int getContadorFailPlataAlternativo() {
		return contadorFailPlataAlternativo;
	}

	public void setContadorFailPlataAlternativo(int contadorFailPlataAlternativo) {
		this.contadorFailPlataAlternativo = contadorFailPlataAlternativo;
	}

	/*
	 * OPERACIONES SOBRE LA SOLUCION
	 */

	/**
	 * Función para generar Servicios Randómicos.
	 * 
	 * @param esquema
	 */
	public void random(EsquemaRestauracion esquema) {
		for (Servicio s : this.genes) {
			s.getSolicitud().setEsquema(esquema);
			s.random();
			int i = 1;
			while (!s.esValido() && i <= 5) {
				s.random();
				i++;
			}
			if (!s.esValido())
				s = s.servicioNulo();
		}
	}

	public void extremos(int i) {
		EsquemaRestauracion e = null;
		if (i == 1) {
			e = EsquemaRestauracion.FullPath;
		} else if (i == 2) {
			e = EsquemaRestauracion.Link;
		}

		for (Servicio s : this.genes) {
			s.getSolicitud().setEsquema(e);
			s.extremos();
			if (i == 1) {
				int j = 1;
				while (!s.esValido() && j <= 5) {
					s.extremos();
					j++;
				}
				if (!s.esValido())
					s = s.servicioNulo();
			} else {
				s = s.servicioNulo();
			}
			s.getSolicitud().setEsquema(EsquemaRestauracion.Segment);
		}
	}

	/**
	 * Calcula el costo en función de la Fórmula de Evaluación Definida. Tambien
	 * mantiene contadores de Alternativos no existentes, cuando deberían
	 * existir.
	 * <p>
	 * Costo = suma_de_distancia x a + suma_de_cambios_LDO x b
	 * </p>
	 */
	@Override
	public double evaluar() {

		this.contadorFailOroPrimario = 0;
		this.contadorFailOroAlternativo = 0;
		this.contadorFailPlataPrimario = 0;
		this.contadorFailPlataAlternativo = 0;
		this.contadorFailBroncePrimario = 0;

		// Costo de una Solucion
		if (this.costoTotalCanales()>0){
			this.costo = this.costoTotalCanales();
			// Fitness de la Solución
			this.fitness = 1 / this.costo;
			this.evaluarProbabilidadRecuperacion();
			this.diferenciaNiveles();	
		}
		
		return this.fitness;
	}

	public int getDiferenciaNiveles() {
		return diferenciaNiveles;
	}

	public void setDiferenciaNiveles(int diferenciaNiveles) {
		this.diferenciaNiveles = diferenciaNiveles;
	}

	private void diferenciaNiveles() {
		this.setDiferenciaNiveles(this.evaluarNivel());
		this.setGanancia(this.evaluarGananciaNivel());
	}

	/*
	 * Función para llamar a las funciones necesarias para calcular la
	 * Probabilidad de Recuperación de Cada Servicio.
	 */
	private void evaluarProbabilidadRecuperacion() {
		this.competenciasDePrimarios();
		this.competenciasDirectas();
	}

	/*
	 * Obtiene el costo total de los canales utilizados en la solución.
	 * 
	 * @return
	 */
	private double costoTotalCanales() {
		contadorCosto = 0;
		cambiosLDO = 0;
		enlacesContado = new HashSet<Enlace>();
		/*
		 * El cálculo de las variables del costo se suman para cada gen
		 * (Servicio) del individuo (Solucion).
		 */
		for (Servicio gen : this.genes) {

			if (gen == null || !gen.esValido())
				continue;

			// Se cuenta cada Oro que no tiene un alternativo.
			if (!gen.oroTieneAlternativo())
				this.contadorFailOroAlternativo++;

			// Se cuenta que Nivel M que no tiene un alternativo.
			if (!gen.plataTieneAlternativo())
				this.contadorFailPlataAlternativo++;

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
					this.contadorFailOroPrimario++;
				} else if (nivel.esBronce()) {
					this.contadorFailBroncePrimario++;
					// (nivel.ordinal() >= Nivel.Plata0.ordinal())
				} else if (nivel.esPlata()) {
					this.contadorFailPlataPrimario++;
				}

			} else {
				// Se cuentan y suman los enlaces y cambios de longitud de onda
				// del primario.
				contadorInterno(primario.getEnlaces());
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
						contadorInterno(alternativo.getEnlaces());
					}
				} else {
					if (gen.getAlternativoLink() != null) {
						for (Camino alternativo : gen.getAlternativoLink()) {
							if (alternativo != null) {
								contadorInterno(alternativo.getEnlaces());
							}
						}
					}
				}
			}
		}

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
	private void contadorInterno(Set<Enlace> enlaces) {
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
			if (!enlacesContado.contains(s)) {
				enlacesContado.add(s);
				contadorCosto += ca.getCosto();
			}

		}

	}

	/**
	 * Función que obtiene por cada Servicio los Servicios cuyo caminos
	 * primarios comparten algún Canal Óptico.
	 */
	public void competenciasDePrimarios() {

		for (Servicio gen1 : this.genes) {
			if (gen1 == null) {
				continue;
			}

			for (Servicio gen2 : this.genes) {

				if (gen2 == null || gen1.compareTo(gen2) >= 0) {
					continue;
				}
				this.contadorIguales(gen1, gen2);
			}
		}
	}

	/*
	 * Proceso que identifica y almacena los servicios cuyo par de enlaces (e y
	 * e2) del camino primario de dos Servicios (gen1 y gen2 respectivamente)
	 * están en el mismo canalOptico (definición de equals).
	 */
	private void contadorIguales(Servicio gen1, Servicio gen2) {

		Camino c1 = gen1.getPrimario();
		if (c1 == null || c1.getDestino() == null)
			return;
		Camino c2 = gen2.getPrimario();
		
		if (c2 == null || c2.getDestino() == null)
			return;

		for (Salto s : c1.getSaltos()) {
			Enlace e = s.getEnlace();
			for (Salto s2 : c2.getSaltos()) {
				Enlace e2 = s2.getEnlace();
				if (e.equals2(e2)) {
					procesarServiciosSolapados(gen1, gen2, e, e2);
					// break;
				}
			}
		}
	}

	/*
	 * Procesa los Servicios Solapados. Como los enlaces e y e2 son iguales se
	 * almacenan como servicios solapados entre los servicios comparados. Los
	 * servicios se intercambian y almacenan como Servicios Sopalados.
	 */
	private void procesarServiciosSolapados(Servicio gen1, Servicio gen2,
			Enlace e, Enlace e2) {
		Set<Servicio> servicios1 = gen1.getServiciosSolapados(e);
		servicios1.add(gen2);
		gen1.getServiciosSolapados().put(e, servicios1);
		Set<Servicio> servicios2 = gen2.getServiciosSolapados(e2);
		servicios2.add(gen1);
		gen2.getServiciosSolapados().put(e2, servicios2);

	}

	/**
	 * Función que recorre cada gen y calcula su probabilidad de Recuperación.
	 */
	public void competenciasDirectas() {

		for (Servicio gen : this.genes) {
			if (gen == null) {
				continue;
			}
			gen.calcularProbabilidad();
		}
	}

	/**
	 * Función que compara la solucion con otra. Si los valores resultantes son
	 * 0, entonces las soluciones son iguales, si los valores resultantes son
	 * menores a 0, esta solucion es mejor; y si son mayores a 0 el parametro
	 * recibido es mejor. Las prioridades siguen el siguiente orden:
	 * Primario_Oro, Secundario_Oro, Primario_Plata, Secundario_Plata, Bronce.
	 * 
	 * @param s
	 * @return
	 */
	public boolean comparar(Individuo i) {
		Solucion s = (Solucion) i;
		boolean retorno = false;
		int oroP = this.contadorFailOroPrimario;
		oroP -= s.contadorFailOroPrimario;
		int oroA = this.contadorFailOroAlternativo;
		oroA -= s.contadorFailOroAlternativo;
		int plataP = this.contadorFailPlataPrimario;
		plataP -= s.contadorFailPlataPrimario;
		int plataA = this.contadorFailPlataAlternativo;
		plataA -= s.contadorFailPlataAlternativo;
		int bronce = this.contadorFailBroncePrimario;
		bronce -= s.contadorFailBroncePrimario;
		int nivel = this.getDiferenciaNiveles() - s.getDiferenciaNiveles();
		// nivel = 0;
		double costoResultante = this.costo - s.costo;

		if (oroP == 0) {
			if (oroA == 0) {
				if (plataP == 0) {
					if (plataA == 0) {
						if (bronce == 0) {
							if (nivel == 0) {
								if (costoResultante <= 0)
									retorno = false;
								else
									retorno = true;
							} else {
								if (nivel < 0)
									retorno = false;
								else
									retorno = true;
							}
						} else {
							if (bronce < 0)
								retorno = false;
							else
								retorno = true;
						}
					} else {
						if (plataA < 0)
							retorno = false;
						else
							retorno = true;
					}
				} else {
					if (plataP < 0)
						retorno = false;
					else
						retorno = true;
				}
			} else {
				if (oroA < 0)
					retorno = false;
				else
					retorno = true;
			}
		} else {
			if (oroP < 0)
				retorno = false;
			else
				retorno = true;
		}

		return retorno;
	}

	private int evaluarNivel() {
		int valor = 0;
		for (Servicio s : this.genes) {
			valor += s.evaluarNivel();
		}
		return valor;
	}

	public double evaluarGananciaNivel() {
		double valor = 0.0;
		for (Servicio s : this.genes) {
			valor += s.getSolicitud().getNivel().ganancia(s.getpRecuperacion());
		}
		return valor;
	}

	public void imprimirCosto() {
		String retorno = "+>Costo:" + this.costo;
		retorno += ". Diff-Nivel:" + this.getDiferenciaNiveles();
		DecimalFormat formatear = new DecimalFormat("###,##0.0");
		String ganancia = formatear.format(this.ganancia);
		retorno += ". Ganancia:" + ganancia;
		String diff = ". $F$ " + this.totalFallas();
		retorno += diff;
		System.out.println(retorno);
	}

	private String totalFallas() {
		String resultado = "";
		resultado = "Oro:" + this.contadorFailOroPrimario + "-"
				+ this.contadorFailOroAlternativo + "; Medios:"
				+ this.contadorFailPlataPrimario + "-"
				+ this.contadorFailPlataAlternativo + "; Bronce:"
				+ this.contadorFailBroncePrimario + ";";
		return resultado;
	}

	public String obtenerDetalleCosto() {
		String retorno = "_" + this.contadorCosto + "_" + this.cambiosLDO + "";
		return retorno;
	}

	public String solapados() {
		String retorno = "{";
		for (Servicio s : this.genes) {
			retorno += "\nSolicitud: " + s.getSolicitud() + " - ";
			retorno += "% de Recuperación: " + s.getpRecuperacion() + "% \n";
			// for (Servicio s2 : s.getServiciosSolapados()) {
			// retorno += s2.toString() + ", ";
			// }
		}
		retorno += "} \n";
		return retorno;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Solucion other = (Solucion) obj;
		if (genes == null) {
			if (other.genes != null)
				return false;
		} else if (!genes.equals(other.genes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		int diffNivel = this.evaluarNivel();
		final int maxLen = genes.size();
		return "[Solucion(" + this.id + "):\n [fitness=" + fitness + ", costo="
				+ costo + "(" + this.contadorCosto + "#" + this.cambiosLDO
				+ "@" + diffNivel + "), genes="
				+ (genes != null ? toString(genes, maxLen) : "Vacio.") + "]";
	}

	private String toString(Set<Servicio> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append(" [\n");
		int i = 0;
		for (Iterator<Servicio> iterator = collection.iterator(); iterator
				.hasNext(); i++) {
			if (i > 0)
				builder.append(", \n");
			builder.append(iterator.next().toString());
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Función para obtener una copia de los genes
	 * 
	 * @return
	 */
	public TreeSet<Servicio> copiarGenes() {
		TreeSet<Servicio> copia = new TreeSet<Servicio>();
		for (Servicio s : this.getGenes()) {
			Servicio s1 = new Servicio(s);
			copia.add(s1);
		}
		return (TreeSet<Servicio>) copia;
	}

}

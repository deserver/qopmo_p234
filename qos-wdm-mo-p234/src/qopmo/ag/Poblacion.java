package qopmo.ag;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import qopmo.wdm.Red;
import qopmo.wdm.qop.EsquemaRestauracion;
import qopmo.ag.operadores.OperadorCruce;
import qopmo.ag.operadores.OperadorSeleccion;
import qopmo.ag.operadores.impl.CruceLink;
import qopmo.ag.operadores.impl.CrucePath;
import qopmo.ag.operadores.impl.CruceSegment;
import qopmo.ag.operadores.impl.TorneoBinario;

/**
 * Clase Población que implementar las operaciones propias de la Población.
 * <p>
 * Administrar: Individuos, hijos, fitness, operador de cruce y operador de
 * Selección.
 * </p>
 * 
 * @author mrodas
 * 
 */
public class Poblacion {

	/*
	 * Individuos de la población
	 */
	private Collection<Individuo> individuos;

	/*
	 * Hijos de los individuos selectos
	 */
	private Collection<Individuo> hijos;

	/*
	 * Operador de cruce
	 */
	private OperadorCruce operadorCruce;

	private static Red red;

	/*
	 * Operador de seleccion
	 */
	private OperadorSeleccion operadorSeleccion;

	private Individuo mejor;

	private EsquemaRestauracion esquema;

	/**
	 * Constructor de la Población.
	 * 
	 * @param individuos
	 */
	public Poblacion(Collection<Individuo> individuos) {
		this.individuos = individuos;
		this.operadorSeleccion = new TorneoBinario();
		this.operadorCruce = new CrucePath();
		this.hijos = new ArrayList<Individuo>();
		this.mejor = new Solucion();
		this.mejor.setFitness(0);

	}

	public static Red getRed() {
		return red;
	}

	public static void setRed(Red red) {
		Poblacion.red = red;
	}

	public Individuo getMejor() {
		return this.mejor;
	}

	public void setMejor(Individuo mejor) {
		Solucion s = new Solucion(mejor);
		this.mejor = s;
	}

	/**
	 * Función que obtiene el tamaño de la población
	 * 
	 * @return tamanho
	 */
	public int getTamanho() {
		return this.individuos.size();
	}

	public Collection<Individuo> getIndividuos() {
		return individuos;
	}

	public ArrayList<Individuo> getIndividuosToArray() {
		ArrayList<Individuo> a = new ArrayList<Individuo>(this.individuos);
		return a;
	}

	public void setIndividuos(List<Individuo> individuos) {
		this.individuos = individuos;
	}

	public Collection<Individuo> getHijos() {
		return hijos;
	}

	public void setHijos(List<Individuo> hijos) {
		this.hijos = hijos;
	}

	public OperadorCruce getOperadorCruce() {
		return operadorCruce;
	}

	public void setOperadorCruce(OperadorCruce operadorCruce) {
		this.operadorCruce = operadorCruce;
	}

	public OperadorSeleccion getOperadorSeleccion() {
		return operadorSeleccion;
	}

	public void setOperadorSeleccion(OperadorSeleccion operadorSeleccion) {
		this.operadorSeleccion = operadorSeleccion;
	}

	/**
	 * Se mueve la población a la siguiente generación.
	 */
	public void siguienteGeneracion() {
		// Condición de Elitismo: Se mantiene el mejor.
		// Solucion s3 = null;
		// Solucion s = new Solucion(this.mejor);
		this.hijos.add(this.mejor);
		this.individuos = this.hijos;
		this.hijos = new ArrayList<Individuo>();
		Poblacion.red.inicializar();
	}

	/**
	 * Se genera randómicamente la Población.
	 */
	public void generarPoblacion(EsquemaRestauracion esquema) {
		this.esquema = esquema;
		this.elegirCruce();

		int ind1 = 1;
		for (Individuo i : this.individuos) {
			Solucion s = (Solucion) i;
			Poblacion.red.inicializar();
			if (esquema == EsquemaRestauracion.Segment) {
				if (ind1 > 2) {
					s.random(esquema);
				} else { // condicion para incluir extremos.
					if (ind1 == 1)
						s.extremos(1);
					else
						s.extremos(2);
				}
			} else {
				s.random(esquema);
			}
			s.setId(ind1);
			ind1++;
		}

	}

	private void elegirCruce() {
		if (this.esquema == EsquemaRestauracion.FullPath)
			this.operadorCruce = new CrucePath();
		else if (this.esquema == EsquemaRestauracion.Segment)
			this.operadorCruce = new CruceSegment();
		else if (this.esquema == EsquemaRestauracion.Link)
			this.operadorCruce = new CruceLink();
	}

	/**
	 * Operación de cruce de Individuos de un conjunto selecto de individuos.
	 * <p>
	 * La operacion de cruce se realiza con los individuos ya seleccionados.
	 * </p>
	 * 
	 * @param selectos
	 */
	public void cruzar(Collection<Individuo> selectos) {

		if (selectos == null)
			throw new Error("No hay selección.");

		// Tamaño de población seleccionada
		int cantMejores = selectos.size();

		// Auxiliar de Individuos
		List<Individuo> individuos = new ArrayList<Individuo>(selectos);

		// Se inicializa la clase Random
		Random rand = new Random();
		rand.nextInt();

		for (int i = 1; i < cantMejores; i++) {

			// Se eligen a dos individuos (torneo "binario")
			int ind1 = rand.nextInt(cantMejores);
			int ind2 = rand.nextInt(cantMejores);

			// Nos aseguramos que no sean del mismo indice.
			int limite = 1;
			while (ind1 == ind2 && limite < 10) {
				ind2 = rand.nextInt(cantMejores);
				limite++;
			}

			Individuo individuo1 = individuos.get(ind1);
			Individuo individuo2 = individuos.get(ind2);
			Individuo hijo = null;
			// System.out.println("&) Cruce N°" + i);
			// System.out.println("++I1:" + individuo1);
			// System.out.println("++I2:" + individuo2);
			int limite2 = 1;
			while (individuo1.getCosto() == 0.0 && individuo2.getCosto() == 0.0
					&& limite2 < 5) {
				ind1 = rand.nextInt(cantMejores);
				individuo1 = individuos.get(ind1);
				ind2 = rand.nextInt(cantMejores);
				individuo2 = individuos.get(ind2);
				limite2++;
			}
			// Se extrae los fitness de los correspondientes individuos

			red.inicializar();
			hijo = this.operadorCruce.cruzar(individuo1, individuo2);

			// Se agrega mutación
			/*int ind3 = rand.nextInt(10);
			Solucion s2;
			if (ind3 <= 2) {
				s2 = (Solucion) hijo;
				s2.random(esquema);
			}
			*/

			this.hijos.add(hijo);
		}

	}

	public void mutar(int limite) {

		for (Individuo i : this.hijos) {
			// Se agrega mutación
			// Se inicializa la clase Random
			Random rand = new Random();
			rand.nextInt(10);
			int ind3 = rand.nextInt(10);
			Solucion s2;
			if (ind3 <= limite) {
				s2 = (Solucion) i;
				s2.random(esquema);
			}
		}
	}

	/**
	 * Evaluación de todos los individuos de la Población. Obtiene el mejor.
	 */
	public void evaluar() {

		// int j = 1;
		boolean primero = true;
		for (Individuo i : this.individuos) {
			i.evaluar();
			if (primero) {
				if (i.getCosto() > 0.0) {
					this.setMejor(i);
					primero = false;
				}
			} else {
				if (this.mejor.comparar(i)) {
					this.setMejor(i);
				}
			}
			// j++;
		}
	}

	/**
	 * Operación de seleccion de Individuos para cruzar.
	 * 
	 * @return individuos seleccionados
	 */
	public Collection<Individuo> seleccionar() {
		Collection<Individuo> selectos = this.operadorSeleccion
				.seleccionar(this);
		return selectos;
	}

	/**
	 * Función para ir almacenando los mejores de cada generación.
	 */
	public List<String> almacenarMejor(int val) {
		String generacion = "" + val;
		Solucion best = ((Solucion) this.mejor);
		String costo = "" + best.getCosto();
		String failOro = "" + best.getContadorFailOro() + "-"
				+ best.getContadorFailOroAlternativo();
		String failPlata = "" + best.getContadorFailPlata() + "-"
				+ best.getContadorFailPlataAlternativo();
		String failBronce = "" + best.getContadorFailBronce();
		String diffNiveles = "" + best.getDiferenciaNiveles();
		DecimalFormat formatear = new DecimalFormat("###,##0.00");
		String ganancia = formatear.format(best.getGanancia());
		List<String> lista = new ArrayList<String>();
		lista.add(generacion);
		lista.add(costo.replace(".", ","));
		lista.add(diffNiveles);
		lista.add(ganancia);
		lista.add(failOro);
		lista.add(failPlata);
		lista.add(failBronce);

		return lista;
	}

	@Override
	public String toString() {
		return "Poblacion [individuos="
				+ (individuos != null ? toString(individuos, individuos.size())
						: null) + "]";
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

}

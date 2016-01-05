package qopmo.prueba2;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import qopmo.util.CSVWriter;
import qopmo.wdm.Red;
import qopmo.wdm.qop.Caso;
import qopmo.wdm.qop.EsquemaRestauracion;
import qopmo.wdm.qop.Servicio;
import qopmo.ag.Individuo;
import qopmo.ag.Poblacion;
import qopmo.ag.Solucion;

/**
 * Prueba 1.2: Clase de Prueba del Algoritmo Genetico propuesto considerando
 * esquema de protección Segment-Oriented.
 * 
 * @author mrodas
 * 
 */
@RunWith(Parameterized.class)
public class SilverRecoveryProbabilitiesTest {

	// VARIABLES DE CONEXION A LA BASE DE DATOS
	private static EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("tesis");
	private static EntityManager em = emf.createEntityManager();

	// PARAMETROS DEL ALGORITMO
	// private String casoPrincipal = "CasoCNunez_10";
	private String casoPrincipal = "CasoMrodas_4";
	private static int cantidadCorridas = 1;
	private static int cantidadGeneraciones = 100000;
	private static int tamanhoPoblacion = 50;
	private static int probabilidadMutacion = 1; // 10%
	private EsquemaRestauracion esquema = EsquemaRestauracion.Segment;

	// VARIABLES AUXILIARES
	private Red NSFNET;
	public Poblacion p;
	private final Integer num;
	private final Integer caso = 4;
	private CSVWriter csv = new CSVWriter();

	/**
	 * Constructor para la Prueba 1 utilizando Orientación a Segmento.
	 * 
	 * @param x
	 */
	public SilverRecoveryProbabilitiesTest(final Integer x) {
		this.num = x;
	}

	@Parameters
	public static Collection<Integer[]> init() throws Exception {
		final Collection<Integer[]> datos = new ArrayList<Integer[]>();
		for (int i = 1; i <= cantidadCorridas; i++) {
			datos.add(new Integer[] { i });
		}

		return datos;
	}

	@Before
	public void setUp() throws Exception {
		NSFNET = em.find(Red.class, 1); // NSFnet
		NSFNET.inicializar();
	}

	@Test
	public void algoritmoGenetico() {

		System.out.println("Prueba Algoritmo Genetico. (Segment-Oriented)");
		// 0. Obtener Poblacion Inicial
		this.obtenerPoblacion(tamanhoPoblacion);
		int generacion = 1;

		while (generacion <= cantidadGeneraciones) {

			//System.out.println(" * Generación Nº " + generacion);
			p.evaluar();
			Collection<Individuo> seleccionados = p.seleccionar();
			p.cruzar(seleccionados);
			p.mutar(probabilidadMutacion);
			// System.out.println("Imprimiendo Población...");
			// System.out.println(p.toString());
			// System.out.println("Fin Impresion.");
			// p.getMejor().imprimirCosto();

			csv.addValor(p.almacenarMejor(generacion));

			generacion++;
			p.siguienteGeneracion();
		}
		p.evaluar();
		if (this.num == 0)
			this.imprimirGrafo("Segment_Mejor");
		String nombre = "" + this.num + "_Segment_Mejor_" + (generacion - 1);
		csv.generateCsvFile(nombre, this.caso);
		System.out.println(p.getMejor().toString());

		System.out.println("FIN Prueba Algoritmo Genetico. (Segment-Oriented)."
				+ this.num);
	}

	/*
	 * FUNCIONES AUXILIARES
	 */

	/*
	 * Funcion para obtener una cantidad de Individuos para la población
	 * Inicial, cuya Solicitud es la unica seteada hasta el momento.
	 */
	private Set<Individuo> obtenerPrueba(int cantidad) {

		Set<Individuo> individuos = new HashSet<Individuo>(cantidad);
		Caso prueba1 = em.find(Caso.class, this.casoPrincipal);

		for (int i = 0; i < cantidad; i++) {
			Solucion solucion = new Solucion(prueba1.getSolicitudes());

			individuos.add(solucion);
		}

		return individuos;
	}

	/*
	 * Obtiene la población Inicial a partir de la Prueba cargada.
	 */
	private void obtenerPoblacion(int tamanho) {

		// 0. Obtener individuos Iniciales.
		Set<Individuo> individuos = this.obtenerPrueba(tamanho);
		assertTrue(individuos.size() == tamanho);

		// 1. Se crea la Poblacion Inicial con los individuos iniciales.
		p = new Poblacion(individuos);
		// 2. Se carga la Red en la Poblacion.
		Poblacion.setRed(NSFNET);
		// 3. Se generan los caminos de la poblacion inicial.
		p.generarPoblacion(esquema);
		// 4. Se imprime la Poblacion Inicial
		// System.out.println(p.toString());
	}

	/*
	 * Funcion para imprimir el Grafo con sus servicios.
	 */
	private void imprimirGrafo(String id) {
		Individuo mejor = p.getMejor();
		String dir = "C:\\Users\\mrodas\\Downloads\\tesis\\test";
		int i = 1;
		for (Servicio s : mejor.getGenes()) {
			Poblacion.getRed().drawServicio(s, dir, id + "_" + i);
			i++;
		}

		Poblacion.getRed().utilizacion(dir, "");
	}

}

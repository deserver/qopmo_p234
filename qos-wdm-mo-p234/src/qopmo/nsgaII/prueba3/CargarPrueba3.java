package qopmo.nsgaII.prueba3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import qopmo.util.CSVReader;
import qopmo.wdm.CanalOptico;
import qopmo.wdm.Nodo;
import qopmo.wdm.Red;
import qopmo.wdm.qop.Caso;
import qopmo.wdm.qop.Nivel;
import qopmo.wdm.qop.Solicitud;

/**
 * <p>
 * Prueba 3. Se prueba la diferencia de costos entre conjuntos de solicitudes
 * homogeneas. Se comparan los costos de las mejores soluciones.
 * </p>
 * Caracteristicas
 * <p>
 * Red NSFNet, donde los Canales Opticos tienen 1 fibra y 55 Longitudes de Onda.
 * El algoritmo Genetico tiene una población de 50 individuos y se realizan 50
 * generaciones. Existen 2 ejemplos: de 4 y 10 Solicitudes. Se realiza 1
 * Corrida.
 * </p>
 * 
 * @author mrodas
 * 
 */
public class CargarPrueba3 {

	// Variables de conexión a la Base de Datos
	private static EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("tesis");
	private static EntityManager em = emf.createEntityManager();

	public static void main(String args[]) {
		genRedes();
	}

	public static void genRedes() {

		/* NSFnet */
		CSVReader lector = new CSVReader();
		List<List<Integer>> nsf = lector.leerRed("NSFnet.csv");
		List<Integer> cabeza = nsf.remove(0);
		persistNet2(cabeza.get(0), nsf, "NSFNet");
		System.out.println("NSF_NET=> nodos:" + cabeza.get(0) + " enlaces:"
				+ cabeza.get(1));

		cargarPruebas();
	}

	/*
	 * Función para Persistir Redes (new)
	 */
	public static void persistNet2(Integer nodos, List<List<Integer>> enlaces,
			String nombre) {

		HashMap<String, Nodo> nodoMap = new HashMap<String, Nodo>();
		Red red = new Red();
		red.setNombre(nombre);

		em.getTransaction().begin();
		for (int i = 1; i <= nodos; i++) {
			Nodo nodo = new Nodo();
			nodo.setLabel("" + i);
			nodoMap.put("" + i, nodo);
			red.addNodo(nodo);
		}
		em.persist(red);
		em.getTransaction().commit();

		em.getTransaction().begin();
		for (int i = 0; i < enlaces.size(); i++) {
			List<Integer> fila = enlaces.get(i);
			Nodo a = nodoMap.get("" + fila.get(0));
			Nodo b = nodoMap.get("" + fila.get(1));
			CanalOptico canal = new CanalOptico(a, b, fila.get(3), fila.get(4));
			canal.setCosto(fila.get(2));
			a.addCanal(canal);
			b.addCanal(canal);
			red.addCanal(canal);
		}

		em.persist(red);
		em.getTransaction().commit();
	}

	/*
	 * Carga los casos de Prueba
	 */
	private static void cargarPruebas() {
		// casos de prueba
		prueba_CNnunez10();
		System.out.println("Fin CNnunez10");
		prueba_CNnunez20();
		System.out.println("Fin CNnunez20");
		prueba_CNnunez30();
		System.out.println("Fin CNnunez30");
		prueba_CNnunez40();
		System.out.println("Fin CNnunez40");
		prueba_MRodas4();
		System.out.println("Fin MRodas4");
	}

	/*
	 * Función para cargar varias solicitudes
	 */
	public static Set<Solicitud> cargarSolicitudes(List<List<Long>> lista) {

		Set<Solicitud> solicitudes = new HashSet<Solicitud>();
		Nodo origen = null;
		Nodo destino = null;
		Nivel nivel = null;
		long i = 0;
		Solicitud sol = null;
		for (List<Long> par : lista) {
			if (par.size() < 3)
				continue;

			origen = em.find(Nodo.class, par.get(0));
			destino = em.find(Nodo.class, par.get(1));
			i = par.get(2);
			nivel = obtenerNivel(i);

			sol = new Solicitud(origen, destino, nivel);
			em.getTransaction().begin();
			em.persist(sol);
			em.getTransaction().commit();
			solicitudes.add(sol);
		}

		return solicitudes;
	}

	/*
	 * Función para asignar el Nivel de Probabilidad Correspondiente.
	 * 
	 * @param probabilidad
	 * 
	 * @return Nivel n
	 */
	private static Nivel obtenerNivel(Long probabilidad) {
		Nivel n;
		if (probabilidad >= 101.0)
			n = Nivel.Oro;
		else if (probabilidad == 100.0)
			n = Nivel.Plata0;
		else if (probabilidad >= 75.0)
			n = Nivel.Plata1;
		else if (probabilidad >= 50.0)
			n = Nivel.Plata2;
		else if (probabilidad >= 25.0)
			n = Nivel.Plata3;
		else if (probabilidad >= 0.0)
			n = Nivel.Bronce;
		else
			n = Nivel.Bronce;

		return n;
	}

	/*
	 * Funciones para cargar Casos de Pruebas del Paper de CNunez
	 */
	/*
	 * Función correspondiente a 10 Solicitudes.
	 */
	private static void prueba_CNnunez10() {

		int i = 10;
		for (i = 10; i <= 15; i++) {
			Caso c = new Caso(em.find(Red.class, 1), "CasoCNunez_" + i);

			CSVReader lector = new CSVReader();

			String nombre = "solicitudes-" + i + ".csv";
			List<List<Long>> lectura = lector.leerSolicitudes(nombre);

			Set<Solicitud> solicitudes = cargarSolicitudes(lectura);
			c.setSolicitudes(solicitudes);
			em.getTransaction().begin();
			em.persist(c);
			em.getTransaction().commit();
		}
	}

	/*
	 * Función correspondiente a 20 Solicitudes.
	 */
	private static void prueba_CNnunez20() {

		int i = 20;
		for (i = 20; i <= 25; i++) {
			Caso c = new Caso(em.find(Red.class, 1), "CasoCNunez_" + i);

			CSVReader lector = new CSVReader();
			String nombre = "solicitudes-" + i + ".csv";
			List<List<Long>> lectura = lector.leerSolicitudes(nombre);

			Set<Solicitud> solicitudes = cargarSolicitudes(lectura);
			c.setSolicitudes(solicitudes);

			em.getTransaction().begin();
			em.persist(c);
			em.getTransaction().commit();
		}

	}

	/*
	 * Función correspondiente a 30 Solicitudes.
	 */
	private static void prueba_CNnunez30() {
		int i = 30;
		for (i = 30; i <= 35; i++) {
			Caso c = new Caso(em.find(Red.class, 1), "CasoCNunez_" + i);

			CSVReader lector = new CSVReader();
			String nombre = "solicitudes-" + i + ".csv";
			List<List<Long>> lectura = lector.leerSolicitudes(nombre);

			Set<Solicitud> solicitudes = cargarSolicitudes(lectura);
			c.setSolicitudes(solicitudes);

			em.getTransaction().begin();
			em.persist(c);
			em.getTransaction().commit();
		}
	}

	/*
	 * Función correspondiente a 40 Solicitudes.
	 */
	private static void prueba_CNnunez40() {
		int i = 40;
		for (i = 40; i <= 45; i++) {
			Caso c = new Caso(em.find(Red.class, 1), "CasoCNunez_" + i);
			CSVReader lector = new CSVReader();
			String nombre = "solicitudes-" + i + ".csv";
			List<List<Long>> lectura = lector.leerSolicitudes(nombre);

			Set<Solicitud> solicitudes = cargarSolicitudes(lectura);
			c.setSolicitudes(solicitudes);

			em.getTransaction().begin();
			em.persist(c);
			em.getTransaction().commit();
		}
	}

	/*
	 * Función correspondiente a 4 Solicitudes.
	 */
	private static void prueba_MRodas4() {
		Caso c = new Caso(em.find(Red.class, 1), "CasoMrodas_3");
		CSVReader lector = new CSVReader();
		List<List<Long>> lectura = lector.leerSolicitudes("solicitudes-4.csv");

		Set<Solicitud> solicitudes = cargarSolicitudes(lectura);
		c.setSolicitudes(solicitudes);

		em.getTransaction().begin();
		em.persist(c);
		em.getTransaction().commit();
	}

}

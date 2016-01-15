package qopmo.nsgaII.prueba2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import qopmo.wdm.CanalOptico;
import qopmo.wdm.Nodo;
import qopmo.wdm.Red;
import qopmo.wdm.qop.Caso;
import qopmo.wdm.qop.Nivel;
import qopmo.wdm.qop.Solicitud;

/**
 * <p>
 * Prueba 2. Se prueba el porque es necesario establecer Niveles M. Se comparan
 * probabilidades de recuperación de Nivel de Plata. Solo se definen los Niveles
 * Oro (100%), Plata(0%), Bronce (0%).
 * </p>
 * Caracteristicas
 * <p>
 * Red NSFNet, donde los Canales Opticos tienen 1 fibra y 2 Longitudes de Onda.
 * El algoritmo Genetico tiene una población de 50 individuos y se realizan 50
 * generaciones. Existen 2 ejemplos: de 4 y 10 Solicitudes. Se realiza 1
 * Corrida.
 * </p>
 * 
 * @author mrodas
 * 
 */
public class CargarPrueba2 {

	// Variables de conexión a la Base de Datos
	private static EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("tesis");
	private static EntityManager em = emf.createEntityManager();

	// Variables de Dimensión de la Red NSF.
	private static int numeroFibras = 1;
	private static int numeroLongitudesDeOnda = 2;

	public static void main(String args[]) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("genRedes"))
				genRedes();
			if (args[0].equalsIgnoreCase("pruebasCnunez")) {
				prueba_CNnunez10();
			}
			if (args[0].equalsIgnoreCase("pruebasMrodas")) {
				prueba_MRodas4();
			}

		}
	}

	public static void genRedes() {

		/* NSFnet */
		int[][] NSFnet_enlaces = { { 1, 2, 1 }, { 1, 3, 1 }, { 1, 4, 1 },
				{ 2, 4, 1 }, { 2, 7, 1 }, { 3, 5, 1 }, { 3, 8, 1 },
				{ 4, 11, 1 }, { 5, 6, 1 }, { 5, 11, 1 }, { 6, 7, 1 },
				{ 7, 10, 1 }, { 8, 9, 1 }, { 8, 14, 1 }, { 9, 10, 1 },
				{ 9, 13, 1 }, { 10, 12, 1 }, { 10, 14, 1 }, { 11, 12, 1 },
				{ 11, 13, 1 }, { 13, 14, 1 }, };

		persistNet(14, NSFnet_enlaces, "NSFNet");
		System.out.println("NSF_NET: " + NSFnet_enlaces);
	}

	public static void persistNet(int nodos, int[][] enlaces, String nombre) {

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
		for (int i = 0; i < enlaces.length; i++) {
			Nodo a = nodoMap.get("" + enlaces[i][0]);
			Nodo b = nodoMap.get("" + enlaces[i][1]);
			CanalOptico canal = new CanalOptico(a, b, numeroFibras,
					numeroLongitudesDeOnda);
			a.addCanal(canal);
			b.addCanal(canal);
			canal.setCosto(enlaces[i][2]);
			red.addCanal(canal);
		}
		em.persist(red);
		em.getTransaction().commit();
		// casos de prueba;
		prueba_CNnunez10();
		prueba_MRodas4();
	}

	/**
	 * Función que carga el Caso 2 que corresponde a 10 Solicitudes de Plata
	 * (0%).
	 */
	private static void prueba_CNnunez10() {
		Caso c = new Caso(em.find(Red.class, 1), "CasoCNunez_10");
		// (2,15,2) (14,5,2) (4,13,2) (12,9,2) (8,14,2)
		// (4,14,1) (12,8,1) (2,14,1) (3,14,0) (9,13,0)
		Long[][] aux = { { 2L, 15L }, { 14L, 5L }, { 4L, 13L }, { 12L, 9L },
				{ 8L, 14L }, { 4L, 14L }, { 12L, 8L }, { 2L, 14L },
				{ 3L, 14L }, { 9L, 13L } };

		List<Long[]> lista = new ArrayList<Long[]>();
		for (int i = 0; i < aux.length; i++) {
			lista.add(aux[i]);
		}

		Set<Solicitud> solicitudes = cargarSolicitudes(lista);
		c.setSolicitudes(solicitudes);
		em.getTransaction().begin();
		em.persist(c);
		em.getTransaction().commit();
	}

	/**
	 * Función que carga el Caso 2 que corresponde a 4 Solicitudes de Plata.
	 */
	private static void prueba_MRodas4() {
		Caso c = new Caso(em.find(Red.class, 1), "CasoMrodas_4");
		// (1,14,2) (1,14,2) (1,14,2) (1,14,2)
		Long[][] aux = { { 2L, 15L }, { 2L, 7L }, { 13L, 4L }, { 7L, 2L } };

		List<Long[]> lista = new ArrayList<Long[]>();
		for (int i = 0; i < aux.length; i++) {
			lista.add(aux[i]);
		}

		c.setSolicitudes(cargarSolicitudes(lista));

		em.getTransaction().begin();
		em.persist(c);
		em.getTransaction().commit();
	}

	/**
	 * Función para cargar varias solicitudes
	 */
	private static Set<Solicitud> cargarSolicitudes(List<Long[]> lista) {

		Set<Solicitud> solicitudes = new HashSet<Solicitud>();
		Nodo origen = null;
		Nodo destino = null;
		Solicitud sol = null;
		for (Long[] par : lista) {
			origen = em.find(Nodo.class, par[0]);
			destino = em.find(Nodo.class, par[1]);
			sol = new Solicitud(origen, destino, Nivel.Plata10);
			em.getTransaction().begin();
			em.persist(sol);
			em.getTransaction().commit();
			solicitudes.add(sol);
		}

		return solicitudes;
	}

}

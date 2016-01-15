package qopmo.nsgaII.prueba4;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import qopmo.util.CSVReader;
import qopmo.wdm.CanalOptico;
import qopmo.wdm.Nodo;
import qopmo.wdm.Red;

/**
 * Clase Principal para cargar las Redes que están registradas en archivos csv,
 * a la base de datos para su posterior uso.
 * 
 * @author mrodas
 * 
 */
public class CargarRedes {

	private static EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("tesis");
	private static EntityManager em = emf.createEntityManager();

	public static void main(String args[]) {

		/* NSFnet */
		CSVReader lector = new CSVReader();
		List<List<Integer>> nsf = lector.leerRed("NSFnet.csv");
		List<Integer> cabeza = nsf.remove(0);
		persistNet(cabeza.get(0), nsf, "NSFNet");
		System.out.println("NSF_NET=> nodos:" + cabeza.get(0) + " enlaces:"
				+ cabeza.get(1));
/*
		/* arpanet /
		lector = new CSVReader();
		List<List<Integer>> arpa = lector.leerRed("ARPAnet.csv");
		cabeza = arpa.remove(0);
		persistNet(cabeza.get(0), arpa, "ARPANet");
		System.out.println("ARPA_NET=> nodos:" + cabeza.get(0) + " enlaces:"
				+ cabeza.get(1));

		/* chinaNet /
		lector = new CSVReader();
		List<List<Integer>> china = lector.leerRed("CHINAnet.csv");
		cabeza = china.remove(0);
		persistNet(cabeza.get(0), china, "CHINANet");
		System.out.println("CHINA_NET=> nodos:" + cabeza.get(0) + " enlaces:"
				+ cabeza.get(1));

		/* red-mesh8x8 /
		lector = new CSVReader();
		List<List<Integer>> mesh8 = lector.leerRed("red-mesh8x8.csv");
		cabeza = mesh8.remove(0);
		persistNet(cabeza.get(0), mesh8, "mesh8");
		System.out.println("MESH8X8_NET=> nodos:" + cabeza.get(0) + " enlaces:"
				+ cabeza.get(1));

		/* eufrance /
		lector = new CSVReader();
		List<List<Integer>> france = lector.leerRed("EUFRANCEnet.csv");
		cabeza = france.remove(0);
		persistNet(cabeza.get(0), france, "EUFRANCE");
		System.out.println("EUFRANCE_NET=> nodos:" + cabeza.get(0)
				+ " enlaces:" + cabeza.get(1));
	*/
	}

	/*
	 * Función para Persistir Redes (new)
	 */
	public static void persistNet(Integer nodos, List<List<Integer>> enlaces,
			String nombre) {

		HashMap<String, Nodo> nodoMap = new HashMap<String, Nodo>();
		Red red = new Red();
		red.setNombre(nombre);

		em.getTransaction().begin();
		for (long i = 1; i <= nodos; i++) {
			Nodo nodo = new Nodo();
			nodo.setId(i);
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
}

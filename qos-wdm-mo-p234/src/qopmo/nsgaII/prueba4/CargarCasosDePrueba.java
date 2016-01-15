package qopmo.nsgaII.prueba4;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import qopmo.util.CSVReader;
import qopmo.wdm.Nodo;
import qopmo.wdm.Red;
import qopmo.wdm.qop.Caso;
import qopmo.wdm.qop.Nivel;
import qopmo.wdm.qop.Solicitud;

/**
 * Clase de prueba - Test Nro 2.
 * <p>
 * Se prueba la diferencia de costos entre niveles Oro-Plata-Bronce vs Nuevos
 * Niveles. Se comparan los costos de las mejores soluciones.
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
public class CargarCasosDePrueba {

	private static EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("tesis");
	private static EntityManager em = emf.createEntityManager();

	private static int sesiones = 5;
	private static int saltos = 3;
	private static int dsvStd = 50;
	private static String nombre = "caso";
	private static String extension = ".prueba";
	private static int idRed = 1;

	public static void main(String args[]) {

		int i = 1; // número de sesiones
		int j = 1; // número de las longitudes minimas de las sesiones.
		int k = 10; // desviación standard correspondiente

		for (i = 1; i <= sesiones; i++) {
			for (j = 1; j <= saltos; j++) {
				for (k = 10; k <= dsvStd; k = k + 10) {
					String nombre2 = nombre;
					nombre2 += "_" + i + "_" + j + "_" + k;
					// nombre2 += extension;
					persistirCaso(nombre2);
					System.out.print("-"+nombre2+"-");
				}
			}
		}
	}

	/*
	 * Función para cargar un Caso particular
	 */
	private static void persistirCaso(String nombre) {
		//Caso existe = em.find(Caso.class, ar)
		Caso c = new Caso(em.find(Red.class, idRed), nombre);
		CSVReader lector = new CSVReader();
		List<List<Long>> lectura = lector.leerSolicitudes(nombre + extension);

		Set<Solicitud> solicitudes = cargarSolicitudes(lectura);
		c.setSolicitudes(solicitudes);

		em.getTransaction().begin();
		em.persist(c);
		em.getTransaction().commit();
	}
	
	/*
	 * Función para guardar las solicitudes en la BD.
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
		else if (probabilidad >= 90.0)
			n = Nivel.Plata1;
		else if (probabilidad >= 80.0)
			n = Nivel.Plata2;
		else if (probabilidad >= 70.0)
			n = Nivel.Plata3;
		else if (probabilidad == 60.0)
			n = Nivel.Plata4;
		else if (probabilidad >= 50.0)
			n = Nivel.Plata5;
		else if (probabilidad >= 40.0)
			n = Nivel.Plata6;
		else if (probabilidad >= 30.0)
			n = Nivel.Plata7;
		else if (probabilidad >= 20.0)
			n = Nivel.Plata8;
		else if (probabilidad >= 10.0)
			n = Nivel.Plata9;
		else if (probabilidad >= 0.0)
			n = Nivel.Bronce;
		else
			n = Nivel.Bronce;

		return n;
	}

}

package qopmo.nsgaII.prueba4;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import qopmo.wdm.Red;
import qopmo.wdm.qop.Caso;

/**
 * Clase para la creación de casos de prueba.
 * <p>
 * Se espera generar los casos de prueba en función a la red proveida, la
 * cantidad de sesiones, la longitud mínima y la desviación standard
 * </p>
 * 
 * @author mrodas
 * 
 */
public class CrearCasos {

	private static EntityManagerFactory emf = Persistence
			.createEntityManagerFactory("tesis");
	private static EntityManager em = emf.createEntityManager();
	private static int sesiones = 5;
	private static int saltos = 3;
	private static int dsvStd = 50;
	private static int idRed = 1; // 1 es NSFNET

	public static void main(String args[]) {

		Red NSFNET = em.find(Red.class, idRed);
		NSFNET.inicializar();
		// NSFNET.imprimirRed();
		Caso c = new Caso(NSFNET, "caso");
		c.calcularInstancia(sesiones, saltos, dsvStd);
	}
}

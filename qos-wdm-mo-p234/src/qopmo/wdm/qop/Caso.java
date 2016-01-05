package qopmo.wdm.qop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import qopmo.util.CSVWriter;
import qopmo.util.MiRandom;
import qopmo.wdm.Nodo;
import qopmo.wdm.Red;

@Entity
public class Caso {

	@Id
	private String nombre;

	@ManyToOne
	Red red;

	@OneToMany(cascade = CascadeType.ALL)
	Set<Solicitud> solicitudes;

	public Caso() {
	}

	/**
	 * Crea un caso randomico.(solo abarca oro, Plata, y Bronce)
	 * 
	 * @param red
	 *            Red
	 * @param cantSolicitudes
	 *            Cantidad de solicitudes que se generaran
	 * @param probNiveles
	 *            Probabilidades de que una solicitud tenga un nivel de calidad
	 *            dado(Oro=0,Plata=1,Bronce=2). Se asume que las probabilidades
	 *            suman 1.
	 */
	public Caso(Red red, int cantSolicitudes, double[] probNiveles) {
		this.red = red;

		solicitudes = new HashSet<Solicitud>();

		double marcaOro = probNiveles[0];
		double marcaPlata = marcaOro + probNiveles[1];

		for (int i = 0; i < cantSolicitudes; i++) {
			Solicitud s = null;

			do {
				Nodo origen = red.randomNodo();
				Nodo destino = red.randomNodo();
				Nivel nivel = null;

				while (origen.equals(destino))
					destino = red.randomNodo();

				double ruleta = Math.random();

				// solo abarca oro, Plata, y Bronce.
				if (ruleta <= marcaOro) {
					nivel = Nivel.Oro;
				} else if (ruleta <= marcaPlata) {
					nivel = Nivel.Plata1;
				} else {
					nivel = Nivel.Bronce;
				}

				s = new Solicitud(origen, destino, nivel);

			} while (solicitudes.contains(s));

			solicitudes.add(s);
		}
	}

	public Caso(Red red, String nombre) {
		this.red = red;
		this.nombre = nombre;
	}

	public Red getRed() {
		return red;
	}

	public void setRed(Red red) {
		this.red = red;
	}

	public Set<Solicitud> getSolicitudes() {
		return solicitudes;
	}

	public void setSolicitudes(Set<Solicitud> solicitudes) {
		this.solicitudes = solicitudes;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public String toString() {
		return nombre;
	}

	private void generarArchivo(int sesiones, int saltos, double dsvStd,
			String nombre) {
		red.imprimirRed();
		Set<Nodo> nodos = red.getNodos();
		HashMap<Nodo, Set<Nodo>> NodosD = new HashMap<Nodo, Set<Nodo>>();

		System.out.println("Archivo: " + nombre);
		// Se calculan posibles destinos en función al número de saltos.
		for (Nodo n : nodos) {
			// Hacer disktra
			HashMap<Nodo, Integer> distanciasA = new HashMap<Nodo, Integer>();
			Set<Nodo> nodos2 = new HashSet<Nodo>();
			distanciasA = n.dijkstra();
			int distancia = 0;

			for (Nodo n1 : nodos) {
				if (distanciasA.containsKey(n1)) {
					distancia = distanciasA.get(n1);
					if (distancia == saltos) {
						nodos2.add(n1);
					}
				}
			}
			NodosD.put(n, nodos2);
		}

		Nodo B2 = null;
		List<List<String>> solicitudes = new ArrayList<List<String>>();
		
		// Para todo origen A2
		for (Nodo A2 : nodos) {

			Set<Nodo> nodos2 = new HashSet<Nodo>();

			nodos2 = NodosD.get(A2);

			if (nodos2 == null)
				continue;

			int total = nodos2.size();

			// Para la cantidad de sesiones definidas
			for (int h = 1; h <= sesiones; h++) {
				MiRandom r1 = new MiRandom(1, total, 1); // distribución normal
				int B1 = (int) r1.random();

				int ind = 1;
				Iterator<Nodo> iter = nodos2.iterator();

				while (ind < B1) {
					iter.next();
					ind++;
				}
				// Se guarda el Destino Aleatorio de distancia "saltos"
				B2 = iter.next();
				MiRandom r2 = new MiRandom(0, 100, dsvStd);
				// Se guarda la Probabilidad Aleatoria con desviación "desvStd"
				int P1 = (int) r2.obtenerRandom();
				
				List<String> list1 = new ArrayList<String>();
				list1.add(A2.toString());
				list1.add(B2.toString());
				list1.add("" + P1);
				System.out.println("" + A2 + ";" + B2 + ";" + P1);
				
				solicitudes.add(list1);
			}
		}
		
		CSVWriter writer = new CSVWriter();
		writer.setValores(solicitudes);
		writer.generarCasoPrueba(nombre);
	}

	public void calcularInstancia(int sesiones, int saltos, int dsvStd) {

		int i = 1; // número de sesiones
		int j = 1; // número de las longitudes minimas de las sesiones.
		int k = 10; // desviación standard correspondiente

		for (i = 1; i <= sesiones; i++) {
			for (j = 1; j <= saltos; j++) {
				for (k = 10; k <= dsvStd; k = k + 10) {
					String nombre2 = nombre;
					nombre2 += "_" + i + "_" + j + "_" + k;
					nombre2 += ".prueba";
					generarArchivo(i, j, k, nombre2);
				}
			}
		}
	}
}

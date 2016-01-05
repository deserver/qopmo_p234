package qopmo.ag.operadores.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import qopmo.wdm.Camino;
import qopmo.wdm.Nodo;
import qopmo.wdm.Salto;
import qopmo.wdm.qop.Exclusividad;
import qopmo.wdm.qop.Nivel;
import qopmo.wdm.qop.Servicio;
import qopmo.ag.Individuo;
import qopmo.ag.Solucion;
import qopmo.ag.operadores.OperadorCruce;

import jmetal.core.Solution;

/**
 * Implementación específica del Cruce mencionado en paper de cnunez.
 * <p>
 * Este cruce se define asi: dada dos soluciones de entrada (s1 y s2), en una
 * primera parte (P1) se copian los Caminos cuyos genes son iguales entre los
 * genes de los padres.
 * </p>
 * <p>
 * Luego, en una segunda parte (P2), se utiliza el algoritmo SPD para completar
 * los caminos faltantes.
 * </p>
 * <p>
 * Finalmente, en una tercera parte (P3) se asignan las longitudes de onda, la
 * primera de forma aleatoria, y luego se trata de mantener dicha longitud de
 * onda. En el caso que no se encuentra disponible la longitud de onda se
 * procederá a elegir otro randomicamente. Así sucesivamente hasta llegar al
 * final del camino.
 * </p>
 */
public class CruceLink implements OperadorCruce {

	@Override
	public Individuo cruzar(Individuo i1, Individuo i2) {
		Solution s1 = (Solution) i1;
		Solution s2 = (Solution) i2;

		Collection<Servicio> hijoAux = new ArrayList<Servicio>();
		Solution hijo = new Solution(1);

		List<Nodo> primeros = new ArrayList<Nodo>();
		List<Nodo> iguales = new ArrayList<Nodo>();

		Camino nuevoPrimario = null;
		Iterator<Servicio> iterador1 = s1.getGenes().iterator();
		Iterator<Servicio> iterador2 = s2.getGenes().iterator();

		Servicio gen1 = null;
		Servicio gen2 = null;

		/*
		 * Cálculos del Nuevo Camino Primario y del Nuevo Camino Secundario.
		 */
		while (iterador1.hasNext() && iterador2.hasNext()) {

			// i++;
			// System.out.println("Generación: " + i);
			iguales.clear();
			primeros.clear();
			gen1 = iterador1.next();
			gen2 = iterador2.next();
			Camino primario1 = gen1.getPrimario();
			Camino primario2 = gen2.getPrimario();
			Nivel nivel = gen1.getSolicitud().getNivel();
			Exclusividad exclusividadPrimario = gen1.getSolicitud()
					.getExclusividadPrimario();

			/*
			 * P0. Se controla que los caminos principales existan. Si no
			 * existen se retorna un servicio nulo y si solo no existe en uno,
			 * se retorna el otro servicio padre.
			 */
			if (!gen1.esValido() && !gen2.esValido()) {
				hijoAux.add(gen1.servicioNulo());
				continue;
			} else if (!gen1.esValido()) {
				gen2.setPrimario();
				if (nivel != Nivel.Bronce) {
					gen2.setAlternativo();
				}
				if (gen2.esValido())
					hijoAux.add(gen2);
				else
					hijoAux.add(gen1.servicioNulo());
				continue;
			} else if (!gen2.esValido()) {
				gen1.setPrimario();
				if (nivel != Nivel.Bronce) {
					gen1.setAlternativo();
				}
				if (gen1.esValido())
					hijoAux.add(gen1);
				else
					hijoAux.add(gen2.servicioNulo());
				continue;
			}

			Nodo nodo = primario1.getOrigen();
			primeros.add(nodo);
			iguales.add(nodo);
			/*
			 * P1.1. Se obtienen los Nodos del primer padre.
			 */
			for (Salto salto : primario1.getSaltos()) {
				nodo = salto.getCanal().getOtroExtremo(nodo);
				primeros.add(nodo);
			}

			nodo = primario2.getOrigen();

			/*
			 * P1.2. Se copian los Nodos iguales entre los caminos de los
			 * padres.
			 */
			for (Salto salto : primario2.getSaltos()) {
				nodo = salto.getCanal().getOtroExtremo(nodo);
				if (primeros.contains(nodo)) {
					iguales.add(nodo);
				}
			}

			Nodo inicio = primario1.getOrigen();
			Nodo fin = primario1.getDestino();

			// se carga el nuevo Camino Primario.
			nuevoPrimario = new Camino(inicio);

			/*
			 * P2.1. Para cada uno de los Nodos iguales se realiza dijkstra en
			 * el orden en que fueron insertados. Si en el proceso el nodo
			 * destino ya se encuenta en el camino creado, se continua al
			 * siguiente nodo.
			 */
			for (Nodo next : iguales) {

				// Se continua si ya existe en el camino creado.
				if (nuevoPrimario.contiene(next))
					continue;

				// Camino subCamino = inicio.busquedaAnchura1Nivel(next,
				// exclusividadPrimario);

				// if (subCamino == null)
				Camino subCamino = inicio.dijkstra(next, exclusividadPrimario);

				// Condición que si es cierta, ocurrio un error
				if (subCamino == null) {
					nuevoPrimario.desbloquearCanales();
					nuevoPrimario = null;
					break;

				}
				// if (!next.equals(fin) && subCamino.contiene(fin)) {
				// System.out.println ("Especial: "+next+" Fin: "+fin);
				// nuevoPrimario.desbloquearCanales();
				// nuevoPrimario = null;
				// break;
				// }

				// se va anexando el camino creado.
				nuevoPrimario.anexar(subCamino);
				// luego de agregar, se bloquea los Canales.
				subCamino.bloquearCanales();
				inicio = next;
			}

			// Si no se encontro camino entonces se guarda un servicio null.
			if (nuevoPrimario == null) {
				hijoAux.add(gen1.servicioNulo());
				continue;
			}
			// Al finalizar el camino Primario se desbloquean los nodos.
			// nuevoPrimario.desbloquearCanales();

			/*
			 * P2.2. Se crea el nuevo Servicio y se asigna su Camino Primario.
			 */
			Servicio newServicio = new Servicio(gen1.getSolicitud());
			newServicio.setPrimario(nuevoPrimario);
			newServicio.setPrimario();

			/*
			 * P3.1. Cargar Nuevo Secundario: Realizar algoritmo Shortest Path
			 * Disjktra (SPD) desde el Nodo inicio al Nodo fin.
			 */
			if (nivel != Nivel.Bronce) {
				newServicio.buscarAlternativo();
				newServicio.setAlternativo();
			}
			/*
			 * P3.2. Se almacena el Nuevo Servicio (gen), a un conjunto
			 * auxiliar.
			 */
			if (newServicio.esValido())
				hijoAux.add(newServicio);
			else
				hijoAux.add(gen1.servicioNulo());

		}

		/*
		 * P4.1. Se almacena el nuevo conjunto de Nuevos Servicios en el
		 * Conjunto Hijo.
		 */
		Collection<Servicio> aux = new TreeSet<Servicio>(hijoAux);
		hijo.setGenes(aux);

		return hijo;
	}
}

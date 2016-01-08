package qopmo.ag;

import java.util.TreeSet;

import qopmo.wdm.qop.Servicio;
import jmetal.core.Solution;

/**
 * Interface del Individuo que define las operaciones propias del individuo.
 * 
 * @author mrodas
 * 
 */
public interface Individuo {

	/**
	 * Función para calcular el Fitness del Individuo.
	 * 
	 * @return fitness del Individuo
	 */
	public double evaluar();

	/**
	 * Funcion para comparar 2 individuos e indicar el mejor.
	 * 
	 * @return
	 */
	public int comparar(Solution i);
	
	public boolean comparar(Individuo i);

	/**
	 * Función que obtiene el Costo vinculado al Individuo.
	 * 
	 * @return el costo
	 */
	public double getCosto();

	/**
	 * Función para asignar el fitness.
	 * 
	 * @param fitness
	 */
	public void setFitness(double fitness);
	
	/**
	 * Función para obtener los genes del Individuo.
	 * @return
	 */
	public TreeSet<Servicio> getGenes();

}

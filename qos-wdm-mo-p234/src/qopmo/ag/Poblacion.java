package qopmo.ag;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.Configuration;
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

	public Solution mejor;

	private EsquemaRestauracion esquema;
	
	private List<Solution> solutionsList;
	
	private int capacity;

	/**
	 * Constructor de la Población.
	 * 
	 * @param individuos
	 */
	public Poblacion(Collection<Individuo> individuos, int capacity) {
		this.individuos = individuos;
		this.operadorSeleccion = new TorneoBinario();
		this.operadorCruce = new CrucePath();
		this.hijos = new ArrayList<Individuo>();
		this.mejor = new Solution();
		this.mejor.setFitness(0);
		this.solutionsList = new ArrayList<Solution>();
		this.capacity = capacity;
		
	}
	
	public Poblacion(int maximo){
		this.operadorSeleccion = new TorneoBinario();
		this.operadorCruce = new CrucePath();
		this.hijos = new ArrayList<Individuo>();
		this.mejor = new Solution();
		this.mejor.setFitness(0);
		this.solutionsList = new ArrayList<Solution>();
		this.capacity = maximo;
	}
	
	public void copiarPoblacion(Poblacion population){
		this.hijos = population.getHijos();
		this.individuos = population.getIndividuos();
		//this.solutionsList = population.getSolutionList();
		for (Solution s : population.getSolutionList()){
			this.solutionsList.add(s);
		}
	}
	
	public Poblacion union(Poblacion population){
		int newSize = this.size() + population.size();
		if (newSize < this.capacity)
			newSize = this.capacity;
		
	    Poblacion union = new Poblacion(newSize);                
	    for (int i = 0; i < this.size(); i++) {   
	    	if (this.get(i).caminoValido())
	    		union.add(this.get(i));
	    } // for

	    for (int i = this.size(); i < (this.size() + population.size()); i++) {
	    	if (population.get(i-this.size()).caminoValido())
	    		union.add(population.get(i-this.size()));
	    } // for
	    
		return union;
	}
	public Poblacion unionsincontrol(Poblacion population){
		int newSize = this.size() + population.size();
		if (newSize < this.capacity)
			newSize = this.capacity;
		
	    Poblacion union = new Poblacion(newSize);                
	    for (int i = 0; i < this.size(); i++) {   
	    		union.add(this.get(i));
	    } // for

	    for (int i = this.size(); i < (this.size() + population.size()); i++) {
	    		union.add(population.get(i-this.size()));
	    } // for
	    
		return union;
	}

	public List<Solution> getSolutionList(){
		return this.solutionsList;
	}
	
	public static Red getRed() {
		return red;
	}

	public static void setRed(Red red) {
		Poblacion.red = red;
	}

	public Solution getMejor() {
		return this.mejor;
	}

	public void setMejor(Solution mejor) {
		Solution s = new Solution(mejor);
		this.mejor = s;
	}
	
	public void setCapacity(int maximo){
		this.capacity = maximo;
	}
	
	public int getCapacity(){
		return this.capacity;
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
	
	public void cargarSolutionList(Collection<Individuo> individuos){
		Solution newSolution;
		for (int i=0; i<individuos.size(); i++){
			newSolution = new Solution();
			this.solutionsList.add(newSolution);
		}
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
		//this.hijos.add(this.mejor);
		this.individuos = this.hijos;
		this.hijos = new ArrayList<Individuo>();
		Poblacion.red.inicializar();
	}
	
	public void siguienteGeneracion(Poblacion mejores) {
		// Condición de Elitismo: Se mantiene el mejor.
		// Solucion s3 = null;
		// Solucion s = new Solucion(this.mejor);
		//this.hijos.add(this.mejor);
		if (mejores != null){
			for (Solution s : mejores.getSolutionList()){
				this.hijos.add((Individuo) s);
			}
		}
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
			Solution s = (Solution) i;
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
	public void cruzar(Collection<Individuo> selectos, int probMutacion) {

		//Solution[] solucion = new Solution[selectos.size()];

		if (selectos == null)
			throw new Error("No hay selección.");

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
			hijo =this.operadorCruce.cruzar(individuo1, individuo2);
			
			//hijo = mutar(hijo, probMutacion);//Mutar con probabilidad probMutacion

			this.hijos.add(hijo);
		}
		
		//return solucion;

	}

	public Individuo mutar(Individuo i, int limite) {
		// Se agrega mutación
		// Se inicializa la clase Random
		Random rand = new Random();
		rand.nextInt(10);
		int ind3 = rand.nextInt(10);
		if (ind3 <= limite) {
			Solution s2 = new Solution(i);
			s2.random(esquema);
			i = (Individuo) s2;
		}
		
		return i;
	}

	/**
	 * Evaluación de todos los individuos de la Población. Obtiene el mejor.
	 */
	public void evaluar() {
		boolean primero = true;
		for (Individuo i : this.individuos) {
			i.evaluar();
			/*if (primero) {
				if (i.getCosto()>0.0){
					this.mejor = (Solution) i;
					primero = false;
				}
			} else {
				if (this.mejor.comparar(i))
					this.mejor = (Solution) i;
			}*/
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
	public List<String> almacenarMejor(int val, Solution solution) {
		String generacion = "" + val;
		Solution best = ((Solution) solution);
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
	
	/*
	 * SolucionList operations
	 */
	
	  /** 
	   * Sorts a SolutionSet using a <code>Comparator</code>.
	   * @param comparator <code>Comparator</code> used to sort.
	   */
	  public void sort(Comparator comparator){
	    if (comparator == null) {
	      Configuration.logger_.severe("No criterium for comparing exist");
	      return ;
	    } // if
	    Collections.sort(solutionsList,comparator);
	  } // sort
	
	  /** 
	   * Empties the SolutionSet
	   */
	  public void clear(){
	    solutionsList.clear();
	    //individuos.clear();
	    //hijos.clear();
	  } // clear

	  /** 
	   * Inserts a new solution into the SolutionSet. 
	   * @param solution The <code>Solution</code> to store
	   * @return True If the <code>Solution</code> has been inserted, false 
	   * otherwise. 
	   */
	  public boolean add(Solution solution) {
	    if (solutionsList.size() == capacity) {
	      Configuration.logger_.severe("The population is full");
	      Configuration.logger_.severe("Capacity is : "+capacity);
	      Configuration.logger_.severe("\t Size is: "+ this.size());
	      return false;
	    } // if

	    solutionsList.add(solution);
	    return true;
	  } // add
	  
	  public int size(){
		  return solutionsList.size();
	  } // size
	  
	  /**
	   * Returns the ith solution in the set.
	   * @param i Position of the solution to obtain.
	   * @return The <code>Solution</code> at the position i.
	   * @throws IndexOutOfBoundsException Exception
	   */
	  public Solution get(int i) {
	    if (i >= solutionsList.size()) {
	      throw new IndexOutOfBoundsException("Index out of Bound "+i);
	    }
	    return solutionsList.get(i);
	  } // get
	  
	  /** 
	   * Returns the index of the best Solution using a <code>Comparator</code>.
	   * If there are more than one occurrences, only the index of the first one is returned
	   * @param comparator <code>Comparator</code> used to compare solutions.
	   * @return The index of the best Solution attending to the comparator or 
	   * <code>-1<code> if the SolutionSet is empty
	   */
	   int indexBest(Comparator comparator){
	    if ((solutionsList == null) || (this.solutionsList.isEmpty())) {
	      return -1;
	    }

	    int index = 0; 
	    Solution bestKnown = solutionsList.get(0), candidateSolution;
	    int flag;
	    for (int i = 1; i < solutionsList.size(); i++) {        
	      candidateSolution = solutionsList.get(i);
	      flag = comparator.compare(bestKnown, candidateSolution);
	      if (flag == +1) {
	        index = i;
	        bestKnown = candidateSolution; 
	      }
	    }

	    return index;
	  } // indexBest
	   
	   /** 
	    * Returns the best Solution using a <code>Comparator</code>.
	    * If there are more than one occurrences, only the first one is returned
	    * @param comparator <code>Comparator</code> used to compare solutions.
	    * @return The best Solution attending to the comparator or <code>null<code>
	    * if the SolutionSet is empty
	    */
	   public Solution best(Comparator comparator){
	     int indexBest = indexBest(comparator);
	     if (indexBest < 0) {
	       return null;
	     } else {
	       return solutionsList.get(indexBest);
	     }

	   } // best  
	   
	   /** 
	    * Deletes the <code>Solution</code> at position i in the set.
	    * @param i The position of the solution to remove.
	    */
	   public void remove(int i){        
	     if (i > solutionsList.size()-1) {            
	       Configuration.logger_.severe("Size is: "+this.size());
	     } // if
	     solutionsList.remove(i);    
	   } // remove
	   
	   /**
	    * Write the function values of feasible solutions into a file
	    * @param path File name
	    */
	   public void printFeasibleFUN(String path) {
	     try {
	       FileOutputStream fos   = new FileOutputStream(path)     ;
	       OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
	       BufferedWriter bw      = new BufferedWriter(osw)        ;

	       for (Solution aSolutionsList_ : solutionsList) {
	         if (aSolutionsList_.getOverallConstraintViolation() == 0.0) {
	           bw.write(aSolutionsList_.toString());
	           bw.newLine();
	         }
	       }
	       bw.close();
	     }catch (IOException e) {
	       Configuration.logger_.severe("Error acceding to the file");
	       e.printStackTrace();
	     }
	   }

	   /**
	    * Write the encodings.variable values of feasible solutions into a file
	    * @param path File name
	    */
	   public void printFeasibleVAR(String path) {
	     try {
	       FileOutputStream fos   = new FileOutputStream(path)     ;
	       OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
	       BufferedWriter bw      = new BufferedWriter(osw)        ;            

	       if (size()>0) {
	         int numberOfVariables = solutionsList.get(0).getDecisionVariables().length ;
	         for (Solution aSolutionsList_ : solutionsList) {
	           if (aSolutionsList_.getOverallConstraintViolation() == 0.0) {
	             for (int j = 0; j < numberOfVariables; j++)
	               bw.write(aSolutionsList_.getDecisionVariables()[j].toString() + " ");
	             bw.newLine();
	           }
	         }
	       }
	       bw.close();
	     }catch (IOException e) {
	       Configuration.logger_.severe("Error acceding to the file");
	       e.printStackTrace();
	     }       
	   }
	   public void printParcialResults(){
	     try {
		       for (Solution aSolutionsList_ : solutionsList) {
		         //if (this.vector[i].getFitness()<1.0) {
		         //System.out.println(aSolutionsList_.toString());
		         aSolutionsList_.imprimirCosto();
		         //}
		       }
		     }catch (Exception e) {
		       Configuration.logger_.severe("Error acceding to the file");
		       e.printStackTrace();
		     }
	   }
	   
	   public void printFinalResults(){
		   
		     try {
		    	 if (solutionsList!=null){
					   for (Solution aSolutionsList_ : solutionsList) {
						     //if (this.vector[i].getFitness()<1.0) {
						     System.out.println(aSolutionsList_.toString());
						     //aSolutionsList_.imprimirCosto();
						     //}
					   }
		    	 }
		     }catch (Exception e) {
		       Configuration.logger_.severe("Error acceding to the file");
		       e.printStackTrace();
		     }
		   }


	   /** 
	    * Writes the objective function values of the <code>Solution</code> 
	    * objects into the set in a file.
	    * @param path The output file name
	    */
	   public void printObjectivesToFile(String path){
	     try {
	       /* Open the file */
	       FileOutputStream fos   = new FileOutputStream(path)     ;
	       OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
	       BufferedWriter bw      = new BufferedWriter(osw)        ;

	       for (Solution aSolutionsList_ : solutionsList) {
	         //if (this.vector[i].getFitness()<1.0) {
	         bw.write(aSolutionsList_.toString());
	         System.out.println(aSolutionsList_.toString());
	         aSolutionsList_.imprimirCosto();
	         System.out.println("");
	         //System.out.println(this.almacenarMejor(0, aSolutionsList_));
	         bw.newLine();
	         //}
	       }

	       /* Close the file */
	       bw.close();
	     }catch (IOException e) {
	       Configuration.logger_.severe("Error acceding to the file");
	       e.printStackTrace();
	     }
	   } // printObjectivesToFile

	   /**
	    * Writes the decision encodings.variable values of the <code>Solution</code>
	    * solutions objects into the set in a file.
	    * @param path The output file name
	    */
	   public void printVariablesToFile(String path){
	     try {
	       FileOutputStream fos   = new FileOutputStream(path,true)     ;
	       OutputStreamWriter osw = new OutputStreamWriter(fos)    ;
	       BufferedWriter bw      = new BufferedWriter(osw)        ;            

	       if (size()>0) {
	    	 //int numberOfVariables = solutionsList.get(0).getDecisionVariables().length ;
	         int numberOfVariables = solutionsList.size();
	         for (Solution aSolutionsList_ : solutionsList) {
	           for (int j = 0; j < numberOfVariables; j++){
	             //bw.write(aSolutionsList_.getDecisionVariables()[j].toString() + " ");
	           	 bw.write(aSolutionsList_.imprimirCosto2());
	           	 bw.write(aSolutionsList_.toString());
	           	 //System.out.println(aSolutionsList_.toString());
	           }
	             
	           bw.newLine();
	         }
	       }
	       bw.close();
	     }catch (IOException e) {
	       Configuration.logger_.severe("Error acceding to the file");
	       e.printStackTrace();
	     }       
	   } // printVariablesToFile

}

//  NSGAII.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package qopmo.nsgaII.prueba2;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;

import jmetal.core.*;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.comparators.CrowdingComparator;

import qopmo.ag.operadores.impl.TorneoBinario;
import qopmo.wdm.Camino;
import qopmo.wdm.Red;
import qopmo.wdm.qop.Caso;
import qopmo.wdm.qop.EsquemaRestauracion;
import qopmo.util.CSVWriter;
import qopmo.ag.operadores.*;
import qopmo.ag.Individuo;
import qopmo.ag.Poblacion;
import qopmo.ag.Solucion;
import qopmo.wdm.qop.Servicio;

/** 
 *  Implementation of NSGA-II.
 *  This implementation of NSGA-II makes use of a QualityIndicator object
 *  to obtained the convergence speed of the algorithm. This version is used
 *  in the paper:
 *     A.J. Nebro, J.J. Durillo, C.A. Coello Coello, F. Luna, E. Alba 
 *     "A Study of Convergence Speed in Multi-Objective Metaheuristics." 
 *     To be presented in: PPSN'08. Dortmund. September 2008.
 */

public class NSGAII extends Algorithm {
  /**
   * Constructor
   * @param problem Problem to solve
   */
  public NSGAII(Problem problem) {
    super (problem) ;
	/*this.num = x;
	if (this.num <= 10) {
		this.caso = 40;
	} else if (this.num <= 20) {
		this.caso = 30;
	} else if (this.num <= 30) {
		this.caso = 20;
	} else if (this.num <= 40) {
		this.caso = 10;
	} else {
		this.caso = 1;
	}*/
	//this.casoPrincipal += this.caso;
  } // NSGAII
	  
  private static EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("tesis");
  private static EntityManager em = emf.createEntityManager();
  private String casoPrincipal = "CasoMrodas_4";
  
  public Poblacion population;
  public Red NSFNET;
  private EsquemaRestauracion esquema = EsquemaRestauracion.Segment;
  private CSVWriter csv = new CSVWriter();
  public Integer caso;
  private Integer num;
  
  Problem   problem   ; // The problem to solve
  Algorithm algorithm ; // The algorithm to use

  /**   
   * Runs the NSGA-II algorithm.
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws JMException 
   */
  @Test
  public Poblacion execute() throws JMException, ClassNotFoundException {
    int populationSize;
    int maxEvaluations;
    int evaluations;
    int probMutacion;
    
    int requiredEvaluations; // Use in the example of use of the
    // indicators object (see below)

    //Poblacion population;
    //SolutionSet population;
    Poblacion offspringPopulation;
    Poblacion union;
    Poblacion copyPopulation;

    Operator mutationOperator;
    Operator crossoverOperator;
    Operator selectionOperator;

    Distance distance = new Distance();

    //Read the parameters
    populationSize = ((Integer) getInputParameter("populationSize")).intValue();
    maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
    probMutacion   = ((Integer) getInputParameter("probMutacion")).intValue();
    

    //Initialize the variables
    //population = new SolutionSet(populationSize);
    evaluations = 0;

    
    //Initialize Nsfnet
	NSFNET = em.find(Red.class, 1); // NSFnet
	NSFNET.inicializar();
    
    this.obtenerPoblacion(populationSize);
	

    requiredEvaluations = 0;

    //Read the operators
    //mutationOperator = operators_.get("mutation");
    //crossoverOperator = operators_.get("crossover");
    //selectionOperator = operators_.get("selection");
    OperadorSeleccion seleccionOp = new TorneoBinario();

    // Create the initial solutionSet
    Solution newSolution;
    //population = new Poblacion(populationSize);
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem_);
      problem_.evaluate(newSolution);
      problem_.evaluateConstraints(newSolution);
      evaluations++;
      population.add(newSolution);
    } //for       

    Ranking ranking = new Ranking(population);
    evaluations = 0;
    int cantIt = 0;
    int size, tamanho = 0;
    // Generations 
    
    while (evaluations < maxEvaluations) {
    	
    	size = population.getIndividuos().size();
    	tamanho = (size*size)+size;

    	offspringPopulation = new Poblacion(tamanho);
    	copyPopulation = new Poblacion(tamanho);
      
      //population.evaluar();
      
      for (int i = 0; i < (populationSize); i++) {
    	  if (evaluations<maxEvaluations){
	    	  

		      for (Individuo ind : population.getIndividuos()){
		    	  Solution s = (Solution) ind;
		    	  s.setNumberOfObjectives(problem_.getNumberOfObjectives());
		    	  problem_.evaluate(s);
		    	  //if (s.getCosto()<2.8)
		    		  //System.out.println(s.getCosto());
		    	  if (s.getCosto() > 0 && s.caminoValido())
		        	  offspringPopulation.add(s);

		      }
	        	

	          Collection<Individuo> selectos = seleccionOp.seleccionar(population);
	          
	          population.cruzar(selectos, probMutacion);
	          /*copyPopulation.copiarPoblacion(offspringPopulation);
	          Poblacion mejores = getFront(copyPopulation);
	          population.siguienteGeneracion(mejores);*/
	          population.siguienteGeneracion();
	          
	          evaluations++;
	          
    	  }//if                       
      } // for

	      for (Individuo ind : population.getIndividuos()){
	    	  Solution s = (Solution) ind;
	    	  s.setNumberOfObjectives(problem_.getNumberOfObjectives());
	    	  problem_.evaluate(s);
	    	  if (s.getCosto() > 0 && s.caminoValido())
	        	  offspringPopulation.add(s);
	      }
      // Create the solutionSet union of solutionSet and offSpring
     // union = ((SolutionSet) population).union(offspringPopulation);
      union = offspringPopulation.union(population);
    	  
      //union = offspringPopulation;
      // Ranking the union
      ranking = new Ranking(union);

      int remain = populationSize;
      int index = 0;
      Poblacion front = null;
      population.clear();

      // Obtain the next front
      front = ranking.getSubfront(index);

      while ( front!= null && (remain > 0)  && (remain >= front.size())) {
        //Assign crowding distance to individuals
        distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
        //Add the individuals of this front
        for (int k = 0; k < front.size(); k++) {
          population.add(front.get(k));
        } // for

        //Decrement remain
        remain = remain - front.size();

        //Obtain the next front
        index++;
        if (remain > 0) {
          front = ranking.getSubfront(index);
        } // if        
      } // while

      if (front != null){
	      // Remain is less than front(index).size, insert only the best one
	      if (remain > 0) {  // front contains individuals to insert                        
	        distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
	        front.sort(new CrowdingComparator());
	        for (int k = 0; k < remain; k++) {
	          population.add(front.get(k));
	        } // for
      }else{
    	  break;
      }
	     
        remain = 0;
      } // if    

      // This piece of code shows how to use the indicator object into the code
      // of NSGA-II. In particular, it finds the number of evaluations required
      // by the algorithm to obtain a Pareto front with a hypervolume higher
      // than the hypervolume of the true Pareto front.
      /*if ((indicators != null) &&
          (requiredEvaluations == 0)) {
        double HV = indicators.getHypervolume(population);
        if (HV >= (0.98 * indicators.getTrueParetoFrontHypervolume())) {
          requiredEvaluations = evaluations;
        } // if
      } // if*/
    } // while
    cantIt++;
    

    // Return as output parameter the required evaluations
    setOutputParameter("evaluations", requiredEvaluations);

    // Return the first non-dominated front
    ranking = new Ranking(population);
    if (ranking.getSubfront(0) != null)
    	ranking.getSubfront(0).printFeasibleFUN("FUN_NSGAII") ;

    
    


	return ranking.getSubfront(0);
	

	
  } // execute

  /*
	 * Funcion para obtener una cantidad de Individuos para la población
	 * Inicial, cuya Solicitud es la unica seteada hasta el momento.
	 */
	private Set<Individuo> obtenerPrueba(int cantidad) {
	
		Set<Individuo> individuos = new HashSet<Individuo>(cantidad);
		Caso prueba1 = em.find(Caso.class, casoPrincipal);
	
		for (int i = 0; i < cantidad; i++) {
			Solution solucion = new Solution(prueba1.getSolicitudes());
			
			individuos.add(solucion);
		}

	
		return individuos;
	}//obtenerPrueba
	
	/*
	 * Obtiene la población Inicial a partir de la Prueba cargada.
	 */
	private void obtenerPoblacion(int tamanho) {
	
		// 0. Obtener individuos Iniciales.
		Set<Individuo> individuos = this.obtenerPrueba(tamanho);
	
		// 1. Se crea la Poblacion Inicial con los individuos iniciales.
		population = new Poblacion(individuos, tamanho);
		// 2. Se carga la Red en la Poblacion.
		Poblacion.setRed(NSFNET);
		// 3. Se generan los caminos de la poblacion inicial.
		population.generarPoblacion(esquema);
		// 4. Se imprime la Poblacion Inicial
		// System.out.println(p.toString());
	}//obtenerPoblacion

	 // Create the initial solutionSet
    /*Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem_);
      problem_.evaluate(newSolution);
      problem_.evaluateConstraints(newSolution);
      evaluations++;
      population.add(newSolution);
    } //for       */

	public int getCaso(){
		return this.caso;
	}
	
	public Poblacion getFront(Poblacion population){
		Ranking ranking = new Ranking(population);
		Distance distance = new Distance();
		
		int remain = population.size();
		int index = 0;
		Poblacion front = null;
		Poblacion poblacion = new Poblacion(population.size());
		
		front = ranking.getSubfront(index);
		
	      while ( front!= null && (remain > 0)  && (remain >= front.size())) {
	        //Assign crowding distance to individuals
	        distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
	        //Add the individuals of this front
	        for (int k = 0; k < front.size(); k++) {
	          poblacion.add(front.get(k));
	        } // for
	
	        //Decrement remain
	        remain = remain - front.size();
	
	        //Obtain the next front
	        index++;
	        if (remain > 0) {
	          front = ranking.getSubfront(index);
	        } // if        
	      } // while
	
	      if (front != null){
		      // Remain is less than front(index).size, insert only the best one
		      if (remain > 0) {  // front contains individuals to insert                        
		        distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
		        front.sort(new CrowdingComparator());
		        for (int k = 0; k < remain; k++) {
		          poblacion.add(front.get(k));
		        } // for
		      }
	        remain = 0;
	      } // if    
	
		
		return ranking.getSubfront(0);
	}
} // NSGA-II


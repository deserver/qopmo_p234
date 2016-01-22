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

package qopmo.nsgaII.prueba4.testG00;



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
import qopmo.wdm.qop.Solicitud;

/** 
 *  Implementation of NSGA-II.
 *  This implementation of NSGA-II makes use of a QualityIndicator object
 *  to obtained the convergence speed of the algorithm. This version is used
 *  in the paper:
 *     A.J. Nebro, J.J. Durillo, C.A. Coello Coello, F. Luna, E. Alba 
 *     "A Study of Convergence Speed in Multi-Objective Metaheuristics." 
 *     To be presented in: PPSN'08. Dortmund. September 2008.
 */

public class NSGAII_G00 extends Algorithm {
  /**
   * Constructor
   * @param problem Problem to solve
   */
  public NSGAII_G00(Problem problem, int x) {
    super (problem) ;
	this.num = x;
	String caso = casosDePrueba[this.num];

	this.casoPrincipal += caso;
  } // NSGAII
	  
  private static EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("tesis");
  private static EntityManager em = emf.createEntityManager();
  private String casoPrincipal = "caso_";
  
  public Poblacion population;
  public Red NSFNET;
  private EsquemaRestauracion esquema = EsquemaRestauracion.Segment;
  private CSVWriter csv = new CSVWriter();
  public String caso;
  private Integer num;
	private static int[] tiempoTotal = {
			7000, 7000, 7000, 7000, 7000, //35 
			8000, 8000, 8000, 8000,	8000, //40
			9000, 9000, 9000, 9000, 9000,  //45
			40000, 40000, 40000, 40000, 40000, //200
			80000, 80000, 80000, 80000, 80000, //400
			100000, 100000, 100000, 100000, 100000 //500
			}; //1220
		private static String[] casosDePrueba = {
				"1_1_10", "1_1_20", "1_1_30", "1_1_40", "1_1_50", 
				"1_2_10", "1_2_20", "1_2_30", "1_2_40",	"1_2_50", 
				"1_3_10", "1_3_20", "1_3_30", "1_3_40", "1_3_50",
				"2_1_10", "2_1_20", "2_1_30", "2_1_40", "2_1_50",
				"2_2_10", "2_2_20", "2_2_30", "2_2_40", "2_2_50",
				"2_3_10", "2_3_20", "2_3_30", "2_3_40", "2_3_50"
				};


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
    int nrocaso;
    int corridas;
    
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
    nrocaso        = ((Integer) getInputParameter("nrocaso")).intValue();
    corridas       = ((Integer) getInputParameter("corridas")).intValue();
    

    //Initialize the variables
    //population = new SolutionSet(populationSize);
    evaluations = 0;
    caso = casosDePrueba[nrocaso];
    
    //Initialize Nsfnet
	NSFNET = em.find(Red.class, 1); // NSFnet
	NSFNET.inicializar();
    
	long time_start, time_end = 0;
	// captura tiempo Inicial
	time_start = System.currentTimeMillis();
	// 0. Obtener Poblacion Inicial
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

    System.out.println(caso + "-" + corridas + " Test Genetico.");
    
    while (evaluations < tiempoTotal[nrocaso]) {

    	  size = population.getIndividuos().size();
    	  tamanho = size*size+size;
	      offspringPopulation = new Poblacion(tamanho);
	      copyPopulation = new Poblacion(tamanho);
	      //offspringPopulation.copiarPoblacion(population);;
	      
	      
	      //for (int i = 0; i < (populationSize); i++) {
	    	  if (evaluations<tiempoTotal[nrocaso]){
	    	  

			      for (Individuo ind : population.getIndividuos()){
			    	  Solution s = (Solution) ind;
			    	  s.setNumberOfObjectives(problem_.getNumberOfObjectives());
			    	  problem_.evaluate(s);
			    	  //if (s.getCosto()<2.8)
			    		  //System.out.println(s.getCosto());
			    	  if (s.getCosto() > 0)
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
                           
	      //} // for
	
	      for (Individuo ind : population.getIndividuos()){
	    	  Solution s = (Solution) ind;
	    	  s.setNumberOfObjectives(problem_.getNumberOfObjectives());
	    	  problem_.evaluate(s);
	    	  if (s.getCosto() > 0)
	        	  offspringPopulation.add(s);
	      }
	      // Create the solutionSet union of solutionSet and offSpring
	     // union = ((SolutionSet) population).union(offspringPopulation);
	      
 
	      union = offspringPopulation.unionsincontrol(population);
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
	      
	      if (evaluations % maxEvaluations == 0) {
	    	  	System.out.println();
				System.out.print("Población Nro: " + evaluations + " ");
				// System.out.println("MEJOR--> " + p.getMejor().toString());
				System.out.print("Costo-MEJOR==> ");
				ranking = new Ranking(population);
				if (ranking.getSubfront(0) != null){
					ranking.getSubfront(0).printParcialResults();
					ranking.getSubfront(0).printVariablesToFile("VAR_p3"+"_"+caso);
				}
				//((Solution) p.getMejor()).imprimirCosto();
	      }
    } // while
    cantIt++;
    

	// captura tiempo final
	//time_end = System.currentTimeMillis();
	// Calculo del Tiempo
	/*long tiempo = time_end - time_start;
	long hora = tiempo / 3600000;
	long restohora = tiempo % 3600000;
	long minuto = restohora / 60000;
	long restominuto = restohora % 60000;
	long segundo = restominuto / 1000;
	long restosegundo = restominuto % 1000;
	String time = hora + ":" + minuto + ":" + segundo + "." + restosegundo;
	time = " Tiempo: " + time;
	String fin = caso + " FIN - Test Genetico. Tiempo:" + time;
	fin += " - Nº Generaciones: " + evaluations;
	System.out.println(fin);
	*/
	System.out.println("Evaluaciones: "+ evaluations);
	
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
		Set<Solicitud> solicitudes = prueba1.getSolicitudes();
	
		for (int i = 0; i < cantidad; i++) {
			Solution solucion = new Solution(solicitudes);
			
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


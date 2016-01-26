//  NSGAII_main.java
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

package qopmo.nsgaII.prueba3.test2;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;


import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import qopmo.nsgaII.prueba3.QOP;
import qopmo.ag.Poblacion;

/** 
 * Class to configure and execute the NSGA-II algorithm.  
 *     
 * Besides the classic NSGA-II, a steady-state version (ssNSGAII) is also
 * included (See: J.J. Durillo, A.J. Nebro, F. Luna and E. Alba 
 *                  "On the Effect of the Steady-State Selection Scheme in 
 *                  Multi-Objective Genetic Algorithms"
 *                  5th International Conference, EMO 2009, pp: 183-197. 
 *                  April 2009)
 */ 
@RunWith(Parameterized.class)
public class NSGAII_main {
  public static Logger      logger_ ;      // Logger object
  public static FileHandler fileHandler_ ; // FileHandler object

	private static String[] casosDePrueba = {
			"10", "11", "12", "13", "14", "15", 
			"20", "21", "22", "23", "24", "25", 
			"30", "31", "32", "33", "34", "35", 
			"40", "41", "42", "43", "44", "45"
			};
	
  public static int nrocaso;

  /**
   * @param args Command line arguments.
   * @throws JMException 
   * @throws IOException 
   * @throws SecurityException 
   * Usage: three options
   *      - jmetal.metaheuristics.nsgaII.NSGAII_main
   *      - jmetal.metaheuristics.nsgaII.NSGAII_main problemName
   *      - jmetal.metaheuristics.nsgaII.NSGAII_main problemName paretoFrontFile
   */
  public static void main(String [] args) throws 
                                  JMException, 
                                  SecurityException, 
                                  IOException, 
                                  ClassNotFoundException {
	  
	    Problem   problem   ; // The problem to solve
	    Algorithm algorithm ; // The algorithm to use
	    Operator  crossover ; // Crossover operator
	    Operator  mutation  ; // Mutation operator
	    Operator  selection ; // Selection operator
	    
	    HashMap  parameters ; // Operator parameters
	    
	
	    // Logger object and file to store log messages
	    //logger_      = Configuration.logger_ ;
	    fileHandler_ = new FileHandler("NSGAII_main.log"); 
	    //logger_.addHandler(fileHandler_) ;
	        
	    problem = new QOP();
	    
	    int corridas;
	    String caso;
	    nrocaso = 18;
	    Poblacion population = new Poblacion(50);
	    ArrayList<Poblacion> gralPopulation = new ArrayList<Poblacion>();
	    while (nrocaso < 19){
	    	corridas = 1;
	    	int index = 0;
	    	long initTime2 = System.currentTimeMillis();
		    while(corridas < 30){
			    algorithm = new NSGAII(problem, nrocaso);
			    caso = casosDePrueba[nrocaso];
			    
			    //algorithm = new ssNSGAII(problem);
			
			    // Algorithm parameters
			    algorithm.setInputParameter("populationSize",50);
			    algorithm.setInputParameter("maxEvaluations",1000);
			    algorithm.setInputParameter("probMutacion", 1);//10%
			    algorithm.setInputParameter("nrocaso", nrocaso);
			    algorithm.setInputParameter("corridas", corridas);
			    
			    // Mutation and Crossover for Real codification 
			    /*parameters = new HashMap() ;
			    parameters.put("probability", 0.9) ;
			    parameters.put("distributionIndex", 20.0) ;
			    crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);                   
			
			    parameters = new HashMap() ;
			    parameters.put("probability", 1.0/problem.getNumberOfVariables()) ;
			    parameters.put("distributionIndex", 20.0) ;
			    mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);                    
			
			    // Selection Operator 
			    parameters = null ;
			    selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters) ;  
			    */
			    
			    // Add the operators to the algorithm
			    /*algorithm.addOperator("crossover",crossover);
			    algorithm.addOperator("mutation",mutation);
			    algorithm.addOperator("selection",selection);*/
			    //algorithm.addOperator("torneobinario", torneobinario);
			
			    // Add the indicator object to the algorithm
			    
			    //System.out.println(" "+corridas);
			    // Execute the Algorithm
			    long initTime = System.currentTimeMillis();
			    //System.out.println(caso + "-" + corridas + " Test Genetico.");
			    
			    population = algorithm.execute();
			    long estimatedTime = System.currentTimeMillis() - initTime;
			    //gralPopulation.copiarPoblacion(population);
			    gralPopulation.add(population);
			    //gralPopulation.get(index).printVariablesToFile("Conjunto_Pareto_PreFinal_01"+corridas);
				
				
			    // Result messages 
				if (population != null){
				    //logger_.info("Total execution time: "+estimatedTime + "ms");
				    //logger_.info("Variables values have been writen to file VAR");
				    population.printVariablesToFile("VAR_Final_p3_"+caso);    
				    //logger_.info("Objectives values have been writen to file FUN");
				    population.printObjectivesToFile("FUN_Final_p3_"+caso);
				}else{
					System.out.println("No arrojo resultados");
				}
			  	
			    /*if (indicators != null) {
			      logger_.info("Quality indicators") ;
			      logger_.info("Hypervolume: " + indicators.getHypervolume(population)) ;
			      logger_.info("GD         : " + indicators.getGD(population)) ;
			      logger_.info("IGD        : " + indicators.getIGD(population)) ;
			      logger_.info("Spread     : " + indicators.getSpread(population)) ;
			      logger_.info("Epsilon    : " + indicators.getEpsilon(population)) ;  
			     
			      int evaluations = ((Integer)algorithm.getOutputParameter("evaluations")).intValue();
			      logger_.info("Speed      : " + evaluations + " evaluations") ;      
			    } // if*/
			    corridas++;
			    index++;
		  }
		long estimatedTime2 = System.currentTimeMillis() - initTime2;
	    long tiempo = estimatedTime2;
		long hora = tiempo / 3600000;
		long restohora = tiempo % 3600000;
		long minuto = restohora / 60000;
		long restominuto = restohora % 60000;
		long segundo = restominuto / 1000;
		long restosegundo = restominuto % 1000;
		String time = hora + ":" + minuto + ":" + segundo + "." + restosegundo;
		time = " Tiempo: " + time;
		String fin = casosDePrueba[nrocaso] + " FIN - Test Genetico. Tiempo:" + time;
		//fin += " - Nº Generaciones: " + evaluations;
		System.out.println(fin);
		//if (population != null)
			//population.printFinalResults();
			
		
	    algorithm = new NSGAII(problem, 0);
	    Poblacion pfinal = getSoluciones(gralPopulation);
	    //pfinal.printVariablesToFile("Conjunto_Pareto_PreFinal_"+casosDePrueba[nrocaso]);
	    Poblacion finalPopulation = algorithm.getFront(pfinal);
	    double results[] = calcularPromedios(pfinal);
	    finalPopulation.printVariablesToFile("Conjunto_Pareto_Final_"+casosDePrueba[nrocaso]+".txt");
	    finalPopulation.printResults("Conjunto_Pareto_Final_"+casosDePrueba[nrocaso]+".txt", results);
	    
	    nrocaso++;
		
  		}
	    System.out.println("FIN Prueba Algoritmo Genetico. (Segment-Oriented).");
	  
  } //main
  
  public static int getCaso(int x){
		int num = x, caso;
		if (num <= 10) {
			caso = 40;
		} else if (num <= 20) {
			caso = 30;
		} else if (num <= 30) {
			caso = 20;
		} else if (num <= 40) {
			caso = 10;
		} else {
			caso = 1;
		}
		return caso;
  }
  
  public static Poblacion getSoluciones(ArrayList<Poblacion> population){
	  Poblacion pfinal = new Poblacion(50);
	  
	  List<Solution> finalList = new ArrayList<Solution>();

	  for (int i=0; i<population.size(); i++){
		  for (Solution s : population.get(i).getSolutionList()){
			  if (!finalList.contains(s) && esDistinto(finalList, s)){
			  //if (!finalList.contains(s) ){
				  finalList.add(s);
			  }
		  }
	  }
	  pfinal.setSolutionList(finalList);
	  return pfinal;
  }
  
  public static boolean esDistinto(List<Solution> finalList, Solution sol){
	  boolean flag = false;
	  for (Solution s : finalList){
		  for (int i=0; i<s.getNumberOfObjectives(); i++){
			  if (s.getObjective(i) == sol.getObjective(i)){
				  flag = true;
			  }
			  else{
				  flag = false;
				  break;
			  }
		  }
		  if (flag){
			  break;
		  }
	  }
	  
	  return !flag;
  }
  
  public static double[] calcularPromedios(Poblacion population){
	  int obj = population.getSolutionList().get(0).getNumberOfObjectives();
	  double results[] = new double[obj];
	  for (Solution s: population.getSolutionList()){
		  for (int i=0; i<obj; i++){
			  results[i] = results[i] + s.getObjective(i);
		  }
	  }
	  for (int i=0; i<obj; i++){
		  results[i] = results[i]/population.getSolutionList().size();
		  System.out.println("F.O. "+ i +": "+results[i]);
	  }
	  return results;
  }
  public int getNroCaso(){
	  return this.nrocaso;
  }
} // NSGAII_main

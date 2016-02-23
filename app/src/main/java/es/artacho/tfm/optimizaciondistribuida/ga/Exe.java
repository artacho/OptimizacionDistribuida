package es.artacho.tfm.optimizaciondistribuida.ga;///////////////////////////////////////////////////////////////////////////////
///            Steady State Genetic Algorithm v1.0                          ///
///                by Enrique Alba, July 2000                               ///
///                                                                         ///
///   Executable: set parameters, problem, and execution details here       ///
///////////////////////////////////////////////////////////////////////////////

public class Exe extends Thread

{
  /*public static void main(String args[]) throws Exception
  {*/
    
    /*// PARAMETERS PPEAKS 
    int    gn         = 512;                           // Gene number
    int    gl         = 1;                            // Gene length
    int    popsize    = 512;                          // Population size
    double pc         = 0.8;                          // Crossover probability
    double pm  = 1.0/(double)((double)gn*(double)gl); // Mutation probability
    double tf         = (double)1 ;              // Target fitness beign sought
    long   MAX_ISTEPS = 50000;
*/
      
	 // PARAMETERS SUBSET SUM 
	    /*int    gn         = 128;                           // Gene number
	    int    gl         = 1;                            // Gene length
	    int    popsize    = 512;                          // Population size
	    double pc         = 0.9;                          // Crossover probability
	    double pm  = 6/(double)((double)gn*(double)gl); // Mutation probability
	    double tf         = (double) 73721 ;              // Target fitness beign sought
	    long   MAX_ISTEPS = 256000;
	  
    // PARAMETERS ONEMAX
    /*int    gn         = 512;                          // Gene number
    int    gl         = 1;                            // Gene length
    int    popsize    = 512;                          // Population size
    double pc         = 0.9;                          // Crossover probability
    double pm  = 1.5/(double)((double)gn*(double)gl); // Mutation probability
    double tf         = (double)gn*gl ;           // Target fitness being sought
    long   MAX_ISTEPS = 128000;*/
    
	  
	 /* long ini = 0, fin = 0;
	  
	  for (int i = 0; i < 30; i++) {
	  
	 /* int    gn         = 3;                          // Gene number
	  int    gl         = 25;                            // Gene length
	  int    popsize    = 20;                          // Population size
	  double pc         = 0.8;                          // Crossover probability
	  double pm  = 1.0/(double)((double)gn*(double)gl); // Mutation probability
	  double tf         = (double)2999940.0003 ;           // Target fitness being sought*/
	  //long   MAX_ISTEPS = 5000;  
	  
   /* Problem   problem;                             // The problem being solved

    problem = new ProblemSubset();
    //problem = new ProblemOneMax();
    //problem = new ProblemFunction();
    
    problem.set_geneN(gn);
    problem.set_geneL(gl);
    problem.set_target_fitness(tf);



    Algorithm ga;          // The ssGA being used
    ga = new Algorithm(problem, popsize, gn, gl, pc, pm);

    ini = System.currentTimeMillis();
    for (int step=0; step<MAX_ISTEPS; step++)
    {  
      ga.go_one_step();
      //System.out.print(step); System.out.print("  ");
      //System.out.println(ga.get_bestf());

      if(     (problem.tf_known())                    &&
      (ga.get_solution()).get_fitness()==problem.get_target_fitness()) { 
    	//System.out.print("Solution Found! After ");
        //System.out.println(problem.get_fitness_counter());
        //System.out.println(" evaluations");
        break;
      }

    }
    
    //fin = System.currentTimeMillis();

    System.out.println(problem.get_fitness_counter());
    
    // Print the solution
    /*for(int i=0;i<128;i++)
      System.out.print( (ga.get_solution()).get_allele(i) ); System.out.println();
    System.out.println((ga.get_solution()).get_fitness());
    
    System.out.println(fin-ini);*/
  /*}
  }*/

  public void run () {
        // PARAMETERS SUBSET SUM
	    int    gn         = 128;                           // Gene number
	    int    gl         = 1;                            // Gene length
	    int    popsize    = 512;                          // Population size
	    double pc         = 0.9;                          // Crossover probability
	    double pm  = 6/(double)((double)gn*(double)gl); // Mutation probability
	    double tf         = (double) 73721 ;              // Target fitness beign sought
    long   MAX_ISTEPS = 256000;

   Problem   problem;                             // The problem being solved

    problem = new ProblemSubset();

    problem.set_geneN(gn);
    problem.set_geneL(gl);
    problem.set_target_fitness(tf);



    Algorithm ga = null;          // The ssGA being used

    try {
      ga = new Algorithm(problem, popsize, gn, gl, pc, pm);
    } catch (Exception e) {
      e.printStackTrace();
    }
    for (int step=0; step<MAX_ISTEPS; step++) {
      try {
        ga.go_one_step();
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        if ((problem.tf_known()) &&
                (ga.get_solution()).get_fitness() == problem.get_target_fitness()) {
          //System.out.print("Solution Found! After ");
          //System.out.println(problem.get_fitness_counter());
          //System.out.println(" evaluations");
          break;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
    System.out.println(problem.get_fitness_counter());
  }

}
// END OF CLASS: Exe

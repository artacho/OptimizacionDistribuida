package es.artacho.tfm.optimizaciondistribuida.ga;/*
 * ProblemSubset.java
 *
 * Created on 30 de octubre de 2001, 13:21
 */

/**
 *
 * @author  Antonio
 * @version 
 */
public class ProblemSubset extends Problem {

    /** Creates new ProblemSubset */
    //public ProblemSubset() {
    //    super() ;
    //}

    
  public double Evaluate(Individual Indiv)
  {
    return SUBSET_SUM(Indiv);

  }

  //    PRIVATE METHODS


  // SUBSET SUM PROBLEM	- NPcomplete, Jelasity and Dombi (Artificial Intelligence)
  // Parameter ranges: 0 and 1 (bit string)
  // Strlen = 128 bits
private static int w[]={2902, 5235, 357, 6058, 4846, 8280, 1295, 181, 3264, 
                        7285, 8806, 2344, 9203, 6806, 1511, 2172, 843, 4697, 
                        3348, 1866, 5800, 4094, 2751, 64, 7181, 9167, 5579, 
                        9461, 3393, 4602, 1796, 8174, 1691, 8854, 5902, 4864, 
                        5488, 1129, 1111, 7597, 5406, 2134, 7280, 6465, 4084, 
                        8564, 2593, 9954, 4731, 1347, 8984, 5057, 3429, 7635, 
                        1323, 1146, 5192, 6547, 343, 7584, 3765, 8660, 9318, 
                        5098, 5185, 9253, 4495, 892, 5080, 5297, 9275, 7515, 
                        9729, 6200, 2138, 5480, 860, 8295, 8327, 9629, 4212, 
                        3087, 5276, 9250, 1835, 9241, 1790, 1947, 8146, 8328, 
                        973, 1255, 9733, 4314, 6912, 8007, 8911, 6802, 5102, 
                        5451, 1026, 8029, 6628, 8121, 5509, 3603, 6094, 4447, 
                        683, 6996, 3304, 3130, 2314, 7788, 8689, 3253, 5920, 
                        3660, 2489, 8153, 2822, 6132, 7684, 3032, 9949, 59, 
                        6669, 6334};
  private static int C = 73721;
  private double SUBSET_SUM(Individual indiv)
  {

    int i;
    double fitness = 0.0;

    if(CL!=128)	
      System.out.println("Length mismatch error in Subset sum function.");


    for( i=0; i<CL; i++ )
      fitness += indiv.get_allele(i)*w[i];

    if (fitness>C)
    {
      fitness = C - fitness*0.1;
      if(fitness<0.0) fitness=0.0;
    }
    indiv.set_fitness(fitness);
    return fitness;
  }
}



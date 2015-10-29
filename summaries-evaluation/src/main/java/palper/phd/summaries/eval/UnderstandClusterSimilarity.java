/**
 * 
 */
package palper.phd.summaries.eval;

import smile.validation.RandIndex;

/**
 * @author pinarpink
 *
 */
public class UnderstandClusterSimilarity {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    System.out.println("measure");
    
    int[] users = {1,1,2,2,2,2,3,3,3,3,4,4,4,5,5,5,5,5,5,5};
    int[] conservative = {0,0,0,2,2,2,0,3,3,3,4,4,4,5,5,5,5,5,5,0};
  
    int[] aggressive = {2,2,2,2,2,3,3,3,3,4,4,4,5,5,5,5,5,5,5,5};
        
  
    
   RandIndex instance = new RandIndex();
   // double expResult = 0.9651;
    double result = instance.measure(users, conservative);
    System.out.println("Result conservative"+result);
    
    double result2 = instance.measure(users, aggressive);
    System.out.println("Result aggressive "+result2);
  }

}

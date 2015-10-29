/**
 * 
 */
package palper.phd.workflow.wfdesc;

import java.util.HashSet;
import java.util.Set;

/**
 * @author pinarpink
 *
 */
public class SummarizationStatistics {

  /*
   * True positive
   */
  Set<String> keptImportant = new HashSet<String>();
  
  /*
   * False negative
   */
  Set<String> abstractedImportant = new HashSet<String>();
  
  /*
   * False positive
   */
  Set<String> keptUnimportant = new HashSet<String>();
  
  /*
   * True negative
   */
  Set<String> abstractedUnimportant = new HashSet<String>();
  
  /**
   * 
   */
  public SummarizationStatistics(PreSummaryStatistics pre, PostSummaryStatistics post) {
    keptImportant.addAll(post.getOriginalNonStrProcessorOutputs());
    keptImportant.retainAll(pre.getGroundTruthImportant());
    
    
    abstractedUnimportant.addAll(pre.getNonStrProcessorOutputs());
    abstractedUnimportant.removeAll(post.getOriginalNonStrProcessorOutputs());
    abstractedUnimportant.retainAll(pre.getGroundTruthUnimportant());
    
    keptUnimportant.addAll(post.getOriginalNonStrProcessorOutputs());
    keptUnimportant.retainAll(pre.getGroundTruthUnimportant());
    
    abstractedImportant.addAll(pre.getNonStrProcessorOutputs());
    abstractedImportant.removeAll(post.getOriginalNonStrProcessorOutputs());
    abstractedImportant.retainAll(pre.getGroundTruthImportant());
  }

  public Set<String> getKeptImportant() {
    return keptImportant;
  }

  public Set<String> getAbstractedImportant() {
    return abstractedImportant;
  }

  public Set<String> getKeptUnimportant() {
    return keptUnimportant;
  }

  public Set<String> getAbstractedUnimportant() {
    return abstractedUnimportant;
  }



}

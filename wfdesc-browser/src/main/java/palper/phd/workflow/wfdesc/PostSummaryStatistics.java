/**
 * 
 */
package palper.phd.workflow.wfdesc;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author pinarpink
 *
 */
public class PostSummaryStatistics extends WfdescStatistics {

  
  private Set<String> originalNonStrProcessorOutputs = new HashSet<String>();

  /**
   * @param model
   */
  public PostSummaryStatistics(Model model) {
    super(model);


    for (String out : nonStrProcessorOutputs) {
      String originalOutPortUri = WfDescRdfUtils.getOriginalPortUri(model.getResource(out), model);
      if (originalOutPortUri != null) {
        originalNonStrProcessorOutputs.add(originalOutPortUri);
      } else {
        originalNonStrProcessorOutputs.add(out);
      }
    }
  }
  @Override
  public String toString() {
    String result =super.toString() + "\n Original outports:" +originalNonStrProcessorOutputs.toString();

    return result;
  }
  public Set<String> getOriginalNonStrProcessorOutputs() {
    return originalNonStrProcessorOutputs;
  }
}

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
public class PreSummaryStatistics extends WfdescStatistics {
  
///if there is any groundtruth that is put into wfdesc via annotations we
  //also collect this info into lists
  private Set<String> bookmarkedIntermediaryProcessorOutputPorts = new HashSet<String>();
  private Set<String> topLevelVisibleOutputPorts = new HashSet<String>();


  
  private Set<String> groundTruthImportant =new HashSet<String>();
  private Set<String> groundTruthUnimportant =new HashSet<String>();
  
 

  /**
   * @param model
   */
  public PreSummaryStatistics(Model model) {
    super(model);
    

    for (String out : nonStrProcessorOutputs) {
       if (WfDescRdfUtils.isTopLevelVisible(model.getResource(out), model)){
         topLevelVisibleOutputPorts.add(out);
       }
       

       if (WfDescRdfUtils.isBookmarked(model.getResource(out), model)){
         bookmarkedIntermediaryProcessorOutputPorts.add(out);
       }
       

    }
    
    groundTruthImportant.addAll(topLevelVisibleOutputPorts);
    groundTruthImportant.addAll(bookmarkedIntermediaryProcessorOutputPorts);
    
    
    
    groundTruthUnimportant.addAll(nonStrProcessorOutputs);
    groundTruthUnimportant.removeAll(groundTruthImportant);
  }



  public Set<String> getBookmarkedIntermediaryProcessorOutputPorts() {
    return bookmarkedIntermediaryProcessorOutputPorts;
  }


  public Set<String> getTopLevelVisibleOutputPorts() {
    return topLevelVisibleOutputPorts;
  }

  @Override
  public String toString() {
    String result =super.toString() + "\n GT important:" +groundTruthImportant.toString()+ "\n GT Unimportant: "+groundTruthUnimportant.toString();

    return result;
  }

  @Override
  public String toCsvString() {

    return super.toCsvString();
  }
  public Set<String> getGroundTruthImportant() {
    return groundTruthImportant;
  }



  public Set<String> getGroundTruthUnimportant() {
    return groundTruthUnimportant;
  }


}

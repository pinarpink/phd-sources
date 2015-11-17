/**
 * 
 */
package palper.phd.workflow.wfdesc;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author pinarpink
 * 
 */
public abstract class WfdescStatistics implements Serializable {

  Set<String> procList = new HashSet<String>();
  Set<String> linkList = new HashSet<String>();

  Set<String> strConsProcList = new HashSet<String>();
  Set<String> wfInList = new HashSet<String>();
  Set<String> wfOutList = new HashSet<String>();

  Set<String> nonStrProcessorOutputs = new HashSet<String>();



  public WfdescStatistics(Model model) {


    super();
    Resource wfResource = WfDescRdfUtils.getWorkflowResource(model);
    Set<Resource> procs = WfDescRdfUtils.getProcessors(model, wfResource);

    for (Resource proc : procs) {

      if (WfDescRdfUtils.isStringConstant(model, proc)) {
        strConsProcList.add(proc.getURI());
      } else {
        procList.add(proc.getURI());
      }
    }


    Set<Resource> wfInputs = WfDescRdfUtils.getInputPorts(wfResource, model);
    Set<Resource> wfOutputs = WfDescRdfUtils.getOutputPorts(wfResource, model);

    for (Resource inp : wfInputs) {
      wfInList.add(inp.getURI());

    }
    for (Resource out : wfOutputs) {

      wfOutList.add(out.getURI());
    }

    Set<Resource> links = WfDescRdfUtils.getDataLinks(model, wfResource);

    for (Resource link : links) {
      linkList.add(link.getURI());
    }
    for (String noStrProcId : procList) {
      Set<Resource> outputs = WfDescRdfUtils.getOutputPorts(model.getResource(noStrProcId), model);

      for (Resource outport : outputs) {
        nonStrProcessorOutputs.add(outport.getURI());

      }

    }

  }

  @Override
  public String toString() {
    String result =
        "numOfProcs: " + procList.size() + "\n" + "numOfLinks: " + linkList.size() + "\n"
            + "numOfConstantInputs: " + strConsProcList.size() + "\n" + "numOfWfInputs: "
            + wfInList.size() + "\n" + "numOfWfOutputs: " + wfOutList.size() + "\n"
            + "num Of Internal Pocessor Outputs: " + nonStrProcessorOutputs.size() + "\n";

    return result;
  }


  public String toCsvString() {
    String result =
         procList.size() + ";" +  linkList.size() + ";";

    return result;
  }


  public Set<String> getProcList() {
    return procList;
  }

  public Set<String> getLinkList() {
    return linkList;
  }

  public Set<String> getStrConsProcList() {
    return strConsProcList;
  }

  public Set<String> getWfInList() {
    return wfInList;
  }

  public Set<String> getWfOutList() {
    return wfOutList;
  }

  public Set<String> getNonStrProcessorOutputs() {
    return nonStrProcessorOutputs;
  }


}

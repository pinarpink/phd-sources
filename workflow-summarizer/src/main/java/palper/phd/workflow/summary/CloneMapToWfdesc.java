/**
 * 
 */
package palper.phd.workflow.summary;

import java.util.Map;
import java.util.Set;

import palper.phd.workflow.wfdesc.SummarizerNamespaces;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author pinarpink
 * 
 */
public class CloneMapToWfdesc {

  public static InfModel decoratePortsWithMappings(InfModel model,
      Map<String, String> clonetoOriginalPorts) {

    Set<Resource> processors =
        WfDescRdfUtils.getProcessors(model, WfDescRdfUtils.getWorkflowResource(model));

    for (Resource res : processors) {
      Set<Resource> inpPorts = WfDescRdfUtils.getInputPorts(res, model);

      for (Resource inp : inpPorts) {
        String cloneUri = inp.getURI();
        String origiNalUri = traceToOriginal(cloneUri, clonetoOriginalPorts);
        if (origiNalUri == null) {
          continue;
        } else {
          decorateModel(origiNalUri, cloneUri, model);
        }
      }
      Set<Resource> outPorts = WfDescRdfUtils.getOutputPorts(res, model);

      for (Resource out : outPorts) {
        String cloneUri = out.getURI();
        String origiNalUri = traceToOriginal(cloneUri, clonetoOriginalPorts);
        if (origiNalUri == null) {
          continue;
        } else {
          decorateModel(origiNalUri, cloneUri, model);
        }
      }


//      for (String key : clonetoOriginalPorts.keySet()) {
//
//        System.out.println(key + " = >" + clonetoOriginalPorts.get(key));
//      }
    }

    return model;
  }

  private static String traceToOriginal(String clonePortUrl,
      Map<String, String> clonetoOriginalPorts) {

    if (clonetoOriginalPorts.get(clonePortUrl) == null) {
      return null;
    }

    String iterKey = clonetoOriginalPorts.get(clonePortUrl);
    String result = iterKey;

    while (iterKey != null) {
      result = iterKey;
      iterKey = clonetoOriginalPorts.get(iterKey);

    }

    return result;
  }

  private static void decorateModel(String originalPortUri, String clonePortUri, InfModel model) {

    model.add(model.getResource(clonePortUri),
        model.createProperty(SummarizerNamespaces.motifsNS + "cloneOf"), originalPortUri);

  }
}

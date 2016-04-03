/**
 * 
 */
package palper.phd.workflow.wf2labelerwf;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.jackson.map.ObjectMapper;

import palper.phd.labeling.model.LabelingOperatorEnum;
import palper.phd.labeling.model.LabelingSpecBean;
import palper.phd.workflow.wfdesc.WfDescCrudUtils;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author pinarpink
 * 
 */
public class PipelineWfdescCreator {

  private int counter = 1;

  private Map<LabelingSpecBean, QName> specToQName = new HashMap<LabelingSpecBean, QName>();

  private Map<String, LabelingSpecBean> processorSpecBag = null;
  private Map<String, List<LabelingSpecBean>> adjustmentSpecBag = null;

  private Model sciWfWfdesc = null;

  public PipelineWfdescCreator(Model sciWfWfdesc, Map<String, LabelingSpecBean> processorSpecBag,
      Map<String, List<LabelingSpecBean>> adjustmentSpecBag) {
    super();

    this.sciWfWfdesc = sciWfWfdesc;
    this.processorSpecBag = processorSpecBag;
    this.adjustmentSpecBag = adjustmentSpecBag;

  }

  public String run() throws Exception {

    Resource wfResource = WfDescRdfUtils.getWorkflowResource(sciWfWfdesc);

    WfDescCrudUtils util = new WfDescCrudUtils(wfResource.getNameSpace() + "labelingPipeline/");

    for (Map.Entry<String, LabelingSpecBean> entry : processorSpecBag.entrySet()) {

      QName processorId = generateLabelingProcessor(entry.getValue(), util);

      List<String> correspondingSciWfOp = new ArrayList<String>();
      correspondingSciWfOp.add(entry.getKey());

      specToQName.put(entry.getValue(), processorId);

    }

    for (Map.Entry<String, List<LabelingSpecBean>> entry : adjustmentSpecBag.entrySet()) {
      for (LabelingSpecBean spec : entry.getValue()) {


        QName processorId = generateLabelingProcessor(spec, util);

        List<String> correspondingSciWfLink = new ArrayList<String>();
        correspondingSciWfLink.add(entry.getKey());

        specToQName.put(spec, processorId);

      }

    }


    for (Map.Entry<LabelingSpecBean, QName> entry : specToQName.entrySet()) {
      LabelingSpecBean spec = entry.getKey();
      if (spec.getOperator().equals(LabelingOperatorEnum.PROPAGATE)) {
        for (String src : spec.getSourcePortUriStringList()) {
          LabelingSpecBean predecessorSpec = getSpecThatCatersForThisPort(src, sciWfWfdesc);

          if (predecessorSpec != null) {
            linkLabelingProcessors(specToQName.get(predecessorSpec), specToQName.get(spec), util);
          }

        }
      } else if (spec.getOperator().equals(LabelingOperatorEnum.DISTRIBUTE)
          || spec.getOperator().equals(LabelingOperatorEnum.GENERALIZE)) {
        String sourcePort = spec.getSourcePortUriStringList().get(0);
        Resource preceedingOp =
            WfDescRdfUtils.getOperationWithOutput(sciWfWfdesc, sciWfWfdesc.getResource(sourcePort));
        LabelingSpecBean preceedingOpSpec = processorSpecBag.get(preceedingOp.getURI());
        if (preceedingOpSpec != null) {
          linkLabelingProcessors(specToQName.get(preceedingOpSpec), specToQName.get(spec), util);
        } else {
          // this is an anomaly should not happen
          throw new RuntimeException();
        }
      }
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    util.save(baos);
    return baos.toString();
  }

  private LabelingSpecBean getSpecThatCatersForThisPort(String srcPortOfAPropagateSpec,
      Model sciWfWfdesc) {

    LabelingSpecBean result = null;

    Resource inlink =
        WfDescRdfUtils
            .getDataLinksWithSinkPort(sciWfWfdesc, sciWfWfdesc.getResource(srcPortOfAPropagateSpec))
            .iterator().next();
    Resource inlinkSourcePort = WfDescRdfUtils.getSource(sciWfWfdesc, inlink);

    int endDepth =
        WfDescRdfUtils.getDepth(sciWfWfdesc.getResource(srcPortOfAPropagateSpec), sciWfWfdesc);
    int startDepth = WfDescRdfUtils.getDepth(inlinkSourcePort, sciWfWfdesc);

    if (endDepth == startDepth) {

      Resource preceedingOp = WfDescRdfUtils.getOperationWithOutput(sciWfWfdesc, inlinkSourcePort);
      result = processorSpecBag.get(preceedingOp.getURI());
    } else {
      List<LabelingSpecBean> adjustersOfInlinkSourcePort =
          adjustmentSpecBag.get(inlinkSourcePort.getURI());

      if (adjustersOfInlinkSourcePort != null) {
        for (LabelingSpecBean adjuster : adjustersOfInlinkSourcePort) {
          if (adjuster.getDataLinkDepthDifference() == (startDepth - endDepth)) {
            result = adjuster;
          }
        }
      }
    }


    return result;

  }

  private QName generateLabelingProcessor(LabelingSpecBean spec, WfDescCrudUtils util)
      throws Exception {
    // return the processor id
    QName result = null;

    StringWriter labelSpecAsJson = new StringWriter();
    ObjectMapper mapper = new ObjectMapper();

    mapper.writeValue(labelSpecAsJson, spec);



    if (spec.getOperator().equals(LabelingOperatorEnum.MINT)) {

      result = util.addBeanshellSubProcess("MINT_" + nextCounter(), labelSpecAsJson.toString());
      System.out.println(result.getLocalPart() + " Mint for: " + spec.getProcessorUriString());

    } else if (spec.getOperator().equals(LabelingOperatorEnum.PROPAGATE)) {

      result =
          util.addBeanshellSubProcess("PROPAGATE_" + nextCounter(), labelSpecAsJson.toString());
      System.out.println(result.getLocalPart() + " Propagate for: " + spec.getProcessorUriString());

    } else if (spec.getOperator().equals(LabelingOperatorEnum.DISTRIBUTE)) {
      result =
          util.addBeanshellSubProcess("DISTRIBUTE_" + nextCounter(), labelSpecAsJson.toString());
      System.out.println(result.getLocalPart() + " Distribute for: " + spec.getProcessorUriString()
          + " " + spec.getSourcePortUriStringList() + " " + spec.getDataLinkDepthDifference());
    } else if (spec.getOperator().equals(LabelingOperatorEnum.GENERALIZE)) {
      result =
          util.addBeanshellSubProcess("GENERALIZE_" + nextCounter(), labelSpecAsJson.toString());
      System.out.println(result.getLocalPart() + " Generalize for: " + spec.getProcessorUriString()
          + " " + spec.getSourcePortUriStringList() + " " + spec.getDataLinkDepthDifference());
    }

    // HOW and WHERE do we fill in the actualizationn or GROUNDING forthe
    // high level operators!!!!
    //This is also specific to the particular application scenario where labelFlow operators will be used.


    return result;
  }

  private void linkLabelingProcessors(QName operation1, QName operation2, WfDescCrudUtils util) {


    util.addSimulatedControlFlowLink(operation1, operation2);

  }

  private int nextCounter() {
    return counter++;
  }
}

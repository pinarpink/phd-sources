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
import palper.phd.workflow.wfdesc.URIUtils;
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

	private Map<LabelingSpecBean, List<String>> specToResource = new HashMap<LabelingSpecBean, List<String>>();
	private Map<LabelingSpecBean, QName> specToQName = new HashMap<LabelingSpecBean, QName>();


	public PipelineWfdescCreator() {
		super();

	}

	public String run(Model sciWfWfdesc,
			Map<String, LabelingSpecBean> processorToSpec,
			Map<String, List<LabelingSpecBean>> linkToSpec) throws Exception {

		Resource wfResource = WfDescRdfUtils.getWorkflowResource(sciWfWfdesc);

		WfDescCrudUtils util = new WfDescCrudUtils(wfResource.getNameSpace()+ "labelingPipeline/"/*,
				 URIUtils.lastBit(wfResource.getURI()) + "labelingPipeline"*/);

		for (Map.Entry<String, LabelingSpecBean> entry : processorToSpec
				.entrySet()) {

			QName processorId = generateLabelingProcessor(entry.getValue(),
					util);

			List<String> correspondingSciWfOp = new ArrayList<String>();
			correspondingSciWfOp.add(entry.getKey());
			specToResource.put(entry.getValue(), correspondingSciWfOp);
			specToQName.put(entry.getValue(), processorId);

		}

		for (Map.Entry<String, List<LabelingSpecBean>> entry : linkToSpec
				.entrySet()) {
			for (LabelingSpecBean spec : entry.getValue()) {

				if (specToResource.get(spec) == null) {
					QName processorId = generateLabelingProcessor(spec, util);

					List<String> correspondingSciWfLink = new ArrayList<String>();
					correspondingSciWfLink.add(entry.getKey());
					specToResource.put(spec, correspondingSciWfLink);

					specToQName.put(spec, processorId);
				} else {

					List<String> linkIds = specToResource.get(spec);
					linkIds.add(entry.getKey());
				}
			}

		}

		for (Map.Entry<LabelingSpecBean, List<String>> entry : specToResource
				.entrySet()) {

			LabelingSpecBean spec = entry.getKey();
			if (spec.getOperator().equals(LabelingOperatorEnum.PROPAGATE)) {
				for (String src : spec.getSourcePortUriStringList()) {
					LabelingSpecBean adjustSpec = getTargettingAdjustment(src);
					if (adjustSpec != null) {
						linkLabelingProcessors(specToQName.get(adjustSpec),
								specToQName.get(spec), util);
					} else {

						Resource inlink = WfDescRdfUtils
								.getDataLinksWithSinkPort(sciWfWfdesc,
										sciWfWfdesc.getResource(src))
								.iterator().next();
						Resource inlinkSourcePort = WfDescRdfUtils.getSource(
								sciWfWfdesc, inlink);
						if (!WfDescRdfUtils.isWorkflowInputPort(
								inlinkSourcePort, sciWfWfdesc)) {
							Resource preceedingOp = WfDescRdfUtils
									.getOperationWithOutput(sciWfWfdesc,
											inlinkSourcePort);
							if (processorToSpec.get(preceedingOp.getURI()) != null) {
								LabelingSpecBean preceedingOpSpec = processorToSpec
										.get(preceedingOp.getURI());
								boolean found = false;
								for (String target : preceedingOpSpec
										.getSinkPortUriStringList()) {
									if (target
											.equals(inlinkSourcePort.getURI())) {
										found = true;
									}
								}
								if (found) {
									linkLabelingProcessors(
											specToQName.get(preceedingOpSpec),
											specToQName.get(spec), util);
								}
							}
						}
					}
				}
			} else if (spec.getOperator().equals(
					LabelingOperatorEnum.DISTRIBUTE)
					|| spec.getOperator().equals(
							LabelingOperatorEnum.GENERALIZE)) {
				String sourcePort = spec.getSourcePortUriStringList().get(0);
				Resource preceedingOp = WfDescRdfUtils.getOperationWithOutput(
						sciWfWfdesc, sciWfWfdesc.getResource(sourcePort));
				LabelingSpecBean preceedingOpSpec = processorToSpec
						.get(preceedingOp.getURI());
				if (preceedingOpSpec != null) {
					linkLabelingProcessors(specToQName.get(preceedingOpSpec),
							specToQName.get(spec), util);
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

	private LabelingSpecBean getTargettingAdjustment(String src) {
		LabelingSpecBean result = null;
		for (Map.Entry<LabelingSpecBean, List<String>> entry : specToResource
				.entrySet()) {
			List<String> targetPorts = entry.getValue();
			for (String port : targetPorts) {
				if (port.equals(src)) {
					result = entry.getKey();
				}
			}

		}
		return result;

	}

	private QName generateLabelingProcessor(LabelingSpecBean spec,
			WfDescCrudUtils util) throws Exception {
		// return the processor id
		QName result = null;

	      StringWriter labelSpecAsJson = new StringWriter();
	        ObjectMapper mapper = new ObjectMapper();

	        mapper.writeValue(labelSpecAsJson, spec);

	
		
		if (spec.getOperator().equals(LabelingOperatorEnum.MINT)) {

			result = util.addBeanshellSubProcess("MINT_" + nextCounter(),
			    labelSpecAsJson.toString());
			System.out.println(result.getLocalPart() + " Mint for: "
					+ spec.getProcessorUriString()/*.getWfElementUriStringList().get(0)*/);

		} else if (spec.getOperator().equals(
				LabelingOperatorEnum.PROPAGATE)) {

			result = util.addBeanshellSubProcess("PROPAGATE_" + nextCounter(),
			    labelSpecAsJson.toString());
			System.out.println(result.getLocalPart() + " Propagate for: "
					+ spec.getProcessorUriString()/*.getWfElementUriStringList().get(0)*/);

		} else if (spec.getOperator().equals(
				LabelingOperatorEnum.DISTRIBUTE)) {
			result = util.addBeanshellSubProcess("DISTRIBUTE_" + nextCounter(),
			    labelSpecAsJson.toString());
			System.out.println(result.getLocalPart() + " Distribute for: "
					+ spec.getProcessorUriString() + " "+spec.getSourcePortUriStringList()+ " " +spec.getDataLinkDepthDifference()/*.getWfElementUriStringList()*/);
		} else if (spec.getOperator().equals(
				LabelingOperatorEnum.GENERALIZE)) {
			result = util.addBeanshellSubProcess("GENERALIZE_" + nextCounter(),
			    labelSpecAsJson.toString());
			System.out.println(result.getLocalPart() + " Generalize for: "
					+ spec.getProcessorUriString()+ " "+spec.getSourcePortUriStringList()+ " " +spec.getDataLinkDepthDifference()/*.getWfElementUriStringList()*/);
		}

		// HOW and WHERE dowe fill in the actualizationn or GROUNDING forthe
		// high level operators!!!!
//
//		QName in1 = util.addInputPortToProcess(result, "labelingSpec"
//				+ nextCounter());


//		StringWriter configString = new StringWriter();
//		ObjectMapper mapper = new ObjectMapper();
//
//		mapper.writeValue(configString, workflowEntity);
//
//		QName res = util.addStringConstantSubProcess("SC_" + nextCounter(),
//				configString.toString());
//		QName out1 = util.addOutputPortToProcess(res, "value" + nextCounter());
//
//
//		util.addDataLink(out1, in1);


		return result;
	}

	private void linkLabelingProcessors(QName operation1, QName operation2,
			WfDescCrudUtils util) {


		util.addSimulatedControlFlowLink(operation1, operation2);

	}

	private int nextCounter() {
		return counter++;
	}
}

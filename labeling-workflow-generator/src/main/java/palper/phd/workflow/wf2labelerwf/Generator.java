package palper.phd.workflow.wf2labelerwf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.codehaus.jackson.map.ObjectMapper;

import palper.phd.labeling.model.LabelingConstants;
import palper.phd.labeling.model.LabelingOperatorEnum;
import palper.phd.labeling.model.LabelingSpecBean;
import palper.phd.provenance.model.DatalinkBean;
import palper.phd.provenance.model.OperationBean;
import palper.phd.provenance.model.PortBean;
import palper.phd.provenance.model.PortTypeEnum;
import palper.phd.provenance.model.WfElementThreadIterator;
import palper.phd.provenance.model.WorkflowElementBean;
import palper.phd.provenance.query.DesignLineageTraversal;
import palper.phd.workflow.annotation.AstroWfAnnotationsStub;
import palper.phd.workflow.wfdesc.WfDescCrudUtils;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;
import uk.org.taverna.scufl2.api.io.WriterException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.ReificationStyle;

/**
 * 
 */

/**
 * @author pinarpink
 * 
 */
public class Generator {

	private Map<String, QName> reflectedWorkflowElements = new HashMap<String, QName>();

	private Model wfdescModel;
	private int counter = 1;

	public Generator() {
		super();

	}

	public void testToBuildLabelingWf() {

		try {

			wfdescModel = ModelFactory
					.createDefaultModel(ReificationStyle.Minimal);

			InputStream is;

			is = new FileInputStream(
					"/Users/pinarpink/Desktop/SummarizationRules/dataset/2920-motifs.wfdesc.ttl");

			wfdescModel.read(is, null, "TURTLE");

			List<List<WorkflowElementBean>> results = DesignLineageTraversal
					.downstreamWorkflowPath(
							wfdescModel,
							"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/in/list_cig_name");

			String baseNS = WfDescRdfUtils.getWorkflowResource(wfdescModel)
					.getURI() + "labeler" + "/";
			WfDescCrudUtils util = new WfDescCrudUtils(baseNS, "Workflow1");

			for (List<WorkflowElementBean> thread : results) {

				Collections.reverse(thread);
				WfElementThreadIterator iter = new WfElementThreadIterator(
						thread);

				WorkflowElementBean lookahead = null;
				WorkflowElementBean current = null;
				boolean pathBroken = false;

				QName anchorOpInLabelingPipeline = null;

				while (iter.hasNext()) {
					current = lookahead;
					lookahead = iter.next();

					if (!reflectedInLabelingWorkflow(lookahead.getUriString())) {
						if (lookahead instanceof PortBean) {
							PortBean pb = (PortBean) lookahead;

							if (pb.getType().equals(PortTypeEnum.INPUT)) {

								// initial input ports of the workflow
								// no path broken as yet
								// keep traversing the path

							} else if (pb.getType().equals(PortTypeEnum.OUTPUT)) {
								if (!pathBroken) {

									// WORKFLOW OUTPUT'larin
									// bir defined depth'i yok onlar input'a
									// gore
									// sekil aliyor.
									// OUTPUT'un her seviyesi icin label
									// hesaplamaya
									// gerek var mi bakilacak.

									LabelingSpecBean linkLabelingSpec = createLabelingSpecForLink(
											(DatalinkBean) current, 1);
									if ((linkLabelingSpec
											.getDataLinkDepthDifference() != 0)) {

										QName proc = generateLabelingProcessor(
												linkLabelingSpec, util);
										reflectedWorkflowElements.put(
												lookahead.getUriString(), proc);

										if (anchorOpInLabelingPipeline != null) {
											linkLabelingProcessors(
													anchorOpInLabelingPipeline,
													proc, util);
										}

										anchorOpInLabelingPipeline = proc;
									}
								}
							} else {
								// this should not be a case??
								// test it with a workflow that contains a
								// sub-workflow!! CASES OF BING BOTH AN INPUT
								// AND OUTPUT

							}
						} else if (lookahead instanceof OperationBean) {

							// handle the operation
							OperationBean op = (OperationBean) lookahead;
							LabelingSpecBean operationLabelingSpec = getLabelingSpec(op
									.getUriString());

							if ((operationLabelingSpec.getOperator().equals(
									LabelingOperatorEnum.PROPAGATE) && !pathBroken)) {

								if (hasReceivingEnd(current.getUriString(),
										operationLabelingSpec)) {

									// handle The incoming link
									LabelingSpecBean linkLabelingSpec = createLabelingSpecForLink(
											(DatalinkBean) current, 0);
									if ((linkLabelingSpec
											.getDataLinkDepthDifference() != 0)) {

										QName proc = generateLabelingProcessor(
												linkLabelingSpec, util);
										reflectedWorkflowElements.put(
												current.getUriString(), proc);

										if (anchorOpInLabelingPipeline != null) {
											linkLabelingProcessors(
													anchorOpInLabelingPipeline,
													proc, util);
										}

										anchorOpInLabelingPipeline = proc;
									}

									QName proc = generateLabelingProcessor(
											operationLabelingSpec, util);
									reflectedWorkflowElements.put(
											lookahead.getUriString(), proc);

									if (anchorOpInLabelingPipeline != null) {
										linkLabelingProcessors(
												anchorOpInLabelingPipeline,
												proc, util);
									}

									anchorOpInLabelingPipeline = proc;

								} else {
									pathBroken = true;

								}
							} else if (operationLabelingSpec.getOperator()
									.equals(LabelingOperatorEnum.MINT)) {

								QName proc = generateLabelingProcessor(
										operationLabelingSpec, util);

								reflectedWorkflowElements.put(
										lookahead.getUriString(), proc);

								anchorOpInLabelingPipeline = proc;

								pathBroken = false;

							} else {
								pathBroken = true;
								// if op has NO annotation than pathBroken=true

							}

						} else if (lookahead instanceof DatalinkBean) {
							DatalinkBean dlb = (DatalinkBean) lookahead;

							if (!pathBroken) {

								if (current instanceof OperationBean) {
									LabelingSpecBean spec = getLabelingSpec(((OperationBean) current)
											.getUriString());
									if (hasSendingEnd(dlb.getUriString(), spec)) {
										pathBroken = false;
									} else {
										pathBroken = true;
									}
								}
							}
						} else {

							// this should not really be a case.. in a path
							// thread the above are al possible beans we can
							// encounter

						}// else

					} else {
						if (!pathBroken) {

							anchorOpInLabelingPipeline = reflectedWorkflowElements
									.get(lookahead.getUriString());
						}
					}
				}// while
			}// for
			try {
				util.save(baseNS, System.out);
			} catch (WriterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private LabelingSpecBean createLabelingSpecForLink(DatalinkBean link,
			int TODO) throws Exception {

		LabelingSpecBean result = new LabelingSpecBean();

		result.setWfElementUriString(link.getUriString());
		Resource source = WfDescRdfUtils.getSource(wfdescModel,
				wfdescModel.getResource(link.getUriString()));

		int sourceDepth = WfDescRdfUtils.getDepth(source, wfdescModel);

		Resource sink = WfDescRdfUtils.getSink(wfdescModel,
				wfdescModel.getResource(link.getUriString()));

		int sinkDepth = WfDescRdfUtils.getDepth(sink, wfdescModel);

		if ((TODO == 0) && ((sourceDepth == -1) || (sinkDepth == -1))) {
			throw new RuntimeException("Missing depth information"
					+ link.getUriString());
		}

		if (TODO == 1) {
			sinkDepth = sourceDepth + 1;
		}
		result.setDataLinkDepthDifference(sourceDepth - sinkDepth);

		// MAKE USE OF THE DEPTH PREDICTION ALGORITHM for this
		if (TODO == 0) {
			if (result.getDataLinkDepthDifference() > 0) {
				result.setOperator(LabelingOperatorEnum.DISTRIBUTE);
				result.setLabelingFunctionIdentifier(LabelingConstants.INVARIANT);
			} else {
				result.setOperator(LabelingOperatorEnum.GENERALIZE);
				result.setLabelingFunctionIdentifier(LabelingConstants.CUMULATIVE);
			}
		} else {

			result.setOperator(LabelingOperatorEnum.GENERALIZE);
			result.setLabelingFunctionIdentifier(LabelingConstants.CUMULATIVE);
		}

		List<String> sourcePortList = new ArrayList<String>();
		sourcePortList.add(source.getURI());

		List<String> sinkPortList = new ArrayList<String>();
		sinkPortList.add(sink.getURI());

		result.setSourcePortUriStringList(sourcePortList);
		result.setSinkPortUriStringList(sinkPortList);

		return result;
	}

	private LabelingSpecBean getLabelingSpec(String uriString) {
		return AstroWfAnnotationsStub.getLabelingSpec(uriString);
	}

	private QName generateLabelingProcessor(LabelingSpecBean workflowEntity,
			WfDescCrudUtils util) throws Exception {
		// return the processor id
		QName result = null;

		if (workflowEntity.getOperator().equals(LabelingOperatorEnum.MINT)) {

			result = util.addBeanshellSubProcess("MINT_" + nextCounter(),
					"BeanshellString...for minting label");
			System.out.println(result.getLocalPart() + " Mint for: "
					+ workflowEntity.getWfElementUriString());

		} else if (workflowEntity.getOperator().equals(
				LabelingOperatorEnum.PROPAGATE)) {

			result = util.addBeanshellSubProcess("PROPAGATE_" + nextCounter(),
					"BeanshellScript...for propagating label");
			System.out.println(result.getLocalPart() + " Propagate for: "
					+ workflowEntity.getWfElementUriString());

		} else if (workflowEntity.getOperator().equals(
				LabelingOperatorEnum.DISTRIBUTE)) {
			result = util.addBeanshellSubProcess("DISTRIBUTE_" + nextCounter(),
					"BeanshellScript...for distributing label");
			System.out.println(result.getLocalPart() + " Distribute for: "
					+ workflowEntity.getWfElementUriString());
		} else if (workflowEntity.getOperator().equals(
				LabelingOperatorEnum.GENERALIZE)) {
			result = util.addBeanshellSubProcess("GENERALIZE_" + nextCounter(),
					"BeanshellScript....for generalizing");
			System.out.println(result.getLocalPart() + " Generalize for: "
					+ workflowEntity.getWfElementUriString());
		}

		// HOW and WHERE dowe fill in the actualizationn or GROUNDING forthe
		// high level operators!!!!

		QName in1 = util.addInputPortToProcess(result, "labelingSpec"
				+ nextCounter());
		// QName in2 = util.addInputPortToProcess(result, "IN_" +
		// nextCounter());

		StringWriter configString = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		// mapper.configure(SerializationConfig.Feature.USE_ANNOTATIONS, true);
		mapper.writeValue(configString, workflowEntity);

		QName res = util.addStringConstantSubProcess("SC_" + nextCounter(),
				configString.toString());
		QName out1 = util.addOutputPortToProcess(res, "value" + nextCounter());

		// QName res2 = util.addSubProcess("SC_" + nextCounter()/*
		// * operation.
		// * getSinkPortUriString
		// * ()
		// */,
		// WfdescSerialiser.STR_CONST);
		// QName out2 = util.addOutputPortToProcess(res2, "OUT_" +
		// nextCounter());

		util.addDataLink(out1, in1);
		// util.addDataLink(out2, in2);

		return result;
	}

	private void linkLabelingProcessors(QName operation1, QName operation2,
			WfDescCrudUtils util) {

		// QName out1 = util.addOutputPortToProcess(operation1, "OUT_CTRL_"
		// + nextCounter());
		// QName in1 = util.addInputPortToProcess(operation2, "IN_CTRL_"
		// + nextCounter());

		util.addSimulatedControlFlowLink(operation1, operation2);

	}

	private boolean hasReceivingEnd(String dataLinkUri,
			LabelingSpecBean labelingSpec) {

		// in fact this method should accept a list of labeling specs and we
		// should iterate over all
		// to see if there are any matching source ports..
		Resource portResource = WfDescRdfUtils.getSink(wfdescModel,
				wfdescModel.getResource(dataLinkUri));
		String portURIString = portResource.getURI();
		// String portURIString = (portResource.getURI().endsWith("/") ?
		// portResource
		// .getURI() : portResource.getURI() + "/");
		if (portURIString.equals(labelingSpec.getSourcePortUriStringList().get(
				0)))
			return true;
		else
			return false;

	}

	private boolean hasSendingEnd(String dataLinkUri,
			LabelingSpecBean labelingSpec) {

		// in fact this method should accept a list of labeling specs and we
		// should iterate over all
		// to see if there are any matching sink ports..
		Resource portResource = WfDescRdfUtils.getSource(wfdescModel,
				wfdescModel.getResource(dataLinkUri));
		String portURIString = portResource.getURI();
		// String portURIString = (portResource.getURI().endsWith("/") ?
		// portResource
		// .getURI() : portResource.getURI() + "/");
		if (portURIString
				.equals(labelingSpec.getSinkPortUriStringList().get(0)))
			return true;
		else
			return false;

	}

	private boolean reflectedInLabelingWorkflow(String uriString) {
		return reflectedWorkflowElements.containsKey(uriString);

	}

	private int nextCounter() {
		return counter++;
	}
}

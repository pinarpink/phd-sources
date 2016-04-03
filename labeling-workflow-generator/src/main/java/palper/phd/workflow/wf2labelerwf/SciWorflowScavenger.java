package palper.phd.workflow.wf2labelerwf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import palper.phd.labeling.model.LabelingOperatorEnum;
import palper.phd.labeling.model.LabelingSpecBean;
import palper.phd.workflow.annotation.MotifAnnotationsStub;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * 
 */

/**
 * @author pinarpink
 * 
 */
public class SciWorflowScavenger {

	private Model sciWorkflowWfdesc;

	private Map<String, LabelingSpecBean> processorSpecBag = new HashMap<String, LabelingSpecBean>();
	private Map<String, List<LabelingSpecBean>> adjustmentSpecBag = new HashMap<String, List<LabelingSpecBean>>();

	public Map<String, LabelingSpecBean> getProcessorSpecBag() {
		return processorSpecBag;
	}

	public void setProcessorSpecBag(
			Map<String, LabelingSpecBean> processorSpecBag) {
		this.processorSpecBag = processorSpecBag;
	}

	public Map<String, List<LabelingSpecBean>> getAdjustmentSpecBag() {
		return adjustmentSpecBag;
	}

	public void setAdjustmentSpecBag(
			Map<String, List<LabelingSpecBean>> adjustmentSpecBag) {
		this.adjustmentSpecBag = adjustmentSpecBag;
	}


	public SciWorflowScavenger(Model wfdescModel) {

		super();

		sciWorkflowWfdesc = wfdescModel;


		obtainLabelingSpecs();

	}

	private void obtainLabelingSpecs() {
		Resource wfResource = WfDescRdfUtils
				.getWorkflowResource(sciWorkflowWfdesc);

		Set<Resource> procs = WfDescRdfUtils.getProcessors(sciWorkflowWfdesc,
				wfResource);

		for (Resource proc : procs) {

			LabelingSpecBean processLabelingSpec = MotifAnnotationsStub
					.getLabelingSpec(proc.getURI());

			if (processLabelingSpec != null) {
				processorSpecBag.put(proc.getURI(), processLabelingSpec);

			} else {

				System.out
						.println("No motif annotation and associated labeling spec found for processor"
								+ proc.getURI());
			}
		}

		boolean detected = false;
		do {
			detected = false;
			String tobeRemoved = null;
			for (Map.Entry<String, LabelingSpecBean> entry : processorSpecBag
					.entrySet()) {

				if ((entry.getValue().getOperator()
						.equals(LabelingOperatorEnum.PROPAGATE))
						&& (isDangling(entry.getKey()))) {
					tobeRemoved = entry.getKey();
					detected = true;
					break;
				}
			}
			if (detected) {
				processorSpecBag.remove(tobeRemoved);
			}

		} while (detected);

		Set<Resource> links = WfDescRdfUtils.getDataLinks(sciWorkflowWfdesc,
				wfResource);

		for (Resource link : links) {

			Resource sourcePort = WfDescRdfUtils.getSource(sciWorkflowWfdesc,
					link);
			Resource sinkPort = WfDescRdfUtils.getSink(sciWorkflowWfdesc, link);


			if (WfDescRdfUtils.isWorkflowInputPort(sourcePort,
					sciWorkflowWfdesc)) {
				// no adjustment needed
				// no need to link two labeling operators, as there aren't two
				// there is just a workflow input port and a follow-on labeling
				// operator of type mint or propagate.

		        // APP SPECIFIC
			  
				// adjustment sadece wrapdping case'i icin gerekli

			} else if (WfDescRdfUtils.isWorkflowOutputPort(sinkPort,
					sciWorkflowWfdesc)) {
				// definitely adjustment needed
				// put generalize specs that will
				// label the enclosing lists all the way to the top.

			  
			 // APP SPECIFIC
	
			  //Note that
				// we also need to saturate the labels downwards to inner lists.
				// think of a workflow that ends with a split operator!!!.

			} else if (linkSourceTainted(sourcePort,
					WfDescRdfUtils.getOperationWithOutput(sciWorkflowWfdesc,
							sourcePort), sinkPort,
					WfDescRdfUtils.getOperationWithInput(sciWorkflowWfdesc,
							sinkPort))) {
				// adjustment might be needed
				// check whether it has been added before
				// if not added then add

				Integer srcDepth = WfDescRdfUtils.getDepth(sourcePort,
						sciWorkflowWfdesc);
				Integer snkDepth = WfDescRdfUtils.getDepth(sinkPort,
						sciWorkflowWfdesc);
				int depthDiff = srcDepth - snkDepth;
				if ((depthDiff > 0) || (depthDiff < 0)) {
					LabelingSpecBean adjustedBefore = hasAssociatedLabelingSpec(
							sourcePort, depthDiff);
					if (adjustedBefore == null) {
					  
					Resource operation = WfDescRdfUtils.getOperationWithOutput(sciWorkflowWfdesc, sourcePort);
						LabelingSpecBean spec = LabelSpecFactory
								.createAdjustmentSpec(operation.getURI(),
										sourcePort.getURI(), 
										depthDiff);
						putToAdjustmentSpecBag(sourcePort, spec);
					} else {

					  
					  //there already is an adjustment spec for the source port and the depth diference 
					  //therefore we do not need to create another adjustment spec for the source port of this datalink.. 
					}

				} else {
				  
					//there is no depth difference among the two sides of this link so no adjustment spec is needed

				}
			} else {

				// the source port of this is not tainted by labels in any way therefore we ignore this link
			}

		}

	}

	private void putToAdjustmentSpecBag(Resource sourcePort,
			LabelingSpecBean spec) {
		if (adjustmentSpecBag.get(sourcePort) != null) {
			adjustmentSpecBag.get(sourcePort).add(spec);
		} else {

			List<LabelingSpecBean> specList = new ArrayList<LabelingSpecBean>();
			specList.add(spec);
			adjustmentSpecBag.put(sourcePort.getURI(), specList);
		}

	}

	private LabelingSpecBean hasAssociatedLabelingSpec(Resource sourcePort,
			int depthDiff) {
		LabelingSpecBean adjustedBefore = null;
		for (Map.Entry<String, List<LabelingSpecBean>> entry : adjustmentSpecBag
				.entrySet()) {
			if (entry.getKey().equals(sourcePort.getURI())) {
				List<LabelingSpecBean> adjustmentSpecs = entry.getValue();
				for (LabelingSpecBean spec : adjustmentSpecs) {
					if (spec.getDataLinkDepthDifference() == depthDiff) {
						adjustedBefore = spec;
					}
				}
			}
		}

		return adjustedBefore;
	}

	private boolean linkSourceTainted(Resource sourcePort, Resource sourceOp,
			Resource sinkPort, Resource sinkOp) {
		boolean sourceTainted = false;

		for (Map.Entry<String, LabelingSpecBean> entry : processorSpecBag
				.entrySet()) {

			if (entry.getKey().equals(sourceOp.getURI())) {

				LabelingSpecBean sourceSpec = entry.getValue();
				List<String> taintedPorts = sourceSpec
						.getSinkPortUriStringList();
				for (String port : taintedPorts) {
					if (port.equals(sourcePort.getURI())) {
						sourceTainted = true;
					}
				}

			}

		}

		return (sourceTainted);
	}

	private boolean isDangling(String procResourceUri) {

		// a dangling propagate operator is one that is preceeded entirely with
		// black box operators

		Resource procResource = sciWorkflowWfdesc.getResource(procResourceUri);

		Set<Resource> upstreamOps = WfDescRdfUtils.getDependencyOperations(
				procResource, sciWorkflowWfdesc);

		List<String> coveredSourcePorts = processorSpecBag.get(
				procResource.getURI()).getSourcePortUriStringList();

		boolean allblackbox = true;
		for (Resource op : upstreamOps) {
			LabelingSpecBean preceedingSpec = processorSpecBag.get(op.getURI());
			if (preceedingSpec != null) {

				List<String> taintedSourcePorts = preceedingSpec
						.getSinkPortUriStringList();

				// check if there is a datalink that links any two ports from
				// the above two lists.

				for (String src : taintedSourcePorts) {
					for (String snk : coveredSourcePorts) {

						Resource srcRes = sciWorkflowWfdesc.getResource(src);
						Resource snkRes = sciWorkflowWfdesc.getResource(snk);
						if (WfDescRdfUtils.datalinkExists(srcRes, snkRes,
								sciWorkflowWfdesc)) {
							allblackbox = false;
						}
					}
				}

			}
		}

		return allblackbox;
	}

}

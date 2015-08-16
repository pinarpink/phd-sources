/**
 * 
 */
package palper.phd.workflow.wf2labelerwf;

import java.util.ArrayList;
import java.util.List;

import palper.phd.labeling.model.LabelingOperatorEnum;
import palper.phd.labeling.model.LabelingSpecBean;

/**
 * @author pinarpink
 * 
 */
public class LabelSpecFactory {

	public static LabelingSpecBean createMintSpec(String operationUri,
			String sourceUri, String sinkUri, String labelFunctionName) {

		if ((sinkUri == null) || (labelFunctionName == null)
				|| (operationUri == null)) {
			throw new IllegalArgumentException();
		}

		LabelingSpecBean result = new LabelingSpecBean();
		result.setOperator(LabelingOperatorEnum.MINT);

		if (sourceUri != null) {
			List<String> sourceList = new ArrayList<String>();
			sourceList.add(sourceUri);
			result.setSourcePortUriStringList(sourceList);
		}

		List<String> sinkList = new ArrayList<String>();
		sinkList.add(sinkUri);
		result.setSinkPortUriStringList(sinkList);
		List<String> wfElements= new ArrayList<String>();
		wfElements.add(operationUri);
		result.setWfElementUriStringList(wfElements);
		result.setLabelingFunctionIdentifier(labelFunctionName);
		return result;
	}

	public static LabelingSpecBean createPropagateSpec(String operationUri,
			String sourceUri, String sinkUri, String labelFunctionName) {

		LabelingSpecBean result = new LabelingSpecBean();
		result.setOperator(LabelingOperatorEnum.PROPAGATE);

		List<String> sinkList = new ArrayList<String>();
		sinkList.add(sinkUri);
		result.setSinkPortUriStringList(sinkList);

		List<String> sourceList = new ArrayList<String>();
		sourceList.add(sourceUri);
		result.setSourcePortUriStringList(sourceList);

		List<String> wfElements= new ArrayList<String>();
		wfElements.add(operationUri);
		result.setWfElementUriStringList(wfElements);
		result.setLabelingFunctionIdentifier(labelFunctionName);

		return result;
	}

	public static LabelingSpecBean createAdjustmentSpec(String linkUri,
			String sourceUri, String sinkUri, 
			Integer depthDiff) {

		LabelingSpecBean result = new LabelingSpecBean();
		
		if (depthDiff>0){
			result.setOperator(LabelingOperatorEnum.DISTRIBUTE);
		}else if (depthDiff<0){
			result.setOperator(LabelingOperatorEnum.GENERALIZE);
		}
		

		// We do not set a labeling function for the Labeling specs
		// corresponding to
		// datalinks in the workflow. As the labeling behvior for these is
		// fixed.

		List<String> sinkList = new ArrayList<String>();
		sinkList.add(sinkUri);
		result.setSinkPortUriStringList(sinkList);

		List<String> sourceList = new ArrayList<String>();
		sourceList.add(sourceUri);
		result.setSourcePortUriStringList(sourceList);
		
		
		List<String> wfElements= new ArrayList<String>();
		wfElements.add(linkUri);
		result.setWfElementUriStringList(wfElements);
		result.setDataLinkDepthDifference(depthDiff);

		return result;
	}

}

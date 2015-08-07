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


	
	public static  LabelingSpecBean createMintSpec(String operationUri, String sourceUri, String sinkUri, String labelFunctionName){
		
		LabelingSpecBean result = new LabelingSpecBean();
		result.setOperator(LabelingOperatorEnum.MINT);

		List<String>  sinkList = new ArrayList<String>();
		sinkList.add(sinkUri);
		
		result.setSinkPortUriStringList(sinkList);
		result.setWfElementUriString(operationUri);
		result.setLabelingFunctionIdentifier(labelFunctionName);
		return result;
	}

	public static  LabelingSpecBean createPropagateSpec(String operationUri, String sourceUri, String sinkUri, String labelFunctionName){
		
		LabelingSpecBean result = new LabelingSpecBean();
		result.setOperator(LabelingOperatorEnum.PROPAGATE);

		
		List<String>  sinkList = new ArrayList<String>();
		sinkList.add(sinkUri);
		
		result.setSinkPortUriStringList(sinkList);
		
		List<String>  sourceList = new ArrayList<String>();
		sourceList.add(sourceUri);
		
		result.setSourcePortUriStringList(sourceList);
		
		
		result.setWfElementUriString(operationUri);
		
		
		result.setLabelingFunctionIdentifier(labelFunctionName);
		return result;
	}
}

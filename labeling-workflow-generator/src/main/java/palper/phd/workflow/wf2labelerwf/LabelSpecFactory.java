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

  public static LabelingSpecBean createMintSpec(String operationUri, List<String> sinkUriList,
      String labelFunctionName) {

    if ((sinkUriList == null) || (labelFunctionName == null) || (operationUri == null)) {
      throw new IllegalArgumentException();
    }
    if ((sinkUriList.size() == 0)) {
      throw new IllegalArgumentException();
    }
    LabelingSpecBean result = new LabelingSpecBean();
    result.setOperator(LabelingOperatorEnum.MINT);


    result.setSinkPortUriStringList(sinkUriList);
    result.setProcessorUriString(operationUri);

    result.setLabelingFunctionIdentifier(labelFunctionName);
    return result;
  }

  public static LabelingSpecBean createPropagateSpec(String operationUri,
      List<String> sourceUriList, List<String> sinkUriList) {


    if ((sinkUriList == null) || (sourceUriList == null) || (operationUri == null)) {
      throw new IllegalArgumentException();
    }
    if ((sinkUriList.size() == 0) || (sourceUriList.size() == 0)) {
      throw new IllegalArgumentException();
    }
    LabelingSpecBean result = new LabelingSpecBean();
    result.setOperator(LabelingOperatorEnum.PROPAGATE);


    result.setSinkPortUriStringList(sinkUriList);
    result.setSourcePortUriStringList(sourceUriList);


    result.setProcessorUriString(operationUri);


    return result;
  }

  public static LabelingSpecBean createAdjustmentSpec(String operationUri, String sourcePortUri,
      Integer depthDiff) {


    if ((operationUri == null) || (sourcePortUri == null) || (depthDiff == null)) {
      throw new IllegalArgumentException();
    }

    LabelingSpecBean result = new LabelingSpecBean();


    if (depthDiff > 0) {
      result.setOperator(LabelingOperatorEnum.DISTRIBUTE);
    } else if (depthDiff < 0) {
      result.setOperator(LabelingOperatorEnum.GENERALIZE);
    }



    List<String> sourceList = new ArrayList<String>();
    sourceList.add(sourcePortUri);
    result.setSourcePortUriStringList(sourceList);

    result.setProcessorUriString(operationUri);


    result.setDataLinkDepthDifference(depthDiff);

    return result;
  }

}

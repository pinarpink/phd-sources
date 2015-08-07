/**
 * 
 */
package palper.phd.labeling.api;

import java.util.List;

import palper.phd.labeling.model.LabelInstanceBean;

/**
 * @author pinarpink
 * 
 */
public interface ILabelAggregationFunction {
	List<LabelInstanceBean> run(List<LabelInstanceBean> invocationParamValues);

}

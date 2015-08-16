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
public interface ILabelPropagationFunction {
	List<LabelInstanceBean> run(List<LabelInstanceBean> inputLabels);

}

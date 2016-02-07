/**
 * 
 */
package palper.phd.labeling.stubs;

import palper.phd.labeling.model.LabelInstanceBean;

/**
 * @author pinarpink
 * 
 */
public class PropagationStubs {


	public static LabelInstanceBean cloneLabelInstance(
			LabelInstanceBean original, String newTargetURIString) {

		LabelInstanceBean clone = new LabelInstanceBean();
		clone.setDefiniton(original.getDefiniton());
		clone.setValue(original.getValue());
		clone.setLabelTargetURIString(newTargetURIString);
		return clone;
	}
}

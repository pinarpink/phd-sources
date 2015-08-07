/**
 * 
 */
package palper.phd.labeling.stubs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import palper.phd.labeling.model.LabelInstanceBean;
import palper.phd.labeling.model.LabelingConstants;

/**
 * @author pinarpink
 * 
 */
public class PropagationStubs {

	@Deprecated
	public static List<LabelInstanceBean> invokePropagatorFunction(
			String propagationFunctionType, List<LabelInstanceBean> labelList) {

		List<LabelInstanceBean> results = new ArrayList<LabelInstanceBean>();

		if (propagationFunctionType
				.equalsIgnoreCase(LabelingConstants.INVARIANT)) {

			for (LabelInstanceBean lbl : labelList) {
				LabelInstanceBean propagatedLbl = new LabelInstanceBean();
				propagatedLbl.setDefiniton(lbl.getDefiniton());
				propagatedLbl.setValue(lbl.getValue());
				results.add(propagatedLbl);
			}
		} else if (propagationFunctionType
				.equalsIgnoreCase(LabelingConstants.CUMULATIVE)) {
			// this is a simple union of multiple labels into a set BUT,
			// in fact it could also be a convex type cummulative function. it
			// all dependes on the label definition!!
			Set<LabelInstanceBean> unitedResults = new HashSet<LabelInstanceBean>();
			for (LabelInstanceBean lbl : labelList) {
				LabelInstanceBean propagatedLbl = new LabelInstanceBean();
				propagatedLbl.setDefiniton(lbl.getDefiniton());
				propagatedLbl.setValue(lbl.getValue());

				unitedResults.add(propagatedLbl);
			}
			results.addAll(unitedResults);
		}

		return results;
	}

	public static LabelInstanceBean cloneLabelInstance(
			LabelInstanceBean original, String newTargetURIString) {

		LabelInstanceBean clone = new LabelInstanceBean();
		clone.setDefiniton(original.getDefiniton());
		clone.setValue(original.getValue());
		clone.setLabelTargetURIString(newTargetURIString);
		return clone;
	}
}

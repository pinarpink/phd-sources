/**
 * 
 */
package palper.phd.labeling.model;

import java.util.List;

/**
 * @author pinarpink
 *
 */
public class LabelVectorBean {

	String identifierUriString;

	List<LabelDefinitonBean>  labelDefinitions;

	
	public String getIdentifierUriString() {
		return identifierUriString;
	}
	public void setIdentifierUriString(String identifierUriString) {
		this.identifierUriString = identifierUriString;
	}
	public List<LabelDefinitonBean> getLabelDefinitions() {
		return labelDefinitions;
	}
	public void setLabelDefinitions(List<LabelDefinitonBean> labelDefinitions) {
		this.labelDefinitions = labelDefinitions;
	}
}

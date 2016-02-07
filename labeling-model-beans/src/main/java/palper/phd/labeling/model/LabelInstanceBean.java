/**
 * 
 */
package palper.phd.labeling.model;

import java.io.Serializable;

/**
 * @author pinarpink
 *
 */
public class LabelInstanceBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8397228331509346539L;

	private String  labelTargetURIString;

	private LabelDefinitonBean  definiton;
	
	private Object value;


	
	public String getLabelTargetURIString() {
		return labelTargetURIString;
	}

	public void setLabelTargetURIString(String labelTargetURIString) {
		this.labelTargetURIString = labelTargetURIString;
	}

	public LabelDefinitonBean getDefiniton() {
		return definiton;
	}

	public void setDefiniton(LabelDefinitonBean definiton) {
		this.definiton = definiton;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object arg0) {
		
		LabelInstanceBean bean = (LabelInstanceBean) arg0;
		
		if (this.definiton.equals(bean.getDefiniton()) 
				&& this.labelTargetURIString.equals(bean.getLabelTargetURIString()) 
				&& this.value.equals(bean.getValue())) return true;
		else return false;
		
	}
}

/**
 * 
 */
package palper.phd.provenance.model;

/**
 * @author pinarpink
 * 
 */
public class PortBean implements WorkflowElementBean{

	PortTypeEnum type;
	String uriString;

	public PortTypeEnum getType() {
		return type;
	}



	public PortBean(PortTypeEnum type, String uriString) {
		this.uriString = uriString;
		this.type = type;
	
	}

	public boolean isInput() {

		if (type.equals(PortTypeEnum.INPUT)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isOutput() {

		if (type.equals(PortTypeEnum.OUTPUT)) {
			return true;
		} else {
			return false;
		}
	}



	public String getUriString() {
		
		return uriString;
	}
}

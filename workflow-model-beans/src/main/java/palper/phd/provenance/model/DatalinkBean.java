/**
 * 
 */
package palper.phd.provenance.model;

/**
 * @author pinarpink
 *
 */
public class DatalinkBean implements WorkflowElementBean{

	
	String uriString;
	
	public DatalinkBean(String uriString) {
		super();
		this.uriString = uriString;
	}

	public String getUriString() {
		return uriString;
	}

}

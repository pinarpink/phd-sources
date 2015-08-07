/**
 * 
 */
package palper.phd.provenance.model;

/**
 * @author pinarpink
 *
 */
public class OperationBean implements WorkflowElementBean{

	String uriString;
	
	public OperationBean(String uriString) {
		super();
		this.uriString = uriString;
	}

	public String getUriString() {

		return uriString;
	}



}

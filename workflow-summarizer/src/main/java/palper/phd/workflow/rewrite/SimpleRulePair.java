/**
 * 
 */
package palper.phd.workflow.rewrite;

/**
 * @author pinarpink
 *
 */
public class SimpleRulePair {

	
	String motifURI;
	String primitive;
	public SimpleRulePair(String motifURI, String primitive) {
		super();
		this.motifURI = motifURI;
		this.primitive = primitive;
	}
	public String getMotifURI() {
		return motifURI;
	}
	
	public String getPrimitive() {
		return primitive;
	}

	
}

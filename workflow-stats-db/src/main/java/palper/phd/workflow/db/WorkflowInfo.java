/**
 * 
 */
package palper.phd.workflow.db;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import palper.phd.workflow.wfdesc.WfdescStatistics;

import com.hp.hpl.jena.rdf.model.InfModel;

/**
 * @author pinarpink
 *
 */
public class WorkflowInfo implements Serializable  {

	
	List<String> unactionableProcessUris;

	InfModel wfdescModel;
	
	String wfdescAsString;
	
	WfdescStatistics statistics;

	
	 private void writeObject(ObjectOutputStream o)
			    throws IOException {  
			    
			    o.writeObject((ArrayList<String>)unactionableProcessUris);  
			    o.writeObject(wfdescAsString);
			    o.writeObject(statistics);
			  }
			  
			  private void readObject(ObjectInputStream o)
			    throws IOException, ClassNotFoundException {  
			    
				  unactionableProcessUris = (ArrayList<String>) o.readObject();  
				  wfdescAsString = (String)o.readObject();
				  statistics = (WfdescStatistics) o.readObject();
			    
			  }
	
	public List<String> getUnactionableProcessUris() {
		return unactionableProcessUris;
	}

	public void setUnactionableProcessUris(List<String> unactionableProcessUris) {
		this.unactionableProcessUris = unactionableProcessUris;
	}

	
	public WfdescStatistics getStatistics() {
		return statistics;
	}

	public void setStatistics(WfdescStatistics statistics) {
		this.statistics = statistics;
	}


	public InfModel getWfdescModel() {
		return wfdescModel;
	}

	public void setWfdescModel(InfModel wfdescModel) {
		this.wfdescModel = wfdescModel;
	}
	public String getWfdescAsString() {
		return wfdescAsString;
	}

	public void setWfdescAsString(String wfdescAsString) {
		this.wfdescAsString = wfdescAsString;
	}
	
}

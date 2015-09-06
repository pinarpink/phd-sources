/**
 * 
 */
package palper.phd.workflow.wfdesc;

import java.io.Serializable;
import java.util.Set;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author pinarpink
 * 
 */
public class WfdescStatistics  implements Serializable{

	private int numOfProcs = 0;
	private int numOfLinks = 0;

	
	private int numOfConstantInputs = 0;
	private int numOfWfInputs = 0;
	private int numOfWfOutputs = 0;

	private int numOfInternalInputPorts = 0;
	private int numOfInternalOutputPorts = 0;

	

	public WfdescStatistics() {
		super();
	}
		
	public WfdescStatistics(Model model) {
		
		
		super();
		Resource wfResource =  WfDescRdfUtils.getWorkflowResource(model);
		Set<Resource> procs = WfDescRdfUtils.getProcessors(model,wfResource);
		numOfProcs = procs.size();
		
		for (Resource proc:procs){
			
			if (WfDescRdfUtils.isStringConstant(model, proc)){
				numOfProcs = numOfProcs -1;
				numOfConstantInputs = numOfConstantInputs +1;	
			}
		}
		
		
		Set<Resource> wfInputs = WfDescRdfUtils.getInputPorts(wfResource, model);
		Set<Resource> wfOutputs = WfDescRdfUtils.getOutputPorts(wfResource, model);
		
		numOfWfInputs = wfInputs.size();
		numOfWfOutputs = wfOutputs.size();
		
		Set<Resource>  links = WfDescRdfUtils.getDataLinks(model, wfResource);
		numOfLinks = links.size();
		
		for (Resource proc:procs){
			Set<Resource> inputs = WfDescRdfUtils.getInputPorts(proc, model);
			numOfInternalInputPorts=numOfInternalInputPorts+inputs.size();
			
			Set<Resource>  outputs=WfDescRdfUtils.getOutputPorts(proc, model);
			numOfInternalOutputPorts=numOfInternalOutputPorts + outputs.size();
		}
	
		
	}

	@Override
	public String toString() {
		String result = "\n"
	            +"numOfProcs: " +numOfProcs+"\n"
				+"numOfLinks: " +numOfLinks+"\n"
				+"numOfConstantInputs: " +numOfConstantInputs +"\n"
				+"numOfWfInputs: " +numOfWfInputs +"\n"
				+"numOfWfOutputs: " +numOfWfOutputs+"\n"
				+"numOfInternalInputPorts: " +numOfInternalInputPorts +"\n"
				+"numOfInternalOutputPorts: " +numOfInternalOutputPorts +"\n";
		return result;
	}
	
	public int getNumOfProcs() {
		return numOfProcs;
	}

	public void setNumOfProcs(int numOfProcs) {
		this.numOfProcs = numOfProcs;
	}

	public int getNumOfLinks() {
		return numOfLinks;
	}

	public void setNumOfLinks(int numOfLinks) {
		this.numOfLinks = numOfLinks;
	}

	public int getNumOfConstantInputs() {
		return numOfConstantInputs;
	}

	public void setNumOfConstantInputs(int numOfConstantInputs) {
		this.numOfConstantInputs = numOfConstantInputs;
	}

	public int getNumOfWfInputs() {
		return numOfWfInputs;
	}

	public void setNumOfWfInputs(int numOfWfInputs) {
		this.numOfWfInputs = numOfWfInputs;
	}

	public int getNumOfWfOutputs() {
		return numOfWfOutputs;
	}

	public void setNumOfWfOutputs(int numOfWfOutputs) {
		this.numOfWfOutputs = numOfWfOutputs;
	}

	public int getNumOfInternalInputPorts() {
		return numOfInternalInputPorts;
	}

	public void setNumOfInternalInputPorts(int numOfInternalInputPorts) {
		this.numOfInternalInputPorts = numOfInternalInputPorts;
	}

	public int getNumOfInternalOutputPorts() {
		return numOfInternalOutputPorts;
	}

	public void setNumOfInternalOutputPorts(int numOfInternalOutputPorts) {
		this.numOfInternalOutputPorts = numOfInternalOutputPorts;
	}
	
	
}

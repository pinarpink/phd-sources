/**
 * 
 */
package palper.phd.workflow.wfdesc;

import java.util.Set;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author pinarpink
 * 
 */
public class WorkflowStatisticsCollector {

	private int numOfProcs = 0;
	private int numOfLinks = 0;

	
	private int numOfConstantInputs = 0;
	private int numOfWfInputs = 0;
	private int numOfWfOutputs = 0;

	private int numOfInternalInputPorts = 0;
	private int numOfInternalOutputPorts = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public WorkflowStatisticsCollector(Model model) {
		
		
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
	
	
}

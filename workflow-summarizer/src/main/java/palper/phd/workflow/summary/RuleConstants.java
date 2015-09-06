package palper.phd.workflow.summary;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class RuleConstants {
	
   
    static String collapseUp = "collapseUp";
    static String collapseDown = "collapseDown";
    static String eliminate = "eliminate";
    static String compose = "compose";
	
	static Map<String, SolutionHandler>  handlers;
	
	static {
		handlers = new HashMap<String, SolutionHandler>();

		handlers.put(collapseDown, new SolutionHandler() {
		
        public boolean handleSummaryRuleSparql(QuerySolution soln, InfModel model) {
        	
			Resource op = soln.getResource("op");
			Resource wf = soln.getResource("wf");
			
			Set<Resource> procs = WfDescRdfUtils.getProcessors(model, WfDescRdfUtils.getWorkflowResource(model));
			
			boolean result = WorkflowRewritePrimitives.diffuseOperationDownStream(model, op, wf);
					
			Set<Resource> procs2 = WfDescRdfUtils.getProcessors(model, WfDescRdfUtils.getWorkflowResource(model));
				
			return result; 
        	
        };
    });

		handlers.put(collapseUp, new SolutionHandler() {
			
	        public boolean handleSummaryRuleSparql(QuerySolution soln, InfModel model) {
	        	

				Resource op = soln.getResource("op");
				Resource wf = soln.getResource("wf");
				
				
				return WorkflowRewritePrimitives.diffuseOperationUpStream(model, op, wf); 
	        	
	        };
	    });
		
		
		
		handlers.put(eliminate, new SolutionHandler() {
			
            public boolean handleSummaryRuleSparql(QuerySolution soln, InfModel model) {
            	
				Resource op = soln.getResource("op");
				
				return WorkflowRewritePrimitives.eliminateOperation(model, op); 
            	
            };
        });
		
		
		
//		handlers.put(compose, new SolutionHandler() {
//			
//            public boolean handleSummaryRuleSparql(QuerySolution soln, InfModel model) {
//            	
//				Resource opA = soln.getResource("opA");
//				Resource opB = soln.getResource("opB");
//				
//				return WorkflowRewritePrimitives.combineOperations(model, opA, opB, MotifPropagationPolicy.BOTH); 
//            	
//            };
//        });

	}
	
	static Map<String, SolutionHandler> getHandlers(){
		return handlers;
	}
}

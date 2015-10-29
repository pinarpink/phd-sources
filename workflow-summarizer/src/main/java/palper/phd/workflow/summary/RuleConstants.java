package palper.phd.workflow.summary;

import java.util.HashMap;
import java.util.Map;

import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Resource;

public class RuleConstants {
	
   
    static String collapseUp = "collapseUp";
    static String collapseDown = "collapseDown";
    static String eliminate = "eliminate";
//    static String compose = "compose";
	
	static Map<String, SolutionHandler>  handlers;
	
	static {
		handlers = new HashMap<String, SolutionHandler>();

		handlers.put(collapseDown, new SolutionHandler() {
		
        public boolean handleSummaryRuleSparql(QuerySolution soln, InfModel model) {
        	
			Resource op = soln.getResource("op");
			
			Resource wfResource = WfDescRdfUtils.getEnclosingWorkflow(model, op);
		
			boolean result = WorkflowRewritePrimitives.diffuseOperationDownStream(model, op, wfResource);
					
			
			return result; 
        	
        };
    });

		handlers.put(collapseUp, new SolutionHandler() {
			
	        public boolean handleSummaryRuleSparql(QuerySolution soln, InfModel model) {
	        	

				Resource op = soln.getResource("op");
		              
		        Resource wfResource = WfDescRdfUtils.getEnclosingWorkflow(model, op);
           
				
				return WorkflowRewritePrimitives.diffuseOperationUpStream(model, op, wfResource); 
	        	
	        };
	    });
		
		
		
		handlers.put(eliminate, new SolutionHandler() {
			
            public boolean handleSummaryRuleSparql(QuerySolution soln, InfModel model) {
            	

              Resource op = soln.getResource("op");
                           
				return WorkflowRewritePrimitives.eliminateOperation(model, op); 
            	
            };
        });
		
		


	}
	
	static Map<String, SolutionHandler> getHandlers(){
		return handlers;
	}
}

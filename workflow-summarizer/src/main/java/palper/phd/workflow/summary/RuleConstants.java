package palper.phd.workflow.summary;

import java.util.HashMap;
import java.util.Map;

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
		
        public Model handleSummaryRuleSparql(QuerySolution soln, InfModel model) {
        	

			Resource opA = soln.getResource("opA");
			
			return WorkflowRewritePrimitives.diffuseOperationDownStream(model, opA); 
        	
        };
    });

		handlers.put(collapseUp, new SolutionHandler() {
			
	        public Model handleSummaryRuleSparql(QuerySolution soln, InfModel model) {
	        	

				Resource opB = soln.getResource("opB");
				
				return WorkflowRewritePrimitives.diffuseOperationUpStream(model, opB); 
	        	
	        };
	    });
		
		
		
		handlers.put(eliminate, new SolutionHandler() {
			
            public Model handleSummaryRuleSparql(QuerySolution soln, InfModel model) {
            	
				Resource opA = soln.getResource("opA");
				
				return WorkflowRewritePrimitives.eliminateOperation(model, opA); 
            	
            };
        });
		
		
		
		handlers.put(compose, new SolutionHandler() {
			
            public Model handleSummaryRuleSparql(QuerySolution soln, InfModel model) {
            	
				Resource opA = soln.getResource("opA");
				Resource opB = soln.getResource("opB");
				
				return WorkflowRewritePrimitives.combineOperations(model, opA, opB, MotifPropagationPolicy.BOTH); 
            	
            };
        });
//		handlers.put(unsinkedOutput, new SolutionHandler() {
//			
//            public Model handleSummaryRuleSparql(QuerySolution soln, InfModel model) {
//            	
//				Resource outA = soln.getResource("outA");
//			
//				
//				return WorkflowRewritePrimitives.eliminatePort(model, outA); 
//            	
//            };
//        });
//		
//		handlers.put(unsourcedInput, new SolutionHandler() {
//			
//            public Model handleSummaryRuleSparql(QuerySolution soln, InfModel model) {
//            	
//				Resource inA = soln.getResource("inA");
//			
//				
//				return WorkflowRewritePrimitives.eliminatePort(model, inA); 
//            	
//            };
//        });
	}
	
	static Map<String, SolutionHandler> getHandlers(){
		return handlers;
	}
}

/**
 * 
 */
package palper.phd.workflow.summary;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author pinarpink
 *
 */
public interface SolutionHandler {
	
	boolean handleSummaryRuleSparql(QuerySolution soln, InfModel model);

}

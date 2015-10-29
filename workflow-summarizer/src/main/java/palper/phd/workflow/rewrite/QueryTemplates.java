/**
 * 
 */
package palper.phd.workflow.rewrite;

/**
 * @author pinarpink
 *
 */
public class QueryTemplates {

	
	private static String queryStr = " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
			+ " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
			+ " PREFIX motifs:  <http://purl.org/net/wf-motifs#> \n"
			+ " SELECT DISTINCT  ?op  \n"
			+ " WHERE { \n"
		    + " <TOKEN2>  a wfdesc:Workflow .\n"		
			+ " <TOKEN2> wfdesc:hasSubProcess ?op . \n"
			+ " ?op a wfdesc:Process . \n"
			+ " ?op motifs:hasDataOperationMotif ?mtf . \n"
			+" ?mtf a <TOKEN1> } \n";

	
	
	public static String getParameterizedQuery(String motif, String wfUri){
		return queryStr.replace("TOKEN1", motif).replace("TOKEN2", wfUri);
	
	
	}
}
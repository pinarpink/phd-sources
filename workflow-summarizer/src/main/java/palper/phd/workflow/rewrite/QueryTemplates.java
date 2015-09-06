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
			+ " PREFIX motifs:  <http://purl.org/wf4ever/motifs.owl#> \n"
			+ " SELECT DISTINCT ?wf ?op  \n"
			+ " WHERE { \n"
			+ " ?wf a wfdesc:Workflow . \n"			
			+ " ?wf wfdesc:hasSubProcess ?op . \n"
			+ " ?op a wfdesc:Process . \n"
			+ " ?op motifs:hasOperationMotif ?mtf . \n"
			+" ?mtf a <TOKEN> } \n";

	
	
	public static String getParameterizedQuery(String motif){
		return queryStr.replace("TOKEN", motif);
		
	}
}
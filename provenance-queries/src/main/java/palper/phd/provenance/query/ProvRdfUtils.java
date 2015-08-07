/**
 * 
 */
package palper.phd.provenance.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import palper.phd.labeling.model.LabelDefinitonBean;
import palper.phd.labeling.model.LabelInstanceBean;
import palper.phd.labeling.model.XSDatatypeEnum;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * @author pinarpink
 * 
 */
public class ProvRdfUtils {

	
	
	public static Set<Resource> getArtifactsDescribedByParameter (String parameterNameUriString, Model model) {

		Set<Resource> result = new HashSet<Resource>();

		String usageQueryStr = " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
				+ " PREFIX wfprov:    <http://purl.org/wf4ever/wfprov#> \n"
				+ " SELECT DISTINCT ?entit1  \n" + " WHERE { \n"
				+ " ?entit1 wfprov:describedByParameter"
				+ " <" + parameterNameUriString + "> . \n" + " } \n";

		Query usageQuery = QueryFactory.create(usageQueryStr);

		QueryExecution usageQueryExec = QueryExecutionFactory.create(
				usageQuery, model);

		try {
			ResultSet results = usageQueryExec.execSelect();

			for (; results.hasNext();) {

				QuerySolution soln = results.nextSolution();

				result.add(soln.getResource("entit1"));
			}

		} finally {
			usageQueryExec.close();

		}
		return result;
	}
	
	
	
	
	public static Set<Resource> getAllUsageInstances(String portUriString,
			Model model) {

		Set<Resource> result = new HashSet<Resource>();

		String usageQueryStr = " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
				+ " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ " SELECT DISTINCT ?entit1  \n" + " WHERE { \n"
				+ " ?actvty prov:qualifiedUsage ?usg1 . \n"
				+ " ?usg1 prov:entity ?entit1 .\n" + " ?usg1 prov:hadRole"
				+ " <" + portUriString + "> . \n" + " } \n";

		Query usageQuery = QueryFactory.create(usageQueryStr);

		QueryExecution usageQueryExec = QueryExecutionFactory.create(
				usageQuery, model);

		try {
			ResultSet results = usageQueryExec.execSelect();

			for (; results.hasNext();) {

				QuerySolution soln = results.nextSolution();

				result.add(soln.getResource("entit1"));
			}

		} finally {
			usageQueryExec.close();

		}
		return result;
	}

	public static Set<Resource> getAllGenerationInstances(String portUriString,
			Model model) {

		Set<Resource> result = new HashSet<Resource>();

		String generationQueryStr = " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
				+ " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ " SELECT DISTINCT ?entit1  \n"
				+ " WHERE { \n"
				+ " ?entit1 prov:qualifiedGeneration ?gen1 . \n"
				+ " ?gen1 prov:hadRole"
				+ " <"
				+ portUriString
				+ "> . \n"
				+ " } \n";

		Query generationQuery = QueryFactory.create(generationQueryStr);
		QueryExecution generationQueryExec = QueryExecutionFactory.create(
				generationQuery, model);

		try {

			ResultSet resultsGeneration = generationQueryExec.execSelect();

			for (; resultsGeneration.hasNext();) {

				QuerySolution soln = resultsGeneration.nextSolution();

				result.add(soln.getResource("entit1"));
			}

		} finally {

			generationQueryExec.close();
		}
		return result;
	}

	public static Map<String, String> getPortArtefactMapForActivity(String activityInstanceUri,Model model) {

		Map<String, String> result = new HashMap<String, String>();

		String usageQueryStr = " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
				+ " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ " SELECT DISTINCT ?entit1 ?role1  \n" 
				+ " WHERE { \n"
				+ " <"+activityInstanceUri+"> prov:qualifiedUsage ?usg1 . \n"
				+ " ?usg1 prov:entity ?entit1 .\n" 
				+ " ?usg1 prov:hadRole ?role1 .\n"
				+ " } \n";

		Query usageQuery = QueryFactory.create(usageQueryStr);

		QueryExecution usageQueryExec = QueryExecutionFactory.create(
				usageQuery, model);

		try {
			ResultSet results = usageQueryExec.execSelect();

			for (; results.hasNext();) {

				QuerySolution soln = results.nextSolution();

				result.put(soln.getResource("role1").getURI(),soln.getResource("entit1").getURI());
			}

		} finally {
			usageQueryExec.close();

		}

		String generationQueryStr = " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
				+ " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ " SELECT DISTINCT ?entit1 ?role1 \n"
				+ " WHERE { \n"
				+ " ?entit1 prov:qualifiedGeneration ?gen1 . \n"
				+ " ?gen1 prov:hadRole ?role1 . \n"
				+ " ?gen1 prov:activity <"+activityInstanceUri+"> .\n"
				+ " } \n";


		Query generationQuery = QueryFactory.create(generationQueryStr);

		QueryExecution generationQueryExec = QueryExecutionFactory.create(
				generationQuery, model);

		try {
			ResultSet results = generationQueryExec.execSelect();

			for (; results.hasNext();) {

				QuerySolution soln = results.nextSolution();

				result.put(soln.getResource("role1").getURI(),soln.getResource("entit1").getURI());
			}

		} finally {
			generationQueryExec.close();
		}
		
		return result;
	}

	
	public static Resource getDataContentPath (String artefactId, Model model){
		Resource result = null;

		String contentQueryStr = " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
				+ " PREFIX tavernaprov:  <http://ns.taverna.org.uk/2012/tavernaprov/> \n"
				+ " SELECT DISTINCT ?cont  \n"
				+ " WHERE { \n"
				+ " <"+artefactId+"> tavernaprov:content  ?cont . \n"
				+ " } \n";

		Query generationQuery = QueryFactory.create(contentQueryStr);
		QueryExecution generationQueryExec = QueryExecutionFactory.create(
				generationQuery, model);

		try {

			ResultSet resultsGeneration = generationQueryExec.execSelect();

			for (; resultsGeneration.hasNext();) {

				QuerySolution soln = resultsGeneration.nextSolution();

				result = soln.getResource("cont");
			}

		} finally {

			generationQueryExec.close();
		}
		return result;
		
		
	}
	public static Set<Resource> getInstantiationsOfOperation(
			String operationUriString, Model model) {

		Set<Resource> result = new HashSet<Resource>();

		String generationQueryStr = " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
				+ " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ " SELECT DISTINCT ?entit1  \n"
				+ " WHERE { \n"
				+ " ?entit1 prov:qualifiedAssociation ?assoc1 . \n"
				+ " ?assoc1 prov:hadPlan"
				+ " <"
				+ operationUriString
				+ "> . \n" + " } \n";

		Query generationQuery = QueryFactory.create(generationQueryStr);
		QueryExecution generationQueryExec = QueryExecutionFactory.create(
				generationQuery, model);

		try {

			ResultSet resultsGeneration = generationQueryExec.execSelect();

			for (; resultsGeneration.hasNext();) {

				QuerySolution soln = resultsGeneration.nextSolution();

				result.add(soln.getResource("entit1"));
			}

		} finally {

			generationQueryExec.close();
		}
		return result;
	}

	public static List<LabelInstanceBean> getLabelsOfArtifacts(
			String labelURIString, String artifactId, Model model) {

		List<LabelInstanceBean> result = new ArrayList<LabelInstanceBean>();

		LabelDefinitonBean def = new LabelDefinitonBean();
		def.setLabelNameURIString(labelURIString);

		// TODO do a lookup for the actual type here!!!!
		def.setDataTypeEnum(XSDatatypeEnum.XS_STRING);

		StmtIterator iter = model.listStatements(model.getResource(artifactId),
				model.getProperty(labelURIString), (RDFNode) null);

		while (iter.hasNext()) {

			Statement stmt = iter.next();
			LabelInstanceBean ins = new LabelInstanceBean();
			ins.setDefiniton(def);

			
			ins.setLabelTargetURIString(stmt.getSubject().getURI());
			ins.setValue(stmt.getLiteral().getValue());

			result.add(ins);
		}

		return result;
	}

	public static Resource getContainerAtDepth(String itemArtifactUriString,
			int depth, Model model) {

		Resource result = null;

		String queryString = "";
		String generationQueryPrefix = " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
				+ " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ " SELECT DISTINCT REPLACEME  \n"
				+ " WHERE { \n"
				+ " REPLACEME a prov:Collection . \n"
				+ " ?item prov:hadMember <"+itemArtifactUriString+ "> . \n";

		String remember = " ?item ";
		String generationQueryMiddle = " ";
		int i = 1;
		depth--;
		
		while (depth > 0) {

			generationQueryMiddle =  " ?m" + i + " prov:hadMember " + remember
					+ " . \n" + generationQueryMiddle;
			remember = " ?m" + i + " ";
			depth--;
			i++;
		}

		queryString = generationQueryPrefix + generationQueryMiddle+ " } \n";
		queryString = queryString.replaceAll("REPLACEME", remember);

		Query generationQuery = QueryFactory.create(queryString);
		QueryExecution generationQueryExec = QueryExecutionFactory.create(
				generationQuery, model);

		try {

			ResultSet resultsGeneration = generationQueryExec.execSelect();

			for (; resultsGeneration.hasNext();) {

				QuerySolution soln = resultsGeneration.nextSolution();

				String varName = remember.trim();
				varName = varName.substring(1);
				result = soln.getResource(varName);
			}

		} finally {

			generationQueryExec.close();
		}
		return result;
	}

	public static Set<Resource> getCollectionMembersAtDepth(
			String collectionArtifactUriString, int depth, Model model) {

		Set<Resource> result = new HashSet<Resource>();

		String queryString = "";
		String generationQueryPrefixStr = " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
				+ " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ " SELECT DISTINCT REPLACEME  \n"
				+ " WHERE { \n"
				+ " ?col a prov:Collection . \n";

		String remember = " ?col ";	
		String generationQueryMiddle=" ";
				
				int i = 1;
		while (depth > 0) {

			generationQueryMiddle =  generationQueryMiddle + remember
					+ "prov:hadMember ?m" + i + " . \n";
			remember = " ?m" + i + " ";
			depth--;
			i++;
		}

		queryString = generationQueryPrefixStr+ generationQueryMiddle + " } \n";

		queryString = queryString.replaceAll("REPLACEME", remember);

		Query generationQuery = QueryFactory.create(queryString);
		QueryExecution generationQueryExec = QueryExecutionFactory.create(
				generationQuery, model);

		try {

			ResultSet resultsGeneration = generationQueryExec.execSelect();

			for (; resultsGeneration.hasNext();) {

				QuerySolution soln = resultsGeneration.nextSolution();
				
				String varName = remember.trim();
				varName = varName.substring(1);
				
				result.add(soln.getResource(varName));
			}

		} finally {

			generationQueryExec.close();
		}
		return result;
	}

	public static Set<Resource> getGenerationsWithRoleAndActivity(
			String activityUriString, String portUriString, Model model) {

		Set<Resource> result = new HashSet<Resource>();


		String generationQueryStr = " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
				+ " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ " SELECT DISTINCT ?entit1  \n"
				+ " WHERE { \n"
				+ " ?entit1 prov:qualifiedGeneration ?gen1 . \n"
				+ " ?gen1 prov:hadRole"
				+ " <"
				+ portUriString
				+ "> . \n"
				+ " ?gen1 prov:activity"
				+ " <"
				+ activityUriString
				+ "> . \n"
				+ " } \n";

		Query generationQuery = QueryFactory.create(generationQueryStr);
		QueryExecution generationQueryExec = QueryExecutionFactory.create(
				generationQuery, model);

		try {

			ResultSet resultsGeneration = generationQueryExec.execSelect();

			for (; resultsGeneration.hasNext();) {

				QuerySolution soln = resultsGeneration.nextSolution();

				result.add(soln.getResource("entit1"));
			}

		} finally {

			generationQueryExec.close();
		}
		return result;
	}

	public static Set<Resource> getUsagesWithRoleAndActivity(
			String activityUriString, String portUriString, Model model) {

		Set<Resource> result = new HashSet<Resource>();

		String generationQueryStr = " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
				+ " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ " SELECT DISTINCT ?entit1  \n"
				+ " WHERE { \n"
				+ " <"
				+ activityUriString
				+ "> prov:qualifiedUsage ?usg1 . \n"
			    + " ?usg1 prov:entity ?entit1 . \n"
				+ " ?usg1 prov:hadRole"
				+ " <"
				+ portUriString
				+ "> . \n"
				+ " } \n";

		Query generationQuery = QueryFactory.create(generationQueryStr);
		QueryExecution generationQueryExec = QueryExecutionFactory.create(
				generationQuery, model);

		try {

			ResultSet resultsGeneration = generationQueryExec.execSelect();

			for (; resultsGeneration.hasNext();) {

				QuerySolution soln = resultsGeneration.nextSolution();

				result.add(soln.getResource("entit1"));
			}

		} finally {

			generationQueryExec.close();
		}
		return result;
	}
}

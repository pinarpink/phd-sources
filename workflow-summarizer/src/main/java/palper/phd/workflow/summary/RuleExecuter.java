/**
 * 
 */
package palper.phd.workflow.summary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.configuration.tree.ConfigurationNode;

import palper.phd.wfdesc.visualize.graphml.GraphmlWriter;
import palper.phd.workflow.rewrite.RuleConfigurationReader;
import palper.phd.workflow.wfdesc.WorkflowStatisticsCollector;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileUtils;

/**
 * @author pinarpink
 * 
 */
public class RuleExecuter {

	static Map<String, String> originalToClone = new HashMap<String, String>();

	static Map<String, String> cloneToOriginal = new HashMap<String, String>();


	private static void exhaustQuery(InfModel wfdescModel,
			String queryFileName, String primitive) throws IOException {

		String queryStr = readFromFile(new File(queryFileName));
		Query query = QueryFactory.create(queryStr);

		boolean queryExhausted = false;
		while (!queryExhausted) {
			QueryExecution qexec = QueryExecutionFactory.create(query,
					wfdescModel);
			try {

				ResultSet results = qexec.execSelect();
				if (results.hasNext()) {
					System.out.println("\n PATTERN MATCH for QUERY :"
							+ queryFileName + " \n");

					QuerySolution soln = results.nextSolution();
					RuleConstants.getHandlers().get(primitive)
							.handleSummaryRuleSparql(soln, wfdescModel);

				} else {
					queryExhausted = true;
				}

			} finally {
				qexec.close();
			}
		}

	}

	private static String readFromFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		BufferedReader in = new BufferedReader(new InputStreamReader(fis,
				"UTF-8"));
		String queryStr = "";
		String line = "";

		while ((line = in.readLine()) != null) {
			queryStr = queryStr + line;
		}

		fis.close();
		return queryStr;
	}

	public static void main(String[] args) {
		RuleConfigurationReader reader = new RuleConfigurationReader();

		 
		List<ConfigurationNode> children = reader.getConfigurations();

		Map<String, String> map = new HashMap<String, String>();
		for (ConfigurationNode child : children) {
			map.put((String) child.getAttributes("queryFilePath").get(0)
					.getValue(),
					(String) child.getAttributes("primitive").get(0).getValue());
		}
		Iterator iterator = map.entrySet().iterator();

		File dataset_dir = new File(args[0]);

		File[] file_array = dataset_dir.listFiles();
		for (int i = 0; i < file_array.length; i++) {
			String wfdescFileName = file_array[i].getPath();

			if (wfdescFileName.endsWith("-motifs.wfdesc.ttl")) {
				try {
				System.out.println("Summarizing:" + wfdescFileName);
				File wfdescfile = new File(wfdescFileName);
				Model baseModel = ModelFactory
						.createDefaultModel(ReificationStyle.Minimal);

				InputStream is = new FileInputStream(wfdescFileName);
				String lang = FileUtils.guessLang(wfdescFileName);

				baseModel.read(is, null, lang);

				Model motifsModel = ModelFactory
						.createDefaultModel(ReificationStyle.Minimal);

				motifsModel
						.read("file:///Users/pinarpink/Desktop/SummarizationRules/ontology/motifs_palper.owl",
								"http://purl.org/wf4ever/motifs.owl#",
								"RDF/XML");
				InfModel wfdescModel = ModelFactory.createRDFSModel(
						motifsModel, baseModel);
				String destFilename = wfdescFileName.replaceFirst("\\..*",
						"") + "-"+reader.getConfigName()+".wfdesc.ttl";

				String summarizationStatisticsFileName = destFilename
						.replaceFirst("\\..*", "")
						+ "stats.txt";
				File statsFile = new File(summarizationStatisticsFileName);
				Writer statWriter = new FileWriter(statsFile);

				WorkflowStatisticsCollector stats = new WorkflowStatisticsCollector(
						wfdescModel.getRawModel());
				statWriter.write(stats.toString());
				statWriter.write("*******************************\n");
				
				while (iterator.hasNext()) {
			
					Map.Entry mapEntry = (Map.Entry) iterator.next();

					String queryFileName = (String) mapEntry.getKey();
					String primitive = (String) mapEntry.getValue();
					exhaustQuery(wfdescModel, queryFileName, primitive);

	
					// // Iterator it = cloneToOriginal.entrySet().iterator();
					// // for (String key : cloneToOriginal.keySet()) {
					// // System.out.println(key + " = >" +
					// cloneToOriginal.get(key));
					// // }
					// }
				

				}//end while
				
				//there is no rewind method for the iterator hence we reassign
				iterator = map.entrySet().iterator();

				stats = new WorkflowStatisticsCollector(
						wfdescModel.getRawModel());
				statWriter.write(stats.toString());
				statWriter.close();

				String graphMlFileName = file_array[i].getName();
				graphMlFileName = graphMlFileName.replaceFirst("\\..*", "")
						+"-"+reader.getConfigName()+ ".graphml";
				File graphMlFile = new File(file_array[i].getParentFile(),
						graphMlFileName);
				Writer fw = new FileWriter(graphMlFile);
				GraphmlWriter gw = new GraphmlWriter(fw,
						wfdescModel.getRawModel());

				gw.write();

//				MappingTableToWfdesc.decoratePortsWithMappings(wfdescModel,
//						cloneToOriginal);

				File wfdescSummarized = new File(destFilename);

				OutputStream oss = new FileOutputStream(wfdescSummarized);

				wfdescModel.getRawModel().write(oss, "TURTLE", null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//end if
		}//end for

	}

	public static Map<String, String> getOriginalToClone() {
		return originalToClone;
	}

	public static void setOriginalToClone(Map<String, String> originalToClone) {
		RuleExecuter.originalToClone = originalToClone;
	}

	public static Map<String, String> getCloneToOriginal() {
		return cloneToOriginal;
	}

	public static void setCloneToOriginal(Map<String, String> cloneToOriginal) {
		RuleExecuter.cloneToOriginal = cloneToOriginal;
	}

}

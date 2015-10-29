/**
 * 
 */
package palper.phd.workflow.summary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.configuration.tree.ConfigurationNode;

import palper.phd.wfdesc.visualize.graphml.GraphmlWriter;
import palper.phd.workflow.db.OriginalWfdescModelsRepo;
import palper.phd.workflow.db.WorkflowInfo;
import palper.phd.workflow.rewrite.QueryTemplates;
import palper.phd.workflow.rewrite.RuleConfigurationReader;
import palper.phd.workflow.rewrite.SimpleRulePair;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileUtils;

/**
 * @author pinarpink
 * 
 */
public class RuleExecuter {

//	static Map<String, String> originalToClone = new HashMap<String, String>();
//	static Map<String, String> cloneToOriginal = new HashMap<String, String>();

	static List<SimpleRulePair> ruleList = new ArrayList<SimpleRulePair>();

	private static boolean exhaustQuery(InfModel wfdescModel,
			String queryString, String primitive) throws IOException {

		// String queryStr = readFromFile(new File(queryFileName));
		Query query = QueryFactory.create(queryString);
		// System.out.println(queryString);

		boolean anyChanges = false;
		boolean queryExhausted = false;

		do {

			QueryExecution qexec = QueryExecutionFactory.create(query,
					wfdescModel);
			try {

				ResultSet results = qexec.execSelect();
				boolean actedUpon = false;
				while (results.hasNext()) {
					System.out.println("\n PATTERN MATCH for QUERY :"
							+ queryString + " \n");

					QuerySolution soln = results.nextSolution();
					
					boolean res = RuleConstants.getHandlers().get(primitive).handleSummaryRuleSparql(soln, wfdescModel);
					
					if (res) {

						actedUpon = true;
						anyChanges = true;
						break;
					}

				}

				queryExhausted = !actedUpon;

			} finally {
				qexec.close();
			}
		} while (!queryExhausted);

		return anyChanges;
	}


  public static void main(String[] args) {
		RuleConfigurationReader reader = new RuleConfigurationReader( "rule-config-collapse-nostrategy.xml");

		List<ConfigurationNode> children = reader.getConfigurations();
		Set<String> actionableMotifSet = new HashSet<String>();

		for (ConfigurationNode child : children) {
			ruleList.add(new SimpleRulePair((String) child
					.getAttributes("motif").get(0).getValue(), (String) child
					.getAttributes("primitive").get(0).getValue()));
			actionableMotifSet.add((String) child.getAttributes("motif").get(0)
					.getValue());
		}

		File dataset_dir = new File(args[0]);

		File[] file_array = dataset_dir.listFiles();
		for (int i = 0; i < file_array.length; i++) {
			String wfdescFileName = file_array[i].getPath();

			if (wfdescFileName.endsWith(".wfdesc.ttl")) {
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
					InfModel wfdescModel = ModelFactory.createRDFSModel(motifsModel, baseModel);
					String destFilename = wfdescFileName.replaceFirst("\\..*","") + "-" + reader.getConfigName() + ".wfdesc.ttl";

					String summarizationStatisticsFileName = destFilename.replaceFirst("\\..*", "") + "stats.txt";
					File statsFile = new File(summarizationStatisticsFileName);
					Writer statWriter = new FileWriter(statsFile);

//					WfdescStatistics stats = new WfdescStatistics(wfdescModel.getRawModel());
//					statWriter.write(stats.toString());
//					statWriter.write("*******************************\n");
//
					Resource wfResource = WfDescRdfUtils.getWorkflowResource(wfdescModel);

					Set<Resource> processorList = WfDescRdfUtils.getProcessors(wfdescModel, wfResource);
					Set<Resource> processorsWithActionableMotifs = WfDescRdfUtils.getMotifAnnotatedProcessors(wfdescModel,wfResource, actionableMotifSet);
					processorList.removeAll(processorsWithActionableMotifs);

					WorkflowInfo wi = new WorkflowInfo();
//					wi.setStatistics(stats);
//					wi.setWfdescModel(wfdescModel);
//					
					StringWriter strwrtr = new StringWriter();
					
					wfdescModel.getRawModel().write(strwrtr, "TURTLE", null);
//					wi.setWfdescAsString(strwrtr.toString());
//					
					List<String> unactionableProcessors = new ArrayList<String>();
					for (Resource res : processorList) {
						unactionableProcessors.add(res.getURI());
					}
					wi.setUnactionableProcessUris(unactionableProcessors);

					OriginalWfdescModelsRepo.getInstance()
							.getWorkflowDatabase().put(wfResource.getURI(), wi);

					boolean changesMade = false;
					do {
						changesMade = false;
						for (SimpleRulePair rulePair : ruleList) {

							
							boolean changesMadePerRule = exhaustQuery(wfdescModel,
									QueryTemplates
									.getParameterizedQuery(rulePair
											.getMotifURI(), wfResource.getURI()),
							rulePair.getPrimitive());
							if (changesMadePerRule) {
								
								changesMade = true;
							}
						}

					} while (changesMade);// end for

//					stats = new WfdescStatistics(
//							wfdescModel.getRawModel());
//					statWriter.write(stats.toString());
//					statWriter.close();

					String graphMlFileName = file_array[i].getName();
					graphMlFileName = graphMlFileName.replaceFirst("\\..*", "")
							+ "-" + reader.getConfigName() + ".graphml";
					File graphMlFile = new File(file_array[i].getParentFile(),
							graphMlFileName);
					Writer fw = new FileWriter(graphMlFile);
					GraphmlWriter gw = new GraphmlWriter(fw,
							wfdescModel.getRawModel());

					gw.write();

					// MappingTableToWfdesc.decoratePortsWithMappings(wfdescModel,
					// cloneToOriginal);

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
			}// end if
		}// end for

	}

//	public static Map<String, String> getOriginalToClone() {
//		return originalToClone;
//	}
//
//	public static void setOriginalToClone(Map<String, String> originalToClone) {
//		RuleExecuter.originalToClone = originalToClone;
//	}
//
//	public static Map<String, String> getCloneToOriginal() {
//		return cloneToOriginal;
//	}
//
//	public static void setCloneToOriginal(Map<String, String> cloneToOriginal) {
//		RuleExecuter.cloneToOriginal = cloneToOriginal;
//	}

}

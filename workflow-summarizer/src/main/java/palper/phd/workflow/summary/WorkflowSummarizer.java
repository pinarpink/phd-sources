/**
 * 
 */
package palper.phd.workflow.summary;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.tree.ConfigurationNode;

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
public class WorkflowSummarizer {

  private Model summarizedWfdesc;
  
  static List<SimpleRulePair> ruleList = new ArrayList<SimpleRulePair>();

  public WorkflowSummarizer(String ruleConfigFileName, String tobeSummarizedWfdescPath) {
    super();

    RuleConfigurationReader reader = new RuleConfigurationReader(ruleConfigFileName);

    List<ConfigurationNode> children = reader.getConfigurations();
    Set<String> actionableMotifSet = new HashSet<String>();

    for (ConfigurationNode child : children) {
      ruleList.add(new SimpleRulePair((String) child.getAttributes("motif").get(0).getValue(),
          (String) child.getAttributes("primitive").get(0).getValue()));
      actionableMotifSet.add((String) child.getAttributes("motif").get(0).getValue());
    }


    try {
      System.out.println("Summarizing:" + tobeSummarizedWfdescPath);

      Model baseModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);

      InputStream is = new FileInputStream(tobeSummarizedWfdescPath);
      String lang = FileUtils.guessLang(tobeSummarizedWfdescPath);

      baseModel.read(is, null, lang);

      Model motifsModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);


      motifsModel.read("file:///Users/pinarpink/Desktop/MOTIF-ONTOLOGY/motif-ontology1.1.owl",
          "http://purl.org/net/wf-motifs#", "RDF/XML");
      motifsModel.read(
          "file:///Users/pinarpink/Desktop/MOTIF-ONTOLOGY/ExtensionsToMotifOntology.owl",
          "http://www.semanticweb.org/pinarpink/ontologies/2013/10/labelingwf#", "RDF/XML");


      InfModel wfdescModel = ModelFactory.createRDFSModel(motifsModel, baseModel);

      Resource wfResource = WfDescRdfUtils.getWorkflowResource(wfdescModel);

      Set<Resource> processorList = WfDescRdfUtils.getProcessors(wfdescModel, wfResource);
      Set<Resource> processorsWithActionableMotifs =
          WfDescRdfUtils.getMotifAnnotatedProcessors(wfdescModel, wfResource, actionableMotifSet);
      processorList.removeAll(processorsWithActionableMotifs);

      WorkflowInfo wi = new WorkflowInfo();

      StringWriter strwrtr = new StringWriter();

      wfdescModel.getRawModel().write(strwrtr, "TURTLE", null);

      List<String> unactionableProcessors = new ArrayList<String>();
      for (Resource res : processorList) {
        unactionableProcessors.add(res.getURI());
      }
      wi.setUnactionableProcessUris(unactionableProcessors);

      OriginalWfdescModelsRepo.getInstance().getWorkflowDatabase().put(wfResource.getURI(), wi);

      boolean changesMade = false;
      do {
        changesMade = false;
        for (SimpleRulePair rulePair : ruleList) {


          boolean changesMadePerRule =
              exhaustQuery(wfdescModel,
                  QueryTemplates.getParameterizedQuery(rulePair.getMotifURI(), wfResource.getURI()),
                  rulePair.getPrimitive());
          if (changesMadePerRule) {

            changesMade = true;
          }
        }

      } while (changesMade);// end for


       CloneMapToWfdesc.decoratePortsWithMappings(wfdescModel, OriginalWfdescModelsRepo.getInstance().getWorkflowDatabase().get(wfResource.getURI()).getCloneToOriginal());


      summarizedWfdesc = wfdescModel.getRawModel();

    } catch (IOException e) {
    
      e.printStackTrace();
    }


  }

  private static boolean exhaustQuery(InfModel wfdescModel, String queryString, String primitive)
      throws IOException {

  
    Query query = QueryFactory.create(queryString);
  
    boolean anyChanges = false;
    boolean queryExhausted = false;

    do {

      QueryExecution qexec = QueryExecutionFactory.create(query, wfdescModel);
      try {

        ResultSet results = qexec.execSelect();
        boolean actedUpon = false;
        while (results.hasNext()) {
          System.out.println("\n PATTERN MATCH for QUERY :" + queryString + " \n");

          QuerySolution soln = results.nextSolution();

          boolean res =
              RuleConstants.getHandlers().get(primitive).handleSummaryRuleSparql(soln, wfdescModel);

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


  public Model getSummarizedWfdescModel() {
    return summarizedWfdesc;

  }



}

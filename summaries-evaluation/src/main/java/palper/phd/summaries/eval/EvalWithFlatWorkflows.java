/**
 * 
 */
package palper.phd.summaries.eval;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import palper.phd.wfdesc.visualize.graphml.GraphmlWriter;
import palper.phd.workflow.flattener.BookmarkEliminator;
import palper.phd.workflow.summary.WorkflowSummarizer;
import palper.phd.workflow.wfdesc.PostSummaryStatistics;
import palper.phd.workflow.wfdesc.PreSummaryStatistics;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileUtils;

/**
 * @author pinarpink
 * 
 */
public class EvalWithFlatWorkflows {


  /**
   * @param args
   */
  public static void main(String[] args) {

    // "/Users/pinarpink/Desktop/SummarizationRules/dataset/FLAT"
    // collapse-nostrategy
    // elim-shims
    File dataset_dir = new File(args[0]);
    String strategy = args[1];
    File[] file_array = dataset_dir.listFiles();
    StatisticsMap summaryStats = new StatisticsMap();

    for (int i = 0; i < file_array.length; i++) {

      String wfdescShortName = file_array[i].getName();
      String wfdescFileName = file_array[i].getPath();

      if (wfdescFileName.endsWith(".wfdesc.ttl")) {
        try {


          Model baseModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);

          InputStream is = new FileInputStream(wfdescFileName);
          String lang = FileUtils.guessLang(wfdescFileName);

          baseModel.read(is, null, lang);

          Resource wfResource = WfDescRdfUtils.getWorkflowResource(baseModel);
          boolean isnested = WfDescRdfUtils.isNestedWorkflow(wfResource, baseModel);

          if (!isnested) {

            BookmarkEliminator be = new BookmarkEliminator(baseModel);


            PreSummaryStatistics preSummaryStats = new PreSummaryStatistics(baseModel);
            System.out.println(preSummaryStats.toString());

            summaryStats.getPreSummaryMap().put(wfdescShortName, preSummaryStats);


            String graphMlFileName = wfdescFileName;
            graphMlFileName = graphMlFileName.replaceFirst("\\..*", "") + "-Unbookmarked.graphml";
            Writer fw = new FileWriter(new File(graphMlFileName));
            GraphmlWriter gw = new GraphmlWriter(fw, be.getBookmarkEliminatedWfdesc());
            gw.write();


            String UnbookmarkedWfdescFieName =
                wfdescFileName.replaceFirst("\\..*", "") + "-unbookmarked.wfdesc.ttl";
            OutputStream oss = new FileOutputStream(UnbookmarkedWfdescFieName);
            be.getBookmarkEliminatedWfdesc().write(oss, "TURTLE", null);



            /*
             * 
             * THIS IS WHERE THE MAGIC HAPPENS
             */
            Model result = summarize(UnbookmarkedWfdescFieName, strategy);



            PostSummaryStatistics postSummaryStats = new PostSummaryStatistics(result);
            System.out.println(postSummaryStats.toString());
            summaryStats.getPostSummaryMap().put(wfdescShortName, postSummaryStats);


            /*
             * 
             */


          }
        } catch (ParserConfigurationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (TransformerException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }// if

    }// for

    try {
      File resultStatsFile =
          new File(dataset_dir.getAbsolutePath() + "/By_"+strategy+"_Stats.csv");
      FileWriter filewr = new FileWriter(resultStatsFile);
      filewr.write(summaryStats.getMapAsCsv());
      filewr.close();


    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private static Model summarize(String sourceWfdescFileName, String strategy) throws IOException,
      ParserConfigurationException, TransformerException {


    WorkflowSummarizer summarizer1 =
        new WorkflowSummarizer("rule-config-" + strategy + ".xml", sourceWfdescFileName);

    Model summarizedModel = summarizer1.getSummarizedWfdescModel();


    String summarizedWfGraphMlFileName =
        sourceWfdescFileName.replaceFirst("\\..*", "") + "-" + strategy + ".graphml";

    Writer fw2 = new FileWriter(new File(summarizedWfGraphMlFileName));
    GraphmlWriter gw2 = new GraphmlWriter(fw2, summarizedModel);
    gw2.write();


    String summarizedWfdescFileName =
        sourceWfdescFileName.replaceFirst("\\..*", "") + "-" + strategy + ".wfdesc.ttl";
    File wfdescSummarized = new File(summarizedWfdescFileName);
    OutputStream oss2 = new FileOutputStream(wfdescSummarized);
    summarizedModel.write(oss2, "TURTLE", null);


    return summarizedModel;

  }
}

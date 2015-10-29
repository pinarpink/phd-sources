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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import palper.phd.wfdesc.visualize.graphml.GraphmlWriter;
import palper.phd.workflow.flattener.BookmarkEliminator;
import palper.phd.workflow.flattener.Flattener;
import palper.phd.workflow.wfdesc.PostSummaryStatistics;
import palper.phd.workflow.wfdesc.PreSummaryStatistics;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;
import palper.phd.workflow.wfdesc.WfdescStatistics;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileUtils;
import palper.phd.workflow.summary.*;

/**
 * @author pinarpink
 * 
 */
public class EvalWithNestedWorkflows {

  /**
   * @param args
   */
  public static void main(String[] args) {

    File dataset_dir = new File("/Users/pinarpink/Desktop/SummarizationRules/dataset/NESTED/");

    File[] file_array = dataset_dir.listFiles();

    StatisticsMap statsMap = new StatisticsMap();


    for (int i = 0; i < file_array.length; i++) {

      String wfdescFileName = file_array[i].getPath();
      String wfdescShortName = file_array[i].getName();

      if (wfdescFileName.endsWith(".wfdesc.ttl")) {
        
        try {


          Model baseModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
          InputStream is = new FileInputStream(wfdescFileName);
          String lang = FileUtils.guessLang(wfdescFileName);
          baseModel.read(is, null, lang);

          boolean isnested =
              WfDescRdfUtils.isNestedWorkflow(WfDescRdfUtils.getWorkflowResource(baseModel),
                  baseModel);

          if (isnested) {

            System.out.println("Unnesting:" + wfdescFileName);

            Flattener ft = new Flattener(baseModel);
            // Set<String> toplevelVisiblePorts = ft.getToplevelVisibleOutPorts();

            BookmarkEliminator be = new BookmarkEliminator(ft.getFlattenedWfdesc());
            // Set<String> manuallyBookmarkedProts = be.getBookmarkPortUris();



            String graphMlFileName = wfdescFileName;
            graphMlFileName =
                graphMlFileName.replaceFirst("\\..*", "") + "-UnnestedUnbookmarked.graphml";

            Writer fw = new FileWriter(new File(graphMlFileName));
            GraphmlWriter gw = new GraphmlWriter(fw, be.getBookmarkEliminatedWfdesc());
            gw.write();


            String unnestedUnbookmarkedWfdescFieName =
                wfdescFileName.replaceFirst("\\..*", "") + "-unnested-unbookmarked.wfdesc.ttl";
            // File wfdescFlattened = new File(unnestedUnbookmarkedWfdescFieName);

            OutputStream oss = new FileOutputStream(unnestedUnbookmarkedWfdescFieName);
            be.getBookmarkEliminatedWfdesc().write(oss, "TURTLE", null);


            PreSummaryStatistics preSummaryStats =
                new PreSummaryStatistics(be.getBookmarkEliminatedWfdesc());
            System.out.println(preSummaryStats.toString());
            statsMap.getPreSummaryMap().put(wfdescShortName, preSummaryStats);


            /*
             * 
             * THIS IS WHERE THE MAGIC HAPPENS
             */

            WorkflowSummarizer summarizer1 =
                new WorkflowSummarizer("rule-config-collapse-nostrategy.xml",
                    unnestedUnbookmarkedWfdescFieName);

            Model summarizedModel = summarizer1.getSummarizedWfdescModel();


            String summarizedWfGraphMlFileName = graphMlFileName;
            summarizedWfGraphMlFileName =
                graphMlFileName.replaceFirst("\\..*", "") + "-" + "collapse-nostrategy.graphml";

            Writer fw2 = new FileWriter(new File(summarizedWfGraphMlFileName));
            GraphmlWriter gw2 = new GraphmlWriter(fw2, summarizedModel);
            gw2.write();


            String summarizedWfdescFileName =
                unnestedUnbookmarkedWfdescFieName.replaceFirst("\\..*", "")
                    + "-collapse-nostrategy.wfdesc.ttl";
            File wfdescSummarized = new File(summarizedWfdescFileName);
            OutputStream oss2 = new FileOutputStream(wfdescSummarized);
            summarizedModel.write(oss2, "TURTLE", null);

            PostSummaryStatistics postSummaryStats = new PostSummaryStatistics(summarizedModel);
            System.out.println(postSummaryStats.toString());
            statsMap.getPostSummaryMap().put(wfdescShortName, postSummaryStats);



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
      File resultStatsFile = new File(dataset_dir.getAbsolutePath() + "/SummarizationStats.csv");

      FileWriter filewr = new FileWriter(resultStatsFile);

      filewr.write(statsMap.getMapAsCsv());
      filewr.close();

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}

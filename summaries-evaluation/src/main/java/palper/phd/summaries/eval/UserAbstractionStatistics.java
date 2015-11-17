/**
 * 
 */
package palper.phd.summaries.eval;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import palper.phd.workflow.wfdesc.PreSummaryStatistics;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileUtils;

/**
 * @author pinarpink
 * 
 */
public class UserAbstractionStatistics {

  /**
   * 
   */
  public UserAbstractionStatistics() {
    // TODO Auto-generated constructor stub
  }

  public static void main(String[] args) {


    File dataset_dir = new File(args[0]);
    File[] file_array = dataset_dir.listFiles();

    String result = "";

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


            PreSummaryStatistics preSummaryStats = new PreSummaryStatistics(baseModel);
            result = result +wfdescShortName +";"+preSummaryStats.toCsvString()+"\n";

          }
        }


        catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }// if

    }// for



    try {
      File resultStatsFile = new File(dataset_dir.getAbsolutePath() + "/UserAbsStats.csv");

      FileWriter filewr = new FileWriter(resultStatsFile);

      filewr.write(result);
      filewr.close();

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}

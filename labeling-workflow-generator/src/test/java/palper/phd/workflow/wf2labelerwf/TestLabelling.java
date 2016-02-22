package palper.phd.workflow.wf2labelerwf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import palper.phd.labeling.operator.LabelWfTopoSorter;
import palper.phd.labeling.operator.LabelingOperHandler;
import palper.phd.labeling.operator.LabellingExecutionConfig;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;

public class TestLabelling {

  @Test
  public void test() {

    try {

      int counter = 1;

      while (counter < 12) {
        URL current = this.getClass().getResource(".");
        String sciWfdescFileName = current.getPath() + "2920_bookmarks_elimntd.wfdesc.ttl";
        String provFileName =
            current.getPath() + "Provenance-bundles" + File.separator + "2920-bm-REPLACEMEinputs"
                + File.separator + "workflowrun.prov.ttl";

        Model provModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);

        String currentProvFileName = provFileName.replaceAll("REPLACEME", String.valueOf(counter));
        provModel.read(
            new FileInputStream(currentProvFileName),
            null, "TURTLE");



        LabellingExecutionConfig.initialize(sciWfdescFileName, currentProvFileName);

        File sciWfdescFile = new File(sciWfdescFileName);
        Model sciWfdesc = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
        InputStream is2 = new FileInputStream(sciWfdescFile);
        sciWfdesc.read(is2, null, "TURTLE");


        Generator gen = new Generator(sciWfdescFile);

        String result = gen.getLabelingWorkflowAsString();
        System.out.println("-------------");
        System.out.println(result);
        System.out.println("-------------");


        Model labelingWfdesc = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
        InputStream is = new ByteArrayInputStream(result.getBytes());
        labelingWfdesc.read(is, null, "TURTLE");

        LabelWfTopoSorter sorter = new LabelWfTopoSorter();
        sorter.sort(labelingWfdesc);
        Map<String, Integer> orders = sorter.getActivityOrders();
        System.out.println(orders);


        LabelingOperHandler handler = new LabelingOperHandler();

        int max = sorter.getMaxOrder();

        for (int i = 0; i <= max; i++) {
          List<String> activitiesToRun = sorter.getActivitiesByOrder().get(i);

          for (String activity : activitiesToRun) {
            String labelingSpecJson = WfDescRdfUtils.getScriptValue(activity, labelingWfdesc);
            handler.execute(labelingSpecJson);
          }
        }


        File labelFile = new File(currentProvFileName.replaceFirst("\\..*", "") + "-Labels.ttl");

        OutputStream oss;
        oss = new FileOutputStream(labelFile);
        LabellingExecutionConfig.getInstance().getLabelingResultModel().write(oss, "TURTLE", null);

        System.out.println("Labels written to" + labelFile.getAbsolutePath());
        
        counter++;
      }
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();

      Assert.fail();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      Assert.fail();
    }

  }

}

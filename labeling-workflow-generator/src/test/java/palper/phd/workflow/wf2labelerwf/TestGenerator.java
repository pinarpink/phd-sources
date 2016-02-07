package palper.phd.workflow.wf2labelerwf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.deri.iris.EvaluationException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileUtils;

import palper.phd.labeling.datalog.DLVClient;
import palper.phd.labeling.datalog.Wfdesc2Datalog;
import palper.phd.labeling.operator.LabelWfTopoSorter;
import palper.phd.workflow.wf2labelerwf.SciWorflowScavenger;
import sun.nio.cs.StandardCharsets;

/**
 * 
 */

/**
 * @author pinarpink
 * 
 */
public class TestGenerator {

  @Ignore
  @Test
  public void TestGenerator() {

    String workflowPath =
        "/Users/pinarpink/Desktop/SummarizationRules/dataset/2920_portname_fixed.wfdesc.ttl";

    File workflowFile = new File(workflowPath);
    Generator gen = new Generator(workflowFile);
    try {
      gen.getLabelingWorkflowAsString();
    } catch (Exception e) {
      Assert.fail();
      e.printStackTrace();
    }


  }

  @Test
  public void TestGeneratorAstroWorkflow() {
  
    try {
      
      String workflowPath =
          "/Users/pinarpink/Desktop/Labeling_Workflows/2920_bookmarks_elimntd.wfdesc.ttl";

      File workflowFile = new File(workflowPath);



      Model sciWfdesc = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
      InputStream is2 = new FileInputStream(workflowFile);
      sciWfdesc.read(is2, null, "TURTLE");

      Map<String, String> nspm1 = sciWfdesc.getNsPrefixMap();

      
      Generator gen = new Generator(workflowFile);
      
      String result = gen.getLabelingWorkflowAsString();
      System.out.println("-------------");

      System.out.println(result);
      System.out.println("-------------");



      String labelingWorkflowFilePath =
          workflowPath.replaceFirst("\\..*", "") + "-labeling-wfdesc.ttl";

      File file = new File(labelingWorkflowFilePath);
      FileOutputStream fop = new FileOutputStream(file);


      // get the content in bytes
      byte[] contentInBytes = result.getBytes();

      fop.write(contentInBytes);
      fop.flush();
      fop.close();

      System.out.println("Done");

      Model labelingWfdesc = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
      InputStream is = new FileInputStream(file);
      labelingWfdesc.read(is, null, "TURTLE");
      Map<String, String> nspm = labelingWfdesc.getNsPrefixMap();

      LabelWfTopoSorter sorter = new LabelWfTopoSorter();
      Map<String, Integer> orders = sorter.sort(labelingWfdesc);
      System.out.println(orders);

    } catch (Exception e) {

      e.printStackTrace();
      Assert.fail();
    }
  }

}

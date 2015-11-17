/**
 * 
 */
package palper.phd.labeling.datalog;

import java.io.File;
import java.io.IOException;

import org.deri.iris.EvaluationException;
import org.junit.Test;

/**
 * @author pinarpink
 *
 */
public class TestLabelStatus {

  @Test
  public void test() {
      DLVClient cli;
      try {
          cli = new DLVClient("prog-input-2");

          String workflowPath = "/Users/pinarpink/Desktop/Labeling_Workflows/dataset/2920_portname_fixed.wfdesc.ttl";

          File workflowFile = new File(workflowPath);
          Wfdesc2Datalog ext = new Wfdesc2Datalog(workflowFile);
          // ext.writeEdbToFile();
          
          //System.out.println(ext.getEdbAsDLVString());
          cli.addWorkflowDescription(ext.getEdbAsDLVString());
         // "context(ctxDynamic,w1,win1,0)."
          
         
         cli.addContextDescription( "\n context(galaxyName, \"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/\", \"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/in/list_cig_name\", 0). \n");
         cli.invokeDlv(null, null);

         System.out.println(cli.getWorkflowIDB().toString());
      } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      } catch (EvaluationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      }

  }

}

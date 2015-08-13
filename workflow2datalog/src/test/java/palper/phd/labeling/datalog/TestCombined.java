/**
 * 
 */
package palper.phd.labeling.datalog;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.deri.iris.EvaluationException;
import org.junit.Test;

/**
 * @author pinarpink
 * 
 */
public class TestCombined {

	@Test
	public void test() {
	
		try {
			DLVClient cli = new DLVClient();

			String workflowPath = "/Users/pinarpink/Desktop/SummarizationRules/dataset/2920.wfdesc.ttl";

			File workflowFile = new File(workflowPath);
			WorkflowEdbGenerator ext = new WorkflowEdbGenerator(workflowFile);
			// ext.writeEdbToFile();
			
			System.out.println(ext.getEdbAsDLVString());
			cli.addWorkflowDescription(ext.getEdbAsDLVString());
			cli.invokeDlv(null, null);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

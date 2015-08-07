/**
 * 
 */
package palper.phd.labeling.datalog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.deri.iris.EvaluationException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author pinarpink
 * 
 */
public class TestEdbGenerator {

	//@Ignore
	@Test
	public void test() {

		// /Users/pinarpink/Desktop/SummarizationRules/dataset

		String dataSetDirStr = "/Users/pinarpink/Desktop/SummarizationRules/dataset";
		String ruleFileStr = "/Users/pinarpink/Work/workspaces/eclipse-helios-new/workflow2datalog/src/main/resources/palper/phd/labeling/datalog/rules_only.datalog";

		File ruleFile = new File(ruleFileStr);

		File dataset_dir = new File(dataSetDirStr);
		File[] file_array = dataset_dir.listFiles();
		for (int i = 0; i < file_array.length; i++) {
			if (file_array[i].getName().endsWith(".wfdesc.ttl")) {
				String t2FlowAsumedName = file_array[i].getName();
				t2FlowAsumedName = t2FlowAsumedName.replaceFirst("\\..*", "")
						+ ".t2flow";

				File t2FlowFile = new File(file_array[i].getParent()
						+ File.separator + t2FlowAsumedName);

				try {
					WorkflowEdbGenerator ext;
					ext = new WorkflowEdbGenerator(file_array[i]);

					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (EvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

}

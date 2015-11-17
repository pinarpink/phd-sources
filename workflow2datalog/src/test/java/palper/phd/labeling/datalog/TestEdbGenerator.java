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

		 
	  String dataSetDirStr = "/Users/pinarpink/Desktop/Labeling_Workflows/dataset";
		//String dataSetDirStr = "/Users/pinarpink/Desktop/SummarizationRules/dataset";

		File dataset_dir = new File(dataSetDirStr);
		File[] file_array = dataset_dir.listFiles();
		for (int i = 0; i < file_array.length; i++) {
			if (file_array[i].getName().endsWith(".wfdesc.ttl")) {
				try {
					Wfdesc2Datalog ext;
					ext = new Wfdesc2Datalog(file_array[i]);
					File res = ext.writeEdbToDlvFile();
					System.out.println("Written to:" + res);
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

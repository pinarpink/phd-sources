/**
 * 
 */
package palper.phd.workflow.wfdesc.extender;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/**
 * @author pinarpink
 *
 */
public class TestExtender {

	@Test
	public void test() {
		
		///Users/pinarpink/Desktop/SummarizationRules/dataset
		
		String dataSetDirStr = "/Users/pinarpink/Desktop/SummarizationRules/dataset";
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
					WfdescExtender ext;
					ext = new WfdescExtender(file_array[i], t2FlowFile);
					ext.write();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

}

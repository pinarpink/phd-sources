package palper.phd.workflow.wf2labelerwf;
import java.io.File;
import java.io.IOException;

import org.deri.iris.EvaluationException;
import org.junit.Test;

import palper.phd.labeling.datalog.DLVClient;
import palper.phd.labeling.datalog.Wfdesc2Datalog;
import palper.phd.workflow.wf2labelerwf.SciWorflowScavenger;

/**
 * 
 */

/**
 * @author pinarpink
 *
 */
public class TestGenerator {
@Test
public void TestGenerator(){
	
	String workflowPath = "/Users/pinarpink/Desktop/SummarizationRules/dataset/2920_portname_fixed.wfdesc.ttl";

	File workflowFile = new File(workflowPath);

	Generator gen = new Generator(workflowFile);
	gen.run();
	
	
}



}

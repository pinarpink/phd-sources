import org.junit.Test;

import palper.phd.workflow.wf2labelerwf.Generator;

/**
 * 
 */

/**
 * @author pinarpink
 *
 */
public class TestWorkflowGenerator {
@Test
public void TestGenerator(){
	Generator gen = new  Generator();
	gen.testToBuildLabelingWf();
}

}

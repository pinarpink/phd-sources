/**
 * 
 */
package palper.phd.workflow.db;

import java.util.HashMap;
import java.util.Map;




/**
 * @author pinarpink
 * 
 */
public class OriginalWfdescModelsRepo {

	private static OriginalWfdescModelsRepo instance = null;

	private Map<String, WorkflowInfo> workflowDatabase;

	public Map<String, WorkflowInfo> getWorkflowDatabase() {
		return workflowDatabase;
	}

	public void setWorkflowDatabase(Map<String, WorkflowInfo> workflowDatabase) {
		this.workflowDatabase = workflowDatabase;
	}

	protected OriginalWfdescModelsRepo() {

		workflowDatabase = new HashMap<String, WorkflowInfo>();
	}

	public static OriginalWfdescModelsRepo getInstance() {
		if (instance == null) {
			instance = new OriginalWfdescModelsRepo();
		}
		return instance;
	}


}

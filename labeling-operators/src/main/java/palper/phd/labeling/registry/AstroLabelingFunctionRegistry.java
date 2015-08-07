/**
 * 
 */
package palper.phd.labeling.registry;

import java.util.HashMap;
import java.util.Map;

import palper.phd.labeling.api.ILabelMintingFunction;

/**
 * @author pinarpink
 *
 */
public class AstroLabelingFunctionRegistry {

	static Map<String, ILabelMintingFunction> registry = new HashMap<String, ILabelMintingFunction>();
	

	static {
		registry.put("SesameXML", new SesameLabeler());
//		registry.put("NAME OF THE VI 237 PROCESSOR", new VisierLabeler());
//		registry.put("NAME OF EXTINCTIOPN CALCULATOR", new ExtinctionLabeler());
	}
	public static ILabelMintingFunction getFunction(String workflowElementId) {

		return registry.get(workflowElementId);
	}
	
	

}

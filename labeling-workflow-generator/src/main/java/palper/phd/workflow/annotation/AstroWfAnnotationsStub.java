/**
 * 
 */
package palper.phd.workflow.annotation;

import java.util.HashMap;
import java.util.Map;

import palper.phd.labeling.model.LabelingConstants;
import palper.phd.labeling.model.LabelingSpecBean;
import palper.phd.workflow.wf2labelerwf.LabelSpecFactory;

/**
 * @author pinarpink
 * 
 */
public class AstroWfAnnotationsStub {

	/**
	 * @param args
	 */

	private static Map<String, LabelingSpecBean> cache;

	private static String pref = "http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/";
	static {
		
		cache = new HashMap<String, LabelingSpecBean>();

		String proc1= pref+"SesameXML/";
		cache.put(proc1, LabelSpecFactory.createMintSpec(proc1, null,proc1+"out/return", LabelingConstants.CUSTOM ));	
		String proc2= pref+"Extracting_DEC/";
		cache.put(proc2, LabelSpecFactory.createPropagateSpec(proc2, proc2+"in/xml-text",proc2+"out/nodelist", LabelingConstants.INVARIANT ));
		String proc3= pref+"Extracting_RA/";
		cache.put(proc3, LabelSpecFactory.createPropagateSpec(proc3, proc3+"in/xml-text",proc3+"out/nodelist", LabelingConstants.INVARIANT ));
		String proc4= pref+"Flatten_List_2/";
		cache.put(proc4, LabelSpecFactory.createPropagateSpec(proc4, proc4+"in/inputlist",proc4+"out/outputlist", LabelingConstants.INVARIANT  ));
		String proc5= pref+"Flatten_List/";
		cache.put(proc5, LabelSpecFactory.createPropagateSpec(proc5, proc5+"in/inputlist",proc5+"out/outputlist", LabelingConstants.INVARIANT  ));
		String proc6= pref+"VII_237/";
		cache.put(proc6, LabelSpecFactory.createMintSpec(proc6, null,proc6+"out/responseBody", LabelingConstants.CUSTOM  ));
		String proc7= pref+"Select_logr25_Mtype/";
		cache.put(proc7, LabelSpecFactory.createPropagateSpec(proc7, proc7+"in/voTable",proc7+"out/outputTable", LabelingConstants.INVARIANT  ));
		String proc8= pref+"Format_conversion/";
		cache.put(proc8, LabelSpecFactory.createPropagateSpec(proc8, proc8+"in/voTable",proc8+"out/outputTable", LabelingConstants.INVARIANT  ));
		String proc9= pref+"Write_Text_File/";
		cache.put(proc9, LabelSpecFactory.createPropagateSpec(proc9, proc9+"in/filecontents",proc9+"out/outputFile", LabelingConstants.INVARIANT ));
		String proc10 = pref+"calculate_internal_extinction/";
		cache.put(proc10, LabelSpecFactory.createMintSpec(proc10, null,proc10+"out/STDOUT", LabelingConstants.CUSTOM  ));
			
		
	}

	public static LabelingSpecBean getLabelingSpec(String operationUriString) {
		return cache.get(operationUriString);
	}

}

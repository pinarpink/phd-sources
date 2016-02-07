/**
 * 
 */
package palper.phd.workflow.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import palper.phd.labeling.model.LabelingSpecBean;
import palper.phd.workflow.wf2labelerwf.LabelSpecFactory;

/**
 * @author pinarpink
 * 
 */
public class MotifAnnotationsStub {

  /**
   * @param args
   */

  private static Map<String, LabelingSpecBean> cache;

  private static String pref =
      "http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/";

  static {

    cache = new HashMap<String, LabelingSpecBean>();

    String proc1 = pref + "SesameXML/";
    List<String> sesameTargetPort = new ArrayList<String>();
    sesameTargetPort.add(proc1 + "out/return");
    cache.put(proc1, LabelSpecFactory.createMintSpec(proc1, sesameTargetPort,
        "palper.phd.labeling.registry.SesameLabeler"));

    String proc2 = pref + "Extracting_DEC/";
    List<String> extractDecSourcePort = new ArrayList<String>();
    extractDecSourcePort.add(proc2 + "in/xml-text");
    List<String> extractDecTargetPort = new ArrayList<String>();
    extractDecTargetPort.add(proc2 + "out/nodelist");
    cache.put(proc2,
        LabelSpecFactory.createPropagateSpec(proc2, extractDecSourcePort, extractDecTargetPort));

    String proc3 = pref + "Extracting_RA/";
    List<String> extractRaSourcePort = new ArrayList<String>();
    extractRaSourcePort.add(proc3 + "in/xml-text");
    List<String> extractRaTargetPort = new ArrayList<String>();
    extractRaTargetPort.add(proc3 + "out/nodelist");
    cache.put(proc3,
        LabelSpecFactory.createPropagateSpec(proc3, extractRaSourcePort, extractRaTargetPort));


    String proc4 = pref + "Flatten_List_2/";
    List<String> flattenList2SourcePort = new ArrayList<String>();
    flattenList2SourcePort.add(proc4 + "in/inputlist");
    List<String> flattenList2TargetPort = new ArrayList<String>();
    flattenList2TargetPort.add(proc4 + "out/outputlist");

    cache
        .put(proc4, LabelSpecFactory.createPropagateSpec(proc4, flattenList2SourcePort,
            flattenList2TargetPort));


    String proc5 = pref + "Flatten_List/";
    List<String> flattenListSourcePort = new ArrayList<String>();
    flattenListSourcePort.add(proc5 + "in/inputlist");
    List<String> flattenListTargetPort = new ArrayList<String>();
    flattenListTargetPort.add(proc5 + "out/outputlist");
    cache.put(proc5,
        LabelSpecFactory.createPropagateSpec(proc5, flattenListSourcePort, flattenListTargetPort));

    String proc6 = pref + "VII_237/";
    List<String> vii237ListTargetPort = new ArrayList<String>();
    vii237ListTargetPort.add(proc6 + "out/responseBody");
    cache.put(proc6, LabelSpecFactory.createMintSpec(proc6, vii237ListTargetPort ,
        "palper.phd.labeling.registry.VII_237Labeler"));
    
    String proc7 = pref + "Select_logr25_Mtype/";
    List<String> selectLogrSourcePort = new ArrayList<String>();
    selectLogrSourcePort.add( proc7 + "in/voTable");
    List<String> selectLogrTargetPort = new ArrayList<String>();
    selectLogrTargetPort.add(proc7
        + "out/outputTable");
    cache.put(proc7, LabelSpecFactory.createPropagateSpec(proc7,selectLogrSourcePort, selectLogrTargetPort));
    
    String proc8 = pref + "Format_conversion/";
    List<String> formatConversionSourcePort = new ArrayList<String>();
    formatConversionSourcePort.add(proc8 + "in/voTable");
    List<String> formatConversionTargetPort = new ArrayList<String>();
    formatConversionTargetPort.add(proc8
        + "out/outputTable");
    cache.put(proc8, LabelSpecFactory.createPropagateSpec(proc8,formatConversionSourcePort , formatConversionTargetPort));
    
    
    String proc9 = pref + "Write_Text_File/";
    List<String> writeTextSourcePort = new ArrayList<String>();
    writeTextSourcePort.add(proc9 + "in/filecontents");
    List<String> writeTextTargetPort = new ArrayList<String>();
    writeTextTargetPort.add(proc9
        + "out/outputFile");
    cache.put(
        proc9,
        LabelSpecFactory.createPropagateSpec(proc9,writeTextSourcePort , writeTextTargetPort));
    
    
    String proc10 = pref + "calculate_internal_extinction/";
    List<String> calculateExtinctTargetPort = new ArrayList<String>();
    calculateExtinctTargetPort.add(proc10 + "out/STDOUT");
    cache.put(proc10, LabelSpecFactory.createMintSpec(proc10, calculateExtinctTargetPort,
        "palper.phd.labeling.registry.Calculate_internal_extinctionLabeler"));


  }

  public static LabelingSpecBean getLabelingSpec(String operationUriString) {
    return cache.get(operationUriString);
  }

}

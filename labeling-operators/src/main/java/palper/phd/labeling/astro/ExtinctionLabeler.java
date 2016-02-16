/**
 * 
 */
package palper.phd.labeling.astro;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import palper.phd.labeling.api.ILabelMintingFunction;
import palper.phd.labeling.model.LabelInstanceBean;
import palper.phd.labeling.operator.LabellingExecutionConfig;
import palper.phd.workflow.wfdesc.URIUtils;

import com.hp.hpl.jena.util.FileUtils;

/**
 * @author pinarpink
 *
 */
public class ExtinctionLabeler implements ILabelMintingFunction{

  public List<LabelInstanceBean> run(
      Map<String, InputStream> invocationParamValues) {

  
  InputStream streamForMorphology = invocationParamValues.get(URIUtils.dropTheLastHash(LabellingExecutionConfig.getInstance().getWfdescModel().getNsPrefixMap().get(""))+"processor/calculate_internal_extinction/out/STDOUT");
 
  
  List<LabelInstanceBean> labelList = new ArrayList<LabelInstanceBean>();

  try {
   
       String outputValue = FileUtils.readWholeFileAsUTF8(streamForMorphology);

       String[] results = outputValue.split("\\s+");
       

       LabelInstanceBean label_2 = new LabelInstanceBean();
       label_2.setDefiniton(AstroLabelVectorStub.getMorphologyLabelDef());
       label_2.setValue(results[2]);
       labelList.add(label_2);
      
   
      
  } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
  } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
  }       

  return labelList;

}

}

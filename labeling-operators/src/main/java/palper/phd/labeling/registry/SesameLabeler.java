/**
 * 
 */
package palper.phd.labeling.registry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import palper.phd.labeling.api.ILabelMintingFunction;
import palper.phd.labeling.model.LabelInstanceBean;
import palper.phd.labeling.stubs.LabelVectorStub;
import palper.phd.labeling.stubs.RdfProvenanceConfig;
import palper.phd.workflow.wfdesc.URIUtils;

import com.hp.hpl.jena.util.FileUtils;

/**
 * @author pinarpink
 *
 */
public class SesameLabeler implements ILabelMintingFunction{

	public List<LabelInstanceBean> run(
			Map<String, File> invocationParamValues) {
	
		
		
		
		
		File fileContainingGalaxyName = invocationParamValues.get(URIUtils.dropTheLastHash(RdfProvenanceConfig.getInstance().getWfdescModel().getNsPrefixMap().get(""))+"processor/SesameXML/in/name");
		List<LabelInstanceBean> labelList = new ArrayList<LabelInstanceBean>();

		try {
			String galaxyNAme;
			galaxyNAme = FileUtils.readWholeFileAsUTF8(new FileInputStream(fileContainingGalaxyName));
		
			LabelInstanceBean label_1 = new LabelInstanceBean();
			label_1.setDefiniton(LabelVectorStub.getInstance()
					.getLabelDefinitions().get(1));
			label_1.setValue(galaxyNAme);

			labelList.add(label_1);
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

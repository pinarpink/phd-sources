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
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileUtils;

/**
 * @author pinarpink
 *
 */
public class SesameLabeler implements ILabelMintingFunction{

	public List<LabelInstanceBean> run(
			Map<String, InputStream> invocationParamValues) {
	
		
		InputStream streamForGalaxyName = invocationParamValues.get(URIUtils.dropTheLastHash(LabellingExecutionConfig.getInstance().getWfdescModel().getNsPrefixMap().get(""))+"processor/SesameXML/in/name");
		InputStream streamForOrigin = invocationParamValues.get(URIUtils.dropTheLastHash(LabellingExecutionConfig.getInstance().getWfdescModel().getNsPrefixMap().get(""))+"processor/SesameXML/out/return");
      
		
		List<LabelInstanceBean> labelList = new ArrayList<LabelInstanceBean>();

		try {
			String galaxyNAme = FileUtils.readWholeFileAsUTF8(streamForGalaxyName);
			LabelInstanceBean label_1 = new LabelInstanceBean();
			label_1.setDefiniton(AstroLabelVectorStub.getSubjectLabelDef());
			label_1.setValue(galaxyNAme);
			labelList.add(label_1);
			
			
			Model wfdesc = LabellingExecutionConfig.getInstance().getWfdescModel();
			Resource processor  = WfDescRdfUtils.getProcessorWithLabel("SesameXML", wfdesc);
			String origin = WfDescRdfUtils.getWsdlProcessorRootUri(processor, wfdesc);
			
			
	         LabelInstanceBean label_2 = new LabelInstanceBean();
	         label_2.setDefiniton(AstroLabelVectorStub.getOriginLabelDef());
	         label_2.setValue(origin);
	         labelList.add(label_2);
			
			
			SesameXmlParser parser = new SesameXmlParser();
			parser.parse(streamForOrigin);
			List<String> catalogs = parser.getOriginCatalogs();
			for (String catalog:catalogs){
			
	            LabelInstanceBean labelForCatalog = new LabelInstanceBean();
	            labelForCatalog.setDefiniton(AstroLabelVectorStub.getCatalogLabelDef());
	            labelForCatalog.setValue(catalog);
	            labelList.add(labelForCatalog);
			}
			
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

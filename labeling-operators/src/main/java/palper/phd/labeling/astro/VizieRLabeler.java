/**
 * 
 */
package palper.phd.labeling.astro;

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

/**
 * @author pinarpink
 * 
 */
public class VizieRLabeler implements ILabelMintingFunction {


  public List<LabelInstanceBean> run(Map<String, InputStream> invocationParamValues) {
    List<LabelInstanceBean> labelList = new ArrayList<LabelInstanceBean>();

      Model wfdesc = LabellingExecutionConfig.getInstance().getWfdescModel();
      Resource processor = WfDescRdfUtils.getProcessorWithLabel("VII_237", wfdesc);
      String origin = WfDescRdfUtils.getWsdlProcessorRootUri(processor, wfdesc);

      LabelInstanceBean label_2 = new LabelInstanceBean();
      label_2.setDefiniton(AstroLabelVectorStub.getOriginLabelDef());
      label_2.setValue(origin);
      labelList.add(label_2);
      
      
      InputStream streamForLabels =
          invocationParamValues.get(URIUtils.dropTheLastHash(LabellingExecutionConfig.getInstance()
              .getWfdescModel().getNsPrefixMap().get(""))
              + "processor/VII_237/out/responseBody");


      VoTableXmlParser parser = new VoTableXmlParser();
      parser.parse(streamForLabels);
      List<String> catalogs = parser.getOriginCatalogs();
      for (String catalog : catalogs) {

        LabelInstanceBean labelForCatalog = new LabelInstanceBean();
        labelForCatalog.setDefiniton(AstroLabelVectorStub.getCatalogLabelDef());
        labelForCatalog.setValue(catalog);
        labelList.add(labelForCatalog);
      }


      List<String> subjects = parser.getSubjects();
      for (String subject : subjects) {

        LabelInstanceBean labelForSubject = new LabelInstanceBean();
        labelForSubject.setDefiniton(AstroLabelVectorStub.getSubjectLabelDef());
        labelForSubject.setValue(subject);
        labelList.add(labelForSubject);
      }


    return labelList;

  }


}

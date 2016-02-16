/**
 * 
 */
package palper.phd.labeling.astro;

import java.util.ArrayList;
import java.util.List;

import palper.phd.labeling.model.LabelDefinitonBean;
import palper.phd.labeling.model.LabelVectorBean;
import palper.phd.labeling.model.XSDatatypeEnum;

/**
 * @author pinarpink
 * 
 */
public class AstroLabelVectorStub {

  private static LabelVectorBean vectorInstance;

  private static LabelVectorBean getInstance() {

    if (vectorInstance == null) {

      vectorInstance = new LabelVectorBean();
      List<LabelDefinitonBean> labelList = new ArrayList<LabelDefinitonBean>();

      LabelDefinitonBean originLabel = new LabelDefinitonBean();
      originLabel.setDataTypeEnum(XSDatatypeEnum.XS_STRING);

      originLabel.setLabelNameURIString("http://palper.phd.labeling.astrocase/referenceURI");

      labelList.add(originLabel);


      LabelDefinitonBean catalogLabel = new LabelDefinitonBean();
      catalogLabel.setDataTypeEnum(XSDatatypeEnum.XS_STRING);
      catalogLabel.setLabelNameURIString("http://palper.phd.labeling.astrocase/referenceCatalog");
      labelList.add(catalogLabel);


      LabelDefinitonBean subjectLabel = new LabelDefinitonBean();
      subjectLabel.setDataTypeEnum(XSDatatypeEnum.XS_STRING);
      subjectLabel.setLabelNameURIString("http://palper.phd.labeling.astrocase/hasSubject");
      labelList.add(subjectLabel);


      LabelDefinitonBean morphologyLabel = new LabelDefinitonBean();
      morphologyLabel.setDataTypeEnum(XSDatatypeEnum.XS_STRING);
      morphologyLabel.setLabelNameURIString("http://palper.phd.labeling.astrocase/hasMorphology");
      labelList.add(morphologyLabel);

      vectorInstance.setLabelDefinitions(labelList);

    }
    return vectorInstance;

  }
  
  public static List<LabelDefinitonBean>  getLabelDefinitions(){
    return getInstance().getLabelDefinitions(); 
  }

  public static LabelDefinitonBean getOriginLabelDef() {
    return getInstance().getLabelDefinitions().get(0);
  }

  public static LabelDefinitonBean getCatalogLabelDef() {
    return getInstance().getLabelDefinitions().get(1);
  }

  public static LabelDefinitonBean getSubjectLabelDef() {
    return getInstance().getLabelDefinitions().get(2);
  }

  public static LabelDefinitonBean getMorphologyLabelDef() {
    return getInstance().getLabelDefinitions().get(3);
  }
}

/**
 * 
 */
package palper.phd.labeling.stubs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import palper.phd.labeling.model.LabelDefinitonBean;
import palper.phd.labeling.model.LabelVectorBean;
import palper.phd.labeling.model.XSDatatypeEnum;

/**
 * @author pinarpink
 * 
 */
public class LabelVectorStub {

	private static LabelVectorBean vectorInstance;

	public static LabelVectorBean getInstance() {

		if (vectorInstance == null) {

			vectorInstance = new LabelVectorBean();
			List<LabelDefinitonBean> labelList = new ArrayList<LabelDefinitonBean>();

			LabelDefinitonBean myProvenanceLabel = new LabelDefinitonBean();
			myProvenanceLabel.setDataTypeEnum(XSDatatypeEnum.XS_STRING);

			myProvenanceLabel
					.setLabelNameURIString("http://purl.org/dc/terms/provenance");

			labelList.add(myProvenanceLabel);

			LabelDefinitonBean mySubjectLabel = new LabelDefinitonBean();
			mySubjectLabel.setDataTypeEnum(XSDatatypeEnum.XS_STRING);
			mySubjectLabel
					.setLabelNameURIString("http://purl.org/dc/terms/subject");
			
			
			labelList.add(mySubjectLabel);
			//Subject.NAME
			//

			vectorInstance.setLabelDefinitions(labelList);

		}
		return vectorInstance;

	}

}

/**
 * 
 */
package palper.phd.labeling.stubs;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import palper.phd.labeling.model.LabelInstanceBean;
import palper.phd.labeling.registry.AstroLabelingFunctionRegistry;
import palper.phd.provenance.query.ProvRdfUtils;
import palper.phd.workflow.wfdesc.URIUtils;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author pinarpink Minting stubs operate at the actual data artifacts layer.
 * 
 */
public class MintingStubs {

	private static List<LabelInstanceBean> invokeOriginMinter(
			String wfElementName, String activityInstanceUriString) {

		List<LabelInstanceBean> labelList = new ArrayList<LabelInstanceBean>();

		String dataOriginString = URIUtils.lastBit(wfElementName);

		LabelInstanceBean label_1 = new LabelInstanceBean();
		label_1.setDefiniton(LabelVectorStub.getInstance()
				.getLabelDefinitions().get(0));
		label_1.setValue(dataOriginString);

		labelList.add(label_1);

		return labelList;

	}

	public static List<LabelInstanceBean> invokeMintingFunction(
			String wfElementName, String activityInstanceUriString) {
		List<LabelInstanceBean> resultLabels = new ArrayList<LabelInstanceBean>();
		List<LabelInstanceBean> originLabels = invokeOriginMinter(
				wfElementName, activityInstanceUriString);

		resultLabels.addAll(originLabels);
		if (AstroLabelingFunctionRegistry.getFunction(URIUtils.lastBit(wfElementName)) != null) {
			Map<String, File> results = new HashMap<String, File>();
			Map<String, String> datarefsByPort = ProvRdfUtils
					.getPortArtefactMapForActivity(activityInstanceUriString,
							RdfProvenanceConfig.getInstance()
									.getProvTraceModel());

			for (Map.Entry<String, String> entry : datarefsByPort.entrySet()) {
				String portNAme = entry.getKey();

				String dataRef = entry.getValue();

				Resource contentPath = ProvRdfUtils.getDataContentPath(dataRef,
						RdfProvenanceConfig.getInstance().getProvTraceModel());

				File dataContentFile;
				try {
					dataContentFile = new File(new URI(contentPath.getURI()));
		

				// TODO
				// here we can check whether file contains binary or text
				// content by
				// checking file extension
				// for now we will assume it contains text.

				results.put(portNAme, dataContentFile);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			resultLabels.addAll(AstroLabelingFunctionRegistry.getFunction(
					URIUtils.lastBit(wfElementName)).run(results));
		}
		return resultLabels;
	}
	

}

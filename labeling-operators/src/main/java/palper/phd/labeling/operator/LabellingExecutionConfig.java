/**
 * 
 */
package palper.phd.labeling.operator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileUtils;

/**
 * @author pinarpink
 * 
 */
public class LabellingExecutionConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Model provTraceModel;

	private Model labelingResultModel;

	private static LabellingExecutionConfig instance;

	private File provenanceExportFolder;

	private Model wfdescModel;

	public Model getWfdescModel() {
		return wfdescModel;
	}

	public void setWfdescModel(Model wfdescModel) {
		this.wfdescModel = wfdescModel;
	}

	public static LabellingExecutionConfig getInstance() {

		return instance;
	}

	public Model getProvTraceModel() {
		return provTraceModel;
	}

	public void setProvTraceModel(Model provTraceModel) {
		this.provTraceModel = provTraceModel;
	}

	public Model getLabelingResultModel() {
		return labelingResultModel;
	}

	public void setLabelingResultModel(Model labelingResultModel) {
		this.labelingResultModel = labelingResultModel;
	}

	public File getProvenanceExportFolder() {
		return provenanceExportFolder;
	}

	public void setProvenanceExportFolder(File provenanceExportFolder) {
		this.provenanceExportFolder = provenanceExportFolder;
	}

	public static void initialize(String wfdescRdfFilepath,
			String provRdfFilePath) {
		instance = new LabellingExecutionConfig();

		instance.setLabelingResultModel(ModelFactory
				.createDefaultModel(ReificationStyle.Minimal));

		Model provModel = ModelFactory
				.createDefaultModel(ReificationStyle.Minimal);

		Model wfdescModel = ModelFactory
				.createDefaultModel(ReificationStyle.Minimal);

		try {


			InputStream is = new FileInputStream(wfdescRdfFilepath);
			String lang = FileUtils.guessLang(wfdescRdfFilepath);

			wfdescModel.read(is, "file://"+wfdescRdfFilepath, lang);
			
			
			InputStream is2 = new FileInputStream(provRdfFilePath);
			String lang2 = FileUtils.guessLang(provRdfFilePath);
			provModel.read(is2,"file://"+provRdfFilePath,lang2);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		instance.setProvTraceModel(provModel);
		instance.setWfdescModel(wfdescModel);
		instance.setProvenanceExportFolder(new File(provRdfFilePath)
				.getParentFile());
	}

}

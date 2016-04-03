/**
 * 
 */
package palper.phd.workflow.wf2labelerwf;

import it.unical.mat.dlv.program.Literal;
import it.unical.mat.wrapper.Predicate;
import it.unical.mat.wrapper.PredicateResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deri.iris.EvaluationException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;

import palper.phd.labeling.datalog.DLVClient;
import palper.phd.labeling.datalog.Wfdesc2Datalog;
import palper.phd.labeling.model.LabelingSpecBean;

/**
 * @author pinarpink
 * 
 */
public class Generator {

	Model sciworkflowWfdesc;
	File wfdescFile;

	public Generator(File wfdescFile) {
		super();
		this.wfdescFile = wfdescFile;
	}

	public String getLabelingWorkflowAsString() throws Exception {


			sciworkflowWfdesc = ModelFactory
					.createDefaultModel(ReificationStyle.Minimal);
			InputStream is = new FileInputStream(wfdescFile);
			sciworkflowWfdesc.read(is, null, "TURTLE");

			SciWorflowScavenger scav = new SciWorflowScavenger(
					sciworkflowWfdesc);

			PipelineWfdescCreator generator = new PipelineWfdescCreator(sciworkflowWfdesc, scav.getProcessorSpecBag(),
                scav.getAdjustmentSpecBag());
			String result = generator.run();
			return result;

	}
}

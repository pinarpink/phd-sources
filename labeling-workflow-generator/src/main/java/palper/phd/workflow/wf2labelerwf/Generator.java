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

	public void run() {
		try {
			DLVClient cli = new DLVClient();

			Wfdesc2Datalog ext = new Wfdesc2Datalog(wfdescFile);
			System.out.println(ext.getEdbAsDLVString());
			cli.addWorkflowDescription(ext.getEdbAsDLVString());
			cli.invokeDlv(null, null);

			Map<String, Integer> predictedDepths = new HashMap<String, Integer>();
			Map<Predicate, List<Literal>> idbModel = cli.getWorkflowIDB();

			for (Map.Entry<Predicate, List<Literal>> entry : idbModel
					.entrySet()) {
				// System.out.println(entry.getKey()+"------"+entry.getValue());
				Predicate pred = entry.getKey();
				if (pred.name().equals("predictedDepth")) {
					List<Literal> literals = entry.getValue();
					for (Literal lit : literals) {
						//TODO find the correct API call in DLV wrapper to extract the str value of literal
						String litStr = lit.getAttributeAt(1).toString();
						int len = litStr.length();
						predictedDepths.put(litStr.substring(1, len-1),
								new Integer(lit.getAttributeAt(2).toString()));
						//System.out.println("==="+lit.getAttributeAt(1).toString());
					}
				}

			}

			sciworkflowWfdesc = ModelFactory
					.createDefaultModel(ReificationStyle.Minimal);
			InputStream is = new FileInputStream(wfdescFile);
			sciworkflowWfdesc.read(is, null, "TURTLE");

			SciWorflowScavenger scav = new SciWorflowScavenger(
					sciworkflowWfdesc, predictedDepths);

			PipelineWfdescCreator generator = new PipelineWfdescCreator();
			generator.run(sciworkflowWfdesc, scav.getProcessorSpecBag(),
					scav.getAdjustmentSpecBag());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/**
 * 
 */
package palper.phd.pipeline.maker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;


import org.apache.commons.lang3.StringEscapeUtils;
/**
 * @author pinarpink
 * 
 */
public class WfdescToClojure {

	protected File file;

	protected Map<String, String> nameCache = new HashMap<String, String>();
	protected Map<String, String> portBnfCache = new HashMap<String, String>();
	protected Map<String, String> operatorBnfCache = new HashMap<String, String>();

	public void makeClojureFile(Model wfdescModel) throws IOException {

	
		
		populateNameCache(wfdescModel);
		makeClojurePipeline(wfdescModel);
		String clojureFileName = "TODO.FIX.NAME.clj";

		/** Write pipeline to StdOut and a new clj file */
		writeCljToFile("/Users/pinarpink/Desktop/SummarizationRules/"+ clojureFileName);

	}

	private void makeClojurePipeline(Model wfdescModel) {
	
		Resource workflowResource = WfDescRdfUtils
				.getWorkflowResource(wfdescModel);


		/**
		 * A processor is a unit which performs some work in a workflow. The
		 * name must be unique within the parent workflow.
		 * 
		 */

		Set<Resource> procs = WfDescRdfUtils.getProcessors(wfdescModel,
				workflowResource);

		for (Resource proc : procs) {

			// IN OUR MODEL NODES i.e. processors only return a CTRL output
			// they do not have other output values... they could but we did not
			// find it necessary

			/***************** WE CREATE A CTRL TYPe OUTPUT PORT FOR EACH PROCESSOR ***********************/
			portBnfCache.put(nameCache.get("CTRL-" + proc.getURI()),
					BNFElementFactory.createControlPortElement(nameCache
							.get("CTRL-" + proc.getURI())));

			/***************** INCOMING CONTROL LINKS ****************/

			Set<Resource> ctrlLinks = WfDescRdfUtils
					.getBlockingControlLinksWihSink(wfdescModel, proc);

			List<String> ctrlInList = new ArrayList<String>();
			for (Resource ctrlLinkRes : ctrlLinks) {

				Resource ctrlSourceOp = WfDescRdfUtils.getControlSource(
						wfdescModel, ctrlLinkRes);
				ctrlInList.add(nameCache.get("CTRL-" + ctrlSourceOp.getURI()));
			}

			if (WfDescRdfUtils.isStringConstant(wfdescModel, proc)) {

				Set<Resource> outs = WfDescRdfUtils.getOutputPorts(proc,
						wfdescModel);
				// String constant processors only have one output
				String portUri = outs.iterator().next().getURI();

				portBnfCache.put(portUri, BNFElementFactory
						.createDataPortElement(nameCache.get(portUri)));

				String constVal = WfDescRdfUtils.getStringConstantValue(
						wfdescModel, proc);
				operatorBnfCache.put(proc.getURI(), BNFElementFactory
						.createStringConstantOperatorElement(nameCache.get(proc.getURI()),"\""+StringEscapeUtils.escapeJava(constVal)+"\"",
								ctrlInList,
								nameCache.get("CTRL-" + proc.getURI()),
								nameCache.get(portUri)));

			} else {

				Set<Resource> ins = WfDescRdfUtils.getInputPorts(proc,
						wfdescModel);

				Resource inPort = ins.iterator().next();

				Set<Resource> fulfillingLinks = WfDescRdfUtils
						.getDataLinksWithSinkPort(wfdescModel, inPort);

				// is input port fulfilled by an incoming link?
				if (fulfillingLinks.size() == 1) {
					// if yes then it requires a read on the PORT that is at
					// the source end of the incoming link

					// there SHOULD NORMALLY BE ONE INCOMIG LINK

					Resource dl = fulfillingLinks.iterator().next();

					Resource linkSourcePort = WfDescRdfUtils.getSource(
							wfdescModel, dl);

					operatorBnfCache.put(proc.getURI(), BNFElementFactory
							.createLabelingOperatorElement(nameCache.get(proc.getURI()),
									nameCache.get(linkSourcePort.getURI()),
									ctrlInList,
									nameCache.get("CTRL-" + proc.getURI())));

				} else {

					// THE MULTI CASE is the MERGE utility of taverna... we
					// currently assume that this merge is achieved
					// with an explicit merge processor in the workflow.
					// We do not support this
					throw new RuntimeException();
				}

			}

		}

	}

	private void writeCljToFile(String fileName) throws IOException {
		File target = new File(fileName);

		FileWriter wr = new FileWriter(target, true);

		Iterator it = portBnfCache.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			wr.write((String) pairs.getValue());
			System.out.println(/*pairs.getKey() + " = " + */pairs.getValue());

		}


		it = operatorBnfCache.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			wr.write((String) pairs.getValue());
			System.out.println(/*pairs.getKey() + " = " +*/ pairs.getValue());

		}
		
		wr.close();

	}

	private void populateNameCache(Model wfdescModel) {

		Resource workflowResource = WfDescRdfUtils
				.getWorkflowResource(wfdescModel);

		nameCache.put(workflowResource.getURI(), workflowResource
				.getLocalName().replaceAll("/", "."));

		Set<Resource> inputs = WfDescRdfUtils.getInputPorts(workflowResource,
				wfdescModel);

		for (Resource inp : inputs) {

			nameCache
					.put(inp.getURI(), inp.getLocalName().replaceAll("/", "."));

		}

		Set<Resource> outputs = WfDescRdfUtils.getInputPorts(workflowResource,
				wfdescModel);

		for (Resource out : outputs) {

			nameCache
					.put(out.getURI(), out.getLocalName().replaceAll("/", "."));

		}

		Set<Resource> procs = WfDescRdfUtils.getProcessors(wfdescModel,
				workflowResource);

		for (Resource proc : procs) {

			nameCache.put(proc.getURI(),
					proc.getLocalName().replaceAll("/", "."));

			nameCache.put("CTRL-" + proc.getURI(),
					"CTRL-" + nameCache.get(proc.getURI()));

			Set<Resource> outs = WfDescRdfUtils.getOutputPorts(proc,
					wfdescModel);

			for (Resource pOut : outs) {
				nameCache.put(pOut.getURI(),
						pOut.getLocalName().replaceAll("/", "."));

			}

			Set<Resource> ins = WfDescRdfUtils.getInputPorts(proc, wfdescModel);

			for (Resource pIn : ins) {
				nameCache.put(pIn.getURI(),
						pIn.getLocalName().replaceAll("/", "."));

			}
		}
	}

}

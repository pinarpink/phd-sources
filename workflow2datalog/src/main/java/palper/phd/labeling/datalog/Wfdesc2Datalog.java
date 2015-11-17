
/**
 * @author pinarpink
 *
 */
/**
 * 
 */
package palper.phd.labeling.datalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.deri.iris.EvaluationException;
import org.deri.iris.KnowledgeBaseFactory;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.factory.IConcreteFactory;
import org.deri.iris.api.factory.ITermFactory;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.terms.TermFactory;
import org.deri.iris.terms.concrete.ConcreteFactory;

import palper.phd.workflow.t2flow.lhb.LhbFormulaNode;
import palper.phd.workflow.t2flow.lhb.LhbNodeTypeEnum;
import palper.phd.workflow.wfdesc.URIUtils;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileUtils;


/**
 * @author pinarpink
 * 
 */
public class Wfdesc2Datalog {

	private Model wfdescModel;

	private File destinationFile;

	private File wfdescFile;


	final IBasicFactory BASIC = BasicFactory.getInstance();
	final ITermFactory TERM = TermFactory.getInstance();
	final IConcreteFactory CONCRETE = ConcreteFactory.getInstance();

	private IKnowledgeBase base;
	private Map<IPredicate, IRelation> facts;

	public Wfdesc2Datalog(File wfdescFile) throws FileNotFoundException,
			EvaluationException {
		super();

		this.wfdescFile = wfdescFile;
		wfdescModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
		InputStream is = new FileInputStream(wfdescFile);
		String lang = FileUtils.guessLang(wfdescFile.getName());
		wfdescModel.read(is, null, lang);

		createKnowledgeBase();

	}

	public File writeEdbToDlvFile() throws IOException {
		String edbFilename = wfdescFile.getName();
		edbFilename = edbFilename.replaceFirst("\\..*", "") + ".edb";

		destinationFile = new File(wfdescFile.getParent() + File.separator
				+ edbFilename);

		FileWriter fr = new FileWriter(destinationFile);
		fr.write(this.getEdbAsDLVString());
		fr.close();
		return destinationFile;
	}
	
	public String getEdbAsDLVString() throws IOException {

		String edbIrisString = base.toString();
		edbIrisString = edbIrisString.replaceAll("'", "\"");
		edbIrisString = edbIrisString.replaceAll("\\).", "\\).\n");
		return edbIrisString;
	}

	private void createKnowledgeBase() throws EvaluationException {

		facts = WorkflowEdbStringConstants.getFactHolder();

		/*
		 * 
		 * Workflow
		 */
		Resource wfResource = WfDescRdfUtils.getWorkflowResource(wfdescModel);

		ITerm termWorkflow = TERM.createString(wfResource.getURI());
		facts.get(WorkflowEdbStringConstants.pWF).add(
				BASIC.createTuple(termWorkflow));

		/*
		 * 
		 * Workflow Inputs and Outputs
		 */
		Set<Resource> inputs = WfDescRdfUtils.getInputPorts(wfResource,
				wfdescModel);
		for (Resource inp : inputs) {
			facts.get(WorkflowEdbStringConstants.pWF_IN).add(
					BASIC.createTuple(termWorkflow,
							TERM.createString(inp.getURI())));

			int depth = WfDescRdfUtils.getDepth(inp, wfdescModel);

			facts.get(WorkflowEdbStringConstants.pDEF_DEP).add(
					BASIC.createTuple(termWorkflow,
							TERM.createString(inp.getURI()),
							CONCRETE.createInteger(depth)));

			facts.get(WorkflowEdbStringConstants.pPRED_DEP).add(
					BASIC.createTuple(termWorkflow,
							TERM.createString(inp.getURI()),
							CONCRETE.createInteger(depth)));
		

		}
		Set<Resource> outputs = WfDescRdfUtils.getOutputPorts(wfResource,
				wfdescModel);
		for (Resource out : outputs) {

			facts.get(WorkflowEdbStringConstants.pWF_OUT).add(
					BASIC.createTuple(termWorkflow,
							TERM.createString(out.getURI())));

			facts.get(WorkflowEdbStringConstants.pDEF_DEP).add(
					BASIC.createTuple(termWorkflow,
							TERM.createString(out.getURI()),
							CONCRETE.createInteger(0)));

		}

		/*
		 * 
		 * Processors
		 */
		Set<Resource> procs = WfDescRdfUtils.getProcessors(wfdescModel,
				wfResource);
		for (Resource proc : procs) {
			ITerm termProc = TERM.createString(proc.getURI());
			facts.get(WorkflowEdbStringConstants.pPROC).add(
					BASIC.createTuple(termProc));

			Set<Resource> procInputs = WfDescRdfUtils.getInputPorts(proc,
					wfdescModel);
			for (Resource procInp : procInputs) {

				facts.get(WorkflowEdbStringConstants.pPROC_IN).add(
						BASIC.createTuple(termProc,
								TERM.createString(procInp.getURI())));

				int depth = WfDescRdfUtils.getDepth(procInp, wfdescModel);

				facts.get(WorkflowEdbStringConstants.pDEF_DEP).add(
						BASIC.createTuple(termProc,
								TERM.createString(procInp.getURI()),
								CONCRETE.createInteger(depth)));

			}

			Set<Resource> procOutputs = WfDescRdfUtils.getOutputPorts(proc,
					wfdescModel);

			for (Resource procOut : procOutputs) {
				facts.get(WorkflowEdbStringConstants.pPROC_OUT).add(
						BASIC.createTuple(termProc,
								TERM.createString(procOut.getURI())));

				int depth = WfDescRdfUtils.getDepth(procOut, wfdescModel);

				facts.get(WorkflowEdbStringConstants.pDEF_DEP).add(
						BASIC.createTuple(termProc,
								TERM.createString(procOut.getURI()),
								CONCRETE.createInteger(depth)));

			}

			if (WfDescRdfUtils.hasLhbFormula(wfdescModel, proc)) {

				String lhbJSON = WfDescRdfUtils.getLhbFormulaJSON(wfdescModel,
						proc);
				ObjectMapper mapper = new ObjectMapper();
				try {
					LhbFormulaNode lhbFormula = mapper.readValue(lhbJSON,
							LhbFormulaNode.class);

					String lhbRootId = createLhbFacts(lhbFormula,
							proc.getURI());
					facts.get(WorkflowEdbStringConstants.pHAS_LHB).add(
							BASIC.createTuple(termProc,
									TERM.createString(lhbRootId)));

				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {

				facts.get(WorkflowEdbStringConstants.pHAS_LHB).add(
						BASIC.createTuple(termProc,
								CONCRETE.createToken("null")));

			}

		}

		/*
		 * 
		 * Datalinks
		 */

		Set<Resource> links = WfDescRdfUtils.getDataLinks(wfdescModel,
				wfResource);

		for (Resource linkRes : links) {
			Resource srcPortResource = WfDescRdfUtils.getSource(wfdescModel,
					linkRes);
			Resource snkPortResource = WfDescRdfUtils.getSink(wfdescModel,
					linkRes);

			String srcProcess;
			String snkProcess;
			String srcPort = srcPortResource.getURI();
			String snkPort = snkPortResource.getURI();

			if (WfDescRdfUtils
					.isWorkflowInputPort(srcPortResource, wfdescModel)) {

				srcProcess = wfResource.getURI();

			} else {

				Resource op = WfDescRdfUtils.getOperationWithOutput(
						wfdescModel, srcPortResource);

				srcProcess = op.getURI();

			}

			if (WfDescRdfUtils.isWorkflowOutputPort(snkPortResource,
					wfdescModel)) {
				snkProcess = wfResource.getURI();

			} else {
				Resource op = WfDescRdfUtils.getOperationWithInput(wfdescModel,
						snkPortResource);

				snkProcess = op.getURI();

			}

			facts.get(WorkflowEdbStringConstants.pD_LINK).add(
					BASIC.createTuple(
							TERM.createString(UUID.randomUUID().toString()),
							TERM.createString(srcProcess),
							TERM.createString(srcPort),
							TERM.createString(snkProcess),
							TERM.createString(snkPort)));

		}

		base = KnowledgeBaseFactory.createKnowledgeBase(facts, null);
	

	}

	private String createLhbFacts(LhbFormulaNode node, String processId) {

		String nodeId = UUID.randomUUID().toString();
		if (node.getNodeType().equals(LhbNodeTypeEnum.CROSS)) {
			// put an LHB node datalog fact that is of type CROSS

			facts.get(WorkflowEdbStringConstants.pLHB_NODE).add(
					BASIC.createTuple(TERM.createString(nodeId),
							CONCRETE.createToken("cross"),
							TERM.createString(processId)));

		} else if (node.getNodeType().equals(LhbNodeTypeEnum.DOT)) {
			facts.get(WorkflowEdbStringConstants.pLHB_NODE).add(
					BASIC.createTuple(TERM.createString(nodeId),
							CONCRETE.createToken("dot"),
							TERM.createString(processId)));

		} else if (node.getNodeType().equals(LhbNodeTypeEnum.PORT)) {

			facts.get(WorkflowEdbStringConstants.pLHB_NODE).add(
					BASIC.createTuple(TERM.createString(nodeId),
							TERM.createString(node.getPortName()),
							TERM.createString(processId)));

		}

		int indx = 0;
		for (LhbFormulaNode bn : node.getChildren()) {

			String childId = createLhbFacts(bn, processId);
			facts.get(WorkflowEdbStringConstants.pHAS_CHILD).add(
					BASIC.createTuple(TERM.createString(nodeId),
							TERM.createString(childId),
							CONCRETE.createInteger(indx)));
			indx++;

		}
		return nodeId;

	}
//
//	private String lastBit(String uriString) {
//
//		if (uriString.contains("/")) {
//			int li = uriString.lastIndexOf("/");
//			int lt = uriString.length();
//			if (li == (lt - 1)) {
//				uriString = uriString.substring(0, li);
//				int sli = uriString.lastIndexOf("/");
//				return uriString.substring(sli + 1);
//			} else {
//
//				return uriString.substring(li + 1);
//			}
//		} else {
//			return uriString;
//		}
//	}

}


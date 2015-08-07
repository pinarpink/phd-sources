/**
 * 
 */
package palper.phd.labeling.datalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.deri.iris.EvaluationException;
import org.deri.iris.KnowledgeBaseFactory;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.ITuple;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import palper.phd.workflow.wfdesc.WfDescRdfUtils;
import palper.phd.workflow.t2flow.SimpleScuflInquirer;
//import palper.phd.workflow.t2flow.lhb.FormulaParamBean;
//import palper.phd.workflow.t2flow.lhb.LhbFormulaBean;
//import palper.phd.workflow.t2flow.lhb.LhbNodeTypeEnum;
//import palper.phd.workflow.t2flow.lhb.SimplePortRefBean;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileUtils;

import org.deri.iris.api.basics.IAtom;

import org.deri.iris.api.basics.ILiteral;

import org.deri.iris.api.basics.IQuery;

import org.deri.iris.api.basics.IRule;

import org.deri.iris.api.basics.ITuple;

import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;

import org.deri.iris.api.factory.IBasicFactory;
import org.deri.iris.api.factory.IConcreteFactory;

import org.deri.iris.api.factory.ITermFactory;

import org.deri.iris.api.terms.ITerm;

import org.deri.iris.basics.BasicFactory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.terms.TermFactory;
import org.deri.iris.terms.concrete.ConcreteFactory;

import static palper.phd.labeling.datalog.WorkflowEdbStringConstants.*;

/**
 * @author pinarpink
 * 
 */
public class WorkflowEdbGenerator {

	//private SimpleScuflInquirer ret;

	private Model wfdescModel;

	private File destinationFile;

	private File wfdescFile;
	//private org.w3c.dom.Document document;

	//private File ruleFile;

	final IBasicFactory BASIC = BasicFactory.getInstance();
	final ITermFactory TERM = TermFactory.getInstance();
	final IConcreteFactory CONCRETE = ConcreteFactory.getInstance();

	
	private IKnowledgeBase  base;
	
	public WorkflowEdbGenerator(File wfdescFile)
			throws FileNotFoundException, EvaluationException{
		super();

		//ret = new SimpleScuflInquirer();

		//this.ruleFile = ruleFile;
		
        this.wfdescFile = wfdescFile;
		wfdescModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
		InputStream is = new FileInputStream(wfdescFile);
		String lang = FileUtils.guessLang(wfdescFile.getName());
		wfdescModel.read(is, null, lang);
		
		createKnowledgeBase();

	
	}
	
	public File writeToFile() throws IOException{
		String edbFilename = wfdescFile.getName();
		edbFilename = edbFilename.replaceFirst("\\..*", "") + ".edb";

		destinationFile = new File(wfdescFile.getParent() + File.separator
				+ edbFilename);


		FileWriter fr = new FileWriter(destinationFile);
		fr.write(base.toString());
		
		return destinationFile;
	}

	private void createKnowledgeBase() throws EvaluationException{

	
//			Parser parser = new Parser();
//
//			parser.parse(getReader(ruleFile));

			Map<IPredicate, IRelation> facts = WorkflowEdbStringConstants
					.getFactHolder();

			Resource wfResource = WfDescRdfUtils
					.getWorkflowResource(wfdescModel);
			facts.get(WorkflowEdbStringConstants.pWF).add(
					BASIC.createTuple(TERM.createString(lastBit(wfResource
							.getURI()))));

			Set<Resource> procs = WfDescRdfUtils.getProcessors(wfdescModel,
					wfResource);

			for (Resource proc : procs) {

				facts.get(WorkflowEdbStringConstants.pPROC).add(
						BASIC.createTuple(TERM.createString(lastBit(proc
								.getURI()))));


			}
			
			

		
			Set<Resource> inputs = WfDescRdfUtils.getInputPorts(wfResource,
					wfdescModel);
			for (Resource inp : inputs) {

				
//				facts.get(WorkflowEdbStringConstants.pPROC).add(
//						BASIC.createTuple(TERM.createString(lastBit(proc
//								.getURI()))));
//				
//				
//				final Element nodeElm = doc.createElement(NODE);
//				nodeElm.setAttribute(ID, "WI_"+URIUtils.lastBit(inp.getURI()));
//				appendNodeData(doc, nodeElm, URIUtils.lastBit(inp.getURI()), SimpleScuflNodeEnum.WF_PORT);
//				parent.appendChild(nodeElm);

			}

//			Set<Resource> outputs = WfDescRdfUtils.getOutputPorts(wfResource,
//					wfdescModel);
//			for (Resource out : outputs) {
//
//				final Element nodeElm = doc.createElement(NODE);
//				nodeElm.setAttribute(ID, "WO_"+URIUtils.lastBit(out.getURI()));
//				appendNodeData(doc, nodeElm, URIUtils.lastBit(out.getURI()), SimpleScuflNodeEnum.WF_PORT);
//				parent.appendChild(nodeElm);
//			}
//
//			Set<Resource> procs = WfDescRdfUtils.getProcessors(wfdescModel,
//					wfResource);
//			for (Resource proc : procs) {
//
//				final Element nodeElm = doc.createElement(NODE);
//				nodeElm.setAttribute(ID, "P_"+URIUtils.lastBit(proc.getURI()));
//				
//				
//				boolean isStringConstant = WfDescRdfUtils.isStringConstant(wfdescModel, proc);
//				if (isStringConstant){
//					appendNodeData(doc, nodeElm, URIUtils.lastBit(proc.getURI()), SimpleScuflNodeEnum.STR_CONST);
//				}
//				else{
//					appendNodeData(doc, nodeElm, URIUtils.lastBit(proc.getURI()), SimpleScuflNodeEnum.PROCESSOR);
//				}
//				
//
//				Set<Resource> procInputs = WfDescRdfUtils.getInputPorts(proc,
//						wfdescModel);
//				for (Resource procInp : procInputs) {
//
//					final Element portElm = doc.createElement(PORT);
//					portElm.setAttribute(NAME, URIUtils.lastBit(procInp.getURI()));
//					// appendData(Cytoscape.getNodeAttributes(), doc, nodeElm,
//					// np.getURI().replace('/', '.'));
//					nodeElm.appendChild(portElm);
//				}
//
//				Set<Resource> procOutputs = WfDescRdfUtils.getOutputPorts(proc,
//						wfdescModel);
//				for (Resource procOut : procOutputs) {
//					final Element portElm = doc.createElement(PORT);
//					portElm.setAttribute(NAME, URIUtils.lastBit(procOut.getURI()));
//					// appendData(Cytoscape.getNodeAttributes(), doc, nodeElm,
//					// np.getURI().replace('/', '.'));
//					nodeElm.appendChild(portElm);
//				}
//
//				parent.appendChild(nodeElm);
//
//			}
//
//
//			Set<Resource> links = WfDescRdfUtils.getDataLinks(wfdescModel,
//					wfResource);
//
//			String sourceId = null;
//			String sinkID = null;
//			for (Resource linkRes : links) {
//				Resource srcPortResource = WfDescRdfUtils.getSource(wfdescModel,
//						linkRes);
//				Resource snkPortResource = WfDescRdfUtils.getSink(wfdescModel,
//						linkRes);
//
//				// if ((srcPortResource == null) || (snkPortResource == null)) {
//				// WfDescRdfUtils.getSource(wfdescModel, linkRes);
//				// }
//				final Element edgeElm = doc.createElement(EDGE);
//				if (WfDescRdfUtils
//						.isWorkflowInputPort(srcPortResource, wfdescModel)) {
//
//					sourceId ="WI_"+URIUtils.lastBit(srcPortResource.getURI());
//					edgeElm.setAttribute(SOURCE, sourceId);
//					edgeElm.setAttribute(SOURCE_PORT, "default");
//
//				} else {
//
//					Resource op = WfDescRdfUtils.getOperationWithOutput(
//							wfdescModel, srcPortResource);
//					if (op==null){
//						System.out.println(srcPortResource.getURI());
//						
//					}
//					sourceId = "P_"+URIUtils.lastBit(op.getURI());
//					edgeElm.setAttribute(SOURCE, sourceId);
//					edgeElm.setAttribute(SOURCE_PORT,
//							URIUtils.lastBit(srcPortResource.getURI()));
//				}
//
//				if (WfDescRdfUtils.isWorkflowOutputPort(snkPortResource,
//						wfdescModel)) {
//
//					sinkID = "WO_"+URIUtils.lastBit(snkPortResource.getURI());
//					edgeElm.setAttribute(TARGET, sinkID);
//					edgeElm.setAttribute(TARGET_PORT, "default");
//
//				} else {
//					Resource op = WfDescRdfUtils.getOperationWithInput(wfdescModel,
//							snkPortResource);
//					
//					sinkID = "P_"+URIUtils.lastBit(op.getURI());
//					edgeElm.setAttribute(TARGET, sinkID);
//					edgeElm.setAttribute(TARGET_PORT,
//							URIUtils.lastBit(snkPortResource.getURI()));
//
//				}
//				if (WfDescRdfUtils.isTraceLink(linkRes, wfdescModel)){
//					appendEdgeData(doc, edgeElm, sourceId+"-"
//							+ sinkID,SimpleScuflLinkEnum.INDIRECT_DATALINK);
//				}else{
//					appendEdgeData(doc, edgeElm, sourceId+"-"
//							+ sinkID,SimpleScuflLinkEnum.DIRECT_DATALINK);
//				}
//				
//				// appendData(Cytoscape.getEdgeAttributes(), doc, edgeElm,
//				// edge.getIdentifier());
//				parent.appendChild(edgeElm);
//
//			}
//
//			
//			
//			
//			
//			
//
//				base = KnowledgeBaseFactory.createKnowledgeBase(
//						facts, null);
//				base.
//				System.out.println(base.toString());
//	
	}

	private String lastBit(String uriString) {

		if (uriString.contains("/")) {
			int li = uriString.lastIndexOf("/");
			int lt = uriString.length();
			if (li == (lt - 1)) {
				uriString = uriString.substring(0, li);
				int sli = uriString.lastIndexOf("/");
				return uriString.substring(sli + 1);
			} else {

				return uriString.substring(li + 1);
			}
		} else {
			return uriString;
		}
	}


//	private LhbFormulaBean retrieveLHBWithXpath(String xpathString) {
//
//		LhbFormulaBean result = null;
//		XPath xPath = XPathFactory.newInstance().newXPath();
//		NodeList nodes;
//
//		try {
//			nodes = (NodeList) xPath.evaluate(xpathString,
//					document.getDocumentElement(), XPathConstants.NODESET);
//
//			if (nodes.getLength() != 1) {
//
//				throw new RuntimeException(
//						"There should be exactly one LHB spec per processor");
//			}
//
//			// for (int i = 0; i < nodes.getLength(); ++i) {
//			Element e = (Element) nodes.item(0);
//			result = (LhbFormulaBean) recursiveFormulaTraversal((Element) e
//					.getFirstChild());
//
//		} catch (XPathExpressionException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		return result;
//
//	}

//	private FormulaParamBean recursiveFormulaTraversal(Element e) {
//
//		String tagName = e.getLocalName();
//
//		if (tagName.equalsIgnoreCase("cross")
//				|| tagName.equalsIgnoreCase("dot")) {
//
//			LhbFormulaBean formula = new LhbFormulaBean();
//			formula.setSign(LhbNodeTypeEnum.fromString(tagName.toLowerCase()));
//
//			Node firstChild = ((Node) e).getFirstChild();
//
//			if (firstChild == null) {
//				throw new RuntimeException(
//						"a LHB formula specification should always have a parameter");
//			} else {
//
//				formula.setHead(recursiveFormulaTraversal((Element) firstChild));
//				Node next = ((Node) e).getNextSibling();
//
//				if (next != null) {
//					Node more = next;
//					List<FormulaParamBean> tail = new ArrayList<FormulaParamBean>();
//					while (more != null) {
//						tail.add(recursiveFormulaTraversal((Element) more));
//						more = more.getNextSibling();
//					}
//
//					formula.setTail(tail);
//
//				} else {
//					formula.setTail(null);
//					// the tail of the formula is null;
//				}
//			}
//			return formula;
//
//		} else if (tagName.equalsIgnoreCase("port")) {
//
//			SimplePortRefBean ref = new SimplePortRefBean();
//
//			ref.setName(e.getAttribute("name"));
//
//			return ref;
//		} else {
//
//			throw new RuntimeException(
//					"Unexpected tag in LHB formula specification in t2flow file");
//		}
//
//	}

//	public static Reader getReader(File f) throws IOException {
//		if (!f.exists()) {
//			throw new RuntimeException(f.getName() + " does not exist");
//		}
//
//		if (!f.canRead()) {
//			throw new RuntimeException("cannot read from file " + f.getName()
//					+ "");
//		}
//
//		return new FileReader(f);
//	}

}

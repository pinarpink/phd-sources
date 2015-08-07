/**
 * 
 */
package palper.phd.wfdesc.visualize.graphml;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import palper.phd.workflow.wfdesc.URIUtils;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;




/**
 * @author pinarpink 
 * The below code is adopted from a similar writer from the  Cytoscape GraphML plugin
 *         http://code.google.com/p/graphmlreader/source/browse/trunk/graphmlreader
 *         /src/main/java/org/cytoscape/data/writer/graphml/GraphMLWriter.java
 * 
 */
public class GraphmlWriter {

	private static final String GRAPHML = "graphml";
	private static final String GRAPH = "graph";
	private static final String ID = "id";
	private static final String NODE = "node";
	private static final String PORT = "port";
	private static final String EDGE = "edge";

	private static final String NAME = "name";

	private static final String SOURCE = "source";
	private static final String TARGET = "target";

	private static final String SOURCE_PORT = "sourceport";
	private static final String TARGET_PORT = "targetport";
	private static final String directed = "edgedefault";

	final String[] procAttrNames = { "proc_label", "proc_color" };
	final String[] dataLinkAttrNames = { "link_label", "link_color" };

	private Writer writer;
	private Model wfdescModel;

	protected Map<String, Object> cache = new HashMap<String, Object>();

	public GraphmlWriter(Writer writer, Model wfdescModel) {
		super();
		this.writer = writer;
		this.wfdescModel = wfdescModel;
	}

	public void write() throws IOException, ParserConfigurationException,
			TransformerException {
		final Document graphMLDoc = createDocument();

		TransformerFactory transFactory = TransformerFactory.newInstance();
		transFactory.setAttribute("indent-number", 4);
		Transformer transformer = transFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");

		DOMSource source = new DOMSource(graphMLDoc);

		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);

	}
	
	
	private Document createDocument() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		Document document = builder.newDocument();

		Element root = document.createElement(GRAPHML);

		document.appendChild(root);
		Element graphElm = document.createElement(GRAPH);

		// For now, everything is directed.
		graphElm.setAttribute(directed, "directed");
		graphElm.setAttribute(ID, "Workflow_Title");

		root.appendChild(graphElm);

		writeAttributes(document, root);
		writeNodes(document, graphElm);
		writeEdges(document, graphElm);

		return document;

	}

	private void writeAttributes(Document doc, Element parent) {

		for (String attrName : procAttrNames) {
			Element keyElm = doc.createElement("key");
			keyElm.setAttribute("for", NODE);
			keyElm.setAttribute("attr.name", attrName);
			keyElm.setAttribute("attr.type", "string");
			keyElm.setAttribute(ID, attrName);
			parent.appendChild(keyElm);
		}

		for (String attrName : dataLinkAttrNames) {
			Element keyElm = doc.createElement("key");
			keyElm.setAttribute("for", EDGE);
			keyElm.setAttribute("attr.name", attrName);
			keyElm.setAttribute("attr.type", "string");
			keyElm.setAttribute(ID, attrName);
			parent.appendChild(keyElm);
		}

	}

	private void writeNodes(Document doc, Element parent) {

		Resource wfResource = WfDescRdfUtils.getWorkflowResource(wfdescModel);
		Set<Resource> inputs = WfDescRdfUtils.getInputPorts(wfResource,
				wfdescModel);
		for (Resource inp : inputs) {

			final Element nodeElm = doc.createElement(NODE);
			nodeElm.setAttribute(ID, "WI_"+URIUtils.lastBit(inp.getURI()));
			appendNodeData(doc, nodeElm, URIUtils.lastBit(inp.getURI()), SimpleScuflNodeEnum.WF_PORT);
			parent.appendChild(nodeElm);

		}

		Set<Resource> outputs = WfDescRdfUtils.getOutputPorts(wfResource,
				wfdescModel);
		for (Resource out : outputs) {

			final Element nodeElm = doc.createElement(NODE);
			nodeElm.setAttribute(ID, "WO_"+URIUtils.lastBit(out.getURI()));
			appendNodeData(doc, nodeElm, URIUtils.lastBit(out.getURI()), SimpleScuflNodeEnum.WF_PORT);
			parent.appendChild(nodeElm);
		}

		Set<Resource> procs = WfDescRdfUtils.getProcessors(wfdescModel,
				wfResource);
		for (Resource proc : procs) {

			final Element nodeElm = doc.createElement(NODE);
			nodeElm.setAttribute(ID, "P_"+URIUtils.lastBit(proc.getURI()));
			
			
			boolean isStringConstant = WfDescRdfUtils.isStringConstant(wfdescModel, proc);
			if (isStringConstant){
				appendNodeData(doc, nodeElm, URIUtils.lastBit(proc.getURI()), SimpleScuflNodeEnum.STR_CONST);
			}
			else{
				appendNodeData(doc, nodeElm, URIUtils.lastBit(proc.getURI()), SimpleScuflNodeEnum.PROCESSOR);
			}
			

			Set<Resource> procInputs = WfDescRdfUtils.getInputPorts(proc,
					wfdescModel);
			for (Resource procInp : procInputs) {

				final Element portElm = doc.createElement(PORT);
				portElm.setAttribute(NAME, URIUtils.lastBit(procInp.getURI()));
				// appendData(Cytoscape.getNodeAttributes(), doc, nodeElm,
				// np.getURI().replace('/', '.'));
				nodeElm.appendChild(portElm);
			}

			Set<Resource> procOutputs = WfDescRdfUtils.getOutputPorts(proc,
					wfdescModel);
			for (Resource procOut : procOutputs) {
				final Element portElm = doc.createElement(PORT);
				portElm.setAttribute(NAME, URIUtils.lastBit(procOut.getURI()));
				// appendData(Cytoscape.getNodeAttributes(), doc, nodeElm,
				// np.getURI().replace('/', '.'));
				nodeElm.appendChild(portElm);
			}

			parent.appendChild(nodeElm);

		}

	}


	private void writeEdges(Document doc, Element parent) {

		Resource wfResource = WfDescRdfUtils.getWorkflowResource(wfdescModel);
		Set<Resource> links = WfDescRdfUtils.getDataLinks(wfdescModel,
				wfResource);

		String sourceId = null;
		String sinkID = null;
		for (Resource linkRes : links) {
			Resource srcPortResource = WfDescRdfUtils.getSource(wfdescModel,
					linkRes);
			Resource snkPortResource = WfDescRdfUtils.getSink(wfdescModel,
					linkRes);

			// if ((srcPortResource == null) || (snkPortResource == null)) {
			// WfDescRdfUtils.getSource(wfdescModel, linkRes);
			// }
			final Element edgeElm = doc.createElement(EDGE);
			if (WfDescRdfUtils
					.isWorkflowInputPort(srcPortResource, wfdescModel)) {

				sourceId ="WI_"+URIUtils.lastBit(srcPortResource.getURI());
				edgeElm.setAttribute(SOURCE, sourceId);
				edgeElm.setAttribute(SOURCE_PORT, "default");

			} else {

				Resource op = WfDescRdfUtils.getOperationWithOutput(
						wfdescModel, srcPortResource);
				if (op==null){
					System.out.println(srcPortResource.getURI());
					
				}
				sourceId = "P_"+URIUtils.lastBit(op.getURI());
				edgeElm.setAttribute(SOURCE, sourceId);
				edgeElm.setAttribute(SOURCE_PORT,
						URIUtils.lastBit(srcPortResource.getURI()));
			}

			if (WfDescRdfUtils.isWorkflowOutputPort(snkPortResource,
					wfdescModel)) {

				sinkID = "WO_"+URIUtils.lastBit(snkPortResource.getURI());
				edgeElm.setAttribute(TARGET, sinkID);
				edgeElm.setAttribute(TARGET_PORT, "default");

			} else {
				Resource op = WfDescRdfUtils.getOperationWithInput(wfdescModel,
						snkPortResource);
				
				sinkID = "P_"+URIUtils.lastBit(op.getURI());
				edgeElm.setAttribute(TARGET, sinkID);
				edgeElm.setAttribute(TARGET_PORT,
						URIUtils.lastBit(snkPortResource.getURI()));

			}
			if (WfDescRdfUtils.isTraceLink(linkRes, wfdescModel)){
				appendEdgeData(doc, edgeElm, sourceId+"-"
						+ sinkID,SimpleScuflLinkEnum.INDIRECT_DATALINK);
			}else{
				appendEdgeData(doc, edgeElm, sourceId+"-"
						+ sinkID,SimpleScuflLinkEnum.DIRECT_DATALINK);
			}
			
			// appendData(Cytoscape.getEdgeAttributes(), doc, edgeElm,
			// edge.getIdentifier());
			parent.appendChild(edgeElm);

		}
	}

	private void appendNodeData(Document doc, Element parent, String label, 
			SimpleScuflNodeEnum nodeType) {

		Element dataElm = doc.createElement("data");
		dataElm.setAttribute("key", procAttrNames[0]);
		// label
		dataElm.setTextContent(label);
		parent.appendChild(dataElm);

		Element dataElm2 = doc.createElement("data");
		dataElm2.setAttribute("key", procAttrNames[1]);
		// color
		if (nodeType.equals(SimpleScuflNodeEnum.WF_PORT)) {
			dataElm2.setTextContent("#00ffff");
		} else if (nodeType.equals(SimpleScuflNodeEnum.PROCESSOR)) {
			dataElm2.setTextContent("#99cc00");
		} else {
			dataElm2.setTextContent("#3366ff");
		}
		parent.appendChild(dataElm2);

	}

	private void appendEdgeData(Document doc, Element parent, String label, SimpleScuflLinkEnum linkType) {
		Element dataElm = doc.createElement("data");
		dataElm.setAttribute("key", dataLinkAttrNames[0]);
		// label
		dataElm.setTextContent(label);
		parent.appendChild(dataElm);
		
		Element dataElm2 = doc.createElement("data");
		dataElm2.setAttribute("key", dataLinkAttrNames[1]);
		// color
		if (linkType.equals(SimpleScuflLinkEnum.DIRECT_DATALINK)) {
			dataElm2.setTextContent("#000000");
		}else{
			dataElm2.setTextContent("#ff0000");
		}
		parent.appendChild(dataElm2);
	}


}

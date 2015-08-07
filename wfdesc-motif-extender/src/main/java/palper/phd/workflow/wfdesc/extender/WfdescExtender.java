package palper.phd.workflow.wfdesc.extender;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import palper.phd.workflow.t2flow.SimpleScuflInquirer;
import palper.phd.workflow.wfdesc.SummarizerNamespaces;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;
import palper.phd.workflow.wfdesc.motifs.AnnotationHolderBean;
import palper.phd.workflow.wfdesc.motifs.AnnotationSet;
import palper.phd.workflow.wfdesc.motifs.StructuredAnnotationSetFactory;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileUtils;

/**
 * 
 */

/**
 * @author pinarpink
 * 
 */
public class WfdescExtender {

	private InfModel wfdescModel;

	private SimpleScuflInquirer ret;

	private File destinationFile;
	
	private  org.w3c.dom.Document document;

	public WfdescExtender(File wfdescFile, File t2FlowFile)
			throws FileNotFoundException {
		super();

		Model baseModel = ModelFactory
				.createDefaultModel(ReificationStyle.Minimal);
		InputStream is = new FileInputStream(wfdescFile);
		String lang = FileUtils.guessLang(wfdescFile.getName());
		baseModel.read(is, null, lang);

		Model motifsModel = ModelFactory
				.createDefaultModel(ReificationStyle.Minimal);

		motifsModel
				.read("file:///Users/pinarpink/Desktop/SummarizationRules/ontology/motifs_palper.owl",
						"http://purl.org/wf4ever/motifs.owl#", "RDF/XML");
		wfdescModel = ModelFactory.createRDFSModel(motifsModel, baseModel);
		
		//FOR REPRSENTING SCUFL2 PORT DEPTHS
//		baseModel.read("https://raw.github.com/myGrid/scufl2/master/scufl2-rdf/src/main/resources/scufl2.rdf","http://ns.taverna.org.uk/2010/scufl2#","RDF/XML");
//		

		String filename = wfdescFile.getName();
		filename = filename.replaceFirst("\\..*", "") + "-motifs.wfdesc.ttl";

		destinationFile = new File(wfdescFile.getParent() + File.separator
				+ filename);

		if (t2FlowFile.exists()) {
			// obtain Document somehow, doesn't matter how

			try {
				DocumentBuilder b;
				b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				document = b.parse(t2FlowFile);
				ret = new SimpleScuflInquirer();

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			System.out
					.println("Non-existent t2flow file. Unable to reach motif annotations!");
		}

	}

	public void write() throws IOException {

		expandModelWithMotifs();
		expandModelWithPortDepths();


		RDFWriter fasterWriter = wfdescModel.getRawModel().getWriter("TTL");
		fasterWriter.setProperty("relativeURIs", "same-document");
		fasterWriter.write(wfdescModel.getRawModel(), new FileWriter(
				destinationFile), null);

	}





	private void expandModelWithMotifs() {

		Resource wfResource = WfDescRdfUtils.getWorkflowResource(wfdescModel);
		Set<Resource> inputs = WfDescRdfUtils.getInputPorts(wfResource,
				wfdescModel);
		for (Resource inp : inputs) {
			
			String qry = ret
					.getWorkflowInputPortAnnotationsXPATH(lastBit(inp.getURI()));
			AnnotationSet annots = retrieveAnnotsWithXpath(qry);
			if (annots != null) {
				addAnnotationsToModel(annots, wfdescModel, inp);
			}
		}

		Set<Resource> outputs = WfDescRdfUtils.getOutputPorts(wfResource,
				wfdescModel);
		for (Resource out : outputs) {
			String qry = ret
					.getWorkflowOutputPortAnnotationsXPATH(lastBit(out.getURI()));
			AnnotationSet annots = retrieveAnnotsWithXpath(qry);
			if (annots != null) {
				addAnnotationsToModel(annots, wfdescModel, out);
			}
		}

		Set<Resource> procs = WfDescRdfUtils.getProcessors(wfdescModel,
				wfResource);
		for (Resource proc : procs) {

			String qry = ret.getProcessorAnnotationsXPATH(lastBit(proc
					.getURI()));
			AnnotationSet annots = retrieveAnnotsWithXpath(qry);
			if (annots != null) {
				addAnnotationsToModel(annots, wfdescModel, proc);
			}

			Set<Resource> procInputs = WfDescRdfUtils.getInputPorts(proc,
					wfdescModel);
			for (Resource procInp : procInputs) {

				String qry2 = ret
						.getProcessorInputAnnotationsXPATH(lastBit(proc
								.getURI()), lastBit(procInp.getURI()));
				AnnotationSet inpannots = retrieveAnnotsWithXpath(qry2);
				if (inpannots != null) {
					addAnnotationsToModel(inpannots, wfdescModel, procInp);
				}
			}

			Set<Resource> procOutputs = WfDescRdfUtils.getOutputPorts(proc,
					wfdescModel);
			for (Resource procOut : procOutputs) {
				String qry2 =  ret
						.getProcessorOutputAnnotationsXPATH(lastBit(proc
								.getURI()), lastBit(procOut.getURI()));
				AnnotationSet outannots = retrieveAnnotsWithXpath(qry2);
				if (outannots != null) {
					addAnnotationsToModel(outannots, wfdescModel, procOut);
				}
			}

		}

	}

	private void expandModelWithPortDepths() {

		Resource wfResource = WfDescRdfUtils.getWorkflowResource(wfdescModel);
		Set<Resource> inputs = WfDescRdfUtils.getInputPorts(wfResource,
				wfdescModel);
		for (Resource inp : inputs) {
			String qry = ret
					.getWorkflowInputPortDepthXPATH(lastBit(inp.getURI()));
			int depth = retrieveDepthWithXpath(qry);
			if (depth != -1) {
				addDepthToModel(depth,  wfdescModel, inp);
			}
		}
		Set<Resource> outputs = WfDescRdfUtils.getOutputPorts(wfResource,
				wfdescModel);
		for (Resource out : outputs) {
			String qry = ret
					.getWorkflowOutputPortDepthXPATH(lastBit(out.getURI()));
			int depth = retrieveDepthWithXpath(qry);
			if (depth != -1) {
				addDepthToModel(depth,  wfdescModel, out);
			}
		}

		Set<Resource> procs = WfDescRdfUtils.getProcessors(wfdescModel,
				wfResource);
		for (Resource proc : procs) {


			Set<Resource> procInputs = WfDescRdfUtils.getInputPorts(proc,
					wfdescModel);
			for (Resource procInp : procInputs) {

				String qry = ret
						.getProcessorInputDepthXPATH(lastBit(proc
								.getURI()), lastBit(procInp.getURI()));
				int inpDepth = retrieveDepthWithXpath(qry);
				if (inpDepth != -1) {
					addDepthToModel(inpDepth, wfdescModel, procInp);
				}
			}

			Set<Resource> procOutputs = WfDescRdfUtils.getOutputPorts(proc,
					wfdescModel);
			for (Resource procOut : procOutputs) {
				String qry = ret
						.getProcessorOutputDepthXPATH(lastBit(proc
								.getURI()), lastBit(procOut.getURI()));
				
				int outDepth = retrieveDepthWithXpath(qry);
				if (outDepth != -1) {
					addDepthToModel(outDepth, wfdescModel, procOut);
				}
			}

		}

	}

	
	private void addAnnotationsToModel(AnnotationSet annots, Model wfdescModel,
			Resource res) {
		while (annots.hasNext()) {
			AnnotationHolderBean an = annots.next();

			Resource objectOfAnnotAssertion = wfdescModel.getResource(an.getId());
			
			if (!wfdescModel.contains(objectOfAnnotAssertion, wfdescModel.getProperty(SummarizerNamespaces.rdfNS+"type"))){

				wfdescModel.add(
						objectOfAnnotAssertion,
						wfdescModel.getProperty(SummarizerNamespaces.rdfNS
								+ "type"),
						wfdescModel.getResource(an.getMotifClass()));

			}
			wfdescModel.add(res, wfdescModel.getProperty(an.getPredicate()),
					objectOfAnnotAssertion);
		}
	}
	private void addDepthToModel(int depth, Model wfdescModel,
			Resource res) {
		if (depth != -1) {
			wfdescModel.addLiteral(
						res,
						wfdescModel.getProperty(SummarizerNamespaces.scuflNS
								+ "portDepth"),
						depth);

		}
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
	
	private int retrieveDepthWithXpath(String xpathString) {

		Integer result = null;
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;

		try {
			nodes = (NodeList) xPath.evaluate(xpathString,
					document.getDocumentElement(), XPathConstants.NODESET);

			for (int i = 0; i < nodes.getLength(); ++i) {
				Element e = (Element) nodes.item(i);

				NodeList depths = e.getElementsByTagName("depth");
				if (depths != null) {
					
						Element depth = (Element) depths.item(0);
						

							if (depth != null) {
								String depthStr = depth
										.getTextContent();

								result = new Integer(depthStr);

							}
				}
			}
		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (result != null){
			return result.intValue();
		}else {
			return -1;

		}
		
	}
	
	private AnnotationSet retrieveAnnotsWithXpath(String xpathString) {

		AnnotationSet result = null;
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes;

		try {
			nodes = (NodeList) xPath.evaluate(xpathString,
					document.getDocumentElement(), XPathConstants.NODESET);

			for (int i = 0; i < nodes.getLength(); ++i) {
				Element e = (Element) nodes.item(i);

				NodeList annotations = e.getElementsByTagName("annotationBean");
				if (annotations != null) {
					for (int j = 0; j < annotations.getLength(); ++j) {
						Element annotation = (Element) annotations.item(j);
						if (annotation
								.getAttribute("class")
								.equals("net.sf.taverna.t2.annotation.annotationbeans.FreeTextDescription")) {
							NodeList texts = annotation
									.getElementsByTagName("text");

							if (texts != null) {
								String annotStr = texts.item(0)
										.getTextContent();

								result = StructuredAnnotationSetFactory
										.fromInventedFormat(annotStr);

							}
						} else {
							continue;
						}
					}

				}

			}
		} catch (XPathExpressionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return result;

	}



}

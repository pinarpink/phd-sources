package palper.phd.workflow.wfdesc;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
/**
 * 
 */

import org.openrdf.elmo.ElmoModule;
import org.openrdf.elmo.sesame.SesameManager;
import org.openrdf.elmo.sesame.SesameManagerFactory;
import org.purl.wf4ever.wfdesc.scufl2.Labelled;
import org.purl.wf4ever.wfdesc.scufl2.TurtleWriterWithBase;
import org.purl.wf4ever.wfdesc.scufl2.WfdescSerialiser;

import java.io.OutputStream;
import java.net.URI;

import javax.xml.namespace.QName;

import org.openrdf.OpenRDFException;
import org.openrdf.elmo.ElmoModule;
import org.openrdf.elmo.sesame.SesameManager;
import org.openrdf.elmo.sesame.SesameManagerFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.contextaware.ContextAwareConnection;
import org.openrdf.rio.helpers.OrganizedRDFWriter;
import org.purl.wf4ever.wf4ever.BeanshellScript;
import org.purl.wf4ever.wf4ever.CommandLineTool;
import org.purl.wf4ever.wf4ever.RScript;
import org.purl.wf4ever.wf4ever.SOAPService;

import org.purl.wf4ever.wfdesc.Description;
import org.purl.wf4ever.wfdesc.Input;
import org.purl.wf4ever.wfdesc.Output;
import org.purl.wf4ever.wfdesc.Process;

import palper.phd.labelingwf.DepthInput;
import palper.phd.labelingwf.DepthOutput;
import palper.phd.labelingwf.StringConstant;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.WriterException;

/**
 * @author pinarpink
 * 
 */
public class WfDescCrudUtils {

	private SesameManager sesameManager;

	private QName wfQName;

	private String baseURIString;
	
	public SesameManager getSesameManager() {
		if (sesameManager == null) {
			ElmoModule module = new ElmoModule();
			module.addConcept(Labelled.class);
			SesameManagerFactory factory = new SesameManagerFactory(module);
			factory.setInferencingEnabled(true);
			sesameManager = factory.createElmoManager();

		}
		return sesameManager;
	}

	public WfDescCrudUtils(String namespace/*, String workflowURI*/) {

		baseURIString = namespace;
		wfQName = new QName(namespace, "" /*workflowURI*/);

		getSesameManager().create(wfQName,
				org.purl.wf4ever.wfdesc.Workflow.class);

	}

	public QName addBeanshellSubProcess(String subProcessURI,
			String beanShellString) {

		QName qName = new QName(wfQName.getNamespaceURI(), subProcessURI);

		BeanshellScript process = getSesameManager().create(qName,
				BeanshellScript.class);
		getSesameManager()
				.designate(wfQName, org.purl.wf4ever.wfdesc.Workflow.class)
				.getWfHasSubProcess().add(process);
		BeanshellScript script = getSesameManager().designateEntity(process,
				BeanshellScript.class);
		script.getWfScript().add(beanShellString);

		return qName;
	}

	public QName addStringConstantSubProcess(String subProcessURI,
			String constantValue) {
		QName qName = new QName(wfQName.getNamespaceURI(), subProcessURI);
		StringConstant process = getSesameManager().create(qName,
				StringConstant.class);
		getSesameManager()
				.designate(wfQName, org.purl.wf4ever.wfdesc.Workflow.class)
				.getWfHasSubProcess().add(process);

		process.getWfConstantValue().add(constantValue);

		return qName;
	}

	public QName addInputPortToProcess(QName parentQName, String portURI) {

		QName portQName = new QName(wfQName.getNamespaceURI(), portURI);
		DepthInput input = sesameManager.create(portQName, DepthInput.class);
		input.getWfDefinedDepth().add(java.math.BigInteger.ZERO);
		Process process = sesameManager.designate(parentQName, Process.class);
		process.getWfHasInput().add(input);
		return portQName;
	}

	public QName addOutputPortToProcess(QName parentQName, String portURI) {

		QName portQName = new QName(wfQName.getNamespaceURI(), portURI);

		DepthOutput output = sesameManager.create(portQName, DepthOutput.class);
		output.getWfDefinedDepth().add(java.math.BigInteger.ZERO);
		sesameManager.designate(parentQName, Process.class).getWfHasOutput()
				.add(output);

		return portQName;
	}

	public void addSimulatedControlFlowLink(QName sourceQName, QName sinkQName) {

//		QName linkQName = new QName(wfQName.getNamespaceURI(), "controllink:"
//				+ Math.random());
//
//		org.purl.wf4ever.wfdesc.DataLink dl = sesameManager.create(linkQName,
//				org.purl.wf4ever.wfdesc.DataLink.class);
//		Output source = sesameManager.designate(sourceQName, Output.class);
//		dl.getWfHasSource().add(source);
//		Input sink = sesameManager.designate(sinkQName, Input.class);
//		dl.getWfHasSink().add(sink);
//		sesameManager
//				.designate(wfQName, org.purl.wf4ever.wfdesc.Workflow.class)
//				.getWfHasDataLink().add(dl);
		
		
		
		QName linkQName = new QName(wfQName.getNamespaceURI(), "controllink:"
				+ Math.random());
		palper.phd.labelingwf.BlockingControlLink cl = sesameManager.create(linkQName, palper.phd.labelingwf.BlockingControlLink .class);
		
		Process sourceOperation = sesameManager.designate(sourceQName, Process.class);
		cl.getWfHasControlSource().add(sourceOperation);
		Process sinkOperation = sesameManager.designate(sinkQName, Process.class);
		cl.getWfHasControlSink().add(sinkOperation);
		
		
		sesameManager.designate(wfQName, palper.phd.labelingwf.LabelingwfWorkflow.class).getWfHasBlockingControlLink().add(cl);
		// ADD MORE ANNOTATIONS TO DENOTE IT IS A CONTROL LINK

	}

	public void addDataLink(QName sourceQName, QName sinkQName) {

		QName linkQName = new QName(wfQName.getNamespaceURI(), "datalink:"
				+ Math.random());

		org.purl.wf4ever.wfdesc.DataLink dl = sesameManager.create(linkQName,
				org.purl.wf4ever.wfdesc.DataLink.class);
		Output source = sesameManager.designate(sourceQName, Output.class);
		dl.getWfHasSource().add(source);
		Input sink = sesameManager.designate(sinkQName, Input.class);
		dl.getWfHasSink().add(sink);
		sesameManager
				.designate(wfQName, org.purl.wf4ever.wfdesc.Workflow.class)
				.getWfHasDataLink().add(dl);

	}

	//
	//
	// // } else if (node instanceof InputPort) {
	// Input input = sesameManager.create(qName, Input.class);
	// Process process = sesameManager.designate(parentQName, Process.class);
	// process.getWfHasInput().add(input);
	// // } else if (node instanceof OutputPort) {
	// Output output = sesameManager.create(qName, Output.class);
	// sesameManager.designate(parentQName, Process.class).getWfHasOutput()
	// .add(output);
	// // } else if (node instanceof DataLink) {

	public void save(OutputStream output)
			throws WriterException {

		ContextAwareConnection connection = sesameManager.getConnection();
		try {
			connection
					.setNamespace("wfdesc", "http://purl.org/wf4ever/wfdesc#");
			connection.setNamespace("wf4ever",
					"http://purl.org/wf4ever/wf4ever#");
			connection.setNamespace("rdfs",
					"http://www.w3.org/2000/01/rdf-schema#");
			connection
					.setNamespace("lblwf",
							"http://www.semanticweb.org/pinarpink/ontologies/2013/10/labelingwf#");

            connection.setNamespace("", "#");
		
			connection.export(new OrganizedRDFWriter(new TurtleWriterWithBase(
					output, new URI(baseURIString))));
		} catch (OpenRDFException e) {
			throw new WriterException("Can't write to output", e);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

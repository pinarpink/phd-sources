/**
 * 
 */
package palper.phd.provenance.query;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import palper.phd.provenance.model.DatalinkBean;
import palper.phd.provenance.model.OperationBean;
import palper.phd.provenance.model.PortBean;
import palper.phd.provenance.model.PortTypeEnum;
import palper.phd.provenance.model.WfElementThreadIterator;
import palper.phd.provenance.model.WorkflowElementBean;
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
public class DesignLineageTraversal {
	
	

//	public DesignLineageTraversal( String wfdescFilePath, String portUriString) {
//		
//		
//		super();
//		
//	try {
//
//			
//			Model wfdescModel = ModelFactory
//					.createDefaultModel(ReificationStyle.Minimal);
//
//			InputStream is;
//
//			is = new FileInputStream(wfdescFilePath);
//
//			wfdescModel.read(is, null, FileUtils.guessLang(wfdesc_file_path.getName()));
//			
//			Model provModel = ModelFactory
//					.createDefaultModel(ReificationStyle.Minimal);
//
//			InputStream is2 =new FileInputStream(prov_file_path);
//
//			
//			provModel.read(is2,"file:/Users/pinarpink/Desktop/UTILIZING_SUMMARIES/2920_WF_RUN-4/workflowrun.prov.ttl/" ,FileUtils.langTurtle);
//
////			List<List<PortBean>> results = downstreamLineagePorts(wfdescModel, "http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/in/list_cig_name");
//			
//
////			List<List<WorkflowElementBean>> results = upstreamLineagePorts(wfdescModel, "http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/out/logr25");
//			                                                        
//	
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
////			catch (MalformedURLException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
//
//		
//	}



	public static List<List<PortBean>> downstreamLineagePorts(Model wfdescModel,
			String portURIAsString) {

		Resource portResource = wfdescModel.getResource(portURIAsString);
		
		PortBean currentPort = buildPortBean(wfdescModel, portResource);
		Set<Resource> downstreamPorts = getDownStreamAdjacentPorts(wfdescModel,
				portResource, currentPort);

		Iterator<Resource> iter = downstreamPorts.iterator();
		List<List<PortBean>> combinedPaths = new ArrayList<List<PortBean>>();

		if (iter.hasNext()) {

			while (iter.hasNext()) {

				Resource downStreamPort = iter.next();

				List<List<PortBean>> possiblePaths = downstreamLineagePorts(wfdescModel, downStreamPort.getURI());
				Iterator<List<PortBean>> iter2 = possiblePaths.iterator();

				while (iter2.hasNext()) {
					iter2.next().add(buildPortBean(wfdescModel, portResource));
				}

				combinedPaths.addAll(possiblePaths);

			}

		} else {

			List<PortBean> singleThread = new ArrayList<PortBean>();

			singleThread.add(buildPortBean(wfdescModel, portResource));

			combinedPaths.add(singleThread);

		}
		return combinedPaths;
	}

	static Set<Resource> getDownStreamAdjacentPorts(Model wfdescModel,
			Resource portResource, PortBean currentPort) {

		if (currentPort.isOutput()
				|| WfDescRdfUtils
						.isWorkflowInputPort(portResource, wfdescModel)) {
			Set<Resource> links = WfDescRdfUtils.getDataLinksWithSourcePort(
					wfdescModel, portResource);

			Set<Resource> result = new HashSet<Resource>();
			Iterator<Resource> iter = links.iterator();

			while (iter.hasNext()) {
				Resource linkResource = iter.next();
				result.add(WfDescRdfUtils.getSink(wfdescModel, linkResource));

			}
			return result;
		} else if (WfDescRdfUtils.isWorkflowOutputPort(portResource,
				wfdescModel)) {
			return new HashSet<Resource>();
		} else if (currentPort.isInput()) {
			Resource op = WfDescRdfUtils.getOperationWithInput(wfdescModel,
					portResource);
			return WfDescRdfUtils.getOutputPorts(op, wfdescModel);

		} else {
			// is there any other case? See to it after tests
			return new HashSet<Resource>();

		}

	}



	public static List<List<WorkflowElementBean>> upstreamLineagePorts(Model wfdescModel, String portURIAsString) {

		Resource portResource = wfdescModel.getResource(portURIAsString);
		
		PortBean currentPort = buildPortBean(wfdescModel, portResource);
		Set<Resource> upstreamPorts = getUpStreamAdjacentPorts(wfdescModel,
				portResource, currentPort);

		Iterator<Resource> iter = upstreamPorts.iterator();
		List<List<WorkflowElementBean>> combinedPaths = new ArrayList<List<WorkflowElementBean>>();

		if (iter.hasNext()) {

			while (iter.hasNext()) {

				Resource upStreamPort = iter.next();

				List<List<WorkflowElementBean>> possiblePaths = upstreamLineagePorts(wfdescModel, upStreamPort.getURI());
				Iterator<List<WorkflowElementBean>> iter2 = possiblePaths.iterator();

				while (iter2.hasNext()) {
					iter2.next().add(buildPortBean(wfdescModel, portResource));
				}

				combinedPaths.addAll(possiblePaths);

			}

		} else {

			List<WorkflowElementBean> singleThread = new ArrayList<WorkflowElementBean>();

			singleThread.add(buildPortBean(wfdescModel, portResource));

			combinedPaths.add(singleThread);

		}
		return combinedPaths;
	}
	
	static Set<Resource> getUpStreamAdjacentPorts(Model wfdescModel,
			Resource portResource, PortBean currentPort) {

		if (currentPort.isInput()
				|| WfDescRdfUtils
						.isWorkflowOutputPort(portResource, wfdescModel)) {
			Set<Resource> links = WfDescRdfUtils.getDataLinksWithSinkPort(
					wfdescModel, portResource);

			Set<Resource> result = new HashSet<Resource>();
			Iterator<Resource> iter = links.iterator();

			while (iter.hasNext()) {
				Resource linkResource = iter.next();
				result.add(WfDescRdfUtils.getSource(wfdescModel, linkResource));

			}
			return result;
		} else if (WfDescRdfUtils.isWorkflowInputPort(portResource,
				wfdescModel)) {
			return new HashSet<Resource>();
		} else if (currentPort.isOutput()) {
			Resource op = WfDescRdfUtils.getOperationWithOutput(wfdescModel,
					portResource);
			return WfDescRdfUtils.getInputPorts(op, wfdescModel);

		} else {
			// is there any other case? See to it after tests
			return new HashSet<Resource>();

		}

	}
	
	static PortBean buildPortBean(Model wfdescModel, Resource portResource) {

		if (portResource == null) {
			throw new IllegalArgumentException();
		}

		PortBean result;

		if (WfDescRdfUtils.isInputPort(portResource, wfdescModel)) {
			result = new PortBean(PortTypeEnum.INPUT, portResource.getURI());
		} else if (WfDescRdfUtils.isOutputPort(portResource, wfdescModel)) {
			result = new PortBean(PortTypeEnum.OUTPUT, portResource.getURI());
		} else {
			result = new PortBean(PortTypeEnum.HYBRID, portResource.getURI());
		}

		return result;

	}

	
	public static List<List<WorkflowElementBean>> downstreamWorkflowPath(Model wfdescModel,
			String elementURIString) {

		Resource elementResource = wfdescModel.getResource(elementURIString);

		WorkflowElementBean currentElement = null;
		Set<Resource> nextElements =new HashSet<Resource>();
		
		if (WfDescRdfUtils.isWorkflowInputPort(elementResource, wfdescModel)){
			currentElement = buildPortBean(wfdescModel, elementResource);
			nextElements = WfDescRdfUtils.getDataLinksWithSourcePort(wfdescModel, elementResource);
			
			
		}else if (WfDescRdfUtils.isWorkflowOutputPort(elementResource, wfdescModel)){
			currentElement = buildPortBean(wfdescModel, elementResource);
			// there are no next elements.
			
		}
		else if (WfDescRdfUtils.isDataLink( wfdescModel, elementResource)){
			currentElement = new DatalinkBean(elementResource.getURI());
			Resource sinkResource= WfDescRdfUtils.getSink(wfdescModel, elementResource);
			if (WfDescRdfUtils.isWorkflowOutputPort(sinkResource, wfdescModel)){
				nextElements.add(sinkResource);
			}else{
				nextElements.add(WfDescRdfUtils.getOperationWithInput(wfdescModel, sinkResource));
			}
		}else if (WfDescRdfUtils.isOperation( wfdescModel, elementResource)){
			currentElement = new OperationBean(elementResource.getURI());
			nextElements = WfDescRdfUtils.getOutlinks(elementResource, wfdescModel);
		}

		Iterator<Resource> iter = nextElements.iterator();
		List<List<WorkflowElementBean>> combinedPaths = new ArrayList<List<WorkflowElementBean>>();

		if (iter.hasNext()) {

			while (iter.hasNext()) {

				Resource downStreamElement = iter.next();

				List<List<WorkflowElementBean>> possiblePaths = downstreamWorkflowPath(wfdescModel, downStreamElement.getURI());
				Iterator<List<WorkflowElementBean>> iter2 = possiblePaths.iterator();

				while (iter2.hasNext()) {
					iter2.next().add(currentElement);
				}

				combinedPaths.addAll(possiblePaths);

			}

		} else {

			List<WorkflowElementBean> singleThread = new ArrayList<WorkflowElementBean>();

			singleThread.add(currentElement);

			combinedPaths.add(singleThread);

		}
		return combinedPaths;
	}
}

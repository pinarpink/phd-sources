/**
 * 
 */
package palper.phd.workflow.summary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import palper.phd.workflow.db.OriginalWfdescModelsRepo;
import palper.phd.workflow.wfdesc.SummarizerNamespaces;
import palper.phd.workflow.wfdesc.URIUtils;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * @author pinarpink
 * 
 */
public class WorkflowRewritePrimitives {

	static boolean diffuseOperationDownStream(InfModel model, Resource op,
			Resource wf) {

		Set<Resource> dependentOps = WfDescRdfUtils.getDependentOperations(op,
				model);

		if (dependentOps.isEmpty()) {
			return false;
		}
		if(OriginalWfdescModelsRepo.getInstance()
				.getWorkflowDatabase().get(wf.getURI())
				.getUnactionableProcessUris().contains(op.getURI())){
			return false;
		}
		if (checkNACExists(dependentOps, model)) {
			System.out.println("******************************");
			System.out.println("NEGATIVE APPLICATION CONDITION ENCOUNTERED :"
					+ WfDescRdfUtils.getWorkflowResource(model).getURI()
					+ "Operation" + URIUtils.lastBit(op.getURI()));
			System.out.println("******************************");
			return false;
			// stripMotifAnnotations(op, model);

		} else {

			for (Resource dep : dependentOps) {

				Resource composite = composeOperations(model, op, dep,
						MotifPropagationPolicy.BOTH, dependentOps,wf);
				System.out.println("Added: " + composite.getURI());

				if (OriginalWfdescModelsRepo.getInstance()
						.getWorkflowDatabase().get(wf.getURI())
						.getUnactionableProcessUris().contains(dep.getURI())) {
					OriginalWfdescModelsRepo.getInstance()
							.getWorkflowDatabase().get(wf.getURI())
							.getUnactionableProcessUris()
							.add(composite.getURI());
				}

				removeOperation(dep, model);
				System.out.println("Removed: " + dep.getURI());
			}

			removeOperation(op, model);
			System.out.println("Removed: " + op.getURI());
			return true;
		}

	}

	private static boolean checkNACExists(Set<Resource> dependentOps,
			InfModel model) {
		Set<Resource> combinedLineage = new HashSet<Resource>();

		for (Resource dp : dependentOps) {
			getDownStreamLineage(combinedLineage, dp, model);
		}
		combinedLineage.retainAll(dependentOps);
		return !combinedLineage.isEmpty();
	}

	static boolean diffuseOperationUpStream(InfModel model, Resource op,
			Resource wf) {

		Set<Resource> dependencyOps = WfDescRdfUtils.getNonStrConstantDependencyOperations(
				op, model);

		if (dependencyOps.isEmpty()) {
			return false;
		}
		if(OriginalWfdescModelsRepo.getInstance()
				.getWorkflowDatabase().get(wf.getURI())
				.getUnactionableProcessUris().contains(op.getURI())){
			return false;
		}
		if (dependencyOps.size() > 1) {
			System.out
					.println("########NEGATIVE APPLICAITON CONDITION CANNOT COLLAPSE ON TO MULTIPLE UPSTREAM HOSTS ");

			System.out
					.println("#####################################################");
			return false;

		} else {
			for (Resource dep : dependencyOps) {

				Resource composite = composeOperations(model, dep, op,
						MotifPropagationPolicy.BOTH, dependencyOps,wf);
				System.out.println("Added: " + composite.getURI());

				if (OriginalWfdescModelsRepo.getInstance()
						.getWorkflowDatabase().get(wf.getURI())
						.getUnactionableProcessUris().contains(dep.getURI())) {
					OriginalWfdescModelsRepo.getInstance()
							.getWorkflowDatabase().get(wf.getURI())
							.getUnactionableProcessUris()
							.add(composite.getURI());
				}

				removeOperation(dep, model);
				System.out.println("Removed: " + dep.getURI());
			}
			removeOperation(op, model);
			System.out.println("Removed: " + op.getURI());
			return true;
		}

	}


	private static Resource composeOperations(InfModel model, Resource opA,
			Resource opB, MotifPropagationPolicy policy,
			Set<Resource> ignoreList, Resource wfResource) {

		String comOpName = null;

		if (policy.equals(MotifPropagationPolicy.BOTH)) {

			comOpName = "comp:"
					+ URIUtils.lastBit(URIUtils.lastBit(opA.getURI()) + "_"
							+ URIUtils.lastBit(opB.getURI()));

		} else if (policy.equals(MotifPropagationPolicy.FORMER)) {
			comOpName = "comp:" + URIUtils.lastBit(opA.getURI());

		} else if (policy.equals(MotifPropagationPolicy.LATTER)) {

			comOpName = "comp:" + URIUtils.lastBit(opB.getURI());
		} else {
			comOpName = "comp:" + Math.random();
		}

		System.out.println("New Composite: " + comOpName + " comprised of "
				+ URIUtils.lastBit(opA.getURI()) + " and "
				+ URIUtils.lastBit(opB.getURI()));

		Resource compositeOp = model.createResource(comOpName);

		compositeOp = model.getResource(compositeOp.getURI());
		model.add(compositeOp,
				model.getProperty(SummarizerNamespaces.rdfNS, "type"),
				model.getResource(SummarizerNamespaces.wfdescNS + "Process"));
		model.add(WfDescRdfUtils.getWorkflowResource(model), model.getProperty(
				SummarizerNamespaces.wfdescNS, "hasSubProcess"), compositeOp);

		for (Resource inL : WfDescRdfUtils.getInlinks(opA, model)) {

			cloneInlinkAndAttach(inL, compositeOp, model, wfResource);

		}

		for (Resource outL : WfDescRdfUtils.getOutlinks(opB, model)) {

			cloneOutLinkAndAttach(outL, compositeOp, model, wfResource);

		}

		// list all output links from opA - setAll
		// list all output links of opA that are sinked to opB -
		// setConnectedOut
		Set<Resource> setAllOutLinks = WfDescRdfUtils.getOutlinks(opA, model);
		Set<Resource> setConnectedOutLinks = WfDescRdfUtils
				.getConnectingOutLinksOfOpA(opA, opB, model);

		// setAll MINUS setConnected gives us the output links of opA that
		// need
		// to be
		// carried forward to the composed operation
		setAllOutLinks.removeAll(setConnectedOutLinks);

		for (Resource externalLink : setAllOutLinks) {

			Resource extSink = WfDescRdfUtils.getOperationWithInput(model,
					WfDescRdfUtils.getSink(model, externalLink));
			if (!ignoreList.contains(extSink)) {
				cloneOutLinkAndAttach(externalLink, compositeOp, model,wfResource);
			}
		}

		// list all input datalinks of opB - setAll
		// list all input datalinks of opB that are sourced from opA -
		// setConnectedIn
		Set<Resource> setAllInlinks = WfDescRdfUtils.getInlinks(opB, model);
		Set<Resource> setConnectedInlinks = WfDescRdfUtils
				.getConnectingInLinksOfOpB(opA, opB, model);

		// setAll MINUS setConnected gives us the outputs of opA that need
		// to be
		// carried forward to the composed operation
		setAllInlinks.removeAll(setConnectedInlinks);

		for (Resource externalLink : setAllInlinks) {

			Resource extSource = WfDescRdfUtils.getOperationWithOutput(model,
					WfDescRdfUtils.getSource(model, externalLink));
			if (!ignoreList.contains(extSource)) {
				cloneInlinkAndAttach(externalLink, compositeOp, model, wfResource);
			}
		}

		if (policy.equals(MotifPropagationPolicy.BOTH)) {

			cloneMotifsAndAttach(opA, compositeOp, model);
			cloneMotifsAndAttach(opB, compositeOp, model);

		} else if (policy.equals(MotifPropagationPolicy.FORMER)) {
			cloneMotifsAndAttach(opA, compositeOp, model);

		} else if (policy.equals(MotifPropagationPolicy.LATTER)) {
			cloneMotifsAndAttach(opB, compositeOp, model);

		} else {
			// do nothing regarding motifs
		}
		return compositeOp;

	}

	private static void cloneMotifsAndAttach(Resource opA,
			Resource compositeOp, InfModel model) {

		StmtIterator iter = model.listStatements(opA, null, (RDFNode) null);

		List<Statement> statements = new ArrayList<Statement>();

		while (iter.hasNext()) {
			statements.add(iter.next());
		}

		for (Statement st : statements) {

			if ((st.getPredicate().getNameSpace()
					.equalsIgnoreCase(SummarizerNamespaces.wfdescNS))
					&& (st.getPredicate().getLocalName()
							.equalsIgnoreCase("hasInput"))) {
				continue;

			}
			if ((st.getPredicate().getNameSpace()
					.equalsIgnoreCase(SummarizerNamespaces.wfdescNS))
					&& (st.getPredicate().getLocalName()
							.equalsIgnoreCase("hasOutput"))) {
				continue;

			}
			model.add(compositeOp, st.getPredicate(), st.getObject());

		}

	}

	static InfModel eliminatePort(InfModel model, Resource port) {
		model.removeAll(null, null, port);
		model.removeAll(port, null, null);
		return model;
	}

	private static InfModel cloneInlinkAndAttach(Resource oldLink,
			Resource newOp, InfModel model, Resource wfResource) {

		Resource newPort = null;
		Resource oldSinkPort = WfDescRdfUtils.getSink(model, oldLink);

		
		
		if (OriginalWfdescModelsRepo.getInstance()
	        .getWorkflowDatabase().get(wfResource.getURI()).getOriginalToClone().containsKey(
				oldSinkPort.getURI() + newOp.getURI())) {

			newPort = model.getResource(OriginalWfdescModelsRepo.getInstance()
		        .getWorkflowDatabase().get(wfResource.getURI()).getOriginalToClone().get(
					oldSinkPort.getURI() + newOp.getURI()));

		} else {
			newPort = model.createResource(oldSinkPort.getURI() + "/clone/"
					+ Math.random());
			model.add(newPort,
					model.getProperty(SummarizerNamespaces.rdfNS + "type"),
					model.getResource(SummarizerNamespaces.wfdescNS + "Input"));

			newPort = model.getResource(newPort.getURI());
			model.add(
					newOp,
					model.getProperty(SummarizerNamespaces.wfdescNS
							+ "hasInput"), newPort);

			OriginalWfdescModelsRepo.getInstance()
	        .getWorkflowDatabase().get(wfResource.getURI()).getOriginalToClone().put(
					oldSinkPort.getURI() + newOp.getURI(), newPort.getURI());

			OriginalWfdescModelsRepo.getInstance()
	        .getWorkflowDatabase().get(wfResource.getURI()).getCloneToOriginal().put(newPort.getURI(),
					oldSinkPort.getURI());

		}

		Resource newLink = model.createResource(oldLink.getURI() + "/clone/"
				+ Math.random());
		model.add(newLink,
				model.getProperty(SummarizerNamespaces.rdfNS + "type"),
				model.getResource(SummarizerNamespaces.wfdescNS + "DataLink"));

		model.add(
				WfDescRdfUtils.getWorkflowResource(model),
				model.getProperty(SummarizerNamespaces.wfdescNS, "hasDataLink"),
				newLink);

		model.add(newLink,
				model.getProperty(SummarizerNamespaces.wfdescNS + "hasSink"),
				newPort);

		Statement existingLinkSourceStatement = model.listStatements(oldLink,
				model.getProperty(SummarizerNamespaces.wfdescNS + "hasSource"),
				(RDFNode) null).next();

		model.add(newLink, existingLinkSourceStatement.getPredicate(),
				existingLinkSourceStatement.getObject());

		// }
		// ;
		return model;

		// TODO: cloning of other statements e.g. annotations regarding the old
		// port
	}

	private static InfModel cloneOutLinkAndAttach(Resource oldLink,
			Resource newOp, InfModel model, Resource wfResource) {

		Resource newPort = null;
		Resource oldSorcePort = WfDescRdfUtils.getSource(model, oldLink);
		
		if (OriginalWfdescModelsRepo.getInstance()
	        .getWorkflowDatabase().get(wfResource.getURI()).getOriginalToClone().containsKey(
				oldSorcePort.getURI() + newOp.getURI())) {

			newPort = model.getResource(OriginalWfdescModelsRepo.getInstance()
		        .getWorkflowDatabase().get(wfResource.getURI()).getOriginalToClone().get(
					oldSorcePort.getURI() + newOp.getURI()));

		} else {
			newPort = model.createResource(oldSorcePort.getURI() + "/clone/"
					+ Math.random());
			model.add(newPort,
					model.getProperty(SummarizerNamespaces.rdfNS + "type"),
					model.getResource(SummarizerNamespaces.wfdescNS + "Output"));
			newPort = model.getResource(newPort.getURI());
			model.add(
					newOp,
					model.getProperty(SummarizerNamespaces.wfdescNS
							+ "hasOutput"), newPort);
			OriginalWfdescModelsRepo.getInstance()
	        .getWorkflowDatabase().get(wfResource.getURI()).getOriginalToClone().put(
					oldSorcePort.getURI() + newOp.getURI(), newPort.getURI());

			OriginalWfdescModelsRepo.getInstance()
	        .getWorkflowDatabase().get(wfResource.getURI()).getCloneToOriginal().put(newPort.getURI(),
					oldSorcePort.getURI());

		}

		Resource newLink = model.createResource(oldLink.getURI() + "/clone/"
				+ Math.random());
		model.add(newLink,
				model.getProperty(SummarizerNamespaces.rdfNS + "type"),
				model.getResource(SummarizerNamespaces.wfdescNS + "DataLink"));
		model.add(
				WfDescRdfUtils.getWorkflowResource(model),
				model.getProperty(SummarizerNamespaces.wfdescNS, "hasDataLink"),
				newLink);

		model.add(newLink,
				model.getProperty(SummarizerNamespaces.wfdescNS + "hasSource"),
				newPort);

		Statement existingLinkSinkStatement = model.listStatements(oldLink,
				model.getProperty(SummarizerNamespaces.wfdescNS + "hasSink"),
				(RDFNode) null).next();

		model.add(newLink, existingLinkSinkStatement.getPredicate(),
				existingLinkSinkStatement.getObject());

		// }
		// ;
		return model;

		// TODO: cloning of other statements e.g. annotations regarding the old
		// port

	}

	private static void getDownStreamLineage(Set<Resource> lineage,
			Resource opA, InfModel model) {

		Set<Resource> immediate = WfDescRdfUtils.getDependentOperations(opA,
				model);
		lineage.addAll(immediate);

		for (Resource dep : immediate) {

			getDownStreamLineage(lineage, dep, model);

		}

		return;

	}


	private static void removeOperation(Resource op, InfModel model) {

		for (Resource link : WfDescRdfUtils.getInlinks(op, model)) {

			model.removeAll(null, null, link);
			model.removeAll(link, null, null);

		}

		for (Resource link : WfDescRdfUtils.getOutlinks(op, model)) {

			model.removeAll(null, null, link);
			model.removeAll(link, null, null);

		}
		for (Resource inp : WfDescRdfUtils.getInputPorts(op, model)) {

			model.removeAll(null, null, inp);
			model.removeAll(inp, null, null);

		}

		for (Resource outp : WfDescRdfUtils.getOutputPorts(op, model)) {

			model.removeAll(null, null, outp);
			model.removeAll(outp, null, null);

		}

		model.removeAll(null, null, op);
		model.removeAll(op, null, null);
	}

	// /////////////////////////////////////////////////////////////////////////////
	static boolean eliminateOperation(InfModel model, Resource op) {

		Set<Resource> inLinks = WfDescRdfUtils.getInlinks(op, model);
		Set<Resource> outLinks = WfDescRdfUtils.getOutlinks(op, model);
		for (Resource inlink : inLinks) {
			for (Resource outlink : outLinks) {

				Resource sourcePort = WfDescRdfUtils.getSource(model, inlink);
				Resource targetPort = WfDescRdfUtils.getSink(model, outlink);
				linkPorts(model, sourcePort, targetPort);

			}
		}

		removeOperation(op, model);
		return true;
	}

	private static Resource linkPorts(InfModel model, Resource portA,
			Resource portB) {
		Resource newLink = model.createResource("http://cloneLink/"
				+ Math.random());
		model.add(newLink,
				model.getProperty(SummarizerNamespaces.rdfNS + "type"),
				model.getResource(SummarizerNamespaces.wfdescNS + "DataLink"));

		model.add(
				WfDescRdfUtils.getWorkflowResource(model),
				model.getProperty(SummarizerNamespaces.wfdescNS, "hasDataLink"),
				newLink);

		model.add(newLink,
				model.getProperty(SummarizerNamespaces.wfdescNS + "hasSource"),
				portA);

		model.add(newLink,
				model.getProperty(SummarizerNamespaces.wfdescNS + "hasSink"),
				portB);
		model.addLiteral(newLink,
				model.getProperty(SummarizerNamespaces.rdfsNS + "comment"),
				"tracelink");
		return newLink;

	}

}

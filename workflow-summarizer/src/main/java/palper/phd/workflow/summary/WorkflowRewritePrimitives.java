/**
 * 
 */
package palper.phd.workflow.summary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import palper.phd.workflow.wfdesc.SummarizerNamespaces;
import palper.phd.workflow.wfdesc.URIUtils;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * @author pinarpink
 * 
 */
public class WorkflowRewritePrimitives {

	static Model diffuseOperationDownStream(InfModel model, Resource op) {

		Set<Resource> dependentOps = getDependentOperations(op, model);

		Set<Resource> combinedLineage = new HashSet<Resource>();

		for (Resource dp : dependentOps) {
			getDownStreamLineage(combinedLineage, dp, model);
		}
		combinedLineage.retainAll(dependentOps);
		if (combinedLineage.isEmpty()) {

			for (Resource dep : dependentOps) {

				composeOperations(model, op, dep,
						MotifPropagationPolicy.LATTER, dependentOps);
				removeOperation(dep, model);
			}

			removeOperation(op, model);
		} else {
			System.out.println("******************************");
			System.out.println("VIOLATION :"
					+ WfDescRdfUtils.getWorkflowResource(model).getURI()
					+ "Operation" + URIUtils.lastBit(op.getURI()));
			System.out.println("******************************");
			stripMotifAnnotations(op, model);
		}
		return model;
	}

	private static InfModel stripMotifAnnotations(Resource op, InfModel model) {

		StmtIterator iter = model.listStatements(
				op,
				model.getProperty(SummarizerNamespaces.motifsNS
						+ "hasOperationMotif"), (RDFNode) null);

		List<Statement> stmtList = new ArrayList<Statement>();
		while (iter.hasNext()) {
			stmtList.add(iter.next());
		}
		for (Statement st : stmtList) {
			model.remove(st);
		}
		return model;
	}

	static Model diffuseOperationUpStream(InfModel model, Resource op) {

		Set<Resource> dependencyOps = getDependencyOperations(op, model);

		Set<Resource> combinedLineage = new HashSet<Resource>();

		for (Resource dp : dependencyOps) {
			getUpStreamLineage(combinedLineage, dp, model);
		}
		combinedLineage.retainAll(dependencyOps);
		if (combinedLineage.isEmpty()) {
			
			if (dependencyOps.size()>1){
			System.out.println("#####################################################");
			System.out.println("############### MULTI-OP COLLAPSE UPSTREAM#######");
			System.out.println("#####################################################");
			
			}
			for (Resource dep : dependencyOps) {

				composeOperations(model, dep, op, MotifPropagationPolicy.FORMER, dependencyOps);
				removeOperation(dep, model);
			}
			removeOperation(op, model);
		} else {

			System.out.println("******************************");
			System.out.println("VIOLATION :"
					+ WfDescRdfUtils.getWorkflowResource(model).getURI()
					+ "Operation" + URIUtils.lastBit(op.getURI()));
			System.out.println("******************************");
			stripMotifAnnotations(op, model);
		
		}

		return model;
	}

	static Model combineOperations(InfModel model, Resource opA, Resource opB,
			MotifPropagationPolicy policy) {

		composeOperations(model, opA, opB, policy, new HashSet<Resource>());

		removeOperation(opA, model);
		removeOperation(opB, model);

		return model;

	}

	private static Resource composeOperations(InfModel model, Resource opA,
			Resource opB, MotifPropagationPolicy policy,
			Set<Resource> ignoreList) {

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

			cloneInlinkAndAttach(inL, compositeOp, model);

		}

		for (Resource outL : WfDescRdfUtils.getOutlinks(opB, model)) {

			cloneOutLinkAndAttach(outL, compositeOp, model);

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

			Resource extSink = WfDescRdfUtils.getOperationWithInput(model, WfDescRdfUtils.getSink(model, externalLink));
			if (!ignoreList.contains(extSink)) {
				cloneOutLinkAndAttach(externalLink, compositeOp, model);
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

			Resource extSource = WfDescRdfUtils.getOperationWithOutput(model,WfDescRdfUtils.getSource(model, externalLink));
			if (!ignoreList.contains(extSource)) {
				cloneInlinkAndAttach(externalLink, compositeOp, model);
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
			Resource newOp, InfModel model) {

		Resource newPort = null;
		Resource oldSinkPort = WfDescRdfUtils.getSink(model, oldLink);

		if (RuleExecuter.getOriginalToClone().containsKey(
				oldSinkPort.getURI() + newOp.getURI())) {

			newPort = model.getResource(RuleExecuter.getOriginalToClone().get(
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

			RuleExecuter.getOriginalToClone().put(
					oldSinkPort.getURI() + newOp.getURI(), newPort.getURI());
			
			RuleExecuter.getCloneToOriginal().put(newPort.getURI(), oldSinkPort.getURI());

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
			Resource newOp, InfModel model) {

		Resource newPort = null;
		Resource oldSorcePort = WfDescRdfUtils.getSource(model, oldLink);
		if (RuleExecuter.getOriginalToClone().containsKey(
				oldSorcePort.getURI() + newOp.getURI())) {

			newPort = model.getResource(RuleExecuter.getOriginalToClone().get(
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
			RuleExecuter.getOriginalToClone().put(
					oldSorcePort.getURI() + newOp.getURI(), newPort.getURI());
			
			RuleExecuter.getCloneToOriginal().put(newPort.getURI(), oldSorcePort.getURI());

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

	private static void getDownStreamLineage(Set<Resource> lineage, Resource opA,
			InfModel model) {

		
		Set<Resource> immediate = getDependentOperations(opA, model);
		lineage.addAll(immediate);

		for (Resource dep : immediate) {

			getDownStreamLineage(lineage, dep, model);

		}

		return;

	}

	private static void getUpStreamLineage(Set<Resource> lineage, Resource opA, InfModel model) {

		
		Set<Resource> immediate = getDependencyOperations(opA, model);
		lineage.addAll(immediate);

		for (Resource dep : immediate) {

			
			getUpStreamLineage(lineage, dep, model);
			//lineage.addAll(getDependencyOperations(dep, model));

		}

		return;

	}

	private static Set<Resource> getDependentOperations(Resource opA,
			InfModel model) {

		Set<Resource> result = new HashSet<Resource>();

		String queryStr = " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
				+ " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ " SELECT DISTINCT ?opB  \n"
				+ " WHERE { \n"
				+ " <"
				+ opA.getURI()
				+ "> wfdesc:hasOutput ?outA . \n"
				+ " ?link a wfdesc:DataLink . \n"
				+ " ?link wfdesc:hasSource ?outA . \n"
				+ " ?link wfdesc:hasSink ?inB . \n"
				+ " ?opB  wfdesc:hasInput ?inB . \n" + " } \n";

		Query query = QueryFactory.create(queryStr);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		try {
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {

				QuerySolution soln = results.nextSolution();

				result.add(soln.getResource("opB"));

			}
		} finally {
			qexec.close();
		}
		return result;
	}

	private static Set<Resource> getDependencyOperations(Resource opA,
			InfModel model) {

		Set<Resource> result = new HashSet<Resource>();

		String queryStr = " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
				+ " PREFIX wf4ever: <http://purl.org/wf4ever/wf4ever#> \n"
				+ " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ " SELECT DISTINCT ?opD  \n"
				+ " WHERE { \n"
				+ " ?opD wfdesc:hasOutput ?outD . \n"
				+ " <"
				+ opA.getURI()
				+ "> wfdesc:hasInput ?inA . \n"
				+ " ?link a wfdesc:DataLink . \n"
				+ " ?link wfdesc:hasSource ?outD . \n"
				+ " ?link wfdesc:hasSink ?inA . \n"
				+ "FILTER (NOT EXISTS { ?opD a wf4ever:StringConstant } ) \n"
				+ " } \n";

		Query query = QueryFactory.create(queryStr);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		try {
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {

				QuerySolution soln = results.nextSolution();

				result.add(soln.getResource("opD"));

			}
		} finally {
			qexec.close();
		}
		return result;
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
	///////////////////////////////////////////////////////////////////////////////
	static Model eliminateOperation(InfModel model, Resource op) {

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
		return model;
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
		model.addLiteral(newLink, model.getProperty(SummarizerNamespaces.rdfsNS + "comment"), "tracelink");
		return newLink;

	}

}

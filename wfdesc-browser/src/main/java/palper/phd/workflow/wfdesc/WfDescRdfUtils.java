/**
 * 
 */
package palper.phd.workflow.wfdesc;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * @author pinarpink
 * 
 */
public class WfDescRdfUtils {

  public static Set<Resource> getInputPorts(Resource op, Model model) {
    Set<Resource> res = new HashSet<Resource>();
    for (RDFNode nd : model.listObjectsOfProperty(op,
        model.getProperty(SummarizerNamespaces.wfdescNS + "hasInput")).toList()) {
      res.add((Resource) nd);
    };
    return res;
  }

  public static Set<Resource> getDependentOperations(Resource opA, Model model) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?opB  \n" + " WHERE { \n" + " <" + opA.getURI()
            + "> wfdesc:hasOutput ?outA . \n" + " ?link a wfdesc:DataLink . \n"
            + " ?link wfdesc:hasSource ?outA . \n" + " ?link wfdesc:hasSink ?inB . \n"
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

  public static Resource getSpecializedWorkflowDefinition(Resource wfProcessorResource, Model model) {

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX prov: <http://www.w3.org/ns/prov#> \n" + " SELECT DISTINCT ?wfDef  \n"
            + " WHERE { \n" + " <" + wfProcessorResource.getURI()
            + "> prov:specializationOf ?wfDef . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      if (results.hasNext()) {

        QuerySolution soln = results.nextSolution();

        return soln.getResource("wfDef");

      } else {

        return null;
      }
    } finally {
      qexec.close();
    }

  }


  public static Set<Resource> getNonStrConstantDependencyOperations(Resource opA, Model model) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX lblwf: <http://www.semanticweb.org/pinarpink/ontologies/2013/10/labelingwf#>\n"
            + " SELECT DISTINCT ?opB  \n" + " WHERE { \n" + " ?opB wfdesc:hasOutput ?outB . \n"
            + " ?link a wfdesc:DataLink . \n" + " ?link wfdesc:hasSource ?outB . \n"
            + " ?link wfdesc:hasSink ?inA . \n" + " <" + opA.getURI() + ">"
            + " wfdesc:hasInput ?inA . \n"
            + " FILTER (NOT EXISTS { ?opB a lblwf:StringConstant }) \n" + " } \n";

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

  public static Set<Resource> getDependencyOperations(Resource opA, Model model) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?opB  \n" + " WHERE { \n" + " ?opB wfdesc:hasOutput ?outB . \n"
            + " ?link a wfdesc:DataLink . \n" + " ?link wfdesc:hasSource ?outB . \n"
            + " ?link wfdesc:hasSink ?inA . \n" + " <" + opA.getURI() + ">"
            + " wfdesc:hasInput ?inA . \n" + " } \n";

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


  public static boolean datalinkExists(Resource sourcePort, Resource sinkPort, Model model) {


    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?link  \n" + " WHERE { \n" + " ?link a wfdesc:DataLink . \n"
            + " ?link wfdesc:hasSource " + "<" + sourcePort.getURI() + ">" + "  . \n"
            + " ?link wfdesc:hasSink " + "<" + sinkPort.getURI() + ">" + "  . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);

    boolean exists = false;
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();
        // we're nt really interested in the soln
        // we re just moving the iterator
        exists = true;

      }
    } finally {
      qexec.close();
    }
    return exists;
  }


  public static Set<Resource> getUpstreamWorkflowInputs(Resource opA, Model model) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX wf4ever: <http://purl.org/wf4ever/wf4ever#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?win  \n" + " WHERE { \n" + " ?wf wfdesc:hasInput ?win . \n"
            + " ?link a wfdesc:DataLink . \n" + " ?link wfdesc:hasSource ?win . \n"
            + " ?link wfdesc:hasSink ?inA . \n" + "<" + opA.getURI() + ">"
            + " wfdesc:hasInput  ?inA . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("win"));

      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static boolean isTraceLink(Resource link, Model model) {


    StmtIterator iter =
        model.listStatements(link, model.getProperty(SummarizerNamespaces.rdfsNS, "comment"),
            (RDFNode) null);

    if (iter.hasNext()) {
      return true;
    } else {
      return false;
    }


  }

  
  public static boolean isTopLevelVisible(Resource port, Model model){
    
    StmtIterator iter =
        model.listStatements(port, model.getProperty(SummarizerNamespaces.motifsNS, "topLevelVisible"),
            (RDFNode) null);

    if (iter.hasNext()) {
      return true;
    } else {
      return false;
    }
  }
  
  public static boolean isBookmarked(Resource port, Model model){
    
    StmtIterator iter =
        model.listStatements(port, model.getProperty(SummarizerNamespaces.motifsNS, "bookmarked"),
            (RDFNode) null);

    if (iter.hasNext()) {
      return true;
    } else {
      return false;
    }
  }
  public static String getOriginalPortUri(Resource clonePort, Model model) {

    StmtIterator iter =
        model.listStatements(clonePort,
            model.getProperty(SummarizerNamespaces.motifsNS, "cloneOf"), (RDFNode) null);

    if (iter.hasNext()) {
      return (String) iter.next().getObject().asLiteral().getValue();
    } else {
      return null;
    }
  }

  public static int getDepth(Resource port, Model model) {

    // .scuflNS, "portDepth"
    StmtIterator iter =
        model.listStatements(port,
            model.getProperty(SummarizerNamespaces.labelingExtNS, "definedDepth"), (RDFNode) null);

    if (iter.hasNext()) {
      return iter.next().getLiteral().getInt();
    } else {
      return -1;
    }


  }


  public static String getLabel(Resource element, Model model) {

    StmtIterator iter =
        model.listStatements(element, model.getProperty(SummarizerNamespaces.rdfsNS, "label"),
            (RDFNode) null);

    if (iter.hasNext()) {
      return iter.next().getLiteral().getString();
    } else {
      return null;
    }
  }

  public static Set<Resource> getOutputPorts(Resource op, Model model) {
    Set<Resource> res = new HashSet<Resource>();
    for (RDFNode nd : model.listObjectsOfProperty(op,
        model.getProperty(SummarizerNamespaces.wfdescNS + "hasOutput")).toList()) {
      res.add((Resource) nd);
    };
    return res;
  }


  public static Set<Resource> getInlinks(Resource op, InfModel model) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n" + " SELECT ?link  \n"
            + " WHERE { \n" + " ?link a wfdesc:DataLink . \n"
            + " ?link wfdesc:hasSource ?source . \n" + " ?link wfdesc:hasSink ?inB . \n" + " <"
            + op.getURI() + ">  wfdesc:hasInput ?inB . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("link"));
      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static Set<Resource> getProcessors(Model model, Resource workflowResource) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n" + " SELECT DISTINCT ?op  \n"
            + " WHERE { \n" + " <" + workflowResource.getURI() + "> wfdesc:hasSubProcess ?op . \n"
            + " ?op a wfdesc:Process . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("op"));

      }
    } finally {
      qexec.close();
    }
    return result;
  }


  public static Resource getEnclosingWorkflow(Model model, Resource processorResource) {


    Resource result = null;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n" + " SELECT DISTINCT ?wf  \n"
            + " WHERE { \n" + " ?wf wfdesc:hasSubProcess <" + processorResource.getURI() + "> . \n"
            + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result = soln.getResource("wf");

      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static Set<Resource> getSubWorkflows(Model model, Resource workflowResource) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?subWf  \n" + " WHERE { \n" + " <" + workflowResource.getURI()
            + "> wfdesc:hasSubProcess ?subWf . \n" + " ?subWf a wfdesc:Workflow . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("subWf"));

      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static Set<Resource> getMotifAnnotatedProcessors(InfModel model,
      Resource workflowResource, Set<String> motifSet) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n" + " SELECT DISTINCT ?op  \n"
            + " WHERE { \n";
    int count = 1;
    for (String mtf : motifSet) {
      queryStr =
          queryStr + "{ <" + workflowResource.getURI() + "> wfdesc:hasSubProcess ?op . \n"
              + " ?op a wfdesc:Process . \n" + " ?op motifs:hasDataOperationMotif ?mtf . \n"
              + " ?mtf a " + "<" + mtf + "> } \n";

      if (count < motifSet.size()) {
        queryStr = queryStr + " UNION \n";
      }
      count++;
    }

    queryStr = queryStr + "} \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("op"));

      }
    } finally {
      qexec.close();
    }
    return result;
  }


  public static Set<Resource> getDataLinks(Model model, Resource workflowResource) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n" + " SELECT DISTINCT ?link  \n"
            + " WHERE { \n" + " ?link a wfdesc:DataLink . \n" + " <" + workflowResource.getURI()
            + "> wfdesc:hasDataLink ?link . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("link"));

      }
    } finally {
      qexec.close();
    }
    return result;
  }


  public static Set<Resource> getAllBlockingControlLinks(Model model, Resource workflowResource) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n"
            + " PREFIX lblwf: <http://www.semanticweb.org/pinarpink/ontologies/2013/10/labelingwf#>\n"
            + " SELECT DISTINCT ?link  \n" + " WHERE { \n"
            + " ?link a lblwf:BlockingControlLink . \n" + " <" + workflowResource.getURI()
            + "> lblwf:hasBlockingControlLink ?link . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("link"));

      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static Set<Resource> getBlockingControlLinksWihSink(Model model, Resource sinkOperation) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX lblwf: <http://www.semanticweb.org/pinarpink/ontologies/2013/10/labelingwf#>\n"
            + " SELECT DISTINCT ?link  \n" + " WHERE { \n"
            + "?link a lblwf:BlockingControlLink . \n" + " ?link lblwf:hasControlSink " + "<"
            + sinkOperation.getURI() + "> . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("link"));

      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static Set<Resource> getDataLinksWithSourcePort(Model model, Resource portResource) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?link  \n" + " WHERE { \n" + " ?link a wfdesc:DataLink . \n"
            + " ?link wfdesc:hasSource <" + portResource.getURI() + "> . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("link"));

      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static Set<Resource> getDataLinksWithSinkPort(Model model, Resource portResource) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?link  \n" + " WHERE { \n" + " ?link a wfdesc:DataLink . \n"
            + " ?link wfdesc:hasSink <" + portResource.getURI() + "> . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("link"));

      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static Resource getSource(Model model, Resource linkResource) {

    Resource result = null;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n"
            + " SELECT DISTINCT ?source  \n" + " WHERE { \n" + " <" + linkResource.getURI()
            + "> wfdesc:hasSource ?source . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result = soln.getResource("source");

      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static Resource getSink(Model model, Resource linkResource) {

    Resource result = null;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n" + " SELECT DISTINCT ?sink  \n"
            + " WHERE { \n" + " <" + linkResource.getURI() + "> wfdesc:hasSink ?sink . \n"
            + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result = soln.getResource("sink");

      }
    } finally {
      qexec.close();
    }
    return result;
  }


  // /

  public static Resource getControlSource(Model model, Resource controlLinkResource) {

    Resource result = null;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n"
            + " PREFIX lblwf: <http://www.semanticweb.org/pinarpink/ontologies/2013/10/labelingwf#>\n"
            + " SELECT DISTINCT ?source  \n" + " WHERE { \n" + " <" + controlLinkResource.getURI()
            + "> lblwf:hasControlSource ?source . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result = soln.getResource("source");

      }
    } finally {
      qexec.close();
    }
    return result;
  }

  // /
  public static Resource getControlSink(Model model, Resource controlLinkResource) {

    Resource result = null;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n"
            + " PREFIX lblwf: <http://www.semanticweb.org/pinarpink/ontologies/2013/10/labelingwf#>\n"
            + " SELECT DISTINCT ?sink  \n" + " WHERE { \n" + " <" + controlLinkResource.getURI()
            + "> lblwf:hasControlSink ?sink . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result = soln.getResource("sink");

      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static Set<Resource> getConnectingOutLinksOfOpA(Resource opA, Resource opB, InfModel model) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?lnk  \n" + " WHERE { \n" + " <" + opA.getURI()
            + "> wfdesc:hasOutput ?outA . \n" + " ?lnk a wfdesc:DataLink . \n"
            + " ?lnk wfdesc:hasSource ?outA . \n" + " ?lnk wfdesc:hasSink ?inB . \n" + " <"
            + opB.getURI() + ">  wfdesc:hasInput ?inB . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("lnk"));
      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static Set<Resource> getConnectingInLinksOfOpB(Resource opA, Resource opB, InfModel model) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?lnk  \n" + " WHERE { \n" + " <" + opA.getURI()
            + "> wfdesc:hasOutput ?outA . \n" + " ?lnk a wfdesc:DataLink . \n"
            + " ?lnk wfdesc:hasSource ?outA . \n" + " ?lnk wfdesc:hasSink ?inB . \n" + " <"
            + opB.getURI() + ">  wfdesc:hasInput ?inB . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("lnk"));

      }
    } finally {
      qexec.close();
    }
    return result;
  }


  public static Set<Resource> getBookmarkedIntermediaryPorts(Resource wfResource, Model model) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?lnk1  \n" + " WHERE { \n" + " <" + wfResource.getURI()
            + ">  wfdesc:hasSubProcess ?proc . \n" + " ?proc wfdesc:hasOutput ?port . \n"
            + " ?lnk1 a wfdesc:DataLink . \n" + " ?lnk1 wfdesc:hasSource ?port . \n"
            + " ?lnk1 wfdesc:hasSink ?wfOut1 . \n" + " <" + wfResource.getURI()
            + ">  wfdesc:hasOutput ?wfOut1 . \n" + " ?lnk2 wfdesc:hasSource ?port . \n"
            + " ?lnk2 wfdesc:hasSink ?someInPort . \n" + " FILTER ( ?lnk1 != ?lnk2 ) . \n "
            + " FILTER (NOT EXISTS { " + " <" + wfResource.getURI()
            + ">  wfdesc:hasOutput ?someInPort} )" + "} \n";


    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("lnk1"));

      }
    } finally {
      qexec.close();
    }
    return result;
  }


  public static Set<Resource> getOutlinks(Resource op, Model model) {

    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n" + " SELECT ?link  \n"
            + " WHERE { \n" + " ?link a wfdesc:DataLink . \n"
            + " ?link wfdesc:hasSource ?outB . \n" + " ?link wfdesc:hasSink ?source . \n" + " <"
            + op.getURI() + ">  wfdesc:hasOutput ?outB . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("link"));
      }
    } finally {
      qexec.close();
    }
    return result;
  }


  public static Resource getOperationWithInput(Model model, Resource input) {

    Resource result = null;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n" + " SELECT DISTINCT ?op  \n"
            + " WHERE { \n ?op wfdesc:hasInput  <" + input.getURI() + "> .\n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result = soln.getResource("op");

      }
    } finally {
      qexec.close();
    }
    return result;
  }


  public static Resource getOperationWithOutput(Model model, Resource output) {

    Resource result = null;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n" + " SELECT DISTINCT ?op  \n"
            + " WHERE { \n ?op wfdesc:hasOutput  <" + output.getURI() + "> .\n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result = soln.getResource("op");

      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static Resource getWorkflowResource(Model model) {

    Resource result = null;
    StmtIterator iter =
        model.listStatements(null, model.getProperty(SummarizerNamespaces.rdfNS, "type"),
            model.getResource(SummarizerNamespaces.wfdescNS + "Workflow"));


    Map<String, String> mp = model.getNsPrefixMap();
    String baseURI = model.getNsPrefixURI("");
    if (baseURI == null) {
      baseURI = model.getNsPrefixURI("base");
    }

    if (baseURI.endsWith("#")) {
      // remove the trailing # character
      baseURI = baseURI.substring(0, baseURI.length() - 1);

    }
    while (iter.hasNext()) {
      Statement st = iter.next();
      Resource wfResource = st.getSubject();
      if (wfResource.getURI().equals(baseURI))
        result = wfResource;

    }
    return result;
  }

  public static boolean isWorkflowInputPort(Resource resource, Model model) {

    boolean result = false;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n" + " SELECT DISTINCT ?wf  \n"
            + " WHERE { \n" + " ?wf wfdesc:hasInput  <" + resource.getURI() + "> . \n"
            + "?wf a wfdesc:Workflow .\n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      if (results.hasNext()) {
        result = true;
      } else {
        // not a workflow input
      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static boolean isNestedWorkflow(Resource wfResource, Model model) {

    boolean result = false;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n" + " SELECT DISTINCT ?sub  \n"
            + " WHERE { \n" + " <" + wfResource.getURI() + ">  a wfdesc:Workflow .\n"
            + "?wf wfdesc:hasSubProcess ?sub . \n" + "?sub a wfdesc:Workflow . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      if (results.hasNext()) {
        result = true;
      } else {
        // not a workflow input
      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static boolean isHybridPort(Resource resource, Model model) {

    boolean result = false;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n" + " SELECT ?op1 ?op2  \n"
            + " WHERE { \n" + " ?op1 wfdesc:hasInput  <" + resource.getURI() + "> . \n"
            + "?op2 wfdesc:hasOutput <" + resource.getURI() + "> . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      if (results.hasNext()) {
        result = true;
      } else {
        // not a hybrid
      }
    } finally {
      qexec.close();
    }
    return result;
  }


  public static boolean isInputPort(Resource resource, Model model) {

    boolean result = false;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n" + " SELECT ?op1   \n"
            + " WHERE { \n" + " ?op1 wfdesc:hasInput  <" + resource.getURI() + "> . \n"
            + " FILTER (NOT EXISTS { " + "?op2 wfdesc:hasOutput <" + resource.getURI() + "> }) \n"
            + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      if (results.hasNext()) {
        result = true;
      } else {
        // not a hybrid
      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static boolean isOutputPort(Resource resource, Model model) {

    boolean result = false;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n" + " SELECT ?op1   \n"
            + " WHERE { \n" + " ?op1 wfdesc:hasOutput  <" + resource.getURI() + "> . \n"
            + " FILTER (NOT EXISTS { " + "?op2 wfdesc:hasInput <" + resource.getURI() + "> }) \n"
            + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      if (results.hasNext()) {
        result = true;
      } else {
        // not a hybrid
      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static boolean isWorkflowOutputPort(Resource resource, Model model) {

    boolean result = false;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n" + " SELECT DISTINCT ?wf  \n"
            + " WHERE { \n" + " ?wf wfdesc:hasOutput  <" + resource.getURI() + "> . \n"
            + "?wf a wfdesc:Workflow .\n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      if (results.hasNext()) {
        result = true;
      } else {
        // not a workflow input
      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static boolean isDataLink(Model model, Resource processor) {
    StmtIterator iter =
        model.listStatements(processor, model.getProperty(SummarizerNamespaces.rdfNS, "type"),
            model.getResource(SummarizerNamespaces.wfdescNS + "DataLink"));

    if (iter.hasNext()) {
      return true;

    } else {
      return false;
    }

  }

  public static boolean isOperation(Model model, Resource processor) {
    StmtIterator iter =
        model.listStatements(processor,
            model.getProperty(SummarizerNamespaces.labelingExtNS, "type"),
            model.getResource(SummarizerNamespaces.wfdescNS + "Process"));

    if (iter.hasNext()) {
      return true;

    } else {
      return false;
    }

  }

  public static boolean hasLhbFormula(Model model, Resource processor) {
    return model.contains(processor,
        model.getProperty(SummarizerNamespaces.labelingExtNS, "lhbFormula"));


  }

  public static String getLhbFormulaJSON(Model model, Resource processor) {

    Literal result = null;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n"
            + " PREFIX lblwf: <http://www.semanticweb.org/pinarpink/ontologies/2013/10/labelingwf#>\n"
            + " SELECT  ?val \n" + " WHERE { \n"
            // + " ?proc a lblwf:StringConstant . \n"
            + "<" + processor.getURI() + ">  lblwf:lhbFormula ?val . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result = soln.getLiteral("val");

      }
    } finally {
      qexec.close();
    }
    return (String) result.getValue();

  }


  public static boolean isStringConstant(Model model, Resource processor) {
    StmtIterator iter =
        model.listStatements(processor, model.getProperty(SummarizerNamespaces.rdfNS, "type"),
            model.getResource(SummarizerNamespaces.labelingExtNS + "StringConstant"));
    StmtIterator iter2 =
        model.listStatements(processor, model.getProperty(SummarizerNamespaces.rdfNS, "type"),
            model.getResource(SummarizerNamespaces.wf4everNS + "StringConstant"));

    if (iter.hasNext() || iter2.hasNext()) {
      return true;

    } else {
      return false;
    }

  }

  public static String getStringConstantValue(Model model, Resource constantTypeProcessor) {

    Literal result = null;

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n"
            + " PREFIX lblwf: <http://www.semanticweb.org/pinarpink/ontologies/2013/10/labelingwf#>\n"
            + " SELECT  ?val \n" + " WHERE { \n"
            // + " ?proc a lblwf:StringConstant . \n"
            + "<" + constantTypeProcessor.getURI() + ">  lblwf:constantValue ?val . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result = soln.getLiteral("val");

      }
    } finally {
      qexec.close();
    }
    return (String) result.getValue();

  }

  public static Resource getInputWithLabel(String wfInPortName, Resource operation, Model model) {

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX prov: <http://www.w3.org/ns/prov#> \n" + " SELECT DISTINCT ?inp  \n"
            + " WHERE { \n" + " <" + operation.getURI() + "> wfdesc:hasInput  ?inp . \n"
            + "?inp rdfs:label \"" + wfInPortName + "\". \n" + "} \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      if (results.hasNext()) {

        QuerySolution soln = results.nextSolution();

        return soln.getResource("inp");

      } else {

        return null;
      }
    } finally {
      qexec.close();
    }
  }

  public static Resource getOutputWithLabel(String wfOutPortName, Resource operation, Model model) {

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX prov: <http://www.w3.org/ns/prov#> \n" + " SELECT DISTINCT ?out  \n"
            + " WHERE { \n" + " <" + operation.getURI() + "> wfdesc:hasOutput  ?out . \n"
            + "?out rdfs:label \"" + wfOutPortName + "\". \n" + "  } \n";
    //
    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      if (results.hasNext()) {

        QuerySolution soln = results.nextSolution();

        return soln.getResource("out");

      } else {

        return null;
      }
    } finally {
      qexec.close();
    }
  }


  
  public static Set<Resource> getNonStrNonWfProcessorsOutputPorts(Model model, Resource wfResource) {
    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " PREFIX motifs:  <http://purl.org/net/wf-motifs#>\n"
            + " PREFIX lblwf: <http://www.semanticweb.org/pinarpink/ontologies/2013/10/labelingwf#>\n"
            + " SELECT DISTINCT ?outp  \n" + " WHERE { \n" + " <" + wfResource.getURI()
            + "> wfdesc:hasSubProcess ?op . \n" + " ?op a wfdesc:Process . \n"
            + " ?op wfdesc:hasOutput ?outp . \n"
            + " FILTER (NOT EXISTS { ?op a lblwf:StringConstant }) . \n"
            + " FILTER (NOT EXISTS { ?op a wfdesc:Workflow }) . \n" + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("outp"));

      }
    } finally {
      qexec.close();
    }
    return result;
  }

}

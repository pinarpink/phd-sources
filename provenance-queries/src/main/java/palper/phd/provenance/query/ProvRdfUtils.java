/**
 * 
 */
package palper.phd.provenance.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import palper.phd.labeling.model.LabelDefinitonBean;
import palper.phd.labeling.model.LabelInstanceBean;
import palper.phd.labeling.model.XSDatatypeEnum;
import palper.phd.workflow.wfdesc.SummarizerNamespaces;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * @author pinarpink
 * 
 */
public class ProvRdfUtils {



  public static Set<Resource> getArtifactsDescribedByParameter(String parameterNameUriString,
      Model model) {

    Set<Resource> result = new HashSet<Resource>();

    String usageQueryStr =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX wfprov:    <http://purl.org/wf4ever/wfprov#> \n"
            + " SELECT DISTINCT ?entit1  \n" 
            + " WHERE { \n"
            + " ?entit1 wfprov:describedByParameter" 
            + " <" + parameterNameUriString + "> . \n"
            + " } \n";

    Query usageQuery = QueryFactory.create(usageQueryStr);

    QueryExecution usageQueryExec = QueryExecutionFactory.create(usageQuery, model);

    try {
      ResultSet results = usageQueryExec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("entit1"));
      }

    } finally {
      usageQueryExec.close();

    }
    return result;
  }

 public static Set<Resource> getEntitiesWithLabel(String labelUriString, String labelValue, Model model){
   
    
    Set<Resource> result = new HashSet<Resource>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
        +"PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n" 
        + " SELECT DISTINCT ?data  \n"
            + " WHERE { \n" 
            + " ?data a prov:Entity . \n"
            + " ?data <" +labelUriString+"> \""+labelValue+"\" . \n"
            + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, model);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("data"));
      }
    } finally {
      qexec.close();
    }
    return result;
    
    
  }
  public static String getInlineContentforDataArtifact(String dataArtifactUriString, Model model){
    String result = null;

    String contentQueryStr =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX tavernaprov:  <http://ns.taverna.org.uk/2012/tavernaprov/> \n"
            + " PREFIX cnt: <http://www.w3.org/2011/content#>"
            + " SELECT DISTINCT ?contenttext  \n" 
            + " WHERE { \n" 
            + " <" 
            + dataArtifactUriString
            + "> tavernaprov:content  ?cont . \n" 
            + "?cont a  cnt:ContentAsText . \n"
            + "?cont cnt:chars ?contenttext . \n"
            + " } \n";

    Query generationQuery = QueryFactory.create(contentQueryStr);
    QueryExecution generationQueryExec = QueryExecutionFactory.create(generationQuery, model);

    try {

      ResultSet resultsGeneration = generationQueryExec.execSelect();

      if (resultsGeneration.hasNext()) {

        QuerySolution soln = resultsGeneration.nextSolution();

        result = (String)(soln.getLiteral("contenttext").getValue());
      }

    } finally {

      generationQueryExec.close();
    }
    return result;

  }
public static Resource getGenerationRole(String dataArtifactUriString, String activityUriString, Model model){
  
  Resource result = null;
  
  String generationQueryStr =
      " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
          + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
          + " SELECT DISTINCT ?role  \n" 
          + " WHERE { \n"
          + " <"+dataArtifactUriString+">  prov:qualifiedGeneration ?gen1  . \n" 
          + " ?gen1 prov:hadRole  ?role . \n "
          + " ?gen1 prov:activity <"+activityUriString+"> . \n" 
          + " } \n";

  Query generationQuery = QueryFactory.create(generationQueryStr);
  QueryExecution generationQueryExec = QueryExecutionFactory.create(generationQuery, model);

  try {

    ResultSet resultsGeneration = generationQueryExec.execSelect();

    if (resultsGeneration.hasNext()) {

      QuerySolution soln = resultsGeneration.nextSolution();

      result = soln.getResource("role");
    }

  } finally {

    generationQueryExec.close();
  }
  return result;
}
public static Resource getUsageRole(String dataArtifactUriString, String activityUriString, Model model){
  
  Resource result = null;
  String usageQueryStr =
      " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
          + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
          + " SELECT DISTINCT ?role  \n" 
          + " WHERE { \n"
          + " <"+activityUriString+ "> prov:qualifiedUsage ?usg1 . \n" 
          + " ?usg1 prov:hadRole ?role .\n"
          + " ?usg1 prov:entity" + " <" + dataArtifactUriString + "> . \n" + " } \n";
  
  Query usageQuery = QueryFactory.create(usageQueryStr);

  QueryExecution usageQueryExec = QueryExecutionFactory.create(usageQuery, model);

  try {
    ResultSet results = usageQueryExec.execSelect();

    if (results.hasNext()) {

      QuerySolution soln = results.nextSolution();

      result = soln.getResource("role");
    }

  } finally {
    usageQueryExec.close();

  }
  return result;
}
  public static Set<Resource> getAllUsageInstances(String portUriString, Model model) {

    Set<Resource> result = new HashSet<Resource>();

    String usageQueryStr =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?entit1  \n" 
            + " WHERE { \n"
            + " ?usg1 a prov:Usage . \n" 
            + " ?usg1 prov:entity ?entit1 .\n"
            + " ?usg1 prov:hadRole" 
            + " <" + portUriString + "> . \n" 
            + " } \n";

    Query usageQuery = QueryFactory.create(usageQueryStr);

    QueryExecution usageQueryExec = QueryExecutionFactory.create(usageQuery, model);

    try {
      ResultSet results = usageQueryExec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getResource("entit1"));
      }

    } finally {
      usageQueryExec.close();

    }
    return result;
  }

  public static Set<Resource> getAllGenerationInstances(String portUriString, Model model) {

    Set<Resource> result = new HashSet<Resource>();

    String generationQueryStr =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?entit1  \n" + " WHERE { \n"
            + " ?entit1 prov:qualifiedGeneration ?gen1 . \n" + " ?gen1 prov:hadRole" + " <"
            + portUriString + "> . \n" + " } \n";

    Query generationQuery = QueryFactory.create(generationQueryStr);
    QueryExecution generationQueryExec = QueryExecutionFactory.create(generationQuery, model);

    try {

      ResultSet resultsGeneration = generationQueryExec.execSelect();

      for (; resultsGeneration.hasNext();) {

        QuerySolution soln = resultsGeneration.nextSolution();

        result.add(soln.getResource("entit1"));
      }

    } finally {

      generationQueryExec.close();
    }
    return result;
  }

  public static Map<String, String> getPortArtefactMapForActivity(String activityInstanceUri,
      Model model) {

    Map<String, String> result = new HashMap<String, String>();

    String usageQueryStr =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?entit1 ?role1  \n" + " WHERE { \n" + " <" + activityInstanceUri
            + "> prov:qualifiedUsage ?usg1 . \n" + " ?usg1 prov:entity ?entit1 .\n"
            + " ?usg1 prov:hadRole ?role1 .\n" + " } \n";

    Query usageQuery = QueryFactory.create(usageQueryStr);

    QueryExecution usageQueryExec = QueryExecutionFactory.create(usageQuery, model);

    try {
      ResultSet results = usageQueryExec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.put(soln.getResource("role1").getURI(), soln.getResource("entit1").getURI());
      }

    } finally {
      usageQueryExec.close();

    }

    String generationQueryStr =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?entit1 ?role1 \n" + " WHERE { \n"
            + " ?entit1 prov:qualifiedGeneration ?gen1 . \n" + " ?gen1 prov:hadRole ?role1 . \n"
            + " ?gen1 prov:activity <" + activityInstanceUri + "> .\n" + " } \n";


    Query generationQuery = QueryFactory.create(generationQueryStr);

    QueryExecution generationQueryExec = QueryExecutionFactory.create(generationQuery, model);

    try {
      ResultSet results = generationQueryExec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.put(soln.getResource("role1").getURI(), soln.getResource("entit1").getURI());
      }

    } finally {
      generationQueryExec.close();
    }

    return result;
  }


  public static Resource getDataContentPath(String artefactId, Model model) {
    Resource result = null;

    String contentQueryStr =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX tavernaprov:  <http://ns.taverna.org.uk/2012/tavernaprov/> \n"
            + " SELECT DISTINCT ?cont  \n" + " WHERE { \n" + " <" + artefactId
            + "> tavernaprov:content  ?cont . \n" + " } \n";

    Query generationQuery = QueryFactory.create(contentQueryStr);
    QueryExecution generationQueryExec = QueryExecutionFactory.create(generationQuery, model);

    try {

      ResultSet resultsGeneration = generationQueryExec.execSelect();

      for (; resultsGeneration.hasNext();) {

        QuerySolution soln = resultsGeneration.nextSolution();

        result = soln.getResource("cont");
      }

    } finally {

      generationQueryExec.close();
    }
    return result;


  }

  public static Set<Resource> getInstantiationsOfOperation(String operationUriString, Model model) {

    Set<Resource> result = new HashSet<Resource>();

    String generationQueryStr =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?entit1  \n" 
            + " WHERE { \n"
            + " ?entit1 prov:qualifiedAssociation ?assoc1 . \n" 
            + " ?assoc1 prov:hadPlan" + " <"
            + operationUriString + "> . \n" + " } \n";

    Query generationQuery = QueryFactory.create(generationQueryStr);
    QueryExecution generationQueryExec = QueryExecutionFactory.create(generationQuery, model);

    try {

      ResultSet resultsGeneration = generationQueryExec.execSelect();

      for (; resultsGeneration.hasNext();) {

        QuerySolution soln = resultsGeneration.nextSolution();

        result.add(soln.getResource("entit1"));
      }

    } finally {

      generationQueryExec.close();
    }
    return result;
  }

  public static List<LabelInstanceBean> getLabelsOfArtifacts(String labelURIString,
      String artifactId, Model model) {

    List<LabelInstanceBean> result = new ArrayList<LabelInstanceBean>();

    LabelDefinitonBean def = new LabelDefinitonBean();
    def.setLabelNameURIString(labelURIString);

    // TODO do a lookup for the actual type here!!!!
    def.setDataTypeEnum(XSDatatypeEnum.XS_STRING);

    StmtIterator iter =
        model.listStatements(model.getResource(artifactId), model.getProperty(labelURIString),
            (RDFNode) null);

    while (iter.hasNext()) {

      Statement stmt = iter.next();
      LabelInstanceBean ins = new LabelInstanceBean();
      ins.setDefiniton(def);


      ins.setLabelTargetURIString(stmt.getSubject().getURI());
      ins.setValue(stmt.getLiteral().getValue());

      result.add(ins);
    }

    return result;
  }



  public static Set<Resource> getAllContainedItems(String containerArtifactUriString,
      Model provModel) {

    Set<Resource> result = new HashSet<Resource>();

    if (hasItem(containerArtifactUriString, provModel)) {
      Set<Resource> items = getCollectionMembersAtDepth(containerArtifactUriString, 1, provModel);

      for (Resource item : items) {
        result.add(item);
        Set<Resource> itemSubHierarchy = getAllContainedItems(item.getURI(), provModel);
        result.addAll(itemSubHierarchy);

      }

    }

    return result;
  }

  public static boolean hasItem(String containerArtifactUriString, Model model) {

    boolean result = false;

    String queryString =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?item  \n" + " WHERE { \n" + "<" + containerArtifactUriString
            + ">  prov:hadMember  ?item . \n" + "} \n";


    Query query = QueryFactory.create(queryString);
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

  public static boolean hasContainer(String itemArtifactUriString, Model model) {

    boolean result = false;

    String queryString =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?coll  \n" + " WHERE { \n" + " ?coll a prov:Collection . \n"
            + " ?coll prov:hadMember <" + itemArtifactUriString + "> . \n" + " } \n";


    Query query = QueryFactory.create(queryString);
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

  public static Set<Resource> getAllContainers(String itemArtifactUriString, Model model) {

    Set<Resource> result = new HashSet<Resource>();
    String pointer = itemArtifactUriString;

    if (hasContainer(pointer, model)) {

      do {

        Resource container = getContainer(pointer, model);
        result.add(container);
        pointer = container.getURI();



      } while (hasContainer(pointer, model));


    }

    return result;
  }

  public static Resource getContainer(String itemArtifactUriString, Model model) {

    Resource result = null;

    String queryString =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?coll  \n" + " WHERE { \n" + " ?coll a prov:Collection . \n"
            + " ?coll prov:hadMember <" + itemArtifactUriString + "> . \n" + " } \n";


    Query generationQuery = QueryFactory.create(queryString);
    QueryExecution generationQueryExec = QueryExecutionFactory.create(generationQuery, model);

    try {

      ResultSet resultsGeneration = generationQueryExec.execSelect();

      for (; resultsGeneration.hasNext();) {

        QuerySolution soln = resultsGeneration.nextSolution();

        result = soln.getResource("coll");
      }

    } finally {

      generationQueryExec.close();
    }
    return result;
  }

  public static Resource getContainerAtDepth(String itemArtifactUriString, int depth, Model model) {

    Resource result = null;

    String queryString = "";
    String generationQueryPrefix =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT REPLACEME  \n" + " WHERE { \n"
            + " REPLACEME a prov:Collection . \n" + " ?item prov:hadMember <"
            + itemArtifactUriString + "> . \n";

    String remember = " ?item ";
    String generationQueryMiddle = " ";
    int i = 1;
    depth--;

    while (depth > 0) {

      generationQueryMiddle =
          " ?m" + i + " prov:hadMember " + remember + " . \n" + generationQueryMiddle;
      remember = " ?m" + i + " ";
      depth--;
      i++;
    }

    queryString = generationQueryPrefix + generationQueryMiddle + " } \n";
    queryString = queryString.replaceAll("REPLACEME", remember);

    Query generationQuery = QueryFactory.create(queryString);
    QueryExecution generationQueryExec = QueryExecutionFactory.create(generationQuery, model);

    try {

      ResultSet resultsGeneration = generationQueryExec.execSelect();

      for (; resultsGeneration.hasNext();) {

        QuerySolution soln = resultsGeneration.nextSolution();

        String varName = remember.trim();
        varName = varName.substring(1);
        result = soln.getResource(varName);
      }

    } finally {

      generationQueryExec.close();
    }
    return result;
  }

  public static Set<Resource> getCollectionMembersAtDepth(String collectionArtifactUriString,
      int depth, Model model) {
    
    if ((depth == 0)||(depth<0)){
      throw new IllegalArgumentException("depth should be greater than zero!");
    }

    Set<Resource> result = new HashSet<Resource>();

    String queryString = "";
    String generationQueryPrefixStr =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT REPLACEME  \n" 
            + " WHERE { \n" 
            + " <" +collectionArtifactUriString+ "> a prov:Collection . \n";

    String remember = " <" +collectionArtifactUriString+"> ";
    String generationQueryMiddle = " ";

    int i = 1;
    while (depth > 0) {

      generationQueryMiddle = generationQueryMiddle + remember + "prov:hadMember ?m" + i + " . \n";
      remember = " ?m" + i + " ";
      depth--;
      i++;
    }

    queryString = generationQueryPrefixStr + generationQueryMiddle  + " } \n";


    queryString = queryString.replaceAll("REPLACEME", remember);

    Query generationQuery = QueryFactory.create(queryString);
    QueryExecution generationQueryExec = QueryExecutionFactory.create(generationQuery, model);

    try {

      ResultSet resultsGeneration = generationQueryExec.execSelect();

      for (; resultsGeneration.hasNext();) {

        QuerySolution soln = resultsGeneration.nextSolution();

        String varName = remember.trim();
        varName = varName.substring(1);

        result.add(soln.getResource(varName));
      }

    } finally {

      generationQueryExec.close();
    }
    return result;
  }

  public static Set<Resource> getGenerationsWithRoleAndActivity(String activityUriString,
      String portUriString, Model model) {

    Set<Resource> result = new HashSet<Resource>();


    String generationQueryStr =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?entit1  \n" + " WHERE { \n"
            + " ?entit1 prov:qualifiedGeneration ?gen1 . \n" 
            + " ?gen1 prov:hadRole" + " <"
            + portUriString + "> . \n" + " ?gen1 prov:activity" + " <" + activityUriString
            + "> . \n" + " } \n";

    Query generationQuery = QueryFactory.create(generationQueryStr);
    QueryExecution generationQueryExec = QueryExecutionFactory.create(generationQuery, model);

    try {

      ResultSet resultsGeneration = generationQueryExec.execSelect();

      for (; resultsGeneration.hasNext();) {

        QuerySolution soln = resultsGeneration.nextSolution();

        result.add(soln.getResource("entit1"));
      }

    } finally {

      generationQueryExec.close();
    }
    return result;
  }

  public static Set<Resource> getUsagesWithRoleAndActivity(String activityUriString,
      String portUriString, Model model) {

    Set<Resource> result = new HashSet<Resource>();

    String generationQueryStr =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?entit1  \n" + " WHERE { \n" + " <" + activityUriString
            + "> prov:qualifiedUsage ?usg1 . \n" + " ?usg1 prov:entity ?entit1 . \n"
            + " ?usg1 prov:hadRole" + " <" + portUriString + "> . \n" + " } \n";

    Query generationQuery = QueryFactory.create(generationQueryStr);
    QueryExecution generationQueryExec = QueryExecutionFactory.create(generationQuery, model);

    try {

      ResultSet resultsGeneration = generationQueryExec.execSelect();

      for (; resultsGeneration.hasNext();) {

        QuerySolution soln = resultsGeneration.nextSolution();

        result.add(soln.getResource("entit1"));
      }

    } finally {

      generationQueryExec.close();
    }
    return result;
  }



  public static boolean isDataArtifact(Model model, Resource provNode) {
    StmtIterator iter =
        model.listStatements(provNode, model.getProperty(SummarizerNamespaces.rdfNS, "type"),
            model.getResource(SummarizerNamespaces.wfprovNS + "Artifact"));

    if (iter.hasNext()) {
      return true;

    } else {
      return false;
    }

  }

  public static boolean isActivity(Model model, Resource provNode) {
    StmtIterator iter =
        model.listStatements(provNode, model.getProperty(SummarizerNamespaces.rdfNS, "type"),
            model.getResource(SummarizerNamespaces.wfprovNS + "ProcessRun"));

    if (iter.hasNext()) {
      return true;

    } else {
      return false;
    }

  }



  public static boolean isDataUsed(Model provModel, Resource provNode) {

    boolean result = false;

    String queryString =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?actvty  \n" + " WHERE { \n" + "?actvty prov:used <"
            + provNode.getURI() + "> . \n" + "} \n";


    Query query = QueryFactory.create(queryString);
    QueryExecution qexec = QueryExecutionFactory.create(query, provModel);
    try {
      ResultSet results = qexec.execSelect();

      if (results.hasNext()) {
        result = true;
      } else {
     
      }
    } finally {
      qexec.close();
    }
    return result;
  }


  public static boolean hasGeneratedData(Model provModel, Resource provNode) {

    boolean result = false;

    String queryString =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?dat  \n" 
            + " WHERE { \n" 
            + "?dat prov:wasGeneratedBy <"
            + provNode.getURI() 
            + "> . \n" + "} \n";


    Query query = QueryFactory.create(queryString);
    QueryExecution qexec = QueryExecutionFactory.create(query, provModel);
    try {
      ResultSet results = qexec.execSelect();

      if (results.hasNext()) {
        result = true;
      } else {
      
      }
    } finally {
      qexec.close();
    }
    return result;
  }



  public static Set<Resource> getUsingActivities(Model provModel, Resource provNode) {
    Set<Resource> resultSet = new HashSet<Resource>();

    String queryString =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?actvty  \n" 
            + " WHERE { \n" 
            + "?actvty prov:used <"
            + provNode.getURI() + "> . \n" + "} \n";


    Query query = QueryFactory.create(queryString);
    QueryExecution qexec = QueryExecutionFactory.create(query, provModel);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        resultSet.add(soln.getResource("actvty"));
      }
    } finally {
      qexec.close();
    }
    return resultSet;
  }



  public static Set<Resource> getDataGeneratedBy(Model provModel, Resource provNode) {
    Set<Resource> resultSet = new HashSet<Resource>();


    String queryString =
        " PREFIX prov: <http://www.w3.org/ns/prov#> \n"
            + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n"
            + " SELECT DISTINCT ?dat  \n" 
            + " WHERE { \n" 
            + "?dat prov:wasGeneratedBy <"
            + provNode.getURI() 
            + "> . \n" + "} \n";
    Query query = QueryFactory.create(queryString);
    QueryExecution qexec = QueryExecutionFactory.create(query, provModel);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        resultSet.add(soln.getResource("dat"));
      }
    } finally {
      qexec.close();
    }
    return resultSet;
  }

  public static Set<String> getALabelOfEntity(Resource res, String labelUriString, Model provModel) {
    
    Set<String> result = new HashSet<String>();

    String queryStr =
        " PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#> \n"
        +"PREFIX prov: <http://www.w3.org/ns/prov#> \n"
       + " PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> \n" 
        + " SELECT DISTINCT ?lbl  \n"
            + " WHERE { \n" 
            + " <"+res.getURI()+">  <" +labelUriString + "> ?lbl . \n"
            + " } \n";

    Query query = QueryFactory.create(queryStr);
    QueryExecution qexec = QueryExecutionFactory.create(query, provModel);
    try {
      ResultSet results = qexec.execSelect();

      for (; results.hasNext();) {

        QuerySolution soln = results.nextSolution();

        result.add(soln.getLiteral("lbl").getString());
      }
    } finally {
      qexec.close();
    }
    return result;
  }

  public static boolean hasLabel(Resource entity, String labelUriString, String labelValue, Model provModel) {
    StmtIterator iter =
        provModel.listStatements(entity, provModel.createProperty(labelUriString),
            labelValue);

    if (iter.hasNext()) {
      return true;

    } else {
      return false;
    }
  }



}

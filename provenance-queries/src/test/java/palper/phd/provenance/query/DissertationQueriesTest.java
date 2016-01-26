/**
 * 
 */
package palper.phd.provenance.query;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.ReificationStyle;

/**
 * @author pinarpink
 * 
 */
public class DissertationQueriesTest {

  @Test
  public void testQ1P() {

    try {
      URL current =  this.getClass().getResource(".");
      String  provFileName = current.getPath()+"Provenance-bundles"+File.separator+"2920-bm-REPLACEMEinputs"+File.separator+"workflowrun.prov.ttl";

      int counter = 1;

      while (counter < 12) {

        System.out.println("Counter is " + counter );
        
        Model provModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);

        provModel.read(
            new FileInputStream(provFileName.replaceAll("REPLACEME", String.valueOf(counter))),
            null, "TURTLE");

        ExecutionLineageTraversal elt = new ExecutionLineageTraversal(provModel);

        
        Set<Resource> sesameRetrievals = ProvRdfUtils.getInstantiationsOfOperation("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/SesameXML/", provModel);
        if (!(sesameRetrievals.size() > 0))
          fail("no sesame database retrieval activity trace found");
        
        Set<Resource> sesameDataIds = new HashSet<Resource>();
        
        Set<String> descendantsOfSesameData = new HashSet<String>();
        
        Set<String> copiesOfSesameData = new HashSet<String>();
        
        for (Resource retrieval : sesameRetrievals) {

          sesameDataIds.add(
              ProvRdfUtils.getGenerationsWithRoleAndActivity(retrieval.getURI(), "http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/SesameXML/out/return", provModel)
                 .iterator().next());
          Set<ProvTraceStep> traversal = elt.traverseDown(retrieval);

          
        Set<String> copyPorts = new HashSet<String>();
        copyPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/SesameXML/out/return");
        copyPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Flatten_List/out/outputlist");
        copyPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Flatten_List_2/out/outputlist");
        copyPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Extracting_RA/out/nodelist");
        copyPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Extracting_DEC/out/nodelist");
    

        Set<String> allPorts = new HashSet<String>();
        allPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/SesameXML/out/return");
        allPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Flatten_List/out/outputlist");
        allPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Flatten_List_2/out/outputlist");
        allPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/VII_237/out/responseBody");
        allPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Format_conversion/out/outputTable");
        allPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/calculate_internal_extinction/out/STDOUT");
        
        allPorts
        .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Extracting_RA/out/nodelist");
        allPorts
        .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Extracting_DEC/out/nodelist");
    
        allPorts.add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Select_logr25_Mtype/out/outputTable");
        allPorts.add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Write_Text_File/out/outputFile") ;    
        
        descendantsOfSesameData.addAll(TraceFilters.getDataEntitiesGeneratedByRoleSet(traversal, allPorts));
        copiesOfSesameData.addAll(TraceFilters.getDataEntitiesGeneratedByRoleSet(traversal, copyPorts));

    
       }
        


        
        /****
         * PREPARE THE ADDITINAL data artifacts  that are in a container or item relation with data nodes in the lineage trace
         */
      
        
        System.out.println("Descendants of Sesame data Size:  "+descendantsOfSesameData.size());
        //System.out.println("Set : "+descendantsOfSesameData);
        
        
        System.out.println("All copies of Sesame data Size:  "+copiesOfSesameData.size());
       // System.out.println("Set : "+copiesOfSesameData);
        
        descendantsOfSesameData.retainAll(copiesOfSesameData);
        
        System.out.println("Descendants that are copies Size: "+descendantsOfSesameData.size());
        //System.out.println("Set : "+descendantsOfSesameData);

    
        counter++;

        System.out.println(" ");
        }
      
   
    } catch (FileNotFoundException e) {

      e.printStackTrace();
    }
    
  }
  
  
  @Test
  public void testQ2P() {
    try {

      URL current =  this.getClass().getResource(".");
      String  provFileName = current.getPath()+"Provenance-bundles"+File.separator+"2920-bm-REPLACEMEinputs"+File.separator+"workflowrun.prov.ttl";

      int counter = 1;
      while (counter < 12) {

        Model provModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);



        provModel.read(
            new FileInputStream(provFileName.replaceAll("REPLACEME", String.valueOf(counter))),
            null, "TURTLE");

        ExecutionLineageTraversal elt = new ExecutionLineageTraversal(provModel);


        // id of data node for M31 galaxy
        Resource provNode = null;

        Set<Resource> entities =
            ProvRdfUtils
                .getAllUsageInstances(
                    "http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/in/list_cig_name",
                    provModel);
        if (entities.size() != 1)
          fail();
        Set<Resource> items =
            ProvRdfUtils.getAllContainedItems(entities.iterator().next().getURI(), provModel);


        for (Resource item : items) {
          String content = ProvRdfUtils.getInlineContentforDataArtifact(item.getURI(), provModel);
          if (content.equalsIgnoreCase("M31")) {
            provNode = item;
          }
        }
        if (provNode == null)
          fail("unable to find an input with value M31.");

        System.out.println("Counter is " + counter + ". Lineage for input M31.");
        Set<ProvTraceStep> traversal = elt.traverseDown(provNode);

        System.out.println("Total number of trace steps: \t" + traversal.size());

        System.out.println("Number of distinct data entities in trace: \t"
            + TraceFilters.getDistinctDataEntities(traversal).size());


        Set<String> bookmarkedPorts = new HashSet<String>();
        bookmarkedPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/SesameXML/out/return");
        bookmarkedPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Flatten_List/out/outputlist");
        bookmarkedPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Flatten_List_2/out/outputlist");
        bookmarkedPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/VII_237/out/responseBody");
        bookmarkedPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Format_conversion/out/outputTable");
        bookmarkedPorts
            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/calculate_internal_extinction/out/STDOUT");



        System.out.println("Number of bookmarked data entities  in lineage: \t"
            + TraceFilters.getDataEntitiesGeneratedByRoleSet(traversal, bookmarkedPorts).size());

        System.out.println("");
        counter++;
      }



    } catch (FileNotFoundException e) {

      e.printStackTrace();
    }

  }
  
  @Test
  public void testQ3P() {

    try {
      URL current =  this.getClass().getResource(".");
      String  provFileName = current.getPath()+"Provenance-bundles"+File.separator+"2920-bm-REPLACEMEinputs"+File.separator+"workflowrun.prov.ttl";

      int counter = 1;

      while (counter < 12) {

        Model provModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);

        provModel.read(
            new FileInputStream(provFileName.replaceAll("REPLACEME", String.valueOf(counter))),
            null, "TURTLE");

        ExecutionLineageTraversal elt = new ExecutionLineageTraversal(provModel);

        // id of data node for M31 galaxy
        Resource provNode = null;

        Set<Resource> entities =
            ProvRdfUtils
                .getAllUsageInstances(
                    "http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/in/list_cig_name",
                    provModel);
        if (entities.size() != 1)
          fail();
        Set<Resource> items =
            ProvRdfUtils.getAllContainedItems(entities.iterator().next().getURI(), provModel);


        for (Resource item : items) {
          String content = ProvRdfUtils.getInlineContentforDataArtifact(item.getURI(), provModel);
          if (content.equalsIgnoreCase("M31")) {
            provNode = item;
          }
        }
        if (provNode == null)
          fail("unable to find an input with value M31.");

        
        Set<ProvTraceStep> traversal = elt.traverseDown(provNode);
        Set<String> dataLineageOfM31 = TraceFilters.getDistinctDataEntities(traversal);

        Set<Resource> extinctionCalculations =
            ProvRdfUtils
                .getInstantiationsOfOperation(
                    "http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/calculate_internal_extinction/",
                    provModel);

        if (!(extinctionCalculations.size() > 0))
          fail("no extinction calculation activity trace found");

        Set<String> extinctionValuesObtainedWithInputsDerivedFromM31 = new HashSet<String>();
        for (Resource calculation : extinctionCalculations) {
          Resource raCoordinate =
              ProvRdfUtils
                  .getUsagesWithRoleAndActivity(
                      calculation.getURI(),
                      "http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/calculate_internal_extinction/in/ra",
                      provModel).iterator().next();
          Resource decCoordinate =
              ProvRdfUtils
                  .getUsagesWithRoleAndActivity(
                      calculation.getURI(),
                      "http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/calculate_internal_extinction/in/dec",
                      provModel).iterator().next();

          if (dataLineageOfM31.contains(raCoordinate.getURI())
              && dataLineageOfM31.contains(decCoordinate.getURI())) {
            extinctionValuesObtainedWithInputsDerivedFromM31.add(ProvRdfUtils.getGenerationsWithRoleAndActivity(
                    calculation.getURI(),
                    "http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/calculate_internal_extinction/out/STDOUT",
                    provModel).iterator().next().getURI());
          }

        }
        System.out.println("Counter is " + counter + ". extinctionValuesObtainedWithInputsDerivedFromM31: \t"+extinctionValuesObtainedWithInputsDerivedFromM31.size());
       
        counter++;
      }
    } catch (FileNotFoundException e) {

      e.printStackTrace();
    }

  }
}

/**
 * 
 */
package palper.phd.provenance.query;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

    // data ibtained from sesame database
  }

//  @Test
//  public void testQ2P() {
//    try {
//
//      String provFileName =
//          "/Users/pinarpink/Desktop/Labeling_Workflows/Provenance-bundles/2920-bm-REPLACEMEinputs/workflowrun.prov.ttl";
//
//      int counter = 1;
//      while (counter < 12) {
//
//        Model provModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
//
//
//
//        provModel.read(
//            new FileInputStream(provFileName.replaceAll("REPLACEME", String.valueOf(counter))),
//            null, "TURTLE");
//
//        ExecutionLineageTraversal elt = new ExecutionLineageTraversal(provModel);
//
//
//        // id of data node for M31 galaxy
//        Resource provNode = null;
//
//        Set<Resource> entities =
//            ProvRdfUtils
//                .getAllUsageInstances(
//                    "http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/in/list_cig_name",
//                    provModel);
//        if (entities.size() != 1)
//          fail();
//        Set<Resource> items =
//            ProvRdfUtils.getAllContainedItems(entities.iterator().next().getURI(), provModel);
//
//
//        for (Resource item : items) {
//          String content = ProvRdfUtils.getInlineContentforDataArtifact(item.getURI(), provModel);
//          if (content.equalsIgnoreCase("M31")) {
//            provNode = item;
//          }
//        }
//        if (provNode == null)
//          fail("unable to find an input with value M31.");
//
//        System.out.println("Counter is " + counter + ". Lineage for input M31.");
//        Set<ProvTraceStep> traversal = elt.traverseDown(provNode);
//
//        System.out.println("Total number of trace steps: \t" + traversal.size());
//
//        System.out.println("Number of distinct data entities in trace: \t"
//            + TraceFilters.getDistinctDataEntities(traversal).size());
//
//
//        Set<String> bookmarkedPorts = new HashSet<String>();
//        bookmarkedPorts
//            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/SesameXML/out/return");
//        bookmarkedPorts
//            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Flatten_List/out/outputlist");
//        bookmarkedPorts
//            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Flatten_List_2/out/outputlist");
//        bookmarkedPorts
//            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/VII_237/out/responseBody");
//        bookmarkedPorts
//            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/Format_conversion/out/outputTable");
//        bookmarkedPorts
//            .add("http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/calculate_internal_extinction/out/STDOUT");
//
//
//
//        System.out.println("Number of bookmarked data entities  in lineage: \t"
//            + TraceFilters.getDataEntitiesGeneratedByRoleSet(traversal, bookmarkedPorts).size());
//
//        System.out.println("");
//        counter++;
//      }
//
//
//
//    } catch (FileNotFoundException e) {
//
//      e.printStackTrace();
//    }
//
//  }

  @Test
  public void testQ3P() {

    try {
      String provFileName =
          "/Users/pinarpink/Desktop/Labeling_Workflows/Provenance-bundles/2920-bm-REPLACEMEinputs/workflowrun.prov.ttl";

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

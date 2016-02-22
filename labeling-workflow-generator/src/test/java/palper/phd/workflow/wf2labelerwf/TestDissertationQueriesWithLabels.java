package palper.phd.workflow.wf2labelerwf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import palper.phd.provenance.query.ProvRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.ReificationStyle;

public class TestDissertationQueriesWithLabels {

  
  @Test
  public void testQ1L() {


    try {

      int counter = 1;

      while (counter < 12) {
        URL current = this.getClass().getResource(".");

        String provFileName =
            current.getPath() + "Provenance-bundles" + File.separator + "2920-bm-REPLACEMEinputs"
                + File.separator + "workflowrun.prov.ttl";


        String labelFileName =
            current.getPath() + "Provenance-bundles" + File.separator + "2920-bm-REPLACEMEinputs"
                + File.separator + "workflowrun-Labels.ttl";

        Model provModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);

        String currentProvFileName = provFileName.replaceAll("REPLACEME", String.valueOf(counter));
        String currentLabelFileName =
            labelFileName.replaceAll("REPLACEME", String.valueOf(counter));
        provModel.read(new FileInputStream(currentProvFileName), null, "TURTLE");


        provModel.read(new FileInputStream(currentLabelFileName), null, "TURTLE");


        Set<Resource> dataFromSesameDb =
            ProvRdfUtils.getEntitiesWithLabel("http://palper.phd.labeling.astrocase/referenceURI",
                "http://cdsws.u-strasbg.fr/", provModel);


        System.out.println("-------------");
        System.out.println(dataFromSesameDb.size());


        for (Resource res : dataFromSesameDb) {
          Set<String> catalogLabels =
              ProvRdfUtils.getALabelOfEntity(res,
                  "http://palper.phd.labeling.astrocase/referenceCatalog", provModel);
          System.out.print(catalogLabels);
        }
        System.out.println("-------------");
        counter++;
      }
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();

      Assert.fail();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      Assert.fail();
    }
  }


  @Test
  public void testQ2L() {
    try {

      int counter = 1;

      while (counter < 12) {
        URL current = this.getClass().getResource(".");

        String provFileName =
            current.getPath() + "Provenance-bundles" + File.separator + "2920-bm-REPLACEMEinputs"
                + File.separator + "workflowrun.prov.ttl";


        String labelFileName =
            current.getPath() + "Provenance-bundles" + File.separator + "2920-bm-REPLACEMEinputs"
                + File.separator + "workflowrun-Labels.ttl";

        Model provModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);

        String currentProvFileName = provFileName.replaceAll("REPLACEME", String.valueOf(counter));
        String currentLabelFileName =
            labelFileName.replaceAll("REPLACEME", String.valueOf(counter));
        provModel.read(new FileInputStream(currentProvFileName), null, "TURTLE");


        provModel.read(new FileInputStream(currentLabelFileName), null, "TURTLE");


        Set<Resource> dataAboutAndromeda =
            ProvRdfUtils.getEntitiesWithLabel("http://palper.phd.labeling.astrocase/hasSubject",
                "M31", provModel);

        Set<Resource> dataAboutAndromedaAlternateIdScheme =
            ProvRdfUtils.getEntitiesWithLabel("http://palper.phd.labeling.astrocase/hasSubject",
                "NGC224", provModel);

        dataAboutAndromeda.addAll(dataAboutAndromedaAlternateIdScheme);
        System.out.println("-------------");
        System.out.println(dataAboutAndromeda.size());

        System.out.println("-------------");
        counter++;
      }
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();

      Assert.fail();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testQ3L() {
    try {

      int counter = 1;

      while (counter < 12) {
        URL current = this.getClass().getResource(".");

        String provFileName =
            current.getPath() + "Provenance-bundles" + File.separator + "2920-bm-REPLACEMEinputs"
                + File.separator + "workflowrun.prov.ttl";


        String labelFileName =
            current.getPath() + "Provenance-bundles" + File.separator + "2920-bm-REPLACEMEinputs"
                + File.separator + "workflowrun-Labels.ttl";

        Model provModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);

        String currentProvFileName = provFileName.replaceAll("REPLACEME", String.valueOf(counter));
        String currentLabelFileName =
            labelFileName.replaceAll("REPLACEME", String.valueOf(counter));
        provModel.read(new FileInputStream(currentProvFileName), null, "TURTLE");


        provModel.read(new FileInputStream(currentLabelFileName), null, "TURTLE");



        Set<Resource> extinctionCalculations =
            ProvRdfUtils
                .getInstantiationsOfOperation(
                    "http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/calculate_internal_extinction/",
                    provModel);

        Set<Resource> result = new HashSet<Resource>();

        for (Resource exCalculation : extinctionCalculations) {

          Set<Resource> raCoordinate =
              ProvRdfUtils
                  .getUsagesWithRoleAndActivity(
                      exCalculation.getURI(),
                      "http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/calculate_internal_extinction/in/ra",
                      provModel);

          if (raCoordinate.size() != 1) {
            Assert.fail();
          }
          Resource ra = raCoordinate.iterator().next();

          if (ProvRdfUtils.hasLabel(ra, "http://palper.phd.labeling.astrocase/hasSubject", "M31",
              provModel)) {
            Set<String> morphologyValueLabel =
                ProvRdfUtils.getALabelOfEntity(ra,
                    "http://palper.phd.labeling.astrocase/hasSubject", provModel);


            if (morphologyValueLabel.size() != 1) {
              Assert.fail();
            }

            Double morphologyValue = Double.parseDouble(morphologyValueLabel.iterator().next());

            if (morphologyValue < 0.45) {
              Set<Resource> extinction =
                  ProvRdfUtils
                      .getGenerationsWithRoleAndActivity(
                          exCalculation.getURI(),
                          "http://ns.taverna.org.uk/2010/workflowBundle/5007a32e-5354-4cfe-8557-8cff34307444/workflow/Workflow4/processor/calculate_internal_extinction/out/STDOUT",
                          provModel);
              if (extinction.size() != 1) {
                Assert.fail();
              }
              result.add(extinction.iterator().next());
            }

          }


        }

        Set<Resource> dataAboutAndromeda =
            ProvRdfUtils.getEntitiesWithLabel("http://palper.phd.labeling.astrocase/hasSubject",
                "M31", provModel);

        Set<Resource> dataAboutAndromedaAlternateIdScheme =
            ProvRdfUtils.getEntitiesWithLabel("http://palper.phd.labeling.astrocase/hasSubject",
                "NGC224", provModel);

        dataAboutAndromeda.addAll(dataAboutAndromedaAlternateIdScheme);
        System.out.println("-------------");
        System.out.println(dataAboutAndromeda.size());

        System.out.println("-------------");
        counter++;
      }
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();

      Assert.fail();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      Assert.fail();
    }
  }
}

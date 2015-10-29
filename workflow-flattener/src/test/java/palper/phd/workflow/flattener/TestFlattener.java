package palper.phd.workflow.flattener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Test;

import palper.phd.wfdesc.visualize.graphml.GraphmlWriter;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileUtils;

public class TestFlattener {

  @Test
  public void test() {

    File dataset_dir = new File("/Users/pinarpink/Desktop/SummarizationRules/dataset/");

    File[] file_array = dataset_dir.listFiles();
    for (int i = 0; i < file_array.length; i++) {
      String wfdescFileName = file_array[i].getPath();

      if (wfdescFileName.endsWith(".wfdesc.ttl")) {
        try {

          //File wfdescfile = new File(wfdescFileName);
          Model baseModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);

          InputStream is = new FileInputStream(wfdescFileName);
          String lang = FileUtils.guessLang(wfdescFileName);

          baseModel.read(is, null, lang);

          boolean isnested =
              WfDescRdfUtils.isNestedWorkflow(WfDescRdfUtils.getWorkflowResource(baseModel),
                  baseModel);


          
          if (isnested) {
            System.out.println("Flattening:" + wfdescFileName);

            Flattener ft = new Flattener(baseModel);

            String graphMlFileName = wfdescFileName;
            graphMlFileName = graphMlFileName.replaceFirst("\\..*", "") + "-flattened" + ".graphml";
            File graphMlFile = new File(graphMlFileName);
            Writer fw = new FileWriter(graphMlFile);

            GraphmlWriter gw = new GraphmlWriter(fw, ft.getFlattenedWfdesc());
            gw.write();
            
            
            String destFilename = wfdescFileName.replaceFirst("\\..*","") + "-flattened.wfdesc.ttl";

            File wfdescFlattened = new File(destFilename);

            OutputStream oss = new FileOutputStream(wfdescFlattened);

            ft.getFlattenedWfdesc().write(oss, "TURTLE", null);
            

            String flattenedGroupsFileName = destFilename.replaceFirst("\\..*", "") + "flattenGroups.csv";
    
            File groupsFIle = new File(flattenedGroupsFileName);
            Writer groupWrite = new FileWriter(groupsFIle);

           Map<String, Set<String>> userGroupings = ft.getFlattenedProcessorGroupings();
            for (Map.Entry<String, Set<String>> entry : userGroupings.entrySet()) {
              String line = entry.getKey()+";";
              Set<String> group = entry.getValue();
              Iterator iter2 = group.iterator();
              
              while (iter2.hasNext()) {
                line = line+(String)iter2.next();
              }
              
              groupWrite.write(line+"\n");
       
            }
            
            groupWrite.close();
          }
        } catch (ParserConfigurationException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (TransformerException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }

    }
  }

}

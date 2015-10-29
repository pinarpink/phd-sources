package palper.phd.workflow.flattener;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Test;

import palper.phd.wfdesc.visualize.graphml.GraphmlWriter;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileUtils;

public class TestBookmarkEliminator {

  @Test
  public void test() {

    File dataset_dir = new File("/Users/pinarpink/Desktop/SummarizationRules/dataset/");

    File[] file_array = dataset_dir.listFiles();
    for (int i = 0; i < file_array.length; i++) {
      String wfdescFileName = file_array[i].getPath();

      if (wfdescFileName.endsWith(".wfdesc.ttl")) {

        try {
          // File wfdescfile = new File(wfdescFileName);
          Model baseModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);

          InputStream is;

          is = new FileInputStream(wfdescFileName);

          String lang = FileUtils.guessLang(wfdescFileName);

          baseModel.read(is, null, lang);


          System.out.println("Bookmarks for:" + wfdescFileName);

          BookmarkEliminator be = new BookmarkEliminator(baseModel);

          System.out.println(be.getBookmarkPortUris().toString());
        } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

    }

  }
}

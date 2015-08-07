import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import palper.phd.pipeline.maker.WfdescToClojure;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.ReificationStyle;

/**
 * 
 */

/**
 * @author pinarpink
 * 
 */
public class TestCljGenerator {

	@Test
	public void test() {
		try {

			Model wfdescModel = ModelFactory
					.createDefaultModel(ReificationStyle.Minimal);

			InputStream is;

			is = new FileInputStream(
					"/Users/pinarpink/Desktop/SummarizationRules/dataset/lbl-2920.wfdesc.ttl");
			
			wfdescModel.read(is, null, "TURTLE");
			WfdescToClojure gen = new WfdescToClojure();
			gen.makeClojureFile(wfdescModel);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

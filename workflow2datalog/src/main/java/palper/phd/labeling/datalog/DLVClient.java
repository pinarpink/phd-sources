/**
 * 
 */
package palper.phd.labeling.datalog;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import it.unical.mat.dlv.program.Literal;
import it.unical.mat.dlv.program.Query;
import it.unical.mat.wrapper.DLVError;
import it.unical.mat.wrapper.DLVInputProgram;
import it.unical.mat.wrapper.DLVInputProgramImpl;
import it.unical.mat.wrapper.DLVInvocation;
import it.unical.mat.wrapper.DLVInvocation.DLVInvocationState;
import it.unical.mat.wrapper.DLVInvocationException;
import it.unical.mat.wrapper.DLVWrapper;
import it.unical.mat.wrapper.Model;
import it.unical.mat.wrapper.ModelBufferedHandler;
import it.unical.mat.wrapper.ModelHandler;
import it.unical.mat.wrapper.ModelResult;
import it.unical.mat.wrapper.Predicate;
import it.unical.mat.wrapper.PredicateHandlerWithName;

/**
 * @author pinarpink
 * 
 */
public class DLVClient {

	private DLVInputProgram inputProgram;

	DLVInvocation invocation;

	public DLVClient() throws IOException {
		super();
		 inputProgram = new DLVInputProgramImpl();

		 URL en = this.getClass().getResource("prog-input");
		 
			File fileProgInp;
			try {
				fileProgInp = new File(en.toURI());

				File[] files = fileProgInp.listFiles();
				// or

				for (File f : files) {
					inputProgram.addFile(f.getAbsolutePath());
				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	public void addWorkflowDescription(String wfEdbAsString) {

		inputProgram.addText(wfEdbAsString);

	}

	public void invokeDlv(String query, List<String> execOptions) {
		// Query qry = new Query();
		// qry.s
		// inputProgram.setQuery(new Query(query));

		try {
//			for (String s : execOptions) {
//
//				invocation.addOption(s);
//
//			}
			URL resourceURL = this.getClass().getResource("dlv");
			invocation = DLVWrapper.getInstance().createInvocation(resourceURL.getPath());
			invocation.setInputProgram(inputProgram);
			invocation.setNumberOfModels(0);
			invocation.setMaxint(99999);

			ModelBufferedHandler modelBufferedHandler = new ModelBufferedHandler(invocation);

			/* In this moment I can start the DLV execution */
			invocation.run();

			/* Scroll all models computed */
			while (modelBufferedHandler.hasMoreModels()) {
				Model model = modelBufferedHandler.nextModel();
				Enumeration<Predicate> ps = model.getPredicates();
				System.out.println("--------");
				while (ps.hasMoreElements()) {
					Predicate predicate = ps.nextElement();
					System.out.println(predicate.toString());
					System.out.println("++++++++++");
//					while (predicate.hasMoreLiterals()) {
//						Literal literal = predicate.nextLiteral();
//						
//					}
				}
			}

			/* If i wont to wait the finish of execution, i can use thi method */
			invocation.waitUntilExecutionFinishes();
			
			List<DLVError>errors =  invocation.getErrors();
			
			for(DLVError err:errors){
				System.out.println(err.getText());
			}

		} catch (DLVInvocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

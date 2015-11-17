/**
 * 
 */
package palper.phd.labeling.datalog;

import it.unical.mat.dlv.program.Literal;
import it.unical.mat.wrapper.DLVError;
import it.unical.mat.wrapper.DLVInputProgram;
import it.unical.mat.wrapper.DLVInputProgramImpl;
import it.unical.mat.wrapper.DLVInvocation;
import it.unical.mat.wrapper.DLVInvocationException;
import it.unical.mat.wrapper.DLVWrapper;
import it.unical.mat.wrapper.ModelBufferedHandler;
import it.unical.mat.wrapper.Predicate;
import it.unical.mat.wrapper.PredicateResult;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pinarpink
 * 
 */
public class DLVClient {

  private DLVInputProgram inputProgram;

  DLVInvocation invocation;

  Map<Predicate, List<Literal>> workflowIDB;

  String baseRulesRelativePath;

  public DLVClient(String baseRulesRelativePath) throws IOException {
    super();
    inputProgram = new DLVInputProgramImpl();

    //"prog-input-2"
    this.baseRulesRelativePath = baseRulesRelativePath;
    URL en = this.getClass().getResource(baseRulesRelativePath);

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


  public void addContextDescription(String contextEdbAsString) {

    inputProgram.addText(contextEdbAsString);

  }

  public void invokeDlv(String query, List<String> execOptions) {


    try {

      URL resourceURL =
          new URL(
              "file:/Users/pinarpink/Work/workspaces/eclipse-helios-gitted/workflow2datalog/src/main/resources/palper/phd/labeling/datalog/dlv");


      invocation = DLVWrapper.getInstance().createInvocation(resourceURL.getPath());
      invocation.setInputProgram(inputProgram);
      invocation.setNumberOfModels(0);
      invocation.setMaxint(99999);

      ModelBufferedHandler modelBufferedHandler = new ModelBufferedHandler(invocation);

      /* In this moment I can start the DLV execution */
      invocation.run();
      /* If i wont to wait the finish of execution, i can use thi method */
      invocation.waitUntilExecutionFinishes();

      workflowIDB = new HashMap<Predicate, List<Literal>>();
      /* Scroll all models computed */
      while (modelBufferedHandler.hasMoreModels()) {
        it.unical.mat.wrapper.Model workflowIDBModel = modelBufferedHandler.nextModel();
        Enumeration<Predicate> ps = workflowIDBModel.getPredicates();
        // System.out.println("--------");
        List<Predicate> predList = new ArrayList<Predicate>();


        while (ps.hasMoreElements()) {
          Predicate predicate = ps.nextElement();
          predList.add(predicate);

          // if (predicate.name().equals("predictedDepth")){
          workflowIDB.put(predicate, new ArrayList<Literal>());
          while (predicate.hasMoreLiterals()) {
            Literal literal = predicate.nextLiteral();
            workflowIDB.get(predicate).add(literal);
            System.out.println(literal.toString());

          }
          // }


          // System.out.println("++++++++++");

        }
      }

      List<DLVError> errors = invocation.getErrors();

      for (DLVError err : errors) {
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


  public Map<Predicate, List<Literal>> getWorkflowIDB() {
    return workflowIDB;
  }

}

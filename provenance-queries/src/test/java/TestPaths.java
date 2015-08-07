import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import palper.phd.provenance.model.DatalinkBean;
import palper.phd.provenance.model.OperationBean;
import palper.phd.provenance.model.PortBean;
import palper.phd.provenance.model.PortTypeEnum;
import palper.phd.provenance.model.WfElementThreadIterator;
import palper.phd.provenance.model.WorkflowElementBean;
import palper.phd.provenance.query.DesignLineageTraversal;
import palper.phd.provenance.query.ProvRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.util.FileUtils;

/**
 * 
 */

/**
 * @author pinarpink
 * 
 */
public class TestPaths {

	@Test
	 public void testPaths() {
	
	 //
	 // File prov_file_path = new File(args[1]);
	 // File wfdesc_file_path = new File();
	 try {
	
	 Model wfdescModel = ModelFactory
	 .createDefaultModel(ReificationStyle.Minimal);
	
	 InputStream is;
	
	 is = new FileInputStream(
	 "/Users/pinarpink/Desktop/SummarizationRules/dataset/2920.wfdesc.ttl");
	
	 wfdescModel.read(is, null, "TURTLE");
	
	 // List<List<PortBean>> results =
	 // downstreamLineagePorts(wfdescModel,
	 //
	 //"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/in/list_cig_name");
	
	 List<List<WorkflowElementBean>> results = DesignLineageTraversal
	 .downstreamWorkflowPath(
	 wfdescModel,
	 "http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/in/list_cig_name");
	
	 for (List<WorkflowElementBean> thread : results) {
	
	 Collections.reverse(thread);
	 WfElementThreadIterator iter = new WfElementThreadIterator(
	 thread);
	 // long initNanoTime = System.nanoTime();
	
	 // System.out.println("8888888888888888 THREAD 88888888888888888");
	
	 while (iter.hasNext()) {
	 WorkflowElementBean bean = iter.next();
	 System.out
	 .println("Element:\t" + bean.getUriString() + " ");
	
	 }
	
	 // long endNanoTime = System.nanoTime();
	
	 // System.out.println(" THREAD DURATION \t:"+
	 // Long.toString(endNanoTime-initNanoTime));
	 
	 
	 /*
	  * 
	  
	  		                                                                  
			for (List<WorkflowElementBean> thread:results){
				
				WfElementThreadIterator iter = new WfElementThreadIterator((List<WorkflowElementBean>)thread);
				long initNanoTime = System.nanoTime();
				
				System.out.println("8888888888888888 THREAD 88888888888888888");
				
				
				while(iter.hasNext()){
					PortBean port = (PortBean)iter.next();
					System.out.println("Port:\t" +port.getUriString()+" ");
					
					Set<Resource> res1 = ProvRdfUtils.getAllUsageInstances(port.getUriString(), provModel);
					Iterator<Resource> iterator = res1.iterator();
					while(iterator.hasNext()){
						Resource dataArtifact = iterator.next();
						System.out.println("\t Artifact:\t" +dataArtifact.getURI()+"");
					}
					Set<Resource> res2 = ProvRdfUtils.getAllGenerationInstances(port.getUriString(), provModel);
					Iterator<Resource> iter2 = res2.iterator();
					while(iter2.hasNext()){
						Resource dataArtifact = iter2.next();
						System.out.println("\t Artifact:\t" +dataArtifact.getURI()+" ");
					}
					
				}
				
				long endNanoTime = System.nanoTime();
				
				System.out.println(" THREAD DURATION \t:"+ Long.toString(endNanoTime-initNanoTime));
				
			}
	  
	  
	  
	  * 
	  * 
	  * 
	  */

	 
	
	 }
	
	 } catch (FileNotFoundException e) {
	 // TODO Auto-generated catch block
	 e.printStackTrace();
	 }
	
	 }
	
}

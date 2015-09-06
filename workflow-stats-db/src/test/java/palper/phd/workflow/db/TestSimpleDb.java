package palper.phd.workflow.db;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentNavigableMap;

import org.junit.Test;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import palper.phd.workflow.wfdesc.WfdescStatistics;

public class TestSimpleDb {

	@Test
	public void test() {
		DB db = DBMaker.fileDB(new File("testdb2"))
		        .closeOnJvmShutdown()
		        .encryptionEnable("password")
		        .make();
		
		// open existing an collection (or create new)
		ConcurrentNavigableMap<String,WorkflowInfo> map = db.treeMap("workflowInfo");
		
		
		WorkflowInfo wi = new WorkflowInfo();
		wi.setUnactionableProcessUris(new ArrayList<String>(
			    Arrays.asList("zozo", "zuzu", "zaza")));
		wi.setStatistics(new WfdescStatistics());
		wi.setWfdescAsString("BLA BLA BLA WFDESC AS TURTLE");
	
		
		
		WorkflowInfo wi2 = new WorkflowInfo();
		wi2.setUnactionableProcessUris(new ArrayList<String>(
			    Arrays.asList("bobo", "bubu", "baba")));
		wi2.setStatistics(new WfdescStatistics());
		wi2.setWfdescAsString("HO HO HO HO WFDESC AS TURTLE");
		

	
		map.put("wf1", wi); 
		map.put("wf2", wi2);
		
		db.commit(); //persist changes into disk
		db.close();

		DB db2 = DBMaker.fileDB(new File("testdb2"))
		        .closeOnJvmShutdown()
		        .encryptionEnable("password")
		        .make();
		
		// open existing an collection (or create new)
		ConcurrentNavigableMap<String,WorkflowInfo> map2 = db2.treeMap("workflowInfo");
	
		
		 Iterator<String> iterator = map2.keySet().iterator();
		 while (iterator.hasNext()) {
	            String current = iterator.next();
	        	System.out.println(current);

	        	System.out.println(map2.get(current).getUnactionableProcessUris());
	        	System.out.println(map2.get(current).getWfdescAsString());
	        	
		 }
		 
		 
	}

}

/**
 * 
 */
package palper.phd.workflow.flattener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import palper.phd.workflow.wfdesc.SummarizerNamespaces;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author pinarpink
 *
 */
public class BookmarkEliminator {

  
  private Model bookmarkEliminatedWfdesc;
  
  private Set<String> bookmarkPortUris =new HashSet<String>();

  public BookmarkEliminator(Model model) {
    
    
    bookmarkEliminatedWfdesc = model;
    
    
    Resource wfResource = WfDescRdfUtils.getWorkflowResource(bookmarkEliminatedWfdesc);
    Set<Resource> bookmarkingLinks = WfDescRdfUtils.getBookmarkedIntermediaryPorts(wfResource, bookmarkEliminatedWfdesc);
    
    for(Resource bmLink:bookmarkingLinks){
      Resource source =  WfDescRdfUtils.getSource(bookmarkEliminatedWfdesc, bmLink);
      bookmarkPortUris.add(source.getURI());
      
    }
   
    
    for(Resource bmLink:bookmarkingLinks){
      Resource sink =  WfDescRdfUtils.getSink(bookmarkEliminatedWfdesc, bmLink);
      bookmarkEliminatedWfdesc.removeAll(null, null, sink);
      bookmarkEliminatedWfdesc.removeAll(sink, null, null);
      
      bookmarkEliminatedWfdesc.removeAll(null, null, bmLink);
      bookmarkEliminatedWfdesc.removeAll(bmLink, null, null);
      
    }
    
    decorateBookmarkedPorts(bookmarkEliminatedWfdesc);
  }


  public Model getBookmarkEliminatedWfdesc() {
    return bookmarkEliminatedWfdesc;
  }


  public Set<String> getBookmarkPortUris() {
    return bookmarkPortUris;
  }

  public Model decorateBookmarkedPorts(Model model) {

    Set<String> ports = getBookmarkPortUris();

    for (String port : ports) {

      model.add(model.getResource(port),
          model.createProperty(SummarizerNamespaces.motifsNS + "bookmarked"), "true");
    }
    return model;
  }

}

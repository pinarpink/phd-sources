/**
 * 
 */
package palper.phd.provenance.model;

import java.util.Iterator;
import java.util.List;


/**
 * @author pinarpink
 *
 */
public class WfElementThreadIterator implements Iterator<WorkflowElementBean> {

	   private Iterator<WorkflowElementBean> iter;
	   private final List<WorkflowElementBean> thread;

	    public WfElementThreadIterator(List<WorkflowElementBean> thread) {
	    	iter = thread.iterator();
	    
	        this.thread = thread;
	    }

	    public boolean hasNext() {
	        return iter.hasNext();
	    }

	    public WorkflowElementBean next() {
	        return iter.next();
	    }

	    public void remove() {
	        throw new UnsupportedOperationException();
	    }
	    

}

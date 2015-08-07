package palper.phd.workflow.wfdesc.motifs;
/**
 * 
 */


import java.util.Iterator;

/**
 * @author pinarpink
 *
 */
public interface AnnotationSet  extends Iterator<AnnotationHolderBean>{

	  
	    /**
	     * Is there another result?
	     */
	    public boolean hasNext() ;

	    /** Moves onto the next result. */
	    
	    public AnnotationHolderBean next() ;

	    /** Return the "row" number for the current iterator item */
	    public int getRowNumber() ;
	    

	}



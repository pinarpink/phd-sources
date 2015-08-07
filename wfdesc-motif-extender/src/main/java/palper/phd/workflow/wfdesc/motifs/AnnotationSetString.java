package palper.phd.workflow.wfdesc.motifs;
/**
 * 
 */


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import palper.phd.workflow.wfdesc.SummarizerNamespaces;

/**
 * @author pinarpink
 * 
 */
public class AnnotationSetString implements AnnotationSet {

	protected List<AnnotationHolderBean> rows = new ArrayList<AnnotationHolderBean>();


	private int rowNumber = 0;
	private Iterator<AnnotationHolderBean> iterator = null;


	public AnnotationSetString(String annotationString) {
		if (annotationString != null) {
			String[] lines = annotationString.split("\n");

			for (String line : lines) {

				if (line.trim().endsWith("#")) {

					String lline = line.substring(0, line.length() - 1);
					String delims = "[$]";
					String[] tokens = lline.split(delims);

					if (tokens.length == 4) {
						AnnotationHolderBean bn = new AnnotationHolderBean(SummarizerNamespaces.motifsNS+tokens[1], SummarizerNamespaces.motifsNS+tokens[2], tokens[3], true);
						rows.add(bn);

					} else if (tokens.length == 3) {
						AnnotationHolderBean bn = new AnnotationHolderBean(
								SummarizerNamespaces.motifsNS+tokens[1], SummarizerNamespaces.motifsNS+tokens[2],""+Math.random(), false);
						rows.add(bn);

					} else {

						continue;
					}
				} else {
					continue;
				}
			}

		}
		reset();
	}

	/**
	 * @throws UnsupportedOperationException
	 *             always thrown.
	 */

	public void remove() throws java.lang.UnsupportedOperationException {
		throw new java.lang.UnsupportedOperationException("AnnotationSetString.class: Attempt to remove an element");
	}

	/**
	 * Is there another possibility?
	 */
	public boolean hasNext() {
		return iterator.hasNext();
	}



	/**
	 * Moves onto the next result possibility. 
	 */

	public AnnotationHolderBean next() {
		return iterator.next();
	}

	/** Reset this result set back to the beginning */
	public void rewind() {
		reset();
	}

	public void reset() {
		iterator = rows.iterator();
		rowNumber = 0;
	}

	/**
	 * Return the "row" number for the current iterator item
	 */
	public int getRowNumber() {
		return rowNumber;
	}


	/**
	 * Return the number of rows
	 */
	public int size() {
		return rows.size();
	}



}

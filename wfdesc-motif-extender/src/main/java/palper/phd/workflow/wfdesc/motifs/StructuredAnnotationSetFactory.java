package palper.phd.workflow.wfdesc.motifs;
/**
 * 
 */


/**
 * @author pinarpink
 * 
 */

public class StructuredAnnotationSetFactory {
	private static AnnotationSetFormat format = AnnotationSetFormat.INVENTED_FORMAT_PALPER;

	public static AnnotationSet load(String annotationString,
			AnnotationSetFormat formatt) {
		if (formatt == null) {
			// Log.warn(AnnotationSet.class,
			// "Null format - defaulting to INVENTED_FORMAT_PALPER") ;
			return fromInventedFormat(annotationString);

		} else if (formatt != AnnotationSetFormat.INVENTED_FORMAT_PALPER) {

			throw new RuntimeException("Unsupported format!");

		} else
			return fromInventedFormat(annotationString);

	}

	public static AnnotationSet fromInventedFormat(String annotationString) {

		AnnotationSetString stringImpl = new AnnotationSetString(
				annotationString);

		return stringImpl;

	}

}
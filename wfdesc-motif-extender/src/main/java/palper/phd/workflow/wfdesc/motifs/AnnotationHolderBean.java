package palper.phd.workflow.wfdesc.motifs;

/**
 * @author pinarpink
 * 
 */
public class AnnotationHolderBean {
	//
	// new$ConfigParameter#
	// new$DataValue#
	// new$InputAugmentation#
	// new$DataRetrieval#
	// new$Merging#
	// new$DataMovement#
	// new$DataReference#
	// new$OutputExtraction#
	// new$Filtering#
	// new$DataAnalysis#
	// new$ExecutionReport#
	// new_or_existing$AsynchronousInvocation$m1#
	// new$Joining#
	// new$FormatTransformation#
	// new$Splitting#
	// new$DataCleaning#
	// new$HumanInteraction#

	private boolean isShared;
	private String predicate;
	private String motifClass;

	private String id;

	public AnnotationHolderBean(String predicate, String motifClass, String id,
			boolean shared) {
		super();
		this.predicate = predicate;
		this.motifClass = motifClass;
		this.isShared = shared;
		this.id = id;
	}

	public boolean isShared() {
		return isShared;
	}

	public void setShared(boolean isShared) {
		this.isShared = isShared;
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public String getMotifClass() {
		return motifClass;
	}

	public void setMotifClass(String motifClass) {
		this.motifClass = motifClass;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}

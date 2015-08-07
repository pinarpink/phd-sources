/**
 * 
 */
package palper.phd.labeling.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * @author pinarpink
 * 
 */
public enum LabelingOperatorEnum {

	MINT("MINT"), 
	PROPAGATE("PROPAGATE"), 
	DISTRIBUTE("DISTRIBUTE"), 
	GENERALIZE("GENERALIZE");

	private String value;

	private LabelingOperatorEnum(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static LabelingOperatorEnum create(String val) {
		LabelingOperatorEnum[] units = LabelingOperatorEnum.values();
		for (LabelingOperatorEnum unit : units) {
			if (unit.getValue().equalsIgnoreCase(val)) {
				return unit;
			}
		}
		return MINT;
	}
}

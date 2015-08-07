/**
 * 
 */
package palper.phd.workflow.t2flow.lhb;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pinarpink
 * 
 */
public enum LhbNodeTypeEnum {

	CROSS("cross"), DOT("dot"), PORT("port");

	private final String strval;

	private static final Map<String, LhbNodeTypeEnum> lookup = new HashMap<String, LhbNodeTypeEnum>();
	
	static {
		for (LhbNodeTypeEnum d : LhbNodeTypeEnum.values())
			lookup.put(d.getstrval(), d);
	}

	private LhbNodeTypeEnum(String strval) {
		this.strval = strval;

	}

	public String getstrval() {
		return strval;
	}

	public static LhbNodeTypeEnum fromString(String strval) {
		values();
		return lookup.get(strval);
	}

}

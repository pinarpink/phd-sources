/**
 * 
 */
package palper.phd.pipeline.maker;

import java.util.List;

/**
 * @author pinarpink
 * 
 */
public class BNFElementFactory {

	public static String createDataPortElement(String portName) {
		return "(dataport " + portName + ") \n";

	};

	public static String createControlPortElement(String portName) {

		return "(controlport " + portName + ") \n";

	};

	public static String createLabelingOperatorElement(String operName, String dataIn,
			List<String> ctrlInputs, String ctrlOut) {

		String result = "(labeloperator " + " "+operName+" "+  dataIn + " ";

		if (ctrlInputs.size() > 0) {
			result += " ( ";
			for (String ci : ctrlInputs) {
				result += " " + ci + " ";
			}
			result += " ) ";
		} else {
			result += " () ";
		}

		result += " " + ctrlOut + ") \n";

		return result;
	};

	public static String createStringConstantOperatorElement(String operName,
			String constantVale, List<String> ctrlInputs, String ctrlOut,
			String dataOut) {
		String result = "(constantoperator " + " "+operName+" "+ constantVale + " ";

		if (ctrlInputs.size() > 0) {
			result += " ( ";
			for (String ci : ctrlInputs) {
				result += " " + ci + " ";
			}
			result += " ) ";
		} else {
			result += " () ";
		}

		result += " " + ctrlOut + " " + dataOut + " " + ") \n";

		return result;
	};
}

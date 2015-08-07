/**
 * 
 */
package palper.phd.workflow.wfdesc;

/**
 * @author pinarpink
 *
 */
public class URIUtils {


	public static String lastBit(String uriString) {

		if (uriString.contains("/")) {
			int li = uriString.lastIndexOf("/");
			int lt = uriString.length();
			if (li == (lt - 1)) {
				uriString = uriString.substring(0, li);
				int sli = uriString.lastIndexOf("/");
				return uriString.substring(sli + 1);
			} else {

				return uriString.substring(li + 1);
			}
		} else {
			return uriString;
		}
	}
	public static String dropTheLastHash(String namespaceString){
		if (namespaceString.endsWith("#")) {
			int li = namespaceString.length();
		
				return namespaceString.substring(0,li-1);
			 
		} else {
			return namespaceString;
		}
	}
}

/**
 * 
 */
package palper.phd.labeling.astro;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;

/**
 * @author pinarpink
 *
 */
public class VoTableXmlParser extends AbstractXmlDataParser {

  public List<String> getOriginCatalogs() {


    XPathFactory xpathFactory = XPathFactory.newInstance();
    XPath xpath = xpathFactory.newXPath();

    List<String> catalogs = new ArrayList<String>();
    try {
      XPathExpression expr = xpath.compile("//RESOURCE");


      NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
      for (int i = 0; i < nodes.getLength(); i++) {

          XPathExpression nameAttribute = xpath.compile("@ID");
          String name = (String) nameAttribute.evaluate(nodes.item(i), XPathConstants.STRING);
          catalogs.add(name);
      }


    } catch (XPathExpressionException e) {
      e.printStackTrace();
    }

    return catalogs;
  }
  
  public List<String> getSubjects() {
    
    XPathFactory xpathFactory = XPathFactory.newInstance();
    XPath xpath = xpathFactory.newXPath();

    List<String> subjects = new ArrayList<String>();

    try {
      XPathExpression expr = xpath.compile("//RESOURCE/TABLE/DATA/TABLEDATA/TR");
///RESOURCE/TABLE/DATA/TABLEDATA/TR
      NodeList dataRows = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
  
      
      for (int i = 0; i < dataRows.getLength(); i++) {
         XPathExpression lastDataCell = xpath.compile("./TD[last()]/text()");
         String galaxyNAmeList = (String) lastDataCell.evaluate(dataRows.item(i), XPathConstants.STRING);
         
         String[] results = galaxyNAmeList.split("\\s+");
         
         for (String res:results){
           subjects.add(res);
         }
      }

    } catch (XPathExpressionException e) {
      e.printStackTrace();
    }catch (Exception exp) {
      exp.printStackTrace();
    }

    return subjects;
  }
}

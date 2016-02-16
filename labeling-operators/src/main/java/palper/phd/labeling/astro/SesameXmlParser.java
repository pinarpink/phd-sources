package palper.phd.labeling.astro;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;


public class SesameXmlParser extends AbstractXmlDataParser {


  public List<String> getOriginCatalogs() {


    XPathFactory xpathFactory = XPathFactory.newInstance();
    XPath xpath = xpathFactory.newXPath();

    List<String> catalogs = new ArrayList<String>();
    try {
      XPathExpression expr = xpath.compile("//Resolver");

      NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
      for (int i = 0; i < nodes.getLength(); i++) {
        XPathExpression hasAliasExp = xpath.compile("./alias");
        NodeList aliases = (NodeList) hasAliasExp.evaluate(nodes.item(i), XPathConstants.NODESET);
        if (aliases.getLength() > 0) {
          XPathExpression nameAttribute = xpath.compile("@name");
          String name = (String) nameAttribute.evaluate(nodes.item(i), XPathConstants.STRING);
          catalogs.add(name);
        }
      }


    } catch (XPathExpressionException e) {
      e.printStackTrace();
    }

    return catalogs;
  }

}

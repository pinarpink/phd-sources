/**
 * 
 */
package palper.phd.workflow.rewrite;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

/**
 * @author pinarpink
 * 
 */
public class RuleConfigurationReader {

	private 	XMLConfiguration config =null;
	public RuleConfigurationReader() {

		super();
		

		try {
			config = new XMLConfiguration(RuleConfigurationReader.class.getResource("rule-config-collapse-shims.xml"));
			config.setExpressionEngine(new XPathExpressionEngine());
			
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	}
	
	public List<ConfigurationNode> getConfigurations(){

			
		ConfigurationNode root = config.getRootNode();
		return root.getChildren();



	}
	
	public String getConfigName(){
		return (String) config.getRootNode().getAttributes("configName").get(0).getValue();
	}

}

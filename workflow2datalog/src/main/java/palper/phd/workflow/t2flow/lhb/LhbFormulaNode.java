/**
 * 
 */
package palper.phd.workflow.t2flow.lhb;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pinarpink
 *
 */
public class LhbFormulaNode {
	
	private LhbNodeTypeEnum nodeType;
	private List<LhbFormulaNode> children = new ArrayList<LhbFormulaNode>();
	private String portName;

	
	public LhbNodeTypeEnum getNodeType() {
		return nodeType;
	}
	public void setNodeType(LhbNodeTypeEnum nodeType) {
		this.nodeType = nodeType;
	}
	public List<LhbFormulaNode> getChildren() {
		return children;
	}
	public void setChildren(List<LhbFormulaNode> children) {
		this.children = children;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	
	
}

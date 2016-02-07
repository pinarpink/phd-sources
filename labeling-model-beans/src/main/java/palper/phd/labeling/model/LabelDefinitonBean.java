	/**
 * 
 */
package palper.phd.labeling.model;

import java.io.Serializable;

//import palper.phd.labeling.api.ILabelAggregationFunction;

/**
 * @author pinarpink
 *
 */
public class LabelDefinitonBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String labelNameURIString;
	
	private XSDatatypeEnum dataTypeEnum;
	
//	private ILabelAggregationFunction aggregationFunction;
//
//	public ILabelAggregationFunction getAggregationFunction() {
//		return aggregationFunction;
//	}
//
//	public void setAggregationFunction(ILabelAggregationFunction aggregationFunction) {
//		this.aggregationFunction = aggregationFunction;
//	}

	public String getLabelNameURIString() {
		return labelNameURIString;
	}

	public void setLabelNameURIString(String labelNameURIString) {
		this.labelNameURIString = labelNameURIString;
	}

	public XSDatatypeEnum getDataTypeEnum() {
		return dataTypeEnum;
	}

	public void setDataTypeEnum(XSDatatypeEnum dataTypeEnum) {
		this.dataTypeEnum = dataTypeEnum;
	}

	@Override
	public boolean equals(Object arg0) {
		
		LabelDefinitonBean bean = (LabelDefinitonBean) arg0;
		
		return labelNameURIString.equals(bean.getLabelNameURIString());
	}
	

}

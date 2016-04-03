/**
 * 
 */
package palper.phd.labeling.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URI;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * @author pinarpink
 * 
 */
public class LabelingSpecBean implements Serializable {

  /**
	 * 
	 */
  @JsonIgnore
  private static final long serialVersionUID = 1L;


  @JsonProperty("processor")
  private String processorUriString;


  @JsonProperty("operator")
  private LabelingOperatorEnum operator;

  @JsonProperty("sourcePortList")
  private List<String> sourcePortUriStringList;

  @JsonProperty("sinkPortList")
  private List<String> sinkPortUriStringList;


  @JsonIgnore
  private URI labelVectorUri;

  @JsonProperty("depthDifference")
  private int dataLinkDepthDifference;

  @JsonProperty("labelingFunction")
  private String labelingFunctionIdentifier;

  @JsonCreator
  public LabelingSpecBean() {
    super();

  }

  // public List<String> getWfElementUriStringList() {
  // return wfElementUriStringList;
  // }
  //
  // public void setWfElementUriStringList(List<String> wfElementUriStringList) {
  // this.wfElementUriStringList = wfElementUriStringList;
  // }


  public String getProcessorUriString() {
    return processorUriString;
  }

  public void setProcessorUriString(String processorUriString) {
    this.processorUriString = processorUriString;
  }

  public int getDataLinkDepthDifference() {
    return dataLinkDepthDifference;
  }

  public void setDataLinkDepthDifference(int dataLinkDepthDifference) {
    this.dataLinkDepthDifference = dataLinkDepthDifference;
  }

  public LabelingOperatorEnum getOperator() {
    return operator;
  }

  public void setOperator(LabelingOperatorEnum operator) {
    this.operator = operator;
  }

  public String getLabelingFunctionIdentifier() {
    return labelingFunctionIdentifier;
  }

  public void setLabelingFunctionIdentifier(String labelingFunctionIdentifier) {
    this.labelingFunctionIdentifier = labelingFunctionIdentifier;
  }

  public URI getLabelVectorUri() {
    return labelVectorUri;
  }

  public void setLabelVectorUriList(URI labelVectorUri) {
    this.labelVectorUri = labelVectorUri;
  }

  public List<String> getSourcePortUriStringList() {
    return sourcePortUriStringList;
  }

  public void setSourcePortUriStringList(List<String> sourcePortUriStringList) {
    this.sourcePortUriStringList = sourcePortUriStringList;
  }

  public List<String> getSinkPortUriStringList() {
    return sinkPortUriStringList;
  }

  public void setSinkPortUriStringList(List<String> sinkPortUriStringList) {
    this.sinkPortUriStringList = sinkPortUriStringList;
  }

  @Override
  public String toString() {

    StringWriter str = new StringWriter();
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(SerializationConfig.Feature.USE_ANNOTATIONS, true);
      mapper.writeValue(str, this);
    } catch (JsonGenerationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JsonMappingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return str.toString();
    // return "LabelingSpecBean [wfElementUriString=" + wfElementUriString +
    // ","+" operator=" + operator + ", "
    // + " sourcePortUriString=" + sourcePortUriString + ", " +
    // "sinkPortUriString=" + sinkPortUriString + ", " +
    // "labelVectorUri=" + labelVectorUri + "]";
  }

  @Override
  public int hashCode() {

    if (this.operator.equals(LabelingOperatorEnum.MINT)
        || this.operator.equals(LabelingOperatorEnum.PROPAGATE)) {
      return (this.operator.hashCode() * this.processorUriString.hashCode());
    } else {
      return (this.operator.hashCode() * this.sourcePortUriStringList.get(0).hashCode()
          /** this.sinkPortUriStringList.get(0).hashCode()*/ * this.dataLinkDepthDifference);
    }

  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof LabelingSpecBean))
      return false;
    LabelingSpecBean other = (LabelingSpecBean) o;

    if (this.operator.equals(LabelingOperatorEnum.MINT)
        || this.operator.equals(LabelingOperatorEnum.PROPAGATE)) {
      return ((this.operator.equals(other.operator)) && (this.processorUriString
          .equals(other.processorUriString)));
    } else {
      return ((this.operator.equals(other.operator))
          && (this.sourcePortUriStringList.get(0).equals(other.sourcePortUriStringList.get(0)))
         /* && (this.sinkPortUriStringList.get(0).equals(other.sinkPortUriStringList.get(0))) */
          && (this.dataLinkDepthDifference == other.dataLinkDepthDifference));
    }

  }

}

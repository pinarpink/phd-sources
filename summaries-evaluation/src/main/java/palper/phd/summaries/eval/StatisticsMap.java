/**
 * 
 */
package palper.phd.summaries.eval;

import java.util.HashMap;
import java.util.Map;

import palper.phd.workflow.wfdesc.PostSummaryStatistics;
import palper.phd.workflow.wfdesc.PreSummaryStatistics;
import palper.phd.workflow.wfdesc.SummarizationStatistics;

/**
 * @author pinarpink
 * 
 */
public class StatisticsMap {

  public Map<String, PreSummaryStatistics> preSummaryMap =
      new HashMap<String, PreSummaryStatistics>();
  public Map<String, PostSummaryStatistics> postSummaryMap =
      new HashMap<String, PostSummaryStatistics>();

  public Map<String, SummarizationStatistics> comparativeMap =
      new HashMap<String, SummarizationStatistics>();

  public StatisticsMap() {
    super();

  }

  private void calculateComparativeStats() {


    for (Map.Entry<String, PreSummaryStatistics> entry : preSummaryMap.entrySet()) {
      
      PreSummaryStatistics preStat  = entry.getValue();
      
      PostSummaryStatistics postStat = postSummaryMap.get((String)entry.getKey());
      
      SummarizationStatistics comparative = new SummarizationStatistics(preStat, postStat);
      comparativeMap.put(entry.getKey(),comparative);
    }

  }


  public String getMapAsCsv() {
    
    calculateComparativeStats();
    String csvStr =
        " \"Workflow ID\";\"Pre # proc\";\"Pre # links\";\"Post # proc\";\"Post # links\";\"PRE PORTS\";\"GT IMPORTANT\";\"GT UNIMPORTANT\";\"POST PORTS\";\"KEPT IMPORTANT\";\"ABSTRACTED UNIMPORTANT\";\"KEPT UNIMPORTANT\";\"ABSTRACTED IMPORTANT\"\n";

    for (Map.Entry<String, PreSummaryStatistics> entry : preSummaryMap.entrySet()) {
      String wfId = entry.getKey();
      PostSummaryStatistics postsummary = postSummaryMap.get(wfId);
      PreSummaryStatistics presummary = entry.getValue();
      SummarizationStatistics comparative = comparativeMap.get(wfId);
      
      String dataRow = quote(wfId) + ";";
      dataRow = dataRow + presummary.getProcList().size() + ";";
      dataRow = dataRow + presummary.getLinkList().size() + ";";
      dataRow = dataRow + postsummary.getProcList().size() + ";";
      dataRow = dataRow + postsummary.getLinkList().size() + ";";

      dataRow = dataRow + quote(presummary.getNonStrProcessorOutputs().toString()) + ";";
      dataRow = dataRow + quote(presummary.getGroundTruthImportant().toString()) + ";";
      dataRow = dataRow + quote(presummary.getGroundTruthUnimportant().toString()) + ";";
      dataRow = dataRow + quote(postsummary.getOriginalNonStrProcessorOutputs().toString()) + ";";
      
      dataRow = dataRow + comparative.getKeptImportant().size()+ ";";
      dataRow = dataRow + comparative.getAbstractedUnimportant().size()+ ";";
      dataRow = dataRow + comparative.getKeptUnimportant().size()+ ";";
      dataRow = dataRow + comparative.getAbstractedImportant().size()+ ";\n";

      csvStr = csvStr + dataRow;
    }

    return csvStr;
  }

  private String quote(String str) {

    return "\"" + str + "\"";
  }



  public Map<String, PreSummaryStatistics> getPreSummaryMap() {
    return preSummaryMap;
  }



  public Map<String, PostSummaryStatistics> getPostSummaryMap() {
    return postSummaryMap;
  }


}

/**
 * 
 */
package palper.phd.labeling.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author pinarpink
 * 
 */
public class LabelWfTopoSorter {

  
  private int maxOrder;
  private  Map<String, Integer> activityOrders;
  public Map<String, Integer> getActivityOrders() {
    return activityOrders;
  }

  public Map<Integer, List<String>> getActivitiesByOrder() {
    return activitiesByOrder;
  }


  private Map<Integer,List<String>> activitiesByOrder;
  
  public LabelWfTopoSorter() {
    super();
    // TODO Auto-generated constructor stub
  }
  
  public int getMaxOrder() {
    return maxOrder;
  }




  /**
   * @param args
   *  if a processor has no predecessors then its topo-order is 0
   *  if it has processors then the order equals +1 of the max of order of preceeding processors
   */
  public void sort(Model wfdescModel) {

    activityOrders = new HashMap<String, Integer>();

    Resource wfResource = WfDescRdfUtils.getWorkflowResource(wfdescModel);
    Set<Resource> procs = WfDescRdfUtils.getProcessors(wfdescModel, wfResource);

    while (procs.size() > 0) {
      SimpleTuple tobeTransferred = null;
      Iterator iter = procs.iterator();
      
      do {
        Resource proc = (Resource) iter.next();
        Set<Resource> predecessors = WfDescRdfUtils.getControlflowPredecessors(proc, wfdescModel);
        boolean allSorted = true;
        Integer max = 0;


        if (predecessors.size() > 0) {
          for (Resource pred : predecessors) {
            if (activityOrders.containsKey(pred.getURI())) {
              Integer predOrder = activityOrders.get(pred.getURI());
              if (predOrder > max) {
                max = predOrder;
              }
            } else {
              allSorted = false;
            }
          }

          if (allSorted) {
            tobeTransferred = new LabelWfTopoSorter.SimpleTuple(proc, max+1);

            break;
          }
        } else {
          tobeTransferred = new LabelWfTopoSorter.SimpleTuple(proc, 0);
          break;
        }
      }while (iter.hasNext());// while
      if (tobeTransferred != null) {
        activityOrders.put(tobeTransferred.getRes().getURI(), tobeTransferred.getOrder());
        procs.remove(tobeTransferred.getRes());
      }// if
    }// while

    
    calculateReverseMap();
    
  }


  private void calculateReverseMap() {
    
    maxOrder = 0;
    
    activitiesByOrder = new HashMap<Integer, List<String>>();
    
    for (Map.Entry<String, Integer> entry : activityOrders.entrySet()){
      
      if (activitiesByOrder.containsKey(entry.getValue())){
        List<String> activities = activitiesByOrder.get(entry.getValue());
        activities.add(entry.getKey());
        activitiesByOrder.put(entry.getValue(),activities);
      }else{
        List<String> activities = new ArrayList<String>();
        activities.add(entry.getKey());
        activitiesByOrder.put(entry.getValue(), activities);
        
      }
      if (entry.getValue()> maxOrder){
        
        maxOrder = entry.getValue();
      }
    }
    
  }


  class SimpleTuple {
    Resource res;
    Integer order;


    public SimpleTuple(Resource resrc, Integer ord) {
      res = resrc;
      order = ord;
    }

    public Resource getRes() {
      return res;
    }

    public Integer getOrder() {
      return order;
    }

  }
}

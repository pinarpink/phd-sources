/**
 * 
 */
package palper.phd.labeling.operator;

import java.util.HashMap;
import java.util.Iterator;
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

  public LabelWfTopoSorter() {
    super();
    // TODO Auto-generated constructor stub
  }


  /**
   * @param args
   */
  public Map<String, Integer> sort(Model wfdescModel) {

    Map<String, Integer> sorted = new HashMap<String, Integer>();

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
            if (sorted.containsKey(pred.getURI())) {
              Integer predOrder = sorted.get(pred.getURI());
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
        sorted.put(tobeTransferred.getRes().getURI(), tobeTransferred.getOrder());
        procs.remove(tobeTransferred.getRes());
      }// if
    }// while

    return sorted;
  }


  // if a processor has no predecessors then its topo-order is 0
  // if it has processors then the order equals +1 of the max of order of preceeding processors

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

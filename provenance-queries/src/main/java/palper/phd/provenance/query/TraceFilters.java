/**
 * 
 */
package palper.phd.provenance.query;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author pinarpink
 * 
 */
public class TraceFilters {

  public static Set<String> getDistinctDataEntities(Set<ProvTraceStep> trace) {


    Set<String> distinctEntities = new HashSet<String>();

    Iterator traceIterator = trace.iterator();

    while (traceIterator.hasNext()) {
      
      ProvTraceStep step = (ProvTraceStep) traceIterator.next();
      ProvRelationKind relation = step.getRelation();
      
      switch (relation) {
        case generation:
          distinctEntities.add(step.getEnd());
          break;

        case usage:
          distinctEntities.add(step.getStart());
          break;

        case hadMember:
          distinctEntities.add(step.getStart());
          distinctEntities.add(step.getEnd());
          break;

        case itemOf:
          distinctEntities.add(step.getStart());
          distinctEntities.add(step.getEnd());
          break;

      }
    }

    return distinctEntities;
  }
  
  public static Set<String> getDataEntitiesGeneratedByRoleSet(Set<ProvTraceStep> trace, Set<String> portURISet) {


    Set<String> generatedEntities = new HashSet<String>();

    Iterator traceIterator = trace.iterator();

    while (traceIterator.hasNext()) {
      
      ProvTraceStep step = (ProvTraceStep) traceIterator.next();
      
      
      if (step.getRelation().equals(ProvRelationKind.generation) ) {
        String dataId = step.getEnd();
     
        String role = step.getRole();
      //  Resource portResource = ProvRdfUtils.getGenerationRole(dataId, activityId, provModel);
        if (portURISet.contains(role)){
          generatedEntities.add(dataId);
        }

      }
    }
 
    return generatedEntities;
  }
  
}

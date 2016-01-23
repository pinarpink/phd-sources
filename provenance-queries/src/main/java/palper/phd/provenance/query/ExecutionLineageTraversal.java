/**
 * 
 */
package palper.phd.provenance.query;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author pinarpink
 * 
 */
public class ExecutionLineageTraversal {



  private Model provModel;
  
  
  public ExecutionLineageTraversal(Model provModel) {
    super();
    this.provModel = provModel;
  }



  public Set<ProvTraceStep> traverseDown(Resource provNode) {

    Set<ProvTraceStep> steps = new HashSet<ProvTraceStep>();
    if (ProvRdfUtils.isDataArtifact(provModel, provNode)) {
      if (ProvRdfUtils.isDataUsed(provModel, provNode)) {

        Set<Resource> activities = ProvRdfUtils.getUsingActivities(provModel, provNode);
        for (Resource activity : activities) {
          String role = ProvRdfUtils.getUsageRole(provNode.getURI(), activity.getURI(), provModel).getURI();
          steps
              .add(new ProvTraceStep(ProvRelationKind.usage, 
                  provNode.getURI(), 
                  activity.getURI(), 
                  role));
          steps.addAll(traverseDown(activity));
        }

      }
      
      Set<Resource> containers = ProvRdfUtils.getAllContainers(provNode.getURI(), provModel);
      Set<Resource> items = ProvRdfUtils.getAllContainedItems(provNode.getURI(), provModel);

      for (Resource dataItem : containers) {
        steps.add(new ProvTraceStep(ProvRelationKind.itemOf, provNode.getURI(), dataItem.getURI(),null));
        if (ProvRdfUtils.isDataUsed(provModel, dataItem)) {

          Set<Resource> usingActivities = ProvRdfUtils.getUsingActivities(provModel, dataItem);
          for (Resource activity : usingActivities) {
            String role = ProvRdfUtils.getUsageRole(dataItem.getURI(), activity.getURI(), provModel).getURI();
            steps.add(new ProvTraceStep(ProvRelationKind.usage, dataItem.getURI(), activity
                .getURI(), role));
            steps.addAll(traverseDown(activity));
          }
        }
      }
      for (Resource dataItem : items) {
        
        steps.add(new ProvTraceStep(ProvRelationKind.hadMember, provNode.getURI(), dataItem.getURI(),null));
        
        if (ProvRdfUtils.isDataUsed(provModel, dataItem)) {

          Set<Resource> usingActivities = ProvRdfUtils.getUsingActivities(provModel, dataItem);
          for (Resource activity : usingActivities) {
            
            String role = ProvRdfUtils.getUsageRole(dataItem.getURI(), activity.getURI(), provModel).getURI();
            
            steps.add(new ProvTraceStep(ProvRelationKind.usage, dataItem.getURI(), activity.getURI(),role));
            steps.addAll(traverseDown(activity));
          }
        }
      }

    } else if (ProvRdfUtils.isActivity(provModel, provNode)) {
      if (ProvRdfUtils.hasGeneratedData(provModel, provNode)) {

        Set<Resource> dataItems = ProvRdfUtils.getDataGeneratedBy(provModel, provNode);
        for (Resource dataItem : dataItems) {
          
          String role = ProvRdfUtils.getGenerationRole(dataItem.getURI(), provNode.getURI(), provModel).getURI();
          
          steps.add(new ProvTraceStep(ProvRelationKind.generation, provNode.getURI(), dataItem
              .getURI(),role));
          steps.addAll(traverseDown(dataItem));
        }
      }
    }
    return steps;
  }

}

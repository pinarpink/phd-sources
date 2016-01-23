/**
 * 
 */
package palper.phd.provenance.query;

import java.net.URI;

/**
 * @author pinarpink
 *
 */
public class ProvTraceStep {

  
  private ProvRelationKind relation;
  
  private String start;
  
  private String end;
  
  private String role;
  
  
  public String getRole() {
    return role;
  }


  public ProvRelationKind getRelation() {
    return relation;
  }


  public String getStart() {
    return start;
  }


  public String getEnd() {
    return end;
  }


  /**
   * 
   */
  public ProvTraceStep(ProvRelationKind rel, String start, String end, String role) {
    
    
   this.relation = rel;
    this.role = role;
    this.start = start;
    this.end = end;
  }


  @Override
  public boolean equals(Object obj) {
    
    return (obj instanceof ProvTraceStep) 
        && (((ProvTraceStep)obj).getStart()).equals(this.getStart())
        && (((ProvTraceStep)obj).getEnd()).equals(this.getEnd())
       && (((ProvTraceStep)obj).getRelation()).equals(this.getRelation()) ;
  }


  @Override
  public int hashCode() {

    return (start+relation.name()+end).hashCode();
  }


  @Override
  public String toString() {
    if (role == null){
      return this.getStart() + "  " + this.getRelation().name() + "  " + this.getEnd();
    }else{
     return  this.getStart() + "  " + this.getRelation().name()+ "("+this.getRole()+")" + "  " + this.getEnd();
    }
    
  }



}

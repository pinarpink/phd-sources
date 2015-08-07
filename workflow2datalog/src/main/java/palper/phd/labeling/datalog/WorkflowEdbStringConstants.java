/**
 * 
 */
package palper.phd.labeling.datalog;
import java.util.HashMap;
import java.util.Map;

import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.basics.BasicFactory;
import org.deri.iris.storage.IRelation;
import org.deri.iris.storage.simple.SimpleRelationFactory;
import org.deri.iris.terms.TermFactory;
/**
 * @author pinarpink
 *
 */
public class WorkflowEdbStringConstants {


private static final String WF = "workflow";
public static final IPredicate pWF = BasicFactory.getInstance().createPredicate(WF, 1);

private static final String PROC = "process";
public static final IPredicate pPROC = BasicFactory.getInstance().createPredicate(PROC, 1);


private static final String D_LINK = "dataLink";
public static final IPredicate pD_LINK = BasicFactory.getInstance().createPredicate(D_LINK, 3);

private static final String PROC_IN = "processInput";
public static final IPredicate pPROC_IN = BasicFactory.getInstance().createPredicate(PROC_IN, 2);

private static final String PROC_OUT = "processOutput";
public static final IPredicate pPROC_OUT = BasicFactory.getInstance().createPredicate(PROC_OUT, 2);

private static final String PROC_LHB = "hasLhbRoot";
public static final IPredicate pPROC_LHB = BasicFactory.getInstance().createPredicate(PROC_LHB, 2);

private static final String REALZ_BY = "realizedBy";
public static final IPredicate pREALZ_BY = BasicFactory.getInstance().createPredicate(REALZ_BY, 2);



private static final String HAS_LHB = "hasLhbRoot";
public static final IPredicate pHAS_LHB = BasicFactory.getInstance().createPredicate(HAS_LHB, 2);


private static final String FORMULA = "lhbFormula";
public static final IPredicate pFORMULA = BasicFactory.getInstance().createPredicate(FORMULA, 3);


private static final String PLIST = "plist";
public static final IPredicate pPLIST = BasicFactory.getInstance().createPredicate(PLIST, 3);


private static final String PORT_REF = "port";
public static final IPredicate pPORT_REF = BasicFactory.getInstance().createPredicate(PORT_REF, 2);


private static final String WF_IN = "workflowInput";
public static final IPredicate pWF_IN = BasicFactory.getInstance().createPredicate(WF_IN, 2);

private static final String WF_OUT = "workflowOutput";
public static final IPredicate pWF_OUT = BasicFactory.getInstance().createPredicate(WF_OUT, 2);





private static final String DEF_DEP = "definedDepth";
public static final IPredicate pDEF_DEP = BasicFactory.getInstance().createPredicate(DEF_DEP, 3);


private static final String PRED_DEP = "predictedDepth";
public static final IPredicate pPRED_DEP = BasicFactory.getInstance().createPredicate(PRED_DEP, 3);


private static Map<IPredicate, IRelation> factHolder;

public static Map<IPredicate, IRelation> getFactHolder() {
	return factHolder;
}

static{
	factHolder = new HashMap<IPredicate, IRelation>();
	IRelation rWF = new SimpleRelationFactory().createRelation();
	IRelation rPROC = new SimpleRelationFactory().createRelation();
	IRelation rD_LINK = new SimpleRelationFactory().createRelation();
	IRelation rPROC_IN = new SimpleRelationFactory().createRelation();
	IRelation rPROC_OUT = new SimpleRelationFactory().createRelation();
	IRelation rPROC_LHB = new SimpleRelationFactory().createRelation();
	IRelation rREALZ_BY = new SimpleRelationFactory().createRelation();
	IRelation rDEF_DEP = new SimpleRelationFactory().createRelation();
	IRelation rHAS_LHB = new SimpleRelationFactory().createRelation();
	IRelation rFORMULA = new SimpleRelationFactory().createRelation();
	IRelation rPLIST = new SimpleRelationFactory().createRelation();
	IRelation rPORT_REF = new SimpleRelationFactory().createRelation();
	
	
	factHolder.put(pWF, rWF);
	factHolder.put(pPROC,rPROC );
	factHolder.put(pD_LINK,rD_LINK );
	factHolder.put(pPROC_IN,rPROC_IN );
	factHolder.put(pPROC_OUT, rPROC_OUT);
	factHolder.put(pPROC_LHB, rPROC_LHB);
	factHolder.put(pREALZ_BY, rREALZ_BY);
	factHolder.put(pDEF_DEP, rDEF_DEP);
	factHolder.put(pHAS_LHB, rHAS_LHB);
	factHolder.put(pFORMULA, rFORMULA);
	factHolder.put(pPLIST, rPLIST);
	factHolder.put(pPORT_REF, rPORT_REF);
	
	
}
}

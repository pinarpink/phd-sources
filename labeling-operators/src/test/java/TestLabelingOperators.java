import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import palper.phd.labeling.operator.LabelingOperatorHandler;
import palper.phd.labeling.stubs.RdfProvenanceConfig;

public class TestLabelingOperators {

	@Ignore @Test
	public void test() {

		String MINT_VII_237 = "{\"operator\":\"MINT\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/VII_237/\",\"sourcePortUriList\":null,\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/VII_237/out/responseBody\"],\"depthDifference\":0,\"labelingFunction\":\"custom\"}";

		String PROP_Select_logr25_Mtype = "{\"operator\":\"PROPAGATE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Select_logr25_Mtype/\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Select_logr25_Mtype/in/voTable\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Select_logr25_Mtype/out/outputTable\"],\"depthDifference\":0,\"labelingFunction\":\"invariant\"}";

		String PROP_Format_conversion = "{\"operator\":\"PROPAGATE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Format_conversion/\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Format_conversion/in/voTable\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Format_conversion/out/outputTable\"],\"depthDifference\":0,\"labelingFunction\":\"invariant\"}";

		String PROP_Write_Text_File = "{\"operator\":\"PROPAGATE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Write_Text_File/\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Write_Text_File/in/filecontents\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Write_Text_File/out/outputFile\"],\"depthDifference\":0,\"labelingFunction\":\"invariant\"}";

		String MINT_calculate_internal_extinction = "{\"operator\":\"MINT\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/calculate_internal_extinction/\",\"sourcePortUriList\":null,\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/calculate_internal_extinction/out/STDOUT\"],\"depthDifference\":0,\"labelingFunction\":\"custom\"}";

		String MINT_SesameXML = "{\"operator\":\"MINT\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/SesameXML/\",\"sourcePortUriList\":null,\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/SesameXML/out/return\"],\"depthDifference\":0,\"labelingFunction\":\"custom\"}";

		String PROP_Extracting_RA = "{\"operator\":\"PROPAGATE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Extracting_RA/\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Extracting_RA/in/xml-text\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Extracting_RA/out/nodelist\"],\"depthDifference\":0,\"labelingFunction\":\"invariant\"}";
		String GEN_Extracting_RA_to_Flatten_List = "{\"operator\":\"GENERALIZE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/datalink?from=processor/Extracting_RA/out/nodelist&to=processor/Flatten_List/in/inputlist\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Extracting_RA/out/nodelist\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Flatten_List/in/inputlist\"],\"depthDifference\":-1,\"labelingFunction\":\"cumulative\"}";
		String PROP_Flatten_List = "{\"operator\":\"PROPAGATE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Flatten_List/\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Flatten_List/in/inputlist\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Flatten_List/out/outputlist\"],\"depthDifference\":0,\"labelingFunction\":\"invariant\"}";

		String PROP_Extracting_DEC = "{\"operator\":\"PROPAGATE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Extracting_DEC/\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Extracting_DEC/in/xml-text\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Extracting_DEC/out/nodelist\"],\"depthDifference\":0,\"labelingFunction\":\"invariant\"}";

		String GEN_Extracting_DEC_to_Flatten_List_2 = "{\"operator\":\"GENERALIZE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/datalink?from=processor/Extracting_DEC/out/nodelist&to=processor/Flatten_List_2/in/inputlist\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Extracting_DEC/out/nodelist\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Flatten_List_2/in/inputlist\"],\"depthDifference\":-1,\"labelingFunction\":\"cumulative\"}";
		String PROP_Flatten_List_2 = "{\"operator\":\"PROPAGATE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Flatten_List_2/\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Flatten_List_2/in/inputlist\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Flatten_List_2/out/outputlist\"],\"depthDifference\":0,\"labelingFunction\":\"invariant\"}";

		String GEN_SesameXML_to_out_sesame = "{\"operator\":\"GENERALIZE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/datalink?from=processor/SesameXML/out/return&to=out/sesame\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/SesameXML/out/return\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/out/sesame\"],\"depthDifference\":-1,\"labelingFunction\":\"cumulative\"}";
		//String GEN_Flatten_List_to_out_ra = "{\"operator\":\"GENERALIZE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/datalink?from=processor/Flatten_List/out/outputlist&to=out/ra\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Flatten_List/out/outputlist\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/out/ra\"],\"depthDifference\":-1,\"labelingFunction\":\"cumulative\"}";
		String GEN_calculate_int_ext_to_out_data_int_ext = "{\"operator\":\"GENERALIZE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/datalink?from=processor/calculate_internal_extinction/out/STDOUT&to=out/data_internal_extinction\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/calculate_internal_extinction/out/STDOUT\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/out/data_internal_extinction\"],\"depthDifference\":-1,\"labelingFunction\":\"cumulative\"}";
		String GEN_Format_conv_to_out_logr25 = "{\"operator\":\"GENERALIZE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/datalink?from=processor/Format_conversion/out/outputTable&to=out/logr25\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Format_conversion/out/outputTable\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/out/logr25\"],\"depthDifference\":-1,\"labelingFunction\":\"cumulative\"}";
		String GEN_VII_237_to_out_leda_output = "{\"operator\":\"GENERALIZE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/datalink?from=processor/VII_237/out/responseBody&to=out/leda_output\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/VII_237/out/responseBody\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/out/leda_output\"],\"depthDifference\":-1,\"labelingFunction\":\"cumulative\"}";
		//String GEN_Flatten_List_2_to_out_dec = "{\"operator\":\"GENERALIZE\",\"wfElementUri\":\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/datalink?from=processor/Flatten_List_2/out/outputlist&to=out/dec\",\"sourcePortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/processor/Flatten_List_2/out/outputlist\"],\"sinkPortUriList\":[\"http://ns.taverna.org.uk/2010/workflowBundle/1b2dee3b-3556-4706-86fd-e417705f20c8/workflow/Workflow4/out/dec\"],\"depthDifference\":-1,\"labelingFunction\":\"cumulative\"}";
		LabelingOperatorHandler handler = new LabelingOperatorHandler();

		try {
			handler.execute(MINT_VII_237);

			handler.execute(MINT_SesameXML);

			handler.execute(MINT_calculate_internal_extinction);

			handler.execute(PROP_Extracting_RA);
			handler.execute(PROP_Extracting_DEC);
			handler.execute(GEN_Extracting_RA_to_Flatten_List);
			handler.execute(GEN_Extracting_DEC_to_Flatten_List_2);
			handler.execute(PROP_Flatten_List);
			handler.execute(PROP_Flatten_List_2);

			handler.execute(PROP_Select_logr25_Mtype);

			handler.execute(PROP_Format_conversion);
			handler.execute(PROP_Write_Text_File);
			handler.execute(GEN_SesameXML_to_out_sesame);
			//handler.execute(GEN_Flatten_List_to_out_ra);
			handler.execute(GEN_calculate_int_ext_to_out_data_int_ext);

			handler.execute(GEN_Format_conv_to_out_logr25);

			handler.execute(GEN_VII_237_to_out_leda_output);

			//handler.execute(GEN_Flatten_List_2_to_out_dec);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void writeLabelModelToFile() {

		try {
			File labelFile = new File(
					"/Users/pinarpink/Desktop/SummarizationRules/dataset/labels-of-2920.wfdesc.ttl");
			OutputStream oss;
			oss = new FileOutputStream(labelFile);
			RdfProvenanceConfig.getInstance().getLabelingResultModel()
					.write(oss, "TURTLE", null);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}

	}
	@BeforeClass
	public static void initialize(){
		RdfProvenanceConfig.initialize("/Users/pinarpink/Desktop/SummarizationRules/dataset/2920-motifs.wfdesc.ttl","/Users/pinarpink/Desktop/Labeling_Workflows/2920_WF_RUN-4/workflowrun.prov.ttl");
	}

}

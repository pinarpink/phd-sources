package palper.phd.labeling.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

import palper.phd.labeling.model.LabelDefinitonBean;
import palper.phd.labeling.model.LabelInstanceBean;
import palper.phd.labeling.model.LabelingOperatorEnum;
import palper.phd.labeling.model.LabelingSpecBean;
import palper.phd.labeling.stubs.LabelVectorStub;
import palper.phd.labeling.stubs.MintingStubs;
import palper.phd.labeling.stubs.PropagationStubs;
import palper.phd.labeling.stubs.RdfProvenanceConfig;
import palper.phd.provenance.query.ProvRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.Lock;

/**
 * 
 */

/**
 * @author pinarpink
 * 
 */
public class LabelingOperHandler {

	public void execute(String labelingSpecJSON) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		LabelingSpecBean spec = mapper.readValue(labelingSpecJSON,
				LabelingSpecBean.class);
		execute(spec);

	}

	public void execute(LabelingSpecBean command) throws Exception {

		if (!isValid(command)) {

			throw new IllegalArgumentException();
		}

		// In the idealized scenario there is some lookup code here that
		// understand the specific grounding with which the labels will be
		// represented.
		// in our case it is RDF
		// we currently statically build-up a config object that points to the
		// RDF files
		// we also convert the returned labels to rdf assertions and put them
		// in the result file.

		List<LabelInstanceBean> results = new ArrayList<LabelInstanceBean>();
		if (command.getOperator().equals(LabelingOperatorEnum.MINT)) {
			results.addAll(executeMint(command));

		} else if (command.getOperator().equals(LabelingOperatorEnum.PROPAGATE)) {

			results.addAll(executePropagate(command));
		} else if (command.getOperator()
				.equals(LabelingOperatorEnum.DISTRIBUTE)) {
			results.addAll(executeDistribute(command));
		} else if (command.getOperator()
				.equals(LabelingOperatorEnum.GENERALIZE)) {
			results.addAll(executeGeneralize(command));
		}
		submitResultsToModel(results);
	}

	private boolean isValid(LabelingSpecBean command) {

		if (command.getSinkPortUriStringList() == null) {
			return false;

		}

		if (command.getSinkPortUriStringList().size() == 0) {
			return false;

		}

		if (command.getOperator().equals(LabelingOperatorEnum.PROPAGATE)) {
			if (command.getSourcePortUriStringList().size() == 0) {
				return false;
			}

		} else if (command.getOperator()
				.equals(LabelingOperatorEnum.GENERALIZE)) {
			if ((command.getSinkPortUriStringList().size() != 1)
					|| (command.getSourcePortUriStringList().size() != 1)) {
				return false;
			}

		}

		return true;
	}

	private List<LabelInstanceBean> executeGeneralize(LabelingSpecBean command) {

		// for the GENERALIZE operators the source and sink port lists contain
		// only one item.
		String sourcePortUriString = command.getSourcePortUriStringList().get(0);

		Set<Resource> generationDataArtifacts = ProvRdfUtils
				.getAllGenerationInstances(sourcePortUriString,
						RdfProvenanceConfig.getInstance().getProvTraceModel());

		// only one of the source artifacts should suffice for finding its
		// container
		Resource oneSourceArtifact = generationDataArtifacts.iterator().next();

		// find the target data artifacts
		// this method should always return the same target artifact when
		// inquired with different source artifacts
		 Resource targetArtifact = ProvRdfUtils.getContainerAtDepth(
				 oneSourceArtifact.getURI(),
		 Math.abs(command.getDataLinkDepthDifference()),
		 RdfProvenanceConfig.getInstance().getProvTraceModel());

		// TODO we need to check if the target artifacts do fill the sink
		// role!!!.
		// Currently we just assume that it does.


		List<LabelInstanceBean> allresults = new ArrayList<LabelInstanceBean>();

		// for each Label Definition in the label vector.
		// inquire model to obtain corresponding label instances.
		// clone the label instance, change its target to the data
		// artifact at the sink port
		// put it into the resulting label model.
		for (LabelDefinitonBean def : LabelVectorStub.getInstance()
				.getLabelDefinitions()) {

			List<LabelInstanceBean> resultsPerLabelDef = new ArrayList<LabelInstanceBean>();
			for(Resource sourceArtefact:generationDataArtifacts){
			
				List<LabelInstanceBean> lblList = ProvRdfUtils
						.getLabelsOfArtifacts(def.getLabelNameURIString(),
								sourceArtefact.getURI(), RdfProvenanceConfig
										.getInstance().getLabelingResultModel());
				for(LabelInstanceBean myLbl:lblList){
					resultsPerLabelDef.add(PropagationStubs.cloneLabelInstance(myLbl, targetArtifact.getURI()));
				}
			}
//			if (def.getAggregationFunction() != null){
//				allresults.addAll(def.getAggregationFunction().run(resultsPerLabelDef));
//			}else{
				allresults.addAll(resultsPerLabelDef);
//			}

		}
		return allresults;
	}

	private List<LabelInstanceBean> executeDistribute(LabelingSpecBean command)
			throws Exception {

		List<LabelInstanceBean> results = new ArrayList<LabelInstanceBean>();

		List<String> sourcePortUriStringList = command
				.getSourcePortUriStringList();
		String sourcePortUriString = sourcePortUriStringList.get(0);

		// for each source port create labels and associate it to the data
		// artifact(s) at sink port

		Set<Resource> generationDataArtifacts = ProvRdfUtils
				.getAllGenerationInstances(sourcePortUriString,
						RdfProvenanceConfig.getInstance().getProvTraceModel());
		for (Resource sourceArtifact : generationDataArtifacts) {
			// find the target data artifacts
			// TODO we need to check if the target artifacts do fill the
			// sink role!!!.
			// Currently we just assume that it does.
			// this is where we will use the sinkportURI parameter
			Set<Resource> targetArtifacts = ProvRdfUtils
					.getCollectionMembersAtDepth(sourceArtifact.getURI(), Math
							.abs(command.getDataLinkDepthDifference()),
							RdfProvenanceConfig.getInstance()
									.getProvTraceModel());



			for (Resource target : targetArtifacts) {

				List<LabelInstanceBean> allresultsForOnesink = new ArrayList<LabelInstanceBean>();
				// for each Label Definition in the label vector.
				// inquire model to obtain corresponding label instances.
				// clone the label instance, change its target to the data
				// artifact at the sink port
				// put it into the resulting label model.
				for (LabelDefinitonBean def : LabelVectorStub.getInstance()
						.getLabelDefinitions()) {

					List<LabelInstanceBean> lblList = ProvRdfUtils
							.getLabelsOfArtifacts(def.getLabelNameURIString(),
									sourceArtifact.getURI(),
									RdfProvenanceConfig.getInstance()
											.getLabelingResultModel());
					for (LabelInstanceBean myLbl : lblList) {

						allresultsForOnesink.add(PropagationStubs
								.cloneLabelInstance(myLbl, target.getURI()));
					}

				}

				results.addAll(allresultsForOnesink);

			}

		}

		return results;
	}

	private List<LabelInstanceBean> executePropagate(LabelingSpecBean command) {
		List<LabelInstanceBean> results = new ArrayList<LabelInstanceBean>();

		Set<Resource> instantiations = ProvRdfUtils
				.getInstantiationsOfOperation(command.getProcessorUriString()/*.getWfElementUriStringList().get(0)*/,
						RdfProvenanceConfig.getInstance().getProvTraceModel());
		List<String> sinkPortUriStringList = command.getSinkPortUriStringList();
		List<String> sourcePortUriStringList = command
				.getSourcePortUriStringList();

		// for each invocation of that operation
		for (Resource inst : instantiations) {
			for (LabelDefinitonBean def : LabelVectorStub.getInstance()
					.getLabelDefinitions()) {
				// for each sink port create labels and associate it to the data
				// artifact(s) at sink port
				for (String sinkPortUriString : sinkPortUriStringList) {

					List<LabelInstanceBean> allresultsForOnesink = new ArrayList<LabelInstanceBean>();
					Set<Resource> dataArtifacts = ProvRdfUtils
							.getGenerationsWithRoleAndActivity(inst.getURI(),
									sinkPortUriString, RdfProvenanceConfig
											.getInstance().getProvTraceModel());
					if (dataArtifacts.size() > 1) {
						System.out
								.println("\n ---> MULTIPLE ARTIFACTS IN A ROLE IN A GENERATION \n But we will pick out the first one!!\n");

					}
					for (Resource dataArtefactAtSinkPort : dataArtifacts) {

						for (String sourcePortUriString : sourcePortUriStringList) {
							Set<Resource> usageDataArtifacts = ProvRdfUtils
									.getUsagesWithRoleAndActivity(
											inst.getURI(), sourcePortUriString,
											RdfProvenanceConfig.getInstance()
													.getProvTraceModel());

							if (usageDataArtifacts.size() > 1) {
								System.out
										.println("\n ---> MULTIPLE ARTIFACTS IN A ROLE IN A USAGE \n But we will pick out the first one!!\n");

							}

							for (Resource usageDataArtefact : usageDataArtifacts) {

								List<LabelInstanceBean> lblList = ProvRdfUtils
										.getLabelsOfArtifacts(
												def.getLabelNameURIString(),
												usageDataArtefact.getURI(),
												RdfProvenanceConfig
														.getInstance()
														.getLabelingResultModel());

								for (LabelInstanceBean labelAtSourceData : lblList) {

									LabelInstanceBean propagatedLabelsForOneLabDef = PropagationStubs
											.cloneLabelInstance(
													labelAtSourceData,
													dataArtefactAtSinkPort
															.getURI());
									allresultsForOnesink
											.add(propagatedLabelsForOneLabDef);
								}
							}

						}

//						if (def.getAggregationFunction() != null) {
//							results.addAll(def.getAggregationFunction().run(
//									allresultsForOnesink));
//						} else {
							results.addAll(allresultsForOnesink);
//						}

					}

				}
			}

		}
		return results;

	}

	private List<LabelInstanceBean> executeMint(LabelingSpecBean command) {

		List<LabelInstanceBean> results = new ArrayList<LabelInstanceBean>();

		Set<Resource> instantiations = ProvRdfUtils
				.getInstantiationsOfOperation(command.getProcessorUriString()/*.getWfElementUriStringList().get(0)*/,
						RdfProvenanceConfig.getInstance().getProvTraceModel());

		// for each invocation of that operation
		for (Resource inst : instantiations) {

			List<String> sinkPortUriStringList = command
					.getSinkPortUriStringList();

			// for each sink port create labels and associate
			for (String sinkPortUriString : sinkPortUriStringList) {
				List<LabelInstanceBean> allresultsForOnesink = MintingStubs
						.invokeMintingFunction(command.getProcessorUriString()/*.getWfElementUriStringList().get(0)*/,
								inst.getURI());

				Set<Resource> dataArtifacts = ProvRdfUtils
						.getGenerationsWithRoleAndActivity(inst.getURI(),
								sinkPortUriString, RdfProvenanceConfig
										.getInstance().getProvTraceModel());
				if (dataArtifacts.size() > 1) {
					System.out
							.println("\n ---> MULTIPLE ARTIFACTS IN A ROLE IN A GENERATION \n But we will pick out the first one!!\n");

				}

				for (LabelInstanceBean lins : allresultsForOnesink) {

					lins.setLabelTargetURIString(dataArtifacts.iterator()
							.next().getURI());

				}

				results.addAll(allresultsForOnesink);

			}

		}
		return results;
	}

	private void submitResultsToModel(List<LabelInstanceBean> results) {
		if (results.size() > 0) {
			Model mod = RdfProvenanceConfig.getInstance()
					.getLabelingResultModel();

			List<Statement> sts = new ArrayList<Statement>();
			for (LabelInstanceBean li : results) {

				sts.add(mod.createLiteralStatement(
						mod.createResource(li.getLabelTargetURIString()),
						mod.createProperty(li.getDefiniton()
								.getLabelNameURIString().toString()),
						li.getValue()));
			}

			mod.enterCriticalSection(Lock.READ);
			try {

				mod.add(sts);

			} finally {
				mod.leaveCriticalSection();
			}
		}
	}
}

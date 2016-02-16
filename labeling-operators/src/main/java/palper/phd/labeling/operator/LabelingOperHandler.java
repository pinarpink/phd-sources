package palper.phd.labeling.operator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;

import palper.phd.labeling.api.ILabelMintingFunction;
import palper.phd.labeling.astro.AstroLabelVectorStub;
import palper.phd.labeling.model.LabelDefinitonBean;
import palper.phd.labeling.model.LabelInstanceBean;
import palper.phd.labeling.model.LabelingOperatorEnum;
import palper.phd.labeling.model.LabelingSpecBean;
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

	  if (command.getOperator().equals(LabelingOperatorEnum.MINT)){
	     if ((command.getSinkPortUriStringList() == null) || (command.getLabelingFunctionIdentifier() == null) || (command.getProcessorUriString() == null)) {
	          return false;
	        }
	        if ((command.getSinkPortUriStringList().size() == 0)) {
	          return false;
	        }

	  }else if (command.getOperator().equals(LabelingOperatorEnum.PROPAGATE)){
	    if ((command.getSinkPortUriStringList() == null) || (command.getSourcePortUriStringList() == null) || (command.getProcessorUriString() == null)) {
	      return false;
	    }
	    if ((command.getSinkPortUriStringList().size() == 0) ||(command.getSourcePortUriStringList().size() == 0)) {
	      return false;
	    }
	  }else if (command.getOperator().equals(LabelingOperatorEnum.GENERALIZE) || command.getOperator().equals(LabelingOperatorEnum.DISTRIBUTE)){
	    if ((command.getProcessorUriString() == null) || (command.getSourcePortUriStringList() == null) || (command.getDataLinkDepthDifference() == 0)) {
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
						LabellingExecutionConfig.getInstance().getProvTraceModel());

		// only one of the source artifacts should suffice for finding its
		// container
		Resource oneSourceArtifact = generationDataArtifacts.iterator().next();

		// find the target data artifacts
		// this method should always return the same target artifact when
		// inquired with different source artifacts
		 Resource targetArtifact = ProvRdfUtils.getContainerAtDepth(
				 oneSourceArtifact.getURI(),
		 Math.abs(command.getDataLinkDepthDifference()),
		 LabellingExecutionConfig.getInstance().getProvTraceModel());

		// TODO we need to check if the target artifacts do fill the sink
		// role!!!.
		// Currently we just assume that it does.


		List<LabelInstanceBean> allresults = new ArrayList<LabelInstanceBean>();

		// for each Label Definition in the label vector.
		// inquire model to obtain corresponding label instances.
		// clone the label instance, change its target to the data
		// artifact at the sink port
		// put it into the resulting label model.
		for (LabelDefinitonBean def : AstroLabelVectorStub.getLabelDefinitions()) {

			List<LabelInstanceBean> resultsPerLabelDef = new ArrayList<LabelInstanceBean>();
			for(Resource sourceArtefact:generationDataArtifacts){
			
				List<LabelInstanceBean> lblList = ProvRdfUtils
						.getLabelsOfArtifacts(def.getLabelNameURIString(),
								sourceArtefact.getURI(), LabellingExecutionConfig
										.getInstance().getLabelingResultModel());
				for(LabelInstanceBean myLbl:lblList){
					resultsPerLabelDef.add(cloneLabelInstance(myLbl, targetArtifact.getURI()));
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
						LabellingExecutionConfig.getInstance().getProvTraceModel());
		for (Resource sourceArtifact : generationDataArtifacts) {
			// find the target data artifacts
			// TODO we need to check if the target artifacts do fill the
			// sink role!!!.
			// Currently we just assume that it does.
			// this is where we will use the sinkportURI parameter
			Set<Resource> targetArtifacts = ProvRdfUtils
					.getCollectionMembersAtDepth(sourceArtifact.getURI(), Math
							.abs(command.getDataLinkDepthDifference()),
							LabellingExecutionConfig.getInstance()
									.getProvTraceModel());



			for (Resource target : targetArtifacts) {

				List<LabelInstanceBean> allresultsForOnesink = new ArrayList<LabelInstanceBean>();
				// for each Label Definition in the label vector.
				// inquire model to obtain corresponding label instances.
				// clone the label instance, change its target to the data
				// artifact at the sink port
				// put it into the resulting label model.
				for (LabelDefinitonBean def : AstroLabelVectorStub.getLabelDefinitions()) {

					List<LabelInstanceBean> lblList = ProvRdfUtils
							.getLabelsOfArtifacts(def.getLabelNameURIString(),
									sourceArtifact.getURI(),
									LabellingExecutionConfig.getInstance()
											.getLabelingResultModel());
					for (LabelInstanceBean myLbl : lblList) {

						allresultsForOnesink.add(cloneLabelInstance(myLbl, target.getURI()));
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
						LabellingExecutionConfig.getInstance().getProvTraceModel());
		List<String> sinkPortUriStringList = command.getSinkPortUriStringList();
		List<String> sourcePortUriStringList = command
				.getSourcePortUriStringList();

		// for each invocation of that operation
		for (Resource inst : instantiations) {
			for (LabelDefinitonBean def : AstroLabelVectorStub.getLabelDefinitions()) {
				// for each sink port create labels and associate it to the data
				// artifact(s) at sink port
				for (String sinkPortUriString : sinkPortUriStringList) {

					List<LabelInstanceBean> allresultsForOnesink = new ArrayList<LabelInstanceBean>();
					Set<Resource> dataArtifacts = ProvRdfUtils
							.getGenerationsWithRoleAndActivity(inst.getURI(),
									sinkPortUriString, LabellingExecutionConfig
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
											LabellingExecutionConfig.getInstance()
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
												LabellingExecutionConfig
														.getInstance()
														.getLabelingResultModel());

								for (LabelInstanceBean labelAtSourceData : lblList) {

									LabelInstanceBean propagatedLabelsForOneLabDef = cloneLabelInstance(
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
						LabellingExecutionConfig.getInstance().getProvTraceModel());

		// for each invocation of that operation
		for (Resource inst : instantiations) {

			List<String> sinkPortUriStringList = command
					.getSinkPortUriStringList();

			// for each sink port create labels and associate
			for (String sinkPortUriString : sinkPortUriStringList) {
				List<LabelInstanceBean> allresultsForOnesink = invokeMintingFunction(command.getLabelingFunctionIdentifier()/*.getWfElementUriStringList().get(0)*/,
								inst.getURI());

				Set<Resource> dataArtifacts = ProvRdfUtils
						.getGenerationsWithRoleAndActivity(inst.getURI(),
								sinkPortUriString, LabellingExecutionConfig
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
			Model mod = LabellingExecutionConfig.getInstance()
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
	

	  
 private List<LabelInstanceBean> invokeMintingFunction(String functionName,
	      String activityInstanceUriString) {
	    List<LabelInstanceBean> resultLabels = null;
	    try {
	     resultLabels = new ArrayList<LabelInstanceBean>();

	      Map<String, InputStream> results = new HashMap<String, InputStream>();
	      Map<String, String> datarefsByPort =
	          ProvRdfUtils.getPortArtefactMapForActivity(activityInstanceUriString,
	              LabellingExecutionConfig.getInstance().getProvTraceModel());

	      for (Map.Entry<String, String> entry : datarefsByPort.entrySet()) {
	        String portNAme = entry.getKey();

	        String dataRef = entry.getValue();

	        Resource contentPath =
	            ProvRdfUtils.getDataContentPath(dataRef, LabellingExecutionConfig.getInstance()
	                .getProvTraceModel());

	          File dataContentFile = new File(new URI(contentPath.getURI()));
	          FileInputStream is = new FileInputStream(dataContentFile);


	          // TODO
	          // here we can check whether file contains binary or text
	          // content by
	          // checking file extension
	          // for now we will assume it contains text.

	          results.put(portNAme, is);


	      }

	      Class cls = Class.forName(functionName);
	      Object obj = cls.newInstance();

	      resultLabels.addAll(((ILabelMintingFunction)obj).run(results));
	      
	    } catch (URISyntaxException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    } catch (FileNotFoundException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    } catch (ClassNotFoundException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    } catch (InstantiationException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    } catch (IllegalAccessException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	    return resultLabels;
	  }
 
 private static LabelInstanceBean cloneLabelInstance(
     LabelInstanceBean original, String newTargetURIString) {

 LabelInstanceBean clone = new LabelInstanceBean();
 clone.setDefiniton(original.getDefiniton());
 clone.setValue(original.getValue());
 clone.setLabelTargetURIString(newTargetURIString);
 return clone;
}
}

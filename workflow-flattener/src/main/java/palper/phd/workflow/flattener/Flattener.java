/**
 * 
 */
package palper.phd.workflow.flattener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import palper.phd.workflow.wfdesc.SummarizerNamespaces;
import palper.phd.workflow.wfdesc.URIUtils;
import palper.phd.workflow.wfdesc.WfDescRdfUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * @author pinarpink
 * 
 */
public class Flattener {


  private Model flattenedWfdesc;

  private Map<String, Set<String>> flattenedProcessorGroupings = new HashMap<String, Set<String>>();

  private Set<String> toplevelVisibleOutPorts = new HashSet<String>();


  public Flattener(Model model) {

    // wfdesc = ModelFactory.createModelForGraph(model.getGraph());
    flattenedWfdesc = model;


    Resource wfResource = WfDescRdfUtils.getWorkflowResource(flattenedWfdesc);
    Set<Resource> workflowTypeProcessors =
        WfDescRdfUtils.getSubWorkflows(flattenedWfdesc, wfResource);

    Set<Resource> allTopLevelProcessorOutputs =
        WfDescRdfUtils.getNonStrNonWfProcessorsOutputPorts(flattenedWfdesc, wfResource);

    for (Resource avisibleOut : allTopLevelProcessorOutputs) {
      toplevelVisibleOutPorts.add(avisibleOut.getURI());

    }


    Set<Resource> workflowDefinitionsInBundle = new HashSet<Resource>();

    for (Resource subwf : workflowTypeProcessors) {


      String uniquePrefix = WfDescRdfUtils.getLabel(subwf, flattenedWfdesc);
      Resource wfdef = WfDescRdfUtils.getSpecializedWorkflowDefinition(subwf, flattenedWfdesc);

      workflowDefinitionsInBundle.add(wfdef);



      Set<String> groupedProcessorUris = new HashSet<String>();

      Set<Resource> defProcessors = WfDescRdfUtils.getProcessors(flattenedWfdesc, wfdef);

      Map<String, String> originalToCloneTempMap = new HashMap<String, String>();

      for (Resource defProc : defProcessors) {

        Map<String, String> mappingsPerProcessor =
            cloneAndAddProcessor(flattenedWfdesc, defProc, uniquePrefix, wfResource);

        for (Map.Entry<String, String> entry : mappingsPerProcessor.entrySet()) {
          originalToCloneTempMap.put(entry.getKey(), entry.getValue());
        }


        groupedProcessorUris.add(originalToCloneTempMap.get(defProc.getURI()));

      }

      flattenedProcessorGroupings.put(subwf.getURI(), groupedProcessorUris);

      Set<Resource> defDatalinks = WfDescRdfUtils.getDataLinks(flattenedWfdesc, wfdef);

      for (Resource defLink : defDatalinks) {


        cloneAndAddDatalink(flattenedWfdesc, defLink, uniquePrefix, wfResource, subwf,
            originalToCloneTempMap);
      }

    }
    for (Resource subwf : workflowTypeProcessors) {


      removeProcessorFromBundle(wfResource, subwf, flattenedWfdesc);

    }

    decorateTopLevelVisiblePorts(flattenedWfdesc);

  }



  private void removeProcessorFromBundle(Resource topLevelWf, Resource procOfWfType, Model model) {

    Set<Resource> inputs = WfDescRdfUtils.getInputPorts(procOfWfType, model);

    for (Resource in : inputs) {

      Set<Resource> inLinks = WfDescRdfUtils.getDataLinksWithSinkPort(model, in);
      Set<Resource> outLinks = WfDescRdfUtils.getDataLinksWithSourcePort(model, in);

      for (Resource inlink : inLinks) {
        for (Resource outlink : outLinks) {

          Resource sourcePort = WfDescRdfUtils.getSource(model, inlink);
          Resource targetPort = WfDescRdfUtils.getSink(model, outlink);
          linkPorts(model, sourcePort, targetPort, topLevelWf);

        }
      }

      for (Resource inlink : inLinks) {

        model.removeAll(null, null, inlink);
        model.removeAll(inlink, null, null);
      }

      for (Resource outlink : outLinks) {

        model.removeAll(null, null, outlink);
        model.removeAll(outlink, null, null);
      }
      model.removeAll(null, null, in);
      model.removeAll(in, null, null);
    }

    Set<Resource> outputs = WfDescRdfUtils.getOutputPorts(procOfWfType, model);

    for (Resource out : outputs) {

      Set<Resource> inLinks = WfDescRdfUtils.getDataLinksWithSinkPort(model, out);
      Set<Resource> outLinks = WfDescRdfUtils.getDataLinksWithSourcePort(model, out);

      for (Resource inlink : inLinks) {
        for (Resource outlink : outLinks) {

          Resource sourcePort = WfDescRdfUtils.getSource(model, inlink);

          toplevelVisibleOutPorts.add(sourcePort.getURI());
          Resource targetPort = WfDescRdfUtils.getSink(model, outlink);
          linkPorts(model, sourcePort, targetPort, topLevelWf);

        }
      }

      for (Resource inlink : inLinks) {

        model.removeAll(null, null, inlink);
        model.removeAll(inlink, null, null);
      }

      for (Resource outlink : outLinks) {

        model.removeAll(null, null, outlink);
        model.removeAll(outlink, null, null);
      }
      model.removeAll(null, null, out);
      model.removeAll(out, null, null);
    }



    model.removeAll(null, null, procOfWfType);
    model.removeAll(procOfWfType, null, null);
  }

  public Model getFlattenedWfdesc() {

    return flattenedWfdesc;
  }


  private static Resource linkPorts(Model model, Resource srcPort, Resource snkPort,
      Resource wfResource) {
    Resource newLink = model.createResource("http://cloneLink/" + Math.random());
    model.add(newLink, model.getProperty(SummarizerNamespaces.rdfNS + "type"),
        model.getResource(SummarizerNamespaces.wfdescNS + "DataLink"));

    model.add(wfResource, model.getProperty(SummarizerNamespaces.wfdescNS, "hasDataLink"), newLink);

    model.add(newLink, model.getProperty(SummarizerNamespaces.wfdescNS + "hasSource"), srcPort);

    model.add(newLink, model.getProperty(SummarizerNamespaces.wfdescNS + "hasSink"), snkPort);
    model.addLiteral(newLink, model.getProperty(SummarizerNamespaces.rdfsNS + "comment"),
        "tracelink");
    return newLink;
  }



  private static void cloneAndAddDatalink(Model model, Resource link, String namePrefix,
      Resource parentWfDef, Resource processorOfWfType, Map<String, String> originalToClone) {


    Resource cloneLink =
        model.createResource(parentWfDef.getURI() + "datalink/" + "l" + Math.random() + "/");
    cloneLink = model.getResource(cloneLink.getURI());
    //
    // System.out.println("Original: " + link.getURI() + "\n" + "Clone: " + cloneLink.getURI());


    Resource originalSource = WfDescRdfUtils.getSource(model, link);
    Resource originalSink = WfDescRdfUtils.getSink(model, link);

    Resource source = null;
    Resource sink = null;

    if (WfDescRdfUtils.isWorkflowInputPort(originalSource, model)) {

      String wfInPortName = URIUtils.lastBit(originalSource.getURI());

      Resource inportResource =
          WfDescRdfUtils.getInputWithLabel(wfInPortName, processorOfWfType, model);

      source = inportResource;
    } else {

      source = model.getResource(originalToClone.get(originalSource.getURI()));
    }



    if (WfDescRdfUtils.isWorkflowOutputPort(originalSink, model)) {
      String wfOutPortName = URIUtils.lastBit(originalSink.getURI());

      Resource outportResource =
          WfDescRdfUtils.getOutputWithLabel(wfOutPortName, processorOfWfType, model);

      sink = outportResource;
    } else {
      sink = model.getResource(originalToClone.get(originalSink.getURI()));
    }


    if ((sink != null) && (source != null)) {
      System.out.println("\t Source:" + source.getURI());
      System.out.println("\t Sink:" + sink.getURI());
      System.out.println("");
      model.add(cloneLink, model.getProperty(SummarizerNamespaces.rdfNS, "type"),
          model.getResource(SummarizerNamespaces.wfdescNS + "DataLink"));
      model.add(cloneLink, model.getProperty(SummarizerNamespaces.wfdescNS, "hasSource"), source);

      model.add(cloneLink, model.getProperty(SummarizerNamespaces.wfdescNS, "hasSink"), sink);

      model.add(parentWfDef, model.getProperty(SummarizerNamespaces.wfdescNS, "hasDataLink"),
          cloneLink);

    } else {

      System.out.println(" EITHER Source or Sink is NULL!!!");


    }

  }

  private static Map<String, String> cloneAndAddProcessor(Model model, Resource processor,
      String namePrefix, Resource wfResource) {


    Map<String, String> result = new HashMap<String, String>();

    String originalProcessorLabel = WfDescRdfUtils.getLabel(processor, model);
    Resource cloneProcessor =
        model.createResource(wfResource.getURI() + "processor/" + namePrefix + "_"
            + originalProcessorLabel + "/");
    cloneProcessor = model.getResource(cloneProcessor.getURI());

    model.add(cloneProcessor, model.getProperty(SummarizerNamespaces.rdfsNS, "label"), namePrefix
        + "_" + originalProcessorLabel);

    cloneAllPropertiesOfProcessor(processor, cloneProcessor, model);

    model.add(cloneProcessor, model.getProperty(SummarizerNamespaces.rdfNS, "type"),
        model.getResource(SummarizerNamespaces.wfdescNS + "Process"));
    model.add(WfDescRdfUtils.getWorkflowResource(model),
        model.getProperty(SummarizerNamespaces.wfdescNS, "hasSubProcess"), cloneProcessor);


    result.put(processor.getURI(), cloneProcessor.getURI());

    Set<Resource> inputs = WfDescRdfUtils.getInputPorts(processor, model);

    for (Resource inp : inputs) {
      Resource cloneInp =
          model.createResource(cloneProcessor.getURI() + "in/" + URIUtils.lastBit(inp.getURI())
              + "/");
      cloneInp = model.getResource(cloneInp.getURI());

      // cloneAllPropertiesOfPort(inp, cloneInp, model);

      model.add(cloneInp, model.getProperty(SummarizerNamespaces.rdfNS, "type"),
          model.getResource(SummarizerNamespaces.wfdescNS + "Input"));
      model.add(cloneProcessor, model.getProperty(SummarizerNamespaces.wfdescNS, "hasInput"),
          cloneInp);
      result.put(inp.getURI(), cloneInp.getURI());

    }
    Set<Resource> outputs = WfDescRdfUtils.getOutputPorts(processor, model);

    for (Resource out : outputs) {
      Resource cloneOut =
          model.createResource(cloneProcessor.getURI() + "out/" + URIUtils.lastBit(out.getURI())
              + "/");
      cloneOut = model.getResource(cloneOut.getURI());

      // cloneAllPropertiesOfPort(out, cloneOut, model);

      model.add(cloneOut, model.getProperty(SummarizerNamespaces.rdfNS, "type"),
          model.getResource(SummarizerNamespaces.wfdescNS + "Output"));
      model.add(cloneProcessor, model.getProperty(SummarizerNamespaces.wfdescNS, "hasOutput"),
          cloneOut);
      result.put(out.getURI(), cloneOut.getURI());
    }


    return result;
  }

  private static void cloneAllPropertiesOfProcessor(Resource processor, Resource cloneProcessor,
      Model model) {

    StmtIterator iter = model.listStatements(processor, null, (RDFNode) null);

    List<Statement> statements = new ArrayList<Statement>();

    while (iter.hasNext()) {
      statements.add(iter.next());
    }


    // all properties are carried forward except the following three
    for (Statement st : statements) {

      if ((st.getPredicate().getNameSpace().equalsIgnoreCase(SummarizerNamespaces.wfdescNS))
          && (st.getPredicate().getLocalName().equalsIgnoreCase("hasInput"))) {
        continue;

      }
      if ((st.getPredicate().getNameSpace().equalsIgnoreCase(SummarizerNamespaces.wfdescNS))
          && (st.getPredicate().getLocalName().equalsIgnoreCase("hasOutput"))) {
        continue;

      }

      if ((st.getPredicate().getNameSpace().equalsIgnoreCase(SummarizerNamespaces.rdfsNS))
          && (st.getPredicate().getLocalName().equalsIgnoreCase("label"))) {
        continue;


      }

      model.add(cloneProcessor, st.getPredicate(), st.getObject());

    }

  }

  public Map<String, Set<String>> getFlattenedProcessorGroupings() {
    return flattenedProcessorGroupings;
  }



  public Set<String> getToplevelVisibleOutPorts() {
    return toplevelVisibleOutPorts;
  }


  public Model decorateTopLevelVisiblePorts(Model model) {

    Set<String> ports = getToplevelVisibleOutPorts();

    for (String port : ports) {

      model.add(model.getResource(port),
          model.createProperty(SummarizerNamespaces.motifsNS + "topLevelVisible"), "true");
    }
    return model;
  }

}

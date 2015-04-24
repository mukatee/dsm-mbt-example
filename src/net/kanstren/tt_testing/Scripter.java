package net.kanstren.tt_testing;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.common.TestUtils;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.model.data.ValueSet;

import java.io.StringWriter;
import java.util.*;

/**
 * @author Teemu Kanstren.
 */
public class Scripter {
  /**
   * For template to report generation.
   */
  private VelocityEngine velocity = new VelocityEngine();
  /**
   * For storing template variables.
   */
  private VelocityContext vc = new VelocityContext();
  private final String inputTemplate;
  private final String outputTemplate;
  private final String batTemplate;
  private Map<String, List<String>> inports = new HashMap<>();
  private Map<String, List<String>> outports = new HashMap<>();

  public Scripter(String inputTemplate, String outputTemplate, String batTemplate) {
    this.inputTemplate = inputTemplate;
    this.outputTemplate = outputTemplate;
    this.batTemplate = batTemplate;
    velocity.setProperty("resource.loader", "file");
    velocity.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
    velocity.setProperty("file.resource.loader.path", ".");
    velocity.setProperty("file.resource.loader.cache", "false");
  }

  public void writeBatFile(String inputFile, String batFile, String checkerFile) {
    vc.put("inputfile", inputFile);
    vc.put("checkerfile", checkerFile);
    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(batTemplate, "UTF8", vc, sw);
    TestUtils.write(sw.toString(), batFile);
  }

  public void writeScript(ModelState state, String inputFile, String outputFile) {
    vc.put("outputname", outputFile);
    List<DFP> dfps = state.getDFPs().getOptions();
    vc.put("dfps", dfps);
    List<DFT> dfts = state.getDFTs().getOptions();
    vc.put("dfts", dfts);
    vc.put("uid", new UID());

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(inputTemplate, "UTF8", vc, sw);
    TestUtils.write(sw.toString(), inputFile);
  }

  public void writeChecks(ModelState state, String pythonFile, String fileToCheck) {
    List<String> checks = new ArrayList<>();
    checks.add("ALL SID UNIQUE");
    checks.add("BLOCKS = " + state.dfpCount());
    checks.add("TYPES = " + state.dftCount());
    List<DFT> dfts = state.getDFTs().getOptions();
    for (DFT dft : dfts) {
      checks.add("TYPE" + dft.getUid() + ".NAME = " + dft.getName());
    }

    List<DFP> dfps = state.getDFPs().getOptions();
    for (DFP dfp : dfps) {
      checks.add("BLOCK" + dfp.getUid() + ".NAME = " + dfp.getName());
      int inPorts = dfp.getServers().size();
      inPorts += dfp.getInFlows().size();
      inPorts += dfp.getInPowers().size();
      int outPorts = dfp.getOutFlows().size();
      outPorts += dfp.getOutPowers().size();
      outPorts += dfp.getClients().size();
      checks.add("BLOCK" + dfp.getUid() + ".INPORT_COUNT = " + inPorts);
      checks.add("BLOCK" + dfp.getUid() + ".OUTPORT_COUNT = " + outPorts);
      if (dfp.getDescription() == null) {
        checks.add("!BLOCK" + dfp.getUid() + ".DESCRIPTION");
      } else {
        checks.add("BLOCK" + dfp.getUid() + ".DESCRIPTION = " + dfp.getDescription());
      }
      createInPortChecks(checks, dfp, dfp.getInFlows().getOptions());
      createInPortChecks(checks, dfp, dfp.getInPowers().getOptions());
      createInPortChecks(checks, dfp, dfp.getServers().getOptions());
      createOutPortChecks(checks, dfp, dfp.getOutFlows().getOptions());
      createOutPortChecks(checks, dfp, dfp.getOutPowers().getOptions());
      createOutPortChecks(checks, dfp, dfp.getClients().getOptions());
    }

    vc.put("checks", checks);
    vc.put("dfps", dfps);
    vc.put("dfts", dfts);
    vc.put("dft_count", dfts.size());
    vc.put("dfp_count", dfps.size());
    vc.put("inport_count_dict", createPortDict(inports));
    vc.put("outport_count_dict", createPortDict(outports));
    List<String> linesList = createLinesList(dfps);
    vc.put("expected_lines_list", linesList.toString());
    vc.put("line_count", linesList.size());
    vc.put("desc_dict", createDescDict(dfps));
    vc.put("outputfile", fileToCheck);
    vc.put("uid", new UID());

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(outputTemplate, "UTF8", vc, sw);
    TestUtils.write(sw.toString(), pythonFile);
  }

  private String createDescDict(List<DFP> dfps) {
    List<String> descs = new ArrayList<>();
    for (DFP dfp : dfps) {
      if (dfp.getDescription() == null) {
        descs.add("\""+dfp.getName()+"\":"+"None");
      } else {
        descs.add("\""+dfp.getName()+"\":\""+dfp.getDescription()+"\"");
      }
    }
    String dict = "{";
    int i = 0;
    for (String desc : descs) {
      dict += desc;
      i++;
      if (i < descs.size()) dict += ", ";
    }
    dict += "}";
    return dict;
  }

  private List<String> createLinesList(List<DFP> dfps) {
    List<String> lines = new ArrayList<>();
    for (DFP dfp : dfps) {
      for (DFPPort port : dfp.getInFlows().getOptions()){
        List<DFPPort> pairs = port.getPairs();
        for (DFPPort pair : pairs) {
          lines.add("\""+dfp.getName()+"."+port.getPosition()+"->"+pair.getDfp().getName()+"."+pair.getPosition()+"\"");
        }
      }
    }
    for (DFP dfp : dfps) {
      for (DFPPort port : dfp.getInPowers().getOptions()){
        List<DFPPort> pairs = port.getPairs();
        for (DFPPort pair : pairs) {
          lines.add("\""+dfp.getName()+"."+port.getPosition()+"->"+pair.getDfp().getName()+"."+pair.getPosition()+"\"");
        }
      }
    }
    for (DFP dfp : dfps) {
      for (DFPPort port : dfp.getClients().getOptions()){
        List<DFPPort> pairs = port.getPairs();
        for (DFPPort pair : pairs) {
          lines.add("\""+dfp.getName()+"."+port.getPosition()+"->"+pair.getDfp().getName()+"."+pair.getPosition()+"\"");
        }
      }
    }
    return lines;
  }

  private String createPortDict(Map<String, List<String>> ports) {
    List<String> counts = new ArrayList<>();
    for (String dfp : ports.keySet()) {
      counts.add("\""+dfp+"\":"+ports.get(dfp).size());
    }
    String dict = "{";
    int i = 0;
    for (String count : counts) {
      dict += count;
      i++;
      if (i < counts.size()) dict += ", ";
    }
    dict += "}";
    return dict;
  }

  private void createInPortChecks(List<String> checks, DFP dfp, List<DFPPort> ports) {
    List<String> list = inports.get(dfp.getName());
    if (list == null) {
      list = new ArrayList<>();
      inports.put(dfp.getName(), list);
    }
    for (DFPPort port : ports) {
      checks.add("BLOCK" + dfp.getUid() + ".INPORTS.HAS = " + port.getName());
      list.add(port.getName());
    }
  }

  private void createOutPortChecks(List<String> checks, DFP dfp, List<DFPPort> ports) {
    List<String> list = outports.get(dfp.getName());
    if (list == null) {
      list = new ArrayList<>();
      outports.put(dfp.getName(), list);
    }
    for (DFPPort port : ports) {
      checks.add("BLOCK" + dfp.getUid() + ".OUTPORTS.HAS = " + port.getName());
      list.add(port.getName());
    }
  }

  public void writeTrace(TestCase test, String file) {
    String trace = "";
    List<String> steps = test.getAllStepNames();
    for (String step : steps) {
      trace += step.toUpperCase() + "\n";
    }
    TestUtils.write(trace, file);
  }
}

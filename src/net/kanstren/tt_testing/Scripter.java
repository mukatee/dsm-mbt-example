package net.kanstren.tt_testing;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.common.TestUtils;
import osmo.tester.generator.testsuite.TestCase;

import java.io.StringWriter;
import java.util.List;

/**
 * @author Teemu Kanstren.
 */
public class Scripter {
  /** For template to report generation. */
  private VelocityEngine velocity = new VelocityEngine();
  /** For storing template variables. */
  private VelocityContext vc = new VelocityContext();
  private final String template;

  public Scripter(String template) {
    this.template = template;
    velocity.setProperty("resource.loader", "file");
    velocity.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
    velocity.setProperty("file.resource.loader.path", ".");
    velocity.setProperty("file.resource.loader.cache", "false");
  }

  public void writeScript(ModelState state, String file) {
    List<DFP> dfps = state.getDFPs().getOptions();
    vc.put("dfps", dfps);
    List<DFT> dfts = state.getDFTs().getOptions();
    vc.put("dfts", dfts);
    vc.put("uid", new UID());

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate(template, "UTF8", vc, sw);
    TestUtils.write(sw.toString(), file);
  }

  public void writeChecks(ModelState state, String file) {
    String checks = "";
    checks += "ADD SID UNIQUE"+"\n";
    checks += "BLOCKS = "+state.dfpCount()+"\n";
    checks += "TYPES = "+state.dftCount()+"\n";
    List<DFT> dfts = state.getDFTs().getOptions();
    for (DFT dft : dfts) {
      checks += "TYPE"+dft.getUid()+".NAME = "+dft.getName()+"\n";
    }

    List<DFP> dfps = state.getDFPs().getOptions();
    for (DFP dfp : dfps) {
      checks += "BLOCK"+dfp.getUid()+".NAME = "+dfp.getName()+"\n";
      int inPorts = dfp.getServers().size();
      inPorts += dfp.getInFlows().size();
      inPorts += dfp.getInPowers().size();
      int outPorts = dfp.getOutFlows().size();
      outPorts += dfp.getOutPowers().size();
      outPorts += dfp.getClients().size();
      checks += "BLOCK"+dfp.getUid()+".INPORT_COUNT = "+inPorts+"\n";
      checks += "BLOCK"+dfp.getUid()+".OUTPORT_COUNT = "+outPorts+"\n";
      if (dfp.getDescription() == null) {
        checks += "!BLOCK"+dfp.getUid()+".DESCRIPTION\n";
      } else {
        checks += "BLOCK"+dfp.getUid()+".DESCRIPTION = "+dfp.getDescription()+"\n";
      }
      createPortChecks(dfp, dfp.getInFlows().getOptions(), "IN");
      createPortChecks(dfp, dfp.getInPowers().getOptions(), "IN");
      createPortChecks(dfp, dfp.getServers().getOptions(), "IN");
      createPortChecks(dfp, dfp.getOutFlows().getOptions(), "OUT");
      createPortChecks(dfp, dfp.getOutPowers().getOptions(), "OUT");
      createPortChecks(dfp, dfp.getClients().getOptions(), "OUT");
    }
    TestUtils.write(checks, file);
  }

  private String createPortChecks(DFP dfp, List<DFPPort> ports, String type) {
    String checks = "";
    for (DFPPort port : ports) {
      checks += "BLOCK"+dfp.getUid()+"."+type+"PORTS.HAS = "+port.getName()+"\n";
    }
    return checks;
  }

  public void writeTrace(TestCase test, String file) {
    String trace = "";
    List<String> steps = test.getAllStepNames();
    for (String step : steps) {
      trace += step.toUpperCase()+"\n";
    }
    TestUtils.write(trace, file);
  }
}

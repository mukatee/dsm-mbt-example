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

  public Scripter() {
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

    StringWriter sw = new StringWriter();
    velocity.mergeTemplate("json-input.vm", "UTF8", vc, sw);
    TestUtils.write(sw.toString(), file);
  }

  public void writeChecks(ModelState state, String file) {
    String checks = "";
    checks += "blocks = "+state.dfpCount()+"\n";
    checks += "types = "+state.dftCount()+"\n";
    checks += "all SID unique"+"\n";
    List<DFT> dfts = state.getDFTs().getOptions();
    for (DFT dft : dfts) {
      checks += "type"+dft.getId()+".name = "+dft.getName()+"\n";
    }

    List<DFP> dfps = state.getDFPs().getOptions();
    for (DFP dfp : dfps) {
      checks += "block"+dfp.getId()+".name = "+dfp.getName()+"\n";
      int inPorts = dfp.getServers().size();
      inPorts += dfp.getInFlows().size();
      inPorts += dfp.getInPowers().size();
      int outPorts = dfp.getOutFlows().size();
      outPorts += dfp.getOutPowers().size();
      outPorts += dfp.getClients().size();
      checks += "block"+dfp.getId()+".inport_count = "+inPorts+"\n";
      checks += "block"+dfp.getId()+".outport_count = "+outPorts+"\n";
      if (dfp.getDescription() == null) {
        checks += "!block"+dfp.getId()+".description\n";
      } else {
        checks += "block"+dfp.getId()+".description = "+dfp.getDescription()+"\n";
      }
      createPortChecks(dfp, dfp.getInFlows().getOptions(), "in");
      createPortChecks(dfp, dfp.getInPowers().getOptions(), "in");
      createPortChecks(dfp, dfp.getServers().getOptions(), "in");
      createPortChecks(dfp, dfp.getOutFlows().getOptions(), "out");
      createPortChecks(dfp, dfp.getOutPowers().getOptions(), "out");
      createPortChecks(dfp, dfp.getClients().getOptions(), "out");
    }
    TestUtils.write(checks, file);
  }

  private String createPortChecks(DFP dfp, List<DFPPort> ports, String type) {
    String checks = "";
    for (DFPPort port : ports) {
      checks += "block"+dfp.getId()+"."+type+"ports.has = "+port.getName()+"\n";
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

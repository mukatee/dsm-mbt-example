package net.kanstren.tt_testing;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import osmo.common.TestUtils;

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
}

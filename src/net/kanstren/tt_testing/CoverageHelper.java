package net.kanstren.tt_testing;

import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Teemu Kanstren.
 */
public class CoverageHelper {
  private final ModelState state;
  private final TestSuite suite;

  public CoverageHelper(ModelState state, TestSuite suite) {
    this.state = state;
    this.suite = suite;
  }

  public void createCoverage(TestCase test) {
    TestCoverage coverage = test.getCoverage();
    String dfpCat = value012NFor(state.dfpCount());
    coverage.addVariableValue("DFP", dfpCat);
    coverage.addVariableValue("DFT", value012NFor(state.dftCount()));

    List<DFT> dfts = state.getDFTs().getOptions();
    for (DFT dft : dfts) {
      coverage.addVariableValue("DFT-DFP", value012NFor(dft.getChildren().size()));
    }

    List<DFP> dfps = state.getDFPs().getOptions();
    for (DFP dfp1 : dfps) {
      singleDFPCoverage(dfp1, dfpCat);

      for (DFP dfp2 : dfps) {
        if (dfp1.getId() == dfp2.getId()) continue;
        twoDFPCoverage(dfp1, dfp2);
      }
    }
  }

  private void singleDFPCoverage(DFP dfp, String dfpCat) {
    int dfp1InFlows = dfp.getInFlows().size();
    int dfp1OutFlows = dfp.getOutFlows().size();
    int dfp1InPowers = dfp.getInPowers().size();
    int dfp1OutPowers = dfp.getOutPowers().size();
    int dfp1Clients = dfp.getClients().size();
    int dfp1Servers = dfp.getServers().size();

    String dfpInFlowCat = value01NFor(dfp1InFlows);
    String dfpOutFlowCat = value01NFor(dfp1OutFlows);
    String dfpInPowerCat = value01NFor(dfp1InPowers);
    String dfpOutPowerCat = value01NFor(dfp1OutPowers);
    String dfpClientCat = value01NFor(dfp1Clients);
    String dfpServerCat = value01NFor(dfp1Servers);

    TestCase test = suite.getCurrentTest();
    TestCoverage coverage = test.getCoverage();

    coverage.addVariableValue("DFP-InFlow", "" + dfpInFlowCat);
    coverage.addVariableValue("DFP-OutFlow", "" + dfpOutFlowCat);
    coverage.addVariableValue("DFP-InPower", "" + dfpInPowerCat);
    coverage.addVariableValue("DFP-OutPower", "" + dfpOutPowerCat);
    coverage.addVariableValue("DFP-Client", "" + dfpClientCat);
    coverage.addVariableValue("DFP-Server", "" + dfpServerCat);

    coverage.addVariableValue("DFPs+InFlow", dfpCat + "+" + dfpInFlowCat);
    coverage.addVariableValue("DFPs+OutFlow", dfpCat + "+" + dfpOutFlowCat);
    coverage.addVariableValue("DFPs+InPower", dfpCat + "+" + dfpInPowerCat);
    coverage.addVariableValue("DFPs+OutPower", dfpCat + "+" + dfpOutPowerCat);
    coverage.addVariableValue("DFPs+Server", dfpCat + "+" + dfpServerCat);
    coverage.addVariableValue("DFPs+Client", dfpCat + "+" + dfpClientCat);

    inFlowCoverage(dfp, dfpCat, dfpInFlowCat, dfpOutFlowCat);
    inPowerCoverage(dfp, dfpCat, dfpInPowerCat, dfpOutPowerCat);
    serverCoverage(dfp, dfpCat, dfpServerCat, dfpClientCat);
  }

  private void inFlowCoverage(DFP dfp, String dfpCat, String dfpInFlowCat, String dfpOutFlowCat) {
    TestCase test = suite.getCurrentTest();
    TestCoverage coverage = test.getCoverage();

    for (DFPPort p : dfp.getInFlows().getOptions()) {
      String cat = value01NFor(p.getPairs().size());
      coverage.addVariableValue("Flow-Lines", cat);
      coverage.addVariableValue("DFPs+Flow-Lines", dfpCat+"+"+cat);
      coverage.addVariableValue("DFPs+InFlowPorts+Flow-Lines", dfpCat+"+"+dfpInFlowCat+"+"+cat);
      coverage.addVariableValue("DFPs+OutFlowPorts+Flow-Lines", dfpCat+"+"+dfpOutFlowCat+"+"+cat);
    }
  }

  private void inPowerCoverage(DFP dfp, String dfpCat, String dfpInPowerCat, String dfpOutPowerCat) {
    TestCase test = suite.getCurrentTest();
    TestCoverage coverage = test.getCoverage();

    for (DFPPort p : dfp.getInPowers().getOptions()) {
      String cat = value01NFor(p.getPairs().size());
      coverage.addVariableValue("Power-Lines", cat);
      coverage.addVariableValue("DFPs+Power-Lines", dfpCat+"+"+cat);
      coverage.addVariableValue("DFPs+InPowerPorts+Power-Lines", dfpCat+"+"+dfpInPowerCat+"+"+cat);
      coverage.addVariableValue("DFPs+OutPowerPorts+Power-Lines", dfpCat+"+"+dfpOutPowerCat+"+"+cat);
    }
  }

  private void serverCoverage(DFP dfp, String dfpCat, String dfpServerCat, String dfpClientCat) {
    TestCase test = suite.getCurrentTest();
    TestCoverage coverage = test.getCoverage();

    for (DFPPort p : dfp.getServers().getOptions()) {
      String cat = value01NFor(p.getPairs().size());
      coverage.addVariableValue("Server-Lines", cat);
      coverage.addVariableValue("DFPs+Server-Lines", dfpCat+"+"+cat);
      coverage.addVariableValue("DFPs+ServerPorts+Server-Lines", dfpCat+"+"+dfpServerCat+"+"+cat);
      coverage.addVariableValue("DFPs+ClientPorts+Server-Lines", dfpCat+"+"+dfpClientCat+"+"+cat);
    }
  }

  private void twoDFPCoverage(DFP dfp1, DFP dfp2) {
    int dfp1InFlows = dfp1.getInFlows().size();
    int dfp1OutFlows = dfp1.getOutFlows().size();
    int dfp1InPowers = dfp1.getInPowers().size();
    int dfp1OutPowers = dfp1.getOutPowers().size();
    int dfp1Clients = dfp1.getClients().size();
    int dfp1Servers = dfp1.getServers().size();

    int dfp2InFlows = dfp2.getInFlows().size();
    int dfp2OutFlows = dfp2.getOutFlows().size();
    int dfp2InPowers = dfp2.getInPowers().size();
    int dfp2OutPowers = dfp2.getOutPowers().size();
    int dfp2Clients = dfp2.getClients().size();
    int dfp2Servers = dfp2.getServers().size();

    pair("InFlow", "InFlow", dfp1InFlows, dfp2InFlows);
    pair("InFlow", "OutFlow", dfp1InFlows, dfp2OutFlows);
    pair("InFlow", "InPower", dfp1InFlows, dfp2InPowers);
    pair("InFlow", "OutPower", dfp1InFlows, dfp2OutPowers);
    pair("InFlow", "Client", dfp1InFlows, dfp2Clients);
    pair("InFlow", "Server", dfp1InFlows, dfp2Servers);

    pair("OutFlow", "OutFlow", dfp1OutFlows, dfp2OutFlows);
    pair("OutFlow", "InPower", dfp1OutFlows, dfp2InPowers);
    pair("OutFlow", "OutPower", dfp1OutFlows, dfp2OutPowers);
    pair("OutFlow", "Client", dfp1OutFlows, dfp2Clients);
    pair("OutFlow", "Server", dfp1OutFlows, dfp2Servers);

    pair("InPower", "InPower", dfp1InPowers, dfp2InPowers);
    pair("InPower", "OutPower", dfp1InPowers, dfp2OutPowers);
    pair("InPower", "Client", dfp1InPowers, dfp2Clients);
    pair("InPower", "Server", dfp1InPowers, dfp2Servers);

    pair("OutPower", "OutPower", dfp1OutPowers, dfp2OutPowers);
    pair("OutPower", "Client", dfp1OutPowers, dfp2Clients);
    pair("OutPower", "Server", dfp1OutPowers, dfp2Servers);

    pair("Client", "Client", dfp1Clients, dfp2Clients);
    pair("Client", "Server", dfp1Clients, dfp2Servers);

    pair("Server", "Server", dfp1Servers, dfp2Servers);
  }

  private void pair(String name1, String name2, int value1, int value2) {
    TestCase test = suite.getCurrentTest();
    TestCoverage coverage = test.getCoverage();
    List<String> name = new ArrayList<>();
    name.add(name1);
    name.add(name2);
    Collections.sort(name);
    List<String> value = new ArrayList<>();
    value.add(value01NFor(value1));
    value.add(value01NFor(value2));
    Collections.sort(value);
    coverage.addVariableValue("" + name, "" + value);
  }

  private String value01NFor(int n) {
    if (n < 2) return "" + n;
    return "N";
  }

  private String value012NFor(int n) {
    if (n < 3) return "" + n;
    return "N";
  }
}

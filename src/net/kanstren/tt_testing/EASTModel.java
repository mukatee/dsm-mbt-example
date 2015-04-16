package net.kanstren.tt_testing;

import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.data.ValueSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Teemu Kanstren.
 */
public class EASTModel {
  private final ModelState state;
  private TestSuite suite;

  public EASTModel(ModelState state) {
    this.state = state;
  }

  @TestStep(weight = 5)
  public void createDFP() {
    state.createDFP();
  }

  @TestStep(weight = 2)
  public void createDFT() {
    state.createDFT();
  }

  @Guard("LinkDFPtoDFT")
  public boolean hasUnlinkedDFP() {
    return state.getUnlinkedDFPs().size() > 0 && state.getDFTs().size() > 0;
  }

  @TestStep(weight = 30)
  public void linkDFPtoDFT() {
    ValueSet<DFP> unlinked = state.getUnlinkedDFPs();
    ValueSet<DFT> dfts = state.getDFTs();
    DFT dft = dfts.random();
    DFP dfp = unlinked.random();
    dfp.setDft(dft);
    dft.addChild(dfp);
  }

  @Guard({"AddInFlow", "AddOutFlow", "AddInPower", "AddOutPower", "AddServer", "AddClient"})
  public boolean hasDFP() {
    return state.dfpCount() > 0;
  }

  @TestStep(weight = 20)
  public void addInFlow() {
    state.randomDFP().addInFlow();
  }

  @TestStep(weight = 20)
  public void addOutFlow() {
    state.randomDFP().addOutFlow();
  }

  @TestStep(weight = 20)
  public void addInPower() {
    state.randomDFP().addInPower();
  }

  @TestStep(weight = 20)
  public void addOutPower() {
    state.randomDFP().addOutPower();
  }

  @TestStep(weight = 20)
  public void addServer() {
    state.randomDFP().addServer();
  }

  @TestStep(weight = 20)
  public void addClient() {
    state.randomDFP().addClient();
  }

  @Guard("AddDescription")
  public boolean hasNoDescription() {
    return state.getNoDescDFPs().size() > 0;
  }

  @TestStep(weight = 10)
  public void addDescription() {
    DFP dfp = state.getNoDescDFPs().random();
    dfp.setDescription("Description" + dfp.getId());
  }

  @AfterTest
  public void theEnd() {
    TestCase test = suite.getCurrentTest();
    test.setAttribute("state", state);

    TestCoverage coverage = test.getCoverage();
    coverage.addVariableValue("DFP", value012NFor(state.dfpCount()));
    coverage.addVariableValue("DFT", value012NFor(state.dfpCount()));

    List<DFT> dfts = state.getDFTs().getOptions();
    for (DFT dft : dfts) {
      coverage.addVariableValue("DFT-DFP", value012NFor(dft.getChildren().size()));
    }

    List<DFP> dfps = state.getDFPs().getOptions();
    for (DFP dfp1 : dfps) {
      int dfp1InFlows = dfp1.getInFlows().size();
      int dfp1OutFlows = dfp1.getOutFlows().size();
      int dfp1InPowers = dfp1.getInPowers().size();
      int dfp1OutPowers = dfp1.getOutPowers().size();
      int dfp1Clients = dfp1.getClients().size();
      int dfp1Servers = dfp1.getServers().size();
      coverage.addVariableValue("DFP-InFlow", "" + value01NFor(dfp1InFlows));
      coverage.addVariableValue("DFP-OutFlow", "" + value01NFor(dfp1OutFlows));
      coverage.addVariableValue("DFP-InPower", "" + value01NFor(dfp1InPowers));
      coverage.addVariableValue("DFP-OutPower", "" + value01NFor(dfp1OutPowers));
      coverage.addVariableValue("DFP-Client", "" + value01NFor(dfp1Clients));
      coverage.addVariableValue("DFP-Server", "" + value01NFor(dfp1Servers));
      for (DFP dfp2 : dfps) {
        if (dfp1.getId() == dfp2.getId()) continue;

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
    }

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

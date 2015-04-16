package net.kanstren.tt_testing;

import osmo.common.Randomizer;
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
  private Randomizer rand = new Randomizer();

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

  @Guard("CreateFlowLink")
  public boolean hasFlowPair() {
    return findFlowPair() != null;
  }

  @Guard("CreatePowerLink")
  public boolean hasPowerPair() {
    return findPowerPair() != null;
  }

  @Guard("CreateClientServerLink")
  public boolean hasClientServerPair() {
    return findClientServerPair() != null;
  }

  private Tuple<DFP, DFP> findFlowPair() {
    ValueSet<DFP> dfps = state.getDFPs();
    ValueSet<DFP> inFlows = new ValueSet<>();
    inFlows.setSeed(rand.nextLong());
    ValueSet<DFP> outFlows = new ValueSet<>();
    outFlows.setSeed(rand.nextLong());
    for (DFP dfp : dfps.getOptions()) {
      if (dfp.getInFlows().size() > 0) {
        inFlows.add(dfp);
      }
      if (dfp.getOutFlows().size() > 0) {
        outFlows.add(dfp);
      }
    }
    if (inFlows.size() == 0 || outFlows.size() == 0) return null;
    return getUniquePair(inFlows, outFlows);
  }

  private Tuple<DFP, DFP> findPowerPair() {
    ValueSet<DFP> dfps = state.getDFPs();
    ValueSet<DFP> inPowers = new ValueSet<>();
    inPowers.setSeed(rand.nextLong());
    ValueSet<DFP> outPowers = new ValueSet<>();
    outPowers.setSeed(rand.nextLong());
    for (DFP dfp : dfps.getOptions()) {
      if (dfp.getInPowers().size() > 0) {
        inPowers.add(dfp);
      }
      if (dfp.getOutPowers().size() > 0) {
        outPowers.add(dfp);
      }
    }
    if (inPowers.size() == 0 || outPowers.size() == 0) return null;
    return getUniquePair(inPowers, outPowers);
  }

  private Tuple<DFP, DFP> findClientServerPair() {
    ValueSet<DFP> dfps = state.getDFPs();
    ValueSet<DFP> clients = new ValueSet<>();
    clients.setSeed(rand.nextLong());
    ValueSet<DFP> servers = new ValueSet<>();
    servers.setSeed(rand.nextLong());
    for (DFP dfp : dfps.getOptions()) {
      if (dfp.getClients().size() > 0) {
        clients.add(dfp);
      }
      if (dfp.getServers().size() > 0) {
        servers.add(dfp);
      }
    }
    if (clients.size() == 0 || servers.size() == 0) return null;
    return getUniquePair(clients, servers);
  }

  private Tuple<DFP, DFP> getUniquePair(ValueSet<DFP> set1, ValueSet<DFP> set2) {
    if (set1.getOptions().equals(set2.getOptions())) return null;
    DFP dfp1 = null;
    DFP dfp2 = null;
    while (dfp1 == null || dfp2 == null) {
      dfp1 = set1.random();
      dfp2 = set2.random();
      if (dfp1.equals(dfp2)) {
        dfp1 = null;
        dfp2 = null;
      }
    }
    return new Tuple<>(dfp1, dfp2);
  }

  @TestStep
  public void createFlowLink() {
    Tuple<DFP, DFP> pair = findFlowPair();
    pair.getValue1().connectFlowTo(pair.getValue2());
  }

  @TestStep
  public void createPowerLink() {
    Tuple<DFP, DFP> pair = findPowerPair();
    pair.getValue1().connectPowerTo(pair.getValue2());
  }

  @TestStep
  public void createClientServerLink() {
    Tuple<DFP, DFP> pair = findClientServerPair();
    pair.getValue1().connectClientServer(pair.getValue2());
  }

  @AfterTest
  public void theEnd() {
    TestCase test = suite.getCurrentTest();
    test.setAttribute("state", state);
    createCoverage(test);
  }

  private void createCoverage(TestCase test) {
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
      int dfp1InFlows = dfp1.getInFlows().size();
      int dfp1OutFlows = dfp1.getOutFlows().size();
      int dfp1InPowers = dfp1.getInPowers().size();
      int dfp1OutPowers = dfp1.getOutPowers().size();
      int dfp1Clients = dfp1.getClients().size();
      int dfp1Servers = dfp1.getServers().size();

      String dfp1InFlowCat = value01NFor(dfp1InFlows);
      String dfp1OutFlowCat = value01NFor(dfp1OutFlows);
      String dfp1InPowerCat = value01NFor(dfp1InPowers);
      String dfp1OutPowerCat = value01NFor(dfp1OutPowers);
      String dfp1ClientCat = value01NFor(dfp1Clients);
      String dfp1ServerCat = value01NFor(dfp1Servers);
      coverage.addVariableValue("DFP-InFlow", "" + dfp1InFlowCat);
      coverage.addVariableValue("DFP-OutFlow", "" + dfp1OutFlowCat);
      coverage.addVariableValue("DFP-InPower", "" + dfp1InPowerCat);
      coverage.addVariableValue("DFP-OutPower", "" + dfp1OutPowerCat);
      coverage.addVariableValue("DFP-Client", "" + dfp1ClientCat);
      coverage.addVariableValue("DFP-Server", "" + dfp1ServerCat);

      coverage.addVariableValue("DFPs+InFlow", dfpCat + "+" + dfp1InFlowCat);
      coverage.addVariableValue("DFPs+OutFlow", dfpCat + "+" + dfp1OutFlowCat);
      coverage.addVariableValue("DFPs+InPower", dfpCat + "+" + dfp1InPowerCat);
      coverage.addVariableValue("DFPs+OutPower", dfpCat + "+" + dfp1OutPowerCat);
      coverage.addVariableValue("DFPs+Server", dfpCat + "+" + dfp1ServerCat);
      coverage.addVariableValue("DFPs+Client", dfpCat + "+" + dfp1ClientCat);

      for (Port p : dfp1.getInFlows().getOptions()) {
        String cat = value01NFor(p.getPairs().size());
        coverage.addVariableValue("Flow-Lines", cat);
        coverage.addVariableValue("DFPs+Flow-Lines", dfpCat+"+"+cat);
        coverage.addVariableValue("DFPs+InFlowPorts+Flow-Lines", dfpCat+""+dfp1InFlowCat+"+"+cat);
        coverage.addVariableValue("DFPs+OutFlowPorts+Flow-Lines", dfpCat+""+dfp1OutFlowCat+"+"+cat);
      }
      for (Port p : dfp1.getInPowers().getOptions()) {
        String cat = value01NFor(p.getPairs().size());
        coverage.addVariableValue("Power-Lines", cat);
        coverage.addVariableValue("DFPs+Power-Lines", dfpCat+"+"+cat);
        coverage.addVariableValue("DFPs+InPowerPorts+Power-Lines", dfpCat+""+dfp1InPowerCat+"+"+cat);
        coverage.addVariableValue("DFPs+OutPowerPorts+Power-Lines", dfpCat+""+dfp1OutPowerCat+"+"+cat);
      }
      for (Port p : dfp1.getServers().getOptions()) {
        String cat = value01NFor(p.getPairs().size());
        coverage.addVariableValue("Server-Lines", cat);
        coverage.addVariableValue("DFPs+Server-Lines", dfpCat+"+"+cat);
        coverage.addVariableValue("DFPs+ServerPorts+Server-Lines", dfpCat+""+dfp1ServerCat+"+"+cat);
        coverage.addVariableValue("DFPs+ClientPorts+Server-Lines", dfpCat+""+dfp1ClientCat+"+"+cat);
      }

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

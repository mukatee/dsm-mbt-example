package net.kanstren.tt_testing;

import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Calculates coverage values for the produced input model.
 * The references to "coverage category" refer to the term category-partitioning in test coverage.
 * That is, we try to achieve coverage of the categories but do not necessarily care how it is done.
 * For example, category value N for a DFP can be achieved by 3,4,5,6, or more DFP instances being present
 * in one of the generated input models. But if we get one with 3 it is already covered and another with 4
 * will not add any more coverage (with the N category).
 *
 * @author Teemu Kanstren.
 */
public class CoverageHelper {
  /** From the model state we find out what elements were generated in the input. */
  private final ModelState state;
  /** And this is where we store the coverage data. */
  private final TestSuite suite;

  public CoverageHelper(ModelState state, TestSuite suite) {
    this.state = state;
    this.suite = suite;
  }

  public void createCoverage(TestCase test) {
    TestCoverage coverage = test.getCoverage();
    //we have DFP coverage category of 0,1,2 or N number of DFP in a single input model
    String dfpCat = value012NFor(state.dfpCount());
    //store DFP category as achieve coverage value
    coverage.addVariableValue("DFP", dfpCat);
    //for the DFT we have the same category as for DFP
    coverage.addVariableValue("DFT", value012NFor(state.dftCount()));

    List<DFT> dfts = state.getDFTs().getOptions();
    for (DFT dft : dfts) {
      //here we have a coverage category for how many DFP instances are associated to a single DFT
      coverage.addVariableValue("DFT-DFP", value012NFor(dft.getChildren().size()));
    }

    List<DFP> dfps = state.getDFPs().getOptions();
    for (DFP dfp1 : dfps) {
      singleDFPCoverage(dfp1, dfpCat);

      for (DFP dfp2 : dfps) {
        if (dfp1.getUid() == dfp2.getUid()) continue;
        twoDFPCoverage(dfp1, dfp2);
      }
    }
  }

  /**
   * Calculates DFP coverage category values from the viewpoint of a single DFP instance.
   *
   * @param dfp To calculate for.
   * @param dfpCat The previously defined DFP coverage category (how many DFP are in the input model).
   */
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

    //Coverage category for how many inflow ports a DFP has
    coverage.addVariableValue("DFP-InFlow", "" + dfpInFlowCat);
    //Coverage category for how many outflow ports a DFP has
    coverage.addVariableValue("DFP-OutFlow", "" + dfpOutFlowCat);
    //and so on for all the other port types..
    coverage.addVariableValue("DFP-InPower", "" + dfpInPowerCat);
    coverage.addVariableValue("DFP-OutPower", "" + dfpOutPowerCat);
    coverage.addVariableValue("DFP-Client", "" + dfpClientCat);
    coverage.addVariableValue("DFP-Server", "" + dfpServerCat);

    //Coverage value that combines the target of having 0,1,2,N DFP in a model AND at the same time
    //having 0,1,2,N inflow ports for a DFP in the same model
    coverage.addVariableValue("DFPs+InFlow", dfpCat + "+" + dfpInFlowCat);
    //and the same for all other port types
    coverage.addVariableValue("DFPs+OutFlow", dfpCat + "+" + dfpOutFlowCat);
    coverage.addVariableValue("DFPs+InPower", dfpCat + "+" + dfpInPowerCat);
    coverage.addVariableValue("DFPs+OutPower", dfpCat + "+" + dfpOutPowerCat);
    coverage.addVariableValue("DFPs+Server", dfpCat + "+" + dfpServerCat);
    coverage.addVariableValue("DFPs+Client", dfpCat + "+" + dfpClientCat);

    inFlowCoverage(dfp, dfpCat, dfpInFlowCat, dfpOutFlowCat);
    inPowerCoverage(dfp, dfpCat, dfpInPowerCat, dfpOutPowerCat);
    serverCoverage(dfp, dfpCat, dfpServerCat, dfpClientCat);
  }

  /**
   * Calculates line coverage values in relation to DFP instances in the model.
   *
   * @param dfp The DFP to process for coverage calculation.
   * @param dfpCat Category of DFP instances in the model.
   * @param dfpInFlowCat Category of inflow ports in the model.
   * @param dfpOutFlowCat Category of outflow ports in the model.
   */
  private void inFlowCoverage(DFP dfp, String dfpCat, String dfpInFlowCat, String dfpOutFlowCat) {
    TestCase test = suite.getCurrentTest();
    TestCoverage coverage = test.getCoverage();

    for (DFPPort p : dfp.getInFlows().getOptions()) {
      String cat = value01NFor(p.getPairs().size());
      //Coverage category value for number of inflow-outflow connections in the input model
      coverage.addVariableValue("Flow-Lines", cat);
      //Coverage category value combining number of DFP instances with number of inflow-outflow connections in the input model
      coverage.addVariableValue("DFPs+Flow-Lines", dfpCat+"+"+cat);
      //combine DFP category, inflow port number category, and inflow-outflow connection count category as one coverage value
      coverage.addVariableValue("DFPs+InFlowPorts+Flow-Lines", dfpCat+"+"+dfpInFlowCat+"+"+cat);
      //same for outflow ports
      coverage.addVariableValue("DFPs+OutFlowPorts+Flow-Lines", dfpCat+"+"+dfpOutFlowCat+"+"+cat);
    }
  }

  /**
   * Similar to the inFlowCoverage() method but for power ports.
   *
   * @param dfp The DFP to process for coverage calculation.
   * @param dfpCat Category of DFP instances in the model.
   * @param dfpInPowerCat Category of inpower port count in the model
   * @param dfpOutPowerCat Category of outpower port count in the model.
   */
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

  /**
   * Similar to the inFlowCoverage() method but for client-server ports.
   *
   * @param dfp The DFP to process for coverage calculation.
   * @param dfpCat Category of DFP instances in the model.
   * @param dfpServerCat Category of server port count in the model.
   * @param dfpClientCat Category of client port count in the model.
   */
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

  /**
   * Creates coverage values for relations of two different DFP in the same model.
   * For example, if there is an input model with two DFP's, where DFP1 has one inflow port and DFP2 has two inflow ports,
   * This creates one coverage value for that.
   * Another would be DFP1 with zero inflow ports and DFP2 with two inflow ports.
   * This is done for all port types.
   *
   * @param dfp1 First part of the pair.
   * @param dfp2 Second part of the pair.
   */
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

    //from here on we do all the port types and their combination for given DFP instances
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

  /**
   * Creates a coverage identifier for two coverage elements.
   * Sorts by name and creates a string representing these two.
   * This is done to make sure same elements are not considered twice in different order as a different coverage value.
   *
   * @param name1 Name of first port type.
   * @param name2 Name of second port type.
   * @param value1 Coverage value for item matching name1.
   * @param value2 Coverage value for item matching name2.
   */
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

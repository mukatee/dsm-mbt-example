package net.kanstren.tt_testing;

import osmo.common.Randomizer;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.Description;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.data.ValueSet;

/**
 * The test model used by the generator to build input models.
 * Simulates an expert building transformation input models piece by piece.
 *
 * @author Teemu Kanstren.
 */
public class EASTModel {
  /** Here we maintain the structure of the input we are generating. */
  private final ModelState state;
  /** The value for this is set by the generator. We use it to give the generator coverage values for optimization. */
  private TestSuite suite;
  /** For randomization, initialized by the test generator. */
  private Randomizer rand = new Randomizer();
  /** For finding DFP pairs to connect. */
  private final FriendFinder finder;

  public EASTModel(ModelState state) {
    this.state = state;
    finder = new FriendFinder(state, rand);
  }

  @Description("Creates a DFP instance in the input model")
  @TestStep(weight = 5)
  public void createDFP() {
    state.createDFP();
  }

  @Description("Creates a DFT instance in the input model")
  @TestStep(weight = 2)
  public void createDFT() {
    state.createDFT();
  }

  @Description("Guards the linkDFPtoDFT step, requiring a DFT and a free DFP to exist. ")
  @Guard("LinkDFPtoDFT")
  public boolean hasUnlinkedDFP() {
    return state.getUnlinkedDFPs().size() > 0 && state.getDFTs().size() > 0;
  }

  @Description("Finds a free DFP and connects it to a random existing DFT.")
  @TestStep(weight = 30)
  public void linkDFPtoDFT() {
    ValueSet<DFP> unlinked = state.getUnlinkedDFPs();
    ValueSet<DFT> dfts = state.getDFTs();
    DFT dft = dfts.random();
    DFP dfp = unlinked.random();
    dfp.setDFT(dft);
    dft.addChild(dfp);
  }

  @Description("Guards the steps for adding ports, requiring there to be a DFT where a port can be added.")
  @Guard({"AddInFlow", "AddOutFlow", "AddInPower", "AddOutPower", "AddServer", "AddClient"})
  public boolean hasDFP() {
    return state.dftCount() > 0;
  }

  @Description("Adds a port to a randomly chosen existing DFT in the input model")
  @TestStep(weight = 20)
  public void addInFlow() {
    state.randomDFT().addInFlow();
  }

  @Description("Adds a port to a randomly chosen existing DFT in the input model")
  @TestStep(weight = 20)
  public void addOutFlow() {
    state.randomDFT().addOutFlow();
  }

  @Description("Adds a port to a randomly chosen existing DFT in the input model")
  @TestStep(weight = 20)
  public void addInPower() {
    state.randomDFT().addInPower();
  }

  @Description("Adds a port to a randomly chosen existing DFT in the input model")
  @TestStep(weight = 20)
  public void addOutPower() {
    state.randomDFT().addOutPower();
  }

  @Description("Adds a port to a randomly chosen existing DFT in the input model")
  @TestStep(weight = 20)
  public void addServer() {
    state.randomDFT().addServer();
  }

  @Description("Adds a port to a randomly chosen existing DFT in the input model")
  @TestStep(weight = 20)
  public void addClient() {
    state.randomDFT().addClient();
  }

  @Description("Guards the AddDescription step, requiring there to be a DFP with no description.")
  @Guard("AddDescription")
  public boolean hasNoDescription() {
    return state.getNoDescDFPs().size() > 0;
  }

  @Description("Adds a description to a random existing DFP that has none so far.")
  @TestStep(weight = 10)
  public void addDescription() {
    DFP dfp = state.getNoDescDFPs().random();
    dfp.setDescription("Description" + dfp.getUid());
  }

  @Description("Guards a step for creating a specific connection type, requiring there exists a free pair of matching ports in two separate DFP instances")
  @Guard("CreateFlowLink")
  public boolean hasFlowPair() {
    return finder.findFlowPair() != null;
  }

  @Description("Guards a step for creating a specific connection type, requiring there exists a free pair of matching ports in two separate DFP instances")
  @Guard("CreatePowerLink")
  public boolean hasPowerPair() {
    return finder.findPowerPair() != null;
  }

  @Description("Guards a step for creating a specific connection type, requiring there exists a free pair of matching ports in two separate DFP instances")
  @Guard("CreateClientServerLink")
  public boolean hasClientServerPair() {
    return finder.findClientServerPair() != null;
  }

  @Description("Creates a connection link between two (random) DFP instances that have a free inflow and outflow port at different ends.")
  @TestStep(weight = 50)
  public void createFlowLink() {
    Tuple<DFP, DFP> pair = finder.findFlowPair();
    pair.getValue1().connectFlowTo(pair.getValue2());
  }

  @Description("Creates a connection link between two (random) DFP instances that have a free inpower and outpower port at different ends.")
  @TestStep(weight = 50)
  public void createPowerLink() {
    Tuple<DFP, DFP> pair = finder.findPowerPair();
    pair.getValue1().connectPowerTo(pair.getValue2());
  }

  @Description("Creates a connection link between two (random) DFP instances that have a free client and server port at different ends.")
  @TestStep(weight = 50)
  public void createClientServerLink() {
    Tuple<DFP, DFP> pair = finder.findClientServerPair();
    pair.getValue1().connectClientServer(pair.getValue2());
  }

  @AfterTest
  public void theEnd() {
    TestCase test = suite.getCurrentTest();
    test.setAttribute("state", state);
    CoverageHelper helper = new CoverageHelper(state, suite);
    helper.createCoverage(test);
  }
}

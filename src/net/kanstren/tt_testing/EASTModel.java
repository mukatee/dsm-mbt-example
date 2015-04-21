package net.kanstren.tt_testing;

import osmo.common.Randomizer;
import osmo.tester.annotation.AfterTest;
import osmo.tester.annotation.Guard;
import osmo.tester.annotation.TestStep;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.data.ValueSet;

/**
 * @author Teemu Kanstren.
 */
public class EASTModel {
  private final ModelState state;
  private TestSuite suite;
  private Randomizer rand = new Randomizer();
  private final FriendFinder finder;

  public EASTModel(ModelState state) {
    this.state = state;
    finder = new FriendFinder(state, rand);
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
    dfp.setDFT(dft);
    dft.addChild(dfp);
  }

  @Guard({"AddInFlow", "AddOutFlow", "AddInPower", "AddOutPower", "AddServer", "AddClient"})
  public boolean hasDFP() {
    return state.dftCount() > 0;
  }

  @TestStep(weight = 20)
  public void addInFlow() {
    state.randomDFT().addInFlow();
  }

  @TestStep(weight = 20)
  public void addOutFlow() {
    state.randomDFT().addOutFlow();
  }

  @TestStep(weight = 20)
  public void addInPower() {
    state.randomDFT().addInPower();
  }

  @TestStep(weight = 20)
  public void addOutPower() {
    state.randomDFT().addOutPower();
  }

  @TestStep(weight = 20)
  public void addServer() {
    state.randomDFT().addServer();
  }

  @TestStep(weight = 20)
  public void addClient() {
    state.randomDFT().addClient();
  }

  @Guard("AddDescription")
  public boolean hasNoDescription() {
    return state.getNoDescDFPs().size() > 0;
  }

  @TestStep(weight = 10)
  public void addDescription() {
    DFP dfp = state.getNoDescDFPs().random();
    dfp.setDescription("Description" + dfp.getUid());
  }

  @Guard("CreateFlowLink")
  public boolean hasFlowPair() {
    return finder.findFlowPair() != null;
  }

  @Guard("CreatePowerLink")
  public boolean hasPowerPair() {
    return finder.findPowerPair() != null;
  }

  @Guard("CreateClientServerLink")
  public boolean hasClientServerPair() {
    return finder.findClientServerPair() != null;
  }

  @TestStep(weight = 50)
  public void createFlowLink() {
    Tuple<DFP, DFP> pair = finder.findFlowPair();
    pair.getValue1().connectFlowTo(pair.getValue2());
  }

  @TestStep(weight = 50)
  public void createPowerLink() {
    Tuple<DFP, DFP> pair = finder.findPowerPair();
    pair.getValue1().connectPowerTo(pair.getValue2());
  }

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

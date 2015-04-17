package net.kanstren.tt_testing;

import osmo.common.Randomizer;
import osmo.tester.model.data.ValueSet;

/**
 * @author Teemu Kanstren.
 */
public class FriendFinder {
  private final ModelState state;
  private final Randomizer rand;

  public FriendFinder(ModelState state, Randomizer rand) {
    this.state = state;
    this.rand = rand;
  }

  public Tuple<DFP, DFP> findFlowPair() {
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
//    System.out.println("pair exists");
    return getUniquePair(inFlows, outFlows);
  }

  public Tuple<DFP, DFP> findPowerPair() {
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

  public Tuple<DFP, DFP> findClientServerPair() {
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

  public Tuple<DFP, DFP> getUniquePair(ValueSet<DFP> set1, ValueSet<DFP> set2) {
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
//    System.out.println("tuple:"+dfp1+","+dfp2);
    return new Tuple<>(dfp1, dfp2);
  }
}

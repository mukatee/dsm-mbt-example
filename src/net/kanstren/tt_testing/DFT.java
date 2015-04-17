package net.kanstren.tt_testing;

import osmo.tester.model.data.ValueSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren.
 */
public class DFT {
  private final int id;
  private final List<DFP> children = new ArrayList<>();
  private ValueSet<DFTPort> inFlows = new ValueSet<>();
  private ValueSet<DFTPort> outFlows = new ValueSet<>();
  private ValueSet<DFTPort> inPowers = new ValueSet<>();
  private ValueSet<DFTPort> outPowers = new ValueSet<>();
  private ValueSet<DFTPort> clients = new ValueSet<>();
  private ValueSet<DFTPort> servers = new ValueSet<>();

  public DFT(int id, long seed) {
    this.id = id;
    inFlows.setSeed(seed);
    outFlows.setSeed(seed);
    inPowers.setSeed(seed);
    outPowers.setSeed(seed);
    clients.setSeed(seed);
    servers.setSeed(seed);
  }

  public void addChild(DFP dfp) {
    children.add(dfp);
  }

  public List<DFP> getChildren() {
    return children;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return "DFT" + id;
  }

  public ValueSet<DFTPort> getInFlows() {
    return inFlows;
  }

  public ValueSet<DFTPort> getOutFlows() {
    return outFlows;
  }

  public ValueSet<DFTPort> getInPowers() {
    return inPowers;
  }

  public ValueSet<DFTPort> getOutPowers() {
    return outPowers;
  }

  public ValueSet<DFTPort> getClients() {
    return clients;
  }

  public ValueSet<DFTPort> getServers() {
    return servers;
  }

  public void addInFlow() {
    int id = inFlows.size() + 1;
    DFTPort port = new DFTPort("InFlow" + id, id);
    inFlows.add(port);
    for (DFP dfp : children) {
      dfp.addInFlow(port);
    }
  }

  public void addOutFlow() {
    int id = outFlows.size() + 1;
    DFTPort port = new DFTPort("OutFlow" + id, id);
    outFlows.add(port);
    for (DFP dfp : children) {
      dfp.addOutFlow(port);
    }
  }

  public void addInPower() {
    int id = inPowers.size() + 1;
    DFTPort port = new DFTPort("InPower" + id, id);
    inPowers.add(port);
    for (DFP dfp : children) {
      dfp.addInPower(port);
    }
  }

  public void addOutPower() {
    int id = outPowers.size() + 1;
    DFTPort port = new DFTPort("OutPower" + id, id);
    outPowers.add(port);
    for (DFP dfp : children) {
      dfp.addOutPower(port);
    }
  }

  public void addClient() {
    int id = clients.size() + 1;
    DFTPort port = new DFTPort("Client" + id, id);
    clients.add(port);
    for (DFP dfp : children) {
      dfp.addClient(port);
    }
  }

  public void addServer() {
    int id = servers.size() + 1;
    DFTPort port = new DFTPort("Server" + id, id);
    servers.add(port);
    for (DFP dfp : children) {
      dfp.addServer(port);
    }
  }
}

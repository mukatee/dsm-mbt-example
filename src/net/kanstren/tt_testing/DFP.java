package net.kanstren.tt_testing;

import osmo.tester.model.data.ValueSet;

/**
 * @author Teemu Kanstren.
 */
public class DFP {
  private ValueSet<Port> inFlows = new ValueSet<>();
  private ValueSet<Port> outFlows = new ValueSet<>();
  private ValueSet<Port> inPowers = new ValueSet<>();
  private ValueSet<Port> outPowers = new ValueSet<>();
  private ValueSet<Port> clients = new ValueSet<>();
  private ValueSet<Port> servers = new ValueSet<>();
  private final int id;
  private String description = null;
  private DFT dft = null;

  public DFP(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return "DFP" + id;
  }

  public void addInFlow() {
    int id = inFlows.size() + 1;
    inFlows.add(new Port("InFlow" + id, id));
  }

  public void addOutFlow() {
    int id = outFlows.size() + 1;
    outFlows.add(new Port("OutFlow" + id, id));
  }

  public void addInPower() {
    int id = inPowers.size() + 1;
    inPowers.add(new Port("InPower" + id, id));
  }

  public void addOutPower() {
    int id = outPowers.size() + 1;
    outPowers.add(new Port("OutPower" + id, id));
  }

  public void addClient() {
    int id = clients.size() + 1;
    clients.add(new Port("Client" + id, id));
  }

  public void addServer() {
    int id = servers.size() + 1;
    servers.add(new Port("Server" + id, id));
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ValueSet<Port> getInFlows() {
    return inFlows;
  }

  public ValueSet<Port> getOutFlows() {
    return outFlows;
  }

  public ValueSet<Port> getInPowers() {
    return inPowers;
  }

  public ValueSet<Port> getOutPowers() {
    return outPowers;
  }

  public ValueSet<Port> getClients() {
    return clients;
  }

  public ValueSet<Port> getServers() {
    return servers;
  }

  public DFT getDft() {
    return dft;
  }

  public void setDft(DFT dft) {
    this.dft = dft;
  }
}

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

  public DFP(int id, long seed) {
    this.id = id;
    inFlows.setSeed(seed);
    outFlows.setSeed(seed);
    inPowers.setSeed(seed);
    outPowers.setSeed(seed);
    clients.setSeed(seed);
    servers.setSeed(seed);
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return "DFP" + id;
  }

  public void addInFlow() {
    int id = inFlows.size() + 1;
    inFlows.add(new Port(this, "InFlow" + id, id));
  }

  public void addOutFlow() {
    int id = outFlows.size() + 1;
    outFlows.add(new Port(this, "OutFlow" + id, id));
  }

  public void addInPower() {
    int id = inPowers.size() + 1;
    inPowers.add(new Port(this, "InPower" + id, id));
  }

  public void addOutPower() {
    int id = outPowers.size() + 1;
    outPowers.add(new Port(this, "OutPower" + id, id));
  }

  public void addClient() {
    int id = clients.size() + 1;
    clients.add(new Port(this, "Client" + id, id));
  }

  public void addServer() {
    int id = servers.size() + 1;
    servers.add(new Port(this, "Server" + id, id));
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

  public void connectFlowTo(DFP target) {
    connectPorts(inFlows, target.getOutFlows());
  }

  public void connectPowerTo(DFP target) {
    connectPorts(inPowers, target.getOutPowers());
  }

  public void connectClientServer(DFP target) {
    connectPorts(clients, target.getServers());
  }

  public void connectPorts(ValueSet<Port> sources, ValueSet<Port> targets) {
    Port source = null;
    Port target = null;
    while (sources.available() > 0 && targets.available() > 0) {
      source = sources.reserve();
      if (source.getPairs().containsAll(targets.getOptions())) {
        source = null;
        continue;
      }

      for (Port port : source.getPairs()) {
        if (targets.getOptions().contains(port)) {
          targets.reserve(port);
        }
      }
      target = targets.reserve();
      break;
    }
    //TODO: add method to free all
    while (sources.reserved() > 0) {
      sources.free(sources.randomReserved());
    }
    while (targets.reserved() > 0) {
      targets.free(targets.randomReserved());
    }
    if (source == null) return;
    source.addPair(target);
    target.addPair(target);
  }
}

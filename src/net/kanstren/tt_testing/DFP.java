package net.kanstren.tt_testing;

import osmo.tester.model.data.ValueSet;

/**
 * @author Teemu Kanstren.
 */
public class DFP {
  private final int id;
  private String description = null;
  private DFT dft = null;
  private ValueSet<DFPPort> inFlows = new ValueSet<>();
  private ValueSet<DFPPort> outFlows = new ValueSet<>();
  private ValueSet<DFPPort> inPowers = new ValueSet<>();
  private ValueSet<DFPPort> outPowers = new ValueSet<>();
  private ValueSet<DFPPort> clients = new ValueSet<>();
  private ValueSet<DFPPort> servers = new ValueSet<>();

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ValueSet<DFPPort> getInFlows() {
    return inFlows;
  }

  public ValueSet<DFPPort> getOutFlows() {
    return outFlows;
  }

  public ValueSet<DFPPort> getInPowers() {
    return inPowers;
  }

  public ValueSet<DFPPort> getOutPowers() {
    return outPowers;
  }

  public ValueSet<DFPPort> getClients() {
    return clients;
  }

  public ValueSet<DFPPort> getServers() {
    return servers;
  }

  public DFT getDft() {
    return dft;
  }

  public void setDFT(DFT dft) {
    this.dft = dft;
    for (DFTPort port : dft.getInFlows().getOptions()) {
      addInFlow(port);
    }
    for (DFTPort port : dft.getOutFlows().getOptions()) {
      addOutFlow(port);
    }
    for (DFTPort port : dft.getInPowers().getOptions()) {
      addInPower(port);
    }
    for (DFTPort port : dft.getOutPowers().getOptions()) {
      addOutPower(port);
    }
    for (DFTPort port : dft.getServers().getOptions()) {
      addServer(port);
    }
    for (DFTPort port : dft.getClients().getOptions()) {
      addClient(port);
    }
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

  public void connectPorts(ValueSet<DFPPort> sources, ValueSet<DFPPort> targets) {
    DFPPort source = null;
    DFPPort target = null;
    while (sources.available() > 0 && targets.available() > 0) {
      source = sources.reserve();
      if (source.getPairs().containsAll(targets.getOptions())) {
        source = null;
        continue;
      }

      for (DFPPort port : source.getPairs()) {
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
    target.addPair(source);
  }

  public void addInFlow(DFTPort port) {
    inFlows.add(new DFPPort(port, this));
  }

  public void addOutFlow(DFTPort port) {
    outFlows.add(new DFPPort(port, this));
  }

  public void addInPower(DFTPort port) {
    inPowers.add(new DFPPort(port, this));
  }

  public void addOutPower(DFTPort port) {
    outPowers.add(new DFPPort(port, this));
  }

  public void addServer(DFTPort port) {
    servers.add(new DFPPort(port, this));
  }

  public void addClient(DFTPort port) {
    clients.add(new DFPPort(port, this));
  }
}

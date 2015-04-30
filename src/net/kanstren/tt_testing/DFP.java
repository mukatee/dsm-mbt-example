package net.kanstren.tt_testing;

import osmo.tester.model.data.ValueSet;

/**
 * Represents a DesignFunctionPrototype in the generated input models.
 *
 * @author Teemu Kanstren.
 */
public class DFP {
  /** And id value used to create readable names. */
  private final int id;
  /** Unique ID value across all elements, required by MetaEdit+. */
  private final int uid;
  /** DFP description, if any. If null, it is not included in the input model. */
  private String description = null;
  /** DesignFunctionType for this DFP. */
  private DFT dft = null;
  /** InFlow ports generated for this DFP. */
  private ValueSet<DFPPort> inFlows = new ValueSet<>();
  /** OutFlow ports generated for this DFP. */
  private ValueSet<DFPPort> outFlows = new ValueSet<>();
  /** InPower ports generated for this DFP. */
  private ValueSet<DFPPort> inPowers = new ValueSet<>();
  /** OutPower ports generated for this DFP. */
  private ValueSet<DFPPort> outPowers = new ValueSet<>();
  /** Client ports generated for this DFP. */
  private ValueSet<DFPPort> clients = new ValueSet<>();
  /** Server ports generated for this DFP. */
  private ValueSet<DFPPort> servers = new ValueSet<>();

  public DFP(int id, long seed) {
    this.id = id;
    this.uid = UID.next();
    inFlows.setSeed(seed);
    outFlows.setSeed(seed);
    inPowers.setSeed(seed);
    outPowers.setSeed(seed);
    clients.setSeed(seed);
    servers.setSeed(seed);
  }

  public int getUid() {
    return uid;
  }

  public String getName() {
    return "DFP"+id;
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

  /**
   * We always have to synchronize between DFT and DFP to make sure they have the same number of ports.
   * We need separate port definitions since different DFP may have different connections.
   *
   * @param dft The type to set..
   */
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

  /**
   * Finds a pair of free ports of the same type and connects when.
   *
   * @param sources The source ports where to find a free port.
   * @param targets The target ports where to find a free port.
   */
  public void connectPorts(ValueSet<DFPPort> sources, ValueSet<DFPPort> targets) {
    DFPPort source = null;
    DFPPort target = null;
    //first we go through all the options and remove the ones that are not possible
    while (sources.available() > 0 && targets.available() > 0) {
      source = sources.reserve();
      //we have a source port but do we have a possible target port for this source port?
      if (source.getPairs().containsAll(targets.getOptions())) {
        source = null;
        continue;
      }

      //now we have a source port and there should be a possible target port, so lets find one
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
    //well, if we failed to find a match, lets exit..
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

package net.kanstren.tt_testing;

import osmo.tester.model.data.Text;
import osmo.tester.model.data.ValueSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a DesignFunctionType.
 *
 * @author Teemu Kanstren.
 */
public class DFT {
  /** Unique ID for MetaEdit+. */
  private final int uid;
  /** A running integer to give readable names to instances. */
  private final int id;
  /** Description if any. Null means input model has no description for this DFT. */
  private final String description;
  /** The DFP instances that have this DFT as type. */
  private final List<DFP> children = new ArrayList<>();
  /** InFlow ports for this DFT. */
  private ValueSet<DFTPort> inFlows = new ValueSet<>();
  /** OutFlow ports for this DFT. */
  private ValueSet<DFTPort> outFlows = new ValueSet<>();
  /** InPower ports for this DFT. */
  private ValueSet<DFTPort> inPowers = new ValueSet<>();
  /** OutPower ports for this DFT. */
  private ValueSet<DFTPort> outPowers = new ValueSet<>();
  /** Client ports for this DFT. */
  private ValueSet<DFTPort> clients = new ValueSet<>();
  /** Server ports for this DFT. */
  private ValueSet<DFTPort> servers = new ValueSet<>();
  /** Used in template engine to only define this once inside a DFP (MetaEdit+ requires this..) */
  private boolean defined = false;

  public DFT(int id, long seed) {
    this.id = id;
    //initialize random generators
    Text text = new Text(5, 10).asciiLettersAndNumbersOnly();
    text.setSeed(seed);
    this.description = text.random();
    this.uid = UID.next();
    inFlows.setSeed(seed);
    outFlows.setSeed(seed);
    inPowers.setSeed(seed);
    outPowers.setSeed(seed);
    clients.setSeed(seed);
    servers.setSeed(seed);
  }

  public boolean getDefined() {
    boolean value = defined;
    defined = true;
    return value;
  }

  public void addChild(DFP dfp) {
    children.add(dfp);
  }

  public List<DFP> getChildren() {
    return children;
  }

  public int getUid() {
    return uid;
  }

  public String getName() {
    return "DFT"+id;
  }

  public String getDescription() {
    return description;
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

  /**
   * Adds a port, and makes sure all attached DFP instances so far are synchronized with same ports.
   */
  public void addInFlow() {
    int n = inFlows.size() + 1;
    DFTPort port = new DFTPort("InFlow" + n, UID.next(), n);
    inFlows.add(port);
    for (DFP dfp : children) {
      dfp.addInFlow(port);
    }
  }

  /**
   * Adds a port, and makes sure all attached DFP instances so far are synchronized with same ports.
   */
  public void addOutFlow() {
    int n = outFlows.size() + 1;
    DFTPort port = new DFTPort("OutFlow" + n, UID.next(), n);
    outFlows.add(port);
    for (DFP dfp : children) {
      dfp.addOutFlow(port);
    }
  }

  /**
   * Adds a port, and makes sure all attached DFP instances so far are synchronized with same ports.
   */
  public void addInPower() {
    int n = inPowers.size() + 1;
    DFTPort port = new DFTPort("InPower" + n, UID.next(), n);
    inPowers.add(port);
    for (DFP dfp : children) {
      dfp.addInPower(port);
    }
  }

  /**
   * Adds a port, and makes sure all attached DFP instances so far are synchronized with same ports.
   */
  public void addOutPower() {
    int n = outPowers.size() + 1;
    DFTPort port = new DFTPort("OutPower" + n, UID.next(), n);
    outPowers.add(port);
    for (DFP dfp : children) {
      dfp.addOutPower(port);
    }
  }

  /**
   * Adds a port, and makes sure all attached DFP instances so far are synchronized with same ports.
   */
  public void addClient() {
    int n = clients.size() + 1;
    DFTPort port = new DFTPort("Client" + n, UID.next(), n);
    clients.add(port);
    for (DFP dfp : children) {
      dfp.addClient(port);
    }
  }

  /**
   * Adds a port, and makes sure all attached DFP instances so far are synchronized with same ports.
   */
  public void addServer() {
    int n = servers.size() + 1;
    DFTPort port = new DFTPort("Server" + n, UID.next(), n);
    servers.add(port);
    for (DFP dfp : children) {
      dfp.addServer(port);
    }
  }
}

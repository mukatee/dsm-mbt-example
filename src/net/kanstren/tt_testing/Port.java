package net.kanstren.tt_testing;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren.
 */
public class Port {
  private final DFP parent;
  private final String name;
  private final int id;
  private List<Port> pairs = new ArrayList<>();

  public Port(DFP dfp, String name, int id) {
    this.parent = dfp;
    this.name = name;
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public int getId() {
    return id;
  }

  public void addPair(Port pair) {
    pairs.add(pair);
  }

  public List<Port> getPairs() {
    return pairs;
  }

  public DFP getParent() {
    return parent;
  }
}

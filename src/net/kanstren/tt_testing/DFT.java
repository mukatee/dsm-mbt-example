package net.kanstren.tt_testing;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren.
 */
public class DFT {
  private final int id;
  private final List<DFP> children = new ArrayList<>();

  public DFT(int id) {
    this.id = id;
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
}

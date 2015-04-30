package net.kanstren.tt_testing;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teemu Kanstren.
 */
public class DFPPort {
  private final DFTPort dftPort;
  private final DFP dfp;
  private List<DFPPort> pairs = new ArrayList<>();

  public DFPPort(DFTPort dftPort, DFP dfp) {
    this.dfp = dfp;
    this.dftPort = dftPort;
  }

  public String getName() {
    return dftPort.getName();
  }

  public int getId() {
    return dftPort.getId();
  }

  public void setPosition(int position) {
    this.dftPort.setPosition(position);
//    this.dftPort.setPo;
  }

  public int getPosition() {
    return dftPort.getPosition();
  }

  public void addPair(DFPPort pair) {
    pairs.add(pair);
  }

  public List<DFPPort> getPairs() {
    return pairs;
  }

  public DFP getDfp() {
    return dfp;
  }
}

package net.kanstren.tt_testing;

/**
 * @author Teemu Kanstren.
 */
public class DFTPort {
  private final String name;
  private final int id;

  public DFTPort(String name, int id) {
    this.name = name;
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public int getId() {
    return id;
  }
}

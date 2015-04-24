package net.kanstren.tt_testing;

/**
 * @author Teemu Kanstren.
 */
public class DFTPort {
  private final String name;
  private final int id;
  private final int position;

  public DFTPort(String name, int id, int position) {
    this.name = name;
    this.id = id;
    this.position = position;
  }

  public String getName() {
    return name;
  }

  public int getId() {
    return id;
  }

  public int getPosition() {
    return position;
  }
}

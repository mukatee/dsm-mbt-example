package net.kanstren.tt_testing;

/**
 * @author Teemu Kanstren.
 */
public class DFTPort {
  private final String name;
  private final int id;
  private int position;

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

  public void setPosition(int position) {
    this.position = position;
  }

  public int getPosition() {
    return position;
  }
}

package net.kanstren.tt_testing;

/**
 * For generating unique ID values for MetaEdit+ models.
 * Should probably be modified to produce only positive values if very large sets and optimizations were run.
 *
 * @author Teemu Kanstren.
 */
public class UID {
  private static int n = 1;

  public int getNext() {
    return n++;
  }

  public static int next() {
    return n++;
  }
}

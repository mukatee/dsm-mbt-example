package net.kanstren.tt_testing;

/**
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

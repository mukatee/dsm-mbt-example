package net.kanstren.tt_testing;

/**
 * A fancy name for a pair..
 *
 * @author Teemu Kanstren.
 */
public class Tuple<T, K> {
  private final T value1;
  private final K value2;

  public Tuple(T value1, K value2) {
    this.value1 = value1;
    this.value2 = value2;
  }

  public T getValue1() {
    return value1;
  }

  public K getValue2() {
    return value2;
  }
}

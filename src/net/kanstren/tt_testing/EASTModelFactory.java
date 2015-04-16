package net.kanstren.tt_testing;

import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;

/**
 * @author Teemu Kanstren.
 */
public class EASTModelFactory implements ModelFactory {
  @Override
  public void createModelObjects(TestModels here) {
    ModelState state = new ModelState();
    here.add(state);
    here.add(new EASTModel(state));
  }
}

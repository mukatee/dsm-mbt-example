package net.kanstren.tt_testing;

import osmo.tester.model.ModelFactory;
import osmo.tester.model.TestModels;

/**
 * Used by the generator to create instances of the model for running several generator variants in parallel.
 * Needed to build and attach state to the test model as otherwise generator could not guess this..
 *
 * @author Teemu Kanstren.
 */
public class EASTModelFactory implements ModelFactory {
  @Override
  public void createModelObjects(TestModels here) {
    ModelState state = new ModelState();
    //add state also so generator knows to initialize all model objects with randomization seed in the state
    here.add(state);
    //and the test model object itself, with the state just created
    here.add(new EASTModel(state));
  }
}

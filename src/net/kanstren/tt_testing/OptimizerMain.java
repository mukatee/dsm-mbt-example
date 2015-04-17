package net.kanstren.tt_testing;

import osmo.common.TestUtils;
import osmo.tester.OSMOConfiguration;
import osmo.tester.coverage.ScoreConfiguration;
import osmo.tester.coverage.TestCoverage;
import osmo.tester.generator.algorithm.WeightedRandomAlgorithm;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.optimizer.greedy.MultiGreedy;
import osmo.tester.reporting.coverage.HTMLCoverageReporter;

import java.util.List;

/**
 * @author Teemu Kanstren.
 */
public class OptimizerMain {
  public static void main(String[] args) {
    OSMOConfiguration oc = new OSMOConfiguration();
    oc.setTestEndCondition(new LengthProbability(5, 0.1));
    oc.setFactory(new EASTModelFactory());
    oc.setAlgorithm(new WeightedRandomAlgorithm());
    oc.setTrackOptions(false);
    ScoreConfiguration config = new ScoreConfiguration();
    config.setLengthWeight(0);
    config.setStepWeight(0);
    config.setStateWeight(0);
    config.setStepPairWeight(0);
    config.setStatePairWeight(0);
    config.setRequirementWeight(0);

    long seed = Long.parseLong(args[0]);
    int timeout = Integer.parseInt(args[1]);
    MultiGreedy greedy = new MultiGreedy(oc, config, seed);
    greedy.setTimeout(timeout);
    greedy.setPopulationSize(10000);
    List<TestCase> tests = greedy.search();

    Scripter scripter = new Scripter();
    int testId = 1;
    for (TestCase test : tests) {
      ModelState state = (ModelState) test.getAttribute("state");
      scripter.writeScript(state, "scripts/test" + testId + ".txt");
      scripter.writeChecks(state, "scripts/checks" + testId + ".txt");
      scripter.writeTrace(test, "scripts/trace"+testId+".txt");
      testId++;
    }
    TestCoverage coverage = new TestCoverage(tests);
    HTMLCoverageReporter html = new HTMLCoverageReporter(coverage, tests, greedy.getFsm());
    String actual = html.getTraceabilityMatrix();
    TestUtils.write(actual, "test-matrix.html");
  }
}

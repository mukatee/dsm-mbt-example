package net.kanstren.tt_testing;

import osmo.common.TestUtils;
import osmo.tester.OSMOTester;
import osmo.tester.generator.algorithm.WeightedRandomAlgorithm;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.endcondition.LengthProbability;
import osmo.tester.generator.listener.TracePrinter;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.reporting.coverage.HTMLCoverageReporter;

/**
 * @author Teemu Kanstren.
 */
public class RandomMain {
  public static void main(String[] args) {
    OSMOTester tester = new OSMOTester();
    tester.addListener(new TracePrinter());
    tester.setModelFactory(new EASTModelFactory());
    tester.setAlgorithm(new WeightedRandomAlgorithm());
    tester.setTestEndCondition(new LengthProbability(5, 0.1));
    tester.setSuiteEndCondition(new Length(10));
    long seed = Long.parseLong(args[0]);
    tester.generate(seed);
    TestSuite suite = tester.getSuite();
    Scripter scripter = new Scripter("metaedit.vm", "checks.vm", "run_me.vm");
    int testId = 1;
    for (TestCase test : suite.getAllTestCases()) {
      ModelState state = (ModelState) test.getAttribute("state");
      String dir = "scripts/test"+testId;
      scripter.writeBatFile("input"+testId+".mxm", dir+"/run_me.bat", "checker"+testId+".py");
      scripter.writeScript(state, dir + "/input" + testId + ".mxm", "output" + testId);
      scripter.writeChecks(state, dir+"/checker" + testId + ".py", "reports/output"+testId+".mdl");
      scripter.writeTrace(test, dir+"/trace"+testId+".txt");
      testId++;
    }
    HTMLCoverageReporter html = new HTMLCoverageReporter(suite.getCoverage(), suite.getAllTestCases(), tester.getFsm());
    String actual = html.getTraceabilityMatrix();
    TestUtils.write(actual, "test-matrix.html");
  }
}

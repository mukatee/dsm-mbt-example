package net.kanstren.tt_testing;

import osmo.common.Randomizer;
import osmo.tester.model.data.ValueSet;

import java.util.List;

/**
 * @author Teemu Kanstren.
 */
public class ModelState {
  private ValueSet<DFP> dfps = new ValueSet<>();
  private ValueSet<DFT> dfts = new ValueSet<>();
  private Randomizer rand = new Randomizer();

  public int dftCount() {
    return dfts.size();
  }

  public DFT randomDFT() {
    return dfts.random();
  }

  public int dfpCount() {
    return dfps.size();
  }

  public DFP randomDFP() {
    return dfps.random();
  }

  public void createDFP() {
    dfps.add(new DFP(dfps.size() + 1, rand.nextLong()));
  }

  public void createDFT() {
    dfts.add(new DFT(dfts.size() + 1, rand.nextLong()));
  }

  public ValueSet<DFP> getDFPs() {
    return dfps;
  }

  public ValueSet<DFT> getDFTs() {
    return dfts;
  }

  public ValueSet<DFP> getNoDescDFPs() {
    ValueSet<DFP> result = new ValueSet<>();
    result.setSeed(rand.nextLong());
    List<DFP> options = dfps.getOptions();
    for (DFP dfp : options) {
      if (dfp.getDescription() == null) result.add(dfp);
    }
    return result;
  }

  public ValueSet<DFP> getUnlinkedDFPs() {
    ValueSet<DFP> result = new ValueSet<>();
    result.setSeed(rand.nextLong());
    List<DFP> options = dfps.getOptions();
    for (DFP dfp : options) {
      if (dfp.getDft() == null) result.add(dfp);
    }
    return result;
  }
}

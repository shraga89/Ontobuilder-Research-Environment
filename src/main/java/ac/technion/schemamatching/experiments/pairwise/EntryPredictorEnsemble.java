package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

public class EntryPredictorEnsemble implements PairWiseExperiment {

	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}
}

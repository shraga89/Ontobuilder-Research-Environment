package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * Matches using all 1LM and 2LM supplied and returns
 * precision and recall 
 * @author Tomer Sagi
 *
 */
public class SimpleMatchExperiment implements MatchingExperiment {
	private ArrayList<FirstLineMatcher> flM;
	private ArrayList<SecondLineMatcher> slM;

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		// Using all 1st line matchers 
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		for (FirstLineMatcher m : flM)
		{
			//Match
			MatchInformation mi = m.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			//Using all second line matchers
			for (SecondLineMatcher s : slM)
			{
				//Second Line Match
				MatchInformation mi1 = s.match(mi);
				//calculate Precision and Recall
				K2Statistic b2 = new BinaryGolden();
				String instanceDesc =  esp.getSPID() + "," + m.getName() + "," + s.getName();
				b2.init(instanceDesc, mi1,esp.getExact());
				evaluations.add(b2);
			}
			
		}
		return evaluations;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer,Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		this.flM = flM;
		this.slM = slM;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return "Matrix Predictor Evaluation";
	}
	
	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}

}

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
 * This simple match experiment is intended as a tutorial for 
 * new developers displaying the major features in ORE. 
 * The experiment matches a given schema pair using all 1LM and 2LM 
 * supplied and returns precision and recall 
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
			MatchInformation mi = null;
			//Direct matching using the first line matcher allows to set parameters in the flm
			//mi = m.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			
			/*Preferred method is to use this method which looks up 
			 * the similarity matrix in the database if it exists. 
			*/
			mi = esp.getSimilarityMatrix(m);
			
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
		/*Using the supplied first line matcher list and second line matcher list allows run-time 
		changes to matchers used in the experiment*/
		this.flM = flM;
		this.slM = slM;
		//using property files allows to modify experiment parameters at runtime 
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return "Simple match experiment (Developer Tutorial)";
	}
	
	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}

}

package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.InstanceMatcher;
import ac.technion.schemamatching.matchers.secondline.OBThreshold;
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
 * @author Anna Margolin
 *
 */
public class InstanceMatchExperiment implements PairWiseExperiment {
	private double th = 0.5;
	private double matchTH=0.5;
	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		long ms=System.currentTimeMillis(); 
		// Using all 1st line matchers 
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		
		InstanceMatcher im = new InstanceMatcher(th); //use th here
		MatchInformation mi = im.match(esp.getCandidateOntology(), esp.getTargetOntology(),false);
		//SecondLineMatcher s  = new OBStableMarriage();
		SecondLineMatcher s  = new OBThreshold(matchTH);
		//Second Line Match
		MatchInformation mi1 = s.match(mi);
		
		//calculate Precision and Recall
		K2Statistic b2 = new BinaryGolden();
		String instanceDesc =  esp.getID() + ",InstanceMatcher, OBThreshold";
		b2.init(instanceDesc, mi1,esp.getExact());
		//b2.init(instanceDesc, mi,esp.getExact());
		evaluations.add(b2);
		System.out.println((System.currentTimeMillis()-ms)/1000);
		return evaluations;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		th = Double.parseDouble(properties.getProperty("Threshold"));
		matchTH=Double.parseDouble(properties.getProperty("MatchThreshold"));
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

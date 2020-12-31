/**
 * 
 */
package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.statistics.BinaryROCStatistics;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.OBTermMatch;
import ac.technion.schemamatching.matchers.secondline.OBThreshold;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;

/**
 * @author Adham Hurani
 * This experiments generates ROC Curve points 
 * by calculation the True Positive Ratio vs. 
 * False Positive Ratio
 */
public class ROCExperiment implements PairWiseExperiment 
{
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		double weightNGram = 0.4;
		double weightJaro = 0.2;
		double stringNameWeight = 0.25;
		double wordNameWeight = 0.75;
		double stringLabelWeight = 0;
		double wordLabelWeight = 0;
		
			
			
		String instanceDescription = esp.getID() + "," + Double.toString(stringNameWeight) + "," + Double.toString(stringLabelWeight); 
		//Run Term using these weights on supplied experiment schema pair
		OBTermMatch obt = new OBTermMatch(weightNGram,weightJaro, wordNameWeight, stringNameWeight, stringLabelWeight, wordLabelWeight);
		MatchInformation mi = obt.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
		
		
		//OBValueMatch obt = new OBValueMatch();
		//MatchInformation mi = obt.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
		
		
		//2ndLine match using Threshold (0.25)
		OBThreshold th1 = new OBThreshold(0.25);
		MatchInformation mi2 = th1.match(mi);
		
		//Generate binary statistics
		BinaryROCStatistics rocStat = new BinaryROCStatistics();
		rocStat.init(instanceDescription, mi2,esp.getExact());
		res.add(rocStat);
		
		return res; //No statistics here, Training weights are given after all pairs are added
		
		
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		//no init needed
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		String desc = "This experiments generates tuning information for Term " 
						+ "using Precision, Recall, NBPrecision and NBRecall by " 
						+ " varying the weights of the 2 coomponents in 0.05 increments "
						+ " over a given schema pair";
		return desc;
	}

	public ArrayList<Statistic> summaryStatistics() {
		return null;
	}

}

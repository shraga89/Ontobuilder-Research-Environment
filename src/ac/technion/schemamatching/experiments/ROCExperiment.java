/**
 * 
 */
package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.FirstLineMatcher;
import ac.technion.schemamatching.matchers.OBTermMatch;
import ac.technion.schemamatching.matchers.OBValueMatch;
import ac.technion.schemamatching.matchers.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.BinaryROCStatistics;
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.matchers.OBThreshold;
import ac.technion.schemamatching.matchers.OBStableMarriage;

/**
 * @author Adham Hurani
 * This experiments generates ROC Curve points 
 * by calculation the True Positive Ratio vs. 
 * False Positive Ratio
 */
public class ROCExperiment implements MatchingExperiment 
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
		
			
			
		String instanceDescription = esp.getSPID() + "," + Double.toString(stringNameWeight) + "," + Double.toString(stringLabelWeight); 
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
		
		
		
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
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

}

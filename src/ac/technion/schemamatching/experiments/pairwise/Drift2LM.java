/**
 * 
 */
package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.L2distance;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * @author Tomer Sagi
 * This experiments calculates drift between 1LM and 2LM
 * using L2 distance
 */
public class Drift2LM implements PairWiseExperiment 
{
	ArrayList<FirstLineMatcher> flm;
	ArrayList<SecondLineMatcher> slm;
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) 
	{
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		for (FirstLineMatcher f : flm)
		{
			for (SecondLineMatcher s : slm)
			{
				String instanceDescription = esp.getID() + "," + f.getName() + "," + s.getName();
				MatchInformation fMI = esp.getSimilarityMatrix(f);
				K2Statistic d1 = new L2distance();
				//Distance between flm and reference
				d1.init(instanceDescription + ",flm-reference", fMI, esp.getExact());
				res.add(d1);
				MatchInformation sMI = s.match(fMI);
				//Distance between slm and exact
				K2Statistic d2 = new L2distance();
				d2.init(instanceDescription + ",slm-exact", sMI, esp.getExact());
				res.add(d2);
				//Distance between  1lm and 2lm
				K2Statistic d3 = new L2distance();
				d3.init(instanceDescription + ",flm-slm", fMI,sMI);
				res.add(d3);
				
			}
		}		
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		flm = flM;
		slm = slM;
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
		//unused
		return null;
	}
}

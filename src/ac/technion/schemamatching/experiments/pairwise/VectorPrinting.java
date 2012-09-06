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
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.VectorPrinterUsingExact;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * @author Tomer Sagi
 * This experiment prints vectors of match results using all 1st and 2nd line matchers supplied
 *
 */
public class VectorPrinting implements PairWiseExperiment {

	private ArrayList<FirstLineMatcher> flM = new ArrayList<FirstLineMatcher>();
	private ArrayList<SecondLineMatcher> slM = new ArrayList<SecondLineMatcher>(); 

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.testbed.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		ArrayList<Statistic> vectors = new ArrayList<Statistic>();
		for (FirstLineMatcher m : flM)
		{
			//Match
			MatchInformation mi = m.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			K2Statistic v = new VectorPrinterUsingExact();
			v.init(m.getName(), mi,esp.getExact());
			vectors.add(v);
			for (SecondLineMatcher slm : slM)
			{
				MatchInformation mi1 = slm.match(mi);
			    K2Statistic v2 = new VectorPrinterUsingExact();
				v2.init(m.getName() + "," + slm.getName(), mi1,esp.getExact());
				vectors.add(v2);
			}
		}
		return vectors;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		this.flM = flM;
		this.slM = slM;
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		String res = "This experiment prints vectors of match results using all 1st and 2nd line matchers supplied";
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#summaryStatistics()
	 */
	public ArrayList<Statistic> summaryStatistics() {
		//Irrelevant
		return null;
	}

}

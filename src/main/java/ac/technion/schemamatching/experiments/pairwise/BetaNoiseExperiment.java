package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.NBGoldenAtK;
import ac.technion.schemamatching.statistics.NBGoldenAtR;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * Vary noise level taken from a beta distribution and use different 
 * informed K=2 evaluators to examine behavior w.r.t varied noise
 * @author Tomer Sagi
 *
 */
public class BetaNoiseExperiment implements PairWiseExperiment {
	private Properties properties;
	private double increment = 1.0;
	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		// Using all 1st line matchers 
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		double a = 1.0;
		for(double b = 5.0;b>=1.0;b-=increment)
		{	
			MatchInformation mi = esp.getExact().clone();
			ConversionUtils.betaNoise(mi,a,b);
			//Calculate Non-Binary Measures
			String instanceDesc =  esp.getID() + "," + b;
			K2Statistic nbAtK = new NBGoldenAtK();
			nbAtK.init(instanceDesc, mi,esp.getExact());
			evaluations.add(nbAtK);
			K2Statistic nb = new NBGolden();
			nb.init(instanceDesc, mi,esp.getExact());
			evaluations.add(nb);
			K2Statistic nbAtR = new NBGoldenAtR();
			nbAtR.init(instanceDesc, mi,esp.getExact());
			evaluations.add(nbAtR);
		}
		return evaluations;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		this.properties = properties;
		if (this.properties != null)
			if (this.properties.containsKey("increment"))
				this.increment = Double.parseDouble((String) this.properties.get("increment"));
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return "Sensitivity of evaluators to Beta distributed noise ";
	}
	
	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}

}

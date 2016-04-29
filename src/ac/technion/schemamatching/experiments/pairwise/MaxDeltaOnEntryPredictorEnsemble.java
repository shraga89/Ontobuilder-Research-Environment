package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.OBMaxDelta;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MatchCompetitorDeviationEntryLevel;
import ac.technion.schemamatching.statistics.MatchDistance;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * Compare plain vanilla max delta to using an entry predictor ensemble instead of the
 * raw confidence.
 * @author Tomer Sagi
 *
 */
public class MaxDeltaOnEntryPredictorEnsemble implements PairWiseExperiment {
	private ArrayList<FirstLineMatcher> flM;
	private Properties properties;
	private boolean isMemory;
	private double beta = 0.5d;

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
			mi = esp.getSimilarityMatrix(m, isMemory);
			
			String instanceDesc =  esp.getID() + "," + m.getName();
	
			//Calculate MatchDisatance
			K2Statistic md = new MatchDistance();
			md.init(instanceDesc, mi,esp.getExact());
			evaluations.add(md);
			
			//Run regular max Delta
			OBMaxDelta s1 = new OBMaxDelta(0.1);
			s1.init(properties);
			MatchInformation mi1 = s1.match(mi);
			
			//calculate Precision and Recall
			K2Statistic b2 = new BinaryGolden();
			instanceDesc =  esp.getID() + "," + m.getName() + ",Val," + s1.getConfig();
			b2.init(instanceDesc, mi1,esp.getExact());
			evaluations.add(b2);
			//Calculate MatchDistance
			K2Statistic md2 = new MatchDistance();
			md2.init(instanceDesc, mi1,esp.getExact());
			evaluations.add(md2);
			
			//Run max delta on MCD predictions
			MatchCompetitorDeviationEntryLevel mcd = new MatchCompetitorDeviationEntryLevel();
			mcd.init(instanceDesc, mi, mi);
			ConversionUtils.combineMatrices(mcd.getValMI(),mi,beta);
			MatchInformation mi2 = s1.match(mcd.getValMI());
			

			//calculate Precision and Recall
			K2Statistic b3 = new BinaryGolden();
			instanceDesc =  esp.getID() + "," + m.getName() + ",MCD," + s1.getConfig();
			b3.init(instanceDesc, mi2,esp.getExact());
			evaluations.add(b3);
			//Calculate MatchDistance
			K2Statistic md3 = new MatchDistance();
			md3.init(instanceDesc, mi2,esp.getExact());
			evaluations.add(md3);
			
			
		}
		return evaluations;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		this.flM = flM;
		this.properties = properties;
		this.isMemory = isMemory;
		beta = (this.properties.containsKey("beta") ? (Double)this.properties.get("beta") : beta);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return "Max Delta using prediction ensemble";
	}
	
	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}

}

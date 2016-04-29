package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SLMList;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.EntryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MatchCompetitorDeviationEntryLevel;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.predictors.EntryPredictors;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * Evaluates entry predictors by returning the predictor value next to
 * precision, recall and L2 similarity measures 
 * @author Tomer Sagi
 *
 */
public class EntryPredictorEvaluation implements PairWiseExperiment {
	private ArrayList<FirstLineMatcher> flM;
	private boolean isMemory;

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		// Using all 1st line matchers 
		ArrayList<Statistic> predictions =  new ArrayList<Statistic>();
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		for (FirstLineMatcher m : flM)
		{
			//Match
			MatchInformation mi = esp.getSimilarityMatrix(m, isMemory);
			
			// Calculate predictors
			Statistic  p = new EntryPredictors();
			String instanceDesc = esp.getID() + "_"+m.getName()+"_"+m.getConfig();
			p.init(instanceDesc, mi);
			predictions.add(p);
//			//Calculate NBprecision, NBrecall
//			K2Statistic  nb = new EntryGolden();
//			nb.init(instanceDesc, mi,esp.getExact());
//			evaluations.add(nb);
			//Precision Recall
			MatchInformation matchSelected = SLMList.OBMWBG.getSLM().match(mi);
			K2Statistic mcd = new MatchCompetitorDeviationEntryLevel();
			mcd.init(instanceDesc, mi, matchSelected);
			evaluations.add(mcd);
			K2Statistic b = new EntryGolden();
			b.init(instanceDesc, matchSelected,esp.getExact());
			evaluations.add(b);
//			//Vectors
//			K2Statistic pr = new VectorPrinterUsingExact();
//			pr.init(instanceDesc, mi, esp.getExact());
//			evaluations.add(pr);
//			K2Statistic prms = new VectorPrinterUsingExact();
//			prms.init(instanceDesc, matchSelected, esp.getExact());
//			evaluations.add(prms);

		}
		predictions.addAll(evaluations);
		return predictions;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		this.flM = flM;
		this.isMemory = isMemory;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return "Attribute Predictor Evaluation";
	}

	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}
}

package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SLMList;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.AttributeNBGolden;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.VectorPrinterUsingExact;
import ac.technion.schemamatching.statistics.predictors.AttributePredictors;
import ac.technion.schemamatching.statistics.predictors.MCDAPredictor;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * Evaluates attribute predictors by returning the predictor value next to
 * precision, recall and L2 similarity measures 
 * @author Tomer Sagi
 *
 */
public class AttributePredictorEvaluation implements PairWiseExperiment {
	private ArrayList<FirstLineMatcher> flM;

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
			MatchInformation mi = esp.getSimilarityMatrix(m);
			MatchInformation mi1 = SLMList.OBMaxDelta005.getSLM().match(mi);
			MatchInformation mi2 = SLMList.OBMax.getSLM().match(mi);
			MatchInformation mi3 = SLMList.OBThreshold050.getSLM().match(mi);
			// Calculate predictors
			Statistic  p = new AttributePredictors();
			String instanceDesc = esp.getID() + "_"+m.getName()+"_"+m.getConfig();
			p.init(instanceDesc, mi);
			predictions.add(p);
			//	MCDA predictor
			K2Statistic MCDA1 = new MCDAPredictor();
			String instanceDesc_MCDA = esp.getID()+","+m.getName()+","+"MaxDelta005";
			MCDA1.init(instanceDesc_MCDA, mi, mi1);
			predictions.add(MCDA1);
			
			K2Statistic MCDA2 = new MCDAPredictor();
			instanceDesc_MCDA = esp.getID()+","+m.getName()+","+"MaxDelta0";
			MCDA2.init(instanceDesc_MCDA, mi, mi2);
			predictions.add(MCDA2);
			
			K2Statistic MCDA3 = new MCDAPredictor();
			instanceDesc_MCDA = esp.getID()+","+m.getName()+","+"Threshold050";
			MCDA3.init(instanceDesc_MCDA, mi, mi3);
			predictions.add(MCDA3);
			
			//Calculate NBprecision, NBrecall
			K2Statistic  nb = new AttributeNBGolden();
			nb.init(instanceDesc, mi,esp.getExact());
			evaluations.add(nb);
			//Precision Recall
			MatchInformation matchSelected = SLMList.OBSM.getSLM().match(mi);
			K2Statistic b = new BinaryGolden();
			b.init(instanceDesc, matchSelected,esp.getExact());
			evaluations.add(b);
			//Vectors
			K2Statistic pr = new VectorPrinterUsingExact();
			pr.init(instanceDesc, mi, esp.getExact());
			evaluations.add(pr);
			K2Statistic prms = new VectorPrinterUsingExact();
			prms.init(instanceDesc, matchSelected, esp.getExact());
			evaluations.add(prms);

		}
		predictions.addAll(evaluations);
		return predictions;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer,Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		this.flM = flM;
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

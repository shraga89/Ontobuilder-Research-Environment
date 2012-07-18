package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SLMList;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MatrixPredictors;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * Evaluates matrix predictors by returning the predictor value next to
 * precision, recall and L2 similarity measures 
 * @author Tomer Sagi
 *
 */
public class MatrixPredictorEvaluation implements MatchingExperiment {
	private ArrayList<FirstLineMatcher> flM;

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		// Using all 1st line matchers 
		ArrayList<Statistic> predictions = new ArrayList<Statistic>();
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		for (FirstLineMatcher m : flM)
		{
			//Match
			MatchInformation mi = m.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			MatchInformation mi1 = SLMList.OBMaxDelta005.getSLM().match(mi);
			// Calculate predictors
			Statistic  p = new MatrixPredictors();
			String instanceDesc = esp.getSPID()+","+m.getName()+","+"MaxDelta005";
			p.init(instanceDesc, mi1);
			predictions.add(p);
			//Precision Recall
			K2Statistic b = new BinaryGolden();
			b.init(instanceDesc, mi1,esp.getExact());
			evaluations.add(b);
			//Additional 2LM
			MatchInformation mi2 = SLMList.OBMax.getSLM().match(mi);
			Statistic  p2 = new MatrixPredictors();
			instanceDesc = esp.getSPID()+","+m.getName()+","+"MaxDelta0";
			p2.init(instanceDesc, mi2);
			predictions.add(p2);
			//Precision Recall
			K2Statistic b2 = new BinaryGolden();
			b2.init(instanceDesc, mi2,esp.getExact());
			evaluations.add(b2);
			//Additional 2LM
			MatchInformation mi3 = SLMList.OBThreshold050.getSLM().match(mi);
			Statistic  p3 = new MatrixPredictors();
			instanceDesc = esp.getSPID()+","+m.getName()+","+"Threshold050";
			p3.init(instanceDesc, mi3);
			predictions.add(p3);
			//Precision Recall
			K2Statistic b3 = new BinaryGolden();
			b3.init(instanceDesc, mi3,esp.getExact());
			evaluations.add(b3);
			
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
		return "Matrix Predictor Evaluation";
	}
	
	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}

}

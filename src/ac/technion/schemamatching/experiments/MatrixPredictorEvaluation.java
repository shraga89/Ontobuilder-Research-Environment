package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import schemamatchings.ontobuilder.OntoBuilderWrapper;

import com.modica.ontology.match.MatchInformation;

/**
 * Evaluates matrix predictors by returning the predictor value next to
 * precision, recall and L2 similarity measures 
 * @author Tomer Sagi
 *
 */
public class MatrixPredictorEvaluation implements MatchingExperiment {

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(
			ExperimentSchemaPair esp) {
		// Match using 5 Ontobuilder 1st line matchers 
		MatchInformation sm[] = new MatchInformation[5];
		int[] smids = new int[] {0,1,4,5,6};
		for (int i=0;i<smids.length;i++)
			sm[i] = esp.getSimilarityMatrix(smids[i]);
		// Calculate predictors
		ArrayList<Statistic> predictions = new ArrayList<Statistic>();
		for (int i=0;i<sm.length;i++)
		{
			Statistic  p = new MatrixPredictors();
			p.init(esp.getSPID()+","+smids[i], sm[i]);
			predictions.add(p);
		}
		// TODO Match select using MWBG and Threshold 0.2
		// TODO Return precision, recall, L2 similarity and predictor values
		return predictions;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(Properties properties, ArrayList<OtherMatcher> om) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return "Matrix Predictor Evaluation";
	}

}

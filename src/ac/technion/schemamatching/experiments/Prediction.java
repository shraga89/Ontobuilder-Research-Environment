/**
 * 
 */
package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author Tomer Sagi
 *	This experiment creates ensembles using prediction values on matrices, terms and values.
 */
public class Prediction implements MatchingExperiment {

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(
			ExperimentSchemaPair esp) {
		// TODO Auto-generated method stub
		// Match using 4 1st line matchers using OB and AMC
		
		// Calculate matrix level, row level and line level predictors
		
		// Combine matrices using different predictors as weights
		
		// Use simple selection rule (threshold) to create binary similarity matrix
		
		// Calculate precision, recall, L2 Similarity
		return null;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<OtherMatcher> om) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return "Prediction";
	}

}

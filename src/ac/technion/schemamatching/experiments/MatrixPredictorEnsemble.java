package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;
import ac.technion.schemamatching.matchers.FirstLineMatcher;
import ac.technion.schemamatching.matchers.SecondLineMatcher;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * Uses values of matrix predictors to ensemble 1st line matchers and second line matchers
 * @author Tomer Sagi
 *
 */
public class MatrixPredictorEnsemble implements MatchingExperiment {
	
	private OBExperimentRunner oer;
	private ArrayList<FirstLineMatcher> flM;

	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) 
	{
		//TODO copy pasted from matrix predictor evaluation. Use predictors to create ensembles and match select them. 
		
		return null;
	}

	/**
	 * Takes a schema translator object and fill the supplied matchInformation from the objects in the schematranslator
	 * @param matchInformation
	 * @param st
	 */
	private void fillMI(MatchInformation matchInformation, SchemaTranslator st) {
		// TODO Auto-generated method stub
		
	}

	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		this.flM = flM;
		this.oer = oer;
		return true;
	}

	public String getDescription() {
		return "Ensemble using matrix predictors";
	}

}

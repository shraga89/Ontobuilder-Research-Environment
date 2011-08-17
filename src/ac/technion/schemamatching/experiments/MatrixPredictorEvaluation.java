package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.Properties;

import schemamatchings.util.BestMappingsWrapper;
import schemamatchings.util.SchemaTranslator;

import ac.technion.schemamatching.matchers.FirstLineMatcher;
import ac.technion.schemamatching.matchers.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BasicGolden;
import ac.technion.schemamatching.statistics.GoldenStatistic;
import ac.technion.schemamatching.statistics.L2similarityGolden;
import ac.technion.schemamatching.statistics.MatrixPredictors;
import ac.technion.schemamatching.statistics.Statistic;

import com.modica.ontology.match.Match;
import com.modica.ontology.match.MatchInformation;

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
		ArrayList<MatchInformation> sm = new ArrayList<MatchInformation>();
		ArrayList<Statistic> predictions = new ArrayList<Statistic>();
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		for (FirstLineMatcher m : flM)
		{
			//Match
			MatchInformation mi = esp.getSimilarityMatrix(m);
			sm.add(mi);
			// Calculate predictors
			Statistic  p = new MatrixPredictors();
			String instanceDesc = esp.getSPID()+","+m.getName()+","+m.getConfig();
			assert(p.init(instanceDesc, mi));
			//Calculate precision, recall
			GoldenStatistic  b = new BasicGolden();
			b.init(instanceDesc, mi,esp.getExact());
			evaluations.add(b);
			//L2 similarity
			L2similarityGolden l2 = new L2similarityGolden();
			l2.init(instanceDesc, mi, esp.getExact());
			evaluations.add(l2);
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

}

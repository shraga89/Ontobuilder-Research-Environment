package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.util.BestMappingsWrapper;
import schemamatchings.util.SchemaTranslator;

import ac.technion.schemamatching.statistics.BasicGolden;
import ac.technion.schemamatching.statistics.GoldenStatistic;
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

	private OBExperimentRunner oer;

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
		//Match select using MWBG and Threshold 0.2
		MatchInformation mwbg[] = new MatchInformation[5];
		ArrayList<Statistic> mwbgRes = new ArrayList<Statistic>();
		for (int i=0;i<mwbg.length;i++)
		{
			BestMappingsWrapper.matchMatrix = sm[i].getMatrix();	
			SchemaTranslator st = BestMappingsWrapper.GetBestMapping("MAX_WEIGHT_BIPARTITE_GRAPH");
			st.importIdsFromMatchInfo(sm[i],true);
			mwbg[i] = sm[i]; //TODO implement clone on MatchInformation
			mwbg[i].setMatches(st.getMatches());
			// TODO calculate precision, recall, L2 similarity and predictor values
			oer.getDoc().documentMapping(esp.getSPID(), mwbg[i]);
		}
		double th = 0.2;
		MatchInformation t[] = new MatchInformation[5];
		ArrayList<Statistic> tRes = new ArrayList<Statistic>();
		for (int i=0;i<t.length;i++)
		{
			t[i] = sm[i]; //TODO implement clone on MatchInformation
			ArrayList<Object> removeList = new ArrayList<Object>();
			for (Object om : t[i].getMatches())
			{
				Match m = (Match)om;
				if (m.getEffectiveness()<th)
					removeList.add(om);
			}
			t[i].getMatches().removeAll(removeList);
			oer.getDoc().documentMapping(esp.getSPID(), t[i]);
			//TODO calculate precision, recall, L2 similarity and predictor values
			GoldenStatistic gs = new BasicGolden();
			gs.init(t[i], esp.getExactMapping());
			tRes.add(gs);
		}
		predictions.addAll(mwbgRes);
		predictions.addAll(tRes);
		return predictions;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer,Properties properties, ArrayList<OtherMatcher> om) {
		// TODO Auto-generated method stub
		this.oer = oer;
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

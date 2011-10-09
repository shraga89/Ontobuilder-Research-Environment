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
 * Evaluates matrix predictors by returning the predictor value next to
 * precision, recall and L2 similarity measures 
 * @author Tomer Sagi
 *
 */
public class OldMatrixPredictorEvaluation implements MatchingExperiment {

	private OBExperimentRunner oer;

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Statistic> runExperiment(
			ExperimentSchemaPair esp) {
		/*// Match using 5 Ontobuilder 1st line matchers 
		MatchInformation sm[] = new MatchInformation[5];
		int[] smids = new int[] {0,1,4,5,6};
		for (int i=0;i<smids.length;i++)
			sm[i] = esp.getSimilarityMatrix(smids[i]);
		// Calculate predictors
		ArrayList<Statistic> predictions = new ArrayList<Statistic>();
		for (int i=0;i<sm.length;i++)
		{
			Statistic  p = new MatrixPredictors();
			assert(p.init(esp.getSPID()+","+smids[i], sm[i]));
			predictions.add(p);
		}
		//Match select using MWBG and Threshold 0.2
		MatchInformation mwbg[] = new MatchInformation[5];
		ArrayList<Statistic> mwbgRes = new ArrayList<Statistic>();
		for (int i=0;i<mwbg.length;i++)
		{
			BestMappingsWrapper.matchMatrix = sm[i].getMatrix();	
			SchemaTranslator st = BestMappingsWrapper.GetBestMapping("Max Weighted Bipartite Graph");
			assert (st!=null);
			st.importIdsFromMatchInfo(sm[i],true);
			mwbg[i] = sm[i].clone(); 
			TODO this isn't working. st returns ArrayList<MatchedAttributePair> 
			 * and mwbg[i] is expecting ArrayList<Matches>. Need to upgrade mwbg[i] to work with
			 * matched attribute pairs. 
			 
			mwbg[i].setMatches(st.getMatches());
			TODO use mwbg[i].addMatch(targetTerm, candidateTerm, effectiveness); 
			 * To write a method.   
			
			fillMI(mwbg[i],st);
			//Calculate precision, recall
			GoldenStatistic  b = new BasicGolden();
			String desc = Integer.toString(esp.getSPID()) + "," + Integer.toString(i) + "MWBG";
			b.init(desc, mwbg[i],esp.getExact());
			mwbgRes.add(b);
			//L2 similarity
			L2similarityGolden l2 = new L2similarityGolden();
			l2.init(desc, mwbg[i], esp.getExact());
			mwbgRes.add(l2);
			//Document Result in DB
			oer.getDoc().documentMapping(esp.getSPID(),smids[i],1,0, mwbg[i]);
		}
		double th = 0.2;
		MatchInformation t[] = new MatchInformation[5];
		ArrayList<Statistic> tRes = new ArrayList<Statistic>();
		for (int i=0;i<t.length;i++)
		{
			t[i] = sm[i].clone();
			ArrayList<Object> removeList = new ArrayList<Object>();
			for (Object om : t[i].getMatches())
			{
				Match m = (Match)om;
				if (m.getEffectiveness()<th)
					removeList.add(om);
			}
			t[i].getMatches().removeAll(removeList);
			oer.getDoc().documentMapping(esp.getSPID(),smids[i],0,0, t[i]);
			//Precision, recall
			GoldenStatistic gs = new BasicGolden();
			String desc = Integer.toString(esp.getSPID()) + "," + Integer.toString(i) + "Threshold:" + Double.toString(th);
			gs.init(desc, t[i], esp.getExact());
			tRes.add(gs);
			//L2 similarity
			L2similarityGolden l2 = new L2similarityGolden();
			l2.init(desc, t[i], esp.getExact());
			mwbgRes.add(l2);
			oer.getDoc().documentMapping(esp.getSPID(),smids[i],0,0, t[i]);
		}
		predictions.addAll(mwbgRes);
		predictions.addAll(tRes);
		return predictions;*/
		return null;
	}

	private void fillMI(MatchInformation matchInformation, SchemaTranslator st) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer,Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
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

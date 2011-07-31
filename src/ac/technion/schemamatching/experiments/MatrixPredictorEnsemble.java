package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.Properties;

import schemamatchings.util.BestMappingsWrapper;
import schemamatchings.util.SchemaTranslator;

import com.modica.ontology.match.Match;
import com.modica.ontology.match.MatchInformation;

import ac.technion.schemamatching.statistics.BasicGolden;
import ac.technion.schemamatching.statistics.GoldenStatistic;
import ac.technion.schemamatching.statistics.L2similarityGolden;
import ac.technion.schemamatching.statistics.MatrixPredictors;
import ac.technion.schemamatching.statistics.Statistic;

/**
 * Uses values of matrix predictors to ensemble 1st line matchers and second line matchers
 * @author Tomer Sagi
 *
 */
public class MatrixPredictorEnsemble implements MatchingExperiment {
	
	private OBExperimentRunner oer;

	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) 
	{
		//TODO copy pasted from matrix predictor evaluation. Use predictors to create ensembles and match select them. 
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
			assert(p.init(esp.getSPID()+","+smids[i], sm[i]));
			predictions.add(p);
		}
		//TODO ensemble using predictors and calculate (SM)precision and (SM)recall
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
			/*TODO this isn't working. st returns ArrayList<MatchedAttributePair> 
			 * and mwbg[i] is expecting ArrayList<Matches>. Need to upgrade mwbg[i] to work with
			 * matched attribute pairs. 
			 */
			mwbg[i].setMatches(st.getMatches());
			/*TODO use mwbg[i].addMatch(targetTerm, candidateTerm, effectiveness); 
			 * To write a method.   
			*/
			fillMI(mwbg[i],st);
			//TODO ensemble using predictors and calculate precision and recall
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
			//TODO ensemble using predictors and calculate precision and recall
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
		return predictions;
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
			ArrayList<OtherMatcher> om) {
		// TODO Implement Other Matchers
		this.oer = oer;
		return true;
	}

	public String getDescription() {
		return "Ensemble using matrix predictors";
	}

}

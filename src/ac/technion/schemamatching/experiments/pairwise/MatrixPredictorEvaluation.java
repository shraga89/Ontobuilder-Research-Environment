package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SLMList;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MCC;
import ac.technion.schemamatching.statistics.MatchCompetitorDeviation;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictors;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * Evaluates matrix predictors by returning the predictor value next to
 * precision, recall and L2 similarity measures 
 * @author Tomer Sagi
 *
 */
public class MatrixPredictorEvaluation implements PairWiseExperiment {
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
			//MatchInformation mi1 = SLMList.OBMaxDelta005.getSLM().match(mi);
			MatchInformation mi1 = SLMList.OBMWBG.getSLM().match(mi);
			// Calculate predictors + Mcd- Hagai's new Predictor
			Statistic  p = new MatrixPredictors();
			K2Statistic mcd = new MatchCompetitorDeviation();
			//String instanceDesc = esp.getID()+","+m.getName()+","+"MaxDelta005";
			String instanceDesc = esp.getID()+","+m.getName()+","+"OBMWBG";
			p.init(instanceDesc, mi1);
			mcd.init(instanceDesc, mi, mi1);
			predictions.add(p);
			predictions.add(mcd);
			//Precision Recall
			K2Statistic b = new BinaryGolden();
			b.init(instanceDesc, mi1,esp.getExact());
			evaluations.add(b);
			//Mcc
			K2Statistic mcc = new MCC();
			mcc.init(instanceDesc, mi1, esp.getExact());
			evaluations.add(mcc);
			//Additional 2LM
			/*
			MatchInformation mi2 = SLMList.OBSM.getSLM().match(mi);
			Statistic  p2 = new MatrixPredictors();
			K2Statistic mcd2 = new MatchCompetitorDeviation();
			//instanceDesc = esp.getID()+","+m.getName()+","+"MaxDelta0";
			instanceDesc = esp.getID()+","+m.getName()+","+"Stable Marriage";
			p2.init(instanceDesc, mi2);
			mcd2.init(instanceDesc, mi, mi2);
			predictions.add(p2);
			predictions.add(mcd2);
			//Precision Recall
			K2Statistic b2 = new BinaryGolden();
			b2.init(instanceDesc, mi2,esp.getExact());
			evaluations.add(b2);
			//Mcc
			K2Statistic mcc2 = new MCC();
			mcc2.init(instanceDesc, mi2, esp.getExact());
			evaluations.add(mcc2);
			
			//Additional 2LM
			MatchInformation mi3 = SLMList.OBMaxDelta01.getSLM().match(mi);
			Statistic  p3 = new MatrixPredictors();
			K2Statistic mcd3 = new MatchCompetitorDeviation();
			instanceDesc = esp.getID()+","+m.getName()+","+"OBMaxDelta01";
			p3.init(instanceDesc, mi3);
			mcd3.init(instanceDesc, mi, mi3);
			predictions.add(p3);
			predictions.add(mcd3);
			//Precision Recall
			K2Statistic b3 = new BinaryGolden();
			b3.init(instanceDesc, mi3,esp.getExact());
			evaluations.add(b3);
			//Mcc
			K2Statistic mcc3 = new MCC();
			mcc3.init(instanceDesc, mi3, esp.getExact());
			evaluations.add(mcc3);
			
			//Additional 2LM
			MatchInformation mi4 = SLMList.OBThreshold050.getSLM().match(mi);
			Statistic  p4 = new MatrixPredictors();
			K2Statistic mcd4 = new MatchCompetitorDeviation();
			instanceDesc = esp.getID()+","+m.getName()+","+"Threshold050";
			p4.init(instanceDesc, mi4);
			mcd4.init(instanceDesc, mi, mi4);
			predictions.add(p4);
			predictions.add(mcd4);
			//Precision Recall
			K2Statistic b4 = new BinaryGolden();
			b4.init(instanceDesc, mi4,esp.getExact());
			evaluations.add(b4);
			//Mcc
			K2Statistic mcc4 = new MCC();
			mcc4.init(instanceDesc, mi4, esp.getExact());
			evaluations.add(mcc4);
			
			/*
			//Hagai's new SLM
			SecondLineMatcher obce = new OBCrossEntropy();
			MatchInformation mi4 = obce.match(mi);
			Statistic  p4 = new MatrixPredictors();
			K2Statistic mcd4 = new MatchCompetitorDeviation();
			instanceDesc = esp.getID()+","+m.getName()+","+"OBCrossEntropy";
			p4.init(instanceDesc, mi4);
			mcd4.init(instanceDesc, mi, mi4);
			predictions.add(p4);
			predictions.add(mcd4);
			//Precision Recall
			K2Statistic b4 = new BinaryGolden();
			b4.init(instanceDesc, mi4,esp.getExact());
			evaluations.add(b4);
			//Mcc
			K2Statistic mcc4 = new MCC();
			mcc4.init(instanceDesc, mi4, esp.getExact());
			evaluations.add(mcc4);
			*/
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

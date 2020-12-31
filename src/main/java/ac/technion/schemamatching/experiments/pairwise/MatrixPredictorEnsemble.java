package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.ensembles.Ensemble;
import ac.technion.schemamatching.ensembles.SimpleWeightedEnsemble;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SLMList;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictors;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * Uses values of matrix predictors to ensemble 1st and second line matcher configs 
 * Assumes it recieves a properties file with predictor weights in the following format: 
 * PredictorName = 0.2
 * Note that predictor name should match the result of the getName() method in the corresponding 
 * @link{Predictor} class. 
 * @author Tomer Sagi
 *
 */
public class MatrixPredictorEnsemble implements PairWiseExperiment {
	
	private HashMap<String,Double> predictorWeights = new HashMap<String,Double>();
	private ArrayList<FirstLineMatcher> flM;

	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) 
	{
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		HashMap<String,MatchInformation> flMatches = new HashMap<String,MatchInformation>(); 
		//Match using all 1LMs
		for (FirstLineMatcher f : flM)
			flMatches.put(f.getName(),f.match(esp.getCandidateOntology(), esp.getTargetOntology(), false));
		
		//Match Select using 3 2LMs and calculate Precision and Recall
		HashMap<String,MatchInformation> SecondLineMatches = new HashMap<String,MatchInformation>();
		for (String flmName : flMatches.keySet())
		{
			MatchInformation tmpMI = flMatches.get(flmName);
			MatchInformation mi1 = SLMList.OBThreshold015.getSLM().match(tmpMI);
			SecondLineMatches.put("T015," + flmName, mi1);
			MatchInformation mi2 = SLMList.OBThreshold025.getSLM().match(tmpMI);
			SecondLineMatches.put("T025," + flmName, mi2);
			//MatchInformation mi3 = SLMList.OBSM.getSLM().match(tmpMI);
			//SecondLineMatches.put("SM," + flmName, mi3);
		}
		
		for (String config : SecondLineMatches.keySet())
		{
			K2Statistic b = new BinaryGolden();
			b.init(esp.getID() + "," + config,SecondLineMatches.get(config) ,esp.getExact());
			res.add(b);
		}
				
		//Generate predictor values for 1LMs and use for matcher weights
		HashMap<String, Double> matcherWeights = new HashMap<String, Double>();
		for (String mName : SecondLineMatches.keySet())
		{
			MatrixPredictors mv = new MatrixPredictors();
			if (SecondLineMatches.get(mName)  == null) continue;
			mv.init(esp.getID() + "," + mName, SecondLineMatches.get(mName));
			String h[] = mv.getHeader();
			int numPredictors = h.length -1;
			Double weightedSumOfPrediction = new Double(0.0);
			for (int i=0;i<numPredictors;i++)
			{
				Double p = Double.parseDouble(mv.getData().get(0)[i]);
				Double w = (predictorWeights.containsKey(h[i])?predictorWeights.get(h[i]):0.0);
				weightedSumOfPrediction+=(p*w);
				
			}
			res.add(mv);
			if (weightedSumOfPrediction>0) 
				matcherWeights.put(mName, weightedSumOfPrediction);
		}
		
		//Create ensemble
		Ensemble e = new SimpleWeightedEnsemble();
		e.init(SecondLineMatches, matcherWeights);
		MatchInformation weightedMI = e.getWeightedMatch();
		
		//Calcualte Golden on ensemble results
		K2Statistic b = new BinaryGolden();
		b.init(esp.getID() + ",weighted",weightedMI ,esp.getExact());
		res.add(b);
		
		return res;
	}


	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		this.flM = flM;
		for (Object key : properties.keySet())
		{
			String pName = (String)key;
			Double pWeight = Double.parseDouble((String)properties.get(key));
			predictorWeights.put(pName, pWeight);
		}
		return true;
	}

	public String getDescription() {
		return "Ensemble using matrix predictors";
	}
	
	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}

}

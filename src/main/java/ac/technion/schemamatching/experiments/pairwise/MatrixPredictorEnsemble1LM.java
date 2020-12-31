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
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictors;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * Uses values of matrix predictors to ensemble 1st line matchers and second line matchers. 
 * Assumes it recieves a properties file with predictor weights in the following format: 
 * PredictorName = 0.2
 * Note that predictor name should match the result of the getName() method in the corresponding 
 * @link{Predictor} class. 
 * @author Tomer Sagi
 *
 */
public class MatrixPredictorEnsemble1LM implements PairWiseExperiment {
	
	private HashMap<String,Double> predictorWeights = new HashMap<String,Double>();
	private ArrayList<FirstLineMatcher> flM;

	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) 
	{
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		HashMap<String,MatchInformation> flMatches = new HashMap<String,MatchInformation>(); 
		//Match using all 1LMs
		for (FirstLineMatcher f : flM)
			flMatches.put(f.getName(),f.match(esp.getCandidateOntology(), esp.getTargetOntology(), false));
		
		//Calculate unEnsembled results
		for (String f : flMatches.keySet())
		{
			K2Statistic singleNB = new NBGolden();
			MatchInformation mi = flMatches.get(f);
			String id = esp.getID()+ "," + f; 
			singleNB.init(id, mi, esp.getExact());
			res.add(singleNB);
		}
		
		//Generate predictor values for 1LMs and use for matcher weights
		HashMap<String, Double> matcherWeights = new HashMap<String, Double>();
		for (String mName : flMatches.keySet())
		{
			MatrixPredictors mv = new MatrixPredictors();
			if (flMatches.get(mName)  == null) continue;
			mv.init(esp.getID() + "," + mName, flMatches.get(mName));
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
		e.init(flMatches, matcherWeights);
		MatchInformation weightedMI = e.getWeightedMatch();
		
		//Calculate NB Precision and Recall
		K2Statistic nb = new NBGolden();
		String id = esp.getID()+",weighted"; 
		nb.init(id, weightedMI, esp.getExact());
		res.add(nb);
		
		//Match Select and calculate Precision and Recall 
		//MatchInformation matchSelected = SLMList.OBThreshold025.getSLM().match(weightedMI);
		MatchInformation matchSelected = SLMList.OBSM.getSLM().match(weightedMI);
		K2Statistic b = new BinaryGolden();
		b.init(id, matchSelected,esp.getExact());
		res.add(b);
				
		
		//Calculate unEnsembled results
		for (String f : flMatches.keySet())
		{
			K2Statistic singleB = new BinaryGolden();
			MatchInformation mi = flMatches.get(f);
			matchSelected = SLMList.OBSM.getSLM().match(mi);
			id = esp.getID()+ "," + f; 
			singleB.init(id, matchSelected, esp.getExact());
			res.add(singleB);
		}
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

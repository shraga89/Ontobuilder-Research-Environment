package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.ensembles.Ensemble;
import ac.technion.schemamatching.ensembles.SimpleWeightedEnsemble;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FLMList;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SLMList;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * Uses values supplied to ensemble 1st line matchers 
 * Assumes it receives a properties file with matcher weights in the following format: 
 * w0 = 0.2 where 0 is the matcher id for Term Matcher in the similaritymeasures table of the schemamatching db 
 * @author Tomer Sagi
 *
 */
public class StaticEnsemble implements PairWiseExperiment {
	
	private HashMap<String,Double> matcherWeights = new HashMap<String,Double>();
	private ArrayList<FirstLineMatcher> flM;

	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) 
	{
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		HashMap<String,MatchInformation> flMatches = new HashMap<String,MatchInformation>(); 
		//List all 1LMs with over 0 weight in file
		ArrayList<FirstLineMatcher> tmp = new ArrayList<FirstLineMatcher>();
		for (FirstLineMatcher f : flM)
		{
			String mName = f.getName();
			if (matcherWeights.containsKey(mName) && matcherWeights.get(mName)>0)
				tmp.add(f);
		}
		
		//Match
		for (FirstLineMatcher f : tmp)
			flMatches.put(f.getName(),f.match(esp.getCandidateOntology(), esp.getTargetOntology(), false));
		
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
				
		return res;
	}


	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		this.flM = flM;
		HashMap<Integer, FirstLineMatcher> flmHash = FLMList.getIdFLMHash();
		for (Object key : properties.keySet())
		{
			String strKey =(String)key; 
			Integer mId = Integer.parseInt(strKey.substring(1));
			
			Double pWeight = Double.parseDouble((String)properties.get(key));
			matcherWeights.put(flmHash.get(mId).getName(), pWeight);
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

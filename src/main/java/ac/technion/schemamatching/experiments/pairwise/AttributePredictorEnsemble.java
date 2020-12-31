package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.ensembles.PAttributeEnsemble;
import ac.technion.schemamatching.ensembles.Ensemble;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SLMList;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MatchDistance;
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * Uses values of attribute predictors to ensemble 1st line matchers and second line matchers. 
 * Assumes it recieves a properties file with predictor weights in the following format: 
 * PredictorName = 0.2
 * Note that predictor name should match the result of the getName() method in the corresponding 
 * @link{Predictor} class. 
 * @author Tomer Sagi
 *
 */
public class AttributePredictorEnsemble implements PairWiseExperiment {
	
	private HashMap<String,Double> predictorWeights = new HashMap<String,Double>();
	private ArrayList<FirstLineMatcher> flM;

	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) 
	{
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		HashMap<String,MatchInformation> flMatches = new HashMap<String,MatchInformation>(); 
		//Match using all 1LMs
		for (FirstLineMatcher f : flM)
			flMatches.put(f.getName(),f.match(esp.getCandidateOntology(), esp.getTargetOntology(), false));
		
		//Create ensemble
		Ensemble e = new PAttributeEnsemble();
		e.init(flMatches, predictorWeights);
		MatchInformation weightedMI = e.getWeightedMatch();
		
		//Calculate NB Precision and Recall
		K2Statistic nb = new NBGolden();
		String id = esp.getID()+",weighted"; 
		nb.init(id, weightedMI, esp.getExact());
		res.add(nb);
		
		//Calculate MD
		K2Statistic md = new MatchDistance();
		md.init(id, weightedMI, esp.getExact());
		res.add(md);
		
		//Match Select and calculate Precision and Recall 
		//MatchInformation matchSelected = SLMList.OBThreshold025.getSLM().match(weightedMI);
		//MatchInformation matchSelected = SLMList.OBSM.getSLM().match(weightedMI);
		MatchInformation matchSelected = SLMList.OBMWBG.getSLM().match(weightedMI);
		K2Statistic b = new BinaryGolden();
		b.init(id, matchSelected,esp.getExact());
		res.add(b);
		//Calculate MD
		K2Statistic md2 = new MatchDistance();
		md2.init(id+"+Filtered", matchSelected, esp.getExact());
		res.add(md2);
				
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
		return "Ensemble using attribute predictors";
	}


	public ArrayList<Statistic> summaryStatistics() {
		//Unused in this experiment
		return null;
	}

}

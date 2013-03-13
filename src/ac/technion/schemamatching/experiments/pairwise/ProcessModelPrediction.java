package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FLMList;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.ProcessModelFLM;
import ac.technion.schemamatching.matchers.firstline.ProcessModelFLM.ProcessModelMatchStrategy;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

public class ProcessModelPrediction implements PairWiseExperiment {

	@Override
	public List<Statistic> runExperiment(ExperimentSchemaPair esp) {
		
		HashMap<String,MatchInformation> flMatches = new HashMap<String,MatchInformation>(); 
		ProcessModelFLM flm = new ProcessModelFLM();
		
		for (ProcessModelMatchStrategy strategy : ProcessModelMatchStrategy.values()) {
			System.out.println("Do: " + strategy);
			flm.setMatchingStrategy(strategy);
			flMatches.put(strategy.toString(),flm.match(esp.getCandidateOntology(), esp.getTargetOntology(), false));
			System.out.println("Done.");
		}
		
		return null;
	}

	@Override
	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		
		return false;
	}

	@Override
	public String getDescription() {
		return "Experiment on predicting the performance of various process model matchers";
	}

	@Override
	public List<Statistic> summaryStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

}

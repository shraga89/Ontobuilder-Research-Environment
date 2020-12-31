package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import matching.virtualdoc.VirtualDocParams;
import nl.tue.tm.is.labelAnalyzer.interfaces.SemanticLanguage;
import nl.tue.tm.is.labelAnalyzer.interfaces.SemanticLanguage.LanguageCode;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.ensembles.Ensemble;
import ac.technion.schemamatching.ensembles.SimpleWeightedEnsemble;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.ProcessModelCompleteMatchers;
import ac.technion.schemamatching.matchers.firstline.ProcessModelFLM;
import ac.technion.schemamatching.matchers.firstline.ProcessModelCompleteMatchers.ProcessModelMatcher;
import ac.technion.schemamatching.matchers.firstline.ProcessModelFLM.ProcessModelMatchStrategy;
import ac.technion.schemamatching.matchers.secondline.OBDominants;
import ac.technion.schemamatching.matchers.secondline.OBMaxDelta;
import ac.technion.schemamatching.matchers.secondline.OBThreshold;
import ac.technion.schemamatching.matchers.secondline.ProcessModelConfigMatchers;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.matchers.secondline.ProcessModelConfigMatchers.ProcessModelConfigMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.DummyStatistic;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictors;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

public class ProcessModelEvaluation implements PairWiseExperiment {

	
	public Set<Integer> dutchSPIDs;
	
	public Map<String, Double> predModel12Pre = new HashMap<>();
	public Map<String, Double> predModel13Pre = new HashMap<>();
	public Map<String, Double> predModel23Pre = new HashMap<>();
	public Map<String, Double> predModel12Rec = new HashMap<>();
	public Map<String, Double> predModel13Rec = new HashMap<>();
	public Map<String, Double> predModel23Rec = new HashMap<>();

	public Set<Integer> sample1IDs = new HashSet<>();
	public Set<Integer> sample2IDs = new HashSet<>();
	public Set<Integer> sample3IDs = new HashSet<>();

	@Override
	public List<Statistic> runExperiment(ExperimentSchemaPair esp) {
		
		List<Statistic> results = new ArrayList<>();
		
		/*
		 * Language configuration
		 */
		LanguageCode languageCode = LanguageCode.EN;
		if (this.dutchSPIDs.contains(esp.getID()))
			languageCode = LanguageCode.NL;

		SemanticLanguage.setLanguage(languageCode);
		
		String languageString;
		switch (languageCode){
		case NL:
			languageString = "dutch";
			VirtualDocParams.useStopwords = false;
			break;
		default:
			languageString = "english";
			VirtualDocParams.useStopwords = true;
			break;
		}

		VirtualDocParams.stemmingLanguage = languageString;
		VirtualDocParams.useStemming = true;
		
		/*
		 * Choose prediction models
		 */
		Map<String, Double> modelPre = null;
		Map<String, Double> modelRec = null;
		if (this.sample1IDs.contains(esp.getID())) {
			modelPre = this.predModel23Pre;
			modelRec = this.predModel23Rec;
		}
		else if (this.sample2IDs.contains(esp.getID())) {
			modelPre = this.predModel13Pre;
			modelRec = this.predModel13Rec;
		}
		else if (this.sample3IDs.contains(esp.getID())) {
			modelPre = this.predModel12Pre;
			modelRec = this.predModel12Rec;
		}
				
		/*
		 * 1. Match using all sim measures
		 */
		HashMap<String,MatchInformation> flMatches = new HashMap<String,MatchInformation>(); 
		ProcessModelFLM flm = new ProcessModelFLM();
		for (ProcessModelMatchStrategy strategy : ProcessModelMatchStrategy.values()) {
			System.out.println("FLM: " + strategy.toString());
			flm.setMatchingStrategy(strategy, languageCode);
			MatchInformation miFirst = flm.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			flMatches.put(flm.getName(),miFirst);
		}

		/*
		 * 2. Create Ensembles: generate predictor values for FLMs and use for matcher weights
		 */
		HashMap<String, Double> matcherWeightsPre = new HashMap<String, Double>();
		HashMap<String, Double> matcherWeightsRec = new HashMap<String, Double>();
		for (String mName : flMatches.keySet()) {
			MatrixPredictors mv = new MatrixPredictors();
			mv.init(esp.getID() + "," + mName, flMatches.get(mName));
			String h[] = mv.getHeader();
			int numPredictors = h.length -1;
			Double weightedSumOfPredictionPre = 0.0;
			Double weightedSumOfPredictionRec = 0.0;
			for (int i = 0; i < numPredictors; i++) {
				Double p = Double.parseDouble(mv.getData().get(0)[i]);
				Double wPre = (modelPre.containsKey(h[i])?modelPre.get(h[i]):0.0);
				Double wRec = (modelRec.containsKey(h[i])?modelRec.get(h[i]):0.0);
				weightedSumOfPredictionPre += (p * wPre);
				weightedSumOfPredictionRec += (p * wRec);
			}
			results.add(mv);
			if (weightedSumOfPredictionPre > 0) 
				matcherWeightsPre.put(mName, weightedSumOfPredictionPre);
			if (weightedSumOfPredictionRec > 0) 
				matcherWeightsRec.put(mName, weightedSumOfPredictionRec);
		}
		DummyStatistic stat = new DummyStatistic();
		stat.setName("ProcessModelFLMWeights");
		stat.setHeader(new String[]{"ID","FLM","Precision Weight", "Recall Weight"});
		List<String[]> statData= new ArrayList<>();
		for (String flmName : matcherWeightsPre.keySet()) 
			statData.add(new String[]{String.valueOf(esp.getID()), flmName, 
					String.valueOf(matcherWeightsPre.get(flmName)), String.valueOf(matcherWeightsRec.get(flmName))});
	
		stat.setData(statData);
		results.add(stat);
		
		Ensemble ePre = new SimpleWeightedEnsemble();
		ePre.init(flMatches, matcherWeightsPre);
		MatchInformation weightedMIPre = ePre.getWeightedMatch();
		
		Ensemble eRec = new SimpleWeightedEnsemble();
		eRec.init(flMatches, matcherWeightsRec);
		MatchInformation weightedMIRec = eRec.getWeightedMatch();

		MatchInformation maxMIPre = null;
		MatchInformation maxMIRec = null;
		double maxWeight = 0.0;
		for (Double weight : matcherWeightsPre.values()) 
			maxWeight = Math.max(maxWeight, r(weight));

		for (String flmName : matcherWeightsPre.keySet()) 
			if (r(matcherWeightsPre.get(flmName)) == maxWeight)
				maxMIPre = flMatches.get(flmName);
		
		maxWeight = 0.0;
		for (Double weight : matcherWeightsRec.values()) 
			maxWeight = Math.max(maxWeight, r(weight));

		for (String flmName : matcherWeightsRec.keySet()) 
			if (r(matcherWeightsRec.get(flmName)) == maxWeight)
				maxMIRec = flMatches.get(flmName);

		/*
		 * 3. Do 2LM with config matchers and ontobuilder
		 */
		// Plain result
		ProcessModelCompleteMatchers completeMatcher = new ProcessModelCompleteMatchers();
		for(ProcessModelMatcher matcher : ProcessModelMatcher.values()) {
			if (this.dutchSPIDs.contains(esp.getID()) && matcher.equals(ProcessModelMatcher.BPMatcher))
				continue;
			System.out.println("Complete Matcher: " + matcher.toString());
			
			completeMatcher.setMatchingStrategy(matcher);
			MatchInformation mi = completeMatcher.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			K2Statistic binary = new BinaryGolden();
			String instanceDesc = esp.getID()+","+ completeMatcher.getName();
			binary.init(instanceDesc, mi, esp.getExact());
			results.add(binary);
		}
		
		ProcessModelConfigMatchers configCompleteMatcher = new ProcessModelConfigMatchers();
		for(ProcessModelConfigMatcher matcher : ProcessModelConfigMatcher.values()) {
			if (this.dutchSPIDs.contains(esp.getID()) && matcher.equals(ProcessModelConfigMatcher.BPConfigMatcher))
				continue;
			System.out.println("Complete Config Matcher: " + matcher.toString());
			
			configCompleteMatcher.setMatchingStrategy(matcher);
			MatchInformation mi = configCompleteMatcher.match(weightedMIPre);
			K2Statistic binary = new BinaryGolden();
			String instanceDesc = esp.getID()+","+configCompleteMatcher.getName()+","+"Pre";
			binary.init(instanceDesc, mi, esp.getExact());
			results.add(binary);

			mi = configCompleteMatcher.match(weightedMIRec);
			binary = new BinaryGolden();
			instanceDesc = esp.getID()+","+configCompleteMatcher.getName()+","+"Rec";
			binary.init(instanceDesc, mi, esp.getExact());
			results.add(binary);
			
			mi = configCompleteMatcher.match(maxMIPre);
			binary = new BinaryGolden();
			instanceDesc = esp.getID()+","+configCompleteMatcher.getName()+","+"PreMax";
			binary.init(instanceDesc, mi, esp.getExact());
			results.add(binary);

			mi = configCompleteMatcher.match(maxMIRec);
			binary = new BinaryGolden();
			instanceDesc = esp.getID()+","+configCompleteMatcher.getName()+","+"RecMax";
			binary.init(instanceDesc, mi, esp.getExact());
			results.add(binary);
		}

		System.out.println("OBDominants");

		SecondLineMatcher slm = new OBDominants();
		MatchInformation mi = slm.match(weightedMIPre);
		K2Statistic binary = new BinaryGolden();
		String instanceDesc = esp.getID()+","+slm.getName()+","+"Pre";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);
		mi = slm.match(weightedMIRec);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"Rec";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);
		mi = slm.match(maxMIPre);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"PreMax";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);
		mi = slm.match(maxMIRec);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"RecMax";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);

		System.out.println("OBMaxDelta");

		slm = new OBMaxDelta(0.05);
		mi = slm.match(weightedMIPre);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"Pre";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);
		mi = slm.match(weightedMIRec);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"Rec";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);
		mi = slm.match(maxMIPre);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"PreMax";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);
		mi = slm.match(maxMIRec);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"RecMax";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);

		System.out.println("OBThreshold");

		slm = new OBThreshold(0.5);
		mi = slm.match(weightedMIPre);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"Pre";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);
		mi = slm.match(weightedMIRec);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"Rec";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);
		mi = slm.match(maxMIPre);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"PreMax";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);
		mi = slm.match(maxMIRec);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"RecMax";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);

		slm = new OBThreshold(0.75);
		mi = slm.match(weightedMIPre);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"Pre";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);
		mi = slm.match(weightedMIRec);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"Rec";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);
		mi = slm.match(maxMIPre);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"PreMax";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);
		mi = slm.match(maxMIRec);
		binary = new BinaryGolden();
		instanceDesc = esp.getID()+","+slm.getName()+","+"RecMax";
		binary.init(instanceDesc, mi, esp.getExact());
		results.add(binary);

		return results;
	}

	@Override
	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
				
		dutchSPIDs = new HashSet<>();
		dutchSPIDs.add(2839);
		dutchSPIDs.add(2840);
		dutchSPIDs.add(2841);
		dutchSPIDs.add(2842);
		dutchSPIDs.add(2843);
		dutchSPIDs.add(2844);
		dutchSPIDs.add(2845);
		dutchSPIDs.add(2846);
		dutchSPIDs.add(2847);
		dutchSPIDs.add(2848);
		dutchSPIDs.add(2849);
		dutchSPIDs.add(2850);
		dutchSPIDs.add(2851);
		dutchSPIDs.add(2852);
		dutchSPIDs.add(2853);
		dutchSPIDs.add(2854);
		dutchSPIDs.add(2855);
		
		
		predModel12Pre.put("Dominants",0.344633715);
		predModel12Pre.put("MaxPredictor",0.125182924);
		predModel12Pre.put("BMPredictor",0.120602676);
		predModel12Pre.put("LMMPredictor",0.221132767);
		predModel12Pre.put("BMMPredictor",0.188447918);
		
		predModel13Pre.put("Dominants",0.304676213);
		predModel13Pre.put("MaxPredictor",0.101404174);
		predModel13Pre.put("BMPredictor",0.152350473);
		predModel13Pre.put("LMMPredictor",0.259275928);
		predModel13Pre.put("BMMPredictor",0.182293212);

		predModel23Pre.put("Dominants",0.290146397);
		predModel23Pre.put("MaxPredictor",0.088125045);
		predModel23Pre.put("BMPredictor",0.183160869);
		predModel23Pre.put("LMMPredictor",0.270492383);
		predModel23Pre.put("BMMPredictor",0.168075307);
		predModel23Pre.put("STDEVPredictor",0.051794113);

		predModel12Rec.put("MaxPredictor",0.148756945);
		predModel12Rec.put("LMMPredictor",0.283102701);
		predModel12Rec.put("BMMPredictor",0.143087853);
		predModel12Rec.put("AvgConfPredictor",0.182052158);
		predModel12Rec.put("Dominants",0.17791912);
		predModel12Rec.put("BMPredictor",0.065081224);

		predModel13Rec.put("MaxPredictor",0.161409957);
		predModel13Rec.put("LMMPredictor",0.294731017);
		predModel13Rec.put("Dominants",0.163397151);
		predModel13Rec.put("AvgConfPredictor",0.167286153);
		predModel13Rec.put("BMMPredictor",0.157562314);
		predModel13Rec.put("BMPredictor",0.055613408);

		predModel23Rec.put("MaxPredictor",0.199318547);
		predModel23Rec.put("LMMPredictor",0.271746707);
		predModel23Rec.put("Dominants",0.156725837);
		predModel23Rec.put("AvgConfPredictor",0.169349192);
		predModel23Rec.put("BMMPredictor",0.202859716);

		sample1IDs.add(2839);
		sample1IDs.add(2842);
		sample1IDs.add(2845);
		sample1IDs.add(2848);
		sample1IDs.add(2851);
		sample1IDs.add(2854);
		sample1IDs.add(2856);
		sample1IDs.add(2859);
		sample1IDs.add(2862);
		sample1IDs.add(2865);
		sample1IDs.add(2868);
		sample1IDs.add(2871);
		sample1IDs.add(2874);
		sample1IDs.add(2877);
		sample1IDs.add(2880);
		sample1IDs.add(2883);
		sample1IDs.add(2886);
		sample1IDs.add(2889);
		sample1IDs.add(2803);
		sample1IDs.add(2806);
		sample1IDs.add(2809);
		sample1IDs.add(2812);
		sample1IDs.add(2815);
		sample1IDs.add(2818);
		sample1IDs.add(2821);
		sample1IDs.add(2824);
		sample1IDs.add(2827);
		sample1IDs.add(2830);
		sample1IDs.add(2833);
		sample1IDs.add(2836);
		
		sample2IDs.add(2840);
		sample2IDs.add(2843);
		sample2IDs.add(2846);
		sample2IDs.add(2849);
		sample2IDs.add(2852);
		sample2IDs.add(2855);
		sample2IDs.add(2804);
		sample2IDs.add(2807);
		sample2IDs.add(2810);
		sample2IDs.add(2813);
		sample2IDs.add(2816);
		sample2IDs.add(2819);
		sample2IDs.add(2822);
		sample2IDs.add(2825);
		sample2IDs.add(2828);
		sample2IDs.add(2831);
		sample2IDs.add(2834);
		sample2IDs.add(2837);
		sample2IDs.add(2857);
		sample2IDs.add(2860);
		sample2IDs.add(2863);
		sample2IDs.add(2866);
		sample2IDs.add(2869);
		sample2IDs.add(2872);
		sample2IDs.add(2875);
		sample2IDs.add(2878);
		sample2IDs.add(2881);
		sample2IDs.add(2884);
		sample2IDs.add(2887);
		sample2IDs.add(2890);
		
		sample3IDs.add(2841);
		sample3IDs.add(2844);
		sample3IDs.add(2847);
		sample3IDs.add(2850);
		sample3IDs.add(2853);
		sample3IDs.add(2805);
		sample3IDs.add(2808);
		sample3IDs.add(2811);
		sample3IDs.add(2814);
		sample3IDs.add(2817);
		sample3IDs.add(2820);
		sample3IDs.add(2823);
		sample3IDs.add(2826);
		sample3IDs.add(2829);
		sample3IDs.add(2832);
		sample3IDs.add(2835);
		sample3IDs.add(2838);
		sample3IDs.add(2858);
		sample3IDs.add(2861);
		sample3IDs.add(2864);
		sample3IDs.add(2867);
		sample3IDs.add(2870);
		sample3IDs.add(2873);
		sample3IDs.add(2876);
		sample3IDs.add(2879);
		sample3IDs.add(2882);
		sample3IDs.add(2885);
		sample3IDs.add(2888);
		sample3IDs.add(2891);
		
		return true;
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

	public double r(double v){
		return Math.round(v*1000.0)/1000.0; 
	}
}

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
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.ProcessModelFLM;
import ac.technion.schemamatching.matchers.firstline.ProcessModelFLM.ProcessModelMatchStrategy;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.DummyStatistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictors;
import ac.technion.schemamatching.statistics.predictors.ProcessModelPropertyPredictor;
import ac.technion.schemamatching.statistics.predictors.ProcessModelPropertyPredictor.ProcessModelProperty;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

public class ProcessModelSeparationEvaluation implements PairWiseExperiment {
	
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
		 * Get all properties
		 */
		Map<String, Double> properties = new HashMap<>();
		ProcessModelPropertyPredictor predictor = new ProcessModelPropertyPredictor(esp.getCandidateOntology(), esp.getTargetOntology(),languageCode);
		for (ProcessModelProperty property : ProcessModelProperty.values()) {
			if (this.dutchSPIDs.contains(esp.getID()) && 
					(property.toString().toLowerCase().contains("exclusiveness") || 
							property.toString().toLowerCase().contains("strictorder") || 
							property.toString().toLowerCase().contains("concurrency")))
				continue;

			predictor.setProperty(property);
			properties.put(predictor.getName(), predictor.getRes());
		}

		// the predicted values
		double predictionPre = 0.0;
		double predictionRec = 0.0;

		// init with property predictors
		for (String key : modelPre.keySet())
			if (properties.containsKey(key))
				predictionPre += modelPre.get(key) * properties.get(key);
		
		for (String key : modelRec.keySet())
			if (properties.containsKey(key))
				predictionRec += modelRec.get(key) * properties.get(key);


		Set<ProcessModelMatchStrategy> flms = new HashSet<>();
		flms.add(ProcessModelMatchStrategy.OptSEDOverActivityLabels);
		flms.add(ProcessModelMatchStrategy.LinOverActivities);
		flms.add(ProcessModelMatchStrategy.LinOverActionsOfActivities);
		flms.add(ProcessModelMatchStrategy.LinOverActivitiesWithObjects);
		flms.add(ProcessModelMatchStrategy.LinOverObjectsOfActivities);
		flms.add(ProcessModelMatchStrategy.VirtualDocs);
		flms.add(ProcessModelMatchStrategy.VirtualDocsDistanceSet);
		flms.add(ProcessModelMatchStrategy.VirtualDocsTreeSet);
		flms.add(ProcessModelMatchStrategy.ActivitiesHaveCommonSemanticComponents);
		flms.add(ProcessModelMatchStrategy.ActivitiesHaveCommonSemanticComponents);

		DummyStatistic stat = new DummyStatistic();
		stat.setName("ProcessModelSeparation");
		stat.setHeader(new String[]{"ID","FLM","Prediction Pre","Prediction Rec"});
		List<String[]> statData= new ArrayList<>();

		ProcessModelFLM flm = new ProcessModelFLM();
		for (ProcessModelMatchStrategy strategy : flms) {
			System.out.println("FLM: " + strategy.toString());
			flm.setMatchingStrategy(strategy, languageCode);
			MatchInformation miFirst = flm.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);

			double currentPredictionPre = predictionPre;
			double currentPredictionRec = predictionRec;

			MatrixPredictors mv = new MatrixPredictors();
			mv.init(esp.getID() + "," + flm.getName(), miFirst);
			String h[] = mv.getHeader();
			int numPredictors = h.length -1;
			for (int i = 0; i < numPredictors; i++) {
				Double p = Double.parseDouble(mv.getData().get(0)[i]);
				
				String matrixPred = h[i];
				if (modelPre.containsKey(matrixPred))
					currentPredictionPre += modelPre.get(matrixPred) * p;

				if (modelRec.containsKey(matrixPred))
					currentPredictionRec += modelRec.get(matrixPred) * p;
			}
			
			statData.add(new String[]{String.valueOf(esp.getID()), flm.getName(), String.valueOf(currentPredictionPre),String.valueOf(currentPredictionRec)});
		}
		
		stat.setData(statData);
		results.add(stat);
		
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
		
		
		predModel12Pre.put("Dominants",0.280477496319623);
		predModel12Pre.put("MaxPredictor",0.130121440907229);
		predModel12Pre.put("BMPredictor",0.0905016010450473);
		predModel12Pre.put("RPSTWidthRelative",0.0518381342271626);
		predModel12Pre.put("AvgLengthOfLabelsAbsolute",0.0373924494379596);
		predModel12Pre.put("SizeExclusivenessRelationRelative",0.0320668919040233);
		predModel12Pre.put("AvgConfPredictor",0.0368991665355446);
		predModel12Pre.put("LMMPredictor",0.146569428260973);
		predModel12Pre.put("BMMPredictor",0.118851340962727);
		predModel12Pre.put("SizeConcurrencyRelationAbsolute",0.0389460292414473);
		predModel12Pre.put("RPSTDepthRelative",0.0363360211582629);
		
		predModel13Pre.put("Dominants",0.220718287528929);
		predModel13Pre.put("MaxPredictor",0.0964434955788369);
		predModel13Pre.put("BMPredictor",0.116502530445991);
		predModel13Pre.put("RPSTWidthRelative",0.0612731992851307);
		predModel13Pre.put("AvgLengthOfLabelsAbsolute",0.0570187872599002);
		predModel13Pre.put("LMMPredictor",0.172262333634799);
		predModel13Pre.put("BMMPredictor",0.122855413214613);
		predModel13Pre.put("RPSTDepthAbsolute",0.0396203319194594);
		predModel13Pre.put("NumberSourceNodes",0.035520243954811);
		predModel13Pre.put("SizeStrictOrderRelationAbsolute",0.0467207760461695);
		predModel13Pre.put("NumberLabelsWithObectsInWordNet",0.0310646011313606);

		predModel23Pre.put("Dominants",0.192846996133713);
		predModel23Pre.put("MaxPredictor",0.0807834182564318);
		predModel23Pre.put("BMPredictor",0.128365877689516);
		predModel23Pre.put("LMMPredictor",0.17398943671764);
		predModel23Pre.put("RPSTDepthRelative",0.0525006876419221);
		predModel23Pre.put("AvgLengthOfLabelsAbsolute",0.0792876335276034);
		predModel23Pre.put("BMMPredictor",0.108997420201788);
		predModel23Pre.put("STDEVPredictor",0.0439775376316558);
		predModel23Pre.put("SizeConcurrencyRelationAbsolute",0.0323281562631224);
		predModel23Pre.put("MaxNodeDegreeRelative",0.0459220939894467);
		predModel23Pre.put("AvgNodeDegreeAbsolute",0.0610007419471604);
		predModel23Pre.put("RPSTWidthAbsolute",0.0514627300514169);

		predModel12Rec.put("MaxPredictor",0.109939881992366);
		predModel12Rec.put("NumberLabelsWithActionsInWordNet",0.0538557650885591);
		predModel12Rec.put("NodesInCycle",0.0686181687799514);
		predModel12Rec.put("AvgNodeDegreeRelative",0.0664019892324364);
		predModel12Rec.put("LMMPredictor",0.130875209355389);
		predModel12Rec.put("RPSTDepthRelative",0.0397820387890466);
		predModel12Rec.put("AvgLengthOfLabelsRelative",0.0211057961097557);
		predModel12Rec.put("AvgNumberActionSynsetsAbsolute",0.0418977146205983);
		predModel12Rec.put("BMMPredictor",0.0697160589625133);
		predModel12Rec.put("AvgConfPredictor",0.0767412911926443);
		predModel12Rec.put("Dominants",0.0524353283636107);
		predModel12Rec.put("RPSTWidthRelative",0.0286962466355048);
		predModel12Rec.put("Size",0.0278673119404241);
		predModel12Rec.put("StructurednessAbsolute",0.0374509912632816);
		predModel12Rec.put("SizeExclusivenessRelationRelative",0.0429481011522496);
		predModel12Rec.put("SizeStrictOrderRelationRelative",0.0304487813455108);
		predModel12Rec.put("NumberSinkNodes",0.0209331214757995);
		predModel12Rec.put("SizeConcurrencyRelationRelative",0.0217537820782648);
		predModel12Rec.put("BMPredictor",0.0277965923074849);
		predModel12Rec.put("NumberRPSTFragmentTypesRelative",0.0140192771694924);
		predModel12Rec.put("AvgLengthOfLabelsAbsolute",0.0167165521451169);

		predModel13Rec.put("MaxPredictor",0.149818992007554);
		predModel13Rec.put("NumberLabelsWithActionsInWordNet",0.0735076278348998);
		predModel13Rec.put("NodesInCycle",0.0624678305986258);
		predModel13Rec.put("LMMPredictor",0.155697814223314);
		predModel13Rec.put("Dominants",0.0724962934332204);
		predModel13Rec.put("AvgConfPredictor",0.0958290177606012);
		predModel13Rec.put("BMMPredictor",0.104779451050535);
		predModel13Rec.put("Size",0.0288388496298759);
		predModel13Rec.put("RPSTDepthAbsolute",0.0447890584590399);
		predModel13Rec.put("AvgLengthOfLabelsAbsolute",0.0188123819497674);
		predModel13Rec.put("NumberSinkNodes",0.0371755428439653);
		predModel13Rec.put("MaxNodeDegreeAbsolute",0.0168349442461638);
		predModel13Rec.put("StructurednessAbsolute",0.0347047787063585);
		predModel13Rec.put("NumberLabelsWithObectsInWordNet",0.0545398377686923);
		predModel13Rec.put("SizeExclusivenessRelationAbsolute",0.0291225831820243);
		predModel13Rec.put("AvgNumberActionSynsetsRelative",0.0205849963053613);

		predModel23Rec.put("MaxPredictor",0.149301957302304);
		predModel23Rec.put("NumberLabelsWithActionsInWordNet",0.0399329649276762);
		predModel23Rec.put("LMMPredictor",0.179980230545438);
		predModel23Rec.put("AvgNodeDegreeAbsolute",0.0426446886777592);
		predModel23Rec.put("Dominants",0.0675725561700396);
		predModel23Rec.put("AvgConfPredictor",0.104050917858343);
		predModel23Rec.put("BMMPredictor",0.133232799498934);
		predModel23Rec.put("NodesInCycle",0.0472272180809428);
		predModel23Rec.put("AvgNodeDegreeRelative",0.0683403783961758);
		predModel23Rec.put("RPSTDepthRelative",0.0268399531203316);
		predModel23Rec.put("NumberSinkNodes",0.0139351437272794);
		predModel23Rec.put("SizeConcurrencyRelationRelative",0.0417963718895196);
		predModel23Rec.put("AvgLengthOfLabelsAbsolute",0.0333851991335634);
		predModel23Rec.put("SizeExclusivenessRelationRelative",0.0248983332339145);
		predModel23Rec.put("MaxNodeDegreeRelative",0.0268612874377787);

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

}

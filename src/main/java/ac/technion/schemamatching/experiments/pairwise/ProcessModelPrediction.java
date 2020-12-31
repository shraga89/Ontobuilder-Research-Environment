package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import matching.virtualdoc.VirtualDocParams;
import nl.tue.tm.is.labelAnalyzer.interfaces.SemanticLanguage;
import nl.tue.tm.is.labelAnalyzer.interfaces.SemanticLanguage.LanguageCode;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.ProcessModelCompleteMatchers;
import ac.technion.schemamatching.matchers.firstline.ProcessModelCompleteMatchers.ProcessModelMatcher;
import ac.technion.schemamatching.matchers.firstline.ProcessModelFLM;
import ac.technion.schemamatching.matchers.firstline.ProcessModelFLM.ProcessModelMatchStrategy;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.AttributeNBGolden;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.DummyStatistic;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.predictors.AttributePredictors;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictors;
import ac.technion.schemamatching.statistics.predictors.ProcessModelPropertyPredictor;
import ac.technion.schemamatching.statistics.predictors.ProcessModelPropertyPredictor.ProcessModelProperty;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

public class ProcessModelPrediction implements PairWiseExperiment {

	private List<SecondLineMatcher> slM;
	
	public Set<Integer> dutchSPIDs;

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
		 * 1. Check the actual results obtained with the complete matchers
		 */
		ProcessModelCompleteMatchers completeMatchers = new ProcessModelCompleteMatchers();
		for(ProcessModelMatcher matcher : ProcessModelMatcher.values()) {
			if (this.dutchSPIDs.contains(esp.getID()) && matcher.equals(ProcessModelMatcher.BPMatcher))
				continue;
			
			System.out.println("Complete Matcher: " + matcher.toString());
			
			completeMatchers.setMatchingStrategy(matcher);
			
			MatchInformation mi = completeMatchers.match(esp.getCandidateOntology(), esp.getTargetOntology(), true);
			
			K2Statistic binary = new BinaryGolden();
			String instanceDesc = esp.getID()+","+completeMatchers.getName();
			binary.init(instanceDesc, mi, esp.getExact());
			results.add(binary);
		}
		
		/*
		 * 2. Consider all process model sim measures
		 */
		ProcessModelFLM flm = new ProcessModelFLM();
		for (ProcessModelMatchStrategy strategy : ProcessModelMatchStrategy.values()) {
			System.out.println("FLM: " + strategy.toString());
			flm.setMatchingStrategy(strategy, languageCode);
			MatchInformation miFirst = flm.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			
			/*
			 * 2.1 Non-Binary results for the sim measure, also on attribute level
			 */
			K2Statistic nonBinary = new NBGolden();
			String instanceDesc = esp.getID()+","+flm.getName();
			nonBinary.init(instanceDesc, miFirst, esp.getExact());
			results.add(nonBinary);
			
			K2Statistic  nonBinaryAtt = new AttributeNBGolden();
			instanceDesc = esp.getID()+","+flm.getName();
			nonBinaryAtt.init(instanceDesc, miFirst, esp.getExact());
			results.add(nonBinaryAtt);
			
			/*
			 * 2.2 All matrix and attribute predictors for the sim measure
			 */
			System.out.println("FLM Matrix");
			Statistic  mPred = new MatrixPredictors();
			instanceDesc = esp.getID()+","+flm.getName();
			mPred.init(instanceDesc, miFirst);
			results.add(mPred);
			
			Statistic  aPred = new AttributePredictors();
			instanceDesc = esp.getID()+","+flm.getName();
			aPred.init(instanceDesc, miFirst);
			results.add(aPred);

			/*
			 * 2.3 Binary result for a selection of SLMs
			 */
			System.out.print("FLM with SLM ");
			for (SecondLineMatcher sMatcher : this.slM) {
				System.out.print(sMatcher.getName() + " ");
				MatchInformation miSecond = sMatcher.match(miFirst);

				K2Statistic binary = new BinaryGolden();
				instanceDesc = esp.getID()+","+ flm.getName()+"_"+sMatcher.getName();
				binary.init(instanceDesc, miSecond, esp.getExact());
				results.add(binary);
			}
			System.out.println("");
		}
		
		/*
		 * 3. Consider all process model property predictors
		 */
		DummyStatistic stat = new DummyStatistic();
		stat.setName("ProcessModelPropertyPrediction");
		stat.setHeader(new String[]{"ID","Predictor","Value"});
		List<String[]> statData= new ArrayList<>();

		ProcessModelPropertyPredictor predictor = new ProcessModelPropertyPredictor(esp.getCandidateOntology(), esp.getTargetOntology(),languageCode);
		for (ProcessModelProperty property : ProcessModelProperty.values()) {
			if (this.dutchSPIDs.contains(esp.getID()) && 
					(property.toString().toLowerCase().contains("exclusiveness") || 
							property.toString().toLowerCase().contains("strictorder") || 
							property.toString().toLowerCase().contains("concurrency")))
				continue;

			
			predictor.setProperty(property);
			statData.add(new String[]{String.valueOf(esp.getID()), predictor.getName(), String.valueOf(predictor.getRes())});
		}
		stat.setData(statData);
		results.add(stat);
		
		return results;
	}

	@Override
	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		this.slM = slM;
		
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

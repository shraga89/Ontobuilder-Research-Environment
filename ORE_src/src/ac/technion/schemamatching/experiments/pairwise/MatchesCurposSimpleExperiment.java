package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.curpos.CorpusDataManager;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.CurposAugmentFLM;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.all.BinaryGolden;
import ac.technion.schemamatching.statistics.all.K2Statistic;
import ac.technion.schemamatching.statistics.all.NBGolden;
import ac.technion.schemamatching.statistics.all.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

public class MatchesCurposSimpleExperiment implements PairWiseExperiment {

	private double threshold;
	private ArrayList<SecondLineMatcher> slM;
	private Properties properties;
	
	@Override
	public List<Statistic> runExperiment(ExperimentSchemaPair esp) {
		
		CurposAugmentFLM matcher = new CurposAugmentFLM(esp.getDataSetType(), threshold, 5); 
		
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		
		MatchInformation mi = esp.getSimilarityMatrix(matcher);
		
		//Calculate Non-Binary Precision and Recall
		K2Statistic nb = new NBGolden();
		String instanceDesc =  esp.getID() + "," + matcher.getName();
		nb.init(instanceDesc, mi,esp.getExact());
		evaluations.add(nb);
		
		//Using all second line matchers
		for (SecondLineMatcher s : slM)
		{
			//Second Line Match
			if (properties == null || !s.init(properties)) 
				System.err.println("Initialization of " + s.getName() + 
						"failed, we hope the author defined default values...");
			MatchInformation mi1 = s.match(mi);
			//calculate Precision and Recall
			K2Statistic b2 = new BinaryGolden();
			instanceDesc =  esp.getID() + "," + matcher.getName() + "," + s.getName()+ "," + s.getConfig();
			b2.init(instanceDesc, mi1,esp.getExact());
			evaluations.add(b2);
		}

		return evaluations;
	}

	@Override
	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		
		threshold = Double.parseDouble(properties.getProperty("Threshold","0"));
		CorpusDataManager.setPropertiesFile(properties);
		this.properties =properties;
		
		this.slM = (slM == null)? new ArrayList<SecondLineMatcher>() : slM;
		return true;
	}

	@Override
	public String getDescription() {
		return "Runs an experiment that uses the CurposAugmentMatcher as first line matcher.";
	}

	@Override
	public List<Statistic> summaryStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

}

package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.ensembles.Ensemble;
import ac.technion.schemamatching.ensembles.SimpleWeightedEnsemble;
import ac.technion.schemamatching.matchers.FLMList;
import ac.technion.schemamatching.matchers.FirstLineMatcher;
import ac.technion.schemamatching.matchers.OBTopK;
import ac.technion.schemamatching.matchers.SLMList;
import ac.technion.schemamatching.matchers.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.ComplexBinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.util.ConversionUtils;

public class TopKClustering implements MatchingExperiment {

	private ArrayList<FirstLineMatcher> flM;
	private HashMap<String,Double> matcherWeights = new HashMap<String,Double>();

	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		
//		System.out.println(weightedMI.getMatches());

//		double[] simThresholds = new double[]{0.55,0.5,0.45,0.4,0.35,0.3,0.25};
//		int[] ks = new int[]{20,40,60,80,100,120};
		
		double[] simThresholds = new double[]{0.5};
		int[] ks = new int[]{20,40,60,80,100};
		
		for (int i = 0; i < simThresholds.length; i++) {
			double simThreshold = simThresholds[i];
			for (int j = 0; j < ks.length; j++) {
				int k = ks[j];
				
				
				MatchInformation weightedMI = FLMList.AMCName.getFLM().match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
				ConversionUtils.zeroWeightsByThresholdAndRemoveMatches(weightedMI, simThreshold);
				
				OBTopK.k = k;
				
				MatchInformation matchesBaseline = SLMList.OBMWBG.getSLM().match(weightedMI);
				K2Statistic b = new ComplexBinaryGolden();
				String id = esp.getSPID()+",baseline," + simThreshold + "," + k; 
				b.init(id, matchesBaseline,esp.getExact());
				res.add(b);
				
				System.out.println("Baseline");
				for (String[] s : b.getData())
					for (int l = 0; l < s.length; l++)
						System.out.println(s[l]);

				
				MatchInformation matchesClustered = SLMList.OBTopK.getSLM().match(weightedMI);
				b = new ComplexBinaryGolden();
				id = new String(esp.getSPID()+",clustered," + simThreshold + "," + k); 
				b.init(id, matchesClustered,esp.getExact());
				res.add(b);
				
				System.out.println("Top-K");
				for (String[] s : b.getData())
					for (int l = 0; l < s.length; l++)
						System.out.println(s[l]);


			}
			
		}
		
//		ConversionUtils.zeroWeightsByThresholdAndRemoveMatches(weightedMI, 0.4);
//		
//		//Get baseline matches 
//		MatchInformation matchesBaseline = SLMList.OBMWBG.getSLM().match(weightedMI);
//		
//		K2Statistic b = new ComplexBinaryGolden();
//		String id = esp.getSPID()+",baseline"; 
//		b.init(id, matchesBaseline,esp.getExact());
//		res.add(b);
//		
//		
//		System.out.println("Baseline");
////		System.out.println(matchesBaseline.getMatches().size());
////		System.out.println(matchesBaseline.getMatches());
//		for (String[] s : b.getData())
//			for (int i = 0; i < s.length; i++)
//				System.out.println(s[i]);
//		
//
//		//Get matches with clustering
//		MatchInformation matchesClustered = SLMList.OBTopK.getSLM().match(weightedMI);
//		b = new ComplexBinaryGolden();
//		id = new String(esp.getSPID()+",clustered"); 
//		b.init(id, matchesClustered,esp.getExact());
//		res.add(b);
//
//		System.out.println("Clustered ");
////		System.out.println(matchesClustered.getMatches().size());
//		System.out.println(matchesClustered.getMatches());
//		for (String[] s : b.getData())
//			for (int i = 0; i < s.length; i++)
//				System.out.println(s[i]);

		return res;
	}

	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		
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
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<Statistic> summaryStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

}

package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.ensembles.Ensemble;
import ac.technion.schemamatching.ensembles.SimpleWeightedEnsemble;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FLMList;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SLMList;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.ComplexBinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.util.ConversionUtils;

public class TopKClustering implements PairWiseExperiment {

	private List<FirstLineMatcher> flM;
	private HashMap<String,Double> matcherWeights = new HashMap<String,Double>();
	private Properties properties;

	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		
		ArrayList<Statistic> res = new ArrayList<Statistic>();


//		double[] simThresholds = new double[]{0.55,0.5,0.45,0.4,0.35,0.3,0.25};
//		int[] ks = new int[]{20,40,60,80,100,120};
		
		double[] simThresholds = new double[]{0.7};
		int[] ks = new int[]{20};
		
		for (int i = 0; i < simThresholds.length; i++) {
			double simThreshold = simThresholds[i];
			for (int j = 0; j < ks.length; j++) {
				int k = ks[j];
				
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

				ConversionUtils.zeroWeightsByThresholdAndRemoveMatches(weightedMI, simThreshold);
				
				//OBTopK.k = k;
				
				MatchInformation matchesBaseline = SLMList.OBMWBG.getSLM().match(weightedMI);
				K2Statistic b = new ComplexBinaryGolden();
				String id = esp.getID()+",baseline," + simThreshold + "," + k; 
				b.init(id, matchesBaseline,esp.getExact());
				res.add(b);
				
				System.out.println(matchesBaseline.getCopyOfMatches());
				
				System.out.println("Baseline");
				for (String[] s : b.getData())
					for (int l = 0; l < s.length; l++)
						System.out.println(s[l]);

				SecondLineMatcher slm = SLMList.OBTopK.getSLM();
				slm.init(properties);
				MatchInformation matchesClustered = slm.match(weightedMI);
				b = new ComplexBinaryGolden();
				id = new String(esp.getID()+",clustered," + simThreshold + "," + k); 
				b.init(id, matchesClustered,esp.getExact());
				res.add(b);

				System.out.println(matchesClustered.getCopyOfMatches());

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
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		
		this.flM = flM;
		HashMap<Integer, FirstLineMatcher> flmHash = FLMList.getIdFLMHash();
		for (Object key : properties.keySet())
		{
			String strKey =(String)key; 
			try
			{
			Integer mId = Integer.parseInt(strKey);
			Double pWeight = Double.parseDouble((String)properties.get(key));
			matcherWeights.put(flmHash.get(mId).getName(), pWeight);
			}
			catch(NumberFormatException e)
			{
				continue; //Not all properties in the file are matcher IDs
			}
			this.properties = properties;
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

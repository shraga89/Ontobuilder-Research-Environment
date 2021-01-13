package ac.technion.schemamatching.experiments.pairwise;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SLMList;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.*;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Properties;

/**
 * This simple match experiment is intended as a tutorial for 
 * new developers displaying the major features in ORE.
 * The experiment matches a given schema pair using specific 1LM and 2LM 
 * supplied and returns precision and recall. 
 *  This version adds verbose statistics.
 * @author Tomer Sagi
 *
 */
public class SimpleMatchExperimentVerboseNew implements PairWiseExperiment {
	private ArrayList<FirstLineMatcher> flM;
	//private ArrayList<SecondLineMatcher> slM;
	private Properties properties;
	private boolean isMemory;

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		
		// Using 1st line matchers chosen as parameters
		ArrayList<Statistic> evaluations = new ArrayList<>();
		for (FirstLineMatcher m : flM)
		{
			//Direct matching using the first line matcher allows to set parameters in the flm
			//mi = m.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			
			/*Preferred method is to use this method which looks up 
			 * the similarity matrix in the database if it exists. 
			*/
			MatchInformation mi = esp.getSimilarityMatrix(m, isMemory);
			
			ConversionUtils.zeroWeightsByThresholdAndRemoveMatches(mi, 0.01);
			ConversionUtils.limitToKMatches(mi, 10);
			
			//Calculate Non-Binary Precision and Recall
			K2Statistic nb = new NBGolden();
			String instanceDesc =  esp.getID() + "," + m.getName();
			nb.init(instanceDesc, mi,esp.getExact());
			evaluations.add(nb);
			
			//Calculate Non-Binary Precision and Recall @ K
			K2Statistic nbk = new NBGoldenAtK();
			nbk.init(instanceDesc, mi,esp.getExact());
			evaluations.add(nbk);
			
			//Calculate Non-Binary Precision and Recall @ KA
			K2Statistic nbka = new NBGoldenAtDynamicK();
			nbka.init(instanceDesc, mi,esp.getExact());
			evaluations.add(nbka);			
			
			//Calculate Non-Binary Precision @ R
			K2Statistic nbr = new NBGoldenAtR();
			nbr.init(instanceDesc, mi,esp.getExact());
			evaluations.add(nbr);
			
			//Calculate MatchDisatance
			K2Statistic md = new MatchDistance();
			md.init(instanceDesc, mi,esp.getExact());
			evaluations.add(md);
			
			//selecting second line matchers to use
			ArrayList<SecondLineMatcher> slm_to_use= new ArrayList<>();
			slm_to_use.add(SLMList.OBMWBG.getSLM());
			slm_to_use.add(SLMList.OBMaxDelta01.getSLM());
			slm_to_use.add(SLMList.OBThreshold085.getSLM());
			slm_to_use.add(SLMList.OBDom.getSLM());
			slm_to_use.add(SLMList.OBSM.getSLM());
		
			for (SecondLineMatcher s : slm_to_use)
			{
				//Second Line Match
				if (properties == null || !s.init(properties)) 
					System.err.println("Initialization of " + s.getName() + 
							"failed, we hope the author defined default values...");
				MatchInformation mi1 = s.match(mi);
				//calculate Precision and Recall
				K2Statistic b2 = new BinaryGolden();
				instanceDesc =  esp.getID() + "," + m.getName() + "," + s.getName()+ "," + s.getConfig();
				b2.init(instanceDesc, mi1,esp.getExact());
				evaluations.add(b2);
				//Calculate verbose binary
				K2Statistic b3 = new VerboseBinaryGolden();
				instanceDesc =  esp.getID() + "," + m.getName() + "," + s.getName()+ "," + s.getConfig();
				b3.init(instanceDesc, mi1,esp.getExact());
				evaluations.add(b3);
				//Calculate MatchDisatance
				K2Statistic md2 = new MatchDistance();
				md2.init(instanceDesc, mi1,esp.getExact());
				evaluations.add(md2);
				//Calculate MCC
				K2Statistic mcc = new MCC();
				mcc.init(instanceDesc, mi1,esp.getExact());
				evaluations.add(mcc);
			}
		}
		return evaluations;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		/*Using the supplied first line matcher list and second line matcher list allows run-time 
		changes to matchers used in the experiment*/
		this.flM = flM;
		//this.slM = slM;
		//using property files allows to modify experiment parameters at runtime
		this.properties = properties;
		this.isMemory = isMemory;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return "Simple match experiment";
	}
	
	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}

}

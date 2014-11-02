package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SLMList;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.NBGoldenAtDynamicK;
import ac.technion.schemamatching.statistics.NBGoldenAtK;
import ac.technion.schemamatching.statistics.NBGoldenAtR;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.VerboseBinaryGolden;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

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

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		// Using 1st line matchers chosen as parameters
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		for (FirstLineMatcher m : flM)
		{
			MatchInformation mi = null;
			//Direct matching using the first line matcher allows to set parameters in the flm
			//mi = m.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			
			/*Preferred method is to use this method which looks up 
			 * the similarity matrix in the database if it exists. 
			*/
			mi = esp.getSimilarityMatrix(m);
			
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
			
			//selecting second line matchers to use
			ArrayList<SecondLineMatcher> slm_to_use= new ArrayList<SecondLineMatcher>();
			slm_to_use.add(SLMList.OBMWBG.getSLM());
			slm_to_use.add(SLMList.OBMaxDelta01.getSLM());
			slm_to_use.add(SLMList.OBThreshold085.getSLM());
			slm_to_use.add(SLMList.OBCrossEntropy.getSLM());
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
			}
			
		}
		return evaluations;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer,Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		/*Using the supplied first line matcher list and second line matcher list allows run-time 
		changes to matchers used in the experiment*/
		this.flM = flM;
		//this.slM = slM;
		//using property files allows to modify experiment parameters at runtime
		this.properties = properties;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return "Simple match experiment (Developer Tutorial)";
	}
	
	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}

}
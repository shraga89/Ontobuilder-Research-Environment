package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MatchDistance;
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.NBGoldenAtDynamicK;
import ac.technion.schemamatching.statistics.NBGoldenAtK;
import ac.technion.schemamatching.statistics.NBGoldenAtR;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * This simple match experiment is intended as a tutorial for 
 * new developers displaying the major features in ORE. 
 * The experiment matches a given schema pair using all 1LM and 2LM 
 * supplied and returns precision and recall 
 * @author Tomer Sagi
 *
 */
public class SimpleMatchExperiment implements PairWiseExperiment {
	private ArrayList<FirstLineMatcher> flM;
	private ArrayList<SecondLineMatcher> slM;
	private Properties properties;
	private boolean isMemory;

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		// Using all 1st line matchers

		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		for (FirstLineMatcher m : flM)
		{
			MatchInformation mi = null;
			//Direct matching using the first line matcher allows to set parameters in the flm
			//mi = m.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			
			/*Preferred method is to use this method which looks up 
			 * the similarity matrix in the database if it exists. 
			*/
			mi = esp.getSimilarityMatrix(m, false);
			
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
			
			//Using all second line matchers
			for (SecondLineMatcher s : slM)
			{
				//Second Line Match
				if (properties == null || !s.init(properties)) 
					System.err.println("Initialization of " + s.getName() + 
							"failed, we hope the author defined default values...");
				double startTime = System.currentTimeMillis();
				MatchInformation mi1 = s.match(mi);
				double endTime = System.currentTimeMillis();
				System.out.println(s.getName() + " Runtime: " + (endTime - startTime));
				//calculate Precision and Recall
				K2Statistic b2 = new BinaryGolden();
				instanceDesc =  esp.getID() + "," + m.getName() + "," + s.getName()+ "," + s.getConfig();
				b2.init(instanceDesc, mi1,esp.getExact());
				evaluations.add(b2);
				//Calculate MatchDistance
				K2Statistic md2 = new MatchDistance();
				md2.init(instanceDesc, mi1,esp.getExact());
				evaluations.add(md2);
			}
			
		}

		return evaluations;
	}
	
	public MatchInformation runPairSchemaMatching(ExperimentSchemaPair esp) {
		// Using all 1st line matchers
		for (FirstLineMatcher m : flM)
		{
			//Direct matching using the first line matcher allows to set parameters in the flm
			//mi = m.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			
			/*Preferred method is to use this method which looks up 
			 * the similarity matrix in the database if it exists. 
			*/
			MatchInformation mi = esp.getSimilarityMatrix(m, false);
			if(mi == null)
				System.out.println("mi is null");
			for (SecondLineMatcher s : slM)
			{
				//Second Line Match
				if (properties == null || !s.init(properties)) 
					System.err.println("Initialization of " + s.getName() + 
							"failed, we hope the author defined default values...");
				MatchInformation mi1 = s.match(mi);
				return mi1;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		/*Using the supplied first line matcher list and second line matcher list allows run-time 
		changes to matchers used in the experiment*/
		this.flM = flM;
		this.slM = slM;
		this.isMemory = isMemory;
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

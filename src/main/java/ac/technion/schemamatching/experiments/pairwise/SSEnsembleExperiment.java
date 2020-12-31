package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MatchDistance;
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.NBGoldenAtDynamicK;
import ac.technion.schemamatching.statistics.NBGoldenAtK;
import ac.technion.schemamatching.statistics.NBGoldenAtR;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictors;


/**
 * 	This match experiment is a session of a evaluations fused into a single evaluation. 
 * The experiment matches a given schema pair using specific 1LM and 2LM given by arguments.
 * Returns precision and recall. 
 * @author Shachaf Lemberger & Shay Shusterman
 *
 */

public class SSEnsembleExperiment implements PairWiseExperiment {
	private ArrayList<MatchInformation> miflMl;
	private ArrayList<MatchInformation> mislMl; 
	private ArrayList<FirstLineMatcher> flM;
	private ArrayList<SecondLineMatcher> slM;
	private Properties properties;
	private boolean isMemory;
	private String method;
	public static final String ExpName = "SSEnsemble";
	
	/*
	 * @param 	ExperimentSchemaPair esp - the tested Schema Pair
	 * @return  ArrayList<Statistic> evaluations - Holds all the evaluations concluded by the Experiment
	 * @see 	ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		
		// Using all 1st line matchers
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		for (FirstLineMatcher m : flM)
		{
			MatchInformation mi = null;
			mi = esp.getSimilarityMatrix(m, false);
			
			// Saving Match Information for each 1st line matcher
			miflMl.add(mi);
			
			//Using all 2nd line matchers
			for (SecondLineMatcher s : slM)
			{
				if (properties == null || !s.init(properties)) 
					System.err.println("Initialization of " + s.getName() + 
							"failed, we hope the author defined default values...");
				double startTime = System.currentTimeMillis();
				MatchInformation mi1 = s.match(mi);
				double endTime = System.currentTimeMillis();
				System.out.println(s.getName() + " Runtime: " + (endTime - startTime));
				evaluations = this.calculateSlmStatistics(evaluations, mi1, esp, m.getName(), s.getName(), s.getConfig());
				
				//Saving Match Information for each 2nd line matcher
				mislMl.add(mi1);
			}
		}
		
		// Fusing Match Information 
		MatchInformation fusedMi = this.fuseMatchInformationObjArray(miflMl, esp, this.method);
		
		// Calculating all results for 2nd line matchers for the fused matrix
		for (SecondLineMatcher s : slM)
		{
			if (properties == null || !s.init(properties)) 
				System.err.println("Initialization of " + s.getName() + 
						"failed, we hope the author defined default values...");
			double startTime = System.currentTimeMillis();
			MatchInformation mi1 = s.match(fusedMi);
			double endTime = System.currentTimeMillis();
			System.out.println(s.getName() + " Runtime: " + (endTime - startTime));
			
			evaluations = this.calculateSlmStatistics(evaluations, mi1, esp, ExpName, s.getName(), s.getConfig());
			
			// Saving Match Information of the fused mi for each 2nd line matcher.
			mislMl.add(fusedMi);
		}
		return evaluations;
	}

	/*
	 * @desc 	This function takes all the Match Information for each FlM and creates a new matrix that holds the re-evaluated confidence estimation between two terms
	 * @param 	ArrayList<MatchInformation> FlmMiArray - Array that holds all Match Information for each FlM  
	 * @param	ExperimentSchemaPair esp - The tested Schema Pair
	 * @param 	String method - Argument for method of fusion, defined @properties file
	 * @return 	MatchInformation fusedMi - The new fused matrix
	 */	
	public MatchInformation fuseMatchInformationObjArray(ArrayList<MatchInformation> FlmMiArray, ExperimentSchemaPair esp, String method)
	{
		
		// Create MatchInformation obj from the given schema pair
		MatchInformation fusedMi = new MatchInformation(esp.getCandidateOntology(), esp.getTargetOntology());
		
		for (Term candidateTerm : esp.getCandidateOntology().getTerms(true))
		{
			for (Term targetTerm : esp.getTargetOntology().getTerms(true))
			{
				fusedMi.getMatrix().setMatchConfidence(candidateTerm, targetTerm, 0.0);
			}
		}
		
		if (method.equals("Predictors_Ensenble"))
		{
			
			// Calculate the sum of all predicator evaluations for all FlM 
			double sumWightedPred = 0.0;
			for (MatchInformation currentMi : FlmMiArray)
			{
				Statistic mv = new MatrixPredictors();
				mv.init(ExpName, currentMi);
				List<String[]> temp = mv.getData();
				String[] h = temp.get(0);
				int numPredictors = h.length -1;
				double weightedMatrixPrediction = 0.0;
				for (int i = 0; i < numPredictors; i++)
				{
					double p = Double.parseDouble(mv.getData().get(0)[i]);
					weightedMatrixPrediction += p;
				}
				sumWightedPred += weightedMatrixPrediction;
			}
			
			// Calculate the fused similarity matrix from the similarity cube
			for (MatchInformation currentMi : FlmMiArray)
			{
				Statistic mv1 = new MatrixPredictors();
				mv1.init(ExpName, currentMi);
				List<String[]> temp = mv1.getData();
				String[] h = temp.get(0);
				int numPredictors = h.length -1;
				double currentPred = 0.0;
				// Calculate current matrix predicator evaluation for current FlM
				for (int i = 0; i < numPredictors; i++)
				{
					double p = Double.parseDouble(mv1.getData().get(0)[i]);
					currentPred += p; 
				}
				// Calculate the relative weight of current MatchInformation Matrix
				double weight = currentPred / sumWightedPred;
				for (Term candidateTerm : esp.getCandidateOntology().getTerms(true))
				{
					for (Term targetTerm : esp.getTargetOntology().getTerms(true))
					{
						double tempValue = (double)currentMi.getMatrix().getMatchConfidence(candidateTerm, targetTerm);
						double fusedValue = (double)fusedMi.getMatrix().getMatchConfidence(candidateTerm, targetTerm);
						fusedMi.getMatrix().setMatchConfidence(candidateTerm, targetTerm, (tempValue*weight) + fusedValue);
					}
				}
			}
		}
		if (method.equals("SS"))
		{	
		
			// Calculate confidence score for each MatchInformation matrix
			double sumConfidence = 0.0;
			for (MatchInformation currentMi : FlmMiArray)
			{
				sumConfidence += this.calcMinMaxInterval(currentMi, esp);
			}
			
			// Calculate the fused similarity matrix from the similarity cube
			for (MatchInformation currentMi : FlmMiArray)
			{
				double currentConfidence = this.calcMinMaxInterval(currentMi, esp);
				double weight = currentConfidence / sumConfidence;
				for (Term candidateTerm : esp.getCandidateOntology().getTerms(true))
				{
					for (Term targetTerm : esp.getTargetOntology().getTerms(true))
					{
						double tempValue = (double)currentMi.getMatrix().getMatchConfidence(candidateTerm, targetTerm);
						double fusedValue = (double)fusedMi.getMatrix().getMatchConfidence(candidateTerm, targetTerm);
						fusedMi.getMatrix().setMatchConfidence(candidateTerm, targetTerm, (tempValue)*weight + fusedValue);
					}
				}
			}	
		}
		return fusedMi;
	}

	/*
	 * @desc 	Initializer 
	 * @param 	OBExperimentRunner oer - Experiment Name 
	 * @param 	Properties properties - Properties file name
	 * @param 	ArrayList<FirstLineMatcher> flM - Array list that holds all FlM identifiers for current experiment
	 * @param 	ArrayList<SecondLineMatcher> slM - Array list that holds all SlM identifiers for current experiment
	 * @param 	boolean isMemory - Not used, inherited  
	 * @return 	Boolean - True - indicating success
	 */	
	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM ,boolean isMemory) 
	{
		this.miflMl = new ArrayList<MatchInformation>();
		this.mislMl = new ArrayList<MatchInformation>();
		this.flM = flM;
		this.slM = slM;
		this.isMemory = isMemory;
		//using property files allows to modify experiment parameters at runtime
		this.properties = new Properties();
		this.properties = properties;
		for (Object key : properties.keySet())
		{
			if (key.equals("method"))
			{
				this.method = properties.getProperty((String)key);
			}
		}
		return true;
	}

	/*
	 * @dexc	Inherited
	 * @see 	ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() 
	{
		return "SSEnsemble experiment";
	}
	
	/*
	 * @desc	Inherited
	 */
	public ArrayList<Statistic> summaryStatistics() 
	{
		//unused
		return null;
	}
	/*
	 * @desc 	Function calculates the Non Binary statistics necessary for evaluation of FlM
	 * @param 	ArrayList<Statistic> evaluations - Hold all evaluations estimated in current run  
	 * @param 	MatchInformation mi - Holds currently tested MatchInformation
	 * @param	ExperimentSchemaPair esp -  The tested Schema Pair
	 * @param	String mName - Matcher Name 
	 * @return 	ArrayList<Statistic> evaluations - Holds all the evaluations concluded by the Experiment
	 */	
	
	public ArrayList<Statistic> calculateFlmStatistics(ArrayList<Statistic> evaluations, MatchInformation mi, ExperimentSchemaPair esp, String mName) {
		//Calculate Non-Binary Precision and Recall
		K2Statistic nb = new NBGolden();
		String instanceDesc =  esp.getID() + "," + mName;
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
		
		return evaluations;
	}
	
	/*
	 * @desc 	Function calculates the Binary Golden statistics necessary for evaluation of SlM
	 * @param 	ArrayList<Statistic> evaluations - Hold all evaluations estimated in current run  
	 * @param 	MatchInformation mi - Holds currently tested MatchInformation
	 * @param	ExperimentSchemaPair esp -  The tested Schema Pair
	 * @param	String mName - Matcher Name 
	 * @return 	ArrayList<Statistic> evaluations - Holds all the evaluations concluded by the Experiment
	 */	
	public ArrayList<Statistic> calculateSlmStatistics(ArrayList<Statistic> evaluations, MatchInformation mi1, ExperimentSchemaPair esp, String mName, String sName, String sConfigName) {
		//calculate Precision and Recall
		K2Statistic b2 = new BinaryGolden();
		String instanceDesc =  esp.getID() + "," + mName + "," + sName+ "," + sConfigName;
		b2.init(instanceDesc, mi1,esp.getExact());
		evaluations.add(b2);	
		return evaluations;
	}
	
	/*
	 * @desc 	Function calculates the difference between minimum and maximum values in table 
	 * @param 	MatchInformation currentMi - Holds currently tested MatchInformation
	 * @param 	ExperimentSchemaPair esp -  The tested Schema Pair
	 * @return 	double - difference between minimum and maximum values in table 
	 */	
	public double calcMinMaxInterval(MatchInformation currentMi, ExperimentSchemaPair esp)
	{
		double minValue = 2.0;
		double maxValue = -1.0;
		for (Term candidateTerm : esp.getCandidateOntology().getTerms(true)){
			for (Term targetTerm : esp.getTargetOntology().getTerms(true)){
				if (currentMi.getMatrix().getMatchConfidence(candidateTerm, targetTerm) > maxValue)
				{
					maxValue = currentMi.getMatrix().getMatchConfidence(candidateTerm, targetTerm);
				}
				if (currentMi.getMatrix().getMatchConfidence(candidateTerm, targetTerm) < minValue && currentMi.getMatrix().getMatchConfidence(candidateTerm, targetTerm) != 0)
				{
					minValue = currentMi.getMatrix().getMatchConfidence(candidateTerm, targetTerm);
				}
			}
		}
		return maxValue - minValue;
	}
}
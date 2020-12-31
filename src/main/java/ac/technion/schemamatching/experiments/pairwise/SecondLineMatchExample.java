package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.SimMatrixShell;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.testbed.OREDataSetEnum;

/**
 * This simple match experiment is intended as a tutorial to display how to use
 * ORE to run 2nd line matchers on 1st line matrices given as .csv files. 
 * The experiment matches a given schema pair and matrix using all 2LM 
 * supplied and returns precision and recall. 
 * Experiment assumes a properties file with three properties is supplied:
 * datasetID = <ID of dataset from which the matrices are derived>
 * matrixPath = <Path in which the matrices reside>
 * matrices = <semi-colon delimited pairs where each pair is comma delimited: spid,fileName>
 * Example:
 * datasetID = 24
 * matrixPath = C:\\Temp
 * matrices = 2806,pair04.txt;2807,pair05.txt  
 * @author Tomer Sagi
 *
 */
public class SecondLineMatchExample implements PairWiseExperiment {
	private ArrayList<SecondLineMatcher> slM;
	private SimMatrixShell sms = new SimMatrixShell();
	String pairPath = "";
	HashMap<Integer,String> pairFiles = new HashMap<Integer,String>();
	
	private String instructions = "Invalid pair array supplied to shell matcher. \n " +
			"Please supply a property file with a property named 'matrices'" +
			"and a value of semi-colon delimited pairs where each pair is " +
			"a comma delimited: spid,path pair";

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		MatchInformation mi = null;
		if (!sms.setPath(pairPath,pairFiles.get(new Integer(esp.getID()))))
		{
			System.err.println("No file path found for pair:" + esp.getID());
			return evaluations;
		}
		mi = sms.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
		
		//Calculate Non-Binary Precision and Recall
		K2Statistic nb = new NBGolden();
		String instanceDesc =  esp.getID() + "," + sms.getName();
		nb.init(instanceDesc, mi,esp.getExact());
		evaluations.add(nb);
		
		//Using all second line matchers
		for (SecondLineMatcher s : slM)
		{
			//Second Line Match
			MatchInformation mi1 = s.match(mi);
			//calculate Precision and Recall
			K2Statistic b2 = new BinaryGolden();
			instanceDesc =  esp.getID() + "," + sms.getName() + "," + s.getName()+ "," + s.getConfig();
			b2.init(instanceDesc, mi1,esp.getExact());
			evaluations.add(b2);
		}
		return evaluations;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		this.slM = slM;
		try {
			int dsid = Integer.parseInt(properties.getProperty("datasetID"));
			sms.setImporter(OREDataSetEnum.getByDbid(dsid).getMatchImp());
		}
		catch (Exception e)
		{
			System.err.println("Dataset id unspecified or in invalid format.");
			e.printStackTrace();
			return false;
		}
		
		try {
			this.pairPath = properties.getProperty("matrixPath");
		}
		catch (Exception e)
		{
			System.err.println("Matrix path unspecified or in invalid format.");
			e.printStackTrace();
			return false;
		}
		String matriceFiles = properties.getProperty("matrices");
		String[] pairArray = matriceFiles.split(";");
		if (pairArray.length == 0)
		{
			System.err.println(instructions);
			return false;
		}
		for (String pair : pairArray)
		{
			String[] singlePair = pair.split(",");
			if (singlePair.length !=2)
			{
				System.err.println(instructions);
				return false;
			}
			try{
				pairFiles.put(Integer.parseInt(singlePair[0]),singlePair[1] );
			}
			catch (Exception e)
			{
				System.err.println(instructions);
				e.printStackTrace();
				return false;
			}
		}
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

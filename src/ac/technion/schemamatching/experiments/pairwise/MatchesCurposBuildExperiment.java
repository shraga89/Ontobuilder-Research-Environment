package ac.technion.schemamatching.experiments.pairwise;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.google.gson.Gson;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.curpos.CurposTerm;
import ac.technion.schemamatching.curpos.MatchesCurpos;
import ac.technion.schemamatching.curpos.NameCurposTerm;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

public class MatchesCurposBuildExperiment implements PairWiseExperiment {

	private MatchesCurpos curpos;
	private double threshold;
	private File curposFile;
	
	/**
	 * Runs the experiment to populate the curpos with the data given,     
	 * @param esp Schema pair on which to run the experiment
	 * @return Empty statistics
	 */
	@Override
	public List<Statistic> runExperiment(ExperimentSchemaPair esp) {
		Ontology candOntology = esp.getCandidateOntology();
		Ontology targOntology = esp.getTargetOntology();
		MatchInformation mi = esp.getExact();
		
		Vector<Term> candList = candOntology.getTerms(true);
		Vector<Term> targList = targOntology.getTerms(true);
		
		for (Term candidate:candList)
			for (Term target:targList){
				double fitness = mi.getMatchConfidence(candidate, target);
				if (fitness > threshold){
					CurposTerm candTerm = getCurposTerm(candidate);
					CurposTerm targTerm = getCurposTerm(target);
					
					curpos.add(candTerm, targTerm, fitness);
				}
			}
		
		return new ArrayList<Statistic>();
	}

	public CurposTerm getCurposTerm(Term t){
		// TODO : needs better implementation
		return new NameCurposTerm(t);
	}
	
	/**
	 * Used to initialize a matching experiment. replaces a parameterized constructor
	 * @param properties configuration parameters for the matching experiment
	 * @param flm list of @link{FirstLineMatcher} with which the experiment is run
	 * @param slm list of @link{SecondLineMatcher} with which the experiment is run 
	 * @return true if initialization succeeded false otherwise
	 */
	@Override
	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		
		threshold = Double.parseDouble(properties.getProperty("Threshold","0"));
		String fileName = properties.getProperty("CurposFileName", "Curpos");
		curposFile = getFile(fileName);
		if (curposFile.exists())
		{
			//	TODO : handle if curpos exists
		}
		curpos = new MatchesCurpos();
		return true;
	}

	/**
	 * Checks for existence of filepath supplied and creates the folder tree if needed
	 * @param resultFolder
	 * @return
	 */
	private File getFile(String filePath) {
		File entireFile = new File(filePath);
		File testFolder = entireFile.getParentFile();
		if (!testFolder.exists()) {
			boolean success = testFolder.mkdirs();
			if (!success) {
				System.err
						.println("Unable to create folder");
				return null;
			}
		}
		return entireFile;
	}
	
	/**
	 * 
	 * @return Experiment Description
	 */
	@Override
	public String getDescription() {
		return "This Experiment builds a MatchesCurpos for the StatiticalMatchesCurposFLM";
	}

	/**
	 * This is a hack to save the file because we wanted to find a place at the end of the writing
	 * @return Statistics sumarizing the experiment (for all schema pairs)
	 */
	@Override
	public List<Statistic> summaryStatistics() {
		com.google.gson.Gson g = new Gson();
		String curposJson = g.toJson(curpos);
		
		try{
			BufferedWriter output = new BufferedWriter(new FileWriter(curposFile)) ;
			output.write(curposJson);
			output.close();
		}catch (Exception ex){
			System.err.println("Failed to write curpos to" + curposFile.getPath());
			ex.printStackTrace();
		}
		return new ArrayList<>();
	}

}

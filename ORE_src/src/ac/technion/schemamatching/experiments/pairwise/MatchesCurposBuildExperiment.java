package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.curpos.CorpusDataManager;
import ac.technion.schemamatching.curpos.CurposTerm;
import ac.technion.schemamatching.curpos.MatchesCurpos;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.all.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.testbed.OREDataSetEnum;

public class MatchesCurposBuildExperiment implements PairWiseExperiment {

	private double threshold;
	
	/**
	 * Runs the experiment to populate the curpos with the data given,     
	 * @param esp Schema pair on which to run the experiment
	 * @return Empty statistics
	 */
	@Override
	public List<Statistic> runExperiment(ExperimentSchemaPair esp) {
		// get curpos or create new
		OREDataSetEnum dsType = esp.getDataSetType();
		MatchesCurpos curpos = CorpusDataManager.LoadMatchesCurpos(dsType);
		if (curpos == null) curpos = new MatchesCurpos();
		
		// prepare to fill curpos
		Ontology candOntology = esp.getCandidateOntology();
		Ontology targOntology = esp.getTargetOntology();
		MatchInformation mi = esp.getExact();
		Vector<Term> candList = candOntology.getTerms(true);
		Vector<Term> targList = targOntology.getTerms(true);
		
		// fill curpos
		for (Term candidate:candList)
			for (Term target:targList){
				double fitness = mi.getMatchConfidence(candidate, target);
				if (fitness > threshold){
					CurposTerm candTerm = getCurposTerm(candidate);
					CurposTerm targTerm = getCurposTerm(target);
					
					curpos.add(candTerm, targTerm, fitness);
				}
			}
		
		if (!CorpusDataManager.SaveMatchesCurpos(curpos, dsType))
			System.err.println("Failed to save curpos! eid:" + esp.getID());
		
		// return empty list
		return new ArrayList<Statistic>();
	}

	// here in case it will become more complicated
	private CurposTerm getCurposTerm(Term t){
		return new CurposTerm(t);
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
		CorpusDataManager.setPropertiesFile(properties);
		
		return true;
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
		return new ArrayList<>();
	}

}

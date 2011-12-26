/**
 * 
 */
package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import smb_service.LearnWorkingSet;
import smb_service.SMBTrain;
import smb_service.Schema;
import smb_service.SchemaPair;
import smb_service.SimilarityMatrix;

import ac.technion.schemamatching.matchers.FirstLineMatcher;
import ac.technion.schemamatching.matchers.SecondLineMatcher;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * @author Tomer Sagi
 * Generate matcher weights using machine learning on training sets. 
 * Uses the Adaboost algorithm
 *
 */
public class BoostingExperiment implements MatchingExperiment {
	SMBTrain smb;
	LearnWorkingSet lws = new LearnWorkingSet();
	private ArrayList<FirstLineMatcher> flm;
	private ArrayList<SecondLineMatcher> slm;
	

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		
		Schema c = ConversionUtils.ontology2schema(esp.getCandidateOntology()); 
		Schema t = ConversionUtils.ontology2schema(esp.getTargetOntology());
		SimilarityMatrix e = new SimilarityMatrix();
		//TODO fil with exact match results
		SchemaPair p = new SchemaPair(c,t,e);
		lws.schemaPairList.add(p);
		
		//TODO match and add matching results to lws
		
		return null;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		this.flm = flM;
		this.slm = slM;
		HashMap<Long, String> configs = new HashMap<Long, String>();
		for (FirstLineMatcher f : flm)
			for (SecondLineMatcher s :slm)
			{
				Long cCode = (long)1000 * f.getDBid() + s.getDBid();
				String cName = f.getName() + "," + s.getName();
				configs.put(cCode, cName);
			}
		lws.basicConfigurations = configs ;
		lws.schemaPairList = new ArrayList<SchemaPair>();
		lws.schemata = new HashMap<Long,Schema>();
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return "Generate matcher weights using machine learning on training sets. \n Uses the Adaboost algorithm";
	}

	public ArrayList<Statistic> summaryStatistics() {
		// TODO init SMB, train, return weights as statistics
		return null;
	}

}

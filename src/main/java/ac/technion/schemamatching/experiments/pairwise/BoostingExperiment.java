/**
 * 
 */
package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import smb_service.LearnWorkingSet;
import smb_service.SMBTrain;
import smb_service.Schema;
import smb_service.SchemaPair;
import smb_service.SimilarityMatrix;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.SMBTrainingPrinter;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * @author Tomer Sagi
 * Generate matcher weights using machine learning on training sets. 
 * Uses the Adaboost algorithm
 *
 */
public class BoostingExperiment implements PairWiseExperiment {
	SMBTrain smb;
	LearnWorkingSet lws = new LearnWorkingSet();
	private ArrayList<FirstLineMatcher> flm;
	private ArrayList<SecondLineMatcher> slm;
	

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) 
	{
		Ontology co = esp.getCandidateOntology();
		Ontology to = esp.getTargetOntology();
		Schema c = ConversionUtils.ontology2schema(co); 
		Schema t = ConversionUtils.ontology2schema(to);
		
		SimilarityMatrix e = ConversionUtils.mi2simMatrix(esp.getExact());
		SchemaPair p = new SchemaPair(c,t,e );
		
		//match and add matching results to lws
		HashMap<Long,SimilarityMatrix> correspondenceSet = new HashMap<Long,SimilarityMatrix>();
		for (FirstLineMatcher f : flm)
		{
			MatchInformation mi = f.match(co, to, false);
			for (SecondLineMatcher s :slm)
			{
				MatchInformation sMI = s.match(mi);
				SimilarityMatrix sm = ConversionUtils.mi2simMatrix(sMI);
				correspondenceSet.put((long)1000 * s.getDBid() + f.getDBid(), sm);
			}
		}
		p.setCorrespondenceSet(correspondenceSet);
		lws.schemaPairList.add(p);
		return null; //No statistics here, Training weights are given after all pairs are added
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		this.flm = flM;
		this.slm = slM;
		HashMap<Long, String> configs = new HashMap<Long, String>();
		for (FirstLineMatcher f : flm)
			for (SecondLineMatcher s :slm)
			{
				Long cCode = (long)1000 * s.getDBid() + f.getDBid();
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
		//Train SMB and get weights
		SMBTrain st = new SMBTrain(lws);
		HashMap<Long, Double> res = st.Train();
		Statistic s = new SMBTrainingPrinter(res);
		ArrayList<Statistic> a = new ArrayList<Statistic>();
		a.add(s);
		return a;
	}

}

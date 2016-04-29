package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.simple.Threshold2LM;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SLMList;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MCC;
import ac.technion.schemamatching.statistics.MatchCompetitorDeviation;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.predictors.AttributePredictorsSelect;
import ac.technion.schemamatching.statistics.predictors.EntryPredictorsSelect;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictors;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictorsSelect;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import technion.iem.schemamatching.dbutils.DBInterface;

/**
 * Evaluates matrix predictors by returning the predictor value next to
 * precision, recall and L2 similarity measures 
 * @author Tomer Sagi
 *
 */
public class PredictorCsharp implements PairWiseExperiment {
	private ArrayList<FirstLineMatcher> flM;
	private String type;
	private String predName;
	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		// Using all 1st line matchers 
		ArrayList<Statistic> predictions = new ArrayList<Statistic>();
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		
		for (FirstLineMatcher m : flM)
		{
			//Match
			MatchInformation mi = m.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			String instanceDesc = esp.getID()+","+m.getName()+","+this.predName;
			if (this.type.equals("MatrixPredictors")){
				Statistic  p = new MatrixPredictorsSelect(predName);
				p.init(instanceDesc, mi);
				predictions.add(p);
			}
			else if (this.type.equals("AttributePredictors")){
				Statistic  p = new AttributePredictorsSelect(predName);
				p.init(instanceDesc, mi);
				predictions.add(p);
			}
			else if (this.type.equals("EntryPredictors")){
				Statistic  p = new EntryPredictorsSelect(predName);
				p.init(instanceDesc, mi);
				predictions.add(p);
			}
			else{
				K2Statistic p = new MatchCompetitorDeviation();
				p.init(instanceDesc, mi, esp.getExact());
				predictions.add(p);
			}
		}
		predictions.addAll(evaluations);
		return predictions;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer,Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		this.flM = flM;
		if (properties.containsKey("predictor"))
		{
			String pred = (String) properties.get("predictor");
			String sql = "select pname, ptype from Predictors where pid = ";
			sql = sql + pred + ";";
			DBInterface db = oer.getDB();
			ArrayList<String[]> results = db.runSelectQuery(sql, 2);
			this.type = results.get(0)[1];
			this.predName = results.get(0)[0];
		}
		else{
			System.err.println("No Predictor specified");
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return "Predictor Evaluation";
	}
	
	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}

}

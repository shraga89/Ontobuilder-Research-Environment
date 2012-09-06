/**
 * 
 */
package ac.technion.schemamatching.experiments.holistic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.OBTermMatch;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictors;
import ac.technion.schemamatching.testbed.ExperimentSchema;

/**
 * @author Tomer Sagi
 *
 */
public class SchemaLookup implements HolisticExperiment{
	public ArrayList<Statistic> runExperiment(HashSet<ExperimentSchema> eSet) 
	{
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		OBTermMatch m = new OBTermMatch();
		//prepare all matches
		for (ExperimentSchema e1 : eSet)
			for (ExperimentSchema e2 : eSet)
			{
				if (e1.getID()!=e2.getID())
				{
					MatrixPredictors mv = new MatrixPredictors();
					MatchInformation mi = m.match(e1.getTargetOntology(), e2.getTargetOntology(),false);
					mv.init(e1.getID() + "," + e2.getID(), mi);
					res.add(mv);
				}				
			}
		return res;
	}

	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		return true;
}

	public String getDescription() {
		String desc = "1.Select 10 random schemas each time from our 249 webforms dataset" + 
					  "2.Match with all the rest and predict using a matrix predictor ensemble tuned on Precision." + 
					  "3.Calculate precision @5, precision @ 10 and precision @ 20 on  webforms where a relevant " +
					  "webform is one of the same domain of the one being looked up " +
					  "(we have domain classification for all 249 schemas to 21 domains)." +
					  "A random algorithm would have between 10-20% Precision @10.";
		return desc;
	}
	

}

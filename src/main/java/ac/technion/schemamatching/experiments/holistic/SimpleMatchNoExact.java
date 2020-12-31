/**
 * 
 */
package ac.technion.schemamatching.experiments.holistic;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.OBTermMatch;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.MappingPrinter;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchema;

/**
 * @author Tomer Sagi
 * Match 2 SchemataWith no exact match
 *
 */
public class SimpleMatchNoExact implements HolisticExperiment{
	public List<Statistic> runExperiment(Set<ExperimentSchema> eSet) 
	{
		List<Statistic> res = new ArrayList<Statistic>();
		OBTermMatch m = new OBTermMatch();
		//prepare all matches
		for (ExperimentSchema e1 : eSet)
			for (ExperimentSchema e2 : eSet)
			{
				if (e1.getID()!=e2.getID())
				{
					MappingPrinter mp = new MappingPrinter();
					MatchInformation mi = m.match(e1.getTargetOntology(), e2.getTargetOntology(),false);
					mp.init(e1.getID() + "," + e2.getID(), mi);
					res.add(mp);
				}				
			}
		return res;
	}

	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		return true;
}

	public String getDescription() {
		String desc = "Match between all combinations of schemata supplied and print result vector";
		return desc;
	}
	

}

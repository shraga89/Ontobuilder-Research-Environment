/**
 * 
 */
package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.K1Informed;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * @author Tomer Sagi
 * Profile the schema pair only. 
 */
public class SchemaPairProfiling implements PairWiseExperiment {

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment#runExperiment(ac.technion.schemamatching.testbed.ExperimentSchemaPair)
	 */
	@Override
	public List<Statistic> runExperiment(ExperimentSchemaPair esp) {
		K1Informed k1 = new K1Informed();
		k1.init("" + esp.getID(), esp.getExact());
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		res.add(k1);
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList, java.util.ArrayList)
	 */
	@Override
	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		//FLM and SLM are unused
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Runs K1 statistics on the pair";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment#summaryStatistics()
	 */
	@Override
	public List<Statistic> summaryStatistics() {
		//none
		return null;
	}

}

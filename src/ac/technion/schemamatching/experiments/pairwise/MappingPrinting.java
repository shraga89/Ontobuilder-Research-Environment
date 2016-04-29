/**
 * 
 */
package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsException;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.OBTermMatch;
import ac.technion.schemamatching.matchers.secondline.OBMaxDelta;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.MappingPrinter;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * @author Tomer Sagi
 * Generates mappings to be used to convert to goldenmappings
 *
 */
public class MappingPrinting implements PairWiseExperiment {

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.testbed.ExperimentSchemaPair)
	 */
	private boolean isMemory;

	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		MappingPrinter mp = new MappingPrinter();
		String instanceDescription = esp.getCandidateOntology().getName() + "2" + esp.getTargetOntology().getName();
		MatchInformation mi = esp.getSimilarityMatrix(new OBTermMatch(), isMemory);
		OBMaxDelta slm = new OBMaxDelta(0);
		mp.init(instanceDescription,slm.match(mi));
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		res.add(mp);
		try {
			mi.saveMatchToXML(esp.getID(), mi.getCandidateOntology().getName(), 
					mi.getTargetOntology().getName(),"./match.xml");
		} catch (SchemaMatchingsException e) {
			e.printStackTrace();
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {

		this.isMemory = isMemory;
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#summaryStatistics()
	 */
	public ArrayList<Statistic> summaryStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

}

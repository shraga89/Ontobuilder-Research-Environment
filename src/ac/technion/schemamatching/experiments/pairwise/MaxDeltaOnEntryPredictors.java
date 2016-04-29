package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.OBMaxDelta;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MatchCompetitorDeviationEntryLevel;
import ac.technion.schemamatching.statistics.MatchDistance;
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.VerboseBinaryGolden;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * Compare different instantiations of the MI supplied
 * to max delta by using entry predictors instead of the
 * raw confidence.
 * @author Tomer Sagi
 *
 */
public class MaxDeltaOnEntryPredictors implements PairWiseExperiment {
	private ArrayList<FirstLineMatcher> flM;
	private Properties properties;
	private boolean isMemory;

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		// Using all 1st line matchers 
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		for (FirstLineMatcher m : flM)
		{
			MatchInformation mi = null;
			mi = esp.getSimilarityMatrix(m, isMemory);
			
			//Calculate Non-Binary Precision and Recall
			K2Statistic nb = new NBGolden();
			String instanceDesc =  esp.getID() + "," + m.getName();
			nb.init(instanceDesc, mi,esp.getExact());
			evaluations.add(nb);

			//Calculate MatchDisatance
			K2Statistic md = new MatchDistance();
			md.init(instanceDesc, mi,esp.getExact());
			evaluations.add(md);
			
			//Run regular max Delta
			OBMaxDelta s1 = new OBMaxDelta(0.1);
			s1.init(properties);
			MatchInformation mi1 = s1.match(mi);
			
			//calculate Precision and Recall
			K2Statistic b2 = new BinaryGolden();
			instanceDesc =  esp.getID() + "," + m.getName() + ",Val," + s1.getConfig();
			b2.init(instanceDesc, mi1,esp.getExact());
			evaluations.add(b2);
			//Calculate MatchDistance
			K2Statistic md2 = new MatchDistance();
			md2.init(instanceDesc, mi1,esp.getExact());
			evaluations.add(md2);
			
			//Run max delta on MCD predictions
			OBMaxDelta s2 = new OBMaxDelta(0.1);
			s2.init(properties);
			MatchCompetitorDeviationEntryLevel mcd = new MatchCompetitorDeviationEntryLevel();
			mcd.init(instanceDesc, mi, mi);
			MatchInformation mi2 = s1.match(mcd.getValMI());
			

			//calculate Precision and Recall
			K2Statistic b3 = new BinaryGolden();
			instanceDesc =  esp.getID() + "," + m.getName() + ",MCD," + s2.getConfig();
			b3.init(instanceDesc, mi2,esp.getExact());
			evaluations.add(b3);
			//Calculate MatchDistance
			K2Statistic md3 = new MatchDistance();
			md3.init(instanceDesc, mi2,esp.getExact());
			evaluations.add(md3);
			//Calculate verbose
			K2Statistic v1 = new VerboseBinaryGolden();
			v1.init(instanceDesc, mi2,esp.getExact());
			evaluations.add(v1);
			
			
		}
		return evaluations;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		/*Using the supplied first line matcher list and second line matcher list allows run-time 
		changes to matchers used in the experiment*/
		this.flM = flM;
		this.isMemory = isMemory;
		//using property files allows to modify experiment parameters at runtime
		this.properties = properties;
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

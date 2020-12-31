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
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * @author Tomer Sagi
 * Interface for Pair-Wise Schema matching experiment where every
 * pair is matched and evaluated independently. 
 */
public interface PairWiseExperiment 
{
	/**
	 * Runs the experiment and calculates statistics during the experiment 
	 * and off experiment results.   
	 * @param esp Schema pair on which to run the experiment
	 * @return Statistics 
	 * associated with it.
	 */
	
	public List<Statistic>  runExperiment(ExperimentSchemaPair esp);
	/**
	 * Used to initialize a matching experiment. replaces a parameterized constructor
	 * @param properties configuration parameters for the matching experiment
	 * @param flm list of @link{FirstLineMatcher} with which the experiment is run
	 * @param slm list of @link{SecondLineMatcher} with which the experiment is run
	 * @param isMemory boolean if user want to use lookup method if exist
	 * @return true if initialization succeeded false otherwise
	 */
	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory);
	/**
	 * 
	 * @return Experiment Description
	 */
	public String getDescription();
	
	/**
	 * 
	 * @return Statistics sumarizing the experiment (for all schema pairs)
	 */
	public List<Statistic> summaryStatistics();
}

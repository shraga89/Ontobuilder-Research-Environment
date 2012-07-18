/**
 * 
 */
package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * @author Tomer Sagi
 *
 */
public interface MatchingExperiment 
{
	/**
	 * Runs the experiment and calculates statistics during the experiment 
	 * and off experiment results.   
	 * @param esp Schema pair on which to run the experiment
	 * @return Statistics 
	 * associated with it.
	 */
	
	public ArrayList<Statistic>  runExperiment(ExperimentSchemaPair esp);
	/**
	 * Used to initialize a matching experiment. replaces a parameterized constructor
	 * @param properties configuration parameters for the matching experiment
	 * @param flm list of @link{FirstLineMatcher} with which the experiment is run
	 * @param slm list of @link{SecondLineMatcher} with which the experiment is run 
	 * @return true if initialization succeeded false otherwise
	 */
	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM);
	/**
	 * 
	 * @return Experiment Description
	 */
	public String getDescription();
	
	/**
	 * 
	 * @return Statistics sumarizing the experiment (for all schema pairs)
	 */
	public ArrayList<Statistic> summaryStatistics();
}

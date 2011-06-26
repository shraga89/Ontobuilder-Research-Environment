/**
 * 
 */
package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

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
	 * @param om list of other matchers to which ontobuilder results should be compared
	 * @return true if initialization succeeded false otherwise
	 */
	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<OtherMatcher> om);
	/**
	 * 
	 * @return Experiment Description
	 */
	public String getDescription();
}

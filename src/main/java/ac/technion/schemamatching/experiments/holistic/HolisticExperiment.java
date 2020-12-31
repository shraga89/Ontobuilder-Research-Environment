/**
 * 
 */
package ac.technion.schemamatching.experiments.holistic;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchema;

/**
 * @author Tomer Sagi
 * Interface for Holistic Schema matching experiments where
 * a group of schemas are matched and evaluated together 
 */
public interface HolisticExperiment 
{
	/**
	 * Runs the experiment and calculates statistics during the experiment 
	 * and off experiment results.   
	 * @param hashSet 
	 * @return Statistics 
	 * associated with it.
	 */
	
	public List<Statistic>  runExperiment(Set<ExperimentSchema> hashSet);
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
	
}

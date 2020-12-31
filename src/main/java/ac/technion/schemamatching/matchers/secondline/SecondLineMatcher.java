/**
 * 
 */
package ac.technion.schemamatching.matchers.secondline;

import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * This interface is intended for usage in generating wrapper classes 
 * to existing matching algorithms in other systems.
 * A second line matcher is a matcher that recieves a similarity matrix
 * and returns a similarity matrix after applying some constraint or decision rule.  
 * @author Tomer Sagi
 *
 */
public interface SecondLineMatcher {

	/**
	 * Matcher Name
	 * @return String representing the matcher name
	 */
	public String getName();
	
	/**
	 * Main method of the matcher. 
	 * @param mi Ontology / Schema to be matched
	 * @return a MatchInformation object containing the similarity matrix created
	 */
	public MatchInformation match(MatchInformation mi);
	
	/**
	 * For matchers with configuration parameters. This method 
	 * returns the configuration currently set. 
	 * @return String describing the current configuration.
	 */
	public String getConfig();
	
	/**
	 * Return the schema matching database id of this matcher. 
	 * @return integer corresponding to the SMID field in the SimilarityMeasures table in the schema matching DB
	 */
	public int getDBid();
	
	/**
	 * Used to initialize the second line matcher. Should be invoked
	 * before any other method is invoked. 
	 * @param properties Properties object with parameters to be used 
	 * in the initialization. Refer to the individual documentation
	 * of each second line matcher for the required properties and
	 * acceptable values. 
	 * @return true if properties were properly initiated. False otherwise. 
	 */
	public boolean init (Properties properties);
}

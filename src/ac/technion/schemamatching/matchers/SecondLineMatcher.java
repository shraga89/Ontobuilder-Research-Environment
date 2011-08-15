/**
 * 
 */
package ac.technion.schemamatching.matchers;

import com.modica.ontology.match.MatchInformation;

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
}

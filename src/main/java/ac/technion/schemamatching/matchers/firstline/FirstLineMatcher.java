/**
 * 
 */
package ac.technion.schemamatching.matchers.firstline;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;

/**
 * This interface is intended for usage in generating wrapper classes 
 * to existing matching algorithms in other systems.
 * A first line matcher is a matcher that recieves two ontologies and returns 
 * a similarity matrix. 
 * Matchers that have built-in decision rules can return binary similarity matrices. 
 *  
 * @author Tomer Sagi
 *
 */
public interface FirstLineMatcher {
	
	/**
	 * Matcher Name
	 * @return String representing the matcher name
	 */
	public String getName();
	
	/**
	 * Should return true if the algorithm can be set to return a binary 
	 * similarity matrix rather than a real valued one. 
	 * @return
	 */
	public boolean hasBinary();
	
	/**
	 * Main method of the matcher. 
	 * @param candidate Ontology / Schema to be matched
	 * @param target Ontology / Schema to be matched
	 * @param binary If the algorithm can return a binary matrix
	 * then setting this parameter to true will cause it to do so. 
	 * @return a MatchInformation object containing the similarity matrix created
	 */
	public MatchInformation match(Ontology candidate,Ontology target,boolean binary);
	
	/**
	 * For matchers with configuration parameters. This method 
	 * returns the configuration currently set. 
	 * @return String describing the current configuration.
	 */
	public String getConfig();
	
	/**
	 * Return one the matcher type best describing this matcher
	 * @return
	 */
	public MatcherType getType();

	/**
	 * Return the schema matching database id of this matcher. 
	 * @return integer corresponding to the SMID field in the SimilarityMeasures table in the schema matching DB
	 */
	public int getDBid();
}

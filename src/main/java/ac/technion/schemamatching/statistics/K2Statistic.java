/**
 * 
 */
package ac.technion.schemamatching.statistics;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * @author Tomer Sagi
 *
 */
public interface K2Statistic extends Statistic {

	/**
	 * Initializes the statistic
	 * @param instanceDescription Description of current instance on which the statistic is calculated
	 * @param mi MatchInformation containing the similarity matrix to be compared with exact match
	 * @param exactMatch MatchInformation containing the correct correspondences
	 * @return true if initialization was successful
	 */
	boolean init(String instanceDescription, MatchInformation mi, MatchInformation exactMatch);
}

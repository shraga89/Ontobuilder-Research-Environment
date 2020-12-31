/**
 * 
 */
package ac.technion.schemamatching.ensembles;

import java.util.Map;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * @author Tomer Sagi
 * Interface class for schema matching ensemble methods
 *
 */
public interface Ensemble 
{
	/**
	 * Assumes only consensus matches are relevant. 
	 * Match list is intersection of matcher results. Score is 1
	 * MatchMatrix is binary: 1 if in match list, 0 otherwise
	 * @return
	 */
	MatchInformation getConcensusMatch();
	
	/**
	 * Match list is a majority vote where each matcher counts as 1 vote. Score is 1
	 * MatchMatrix is binary: 1 if in match list, 0 otherwise
	 * @return
	 */
	MatchInformation getMajorityVoteMatch();
	
	/**
	 * Match list is a majority vote where each matcher has a weight in vote. Score is 1 
	 * Majority is over 50% of sum(weights)
	 * MatchMatrix is binary: 1 if in match list, 0 otherwise
	 * @return
	 */
	MatchInformation getWeightedVoteMatch();
	
	/**
	 * Match list is Union of match lists. Score is weighted avg.  
	 * MatchMatrix is weighted avg. of Match Matrices.
	 * If no weights are supplied returns simple average 
	 * @return
	 */
	MatchInformation getWeightedMatch();
	
	/**
	 * Initialize ensemble generator
	 * @param matches HashMap of Matcher names -> MatchInformation objects with match results
	 * @param matcherWeights optional HashMap of Matcher names -> weights to be applied
	 * @return true if initialization succeeded
	 */
	boolean init(Map<String,MatchInformation> matches, Map<String,Double> matcherWeights);
	
	/**
	 * Returns the short name of the ensemble method
	 * @return
	 */
	String getName();
	
}

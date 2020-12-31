package ac.technion.schemamatching.experiments.holistic.network.ranking;

import java.util.List;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.schemamatching.experiments.holistic.network.SchemaNetwork;

/**
 * Interface for ranking strategies used to seek user feedback in a schema network
 * 
 * @author matthias weidlich
 *
 */
public interface Ranking {
	
	/**
	 * Returns a list of matches according to the implemented ranking strategy
	 * 
	 * @param network the schema network
	 * @param count the number of matches that we want to get using the ranking
	 * @param toExclude a set of matches that should be excluded from the ranking
	 * @return a ranked list of matches 
	 */
	public List<Match> rank(SchemaNetwork network, int count, Set<Match> toExclude);

}

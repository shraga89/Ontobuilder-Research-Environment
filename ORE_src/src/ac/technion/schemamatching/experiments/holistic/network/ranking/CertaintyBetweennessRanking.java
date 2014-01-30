package ac.technion.schemamatching.experiments.holistic.network.ranking;

import java.util.List;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.schemamatching.experiments.holistic.network.SchemaNetwork;
import ac.technion.schemamatching.experiments.holistic.network.NetworkStrengthFunctions.NetworkStrengthType;

/**
 * Ranking that relies on: 
 * <ul>
 * <li>a certainty strength function</li>
 * <li>betweeness centrality of attribute nodes</li>
 * </ul>
 * 
 * @author matthias weidlich
 * 
 */
public class CertaintyBetweennessRanking extends AbstractStrengthRanking implements Ranking {

	public List<Match> rank(SchemaNetwork network, int count, Set<Match> toExclude) {
		
		super.initRankingApproach(network, NetworkStrengthType.Certainty);
		return super.getMatchesSortedByBetweennessCentrality(count, toExclude);
	}
}

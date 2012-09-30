package ac.technion.schemamatching.experiments.holistic.nq;

import java.util.List;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.schemamatching.experiments.holistic.nq.NetworkStrengthFunctions.NetworkStrengthType;

public class IndecisivenessDegreeRanking extends AbstractRanking implements Ranking {

	public List<Match> rank(NetworkModel network, int count, Set<Match> toExclude) {
		
		super.initRankingApproach(network, NetworkStrengthType.Indecisiveness);
		return super.getMatchesSortedByDegreeCentrality(count, toExclude);
	}
}

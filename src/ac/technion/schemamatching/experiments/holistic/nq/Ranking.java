package ac.technion.schemamatching.experiments.holistic.nq;

import java.util.List;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.match.Match;

public interface Ranking {
	
	public List<Match> rank(NetworkModel network, int count, Set<Match> toExclude);

}

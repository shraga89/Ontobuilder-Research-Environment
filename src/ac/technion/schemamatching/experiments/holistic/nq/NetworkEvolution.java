package ac.technion.schemamatching.experiments.holistic.nq;

import java.util.List;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.match.Match;

public class NetworkEvolution {

	public static NetworkModel evolveNetwork(NetworkModel network, Ranking ranking, int steps, int validatedPerEval, Set<Match> toExclude) {
		
		int step = 0;
		while (step < steps) {
			System.out.print(steps - step + " ");
			/*
			 * Get the ranked matches for seeking feedback
			 */
			List<Match> sorted = ranking.rank(network,validatedPerEval, toExclude);
			toExclude.addAll(sorted);
			
			for (int i = 0; i < sorted.size(); i++) {
				Match m = sorted.get(i);
				/*
				 * Do validation to simulate user
				 */
				boolean isValid = network.getGoldStandard().getCopyOfMatches().contains(m);
				double newQuality = isValid ? 1.0 : 0.0;
				
				network.updateNetwork(m.getCandidateTerm(), m.getTargetTerm(), newQuality);
				step++;
			}
		}
		System.out.print("\n");
		return network;
	}
	
}

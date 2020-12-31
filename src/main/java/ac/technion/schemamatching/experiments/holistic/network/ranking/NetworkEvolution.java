package ac.technion.schemamatching.experiments.holistic.network.ranking;

import java.util.List;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.schemamatching.experiments.holistic.network.NetworkGoldStandardHandler;
import ac.technion.schemamatching.experiments.holistic.network.SchemaNetwork;

/**
 * Evolves a schema network by simulating user feedback. Matches are ranked according
 * to a ranking strategy and a user is simulated based on the gold standard.
 * 
 * @author matthias weidlich
 *
 */
public class NetworkEvolution {

	public static boolean verbose = true;
	
	
	/**
	 * Update the schema network based on simulated user feedback.
	 * 
	 * @param network the schema network
	 * @param ranking a ranking strategy to select the matches to seek feedback for
	 * @param steps the number of feedback steps, i.e., how many matches should be considered
	 * @param validatedPerEval the number of feedback steps after which the ranking is re-evaluated
	 * @param toExclude matches to exclude when doing the ranking
	 * @return the updated schema network
	 */
	public static SchemaNetwork evolveNetwork(SchemaNetwork network, Ranking ranking, int steps, int validatedPerEval, Set<Match> toExclude) {
		
		int step = 0;
		while (step < steps) {
			if (verbose)
				System.out.print(steps - step + " ");
			
			/*
			 * Get the ranked matches for seeking feedback
			 */
			List<Match> sorted = ranking.rank(network,validatedPerEval, toExclude);
			toExclude.addAll(sorted);
			
			for (int i = 0; i < sorted.size(); i++) {
				Match m = sorted.get(i);
				
				NetworkGoldStandardHandler gold = network.getGoldStandardHandler();
				
				if (gold.termsAreMatched(m.getCandidateTerm(),m.getTargetTerm())) {
					/*
					 * We have the gold standard, return 1.0 if correct and 0.0 otherwise
					 */
					if (gold.isCorrect(m))
						network.updateNetwork(m.getCandidateTerm(), m.getTargetTerm(), 1.0d);
					else
						network.updateNetwork(m.getCandidateTerm(), m.getTargetTerm(), 0.0d);
				}
				else {
					/*
					 * What to do if we do not know whether the match is correct?
					 */
					network.updateNetwork(m.getCandidateTerm(), m.getTargetTerm(), 0.5d);
				}
				
				step++;
			}
		}
		if (verbose)
			System.out.print("\n");
		
		return network;
	}
	
}

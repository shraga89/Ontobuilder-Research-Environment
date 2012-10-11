package ac.technion.schemamatching.experiments.holistic.network.ranking;

import java.util.List;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.experiments.holistic.network.SchemaNetwork;
import ac.technion.schemamatching.testbed.ExperimentSchema;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

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
				
				double newQuality = doValidationToGetNewProbability(network, m);
				
				network.updateNetwork(m.getCandidateTerm(), m.getTargetTerm(), newQuality);
				step++;
			}
		}
		if (verbose)
			System.out.print("\n");
		
		return network;
	}
	
	private static double doValidationToGetNewProbability(SchemaNetwork network, Match m) {
		ExperimentSchema s1 = network.getTermToSchemaMap().get(m.getCandidateTerm());
		ExperimentSchema s2 = network.getTermToSchemaMap().get(m.getTargetTerm());
		
		ExperimentSchemaPair pair12 = OBExperimentRunner.getOER().getDoc().getPair(s1,s2);
		ExperimentSchemaPair pair21 = OBExperimentRunner.getOER().getDoc().getPair(s2,s1);
		
		/*
		 * What to do if we do not know whether the match is correct?
		 */
		if ((pair12.getExact() == null) && (pair21.getExact() == null))
			return 0.5d;
		
		/*
		 * We have the gold standard, return 1.0 if correct
		 */
		if (pair12.getExact() != null)
			if (pair12.getExact().getCopyOfMatches().contains(m))
				return 1.0d;
		if (pair21.getExact() != null)
			if (pair21.getExact().getCopyOfMatches().contains(m))
				return 1.0d;
		
		/*
		 * We have the gold standard, return 0.0 if not correct
		 */
		return 0.0d;
	}
	
	
}

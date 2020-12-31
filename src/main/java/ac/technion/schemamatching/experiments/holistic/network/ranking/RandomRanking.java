package ac.technion.schemamatching.experiments.holistic.network.ranking;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.schemamatching.experiments.holistic.network.SchemaNetwork;
import ac.technion.schemamatching.testbed.ExperimentSchema;

/**
 * Baseline ranking that does a simple random selection of matches
 * 
 * @author matthias weidlich
 *
 */
public class RandomRanking implements Ranking {

	public List<Match> rank(SchemaNetwork network, int count, Set<Match> toExclude) {
		List<Match> result = new ArrayList<Match>();
		
		while (result.size() < count){
			ExperimentSchema s1 = network.pickRandomSchema();
			ExperimentSchema s2 = network.pickRandomSchemaNotTheGivenOne(s1);
			Term t1 = pickRandomTermFromSchema(s1);
			Term t2 = pickRandomTermFromSchema(s2);
			Match m1 = new Match(t1, t2, 1.0d);
			Match m2 = new Match(t2, t1, 1.0d);
			if (!result.contains(m1) 
					&& !result.contains(m2)
					&& !toExclude.contains(m1)
					&& !toExclude.contains(m2))
				result.add(m1);
		}
		
		return result;
	}

	private Term pickRandomTermFromSchema(ExperimentSchema s) {
		Vector<Term> terms = s.getTargetOntology().getTerms(true);
		int r = new Random().nextInt(terms.size()); 
		int i = 0;
		for(Term t : terms) {
		    if (i == r) return t;
		    i++;
		}
		return null;
	}

}

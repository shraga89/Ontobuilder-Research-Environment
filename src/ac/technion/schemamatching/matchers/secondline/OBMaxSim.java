package ac.technion.schemamatching.matchers.secondline;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Simple second line matcher that selects for each attribute in one 
 * schema, one attribute in the other schema, and vice versa. The 
 * selection will always take the attribute with the highest similarity
 * and implements a random choice if there are multiple candidates
 * for an attribute (i.e., multiple attributes have the same maximal
 * similarity).
 * 
 * @author matthias weidlich
 */
public class OBMaxSim implements SecondLineMatcher {

	@Override
	public String getName() {
		return "OB Max Sim";
	}
	
	public double r(double v){
		return Math.round(v*1000.0)/1000.0; 
	}

	@Override
	public MatchInformation match(MatchInformation mi) {		
		MatchInformation res = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
		
		Map<Term, Double> maxForCandidates = new HashMap<>();
		Map<Term, Double> maxForTargets = new HashMap<>();
		
		Vector<Term> candidates = mi.getCandidateOntology().getTerms(true);
		Vector<Term> targets = mi.getTargetOntology().getTerms(true);

		for (Term t : candidates)
			maxForCandidates.put(t, 0.0);
		for (Term t : targets)
			maxForTargets.put(t, 0.0);
		
		for (Match m : mi.getCopyOfMatches()) {
			maxForCandidates.put(m.getCandidateTerm(), Math.max(maxForCandidates.get(m.getCandidateTerm()), r(m.getEffectiveness())));
			maxForTargets.put(m.getTargetTerm(), Math.max(maxForTargets.get(m.getTargetTerm()), r(m.getEffectiveness())));
		}
		
		Random rand = new Random();
//		System.out.println(rand.nextInt(targets.size()) + "Check candidate: " + maxForCandidates.keySet().size() + " of " + mi.getCandidateOntology().getTerms(true).size());
//		System.out.println(rand.nextInt(targets.size()) + "Check targets: " + maxForTargets.keySet().size() + " of " + mi.getTargetOntology().getTerms(true).size());
		
		for (Term t : candidates) {
			Term match = null;
			Double eff = 0.0;
			for (Match m : mi.getCopyOfMatches()) {
				if (m.getCandidateTerm().equals(t) && r(m.getEffectiveness()) == r(maxForCandidates.get(t))) {
					match = m.getTargetTerm();
					eff = m.getEffectiveness();
				}
			}
			
			if (match == null) {
				match = targets.get(rand.nextInt(targets.size()));
			}
			res.updateMatch(t, match, eff);
		}
		for (Term t : targets) {
			Term match = null;
			Double eff = 0.0;
			for (Match m : mi.getCopyOfMatches()) {
				if (m.getTargetTerm().equals(t) && r(m.getEffectiveness()) == r(maxForTargets.get(t))) {
					match = m.getCandidateTerm();
					eff = m.getEffectiveness();
				}
			}
			
			if (match == null) {
				match = candidates.get(rand.nextInt(candidates.size()));
			}
			res.updateMatch(match, t, eff);
		}

		return res;
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Override
	public int getDBid() {
		return 11;
	}

}

/**
 * 
 */
package ac.technion.schemamatching.matchers.firstline;

import java.util.Vector;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;

/**
 * @author Tomer Sagi
 * This simplistic matcher is intended as a tutorial for authoring
 * first line matchers in ORE. 
 * The matcher iterates over the candidate and target term lists
 * and matches the i-th term in each to each other with confidence
 * 1.0. It stops when either list runs out. 
 */
public class TermOrder implements FirstLineMatcher {

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getName()
	 */
	@Override
	public String getName() {
		return "Term Order";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#hasBinary()
	 */
	@Override
	public boolean hasBinary() {
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#match(ac.technion.iem.ontobuilder.core.ontology.Ontology, ac.technion.iem.ontobuilder.core.ontology.Ontology, boolean)
	 */
	@Override
	public MatchInformation match(Ontology candidate, Ontology target,
			boolean binary) {
		Vector<Term> cTerms = candidate.getTerms(true);
		Vector<Term> tTerms = target.getTerms(true);
		MatchInformation res = new MatchInformation(candidate,target); 
		for (int i=0; i<Math.min(cTerms.size(), tTerms.size());i++)
			res.updateMatch(tTerms.get(i), cTerms.get(i), 1.0);
		candidate.removeTerm(candidate.getTerm(0));//new addition by Roee
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getConfig()
	 */
	@Override
	public String getConfig() {
		return "no configurable parameters";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getType()
	 */
	@Override
	public MatcherType getType() {
		return MatcherType.STRUCTURAL_SIBLING;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getDBid()
	 */
	@Override
	public int getDBid() {
		return 16;
	}

}

/**
 * 
 */
package ac.technion.schemamatching.matchers.firstline;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;

/**
 * @author Anna Margolin
 * WIP: Instance based matcher, based on entity resolution
 */
public class InstanceMatcher implements FirstLineMatcher {

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getName()
	 */
	public String getName() {
		return "Instance Matcher";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#hasBinary()
	 */
	public boolean hasBinary() {
		return false;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#match(ac.technion.iem.ontobuilder.core.ontology.Ontology, ac.technion.iem.ontobuilder.core.ontology.Ontology, boolean)
	 */
	public MatchInformation match(Ontology candidate, Ontology target,
			boolean binary) {
		
		File f = candidate.getFile();
		String fileDir = f.getParent();
		File candidateInstancefile = new File(fileDir,candidate.getName()+".xml");
		Vector<Term> cTerms = candidate.getTerms(true);
		ArrayList<String> candidateTermList = new ArrayList<String>();
		for (Term t : cTerms)
		{
			String tProvenance = t.getProvenance();
			candidateTermList.add(tProvenance);
		}
		// TODO run algorithm IMAnna im = new IMAnna(candidateTermList, candidateInstancefile, targetTermList, targetInstanceFile);
		// TODO algoRes = im.getRes();
		ArrayList<Match> algoRes = new ArrayList<Match>();
		
		//fill result object
		MatchInformation res = new MatchInformation(candidate,target);
		for (Match m : algoRes)
		{
			res.updateMatch(m.getTargetTerm(), m.getCandidateTerm(), m.getEffectiveness());
		}
		
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getConfig()
	 */
	public String getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getType()
	 */
	public MatcherType getType() {
		return MatcherType.INSTANCE;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getDBid()
	 */
	public int getDBid() {
		return 13;
	}

}

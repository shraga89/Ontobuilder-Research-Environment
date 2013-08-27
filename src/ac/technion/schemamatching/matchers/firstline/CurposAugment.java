package ac.technion.schemamatching.matchers.firstline;

import java.util.ArrayList;
import java.util.Vector;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.schemamatching.matchers.MatcherType;

public class CurposAugment implements FirstLineMatcher {

	@Override
	public String getName() {
		return "Curpos Augment";
	}

	@Override
	public boolean hasBinary() {
		return false;
	}

	@Override
	public MatchInformation match(Ontology candidate, Ontology target,
			boolean binary) {
		
		MatchInformation res = new MatchInformation(candidate,target);
		Vector<Term> candList = candidate.getTerms(true);
		Vector<Term> targList = target.getTerms(true);
		
		
		
		
		// TODO Auto-generated method stub
		
		//res.setMatrix(mm);
		return res;
	}

	@Override
	public String getConfig() {
		String config = "default";
		return config;
	}

	@Override
	public MatcherType getType() {
		return MatcherType.CORPUS;
	}

	@Override
	public int getDBid() {
		// TODO Auto-generated method stub
		return 0;
	}

}

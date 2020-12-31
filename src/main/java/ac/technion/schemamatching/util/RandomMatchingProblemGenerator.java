

package ac.technion.schemamatching.util;

import java.util.ArrayList;
import java.util.HashSet;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;


public class RandomMatchingProblemGenerator {

	
	private MatchInformation exactMatching;
	private Ontology candOntology;
	private Ontology targetOntology;
	
	
	/**
	 * @param exactMatching
	 * @param candOntology
	 * @param targetOntology
	 */
	public RandomMatchingProblemGenerator(MatchInformation exactMatching,
			Ontology candOntology, Ontology targetOntology) {
		this.exactMatching = exactMatching;
		this.candOntology = candOntology;
		this.targetOntology = targetOntology;
	}
	
	public RandomMatchingProblemInstance generateProblem(int size){
//		int numMatchings = exactMatching.getMatchedAttributesPairsCount();
		//generate exact matching
		ArrayList<Match> pairs = exactMatching.getCopyOfMatches();
		ArrayList<Match> chosenPairs = new ArrayList<Match>();
		int index;
		HashSet<Integer> chosenIndexs = new HashSet<Integer>();
		for (int i=0;i<size;){
			index = (int)Math.floor(Math.random()*pairs.size());
			if (chosenIndexs.contains(new Integer(index))){
				continue;
			}else{
				chosenPairs.add(pairs.get(index));
				chosenIndexs.add(new Integer(index));
				i++;
			}
		}
		
		
		//generate ontologies
		Ontology cand = new Ontology(candOntology.getName());
		Ontology target = new Ontology(targetOntology.getName());
//		MatchedAttributePair pair;
		for (Match m : pairs){
			long cid = m.getCandidateTerm().getId();
			if (cand.getTermByID(cid)!=null) cand.addTerm(candOntology.getTermByID(cid));
			long tid = m.getTargetTerm().getId();
			if (target.getTermByID(tid)!=null) target.addTerm(targetOntology.getTermByID(tid));	
		}
		
		MatchInformation exact = new MatchInformation(cand,target);
		exact.setMatches(chosenPairs);
		return new RandomMatchingProblemInstance(exact, cand, target, chosenPairs);
	}
}

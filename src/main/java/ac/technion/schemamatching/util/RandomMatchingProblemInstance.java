

package ac.technion.schemamatching.util;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;


public class RandomMatchingProblemInstance {

	private MatchInformation exactMatching;
	private Ontology candOntology;
	private Ontology targetOntology;
	private ArrayList<Match> exactPairs;
	
	
	/**
	 * @param exactMatching
	 * @param candOntology
	 * @param targetOntology
	 */
	public RandomMatchingProblemInstance(MatchInformation exactMatching,
			Ontology candOntology, Ontology targetOntology, ArrayList<Match> exactPairs) {
		this.exactMatching = exactMatching;
		this.candOntology = candOntology;
		this.targetOntology = targetOntology;
		this.exactPairs = exactPairs;
	}
	
	
	/**
	 * @return Returns the candOntology.
	 */
	public Ontology getCandOntology() {
		return candOntology;
	}
	/**
	 * @return Returns the exactMatching.
	 */
	public MatchInformation getExactMatching() {
		return exactMatching;
	}
	/**
	 * @return Returns the targetOntology.
	 */
	public Ontology getTargetOntology() {
		return targetOntology;
	}
	
	/**
	 * @return Returns the exactPairs.
	 */
	public ArrayList<Match> getExactPairs() {
		return exactPairs;
	}
}

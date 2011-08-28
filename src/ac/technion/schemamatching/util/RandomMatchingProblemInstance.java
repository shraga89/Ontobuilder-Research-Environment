

package ac.technion.schemamatching.util;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;


public class RandomMatchingProblemInstance {

	private SchemaTranslator exactMatching;
	private Ontology candOntology;
	private Ontology targetOntology;
	private MatchedAttributePair[] exactPairs;
	
	
	/**
	 * @param exactMatching
	 * @param candOntology
	 * @param targetOntology
	 */
	public RandomMatchingProblemInstance(SchemaTranslator exactMatching,
			Ontology candOntology, Ontology targetOntology, MatchedAttributePair[] exactPairs) {
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
	public SchemaTranslator getExactMatching() {
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
	public MatchedAttributePair[] getExactPairs() {
		return exactPairs;
	}
}

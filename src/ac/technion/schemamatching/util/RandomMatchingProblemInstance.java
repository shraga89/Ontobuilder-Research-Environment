

package ac.technion.schemamatching.util;

import schemamatchings.meta.match.MatchedAttributePair;
import schemamatchings.util.SchemaTranslator;

import com.modica.ontology.Ontology;


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

/**
 * 
 */
package ac.technion.schemamatching.matchers;

import com.modica.ontology.Ontology;
import com.modica.ontology.algorithm.PrecedenceAlgorithm;
import com.modica.ontology.match.MatchInformation;

/**
 * Wrapper for default configurated Precedence Match
 * @author Tomer Sagi
 *
 */
public class OBPrecedenceMatch implements FirstLineMatcher {
	PrecedenceAlgorithm ga = new PrecedenceAlgorithm(); 
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getName()
	 */
	public String getName() {
		return "Ontobuilder Precedence Match";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#hasBinary()
	 */
	public boolean hasBinary() {
		return false;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#match(com.modica.ontology.Ontology, com.modica.ontology.Ontology, boolean)
	 */
	public MatchInformation match(Ontology o1, Ontology o2, boolean binary) { 
		return ga.match(o1,o2);
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getConfig()
	 */
	public String getConfig() { 
		String config = "default";
		return config;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getType()
	 */
	public MatcherType getType() {
		return MatcherType.STRUCTURAL_SIBLING;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getDBid()
	 */
	public int getDBid() {
		return 5;
	}


}

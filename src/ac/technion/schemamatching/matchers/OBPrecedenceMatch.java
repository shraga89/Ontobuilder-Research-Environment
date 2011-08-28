/**
 * 
 */
package ac.technion.schemamatching.matchers;


import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapperException;
import ac.technion.schemamatching.experiments.OBExperimentRunner;


/**
 * Wrapper for default configurated Precedence Match
 * @author Tomer Sagi
 *
 */
public class OBPrecedenceMatch implements FirstLineMatcher { 
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
	public MatchInformation match(Ontology candidate, Ontology target, boolean binary) { 
		OntoBuilderWrapper obw = OBExperimentRunner.getOER().getOBW();
		MatchInformation res = null;
		try {
			res = obw.matchOntologies(candidate, target, MatchingAlgorithmsNamesEnum.PRECEDENCE.toString());
		} catch (OntoBuilderWrapperException e) {
			e.printStackTrace();
		}
		return res;
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

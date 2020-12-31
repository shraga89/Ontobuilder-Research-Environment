/**
 * 
 */
package ac.technion.schemamatching.matchers.firstline;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapperException;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.MatcherType;

/**
 * @author Itzik Ben Ezra and Eilon Shitrit.
 *
 */
public class DomainMatch implements FirstLineMatcher {

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getName()
	 */
	public String getName() {
		return "Domain Matcher";
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
	public MatchInformation match(Ontology candidate, Ontology target,boolean binary) {
		System.out.println("Domain Wrapper - match()");
		OntoBuilderWrapper obw = OBExperimentRunner.getOER().getOBW();
		MatchInformation res = null;
		try {
//			res = obw.matchOntologies(candidate, target, MatchingAlgorithmsNamesEnum.VALUE.getName());
			res = obw.matchOntologies(candidate, target, MatchingAlgorithmsNamesEnum.DOMAIN.getName());
		} catch (OntoBuilderWrapperException e) {
			e.printStackTrace();
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getConfig()
	 */
	public String getConfig() {
		String config = "default";
		return config;
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
		return 14;
	}

}

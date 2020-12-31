/**
 * 
 */
package ac.technion.schemamatching.matchers.firstline;

import java.util.HashMap;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.utils.AlgorithmXMLEditor;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapperException;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.MatcherType;

/**
 * Wrapper for default configurated Ontobuilder Term-Value Matcher
 * @author Tomer Sagi
 *
 */
public class OBTermValueMatch implements FirstLineMatcher {
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getName()
	 */
	private double termWeight = 0.8;
	private double valueWeight = 0.2;
	
	/**
	 * Parameterized constructor, edits algorithm.xml file and sets the relevant parameters
	 * Value weight = 1 - Term Weight
	 * @param nGramWeight
	 */
	public OBTermValueMatch(double termWeight)
	{
		this.termWeight = termWeight;
		this.valueWeight = 1 - termWeight;
		HashMap<String,Object> parameterValues = new HashMap<>(); 
		parameterValues.put("termWeight", termWeight);
		parameterValues.put("valueWeight", valueWeight);
		try {
			AlgorithmXMLEditor.updateAlgorithmParams("Term and Value Match",parameterValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public OBTermValueMatch() {}

	public String getName() {
		return "Ontobuilder Term-Value Matcher";
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
			res = obw.matchOntologies(candidate, target, MatchingAlgorithmsNamesEnum.TERM_VALUE_COMBINED.getName());
		} catch (OntoBuilderWrapperException e) {
			e.printStackTrace();
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getConfig()
	 */
	public String getConfig() { 
		String config = "TermWeight=" + Double.toString(termWeight)
						+ ";ValueWeight=" + Double.toString(valueWeight);
		return config;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getType()
	 */
	public MatcherType getType() {
		return MatcherType.SYNTACTIC;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getDBid()
	 */
	public int getDBid() {
		return 2;
	}

}

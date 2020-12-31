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
public class OBCombined implements FirstLineMatcher {
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getName()
	 */
	private double termWeight = 0.5;
	private double valueWeight = 0.1;
	private double graphWeight = 0.1;
	private double precedenceWeight = 0.3;
	
	/**
	 * Parameterized constructor, edits algorithm.xml file and sets the relevant parameters
	 * precedence weight = 1 - (termWeight+valueWeight+graphWeight)
	 * @param termWeight weight given to results of Term algorithm
	 * @param valueWeight weight given to result of Value algorithm
	 * @param graphWeight weight given to result of Graph algorithm
	 */
	public OBCombined(double termWeight, double valueWeight, double graphWeight)
	{
		this.termWeight = termWeight;
		this.valueWeight = valueWeight;
		this.graphWeight = graphWeight;
		this.precedenceWeight = 1- (termWeight + valueWeight + graphWeight);
		HashMap<String,Object> parameterValues = new HashMap<>(); 
		parameterValues.put("termWeight", termWeight);
		parameterValues.put("valueWeight", valueWeight);
		parameterValues.put("graphWeight", graphWeight);
		parameterValues.put("precedenceWeight", precedenceWeight);
		try {
			AlgorithmXMLEditor.updateAlgorithmParams("Combined Match",parameterValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public OBCombined() {}

	public String getName() {
		return "Ontobuilder Combined Matcher";
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
			res = obw.matchOntologies(candidate, target, MatchingAlgorithmsNamesEnum.TERM_VALUE_PRECEDENCE_COMPOSITION_COMBINED.getName());
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
						+ ";ValueWeight=" + Double.toString(valueWeight)
						+ ";GraphWeight=" + Double.toString(graphWeight)
						+ ";precedenceWeight=" + Double.toString(precedenceWeight);
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
		return 3;
	}

}

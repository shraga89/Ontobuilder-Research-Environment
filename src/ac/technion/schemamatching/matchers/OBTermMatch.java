/**
 * 
 */
package ac.technion.schemamatching.matchers;

import java.util.HashMap;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.utils.AlgorithmXMLEditor;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapperException;
import ac.technion.schemamatching.experiments.OBExperimentRunner;

/**
 * Wrapper for default configurated Ontobuilder Term Match
 * @author Tomer Sagi
 *
 */
public class OBTermMatch implements FirstLineMatcher {
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getName()
	 */
	private double weightMaxSubString = 0.5;
	private double weightNGram = 0.5;
	
	/**
	 * Parameterized constructor, edits algorithm.xml file and sets the relevant parameters
	 * with nGram weight and maxSubStringWeight (1- nGramWeight)
	 *  
	 * @param nGramWeight
	 */
	public OBTermMatch(double nGramWeight)
	{
		weightMaxSubString = 1-nGramWeight;
		weightNGram = nGramWeight;
		HashMap<String,Double> parameterValues = new HashMap<String,Double>(); 
		parameterValues.put("nGramWeight", weightNGram);
		parameterValues.put("maxCommonSubStringWeight", weightMaxSubString);
		try {
			AlgorithmXMLEditor.updateAlgorithmParams("Term",parameterValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public OBTermMatch() {}

	public String getName() {
		return "Ontobuilder Term Match";
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
			res = obw.matchOntologies(candidate, target, MatchingAlgorithmsNamesEnum.TERM.toString());
		} catch (OntoBuilderWrapperException e) {
			e.printStackTrace();
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getConfig()
	 */
	public String getConfig() { 
		String config = "NGram=" + Double.toString(weightNGram)+ ";MaxSubStr=" + Double.toString(weightMaxSubString);
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
		return 0;
	}

}

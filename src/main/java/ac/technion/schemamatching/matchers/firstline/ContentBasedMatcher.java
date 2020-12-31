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

public class ContentBasedMatcher implements FirstLineMatcher{
	
	private double weightMaxSubString = 0;
	private double weightNGram = 0.1;
	private double weightJaroWinkler = 0.9;
	private double stringNameWeight = 0.5;
	private double wordNameWeight = 0.5;
	private double stringLabelWeight = 0;
	private double wordLabelWeight = 0;
	
	public ContentBasedMatcher(double nGramWeight, double jaroWinklerWeight, double wordNameWeight,double stringNameWeight ,double stringLabelWeight, double wordLabelWeight)
	{
		weightMaxSubString = 1-nGramWeight- jaroWinklerWeight;
		weightNGram = nGramWeight;
		weightJaroWinkler = jaroWinklerWeight;
		this.wordLabelWeight = wordLabelWeight;
		this.stringLabelWeight = stringLabelWeight;
		this.wordNameWeight = wordNameWeight;
		this.stringNameWeight = stringNameWeight;
		HashMap<String,Object> parameterValues = new HashMap<>(); 
		parameterValues.put("nGramWeight", weightNGram);
		parameterValues.put("maxCommonSubStringWeight", weightMaxSubString);
		parameterValues.put("jaroWinklerWeight",weightJaroWinkler);
		parameterValues.put("wordLabelWeight",wordLabelWeight);
		parameterValues.put("stringLabelWeight",stringLabelWeight);
		parameterValues.put("wordNameWeight",wordNameWeight);
		parameterValues.put("stringNameWeight",stringNameWeight);
		try {
			AlgorithmXMLEditor.updateAlgorithmParams("Term Match",parameterValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ContentBasedMatcher() {}
		/* (non-Javadoc)
		 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getName()
		 */
		public String getName() {
			return "ContentBased Matcher";
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
			System.out.println("Content-Based Wrapper - match()");
			OntoBuilderWrapper obw = OBExperimentRunner.getOER().getOBW();
			MatchInformation res = null;
			try {
				res = obw.matchOntologies(candidate, target, MatchingAlgorithmsNamesEnum.CONTEND_BASED.getName());
			} catch (OntoBuilderWrapperException e) {
				e.printStackTrace();
			}
			return res;
		}

		/* (non-Javadoc)
		 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getConfig()
		 */
		public String getConfig() {
			String config = "NGram=" + Double.toString(weightNGram)
					+ ";MaxSubStr=" + Double.toString(weightMaxSubString)
					+ ";weightJaroWinkler=" + Double.toString(weightJaroWinkler)
					+ ";wordLabelWeight=" + Double.toString(wordLabelWeight)
					+ ";stringLabelWeight=" + Double.toString(stringLabelWeight)
					+ ";wordNameWeight=" + Double.toString(wordNameWeight)
					+ ";stringNameWeight=" + Double.toString(stringNameWeight)
					+ ";weightJaroWinkler=" + Double.toString(weightJaroWinkler);
	return config;
//			String config = "default";
//			return config;
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
			return 21;
		}

}

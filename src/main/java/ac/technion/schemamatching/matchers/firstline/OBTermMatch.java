package ac.technion.schemamatching.matchers.firstline;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.resources.OntoBuilderResources;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmException;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.misc.AlgorithmUtilities;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermAlgorithm;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;

import java.io.InputStream;

/**
 * Wrapper for default configurated Ontobuilder Term Match
 * @author Tomer Sagi
 *
 */
public class OBTermMatch implements FirstLineMatcher {
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getName()
	 */
	private double weightMaxSubString = 0.4;
	private double weightNGram = 0.4;
	private double weightJaroWinkler = 0.2;
	private double stringNameWeight = 0.25;
	private double wordNameWeight = 0.25;
	private double stringLabelWeight = 0.25;
	private double wordLabelWeight = 0.25;
	private boolean useSoundex = true;
	private short useAverage = 1;
	private short nGram = 2;
	private TermAlgorithm ta;
	
	/**
	 * Parameterized constructor, edits algorithm.xml file and sets the relevant parameters
	 * with nGram weight, jaroWinklerWeight and maxSubStringWeight (1- nGramWeight - jaroWinklerWeight)
	 *  
	 * @param nGramWeight
	 */
	public OBTermMatch(double nGramWeight, double jaroWinklerWeight, double wordNameWeight,double stringNameWeight ,double stringLabelWeight, double wordLabelWeight, boolean useSoundex, short useAverage, short nGram)
	{
		this();
		weightMaxSubString = 1-nGramWeight- jaroWinklerWeight;
		weightNGram = nGramWeight;
		weightJaroWinkler = jaroWinklerWeight;
		this.wordLabelWeight = wordLabelWeight;
		this.stringLabelWeight = stringLabelWeight;
		this.wordNameWeight = wordNameWeight;
		this.stringNameWeight = stringNameWeight;
		this.useSoundex = useSoundex;
		this.useAverage = useAverage;
		this.nGram = nGram;
		try {
			ta.setLabelWeights(this.stringLabelWeight, this.wordLabelWeight);
			ta.setMaxCommonSubStringWeight(weightMaxSubString);
			ta.setNameWeights(this.stringNameWeight, this.wordNameWeight);
			ta.setNGram(this.nGram);
			ta.setNGramWeight(this.weightNGram);
			ta.setuseAverage(this.useAverage);
			int mode = 0;
			if (this.useSoundex)
				mode=4;
			
			ta.setMode(mode);
			
		} catch (AlgorithmException e) {
			e.printStackTrace();
		}
		
		
//		HashMap<String,Object> parameterValues = new HashMap<>(); 
//		parameterValues.put("nGramWeight", weightNGram);
//		parameterValues.put("maxCommonSubStringWeight", weightMaxSubString);
//		parameterValues.put("jaroWinklerWeight",weightJaroWinkler);
//		parameterValues.put("wordLabelWeight",wordLabelWeight);
//		parameterValues.put("stringLabelWeight",stringLabelWeight);
//		parameterValues.put("wordNameWeight",wordNameWeight);
//		parameterValues.put("stringNameWeight",stringNameWeight);
//		parameterValues.put("useSoundex",new Boolean(this.useSoundex));
//		parameterValues.put("useAverage",new Boolean(this.useAverage));
//		parameterValues.put("nGram",new Short(this.nGram));
//		
//		try {
//			AlgorithmXMLEditor.updateAlgorithmParams("Term Match",parameterValues);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public OBTermMatch() {
		try {
			InputStream algorithmXML = getClass().getClassLoader().getResourceAsStream(OntoBuilderResources.Config.Matching.ALGORITHMS_XML);
			ta = (TermAlgorithm)AlgorithmUtilities.getAlgorithmsInstance(algorithmXML,MatchingAlgorithmsNamesEnum.TERM.getName());
		} catch (AlgorithmException e) {
			e.printStackTrace();
			ta = new TermAlgorithm();
		}
		}

	public OBTermMatch(double weightNGram2, double weightJaro,
			double wordNameWeight2, double stringNameWeight2,
			double stringLabelWeight2, double wordLabelWeight2) {
		this(weightNGram2,weightJaro,wordNameWeight2,stringNameWeight2,stringLabelWeight2,wordLabelWeight2,
				true,(short) 1,(short) 2);
	}

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
		MatchInformation res;
		
//					res = obw.matchOntologies(candidate, target, MatchingAlgorithmsNamesEnum.TERM.getName());
		res = ta.match(target, candidate);
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getConfig()
	 */
	public String getConfig() {

		return "NGramW=" + weightNGram
						+ ";MaxSubStr=" + weightMaxSubString
						+ ";weightJaroWinkler=" + weightJaroWinkler
						+ ";wordLabelWeight=" + wordLabelWeight
						+ ";stringLabelWeight=" + stringLabelWeight
						+ ";wordNameWeight=" + wordNameWeight
						+ ";stringNameWeight=" + stringNameWeight
						+ ";weightJaroWinkler=" + weightJaroWinkler;
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

/**
 * 
 */
package ac.technion.schemamatching.matchers;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingsUtilities;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * @author Tomer Sagi
 * Ontobuilder Research Environment wrapper for Threshold second line matcher
 */
public class OBThreshold implements SecondLineMatcher {

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getName()
	 */
	public String getName() {
		return "Ontobuilder Threshold";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#match(ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	public MatchInformation match(MatchInformation mi) {
		MatchInformation miTH = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology()); 
		SchemaTranslator tmp = new SchemaTranslator(mi);
		SchemaTranslator th = SchemaMatchingsUtilities.getSTwithThresholdSensitivity(tmp, threshold);
		miTH.setMatches(th.toOntoBuilderMatchList(miTH.getMatrix()));
		ConversionUtils.zeroNonMatched(miTH);
		return miTH;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getConfig()
	 */
	public String getConfig() {
		return "Threshold:" + Double.toString(threshold);
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getDBid()
	 */
	public int getDBid() {
		return 7;
	}

	/**
	 * Sets threshold to supplied value
	 * @param threshold
	 */
	public OBThreshold(double threshold)
	{
		this.threshold = threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public double getThreshold() {
		return threshold;
	}
	private double threshold = 0.25;
}

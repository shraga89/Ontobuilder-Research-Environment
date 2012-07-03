/**
 * 
 */
package ac.technion.schemamatching.matchers.secondline;

import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.simple.Threshold2LM;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * @author Tomer Sagi
 * Ontobuilder Research Environment wrapper for Threshold second line matcher
 */
public class OBThreshold implements SecondLineMatcher {

	private Threshold2LM my2LM;
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
		return my2LM.match(mi);
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
		this.my2LM = new Threshold2LM(threshold);
		Properties p = new Properties();
		p.setProperty("t", Double.toString(threshold));
		my2LM.init(p);
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

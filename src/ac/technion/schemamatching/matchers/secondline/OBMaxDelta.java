/**
 * 
 */
package ac.technion.schemamatching.matchers.secondline;

import java.util.Properties;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.simple.Max2LM;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * @author Tomer Sagi
 * Ontobuilder Research Environment wrapper for Max2LM second line matcher
 */
public class OBMaxDelta implements SecondLineMatcher {

	private Max2LM my2LM;
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getName()
	 */
	public String getName() {
		return "Ontobuilder Max Delta";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#match(ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	public MatchInformation match(MatchInformation mi) {
		MatchInformation res = my2LM.match(mi);
		DescriptiveStatistics ds = new DescriptiveStatistics(res.getNumMatches());
		for (Match m : res.getCopyOfMatches())
			ds.addValue(m.getEffectiveness());
		
		double t = (ds.getMean()>ds.getStandardDeviation()  ? (ds.getMean()>2*ds.getStandardDeviation() ? ds.getMean()- 2*ds.getStandardDeviation() : ds.getMean()-ds.getStandardDeviation()) : 0.1);
		ConversionUtils.zeroWeightsByThresholdAndRemoveMatches(res, t);
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getConfig()
	 */
	public String getConfig() {
		return "Delta:" + Double.toString(delta);
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getDBid()
	 */
	public int getDBid() {
		return 8;
	}

	/**
	 * Sets delta to supplied value
	 * @param delta
	 */
	public OBMaxDelta(double delta)
	{
		this.my2LM = new Max2LM(delta);
		this.delta = delta;
	}
	public void setDelta(double delta) {
		this.delta = delta;
	}

	public double getDelta() {
		return delta;
	}
	private double delta = 0.1;
	@Override
	public boolean init(Properties properties) {
		if (properties.containsKey("delta"))
		{
			delta = Double.parseDouble((String)properties.get("delta"));
			this.my2LM = new Max2LM(delta);
			return true;
		}
		System.err.println("OBMaxDelta 2LM could not find the required " +
				"property 'delta' in the property file");
		return false;		
	}
}

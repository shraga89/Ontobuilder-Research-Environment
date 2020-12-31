/**
 * 
 */
package ac.technion.schemamatching.matchers.secondline;

import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.simple.Max2LM;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * @author Tomer Sagi
 * Ontobuilder Research Environment wrapper for Max2LM second line matcher
 */
public class OBMaxDelta_on_EntryPrediction implements SecondLineMatcher {

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
		return my2LM.match(mi);
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
	public OBMaxDelta_on_EntryPrediction(double delta)
	{
		this.my2LM = new Max2LM(delta);
//		Properties p = new Properties();
//		p.put("t", threshold);
//		my2LM.init(p);
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
			return true;
		}
		System.err.println("OBMaxDelta 2LM could not find the required " +
				"property 'delta' in the property file");
		return false;		
	}
}

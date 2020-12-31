/**
 * 
 */
package ac.technion.schemamatching.matchers.secondline;

import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.functionalDependencies.functionalDependencyMatch;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * @author Maria Kitsis
 * Ontobuilder Research Environment wrapper for Functional Dependency second line matcher
 */
public class OBFuncDepend implements SecondLineMatcher {

	private functionalDependencyMatch fdm;
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getName()
	 */
	public String getName() {
		return "Ontobuilder Functional Dependency ";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#match(ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	public MatchInformation match(MatchInformation mi) {
		return fdm.match(mi);
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
		return 15;
	}

	/**
	 * Sets delta to supplied value
	 * @param delta
	 */
	public OBFuncDepend()
	{
		this.fdm = new functionalDependencyMatch();
//		Properties p = new Properties();
//		p.put("t", threshold);
//		my2LM.init(p);
		//this.delta = delta;
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

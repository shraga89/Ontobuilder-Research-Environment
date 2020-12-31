/**
 * 
 */
package ac.technion.schemamatching.matchers.secondline;

import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.stablemarriage.StableMarriageWrapper;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * @author Tomer Sagi
 * Ontobuilder Research Environment wrapper for Stable Marriage second line matcher
 */
public class OBStableMarriage implements SecondLineMatcher {

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getName()
	 */
	public String getName() {
		return "Ontobuilder Stable Marriage";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#match(ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	public MatchInformation match(MatchInformation mi) {
		StableMarriageWrapper m_StableMarriageWrapper = new StableMarriageWrapper();
		MatchInformation res = m_StableMarriageWrapper.runAlgorithm(mi.getMatrix(), mi.getCandidateOntology(),mi.getTargetOntology());
		assert (res!=null);
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getConfig()
	 */
	public String getConfig() {
		return "default config";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getDBid()
	 */
	public int getDBid() {
		return 2;
	}

	@Override
	public boolean init(Properties properties) {
		return true;
	}

}

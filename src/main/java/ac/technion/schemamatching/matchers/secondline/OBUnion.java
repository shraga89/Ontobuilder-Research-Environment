/**
 * 
 */
package ac.technion.schemamatching.matchers.secondline;

import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.set.Union2LM;
/**
 * @author Tomer Sagi
 * Ontobuilder Research Environment wrapper for Union second line matcher
 */
public class OBUnion implements SecondLineMatcher {

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getName()
	 */
	public String getName() {
		return "Ontobuilder Union Dominants Stable-Marriage";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#match(ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	public MatchInformation match(MatchInformation mi) {
		Union2LM u = new Union2LM();
		return u.match(mi);
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
		return 5;
	}

	@Override
	public boolean init(Properties properties) {
		return true;
	}

}

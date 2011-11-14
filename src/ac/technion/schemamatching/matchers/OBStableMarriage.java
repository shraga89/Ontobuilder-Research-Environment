/**
 * 
 */
package ac.technion.schemamatching.matchers;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.BestMappingsWrapper;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

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
		BestMappingsWrapper.matchMatrix = mi.getMatrix();	
		SchemaTranslator st = BestMappingsWrapper.GetBestMapping("Stable Marriage");
		assert (st!=null);
		MatchInformation res = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
		res.setMatches(st.toOntoBuilderMatchList(res.getMatrix()));
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
		return 1;
	}

}

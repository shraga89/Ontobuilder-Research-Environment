/**
 * 
 */
package ac.technion.schemamatching.matchers;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.BestMappingsWrapper;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * @author Tomer Sagi
 * Ontobuilder Research Environment wrapper for Union second line matcher
 */
public class OBUnion implements SecondLineMatcher {

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getName()
	 */
	public String getName() {
		return "Ontobuilder Union";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#match(ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	public MatchInformation match(MatchInformation mi) {
		BestMappingsWrapper.matchMatrix = mi.getMatrix();	
		SchemaTranslator st = BestMappingsWrapper.GetBestMapping("Union");
		assert (st!=null);
		MatchInformation res = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
		res.setMatches(st.toOntoBuilderMatchList(res.getMatrix()));
		ConversionUtils.zeroNonMatched(res);
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
		return 5;
	}

}

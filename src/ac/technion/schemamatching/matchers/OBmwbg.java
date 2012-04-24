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
 * Ontobuilder Research Environment wrapper for MWBG second line matcher
 */
public class OBmwbg implements SecondLineMatcher {

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getName()
	 */
	public String getName() {
		return "Ontobuilder MWBG";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#match(ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	public MatchInformation match(MatchInformation mi) {
		MatchInformation mwbg = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
		mwbg.setMatrix(mi.getMatrix());
		ConversionUtils.zeroWeightsByThresholdAndRemoveMatches(mwbg, 0.01);
		BestMappingsWrapper.matchMatrix = mwbg.getMatrix();	
		SchemaTranslator st = BestMappingsWrapper.GetBestMapping("Max Weighted Bipartite Graph");
		assert (st!=null);
		mwbg.setMatches(st.toOntoBuilderMatchList(mwbg.getMatrix()));
		ConversionUtils.zeroNonMatched(mwbg);
		return mwbg;
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

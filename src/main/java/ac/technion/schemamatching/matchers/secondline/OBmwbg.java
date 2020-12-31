/**
 * 
 */
package ac.technion.schemamatching.matchers.secondline;

import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsWrapper;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * @author Tomer Sagi
 * Ontobuilder Research Environment wrapper for MWBG second line matcher
 * Leaves k matches per term for terms of the smaller ontology to improve speed
 * Default k = 10
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
		ConversionUtils.limitToKMatches(mwbg, k);
	      try {
	    	  SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(mwbg);
			return smw.getBestMatching();
		} catch (SchemaMatchingsException e) {
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getConfig()
	 */
	public String getConfig() {
		return "k=" + k;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getDBid()
	 */
	public int getDBid() {
		return 1;
	}

	@Override
	public boolean init(Properties properties) {
		if (properties.containsKey("k"))
		{
			k = Integer.parseInt((String)properties.get("k"));
		}
		return true;
	}
	
	int k=10;

}

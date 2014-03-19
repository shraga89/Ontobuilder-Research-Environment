/**
 * 
 */
package ac.technion.schemamatching.matchers.secondline;

import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsWrapper;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingAlgorithmsRunner;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * @author Matthias Weidlich
 * Ontobuilder Research Environment wrapper for top k second line matcher
 */
public class OBTopK implements SecondLineMatcher {

	public int k = 5;
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#getName()
	 */
	public String getName() {
		return "Ontobuilder Top K";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.SecondLineMatcher#match(ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	public MatchInformation match(MatchInformation mi) {
		

		MatchInformation res = null;
        UnifiedTopKMatchingCreator uni = new UnifiedTopKMatchingCreator();
        uni.weighting = UnifiedTopKMatchingCreator.WEIGHTING.OCCURRENCE;
        
        try {
			SchemaMatchingAlgorithmsRunner.setAccumulationMode(true);
            SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(mi);

    		System.out.println("Derive top " + k);
        	
    		for (int i = 1; i <= k; i++) {
        		res = smw.getNextBestMatching();
        		System.out.print(" " + i);
    			ConversionUtils.zeroNonMatched(res);
    			uni.addMatching(i, res);
        	}
	      }
	      catch (Exception e) {
	    	  e.printStackTrace();
	      }
        
		System.out.print("\n");
        uni.buildGraph();
        uni.deriveClusters();
        
		return uni.getResultingMatching();
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
		return 10;
	}

	@Override
	public boolean init(Properties properties) {
		if (properties.containsKey("topK"))
		{
			k = Integer.parseInt((String)properties.get("topK"));
			return true;
		}
		System.err.println("OBTopK 2LM could not find the required " +
				"property 'topK' in the property file");
		return false;
	}

}

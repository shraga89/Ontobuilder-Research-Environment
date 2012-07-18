/**
 * 
 */
package ac.technion.schemamatching.matchers.secondline;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsWrapper;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingAlgorithmsRunner;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * @author Matthias Weidlich
 * Ontobuilder Research Environment wrapper for top k second line matcher
 */
public class OBTopK implements SecondLineMatcher {

	public static int k = 80;
	
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
		

        SchemaTranslator st = null;
        UnifiedTopKMatchingCreator uni = new UnifiedTopKMatchingCreator();
        uni.weighting = UnifiedTopKMatchingCreator.WEIGHTING.OCCURRENCE;
        
        try {
			SchemaMatchingAlgorithmsRunner.setAccumulationMode(true);
            SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(mi.getMatrix());

    		System.out.println("Derive top " + k);
        	
    		for (int i = 1; i <= k; i++) {
        		st = smw.getNextBestMatching();
        		System.out.print(" " + i);
//        		System.out.println(st.getMatches());
        		
    			MatchInformation res = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
    			res.setMatches(st.toOntoBuilderMatchList(res.getMatrix()));
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
		return 8;
	}

}
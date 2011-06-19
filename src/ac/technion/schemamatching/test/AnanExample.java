

package ac.technion.schemamatching.test;

import schemamatchings.meta.algorithms.TKMInitializationException;
import schemamatchings.meta.algorithms.TKMRunningException;
import schemamatchings.util.SchemaMatchingAlgorithmsRunner;
import schemamatchings.util.SchemaTranslator;

/**
 * @author haggai
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AnanExample {
	
	  public static void main(String args[]) {
	    try{
	    	
	      String[] rightNames = {"a1","b1","c1"};
	  	  String[] leftNames = {"a2","b2","c2"};
	  	  double[][] adjMatrix = {{0.1,0.3,0.6},
	  	                          {0.2,0.1,0.5},
	  	                          {0.4,0.5,0.8}};
	  	  
	      SchemaMatchingAlgorithmsRunner smar = new SchemaMatchingAlgorithmsRunner();
	      smar.setInitialSchema(leftNames);
	      try{
	        smar.setMatchedSchema(rightNames,adjMatrix);
	      }catch(TKMInitializationException e){
	        e.printStackTrace();
	        System.exit(1);
	      }
	      SchemaTranslator st;
	      st = smar.getNextBestMatching(true);
	      st.printTranslations();
	   
	    }catch(TKMRunningException e){
	    	 e.printStackTrace();
		     System.exit(1);
	    }
	  }
}

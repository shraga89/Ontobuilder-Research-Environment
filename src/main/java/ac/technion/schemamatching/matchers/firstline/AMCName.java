/**
 * 
 */
package ac.technion.schemamatching.matchers.firstline;

import java.util.Arrays;

import com.sap.research.amc.utils.cli.MatchingConfiguration;

/**
 * Wrapper class for NisB Auto Mapping Core - Name Algorithm
 * @author Tomer Sagi
 *
 */
public class AMCName extends AMCTokenPath {

	public AMCName()
	{
		matcher = Arrays.asList(com.sap.research.amc.utils.cli.MatcherType.NAME);
		conf = new MatchingConfiguration(matcher);
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getName()
	 */
	public String getName() {
		return "AMC Name";
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getType()
	 */
	public ac.technion.schemamatching.matchers.MatcherType getType() {
		return  ac.technion.schemamatching.matchers.MatcherType.SYNTACTIC;
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getDBid()
	 */
	public int getDBid() {
		return 8;
	}
}

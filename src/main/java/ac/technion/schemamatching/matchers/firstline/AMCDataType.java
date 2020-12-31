/**
 * 
 */
package ac.technion.schemamatching.matchers.firstline;

import java.util.Arrays;

import com.sap.research.amc.utils.cli.MatchingConfiguration;

/**
 * Wrapper class for NisB Auto Mapping Core - DataType Algorithm
 * @author Tomer Sagi
 *
 */
public class AMCDataType extends AMCTokenPath {

	public AMCDataType()
	{
		matcher = Arrays.asList(com.sap.research.amc.utils.cli.MatcherType.DATATYPE);
		conf = new MatchingConfiguration(matcher);
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getName()
	 */
	public String getName() {
		return "AMC DataType";
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getType()
	 */
	public ac.technion.schemamatching.matchers.MatcherType getType() {
		return  ac.technion.schemamatching.matchers.MatcherType.DATATYPE;
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getDBid()
	 */
	public int getDBid() {
		return 10;
	}
}

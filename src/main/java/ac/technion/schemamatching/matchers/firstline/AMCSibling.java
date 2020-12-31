/**
 * 
 */
package ac.technion.schemamatching.matchers.firstline;

import java.util.Arrays;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import com.sap.research.amc.utils.cli.MatchingConfiguration;

/**
 * Wrapper class for NisB Auto Mapping Core - Token Path Algorithm
 * @author Tomer Sagi
 *
 */
public class AMCSibling extends AMCTokenPath {

	public AMCSibling()
	{
		matcher = Arrays.asList(com.sap.research.amc.utils.cli.MatcherType.SIBLING);
		conf = new MatchingConfiguration(matcher);
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getName()
	 */
	public String getName() {
		return "AMC Sibling";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#hasBinary()
	 */
	public boolean hasBinary() {
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#match(com.modica.ontology.Ontology, com.modica.ontology.Ontology, boolean)
	 */
	public MatchInformation match(Ontology candidate, Ontology target, boolean binary) {
		return super.match(candidate,target,binary);
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getConfig()
	 */
	public String getConfig() {
		return "default config";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getType()
	 */
	public ac.technion.schemamatching.matchers.MatcherType getType() {
		return  ac.technion.schemamatching.matchers.MatcherType.STRUCTURAL_PARENTCHILD;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getDBid()
	 */
	public int getDBid() {
		return 12;
	}

}

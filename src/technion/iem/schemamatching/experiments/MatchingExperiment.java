/**
 * 
 */
package technion.iem.schemamatching.experiments;

import com.modica.ontology.match.MatchInformation;

/**
 * @author tomer_s
 *
 */
public interface MatchingExperiment 
{
	public MatchInformation runExperiment(MatchInformation in);
}

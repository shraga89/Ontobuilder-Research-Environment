/**
 * 
 */
package ac.technion.schemamatching.experiments;

import java.util.Properties;

import com.modica.ontology.Ontology;
import com.modica.ontology.match.MatchInformation;

/**
 * @author Tomer Sagi
 *
 */
public interface OtherMatcher 
{
	public boolean init(Properties p);
	public MatchInformation match(Ontology candidate,Ontology target);
}

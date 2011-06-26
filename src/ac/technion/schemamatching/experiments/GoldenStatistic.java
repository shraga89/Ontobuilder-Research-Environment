/**
 * 
 */
package ac.technion.schemamatching.experiments;

import schemamatchings.util.SchemaTranslator;

import com.modica.ontology.match.MatchInformation;

/**
 * @author Tomer Sagi
 *
 */
public interface GoldenStatistic extends Statistic {

	boolean init(MatchInformation mi, SchemaTranslator schemaTranslator);
}

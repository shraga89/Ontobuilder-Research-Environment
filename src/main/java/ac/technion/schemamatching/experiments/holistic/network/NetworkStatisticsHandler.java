package ac.technion.schemamatching.experiments.holistic.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchema;

/**
 * The handler helps to collect statistics for a network, when still using 
 * statistics implementations that work on the binary case of two schemas.
 * 
 * @author matthias weidlich
 *
 */
public class NetworkStatisticsHandler {

	private List<Statistic> statistics;
	
	public NetworkStatisticsHandler() {
		this.statistics = new ArrayList<Statistic>();
	}
	
	public void addStatistic(Class<? extends K2Statistic> c, String title, SchemaNetwork network, Set<ExperimentSchema> schemas) {
		
		for (ExperimentSchema s1 : schemas) {
			for (ExperimentSchema s2 : schemas) {
				
				if (s1.equals(s2)) continue;
				
				K2Statistic stat = null;
				
				try {
					stat = c.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				
				stat.init(title, network.getMatchResultForSchemas(s1,s2), network.getGoldStandardHandler().getGoldStandardForSchemas(s1,s2));
				statistics.add(stat);
			}
		}
	}

	public List<Statistic> getStatistics() {
		return statistics;
	}
	
	
}

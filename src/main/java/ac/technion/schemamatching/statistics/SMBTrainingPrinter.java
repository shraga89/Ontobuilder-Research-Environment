/**
 * 
 */
package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.HashMap;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * @author Tomer Sagi
 *
 */
public class SMBTrainingPrinter implements Statistic {

	/**
	 * Use this constructor only instead of init method.
	 * @param res
	 */
	public SMBTrainingPrinter(HashMap<Long, Double> res) {
		for (Long config : res.keySet())
		{
			String[] str = new String[3];
			long fid = (config>1000?config % 1000:config);
			long sid = (config>1000?(config - fid)/1000:0);
			str[0] = Long.toString(fid);
			str[1] = Long.toString(sid);
			str[2] = res.get(config).toString();
			data.add(str);
		}
	}

	private ArrayList<String[]> data = new ArrayList<String[]>();

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#getHeader()
	 */
	public String[] getHeader() {
		return new String[]{"flm","slm","weight"};
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#getName()
	 */
	public String getName() {
		return "SMBTrainingResult";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#getData()
	 */
	public ArrayList<String[]> getData() {
		return data;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#init(java.lang.String, ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	public boolean init(String instanceDescription, MatchInformation mi) {
		// Does nothing, requires usage of constructor
		return true;
	}

}

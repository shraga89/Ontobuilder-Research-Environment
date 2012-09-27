/**
 * 
 */
package ac.technion.schemamatching.statistics;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * @author Tomer Sagi
 * Fully configurable statistic
 *
 */
public class DummyStatistic implements Statistic {

	String[] header = new String[]{};
	/**
	 * @param header the header to set
	 */
	public void setHeader(String[] header) {
		this.header = header;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(ArrayList<String[]> data) {
		this.data = data;
	}

	String name = "";
	ArrayList<String[]> data = new ArrayList<String[]>();
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#getHeader()
	 */
	public String[] getHeader() {
		return header;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#getName()
	 */
	public String getName() {
		return name;
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
		//use setters to set statistic result
		return false;
	}

}

package ac.technion.schemamatching.statistics;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

/**
 * Calculates binary precision and recall
 * @author Tomer Sagi
 *
 */
public class BinaryGolden implements GoldenStatistic {
	private ArrayList<String[]> data;
	private String[] header;
	private SchemaTranslator stExactMatch;
	
	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "Binary Golden Statistic";
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public boolean init(String instanceDescription, MatchInformation mi) {
		return false; //Golden statistics don't implement this method
	}

	public boolean init(String instanceDescription, MatchInformation mi, MatchInformation exactMatch) {
		stExactMatch = new SchemaTranslator(exactMatch);
		data = new ArrayList<String[]>();
		header = new String[]{"instance","Precision","Recall"};
		data.add(new String[] {instanceDescription, Double.toString(mi.getPrecision(stExactMatch)),Double.toString(mi.getRecall(stExactMatch))});
		return true;
	}
	
	/**
	 * A more efficient init saving the conversion from match information to schema translator for the exact match
	 * @param instanceDescription
	 * @param mi
	 * @param exactMatch
	 * @return
	 */
	public boolean init(String instanceDescription, MatchInformation mi, SchemaTranslator exactMatch) {
		data = new ArrayList<String[]>();
		header = new String[]{"instance","Precision","Recall"};
		data.add(new String[] {instanceDescription, Double.toString(mi.getPrecision(exactMatch)),Double.toString(mi.getRecall(exactMatch))});
		return true;
	}

}

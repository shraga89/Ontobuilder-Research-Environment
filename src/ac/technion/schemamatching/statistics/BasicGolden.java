package ac.technion.schemamatching.statistics;

import java.util.ArrayList;

import schemamatchings.util.SchemaTranslator;

import com.modica.ontology.match.MatchInformation;

public class BasicGolden implements GoldenStatistic {
	private ArrayList<String[]> data;
	private String[] header;
	
	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "Basic Golden Mapping Statistic";
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public boolean init(String instanceDescription, MatchInformation mi) {
		return false; //Golden statistics don't implement this method
	}

	public boolean init(String instanceDescription, MatchInformation mi, MatchInformation exactMatch) {
		data = new ArrayList<String[]>();
		header = new String[]{"instance","Precision","Recall"};
		SchemaTranslator st = new SchemaTranslator(exactMatch);
		data.add(new String[] {instanceDescription, Double.toString(mi.getPrecision(st)),Double.toString(mi.getRecall(st))});
		return true;
	}

}

/**
 * 
 */
package ac.technion.schemamatching.statistics;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * @author Tomer Sagi
 * Returns result of mapping in print ready format
 */
public class MappingPrinter implements Statistic{

	private ArrayList<String[]> data;
	private String[] header;

	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "VectorPrinter";
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public boolean init(String instanceDescription, MatchInformation mi) {
		
		//Create Header
		header = new String[]{"instance","candID","candName","candProv"
				,"targID","targName","targProv","conf"};
		
		//Create Data list
		data = new ArrayList<String[]>();
		Double sum = 0.0;
		ArrayList<Match> matches = mi.getCopyOfMatches();
		for (Match m : matches)
		{
			Term c = m.getCandidateTerm();
			Term t = m.getTargetTerm();
			String[] v = new String[]{instanceDescription, Long.toString(c.getId()), 
					c.getName(),c.getProvenance(), Long.toString(t.getId()),t.getName(),
					t.getProvenance(),Double.toString(m.getEffectiveness())};
			data.add(v);
			sum += m.getEffectiveness();
		}
		Double avg = sum/matches.size();
		String[] v = new String[]{instanceDescription, "Average Similarity Measure", 
				"","", "","", "",Double.toString(avg)};
		data.add(v);
		return true;
	}
	
	public boolean init(String instanceDescription, MatchInformation mi, MatchInformation exactMatch) {
		
		//Create Header
		header = new String[]{"instance","candID","candName","candProv"
				,"targID","targName","targProv","conf", "realConf"};
		
		//Create Data list
		data = new ArrayList<String[]>();
		Double sum = 0.0;
		ArrayList<Match> matches = mi.getCopyOfMatches();
		for (Match m : matches)
		{
			Term c = m.getCandidateTerm();
			Term t = m.getTargetTerm();
			String realConf = Double.toString(exactMatch.getMatchConfidence(c, t));
			String[] v = new String[]{instanceDescription, Long.toString(c.getId()), 
					c.getName(),c.getProvenance(), Long.toString(t.getId()),t.getName(),
					t.getProvenance(),Double.toString(m.getEffectiveness()), realConf};
			data.add(v);
			sum += m.getEffectiveness();
		}
		Double avg = sum/matches.size();
		String[] v = new String[]{instanceDescription, "Average Similarity Measure", 
				"","", "","", "",Double.toString(avg), ""};
		data.add(v);
		return true;
	}

}

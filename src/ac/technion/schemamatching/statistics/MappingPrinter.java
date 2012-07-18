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
		for (Match m : mi.getCopyOfMatches())
		{
			Term c = m.getCandidateTerm();
			Term t = m.getTargetTerm();
			String[] v = new String[]{instanceDescription, Long.toString(c.getId()), 
					c.getName(),c.getProvenance(), Long.toString(t.getId()),t.getName(),
					t.getProvenance(),Double.toString(m.getEffectiveness())};
			data.add(v);
		}
		return true;
	}

}

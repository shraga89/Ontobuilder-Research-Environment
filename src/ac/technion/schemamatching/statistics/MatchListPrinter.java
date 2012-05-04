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
 *
 */
public class MatchListPrinter implements Statistic {
	
	ArrayList<String[]> data = new ArrayList<String[]>(); 

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#getHeader()
	 */
	public String[] getHeader() {
		return new String[]{"CandTermId","TargetTermID","CandTermName","TargetTermName","Confidence"};
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#getName()
	 */
	public String getName() {
		return "Match List";
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
		for (Match m : mi.getCopyOfMatches())
		{
			Term c = m.getCandidateTerm();
			Term t = m.getTargetTerm();
			data.add(new String[]{instanceDescription,Long.toString((c.getId())),Long.toString(t.getId()),c.toString(),t.toString(),Double.toString(m.getEffectiveness())});
		}
		return true;
	}

}

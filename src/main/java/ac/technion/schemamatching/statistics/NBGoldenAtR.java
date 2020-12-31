package ac.technion.schemamatching.statistics;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * R-Precision
 * @author Tomer Sagi
 *
 */
public class NBGoldenAtR implements K2Statistic {
	private ArrayList<String[]> data;
	private String[] header;
	
	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "Non binary R-Precision";
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public boolean init(String instanceDescription, MatchInformation mi) {
		return false; //Golden statistics don't implement this method
	}

	public boolean init(String instanceDescription, MatchInformation mi, MatchInformation exactMatch) {
		data = new ArrayList<String[]>();
		header = new String[]{"instance","R-Precision"};
		double tpVal = 0.0d;
		double mLen = 0.0d;
		
		for (Term t : exactMatch.getOriginalTargetTerms())
		{
			ArrayList<Match> exact = exactMatch.getMatchesForTerm(t, false);
			if (exact == null) continue;
			//get min value of exact matches in MI and sum TP values 
			double minVal = 2.0d;
			for (Match m : exact)
			{
				double mVal = mi.getMatchConfidence(m.getCandidateTerm(),m.getTargetTerm());
				minVal = Math.min(minVal, mVal);
				tpVal += mVal;
			}
			// Sum matches for t which are equal or larger than the minimum matched
			if (mi.getMatchesForTerm(t, false)==null)
				continue;
			for (Match m : mi.getMatchesForTerm(t, false))
			{
				double mVal = mi.getMatchConfidence(m.getCandidateTerm(),m.getTargetTerm());
				if (mVal >= minVal ) mLen+=mVal;
			}
		}
		Double precision = StatisticsUtils.setDoubleValueInUnitBounds((mLen==0.0?0.0:tpVal/mLen));
		data.add(new String[] {instanceDescription, precision.toString()});
		return true;
	}

}

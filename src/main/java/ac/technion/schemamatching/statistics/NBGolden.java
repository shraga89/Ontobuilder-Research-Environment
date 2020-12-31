package ac.technion.schemamatching.statistics;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Calculates non-binary precision and recall
 * @author Tomer Sagi
 *
 */
public class NBGolden implements K2Statistic {
	private ArrayList<String[]> data;
	private String[] header;
	
	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "Non binary Golden Statistic";
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public boolean init(String instanceDescription, MatchInformation mi) {
		return false; //Golden statistics don't implement this method
	}

	public boolean init(String instanceDescription, MatchInformation mi, MatchInformation exactMatch) {
		data = new ArrayList<String[]>();
		header = new String[]{"instance","Precision","Recall", "F", "Overall", "VectorProduct", "MatchVectorLength","ExactVectorLength"};
		ArrayList<Match> matches = mi.getCopyOfMatches();
		ArrayList<Match> exact = exactMatch.getCopyOfMatches();
		double prod = 0.0d;
		double exactLen = (double)exact.size();
		double mLen = 0.0d;
		
		for (Match m : matches)
		{
			double tpVal = exactMatch.getMatchConfidence(m.getCandidateTerm(), m.getTargetTerm());
			double val = m.getEffectiveness(); 
			prod+=(tpVal*val);
			mLen+=val;
		}
		Double precision = StatisticsUtils.setDoubleValueInUnitBounds((mLen==0.0?0.0:prod/mLen));
		Double recall = StatisticsUtils.setDoubleValueInUnitBounds((exactLen==0.0?0.0:prod/exactLen));
		Double f = 2d * (precision * recall) / (precision + recall);
		Double overall = recall * (2d - 1d / precision);
		
//		System.out.println(instanceDescription + "," + precision + ", " + recall + ", " + f + ", " + overall);
		data.add(new String[] {instanceDescription, precision.toString(),
				recall.toString(), f.toString(), overall.toString(), 
				Double.toString(prod),Double.toString(mLen),Double.toString(exactLen)});
		return true;
	}
}
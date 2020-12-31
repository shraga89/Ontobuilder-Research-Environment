package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * Expanded version of NBGolden, testing various measures
 * @author Tomer Sagi
 *
 */
public class NBGoldenAtK implements K2Statistic {
	private ArrayList<String[]> data;
	private String[] header;
	private int k=5;
	
	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "Non binary Golden Statistics @ K considered";
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public boolean init(String instanceDescription, MatchInformation mi) {
		return false; //Golden statistics don't implement this method
	}

	public boolean init(String instanceDescription, MatchInformation mi, MatchInformation exactMatch) {
		data = new ArrayList<String[]>();
		header = new String[]{"instance","P@K","R@K","F@K", "Overall@K", "VectorProduct", "MatchVectorLength","ExactVectorLength"};
		instanceDescription = instanceDescription + ",k=" +k;
		MatchInformation kMI = mi.clone();
		ConversionUtils.limitToKMatches(kMI, k);
		ArrayList<Match> matches = kMI.getCopyOfMatches();
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
		data.add(new String[] {instanceDescription, precision.toString(),
				recall.toString(), f.toString(), overall.toString(), 
				Double.toString(prod),Double.toString(mLen),Double.toString(exactLen)});
		return true;
	}

	/**
	 * @return the k
	 */
	public int getK() {
		return k;
	}

	/**
	 * @param k the k to set
	 */
	public void setK(int k) {
		this.k = k;
	}

}

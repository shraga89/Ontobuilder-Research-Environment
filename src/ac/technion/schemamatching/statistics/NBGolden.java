package ac.technion.schemamatching.statistics;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.util.ConversionUtils;
import ac.technion.schemamatching.util.SimilarityVectorUtils;

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
		header = new String[]{"instance","Precision","Recall"};
		ArrayList<Match> matches = mi.getCopyOfMatches();
		ArrayList<Match> exact = mi.getCopyOfMatches();
		double prod = 0.0;
		double exactLen = (double)exact.size();
		double mLen = 0.0;
		for (Match m : matches)
		{
			double tpVal = exactMatch.getMatchConfidence(m.getCandidateTerm(), m.getTargetTerm());
			double val = m.getEffectiveness(); 
			prod+=(tpVal*val);
			mLen+=val;
		}
		Double precision = (mLen==0.0?0.0:prod/mLen);
		Double recall = (exactLen==0.0?0.0:prod/exactLen);
		data.add(new String[] {instanceDescription, precision.toString(),recall.toString()});
		//Commented out by Tomer on 27/09/12 to improve performance
//		if (mi.getMatchMatrix().length<=exactMatch.getMatchMatrix().length)
//			try {
//				mi = ConversionUtils.expandMatrix(mi,exactMatch);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		data.add(new String[] {instanceDescription, Double.toString(calcSMPrecision(mi,exactMatch)),Double.toString(calcSMRecall(mi,exactMatch))});
		return true;
	}

	/**
	 * N^2 Calculation of Similarity Matrix Precision
	 * @param mi match information result of matcher
	 * @param exactMatch 
	 * @return
	 */
	private double calcSMPrecision(MatchInformation mi,
			MatchInformation exactMatch) {
		Double[] vMI = SimilarityVectorUtils.makeArray(mi);
		Double[] vExact = SimilarityVectorUtils.makeArray(exactMatch);
		return SimilarityVectorUtils.calcDotProduct(vMI, vExact)/Math.pow(SimilarityVectorUtils.calcL1Length(vMI),2);
	}
	
	/**
	 * N^2 Calculation of Similarity Matrix Recall
	 * @param mi match information result of matcher
	 * @param exactMatch 
	 * @return
	 */
	private double calcSMRecall(MatchInformation mi,
			MatchInformation exactMatch) {
		Double[] vMI = SimilarityVectorUtils.makeArray(mi);
		Double[] vExact = SimilarityVectorUtils.makeArray(exactMatch);
		return SimilarityVectorUtils.calcDotProduct(vMI, vExact)/Math.pow(SimilarityVectorUtils.calcL1Length(vExact),2);
	}

}

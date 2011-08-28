package ac.technion.schemamatching.statistics;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.util.SimilarityVectorUtils;

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
		data.add(new String[] {instanceDescription, Double.toString(calcSMPrecision(mi,exactMatch)),Double.toString(calcSMRecall(mi,exactMatch))});
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
		double[] vMI = SimilarityVectorUtils.makeArray(mi);
		double[] vExact = SimilarityVectorUtils.makeArray(exactMatch);
		return SimilarityVectorUtils.calcDotProduct(vMI, vExact)/SimilarityVectorUtils.calcL1Length(vMI);
	}
	
	/**
	 * N^2 Calculation of Similarity Matrix Recall
	 * @param mi match information result of matcher
	 * @param exactMatch 
	 * @return
	 */
	private double calcSMRecall(MatchInformation mi,
			MatchInformation exactMatch) {
		double[] vMI = SimilarityVectorUtils.makeArray(mi);
		double[] vExact = SimilarityVectorUtils.makeArray(exactMatch);
		return SimilarityVectorUtils.calcDotProduct(vMI, vExact)/SimilarityVectorUtils.calcL1Length(vExact);
	}

}

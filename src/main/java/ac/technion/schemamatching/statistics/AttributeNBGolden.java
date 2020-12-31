package ac.technion.schemamatching.statistics;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.util.ConversionUtils;
import ac.technion.schemamatching.util.SimilarityVectorUtils;

/**
 * Calculates non-binary precision and recall
 * @author Tomer Sagi
 *
 */
public class AttributeNBGolden implements K2Statistic {
	private ArrayList<String[]> data;
	private String[] header;
	
	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "Attribute Level Non binary Golden Statistic";
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
		if (mi.getMatchMatrix().length<exactMatch.getMatchMatrix().length)
			try {
				mi = ConversionUtils.expandMatrix(mi,exactMatch);
			} catch (Exception e) {
				e.printStackTrace();
			}
		ArrayList<Double[]> miRowArrays = SimilarityVectorUtils.makeRowArrayList(mi);
		ArrayList<Double[]> exactRowArrays = SimilarityVectorUtils.makeRowArrayList(exactMatch);
		assert(miRowArrays.size() == exactRowArrays.size());
		for (int i=0;i<miRowArrays.size();i++)
		{
			Term t = exactMatch.getMatrix().getTargetTerms().get(i);
			data.add(new String[] 
			    {instanceDescription + ",Target," + t.getId() + "," + t.getProvenance()
					, Double.toString(calcSMPrecision(miRowArrays.get(i),exactRowArrays.get(i)))
					,Double.toString(calcSMRecall(miRowArrays.get(i),exactRowArrays.get(i)))});
		}
		ArrayList<Double[]> miColArrays = SimilarityVectorUtils.makeColumnArrayList(mi);
		ArrayList<Double[]> exactColArrays = SimilarityVectorUtils.makeColumnArrayList(exactMatch);
		for (int i=0;i<miColArrays.size();i++)
		{
			Term c = exactMatch.getMatrix().getCandidateTerms().get(i);
			data.add(new String[] 
			    {instanceDescription + ",Candidate," + c.getId() + "," + c.getProvenance()
					, Double.toString(calcSMPrecision(miColArrays.get(i),exactColArrays.get(i)))
					,Double.toString(calcSMRecall(miColArrays.get(i),exactColArrays.get(i)))});
		}
		
		return true;
	}

	/**
	 * N^2 Calculation of Similarity Matrix Precision
	 * @param mi match information result of matcher
	 * @param exactMatch 
	 * @return
	 */
	private double calcSMPrecision(Double[] vRes,Double[] vExact) {
		double len = SimilarityVectorUtils.calcL1Length(vRes);
		double res = (len==0.0?0.0:SimilarityVectorUtils.calcDotProduct(vRes, vExact)/Math.pow(SimilarityVectorUtils.calcL1Length(vRes),2));
		return StatisticsUtils.setDoubleValueInUnitBounds(res);
	}
	
	/**
	 * N^2 Calculation of Similarity Matrix Recall
	 * @param mi match information result of matcher
	 * @param exactMatch 
	 * @return
	 */
	private double calcSMRecall(Double[] vRes,Double[] vExact) {
		double len = SimilarityVectorUtils.calcL1Length(vExact);
		double res = (len==0.0?0.0:SimilarityVectorUtils.calcDotProduct(vRes, vExact)/Math.pow(SimilarityVectorUtils.calcL1Length(vExact),2));
		return StatisticsUtils.setDoubleValueInUnitBounds(res);
	}
	
	

}

package ac.technion.schemamatching.statistics;

import java.util.ArrayList;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Expanded version of NBGolden where k is added to the number of expected matches.
 * @author Tomer Sagi
 *
 */
public class NBGoldenAtDynamicK implements K2Statistic {
	private ArrayList<String[]> data;
	private String[] header;
	private int k=2;
	
	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "Non binary Golden Statistics @ K considered (K dynamic)";
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public boolean init(String instanceDescription, MatchInformation mi) {
		return false; //Golden statistics don't implement this method
	}

	public boolean init(String instanceDescription, MatchInformation mi, MatchInformation exactMatch) {
		data = new ArrayList<String[]>();
		header = new String[]{"instance","P@KA","R@KA","F@KA", "Overall@KA", "VectorProduct", "MatchVectorLength","ExactVectorLength"};
		instanceDescription = instanceDescription + ",k=" +k;
		Ordering<Match> byEffOrd = new Ordering<Match>() {
			  public int compare(Match left, Match right) {
			    return Doubles.compare(left.getEffectiveness(), right.getEffectiveness());
			  }
			}; 
		ArrayList<Match> topMatches = new ArrayList<Match>();
		boolean isCandLarger = (mi.getCandidateOntology().getAllTermsCount() > mi.getTargetOntology().getAllTermsCount());
		Ontology o = (isCandLarger ? mi.getCandidateOntology() : mi.getTargetOntology());
		for (Term t : o.getTerms(true))
		{
			ArrayList<Match> tMatches = mi.getMatchesForTerm(t , isCandLarger);
			ArrayList<Match> exact = exactMatch.getMatchesForTerm(t, isCandLarger);
			int expected = (exact==null?0: exact.size());
			int ka = (expected + k ==0 ? 1 : expected + k); //Even if k=0, we will penalize at least by one match when attributes that shouldn't be matched are matched
			if (tMatches != null)
				topMatches.addAll(byEffOrd.greatestOf(tMatches, ka));
		}
		double prod = 0.0d;
		double exactLen = (double)exactMatch.getNumMatches();
		double mLen = 0.0d;
		
		for (Match m : topMatches)
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

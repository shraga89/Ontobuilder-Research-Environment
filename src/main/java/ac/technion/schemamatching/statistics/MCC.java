package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Implementation of the Matthews correlation coefficient (values in [-1,1])
 * The strength of this measure is that compared to F1 it considers also TN and FN
 * and handles well cases of class imbalance (which are common to schema matching).
 * @see http://en.wikipedia.org/wiki/Matthews_correlation_coefficient
 */
public class MCC implements K2Statistic{

	private ArrayList<String[]> data = null;
	private String[] header = null;

	@Override
	public String[] getHeader() {
		return header;
	}

	@Override
	public String getName() {
		return "MCC Statistic";
	}

	@Override
	public List<String[]> getData() {
		return data;
	}

	@Override
	public boolean init(String instanceDescription, MatchInformation mi) {
		return false;
	}

	@Override
	public boolean init(String instanceDescription, MatchInformation mi,
			MatchInformation exactMatch) {
		
		HashSet<String> matchListIds = new HashSet<String>();
		HashSet<String> exactMatchListIds = new HashSet<String>();
		
		for (Match m : mi.getCopyOfMatches())
		{
			Long candID = m.getCandidateTerm().getId();
			Long targID = m.getTargetTerm().getId();
			matchListIds.add(candID.toString()+targID.toString());
		}
		for (Match m : exactMatch.getCopyOfMatches())
		{
			Long candID = m.getCandidateTerm().getId();
			Long targID = m.getTargetTerm().getId();
			exactMatchListIds.add(candID.toString()+targID.toString());
		}
		
		data = new ArrayList<String[]>();
		header = new String[]{"instance","MCC","TP","TN","FP","FN","N"};
		Double TP = calcTruePositives(matchListIds,exactMatchListIds);
		Double FP = calcFalsePositives(matchListIds,exactMatchListIds);
		Double FN = calcFalseNegatives(matchListIds,exactMatchListIds);
		Double N = new Double(mi.getMatrix().getRowCount()*mi.getMatrix().getColCount());
		Double TN = N - TP - FP - FN;
		Double S = N > 0 ? (TP + FN)/N : 0;
		Double P = N > 0 ? (TP + FP)/N : 0;
		Double MCC = P>0 && S>0 ? (TP/N - S*P)/Math.sqrt(P*S*(1-P)*(1-S)) : 0;
		data.add(new String[] {instanceDescription,MCC.toString(),TP.toString(),
				TN.toString(),FP.toString(),FN.toString(),N.toString()});
		return true;
	}

	private double calcTruePositives(HashSet<String> matchListIds, HashSet<String> exactMatchListIds) {
		double res = 0;
		for (String match : matchListIds)
		{
			if (exactMatchListIds.contains(match)) {
				res++;
			}
			
		}
		return res;
	}
	
	private double calcFalsePositives(HashSet<String> matchListIds, HashSet<String> exactMatchListIds) {
		double res = 0;
		for (String match : matchListIds)
		{
			if (!exactMatchListIds.contains(match)) {
				res++;
			}
			
		}
		return res;
	}
	
	private double calcFalseNegatives(HashSet<String> matchListIds, HashSet<String> exactMatchListIds) {
		double res = 0;
		for (String match : exactMatchListIds)
		{
			if (!matchListIds.contains(match)) {
				res++;
			}
			
		}
		return res;
	}

}

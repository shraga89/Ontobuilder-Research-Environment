package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.HashSet;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * @author Adham Hurani
 * Calculates ROC Statistics 
 * TPR - True positive ratio
 * FPR - False positive ratio
 */
public class BinaryROCStatistics implements K2Statistic {
	private ArrayList<String[]> data;
	private String[] header;
	HashSet<String> matchListIds = new HashSet<String>();
	HashSet<String> exactMatchListIds = new HashSet<String>();
	int neagtiveMatches = 0 ;
	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "Binary ROC Statistic";
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public boolean init(String instanceDescription, MatchInformation mi) {
		return false; 
	}

	public boolean init(String instanceDescription, MatchInformation mi, MatchInformation exactMatch) {
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
		//Irrelevant Pairs Count
		neagtiveMatches =  exactMatch.getTargetOntologyTermsTotal()* exactMatch.getCandidateOntologyTermsTotal() - exactMatchListIds.size();
		
		data = new ArrayList<String[]>();
		header = new String[]{"instance","FPR","TPR","MCC"};
		Double TPR = calcTPR();
		Double FPR = calcFPR();
		Double MCC = calcMCC();
		data.add(new String[] {instanceDescription, FPR.toString(),TPR.toString(),MCC.toString()});
		return true;
	}
	
	private Double calcTPR()
	{
		Double tp = calcTruePositives();
		Double p = (double) exactMatchListIds.size();
		return (p==0?0:tp/p);
		
	}
	private Double calcMCC()
	{
		Double fp = calcFalsePositives();
		Double tp = calcTruePositives();
		Double tn = (double)neagtiveMatches;
		Double fn = (double) exactMatchListIds.size() - tp;
		
		double mone = tp*tn - fp*fn;
		double mehane = Math.sqrt((tp+fp)*(tp+fn)*(tn+fp)*(tn+fn));
		return mone/mehane;
		
	}
	private Double calcFPR()
	{
		Double fp = calcFalsePositives();
		Double n = (double)neagtiveMatches;
		return (n==0?0:fp/n);
		
	}
	private Double calcTruePositives() {
		Double res = 0.0;
		for (String match : matchListIds)
		{
			if (exactMatchListIds.contains(match)) res+=1.0;
		}
		return res;
	}
	private Double calcFalsePositives() {
		Double res = 0.0;
		for (String match : matchListIds)
		{
			if (!exactMatchListIds.contains(match)) res+=1.0;
		}
		return res;
	}

}

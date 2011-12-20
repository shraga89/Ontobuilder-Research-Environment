package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.HashSet;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

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
		for (Match m : mi.getMatches())
		{
			Long candID = m.getCandidateTerm().getId();
			Long targID = m.getTargetTerm().getId();
			matchListIds.add(candID.toString()+targID.toString());
		}
		for (Match m : exactMatch.getMatches())
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


	/**
	 * A more efficient init saving the conversion from match information to schema translator for the exact match
	 * @param instanceDescription
	 * @param mi
	 * @param exactMatch
	 * @return
	 */
	public boolean init(String instanceDescription, MatchInformation mi, SchemaTranslator exactMatch) {
		for (Match m : mi.getMatches())
		{
			Long candID = m.getCandidateTerm().getId();
			Long targID = m.getTargetTerm().getId();
			matchListIds.add(candID.toString()+targID.toString());
		}
		for (MatchedAttributePair m : exactMatch.getMatches())
		{
			Long candID = m.id1;
			Long targID = m.id2;
			exactMatchListIds.add(candID.toString()+targID.toString());
		}
		//Irrelevant Pairs Count
		//neagtiveMatches =  exactMatch.()* exactMatch.getCandidateOntologyTermsTotal() - exactMatchListIds.size();
		
		data = new ArrayList<String[]>();
		header = new String[]{"instance","TPR","FPR"};
		Double TPR = calcTPR();
		Double FPR = calcTPR();
		data.add(new String[] {instanceDescription,TPR.toString(), FPR.toString()});
		return true;
	}

}

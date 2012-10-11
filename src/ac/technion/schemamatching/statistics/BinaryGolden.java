package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.HashSet;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Calculates binary precision and recall
 * @author Tomer Sagi
 *
 */
public class BinaryGolden implements K2Statistic {
	private ArrayList<String[]> data;
	private String[] header;
	HashSet<String> matchListIds = new HashSet<String>();
	HashSet<String> exactMatchListIds = new HashSet<String>();
	
	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "Binary Golden Statistic";
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public boolean init(String instanceDescription, MatchInformation mi) {
		return false; //Golden statistics don't implement this method
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
		
		data = new ArrayList<String[]>();
		header = new String[]{"instance","Precision","Recall", "F", "Overall","TP","Matches","ExactMatches"};
		Double truePositives = calcTruePositives();
		Double matches = (double) matchListIds.size();
		Double exact = (double) exactMatchListIds.size();
		Double precision = (matches==0?0:truePositives/matches);
		Double recall = (exact==0?0:truePositives/exact);
		Double f = 2d * (precision * recall) / (precision + recall);
		Double overall = recall * (2 - 1 / precision);
		data.add(new String[] {instanceDescription,precision.toString(), recall.toString(), 
				f.toString(), overall.toString(),truePositives.toString(),
				matches.toString(),exact.toString()});
		return true;
	}

	private Double calcTruePositives() {
		Double res = 0.0;
		for (String match : matchListIds)
		{
			if (exactMatchListIds.contains(match)) {
				//System.err.println("found: " + match);
				res+=1.0;
			}
			
		}
		return res;
	}
}

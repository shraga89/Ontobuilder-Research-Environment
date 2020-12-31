package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Calculates binary precision and recall
 * @author Tomer Sagi
 *
 */
public class VerboseBinaryGolden implements K2Statistic {
	private ArrayList<String[]> data;
	private String[] header;
	HashSet<String> matchListIds = new HashSet<String>();
	HashSet<String> exactMatchListIds = new HashSet<String>();
	
	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "Verbose Binary Golden Statistics";
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
		header = new String[]{"instance","CandidateTermID","CandidateTerm","TargetTermID","TargetTerm","Confidence", "SetResult"};
		//Prepare sets
		Set<Match> matches = new HashSet<Match>();
		for (Match m : mi.getCopyOfMatches())
			if(m.getEffectiveness()>0.0)
				matches.add(m);
		
		Set<Match> exactMatches = new HashSet<Match>();
		exactMatches.addAll(exactMatch.getCopyOfMatches());
		
		//Print out:
		for (Match m : matches)
		{
			String cat = (exactMatches.contains(m) ? "TP" : "FP");
			data.add(new String[] {instanceDescription
					,Long.toString(m.getCandidateTerm().getId())
					,m.getCandidateTerm().toStringVs2()
					,Long.toString(m.getTargetTerm().getId())
					,m.getTargetTerm().toStringVs2()
					,Double.toString(m.getEffectiveness())
					,cat});
		}
		for (Match m : exactMatches)
		{
			if (!matches.contains(m))
				data.add(new String[] 
						{instanceDescription
						,Long.toString(m.getCandidateTerm().getId())
						,m.getCandidateTerm().toStringVs2()
						,Long.toString(m.getTargetTerm().getId())
						,m.getTargetTerm().toStringVs2(),
						Double.toString(m.getEffectiveness())
						,"FN"});
		}
		
		return true;
	}
}

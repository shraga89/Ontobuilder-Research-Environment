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
		header = new String[]{"instance","CandidateTerm","TargetTerm", "SetResult"};
		//Prepare sets
		Set<Match> matches = new HashSet<Match>();
		matches.addAll(mi.getCopyOfMatches());
		Set<Match> exactMatches = new HashSet<Match>();
		exactMatches.addAll(exactMatch.getCopyOfMatches());
		
		//Prepare True Positives
		Set<Match> tp = new HashSet<Match>();
		tp.addAll(matches);
		tp.retainAll(exactMatches);
		//Prepare False Positives		
		Set<Match> fp = new HashSet<Match>();
		fp.addAll(matches);
		fp.removeAll(exactMatches);
		//Prepare False Negatives
		Set<Match> fn = new HashSet<Match>();
		fn.addAll(exactMatches);
		fn.removeAll(matches);
		
		//Print out:
		for (Match m : tp)
			data.add(new String[] {instanceDescription,m.getCandidateTerm().toString(),m.getTargetTerm().toString(),"TP"});
		for (Match m : fp)
			data.add(new String[] {instanceDescription,m.getCandidateTerm().toString(),m.getTargetTerm().toString(),"FP"});
		for (Match m : fn)
			data.add(new String[] {instanceDescription,m.getCandidateTerm().toString(),m.getTargetTerm().toString(),"FN"});
		
		return true;
	}
}

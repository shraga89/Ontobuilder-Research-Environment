package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.HashSet;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

/**
 * Calculates binary precision and recall
 * @author Tomer Sagi
 *
 */
public class BinaryGolden implements GoldenStatistic {
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
		
		data = new ArrayList<String[]>();
		header = new String[]{"instance","Precision","Recall"};
		Double precision = calcPrecision();
		Double recall = calcRecall();
		data.add(new String[] {instanceDescription,precision.toString(), recall.toString()});
		return true;
	}
	
	private Double calcRecall() {
		Double truePositives = calcTruePositives();
		Double exact = (double) exactMatchListIds.size();
		return (exact==0?0:truePositives/exact);
	}

	private Double calcTruePositives() {
		Double res = 0.0;
		for (String match : matchListIds)
		{
			if (exactMatchListIds.contains(match)) res+=1.0;
		}
		return res;
	}

	private Double calcPrecision() {
		Double truePositives = calcTruePositives();
		Double matches = (double) matchListIds.size();
		return (matches==0?0:truePositives/matches);
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
		data = new ArrayList<String[]>();
		header = new String[]{"instance","Precision","Recall"};
		Double precision = calcPrecision();
		Double recall = calcRecall();
		data.add(new String[] {instanceDescription,precision.toString(), recall.toString()});
		return true;
	}

}

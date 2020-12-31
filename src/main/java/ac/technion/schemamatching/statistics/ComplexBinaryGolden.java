package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Calculates binary recall and completeness for complex correspondences
 * @author Matthias Weidlich
 *
 */
public class ComplexBinaryGolden implements K2Statistic {
	private ArrayList<String[]> data;
	private String[] header;
	Set<String> matchListIds = new HashSet<String>();
	Set<String> exactMatchListIds = new HashSet<String>();
	
	Map<Long, Set<Long>> candToTar = new HashMap<Long, Set<Long>>();
	Map<Long, Set<Long>> tarToCand = new HashMap<Long, Set<Long>>();

	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "Complex Binary Golden Statistic";
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public boolean init(String instanceDescription, MatchInformation mi) {
		return false; //Golden statistics don't implement this method
	}

	/**
	 * Checks whether a given attribute pair is part of a complex correspondence.
	 * 
	 * @param mi the MatchInformation object holding the mapping
	 * @param m the Match object representing the attribute pair
	 * @return true if the attribute pair is part of a complex correspondence, false otherwise
	 */
	private boolean isPartOfComplexMatch(MatchInformation mi, Match m) {
		for (Match m2 : mi.getCopyOfMatches()) {
			if ((m2.getCandidateTerm().getId() == m.getCandidateTerm().getId() && m2.getTargetTerm().getId() != m.getTargetTerm().getId())
					|| (m2.getCandidateTerm().getId() != m.getCandidateTerm().getId() && m2.getTargetTerm().getId() == m.getTargetTerm().getId()))
				return true;
		}
		return false;
	}
	
	public boolean init(String instanceDescription, MatchInformation mi, MatchInformation exactMatch) {
		
		
		for (Match m : mi.getCopyOfMatches()) {
			Long candID = m.getCandidateTerm().getId();
			Long targID = m.getTargetTerm().getId();
			
			matchListIds.add(candID.toString()+","+targID.toString());
		}
		for (Match m : exactMatch.getCopyOfMatches()) {
			if (isPartOfComplexMatch(exactMatch, m)) {
				Long candID = m.getCandidateTerm().getId();
				Long targID = m.getTargetTerm().getId();
				
				if (!candToTar.containsKey(candID))
					candToTar.put(candID,new HashSet<Long>());

				candToTar.get(candID).add(targID);
				
				exactMatchListIds.add(candID.toString()+","+targID.toString());
			}
		}

		data = new ArrayList<String[]>();
		header = new String[]{"instance","Share Complex", "Complex Recall", "Complex Completeness"};
		Double recall = calcRecall();
		Double compl = calcCompl();
		Double shareComplex = ((double) exactMatchListIds.size()) / ((double) exactMatch.getNumMatches());
		data.add(new String[] {instanceDescription, shareComplex.toString(), recall.toString(),compl.toString()});
		return true;
	}
	
	private Double calcRecall() {
		Double truePositives = calcTruePositives();
		Double exact = (double) exactMatchListIds.size();
		return (exact==0?0:truePositives/exact);
	}
	
	/**
	 * Returns the average completeness of all complex correspondences.
	 * The completeness of a complex correspondence refers to the extent 
	 * to which the expected attribute pairs related to a complex 
	 * correspondence (Cartesian product of the attributes of two schemas 
	 * that are covered by the complex correspondence) are actually 
	 * represented by attribute pairs in the MatchInformation object.
	 * 
	 * @return the average of the completeness values of all complex correspondences
	 */
	private Double calcCompl() {

		List<Set<String>> complexSets = new ArrayList<Set<String>>();
		
		for (Long cId : this.candToTar.keySet()) {
			Set<String> complexSet = new HashSet<String>();
			for (Long tId : this.candToTar.get(cId))
				complexSet.add(cId.toString()+","+tId.toString());
			complexSets.add(complexSet);
		}
		
		/*
		 * Build up the actual complex correspondences from the 
		 * attribute pairs		
		 */
		boolean found = true;
		while (found) {
			List<Set<String>> complexSets2 = new ArrayList<Set<String>>();
			for (Set<String> set1 : complexSets) {
				for (Set<String> set2 : complexSets) {
					if (set1.equals(set2))
						continue;
					found = false;
					for (String id1 : set1) {
						String[] ids1 = id1.split(",");
						for (String id2 : set2) {
							String[] ids2 = id2.split(",");
							if (ids1[0].equals(ids2[0]) || ids1[1].equals(ids2[1])) {
								found = true;
								break;
							}
						}
					}
					if (found) {
						set1.addAll(set2);
					}
				}
				if (!complexSets2.contains(set1))
					complexSets2.add(set1);
			}
			complexSets = complexSets2;
			if (complexSets2.size() < 2)
				found = false;
		}

		/*
		 * Get the completeness for those
		 */
		double complSum = 0;
		double complCount = 0;
		for (Set<String> complexSet : complexSets) {
			double foundPairs = 0;
			for (String match : this.matchListIds) {
				if (complexSet.contains(match))
					foundPairs++;
			}
			if (foundPairs > 0) {
				complSum += foundPairs / ((double) complexSet.size());
				complCount++;
			}
		}
		
		return (complCount == 0)? 0 : complSum / complCount;
	}


	private Double calcTruePositives() {
		Double res = 0.0;
		for (String match : matchListIds)
		{
			if (exactMatchListIds.contains(match)) res+=1.0;
		}
		return res;
	}

}

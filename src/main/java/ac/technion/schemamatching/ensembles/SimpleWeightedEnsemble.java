/**
 * 
 */
package ac.technion.schemamatching.ensembles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * @author Tomer Sagi
 * Simple weighted ensemble using weights supplied in init
 */
public class SimpleWeightedEnsemble implements Ensemble {

	private String name = "Simple Weighted Ensemble";
	HashMap<String, MatchInformation> matches = new HashMap<String, MatchInformation>();
	HashMap<String, Double> matcherWeights = new HashMap<String, Double>();
	MatchInformation res;
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.ensembles.Ensemble#getConcensusMatch()
	 */
	public MatchInformation getConcensusMatch() {
		vote(matches.size(),false);
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.ensembles.Ensemble#getMajorityVoteMatch()
	 */
	public MatchInformation getMajorityVoteMatch() {
		double minVote = (double) matches.size()/2.0;
		vote(minVote,false);
		return res;
	}

	/**
	 * @param minVote sum of votes will be compared to this number. If it is greater or equal, the match will pass 
	 * @param useWeights if true will count each vote as the corresponding matcher's weight. otherwise will count each vote as 1. 
	 */
	private void vote(double minVote,boolean useWeights) {
		HashMap<Match,Double> matchVotes = new HashMap<Match,Double>();
		for (String mName : matches.keySet())
		{
			MatchInformation mi = matches.get(mName);
			for (Match m : mi.getCopyOfMatches())
			{
				Double vote = (matchVotes.containsKey(m)?matchVotes.get(m):0.0);
				vote+=(useWeights?matcherWeights.get(mName):1.0);
				matchVotes.put(m, vote);
			}
		}
		ArrayList<Match> voted = new ArrayList<Match>(); 
		for (Match m : matchVotes.keySet())
		{
			if (matchVotes.get(m) >= minVote)
			{
				Match v = new Match(m.getCandidateTerm(),m.getTargetTerm(),1.0);
				voted.add(v);
			}
		}
		res.setMatches(voted);
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.ensembles.Ensemble#getWeightedVoteMatch()
	 */
	public MatchInformation getWeightedVoteMatch() {
		double minVote = 0.5;
		vote(minVote,true);
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.ensembles.Ensemble#getWeightedMatch()
	 */
	public MatchInformation getWeightedMatch() {
		//Prepare match list
		HashMap<Match,Double> matchVotes = new HashMap<Match,Double>();
		for (String mName : matches.keySet())
		{
			MatchInformation mi = matches.get(mName);
			for (Match m : mi.getCopyOfMatches())
			{
				Double vote = 0.0;
				if (matchVotes.containsKey(m))
						vote = matchVotes.get(m);
				vote+=matcherWeights.get(mName)*m.getEffectiveness();
				matchVotes.put(m, vote);
			}
		}
		ArrayList<Match> voted = new ArrayList<Match>(); 
		for (Match m : matchVotes.keySet())
		{
			Match v = new Match(m.getTargetTerm(),m.getCandidateTerm(),matchVotes.get(m));
			voted.add(v);
		}
		
		for (int i=0; i<res.getMatrix().getRowCount();i++)
			for (int j=0; j<res.getMatrix().getColCount();j++)
			{
				double e = 0.0;
				for (MatchInformation mi : matches.values())
					e+=mi.getMatrix().getMatchConfidenceAt(i, j);
				res.getMatrix().setMatchConfidenceAt(i, j, e/matches.size());
			}
		res.setMatches(voted);
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.ensembles.Ensemble#init(java.util.HashMap, java.util.HashMap)
	 */
	public boolean init(Map<String, MatchInformation> matches,
			Map<String, Double> matcherWeights) {
		
		//init matches
		if (matches == null || matches.isEmpty()) return false;
		this.matches = new HashMap<String, MatchInformation>();
		MatchInformation someMI = (MatchInformation)matches.values().toArray()[0];
		res = new MatchInformation(someMI.getCandidateOntology(),someMI.getTargetOntology());
		
		//expand all matrices to fit res
		for (String m : matches.keySet())
		{
			try {
				this.matches.put(m,ConversionUtils.expandMatrix(matches.get(m), res));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		
		//init matcher weights
		if (matcherWeights == null || matcherWeights.isEmpty())
			//Default to simple weights
			for (String mName : matches.keySet())
				this.matcherWeights.put(mName, new Double(1.0/matches.size()));
		else
		{
			//Normalize weights to 1
			double sum = 0.0;
			for (Double val : matcherWeights.values())
				sum+=val;
			
			for (String mName : matcherWeights.keySet())
			{
				if (matches.containsKey(mName))
				{
					Double val = matcherWeights.get(mName)/sum;
					this.matcherWeights.put(mName, val);
				}
			}
			if (this.matcherWeights.size() != matches.size())
			{
				for (String mName : matches.keySet())
				{
					if (!this.matcherWeights.containsKey(mName))
						this.matcherWeights.put(mName, 0.0);
				}
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.ensembles.Ensemble#getName()
	 */
	public String getName() {
		return name;
	}

}

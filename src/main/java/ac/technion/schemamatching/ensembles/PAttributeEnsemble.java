/**
 * 
 */
package ac.technion.schemamatching.ensembles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.schemamatching.statistics.predictors.Predictor;
import ac.technion.schemamatching.statistics.predictors.PredictorList;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * @author Tomer Sagi
 * Predictor based ensemble using weights supplied in init to weight attribute level predictors
 */
public class PAttributeEnsemble implements Ensemble {

	private String name = "Predictor Weighted Attribute Ensemble";
	Map<String, MatchInformation> matches = new HashMap<String, MatchInformation>();
	Map<String, Double> predictorWeights = new HashMap<String, Double>();
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
				vote+=(useWeights?predictorWeights.get(mName):1.0);
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
				vote+=predict(m,mi.getMatrix())*m.getEffectiveness();
				matchVotes.put(m, vote);
			}
		}
		ArrayList<Match> voted = new ArrayList<Match>(); 
		for (Match m : matchVotes.keySet())
		{
			Match v = new Match(m.getTargetTerm(),m.getCandidateTerm(),matchVotes.get(m));
			voted.add(v);
		}
		
		//update matrix by weighted prediction
		for (Term c : res.getMatrix().getCandidateTerms())
			for (Term t : res.getMatrix().getTargetTerms())
			{
				double e = 0.0;
				for (MatchInformation mi : matches.values())
				{
					MatchMatrix mm = mi.getMatrix(); 
					double conf = mm.getMatchConfidence(c, t);
					double p = predict(new Match(t,c,conf) ,mm);
					if (p>0) e+=p*conf;
				}
					
				res.getMatrix().setMatchConfidence(c, t, e/matches.size());
			}
		res.setMatches(voted);
		return res;
	}

	/**
	 * Predicts the match using all predictors and predictorWeights over the corresponding matrix row and column. Averages the result
	 * @param m
	 * @return
	 */
	private double predict(Match m,MatchMatrix mm) 
	{
		double pValue = 0.0;
		for (String pName: predictorWeights.keySet())
		{
			//Predict candidate
			Predictor rp = PredictorList.valueOf(pName).getPredictor();
			rp.init(1,mm.getRowCount());
			rp.newRow();
			for (Term t : mm.getTargetTerms())
				rp.visitColumn(mm.getMatchConfidence(m.getCandidateTerm(), t));
			double rpVal = rp.getRes();
			
			//Predict target
			Predictor cp = PredictorList.valueOf(pName).getPredictor();
			cp.init(1,mm.getColCount());
			cp.newRow();
			for (Term t : mm.getCandidateTerms())
				cp.visitColumn(mm.getMatchConfidence(t, m.getTargetTerm()));
			double cpVal = cp.getRes();
			
			pValue+=0.5*(cpVal+rpVal)*predictorWeights.get(pName);
		}
		return pValue/predictorWeights.size();
	}

	/**
	 * @param predictorWeights replaces interface parameter. Use to send the attribute predictor weights
	 */
	public boolean init(Map<String, MatchInformation> matches,
			Map<String, Double> predictorWeights) {
		
		//init matches
		if (matches == null || matches.isEmpty()) return false;
		this.matches = matches;
		MatchInformation someMI = (MatchInformation)matches.values().toArray()[0];
		res = new MatchInformation(someMI.getCandidateOntology(),someMI.getTargetOntology());
		
		//expand all matrices to fit res
		for (String m : matches.keySet())
		{
			try {
				matches.put(m,ConversionUtils.expandMatrix(matches.get(m), res));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		
		//init predictor weights
		if (predictorWeights == null || predictorWeights.isEmpty())
			return false;
		this.predictorWeights = predictorWeights;
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.ensembles.Ensemble#getName()
	 */
	public String getName() {
		return name;
	}

}

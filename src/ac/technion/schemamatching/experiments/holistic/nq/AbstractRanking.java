package ac.technion.schemamatching.experiments.holistic.nq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.schemamatching.experiments.holistic.nq.NetworkStrengthFunctions.NetworkStrengthType;
import ac.technion.schemamatching.testbed.ExperimentSchema;

public abstract class AbstractRanking implements Ranking {

	protected NetworkModel network;
	
	protected int maxID;
	
	/*
	 * Value matrices for the network
	 */
	protected double[][] valueMatrix;


	/**
	 * Derives the values for the value matrices based on the match matrix. For each pair
	 * of terms it applies the respective strength function to obtain the value for the 
	 * value matrix.
	 */
	protected void updateValueMatrix(double[][] matchMatrix, NetworkStrengthType type) {
		
		for (Term t1 : this.network.gettIDMap().keySet()) {
			for (Term t2 : this.network.gettIDMap().keySet()) {
				setSymmetricMatrixEntry(t1, t2, 
						NetworkStrengthFunctions.applyStrengthFunction(
								matchMatrix[this.network.gettIDMap().get(t1)]
										[this.network.gettIDMap().get(t2)], type));
			}
		}
	}

	protected void initRankingApproach(NetworkModel network, NetworkStrengthType type) {
		
		this.network = network;
		this.maxID = 0;
		
		for (Integer i : this.network.gettIDMap().values())
			this.maxID = Math.max(this.maxID, i);
		
		this.maxID++;

		this.valueMatrix = new double[maxID][maxID];
		
		this.updateValueMatrix(network.getMatchMatrix(),type);
	}

	
	/**
	 * Set entry in the value matrix in a symmetric way
	 * 
	 * @param t1 first term
	 * @param t2 second term
	 * @param value matrix value for the two terms
	 */
	protected void setSymmetricMatrixEntry(Term t1, Term t2, double value) {
		assert(network.gettIDMap().containsKey(t1) && network.gettIDMap().containsKey(t2)) : "Terms are not known in the network";
		this.valueMatrix[network.gettIDMap().get(t1)]
				[network.gettIDMap().get(t2)] = value;
		this.valueMatrix[network.gettIDMap().get(t2)]
				[network.gettIDMap().get(t1)] = value;
	}
	
	public Map<Term, Double> getAdjacentNetworkValuesForTerms() {
		Map<Term, Double> adjacentNetworkValues = new HashMap<Term, Double>();
		
		for (Term t : this.network.gettIDMap().keySet()) {
			int i = this.network.gettIDMap().get(t);
			double sum = 0;
			for (int j : this.network.gettIDMap().values()) 
				sum += this.valueMatrix[i][j];
			adjacentNetworkValues.put(t, sum);
		}	
		return adjacentNetworkValues;
	}
	
	protected List<Match> getMatchesSortedByDegreeCentrality(int count, Set<Match> toExclude) {
		return getMatchesSortedByDegreeCentrality(count, toExclude, 50, null);
	}

	
	protected List<Match> getMatchesSortedByDegreeCentrality(int count, Set<Match> toExclude, int cutSelector, Map<Term, Double> adjacentNetworkValues) {
		List<Match> result = new ArrayList<Match>();
		
		Map<Term, ExperimentSchema> tToSchemaMap = this.network.getTermToSchemaMap();
		
		if (adjacentNetworkValues == null)
			adjacentNetworkValues = this.getAdjacentNetworkValuesForTerms();
		
		/*
		 * Determine the terms we need to consider,
		 * kind of an heuristic, should have zero consequences
		 * in all reasonable cases though
		 */
		List<Double> networkValues = new ArrayList<Double>(adjacentNetworkValues.values());
		Collections.sort(networkValues);
		Collections.reverse(networkValues);
		double cut = networkValues.size() > cutSelector ? networkValues.get(cutSelector) : networkValues.get(networkValues.size() - 1);
		
		for (Term t1 : adjacentNetworkValues.keySet()) {
			if (adjacentNetworkValues.get(t1) < cut)
				continue;
			for (Term t2 : adjacentNetworkValues.keySet()) {
				if (adjacentNetworkValues.get(t2) < cut)
					continue;
				if (tToSchemaMap.get(t1).equals(tToSchemaMap.get(t2)))
					continue;
				if (this.valueMatrix[network.gettIDMap().get(t1)][network.gettIDMap().get(t2)] > 0.9999d)
					continue;
				
				double degreeCentrality = 
						adjacentNetworkValues.get(t1) 
						+ adjacentNetworkValues.get(t2)
						- this.valueMatrix[network.gettIDMap().get(t1)][network.gettIDMap().get(t2)]
						- this.valueMatrix[network.gettIDMap().get(t2)][network.gettIDMap().get(t1)];
				
				Match m1 = new Match(t1, t2, degreeCentrality);
				Match m2 = new Match(t2, t1, degreeCentrality);
				if (!result.contains(m1) 
						&& !result.contains(m2)
						&& !toExclude.contains(m1)						
						&& !toExclude.contains(m2))
					result.add(m1);
			}
		}
		
		Collections.sort(result, new Comparator<Match> () {
			public int compare(Match arg0, Match arg1) {
				if (arg0.getEffectiveness() == arg1.getEffectiveness()) {
		            return 0;
		        } else if (arg0.getEffectiveness() > arg1.getEffectiveness()) {
		            return -1;
		        } else {
		            return 1;
		        } 
			}
		});
		
		/*
		 * We may have only a partial result and need to go into another iteration
		 */
		if (result.size() < count) {
			toExclude.addAll(result);
			result.addAll(getMatchesSortedByDegreeCentrality(count - result.size(), toExclude, cutSelector + 50, adjacentNetworkValues));
		}
		
		return new ArrayList<Match>(result.subList(0, count));
	}
	
	protected List<Match> getMatchesSortedByBetweennessCentrality(int count, Set<Match> toExclude) {
		return getMatchesSortedByBetweennessCentrality(count, toExclude, 50, null);
	}

	
	protected List<Match> getMatchesSortedByBetweennessCentrality(int count, Set<Match> toExclude, int cutSelector, NetworkBetweenness betweenness) {

		List<Match> result = new ArrayList<Match>();
		
		Map<Term, ExperimentSchema> tToSchemaMap = this.network.getTermToSchemaMap();
		if (betweenness == null)
			betweenness = new NetworkBetweenness(this.network.gettIDMap(), this.valueMatrix);
		
		Map<Term, Double> attBetweennessValues = betweenness.getAttBetweennessValuesForTerms();
		
		/*
		 * Determine the terms we need to consider,
		 * kind of an heuristic, need to check the influence 
		 * of this heuristic
		 */
		List<Double> betweennessValues = new ArrayList<Double>(attBetweennessValues.values());
		Collections.sort(betweennessValues);
		Collections.reverse(betweennessValues);
		double cut = betweennessValues.size() > cutSelector ? betweennessValues.get(cutSelector) : betweennessValues.get(betweennessValues.size() - 1);
		
		for (Term t1 : attBetweennessValues.keySet()) {
			if (attBetweennessValues.get(t1) < cut)
				continue;
			for (Term t2 : attBetweennessValues.keySet()) {
				if (attBetweennessValues.get(t2) < cut)
					continue;
				if (tToSchemaMap.get(t1).equals(tToSchemaMap.get(t2)))
					continue;
				if (this.valueMatrix[network.gettIDMap().get(t1)][network.gettIDMap().get(t2)] > 0.9999d)
					continue;
				
				double betweennessCentrality = betweenness.getBetweenness(t1, t2);
				Match m1 = new Match(t1, t2, betweennessCentrality);
				Match m2 = new Match(t2, t1, betweennessCentrality);
				if (!result.contains(m1) 
						&& !result.contains(m2)
						&& !toExclude.contains(m1)						
						&& !toExclude.contains(m2))
					result.add(m1);
			}
		}
		
		Collections.sort(result, new Comparator<Match> () {
			public int compare(Match arg0, Match arg1) {
				if (arg0.getEffectiveness() == arg1.getEffectiveness()) {
		            return 0;
		        } else if (arg0.getEffectiveness() > arg1.getEffectiveness()) {
		            return -1;
		        } else {
		            return 1;
		        } 
			}
		});
		
		/*
		 * We may have only a partial result and need to go into another iteration
		 */
		if (result.size() < count) {
			System.out.print(" iterate ");
			toExclude.addAll(result);
			result.addAll(getMatchesSortedByBetweennessCentrality(count - result.size(), toExclude, cutSelector + 50, betweenness));
		}
		
		return new ArrayList<Match>(result.subList(0, count));
	}

	
}

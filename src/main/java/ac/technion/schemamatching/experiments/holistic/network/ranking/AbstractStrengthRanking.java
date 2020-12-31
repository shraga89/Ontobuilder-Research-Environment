package ac.technion.schemamatching.experiments.holistic.network.ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.schemamatching.experiments.holistic.network.NetworkBetweenness;
import ac.technion.schemamatching.experiments.holistic.network.NetworkStrengthFunctions;
import ac.technion.schemamatching.experiments.holistic.network.NetworkStrengthFunctions.NetworkStrengthType;
import ac.technion.schemamatching.experiments.holistic.network.SchemaNetwork;
import ac.technion.schemamatching.testbed.ExperimentSchema;

/**
 * Abstract class for ranking strategies based on strength. 
 * Those strategies rank matches in a schema network for 
 * seeking user feedback (may be simulated) based on 1) different
 * strength functions applied to match probabilities and 
 * 2) centrality measures for attribute nodes in the network.
 * 
 * @author matthias weidlich
 *
 */
public abstract class AbstractStrengthRanking implements Ranking {

	/**
	 * The original schema network with the probabilities for all matches
	 */
	protected SchemaNetwork network;
	
	/**
	 * Strength matrix derived for the network by 
	 * applying one of the strength functions
	 */
	protected double[][] strengthMatrix;


	/**
	 * Derives the values for the strength matrix from the network. For each pair
	 * of terms it applies the respective strength function to obtain the value for the 
	 * strength matrix.
	 */
	protected void updateStrengthMatrix(double[][] matchMatrix, NetworkStrengthType type) {
		
		for (Term t1 : this.network.getTermToIDMap().keySet()) {
			for (Term t2 : this.network.getTermToIDMap().keySet()) {
				setSymmetricMatrixEntry(t1, t2, 
						NetworkStrengthFunctions.applyStrengthFunction(
								matchMatrix[this.network.getTermToIDMap().get(t1)]
										[this.network.getTermToIDMap().get(t2)], type));
			}
		}
	}

	/**
	 * Initialise the strength based ranking with a schema network and a certain type of 
	 * strength function
	 * 
	 * @param network
	 * @param type
	 */
	protected void initRankingApproach(SchemaNetwork network, NetworkStrengthType type) {
		
		this.network = network;
		
		/*
		 * Get the maximal ID used in the Term to ID mapping
		 */
		int maxID = 0;
		for (Integer i : this.network.getTermToIDMap().values())
			maxID = Math.max(maxID, i);
		
		maxID++;

		this.strengthMatrix = new double[maxID][maxID];
		
		this.updateStrengthMatrix(network.getMatchMatrix(),type);
	}

	
	/**
	 * Set entry in the value matrix in a symmetric way
	 * 
	 * @param t1 first term
	 * @param t2 second term
	 * @param value matrix value for the two terms
	 */
	protected void setSymmetricMatrixEntry(Term t1, Term t2, double value) {
		assert(network.getTermToIDMap().containsKey(t1) && network.getTermToIDMap().containsKey(t2)) : "Terms are not known in the network";
		this.strengthMatrix[network.getTermToIDMap().get(t1)]
				[network.getTermToIDMap().get(t2)] = value;
		this.strengthMatrix[network.getTermToIDMap().get(t2)]
				[network.getTermToIDMap().get(t1)] = value;
	}
	
	public Map<Term, Double> getAdjacentNetworkValuesForTerms() {
		Map<Term, Double> adjacentNetworkValues = new HashMap<Term, Double>();
		
		for (Term t : this.network.getTermToIDMap().keySet()) {
			int i = this.network.getTermToIDMap().get(t);
			double sum = 0;
			for (int j : this.network.getTermToIDMap().values()) 
				sum += this.strengthMatrix[i][j];
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
				if (this.strengthMatrix[network.getTermToIDMap().get(t1)][network.getTermToIDMap().get(t2)] > 0.9999d)
					continue;
				
				double degreeCentrality = 
						adjacentNetworkValues.get(t1) 
						+ adjacentNetworkValues.get(t2)
						- this.strengthMatrix[network.getTermToIDMap().get(t1)][network.getTermToIDMap().get(t2)]
						- this.strengthMatrix[network.getTermToIDMap().get(t2)][network.getTermToIDMap().get(t1)];
				
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

	
	protected List<Match> getMatchesSortedByBetweennessCentrality(int count, Set<Match> toExclude, int cutSelector, NetworkBetweenness<Term> betweenness) {

		List<Match> result = new ArrayList<Match>();
		
		Map<Term, ExperimentSchema> tToSchemaMap = this.network.getTermToSchemaMap();
		if (betweenness == null)
			betweenness = new NetworkBetweenness<Term>(this.network.getTermToIDMap(), this.strengthMatrix);
		
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
				if (this.strengthMatrix[network.getTermToIDMap().get(t1)][network.getTermToIDMap().get(t2)] > 0.9999d)
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

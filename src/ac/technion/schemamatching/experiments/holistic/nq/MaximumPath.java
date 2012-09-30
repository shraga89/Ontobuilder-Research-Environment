package ac.technion.schemamatching.experiments.holistic.nq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;

public class MaximumPath {

	private Map<Term, Integer> tSetMap;
	private Map<Integer, Term> revTSetMap;
	
	private int maxID;
	
	private double[][] distanceMatrix;
	private int[][] pathMatrix;
	
	public MaximumPath(Map<Term, Integer> tSetMap, double[][] matrix) {
		this.tSetMap = tSetMap;
		
		this.revTSetMap = new HashMap<Integer, Term>();
		for (Term t : this.tSetMap.keySet())
			this.revTSetMap.put(this.tSetMap.get(t), t);
		
		this.maxID = 0;
		for (Integer i : this.tSetMap.values())
			this.maxID = Math.max(this.maxID, i);

		this.maxID++;
		
		assert(tSetMap.size() == this.maxID) : "Term to ID mapping is inconsistent!";
		
		/*
		 * Calculate the distance matrix based on the
		 * Floyd-Warschall algorithm.
		 */
		this.distanceMatrix = new double[this.maxID][this.maxID];
		this.pathMatrix = new int[this.maxID][this.maxID];		
		
		for (int i = 0; i < this.maxID; i++) {
			Arrays.fill(this.distanceMatrix[i], 0);
			Arrays.fill(this.pathMatrix[i], -1);
		}
		
		for (int i = 0; i < this.maxID; i++) 
			for (int j = 0; j < this.maxID; j++) 
				this.distanceMatrix[i][j] = matrix[i][j];

		for (int k = 0; k < this.maxID; k++) {
			for (int i = 0; i < this.maxID; i++) {
				for (int j = 0; j < this.maxID; j++) {
//					if (k != j && k != i &&							
//							this.distanceMatrix[i][j] == this.distanceMatrix[i][k] * this.distanceMatrix[k][j]) {
//						this.maxPathsCountMatrix[i][j]++;
//						System.out.println(this.maxPathsCountMatrix[i][j]);
//						System.out.println(this.distanceMatrix[i][j] +" "+ this.distanceMatrix[i][k] +" "+ this.distanceMatrix[k][j]);
//						this.maxMaxPathCount = Math.max(this.maxMaxPathCount, this.maxPathsCountMatrix[i][j]);
//						if (this.maxPathsCountMatrix[i][j] > this.maxInCache || this.numberInCache < this.cacheSize) {
//							if (!this.cache.containsKey(this.maxPathsCountMatrix[i][j]))
//									this.cache.put(this.maxPathsCountMatrix[i][j], new HashSet<Match>());
//							this.cache.get(this.maxPathsCountMatrix[i][j]).add(new Match(this.revTSetMap.get(i), this.revTSetMap.get(j), 1.0d));
//							this.maxInCache = Math.max(maxInCache, this.maxPathsCountMatrix[i][j]);
//							this.numberInCache++;
//							if (this.numberInCache > this.cacheSize) {
//								List<Integer> keys = new ArrayList<Integer>(this.cache.keySet());
//								Collections.sort(keys);
//								int min = keys.get(0);
//								if (this.numberInCache - this.cache.get(min).size() > this.cacheSize) {
//									this.numberInCache -= this.cache.get(keys.get(0)).size();
//									this.cache.remove(keys.get(0));
//								}
//							}
//						}
//					}
//					else 
						if (this.distanceMatrix[i][k] * this.distanceMatrix[k][j] > this.distanceMatrix[i][j]){
//						System.out.println(this.revTSetMap.get(i) + " " + this.revTSetMap.get(j) + " " + this.revTSetMap.get(k));
//						System.out.println(this.distanceMatrix[i][j] + " " + this.distanceMatrix[i][k] + " " + this.distanceMatrix[k][j]);
						this.distanceMatrix[i][j] = this.distanceMatrix[i][k] * this.distanceMatrix[k][j];
						this.pathMatrix[i][j] = k;
					}
				}
			}
		}
	}

	public double getMaximumBetweenTerms(Term t1, Term t2) {
		assert(this.tSetMap.containsKey(t1) && this.tSetMap.containsKey(t2)) : "Terms are not known: " + t1.getName() + " " + t2.getName();
		
		return this.distanceMatrix[this.tSetMap.get(t1)][this.tSetMap.get(t2)];
	}
	
	public List<Term> getMaximalPath(Term from, Term to){
		if (this.distanceMatrix[this.tSetMap.get(from)][this.tSetMap.get(to)] == Double.MAX_VALUE){
	           return new ArrayList<Term>();
		}
	    
		List<Term> path = getIntermediatePath(from, to);
		path.add(0, from);
	    path.add(to);
	    return path;
	}
	
	private List<Term> getIntermediatePath(Term from, Term to){
		if(this.pathMatrix[this.tSetMap.get(from)][this.tSetMap.get(to)] == -1){
			return new ArrayList<Term>();
		}
		
		List<Term> path = new ArrayList<Term>();
		path.addAll(getIntermediatePath(from, this.revTSetMap.get(this.pathMatrix[this.tSetMap.get(from)][this.tSetMap.get(to)])));
		path.add(this.revTSetMap.get(this.pathMatrix[this.tSetMap.get(from)][this.tSetMap.get(to)]));
		path.addAll(getIntermediatePath(this.revTSetMap.get(this.pathMatrix[this.tSetMap.get(from)][this.tSetMap.get(to)]), to));
	    
		return path;
	}
	
}



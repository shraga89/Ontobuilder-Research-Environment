package ac.technion.schemamatching.experiments.holistic.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ac.technion.iem.ontobuilder.core.ontology.OntologyObject;
import ac.technion.schemamatching.experiments.holistic.network.NetworkAggregationFunctions.NetworkAggregationType;

/**
 * Class for computing the maximal paths between a set of objects in a network.
 * 
 * @author matthias weidlich
 *
 * @param <O> a type of an ontology object
 */
public class NetworkMaximalPath<O extends OntologyObject> {

	/*
	 * Object IDs for matrices
	 */
	private Map<O, Integer> objectIDs;
	/*
	 * Reversed ID map 
	 */
	private Map<Integer, O> reversedObjectIDs;

	/*
	 * Maximal ID
	 */
	private int maxID;
	
	/*
	 * Matrix capturing all distances between the objects
	 */
	private double[][] distanceMatrix;
	
	/*
	 * Matrix capturing the path information for all max distances between the objects
	 */
	private int[][] pathMatrix;
	
	/*
	 * Aggregation of values to measure the distance of two objects
	 */
	private NetworkAggregationType type;
	
	public NetworkMaximalPath(Map<O, Integer> objectIDs, double[][] matrix, NetworkAggregationType type) {
		this.objectIDs = objectIDs;
		
		this.reversedObjectIDs = new HashMap<Integer, O>();
		for (O t : this.objectIDs.keySet())
			this.reversedObjectIDs.put(this.objectIDs.get(t), t);
		
		this.maxID = 0;
		for (Integer i : this.objectIDs.values())
			this.maxID = Math.max(this.maxID, i);

		this.maxID++;
		
		assert(objectIDs.size() == this.maxID) : "Term to ID mapping is inconsistent!";
		
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
					if (NetworkAggregationFunctions.applyAggregationFunction(
							this.distanceMatrix[i][k], this.distanceMatrix[k][j], this.type)
							> this.distanceMatrix[i][j]){
						this.distanceMatrix[i][j] = NetworkAggregationFunctions.applyAggregationFunction(this.distanceMatrix[i][k], this.distanceMatrix[k][j], this.type);
						this.pathMatrix[i][j] = k;
					}
				}
			}
		}
	}

	public double getMaximumBetweenTerms(O t1, O t2) {
		assert(this.objectIDs.containsKey(t1) && this.objectIDs.containsKey(t2)) : "Terms are not known: " + t1.getName() + " " + t2.getName();
		
		return this.distanceMatrix[this.objectIDs.get(t1)][this.objectIDs.get(t2)];
	}
	
	public List<O> getMaximalPath(O from, O to){
		if (this.pathMatrix[this.objectIDs.get(from)][this.objectIDs.get(to)] == -1)
	           return new ArrayList<O>();
	    
		List<O> path = getIntermediatePath(from, to);
		path.add(0, from);
	    path.add(to);
	    return path;
	}
	
	private List<O> getIntermediatePath(O from, O to){
		if(this.pathMatrix[this.objectIDs.get(from)][this.objectIDs.get(to)] == -1)
			return new ArrayList<O>();
		
		List<O> path = new ArrayList<O>();
		path.addAll(getIntermediatePath(from, this.reversedObjectIDs.get(this.pathMatrix[this.objectIDs.get(from)][this.objectIDs.get(to)])));
		path.add(this.reversedObjectIDs.get(this.pathMatrix[this.objectIDs.get(from)][this.objectIDs.get(to)]));
		path.addAll(getIntermediatePath(this.reversedObjectIDs.get(this.pathMatrix[this.objectIDs.get(from)][this.objectIDs.get(to)]), to));
	    
		return path;
	}
	
}



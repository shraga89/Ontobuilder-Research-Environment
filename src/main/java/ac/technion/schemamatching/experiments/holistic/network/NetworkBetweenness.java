package ac.technion.schemamatching.experiments.holistic.network;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import ac.technion.iem.ontobuilder.core.ontology.OntologyObject;

/**
 * Computes betweenness centrality for each object and edge in the graph. 
 * 
 * This implementation is adapted from the JUNG project. For further details 
 * on the JUNG project, copyright details, and the respective BSD license 
 * see
 * http://jung.sourceforge.net/license.txt for a description.
 * 
 * Running time is: O(n^2 + nm).
 * @see "Ulrik Brandes: A Faster Algorithm for Betweenness Centrality. Journal of Mathematical Sociology 25(2):163-177, 2001."
 * 
 * @author Scott White
 * @author Tom Nelson 
 * @author matthias weidlich
 *
 * @param <O> a type of an ontology object
 */
public class NetworkBetweenness<O extends OntologyObject> {

	private Map<O, Integer> objectIDs;
	
	private int maxID;
	
	private double[] attributeBetweennessMatrix;
	private double[][] matchBetweennessMatrix;
	private double[][] valueMatrix;

	/**
	 * Auxiliary class to capture temporary data needed for the 
	 * betweenness computation.
	 */
	private class BetweennessData {
        double distance;
        double numSPs;
        double dependency;

        BetweennessData() {
            distance = -1;
            numSPs = 0;
            dependency = 0;
        }
	}

	public NetworkBetweenness (Map<O, Integer> objectIDs, double[][] matrix) {
		this.objectIDs = objectIDs;
		
		this.maxID = 0;
		for (Integer i : this.objectIDs.values())
			this.maxID = Math.max(this.maxID, i);

		this.maxID++;
		
		assert(objectIDs.size() == this.maxID) : "O to ID mapping is inconsistent!";

		this.valueMatrix = matrix;
		this.attributeBetweennessMatrix = new double[this.maxID];
		this.matchBetweennessMatrix = new double[this.maxID][this.maxID];
		
		Arrays.fill(this.attributeBetweennessMatrix, -1.0d);
		for (int i = 0; i < this.maxID; i++)
			Arrays.fill(this.matchBetweennessMatrix[i], -1.0d);
		
		computeBetweenness();
	}
		
	public double getBetweenness(O t1, O t2) {
		assert(this.objectIDs.containsKey(t1) && this.objectIDs.containsKey(t2)) : "Os are not known: " + t1.getName() + " " + t2.getName();
		
		return this.matchBetweennessMatrix[this.objectIDs.get(t1)][this.objectIDs.get(t2)];
	}
	
    private void initializeData(Map<Integer,BetweennessData> stepData) {
		for (int i = 0; i < this.maxID; i++) {
        	if (this.attributeBetweennessMatrix[i] == -1.0d)
        		this.attributeBetweennessMatrix[i] = 0.0d;

            stepData.put(i, new BetweennessData());
            
            for (int j = 0; j < this.maxID; j++) 
            	if (this.matchBetweennessMatrix[i][j] == -1.0d)
            		this.matchBetweennessMatrix[i][j] = 0.0d;
		}
    }

	private void computeBetweenness() {
		
    	Map<Integer,BetweennessData> stepData = new HashMap<Integer,BetweennessData>();
        
		for (int s = 0; s < this.maxID; s++) {

			initializeData(stepData);
			
            stepData.get(s).numSPs = 1;
            stepData.get(s).distance = 0;

            Stack<Integer> stack = new Stack<Integer>();
            Queue<Integer> queue = new LinkedList<Integer>();
            queue.add(s);

            while (!queue.isEmpty()) {
                Integer v = queue.remove();
                stack.push(v);

        		for (int w = 0; w < this.maxID; w++) {
                    if (stepData.get(w).distance < 0.0d) {
                        queue.add(w);
                        stepData.get(w).distance = stepData.get(v).distance * this.valueMatrix[v][w];
                    }

                    // we cannot rely on java double comparison, so we approximate it
                    if ((((int)stepData.get(w).distance * 10000) == ((int)stepData.get(v).distance * this.valueMatrix[v][w] * 10000))) 
                        stepData.get(w).numSPs += stepData.get(v).numSPs;
                }
            }
            
            while (!stack.isEmpty()) {
                Integer w = stack.pop();

        		for (int v = 0; v < this.maxID; v++) {
                    double partialDependency = (stepData.get(v).numSPs / stepData.get(w).numSPs);
                    partialDependency *= (1.0 + stepData.get(w).dependency);
                    stepData.get(v).dependency +=  partialDependency;
                    this.matchBetweennessMatrix[v][w] += partialDependency;
                }
                if (w != s) 
                	this.attributeBetweennessMatrix[w] += stepData.get(w).dependency;
            }
        }

		for (int i = 0; i < this.maxID; i++) {
        	this.attributeBetweennessMatrix[i] /= 2.0d;
    		for (int j = 0; j < this.maxID; j++) {
            	this.matchBetweennessMatrix[i][j] /= 2.0d;
    		}
		}
	}

	public Map<O, Double> getAttBetweennessValuesForTerms() {
		Map<O, Double> result = new HashMap<O, Double>();
		
		for (O t : this.objectIDs.keySet())
        	result.put(t,this.attributeBetweennessMatrix[this.objectIDs.get(t)]);
        	
        return result;
	}
	
}

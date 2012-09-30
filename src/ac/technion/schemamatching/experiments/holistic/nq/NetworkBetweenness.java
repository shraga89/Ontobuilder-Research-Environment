package ac.technion.schemamatching.experiments.holistic.nq;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import ac.technion.iem.ontobuilder.core.ontology.Term;

public class NetworkBetweenness {

	private Map<Term, Integer> tSetMap;
	
	private int maxID;
	
	private double[] attributeBetweennessMatrix;
	private double[][] matchBetweennessMatrix;
	private double[][] valueMatrix;

	public NetworkBetweenness (Map<Term, Integer> tSetMap, double[][] matrix) {
		this.tSetMap = tSetMap;
		
		this.maxID = 0;
		for (Integer i : this.tSetMap.values())
			this.maxID = Math.max(this.maxID, i);

		this.maxID++;
		
		assert(tSetMap.size() == this.maxID) : "Term to ID mapping is inconsistent!";

		this.valueMatrix = matrix;
		this.attributeBetweennessMatrix = new double[this.maxID];
		this.matchBetweennessMatrix = new double[this.maxID][this.maxID];
		
		Arrays.fill(this.attributeBetweennessMatrix, -1.0d);
		for (int i = 0; i < this.maxID; i++)
			Arrays.fill(this.matchBetweennessMatrix[i], -1.0d);
		
		computeBetweenness();
	}
	
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
	
	public double getBetweenness(Term t1, Term t2) {
		assert(this.tSetMap.containsKey(t1) && this.tSetMap.containsKey(t2)) : "Terms are not known: " + t1.getName() + " " + t2.getName();
		
		return this.matchBetweennessMatrix[this.tSetMap.get(t1)][this.tSetMap.get(t2)];
	}
	
    private void initializeData(Map<Integer,BetweennessData> decorator) {
		for (int i = 0; i < this.maxID; i++) {
        	if (this.attributeBetweennessMatrix[i] == -1.0d)
        		this.attributeBetweennessMatrix[i] = 0.0d;

            decorator.put(i, new BetweennessData());
            
            for (int j = 0; j < this.maxID; j++) {
            	if (this.matchBetweennessMatrix[i][j] == -1.0d)
            		this.matchBetweennessMatrix[i][j] = 0.0d;
    		}
		}
    }

	private void computeBetweenness() {
		
    	Map<Integer,BetweennessData> decorator = new HashMap<Integer,BetweennessData>();
        
		for (int s = 0; s < this.maxID; s++) {

			initializeData(decorator);
			
            decorator.get(s).numSPs = 1;
            decorator.get(s).distance = 0;

            Stack<Integer> stack = new Stack<Integer>();
            Queue<Integer> queue = new LinkedList<Integer>();
            queue.add(s);

            while (!queue.isEmpty()) {
                Integer v = queue.remove();
                stack.push(v);

        		for (int w = 0; w < this.maxID; w++) {
                    if (decorator.get(w).distance < 0.0d) {
                        queue.add(w);
                        decorator.get(w).distance = decorator.get(v).distance * this.valueMatrix[v][w];
                    }

                    // we cannot rely on java double comparison, so we approximate it
                    if ((((int)decorator.get(w).distance * 10000) == ((int)decorator.get(v).distance * this.valueMatrix[v][w] * 10000))) 
                        decorator.get(w).numSPs += decorator.get(v).numSPs;
                }
            }
            
            while (!stack.isEmpty()) {
                Integer w = stack.pop();

        		for (int v = 0; v < this.maxID; v++) {
                    double partialDependency = (decorator.get(v).numSPs / decorator.get(w).numSPs);
                    partialDependency *= (1.0 + decorator.get(w).dependency);
                    decorator.get(v).dependency +=  partialDependency;
                    this.matchBetweennessMatrix[v][w] += partialDependency;
                }
                if (w != s) 
                	this.attributeBetweennessMatrix[w] += decorator.get(w).dependency;
            }
        }

		for (int i = 0; i < this.maxID; i++) {
        	this.attributeBetweennessMatrix[i] /= 2.0d;
    		for (int j = 0; j < this.maxID; j++) {
            	this.matchBetweennessMatrix[i][j] /= 2.0d;
    		}
		}
	}

	public Map<Term, Double> getAttBetweennessValuesForTerms() {
		Map<Term, Double> result = new HashMap<Term, Double>();
		
		for (Term t : this.tSetMap.keySet())
        	result.put(t,this.attributeBetweennessMatrix[this.tSetMap.get(t)]);
        	
        return result;
	}
	
}

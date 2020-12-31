package ac.technion.schemamatching.experiments.holistic.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.ensembles.Ensemble;
import ac.technion.schemamatching.ensembles.SimpleWeightedEnsemble;
import ac.technion.schemamatching.experiments.holistic.network.NetworkAggregationFunctions.NetworkAggregationType;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.testbed.ExperimentSchema;

public class SchemaNetwork {
	
	public static double EVALUATION_THRESHOLD_LOW = 0.25;
	
	public static double EVALUATION_THRESHOLD_HIGH = 0.6;
	
	/**
	 * The set of all schemas in the network
	 */
	private Set<ExperimentSchema> schemas;
	
	/**
	 * The map of all attributes / terms in the network to unique id (for value matrices)
	 */
	private Map<Term, Integer> termToIDMap;

	/**
	 * The map of all schemas to terms 
	 */
	private Map<ExperimentSchema, Set<Term>> schemaToTermMap;

	/**
	 * The map of all terms to schemas
	 */
	private Map<Term, ExperimentSchema> termToSchemaMap;
	
	/**
	 * The actual match matrix for the network
	 */
	private double[][] matchMatrix;
	
	/**
	 * Maximum paths for match matrix
	 */
	private NetworkMaximalPath<Term> maximumPath;
	
	/**
	 * Gold standard for the network
	 */
	private NetworkGoldStandardHandler goldStandardHandler;

	public SchemaNetwork(Set<ExperimentSchema> eSet) {
		this(eSet,true);
	}

	public SchemaNetwork(Set<ExperimentSchema> eSet, boolean loadGoldStandard) {

		this.schemas = eSet;
		
		this.goldStandardHandler = null;
		if (loadGoldStandard)
			this.goldStandardHandler = new NetworkGoldStandardHandler(this);
		
		this.termToIDMap = new HashMap<Term, Integer>();
		this.schemaToTermMap = new HashMap<ExperimentSchema,Set<Term>>();
		this.termToSchemaMap = new HashMap<Term, ExperimentSchema>();
		
		/*
		 * Collect collect all terms in the network
		 */
		int i = 0;
		for (ExperimentSchema s : eSet) {
			this.schemaToTermMap.put(s,new HashSet<Term>());
			for (Term t : s.getTargetOntology().getTerms(true)) {
				this.termToIDMap.put(t,i++);
				this.schemaToTermMap.get(s).add(t);
				this.termToSchemaMap.put(t, s);
			}
		}

		/*
		 * Init match matrix 
		 */
		this.matchMatrix = new double[termToIDMap.size()][termToIDMap.size()];
		for (ExperimentSchema s1 : eSet) {
			/*
			 * Probability of matches between attributes of same schema is zero
			 */
			for (Term t1 : this.schemaToTermMap.get(s1)) 
				for (Term t2 : this.schemaToTermMap.get(s1)) 
						setSymmetricMatrixEntry(this.matchMatrix, t1, t2, 0.0d);
			
			for (ExperimentSchema s2 : eSet) {
				
				if (s1.equals(s2)) continue;
				
				/*
				 * Probability of matches between attributes of different schemas is 0.5
				 */
				for (Term t1 : this.schemaToTermMap.get(s1)) 
					for (Term t2 : this.schemaToTermMap.get(s2)) 
							setSymmetricMatrixEntry(this.matchMatrix, t1, t2, 0.5d);
			}
		}
	}
	
	/**
	 * Pick a random schema from the set of schemas in the network.
	 * 
	 * @return a randomly selected schema
	 */
	public ExperimentSchema pickRandomSchema() {
		
		assert(this.schemas.size() > 1) : "Network has less than two schemas";
		
		int r = new Random().nextInt(this.schemas.size()); 
		int i = 0;
		for(ExperimentSchema s : this.schemas) {
		    if (i == r) return s;
		    i++;
		}
		return null;
	}
	
	/**
	 * Pick a random schema from the set of schemas in the network, but not 
	 * the one that is given as a parameter
	 * 
	 * @param s the selected schema must not be s
	 * @return a random schema that is not equal to s
	 */
	public ExperimentSchema pickRandomSchemaNotTheGivenOne(ExperimentSchema s) {
		
		assert(this.schemas.size() > 1) : "Network has less than 2 schemas";
		
		ExperimentSchema pick = pickRandomSchema();
		while (pick.equals(s))
			pick = pickRandomSchema();
		return pick;
	}

	/**
	 * Initialise the network using the first line matchers and weights given as parameters. Each schema 
	 * in the network is matched to a number of other schemas (not necessarily distinct). This number is 
	 * given as a density parameter.
	 * 
	 * @param flM a list of first line matchers
	 * @param matcherWeights the weights for the first line matchers
	 * @param density the number of schemas to which each schema is matched
	 */
	public void initNetwork(List<FirstLineMatcher> flM, HashMap<String,Double> matcherWeights, int density) {
		
		assert(this.schemas.size() > 1) : "Network has less than 2 schemas";
		
		/*
		 * Match schemas according to given density
		 */
		for (ExperimentSchema s1 : this.schemas) {
			int matched = 0;
			while (matched <= density) {
				ExperimentSchema s2 = pickRandomSchemaNotTheGivenOne(s1);
				
				/*
				 * Match s1 and s2
				 */
				HashMap<String,MatchInformation> flMatches = new HashMap<String,MatchInformation>(); 
				
				// List all 1LMs with over 0 weight in file
				ArrayList<FirstLineMatcher> tmp = new ArrayList<FirstLineMatcher>();
				for (FirstLineMatcher f : flM) 
					if (matcherWeights.containsKey(f.getName()) && matcherWeights.get(f.getName())>0)
						tmp.add(f);
				
				// Do the actual matching 
				for (FirstLineMatcher f : tmp)
					flMatches.put(f.getName(),f.match(s1.getTargetOntology(), s2.getTargetOntology(), false));
				
				// Create ensemble
				Ensemble e = new SimpleWeightedEnsemble();
				e.init(flMatches, matcherWeights);

				/*
				 * Record match results in match matrix
				 */
				for (Match m : e.getWeightedMatch().getCopyOfMatches()) {
					if (m.getEffectiveness() > 1.0) {
						System.err.println("First line matching returned score above 1.0 for " + m);
						setSymmetricMatrixEntry(this.matchMatrix, m.getTargetTerm(), m.getCandidateTerm(), 1.0d);
					}
					else {
						setSymmetricMatrixEntry(this.matchMatrix, m.getTargetTerm(), m.getCandidateTerm(), m.getEffectiveness());
					}
				}
				matched++;
			}
		}
	}
	
	/**
	 * Update the match network by adjusting the probability of 
	 * the match between the given terms
	 * 
	 * @param t1 a term in the network
	 * @param t2 a term in the network
	 * @param value the new probability value
	 */
	public void updateNetwork(Term t1, Term t2, double value) {
		// update the network structure
		setSymmetricMatrixEntry(this.matchMatrix, t1, t2, value);
		// make sure to invalidate maximum paths
		this.maximumPath = null;
	}
	
	
	/**
	 * Set entry in given matrix in a symmetric way
	 * 
	 * @param matrix the matrix for which the entry should be update
	 * @param t1 first term
	 * @param t2 second term
	 * @param value matrix value for the two terms
	 */
	private void setSymmetricMatrixEntry(double[][] matrix, Term t1, Term t2, double value) {
		assert(termToIDMap.containsKey(t1) && termToIDMap.containsKey(t2)) : "Terms are not known in the network";
		matrix[termToIDMap.get(t1)][termToIDMap.get(t2)] = value;
		matrix[termToIDMap.get(t2)][termToIDMap.get(t1)] = value;
	}

	/**
	 * Get the match result from the network for the given schemas.
	 * 
	 * @param s1 a schema that is part of the network
	 * @param s2 a schema that is part of the network
	 * @return a match information object comprising the match result for the schemas
	 */
	public MatchInformation getMatchResultForSchemas(ExperimentSchema s1, ExperimentSchema s2) {
		
		assert(this.schemas.contains(s1) && this.schemas.contains(s2)) : "Not all schemas are known in the network";
		
		if (s1.equals(s2)) return null;
				
		MatchInformation mi = new MatchInformation(s1.getTargetOntology(), s2.getTargetOntology());
		
		for (Term t1 : this.schemaToTermMap.get(s1)) {
			for (Term t2 : this.schemaToTermMap.get(s2)) {
				double value = this.getMaximumPath().getMaximumBetweenTerms(t1, t2);
				if ((value >= EVALUATION_THRESHOLD_HIGH) || (value <= EVALUATION_THRESHOLD_LOW))
					mi.updateMatch(t1, t2, value);
			}
		}
		return mi;
	}

	public double[][] getMatchMatrix() {
		return this.matchMatrix;
	}
	
	public Set<ExperimentSchema> getSchemas() {
		return schemas;
	}

	public Map<Term, Integer> getTermToIDMap() {
		return termToIDMap;
	}

	public Map<ExperimentSchema, Set<Term>> getSchemaToTermMap() {
		return schemaToTermMap;
	}

	public Map<Term, ExperimentSchema> getTermToSchemaMap() {
		return termToSchemaMap;
	}

	public NetworkMaximalPath<Term> getMaximumPath() {
		if (this.maximumPath == null)
			this.maximumPath = new NetworkMaximalPath<Term>(this.termToIDMap, this.matchMatrix, NetworkAggregationType.Multiplication);
		return maximumPath;
	}

	public void setMatchMatrix(double[][] matchMatrix) {
		this.matchMatrix = matchMatrix;
	}
	
	public NetworkGoldStandardHandler getGoldStandardHandler() {
		return goldStandardHandler;
	}

	@Override
	public Object clone() {
		SchemaNetwork clone = new SchemaNetwork(new HashSet<ExperimentSchema>(this.getSchemas()));
		
		double[][] cloneMatchMatrix = new double[this.getMatchMatrix().length][this.getMatchMatrix().length];
		for (int i = 0; i < cloneMatchMatrix.length; i++)
			System.arraycopy(this.getMatchMatrix()[i], 0, cloneMatchMatrix[i], 0, this.getMatchMatrix()[i].length);
		clone.setMatchMatrix(cloneMatchMatrix);
		
		return clone;
	}
	
}

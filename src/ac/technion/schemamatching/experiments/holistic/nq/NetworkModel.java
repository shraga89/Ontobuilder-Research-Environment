package ac.technion.schemamatching.experiments.holistic.nq;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.ensembles.Ensemble;
import ac.technion.schemamatching.ensembles.SimpleWeightedEnsemble;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.testbed.ExperimentSchema;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.testbed.OREDataSetEnum;

public class NetworkModel {
	
	public static double EVALUATION_THRESHOLD_LOW = 0.25;
	
	public static double EVALUATION_THRESHOLD_HIGH = 0.6;
	
	
	/*
	 * The set of all schemas in the network
	 */
	private HashSet<ExperimentSchema> schemas;
	
	/*
	 * Unified ontology of all schemas
	 */
	private Ontology unifiedOntology;
	
	/*
	 * The map of all attributes / terms in the network to unique id (for value matrices)
	 */
	private Map<Term, Integer> tIDMap;

	/*
	 * The map of all attributes / terms in the network to their original schema
	 */
	private Map<Term, ExperimentSchema> tToSchemaMap;
	
	/*
	 * The actual match matrix for the network
	 */
	private double[][] matchMatrix;
	
	/*
	 * Shortest paths for match matrix
	 */
	private MaximumPath maximumPath;
	
	/*
	 * The gold standard for the schema pairs in the network
	 */
	private MatchInformation goldStandard;
	
	public NetworkModel(HashSet<ExperimentSchema> eSet) {
		this.schemas = eSet;
		
		for (ExperimentSchema s : this.schemas) 
			System.out.println(s.getTargetOntology().getName());
		
		/*
		 * Collect all terms in the network
		 */
		this.tIDMap = new HashMap<Term, Integer>();
		this.tToSchemaMap = new HashMap<Term, ExperimentSchema>();
		int i = 0;
		for (ExperimentSchema s : this.schemas) {
			for (Term t : s.getTargetOntology().getTerms(true)) {
				this.tIDMap.put(t,i++);
				this.tToSchemaMap.put(t, s);
			}
		}
		
		/*
		 * Init match matrix 
		 */
		this.matchMatrix = new double[tIDMap.size()][tIDMap.size()];
		for (Term t1 : this.tIDMap.keySet()) 
			for (Term t2 : this.tIDMap.keySet()) 
				if (this.tToSchemaMap.get(t1).equals(this.tToSchemaMap.get(t2)))
					setSymmetricMatrixEntry(this.matchMatrix, t1, t2, 0.0d);
				else
					setSymmetricMatrixEntry(this.matchMatrix, t1, t2, 0.5d);

		/*
		 * Init unified ontology
		 */
		this.unifiedOntology = new Ontology("Network Ontology");
		for (Term t : this.tIDMap.keySet())
			this.unifiedOntology.addTerm(t);
		
		/*
		 * Init gold standard
		 */
		
		this.goldStandard = new MatchInformation(this.unifiedOntology, this.unifiedOntology);
		OREDataSetEnum dsEnum = OREDataSetEnum.getByDbid(20);
		for (ExperimentSchema s1 : this.schemas) {
			for (ExperimentSchema s2 : this.schemas) {
				if (s1.equals(s2)) continue;
				ExperimentSchemaPair pair = OBExperimentRunner.getOER().getDoc().getPair(s1,s2);
				String exactMatchPath;
				String sql = "SELECT path FROM schemapairs WHERE CandidateSchema = " + s1.getID() + " AND TargetSchema = " + s2.getID() + ";";
				ArrayList<String[]> res = OBExperimentRunner.getOER().getDB().runSelectQuery(sql, 1);
				exactMatchPath = res.get(0)[0];
				
//				System.out.println(s1.getTargetOntology().getName() + " " + s2.getTargetOntology().getName());
				
				MatchInformation exactMapping = dsEnum.getMatchImp().importMatch(new MatchInformation(this.unifiedOntology,this.unifiedOntology), new File(OBExperimentRunner.getOER().getDsurl() + exactMatchPath));
				for (Match m : exactMapping.getCopyOfMatches()) {
					this.goldStandard.updateMatch(m.getCandidateTerm(), m.getTargetTerm(), m.getEffectiveness());
					this.goldStandard.updateMatch(m.getTargetTerm(), m.getCandidateTerm(), m.getEffectiveness());
				}
			}
		}
	}
	
	/**
	 * Pick a random schema from the set of schemas in the network.
	 * 
	 * @return a randomly selected schema
	 */
	public ExperimentSchema pickRandomSchema() {
		
		assert(this.schemas.size() > 1) : "Network has less than 2 schemas";
		
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
						System.out.println("ERROR in network model MATCH: " + m);
						System.out.println("ERROR in network model MATCH: " + m.getTargetTerm().getProvenance());
						System.out.println("ERROR in network model MATCH: " + m.getCandidateTerm().getProvenance());
						setSymmetricMatrixEntry(this.matchMatrix, m.getTargetTerm(), m.getCandidateTerm(), 1.0d);
						
					}
					else {
						setSymmetricMatrixEntry(this.matchMatrix, m.getTargetTerm(), m.getCandidateTerm(), m.getEffectiveness());
					}
				}

//				this.maximumPath = new MaximumPath(this.tSetMap, this.matchMatrix);
//				for (Match m : e.getWeightedMatch().getCopyOfMatches()) {
//					if (round(m.getEffectiveness()) != round(this.maximumPath.getMaximumBetweenTerms(m.getCandidateTerm(), m.getTargetTerm()))) {
//						System.out.println(m.getCandidateTerm() + " "+ m.getTargetTerm() + " " + m.getEffectiveness() + " " + this.maximumPath.getMaximumBetweenTerms(m.getCandidateTerm(), m.getTargetTerm()));
//						System.out.println(this.maximumPath.getMaximumPath(m.getCandidateTerm(), m.getTargetTerm()));
//					}
//				}
				matched++;
			}
		}
		
		
//		for (Term t1 : this.tSetMap.keySet()) {
//			for (Term t2 : this.tSetMap.keySet()) {
//				if (this.tSetToSchemaMap.get(t1).equals(this.tSetToSchemaMap.get(t2)))
//					continue;
//				System.out.println(t1 + " "+ t2 + " " + " " + this.maximumPath.getMaximumBetweenTerms(t1,t2));
//				System.out.println(this.maximumPath.getMaximumPath(t1,t2));
//			}
//		}
	}
	
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
		assert(tIDMap.containsKey(t1) && tIDMap.containsKey(t2)) : "Terms are not known in the network";
		matrix[tIDMap.get(t1)][tIDMap.get(t2)] = value;
		matrix[tIDMap.get(t2)][tIDMap.get(t1)] = value;
	}
	
	public MatchInformation getMI() {
		
		MatchInformation mi = new MatchInformation(this.unifiedOntology, this.unifiedOntology);

		for (Term t1 : this.tIDMap.keySet()) {
			for (Term t2 : this.tIDMap.keySet()) {
				if (this.tToSchemaMap.get(t1).equals(this.tToSchemaMap.get(t2)))
					continue;
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
	
	public Map<Term, ExperimentSchema> getTermToSchemaMap() {
		return this.tToSchemaMap;
	}

	
	
	public HashSet<ExperimentSchema> getSchemas() {
		return schemas;
	}

	public void setSchemas(HashSet<ExperimentSchema> schemas) {
		this.schemas = schemas;
	}

	public Ontology getUnifiedOntology() {
		return unifiedOntology;
	}

	public void setUnifiedOntology(Ontology unifiedOntology) {
		this.unifiedOntology = unifiedOntology;
	}

	public Map<Term, Integer> gettIDMap() {
		return tIDMap;
	}

	public void settIDMap(Map<Term, Integer> tIDMap) {
		this.tIDMap = tIDMap;
	}

	public Map<Term, ExperimentSchema> gettToSchemaMap() {
		return tToSchemaMap;
	}

	public void settToSchemaMap(Map<Term, ExperimentSchema> tToSchemaMap) {
		this.tToSchemaMap = tToSchemaMap;
	}

	public MaximumPath getMaximumPath() {
		if (this.maximumPath == null)
			this.maximumPath = new MaximumPath(this.tIDMap, this.matchMatrix);
		return maximumPath;
	}

	public void setMaximumPath(MaximumPath maximumPath) {
		this.maximumPath = maximumPath;
	}

	public MatchInformation getGoldStandard() {
		return goldStandard;
	}

	public void setGoldStandard(MatchInformation goldStandard) {
		this.goldStandard = goldStandard;
	}

	public void setMatchMatrix(double[][] matchMatrix) {
		this.matchMatrix = matchMatrix;
	}

	@Override
	public Object clone() {
		NetworkModel clone = new NetworkModel(this.getSchemas());
		
		double[][] cloneMatchMatrix = new double[this.getMatchMatrix().length][this.getMatchMatrix().length];
		for (int i = 0; i < cloneMatchMatrix.length; i++)
			System.arraycopy(this.getMatchMatrix()[i], 0, cloneMatchMatrix[i], 0, this.getMatchMatrix()[i].length);
		clone.setMatchMatrix(cloneMatchMatrix);
		
		return clone;
	}
	
}

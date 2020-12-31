package ac.technion.schemamatching.experiments.holistic.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.ExperimentDocumenter;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.testbed.ExperimentSchema;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * Handles the gold standard for the schemas in a network.
 * 
 * @author matthias weidlich
 *
 */
public class NetworkGoldStandardHandler {

	private SchemaNetwork network;
	
	private Map<ExperimentSchema, Set<ExperimentSchemaPair>> pairs;
	
	public NetworkGoldStandardHandler(SchemaNetwork network) {
		this.network = network;
		this.pairs = new HashMap<ExperimentSchema, Set<ExperimentSchemaPair>>();
		
		/*
		 * Load gold standard 
		 */
		for (ExperimentSchema s1 : network.getSchemas()) {
			for (ExperimentSchema s2 : network.getSchemas()) {
				
				if (s1.equals(s2)) continue;
				
				ExperimentDocumenter ed = OBExperimentRunner.getOER().getDoc();
				int spid = ed.getPairID(s1.getID(),s2.getID(),true);
				
				if (spid == -1) continue;
				
				ExperimentSchemaPair pair = new ExperimentSchemaPair(s1, s2, true);
				
				if (!this.pairs.containsKey(s1))
					this.pairs.put(s1, new HashSet<ExperimentSchemaPair>());

				if (!this.pairs.containsKey(s2))
					this.pairs.put(s2, new HashSet<ExperimentSchemaPair>());
				
				this.pairs.get(s1).add(pair);
				this.pairs.get(s2).add(pair);
			}
		}	
	}

	public boolean schemasAreMatched(ExperimentSchema s1, ExperimentSchema s2) {
		ExperimentDocumenter ed = OBExperimentRunner.getOER().getDoc();
		int spid = ed.getPairID(s1.getID(),s2.getID(),true);
		return (spid != -1) ? true : false;
	}

	public boolean termsAreMatched(Term t1, Term t2) {
		ExperimentSchema s1 = network.getTermToSchemaMap().get(t1);
		ExperimentSchema s2 = network.getTermToSchemaMap().get(t2);
		return schemasAreMatched(s1, s2);
	}

	public boolean isCorrect(Match m) {
		
		assert(termsAreMatched(m.getCandidateTerm(), m.getTargetTerm())) : "Gold standard is not known for the respective schemas";

		ExperimentSchema s1 = network.getTermToSchemaMap().get(m.getCandidateTerm());
		ExperimentSchema s2 = network.getTermToSchemaMap().get(m.getTargetTerm());

		ExperimentSchemaPair pair = getPair(s1,s2);
		
		if (pair.getExact().getCopyOfMatches().contains(m))
			return true;

		return false;
	}
	
	private ExperimentSchemaPair getPair(ExperimentSchema s1, ExperimentSchema s2) {
		Set<ExperimentSchemaPair> pairsTmp = new HashSet<ExperimentSchemaPair>(this.pairs.get(s1));
		pairsTmp.retainAll(this.pairs.get(s2));
		return pairsTmp.iterator().next();
	}
	
	
	/**
	 * Get the gold standard for the given schemas.
	 * 
	 * @param s1 a schema that is part of the network
	 * @param s2 a schema that is part of the network
	 * @return a match information object comprising the match result for the schemas
	 */
	public MatchInformation getGoldStandardForSchemas(ExperimentSchema s1, ExperimentSchema s2) {
		
		assert(this.network.getSchemas().contains(s1) && this.network.getSchemas().contains(s2)) : "Not all schemas are known in the network";
		
		if (s1.equals(s2)) return null;
				
		return getPair(s1,s2).getExact();
	}


}

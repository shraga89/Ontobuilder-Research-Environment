

package ac.technion.schemamatching.util;

import java.util.HashMap;

import schemamatchings.meta.match.MatchedAttributePair;
import ac.technion.schemamatching.util.RandomMatchingProblemInstance;
import schemamatchings.util.SchemaTranslator;

import com.modica.ontology.Ontology;


public class RandomMatchingProblemGenerator {

	
	private SchemaTranslator exactMatching;
	private Ontology candOntology;
	private Ontology targetOntology;
	
	
	/**
	 * @param exactMatching
	 * @param candOntology
	 * @param targetOntology
	 */
	public RandomMatchingProblemGenerator(SchemaTranslator exactMatching,
			Ontology candOntology, Ontology targetOntology) {
		this.exactMatching = exactMatching;
		this.candOntology = candOntology;
		this.targetOntology = targetOntology;
	}
	
	public RandomMatchingProblemInstance generateProblem(int size){
		int numMatchings = exactMatching.getMatchedAttributesPairsCount();
		//generate exact matching
		MatchedAttributePair[] pairs = exactMatching.getMatchedPairs();
		MatchedAttributePair[] chosenPairs = new MatchedAttributePair[size];
		int index;
		HashMap chosenIndexs = new HashMap();
		for (int i=0;i<size;){
			index = (int)Math.floor(Math.random()*pairs.length);
			if (chosenIndexs.containsKey(new Integer(index))){
				continue;
			}else{
				chosenPairs[i] = new MatchedAttributePair(pairs[index].getAttribute1(),pairs[index].getAttribute2(),1.0);
				chosenIndexs.put(new Integer(index), null);
				i++;
			}
		}
		SchemaTranslator exact = new SchemaTranslator(chosenPairs);
		
		//generate ontologies
		Ontology cand = new Ontology(candOntology.getName());
		Ontology target = new Ontology(targetOntology.getName());
		MatchedAttributePair pair;
		for (int i=0;i<size;i++){
			pair = chosenPairs[i];//.substring(0,chosenPairs[i].getAttribute1().indexOf(":")+1
			cand.addTerm(candOntology.getModel().searchTerm(chosenPairs[i].getAttribute1()));
			target.addTerm(targetOntology.getModel().searchTerm(chosenPairs[i].getAttribute2()));	
		}
		
		return new RandomMatchingProblemInstance(exact, cand, target, chosenPairs);
	}
}

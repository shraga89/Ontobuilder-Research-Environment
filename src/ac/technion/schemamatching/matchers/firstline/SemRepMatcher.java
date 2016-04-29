/**
 * 
 */
package ac.technion.schemamatching.matchers.firstline;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.schemamatching.matchers.MatcherType;
import de.wdilab.hashrepository.config.Settings;
import de.wdilab.hashrepository.entities.ResultPath;
import de.wdilab.hashrepository.enums.Language;
import de.wdilab.hashrepository.enums.PathType;
import de.wdilab.hashrepository.exec.SemanticRepositoryAPI;

/**
 * SemRep First Line Matcher, see http://dbs.uni-leipzig.de/en/semrep for more details 
 * @author Rotem Kellner, Elad Sertshuk, Gal Schlesinger
 */
public class SemRepMatcher implements FirstLineMatcher {
	public String getName() {
		return "SemRep Match";
	}

	public boolean hasBinary() {
		return false;
	}

	public MatchInformation match(Ontology candidate, Ontology target, boolean binary) {
		String resourcesDir = "./lib/semRep_resources/resources";
		SemanticRepositoryAPI semApi = new SemanticRepositoryAPI(Language.ENGLISH,resourcesDir);
		//loading SemRep resources
			semApi.loadResource("Wikipedia filtered",true);
			semApi.loadResource("Wikipedia Fieldref", true);
			semApi.loadResource("Wikipedia Redirects", true);
			semApi.loadResource("ConceptNet", true);
		
		//configuring SemRep Params 
			Settings.maximumPathLength = 2;
			Settings.allPathsOption = false;
			Settings.enableNodeDegreeComparison = false;
			Settings.enablePreprocessing = false;
			
		// ArrayLists used to store Strings, will be used as input parameters to SemRep Matcher
		ArrayList<String> candidateAttributes = new ArrayList<String>();
		ArrayList<String> targetAttributes = new ArrayList	<String>();
		
		ArrayList<Term> candidateTermList =  new ArrayList<Term>();
		ArrayList<Term> targetTermList =  new ArrayList<Term>();
		
		//Iterate over both Ontologies and retrieve all of the terms 
		
		for (Term term : candidate.getTerms(true)){
			for (int i=0; i < term.getTerms().size();i++){
				candidateAttributes.add(term.getTerms().get(i).getName());
				candidateTermList.add(term.getTerms().get(i));
			}
		}
		
		for (Term term : target.getTerms(true)){
			for (int i=0; i < term.getTerms().size();i++){
				targetAttributes.add(term.getTerms().get(i).getName());
				targetTermList.add(term.getTerms().get(i));
			}
		}
		
		MatchInformation res = new MatchInformation(candidate, target);
		MatchMatrix matchMatrix = new MatchMatrix(candidateAttributes.size(),targetAttributes.size(),candidateTermList,targetTermList);
	
		/*
		 * Iterate over both Candidate and target term lists. This code compares each pair of terms.
		 * Prior to each comparison, we clean the Terms to adapt to the format of SemRep Database.
		 * Each Comparison result has two outputs, 1. Confidence 2. Path Type (6 Known Relationship types)
		 * Finally, Confidence is normalized for best results by the Relashionship type.
		 * 
		 */
		
		for (int i=0; i < candidateAttributes.size();i++){
			for (int j=0; j < targetAttributes.size();j++){
				String cand_str = cleanAttribute(candidateAttributes.get(i).toString());
				String trgt_str = cleanAttribute(targetAttributes.get(j).toString());
				ResultPath result = semApi.executeQuery(cand_str, trgt_str);
				double confidence;
				
				if (result != null){
					confidence = result.getConfidence();
					double distanceToFullConfidence = 1-confidence;
					PathType pathType = result.getType();
					switch (pathType) {
						// These three cases might be the same attribute - increase confidence
						case EQUAL:
							confidence += distanceToFullConfidence;
							break;
						case IS_A:
							confidence += distanceToFullConfidence/2;
							break;
						case INVERSE_IS_A:
							confidence += distanceToFullConfidence/2;
							break;
						// These three cases should not be the same attribute - decrease confidence
						case HAS_A:
							confidence = 0;
							break;
						case PART_OF:
							confidence = 0;
							break;
						case RELATED:
							confidence = 0;
							break;
					}
				} else {
					confidence = 0;
				}
				matchMatrix.setMatchConfidence(candidateTermList.get(i), targetTermList.get(j), confidence); 
			}
		}
		res.setMatrix(matchMatrix);
		System.out.println(res);
		return res;
	}

	private String cleanAttribute(String attribute) {
		//"Cleaning" input by the most common connecting characters which aren't required for the semRep database
		attribute = attribute.replace("_", " ");
		attribute = attribute.replace("-", " ");
		attribute = attribute.replace(".", " ");
		return attribute;
	}

	public String getConfig() { 
		String config = "default";
		return config;
	}

	public MatcherType getType() {
		return MatcherType.SEMANTIC;
	}

	public int getDBid() {
		return 30;
	}
}

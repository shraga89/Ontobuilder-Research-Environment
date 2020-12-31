package ac.technion.schemamatching.matchers.firstline;

import java.util.Set;

import matching.searchers.predict.OptSEDOverAttributeLabels;
import matching.searchers.predict.PredictionSearcher;
import matching.searchers.predict.SemanticLinPredictionSearcher;
import matching.searchers.predict.SemanticTaggingSimilarityPredictionSearcher;
import matching.searchers.predict.StandardVirtualDocForComponentPredictionSearcher;
import matching.searchers.predict.StandardVirtualDocForDistanceSetPredictionSearcher;
import nl.tue.tm.is.graph.Graph;
import nl.tue.tm.is.graph.TwoValuedVertices;
import nl.tue.tm.is.labelAnalyzer.interfaces.SemanticLanguage;
import nl.tue.tm.is.labelAnalyzer.interfaces.SemanticLanguage.LanguageCode;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;
import ac.technion.schemamatching.util.ProcessModelUtils;

public class ProcessModelFLM implements FirstLineMatcher {

	public enum ProcessModelMatchStrategy {
		// textual similarities
		OptSEDOverActivityLabels,
//		OptSEDOverRoles,
//		OptSEDOverData,
		LinOverActivities,
		LinOverActivitiesWithObjects,
		LinOverActionsOfActivities,
		LinOverObjectsOfActivities,
		ActivitiesHaveEqualNumberOfSemanticComponents,
		ActivitiesHaveCommonSemanticComponents,
		VirtualDocs,
		VirtualDocsDistanceSet,
		VirtualDocsTreeSet,
		
		// textual syntax/organisational/entity-based (SED over roles)
		// textual semantic/functional/partial model (Lin or similar over activities)
		// structural/control flow/full model (GED over graphs)
		// behavioural/control flow/partial model (BP over sub-graph)
		// textual semantic/data/entity-based (some wordnet relation for object names)
	}
	
	private ProcessModelMatchStrategy currentStrategy = ProcessModelMatchStrategy.OptSEDOverActivityLabels;
	
	private LanguageCode languageCode = LanguageCode.EN;
	
	private String languageString = "english";
	
	
	@Override
	public String getName() {
		return this.currentStrategy.toString();
	}

	@Override
	public boolean hasBinary() {
		return false;
	}
	
	public void setMatchingStrategy(ProcessModelMatchStrategy strategy) {
		this.currentStrategy = strategy;
	}
	
	public void setMatchingStrategy(ProcessModelMatchStrategy strategy, LanguageCode languageCode) {
		this.currentStrategy = strategy;
		this.languageCode = languageCode;
		
		switch (this.languageCode){
		case NL:
			this.languageString = "dutch";
			break;
		default:
			this.languageString = "english";
		}
	}

	@Override
	public MatchInformation match(Ontology candidate, Ontology target,
			boolean binary) {
		
		PredictionSearcher searcher = null;
		SemanticLanguage.setLanguage(this.languageCode);
		
		switch (this.currentStrategy) {
		case OptSEDOverActivityLabels:
			searcher = new OptSEDOverAttributeLabels(Graph.ATTRIBUTE_LABEL, this.languageString);
			break;
//		case OptSEDOverRoles:
//			attributes.add(Graph.ATTRIBUTE_UITVOERENDE);
//			attributes.add(Graph.ATTRIBUTE_RESPONSIBLE);
//			searcher = new OptSEDOverAttributeLabels(attributes, this.languageString);
//			break;
//
//		case OptSEDOverData:
//			attributes.add(Graph.ATTRIBUTE_DATA);
//			attributes.add(Graph.ATTRIBUTE_DATA_OBJECTEN);
//			attributes.add(Graph.ATTRIBUTE_INPUTS);
//			attributes.add(Graph.ATTRIBUTE_OUTPUTS);
//			searcher = new OptSEDOverAttributeLabels(attributes, this.languageString);
//			break;
			
		case VirtualDocs:
			searcher = new StandardVirtualDocForDistanceSetPredictionSearcher(0);
			break;
		case VirtualDocsDistanceSet:
			searcher = new StandardVirtualDocForDistanceSetPredictionSearcher(1);
			break;
		case VirtualDocsTreeSet:
			searcher = new StandardVirtualDocForComponentPredictionSearcher(1);
			break;

		case LinOverActivities:
			searcher = new SemanticLinPredictionSearcher(this.languageCode, false, false, false);
			break;

		case LinOverActivitiesWithObjects:
			searcher = new SemanticLinPredictionSearcher(this.languageCode, true, false, false);
			break;
		
		case LinOverActionsOfActivities:
			searcher = new SemanticLinPredictionSearcher(this.languageCode, false, true, false);
			break;

		case LinOverObjectsOfActivities:
			searcher = new SemanticLinPredictionSearcher(this.languageCode, false, false, true);
			break;
			
		case ActivitiesHaveEqualNumberOfSemanticComponents:
			searcher = new SemanticTaggingSimilarityPredictionSearcher(this.languageCode, false);
			break;
			
		case ActivitiesHaveCommonSemanticComponents:
			searcher = new SemanticTaggingSimilarityPredictionSearcher(this.languageCode, true);
			break;

		default:
			System.err.println("No searcher selected");
			break;
		}
		
		Graph sg1 = ProcessModelUtils.loadGraphFromPNML(candidate.getFile().getPath());
		Graph sg2 = ProcessModelUtils.loadGraphFromPNML(target.getFile().getPath());

		MatchInformation result = new MatchInformation(candidate, target);
		
		//Initialize solution mappings
		Set<TwoValuedVertices> solutions = searcher.search(sg1, sg2); 
		for (TwoValuedVertices tvs: solutions){
			if (sg1.getLabel(tvs.v1).isEmpty() || sg2.getLabel(tvs.v2).isEmpty())
				continue;
			Term candidateTerm = candidate.getTermByProvenance(sg1.getLabel(tvs.v1));
			Term targetTerm = target.getTermByProvenance(sg2.getLabel(tvs.v2));
			assert(candidateTerm != null) : "Term not found " + sg1.getLabel(tvs.v1).toString();
			assert(targetTerm != null) : "Term not found " + sg2.getLabel(tvs.v2).toString();
			assert(tvs.v <= 1.0) : "Confidence value above one " + tvs.v;
			result.updateMatch(targetTerm, candidateTerm, tvs.v);
		}

		return result;
	}

	@Override
	public String getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MatcherType getType() {
		return null;
	}

	@Override
	public int getDBid() {
		return 18;
	}
	
}

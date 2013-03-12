package ac.technion.schemamatching.matchers.firstline;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import matching.matchers.EDOCMatcher;
import matching.matchers.Matcher;
import nl.tue.tm.is.graph.Graph;
import nl.tue.tm.is.graph.TwoVertexSets;
import nl.tue.tm.is.ptnet.PTNet;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;

public class ProcessModelMatcher2 implements FirstLineMatcher {

	public enum ProcessModelMatchStrategy {
		// 1. Sim over properties
		// 1.1 textual properties
		OptSEDOverActivities,
		OptSEDOverRoles,
		OptSEDOverData,
		LinOverActivities,
		LinOverActivitiesWithObjects,
		LinOverActionsOfActivities,
		LinOverObjectsOfActivities,
		VirtualDocs,
		VirtualDocsWithContext,
		// 1.2 structural properties
		Size,
		RPSTDepth,
		NodeDegree,
		// 1.3 behavioural properties
		AmountOfExclusiveness,
		AmountOfConcurrency,
		
		// textual syntax/organisational/entity-based (SED over roles)
//		OptSEDOverRoles, 
		// textual semantic/functional/partial model (Lin or similar over activities)
		// structural/control flow/full model (GED over graphs)
		GEDSim,
		// behavioural/control flow/partial model (BP over sub-graph)
		// textual semantic/data/entity-based (some wordnet relation for object names)
	}
	
	private ProcessModelMatchStrategy currentStrategy;
	
	@Override
	public String getName() {
		return "Process Model Matcher";
	}

	@Override
	public boolean hasBinary() {
		return false;
	}
	
	public void setMatchingStrategy(ProcessModelMatchStrategy strategy) {
		this.currentStrategy = strategy;
	}

	@Override
	public MatchInformation match(Ontology candidate, Ontology target,
			boolean binary) {
		
		Matcher matcher = null;
		
		switch (this.currentStrategy) {
		case GEDSim:
			matcher = new EDOCMatcher();
			break;
			
		default:
			break;
		}
		
		Method m;
		try {
			m = matcher.getClass().getMethod("setupMatcher");
			m.invoke(matcher, (Object[]) null);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		Graph sg1 = loadGraphFromPNML(candidate.getFile().getPath());
		Graph sg2 = loadGraphFromPNML(target.getFile().getPath());

		//Initialize solution mappings
		Set<TwoVertexSets> solutionMappingSets = Matcher.match(sg1, sg2); 
		for (TwoVertexSets tvs: solutionMappingSets){
			
		}

		return null;
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
		return 17;
	}
	
	public Graph loadGraphFromPNML(String filename){
		PTNet ptnet = PTNet.loadPNML(filename);
		Graph sg = new Graph(ptnet);
		return sg;
	}

}

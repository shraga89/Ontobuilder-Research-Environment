package ac.technion.schemamatching.matchers.firstline;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import matching.matchers.Matcher;
import matching.matchers.predict.BPMatcher;
import matching.matchers.predict.GEDMatcherWithOptSED;
import matching.matchers.predict.TextMatcherVirtualDoc;
import nl.tue.tm.is.graph.Graph;
import nl.tue.tm.is.graph.TwoVertexSets;
import nl.tue.tm.is.ptnet.PTNet;
import nl.tue.tm.is.ptnet.Transition;

import org.jbpt.bp.BehaviouralProfile;
import org.jbpt.bp.construct.BPCreatorUnfolding;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.io.PNMLSerializer;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;

public class ProcessModelCompleteMatchers implements FirstLineMatcher {
	
	public enum ProcessModelMatcher {
		PureTextMatcher,
		GEDMatcher,
//		BPMatcher
	}
	
	private ProcessModelMatcher currentMatcher = ProcessModelMatcher.PureTextMatcher;

	private PNMLSerializer serializer = new PNMLSerializer();
	
	@Override
	public String getName() {
		return currentMatcher.toString();
	}

	@Override
	public boolean hasBinary() {
		return true;
	}
	
	public void setMatchingStrategy(ProcessModelMatcher matcher) {
		this.currentMatcher = matcher;
	}

	@Override
	public MatchInformation match(Ontology candidate, Ontology target,
			boolean binary) {
		
		Matcher matcher = null;
		
		switch (this.currentMatcher) {
		case GEDMatcher:
			matcher = new GEDMatcherWithOptSED();
			break;
			
		case PureTextMatcher:
			matcher = new TextMatcherVirtualDoc();
			break;
			
//		case BPMatcher:
//			matcher = new BPMatcher();
//			break;
			
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

		MatchInformation result = new MatchInformation(candidate, target);

		//Initialize solution mappings
		Set<TwoVertexSets> solutionMappingSets = Matcher.match(sg1, sg2); 
		for (TwoVertexSets tvs: solutionMappingSets){
			for (Integer v1 : tvs.s1) {
				for (Integer v2 : tvs.s2) {
					Term candidateTerm = candidate.getTermByProvenance(sg1.getLabel(v1));
					Term targetTerm = target.getTermByProvenance(sg2.getLabel(v2));
					assert(candidateTerm != null): "Term not found " + sg1.getLabel(v1);
					assert(targetTerm != null): "Term not found " + sg2.getLabel(v2);;
					result.updateMatch(targetTerm, candidateTerm, 1.0);
				}
			}
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
	
	public Graph loadGraphFromPNML(String filename){
		PTNet ptnet = PTNet.loadPNML(filename);
		for (Transition t : ptnet.transitions())
			t.setName(t.getName().replace('.', ' ').trim());
		
		NetSystem system = serializer.parse(filename);
		for (org.jbpt.petri.Transition t : system.getTransitions())
			t.setName(t.getName().replace('.', ' ').trim());
		
		if (system.getMarkedPlaces().isEmpty())
			for (Place p : system.getSourcePlaces())
				system.getMarking().put(p, 1);

		
		Graph sg = new Graph(ptnet);
		sg.setOriginalNetSystem(system);
		return sg;
	}

}

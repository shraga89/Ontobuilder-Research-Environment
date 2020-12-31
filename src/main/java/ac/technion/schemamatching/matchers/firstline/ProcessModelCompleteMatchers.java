package ac.technion.schemamatching.matchers.firstline;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import matching.matchers.Matcher;
import matching.matchers.predict.BPMatcher;
import matching.matchers.predict.GEDMatcher;
import matching.matchers.predict.TextMatcherVirtualDoc;
import nl.tue.tm.is.graph.Graph;
import nl.tue.tm.is.graph.TwoVertexSets;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;
import ac.technion.schemamatching.util.ProcessModelUtils;

public class ProcessModelCompleteMatchers implements FirstLineMatcher {
	
	public enum ProcessModelMatcher {
		PureTextMatcher,
		GEDMatcher,
		BPMatcher
	}
	
	private ProcessModelMatcher currentMatcher = ProcessModelMatcher.GEDMatcher;

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
			matcher = new GEDMatcher();
			break;
			
		case PureTextMatcher:
			matcher = new TextMatcherVirtualDoc();
			break;
			
		case BPMatcher:
			matcher = new BPMatcher();
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
		
		Graph sg1 = ProcessModelUtils.loadGraphFromPNMLIncludingjBPTNetSystem(candidate.getFile().getPath());
		Graph sg2 = ProcessModelUtils.loadGraphFromPNMLIncludingjBPTNetSystem(target.getFile().getPath());

		MatchInformation result = new MatchInformation(candidate, target);

		//Initialize solution mappings
		Set<TwoVertexSets> solutionMappingSets = Matcher.match(sg1, sg2); 
		for (TwoVertexSets tvs: solutionMappingSets){
			for (Integer v1 : tvs.s1) {
				for (Integer v2 : tvs.s2) {
					if (sg1.getLabel(v1).isEmpty() || sg2.getLabel(v2).isEmpty())
						continue;
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
		return 19;
	}
	
}

package ac.technion.schemamatching.matchers.secondline;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import matching.matchers.Matcher;
import matching.matchers.predict.BPConfigMatcher;
import matching.matchers.predict.GEDConfigMatcher;
import matching.searchers.Searcher;
import nl.tue.tm.is.graph.Graph;
import nl.tue.tm.is.graph.TwoValuedVertexSets;
import nl.tue.tm.is.graph.TwoVertexSets;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.util.ProcessModelUtils;

public class ProcessModelConfigMatchers implements SecondLineMatcher {
	
	public enum ProcessModelConfigMatcher {
		GEDConfigMatcher,
		BPConfigMatcher
	}
	
	private ProcessModelConfigMatcher currentMatcher = ProcessModelConfigMatcher.GEDConfigMatcher;

	@Override
	public String getName() {
		return currentMatcher.toString();
	}

	public void setMatchingStrategy(ProcessModelConfigMatcher matcher) {
		this.currentMatcher = matcher;
	}

	@Override
	public MatchInformation match(MatchInformation mi) {
		
		Matcher matcher = null;
		
		switch (this.currentMatcher) {
		case GEDConfigMatcher:
			matcher = new GEDConfigMatcher();
			break;
			
		case BPConfigMatcher:
			matcher = new BPConfigMatcher();
			break;
			
		default:
			break;
		}
		
		Graph sg1 = ProcessModelUtils.loadGraphFromPNMLIncludingjBPTNetSystem(mi.getCandidateOntology().getFile().getPath());
		Graph sg2 = ProcessModelUtils.loadGraphFromPNMLIncludingjBPTNetSystem(mi.getTargetOntology().getFile().getPath());

		Set<TwoValuedVertexSets>  storedSims = new HashSet<TwoValuedVertexSets>();
		
		for (Match m : mi.getCopyOfMatches()) {
			if (m.getEffectiveness() < 0.2)
				continue;
			Term can = m.getCandidateTerm();
			Term tar = m.getTargetTerm();
			Integer canVertex = sg1.getVertex(can.getName());
			Integer tarVertex = sg2.getVertex(tar.getName());
			assert(canVertex != null);
			assert(tarVertex != null);
			Set<Integer> set1 = new HashSet<>();
			set1.add(canVertex);
			Set<Integer> set2 = new HashSet<>();
			set2.add(tarVertex);
			TwoValuedVertexSets tvs = new TwoValuedVertexSets(set1, set2, m.getEffectiveness());
			storedSims.add(tvs);
		}
		
		System.out.println(storedSims.size());
		
		Method m;
		try {
			m = matcher.getClass().getMethod("setupMatcher");
			m.invoke(matcher, (Object[]) null);

			m = matcher.getClass().getMethod("getMatcher");
			Object matcherRef = m.invoke(matcher, (Object[]) null);

			m = matcher.getClass().getMethod("getSearchers");
			Object invokeResult = m.invoke(matcherRef, (Object[]) null);
			Object searcher = ((List<Searcher>)invokeResult).get(0);
			m = searcher.getClass().getMethod("setSims", storedSims.getClass().getInterfaces()[0]);
			m.invoke(searcher, storedSims);

		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		MatchInformation result = new MatchInformation(mi.getCandidateOntology(), mi.getTargetOntology());

		//Initialize solution mappings
		Set<TwoVertexSets> solutionMappingSets = Matcher.match(sg1, sg2); 
		for (TwoVertexSets tvs: solutionMappingSets){
			for (Integer v1 : tvs.s1) {
				for (Integer v2 : tvs.s2) {
					if (sg1.getLabel(v1).isEmpty() || sg2.getLabel(v2).isEmpty())
						continue;
					Term candidateTerm = mi.getCandidateOntology().getTermByProvenance(sg1.getLabel(v1));
					Term targetTerm = mi.getTargetOntology().getTermByProvenance(sg2.getLabel(v2));
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
	public int getDBid() {
		return 12;
	}

	@Override
	public boolean init(Properties properties) {
		return true;
	}
	
}

/**
 * 
 */
package ac.technion.schemamatching.experiments.holistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.OBTermMatch;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.DummyStatistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchema;

/**
 * Match WS repo against query string
 * 
 * @author Matthias Weidlich
 */
public class WSRetrieval implements HolisticExperiment{

	
	private List<Map<String, String>> queries;
	
	private List<FirstLineMatcher> flM;
	
	public List<Statistic> runExperiment(Set<ExperimentSchema> eSet) {

		List<Statistic> res = new ArrayList<Statistic>();
		DummyStatistic stat = new DummyStatistic();
		stat.setName("WSRetrieval");
		stat.setHeader(new String[]{"Query","Exp","Service","Matcher","Sim"});
		
		List<String[]> statData= new ArrayList<>();
		
		/*
		 * For each query
		 */
		for (Map<String, String> q : this.queries) {
			Ontology qOnto = new Ontology(q.get("title"));
			qOnto.setLight(true);
			Term qTitleTerm = new Term(q.get("title"));
			qOnto.addTerm(qTitleTerm);

			Set<Term> candidateTerms = new HashSet<>();
			candidateTerms.add(qTitleTerm);

			/*
			 * For each schema, using only the title
			 */
			for (ExperimentSchema e  : eSet) {
				/*
				 * For each selected first line matcher
				 */
				for (FirstLineMatcher matcher : this.flM) {
					MatchInformation mi = matcher.match(qOnto, e.getTargetOntology(), false);
					
					/*
					 * Get Match for title
					 */
					double avg = getAvgOfMaxMatchValueForTerms(candidateTerms, mi);
					statData.add(new String[]{q.get("title"),"Title only",e.getTargetOntology().getName(),matcher.getName(),String.valueOf(avg)});
				}
			}		
			
			/*
			 * For each schema, using also the object names title
			 */
			Term qInputTerm = new Term(q.get("input"));
			qOnto.addTerm(qInputTerm);
			Term qOutputTerm = new Term(q.get("output"));
			qOnto.addTerm(qOutputTerm);
			
			candidateTerms.add(qInputTerm);
			candidateTerms.add(qOutputTerm);
			
			for (ExperimentSchema e  : eSet) {
				/*
				 * For each selected first line matcher
				 */
				for (FirstLineMatcher matcher : this.flM) {
					MatchInformation mi = matcher.match(qOnto, e.getTargetOntology(), false);
				
					double avg = getAvgOfMaxMatchValueForTerms(candidateTerms, mi);
					statData.add(new String[]{q.get("title"),"Title and Objects",e.getTargetOntology().getName(),matcher.getName(),String.valueOf(avg)});
				}
			}				
		}
		
		stat.setData(statData);
		res.add(stat);
		
		return res;
	}
	
	private double getAvgOfMaxMatchValueForTerms(Set<Term> candidateTerms, MatchInformation mi) {
		double sum = 0;
		for (Term t : candidateTerms)
			sum += getMaxMatchValueForTerm(t,mi);
		
		return (sum / ((double)candidateTerms.size()));
	}
	
	private double getMaxMatchValueForTerm(Term candidateTerm, MatchInformation mi) {
		double max = 0;
		for (Match match : mi.getMatchesForTerm(candidateTerm, true)) {
			max = Math.max(max, match.getEffectiveness());
		}
		return max;
	}
	
	
	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		
		this.flM = (List<FirstLineMatcher>)flM;
		
		this.queries = new ArrayList<>();
		
		/*
		 * First query
		 */
		Map<String, String> query = new HashMap<>();
		query.put("title","Check Order");
		query.put("input","Order");
		query.put("output","Order");

		query.put("input_states","");
		query.put("output_states","");

		this.queries.add(query);

		/*
		 * Second query
		 */
		query = new HashMap<>();
		query.put("title","Create Quote");
		query.put("input","Request for Quote (RFQ)");
		query.put("output","Quote");

		query.put("input_states","");
		query.put("output_states","");

		this.queries.add(query);

		return true;
	}

	public String getDescription() {
		String desc = "Match queries strings against WS descriptions";
		return desc;
	}
	
}

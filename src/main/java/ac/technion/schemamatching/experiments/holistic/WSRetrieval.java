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
					MatchInformation mi = null;
					try {
						mi = matcher.match(qOnto, e.getTargetOntology(), false);
					} catch (Exception e2) {
						System.err.println("Failed at " + e.getID() + ": " + e.getTargetOntology().getName());
					}
					
					/*
					 * Get Match for title
					 */
					if (mi != null) {
						double avg = getAvgOfMaxMatchValueForTerms(candidateTerms, mi);
						statData.add(new String[]{q.get("title"),"Title only",e.getTargetOntology().getName(),matcher.getName(),String.valueOf(avg)});
					}
					else
						statData.add(new String[]{q.get("title"),"Title only",e.getTargetOntology().getName(),matcher.getName(),"-1"});
				}
			}		
			
			/*
			 * For each schema, using also the object names title
			 */
			String[] objects = q.get("input").split(",");
			for (int i = 0; i < objects.length; i++) {
				Term qTerm = new Term(objects[i]);
				qOnto.addTerm(qTerm);
				candidateTerms.add(qTerm);
			}
			objects = q.get("output").split(",");
			for (int i = 0; i < objects.length; i++) {
				Term qTerm = new Term(objects[i]);
				qOnto.addTerm(qTerm);
				candidateTerms.add(qTerm);
			}
			
			for (ExperimentSchema e  : eSet) {
				/*
				 * For each selected first line matcher
				 */
				for (FirstLineMatcher matcher : this.flM) {
					MatchInformation mi = null;
					try {
						mi = matcher.match(qOnto, e.getTargetOntology(), false);
					} catch (Exception e2) {
						System.err.println("Failed at " + e.getID() + ": " + e.getTargetOntology().getName());
					}
				
					if (mi != null) {
						double avg = getAvgOfMaxMatchValueForTerms(candidateTerms, mi);
						statData.add(new String[]{q.get("title"),"Title and Objects",e.getTargetOntology().getName(),matcher.getName(),String.valueOf(avg)});
					}
					else
						statData.add(new String[]{q.get("title"),"Title and Objects",e.getTargetOntology().getName(),matcher.getName(),"-1"});
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
		 * First query: 1Ve_4qka
		 */
		Map<String, String> query = new HashMap<>();
		query.put("title","Credit Debit Memo Request Processing");
		query.put("input","Complaint,Goods,Debit Reason,Document");
		query.put("output","Credit Debit Memo Request,Credit Debit Memo");
		this.queries.add(query);

		/*
		 * Second query: 1An_kynn
		 */
		query = new HashMap<>();
		query.put("title","Measure Processing");
		query.put("input","Internal order");
		query.put("output","Order,inv. profile,Investment profile");
		this.queries.add(query);

		/*
		 * Third query: 1Ex_dwdy
		 */
		query = new HashMap<>();
		query.put("title","Budget Execution");
		query.put("input","Budget release,budget values");
		query.put("output","Invoice,expenditure budget");
		this.queries.add(query);

		/*
		 * Fourth query: 1Pe_lu4d
		 */
		query = new HashMap<>();
		query.put("title","Processing Offer of Work Contract");
		query.put("input","applicant,offer of work contract");
		query.put("output","offer of work contract");
		this.queries.add(query);

		/*
		 * Fifth query: 1Qu_bxuo
		 */
		query = new HashMap<>();
		query.put("title","Maintenance Planning");
		query.put("input","Maintenance plan");
		query.put("output","Dates due package,Maintenance package,Service order,Maintenance order,Maintenance call, Maintenance plan calls,Inspection lot");
		this.queries.add(query);

		return true;
	}

	public String getDescription() {
		String desc = "Match queries strings against WS descriptions";
		return desc;
	}
	
}

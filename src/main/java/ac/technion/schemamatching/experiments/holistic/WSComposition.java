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
import ac.technion.schemamatching.matchers.secondline.OBDominants;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.DummyStatistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchema;

/**
 * Match input/outpus of ws 
 * 
 * @author Matthias Weidlich
 */
public class WSComposition implements HolisticExperiment{

	
	private Map<Map<String, String>,Map<String, String>> pairs;
	
	private Map<String, String> fixedPairs = new HashMap<>();
	
	private ExperimentSchema getBestForQuery(Set<ExperimentSchema> eSet, Map<String, String> q, boolean outNeeded, ExperimentSchema ignore) {
		
		if (this.fixedPairs.containsKey(q.get("title"))) {
			for (ExperimentSchema e  : eSet) {
				if (this.fixedPairs.get(q.get("title")).equals(e.getTargetOntology().toString()))
					return e;
			}
		}
		
		Ontology qOnto = new Ontology(q.get("title"));
		qOnto.setLight(true);
		Term qTitleTerm = new Term(q.get("title"));
		qOnto.addTerm(qTitleTerm);

		Set<Term> candidateTerms = new HashSet<>();
		candidateTerms.add(qTitleTerm);
		
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
		
		ExperimentSchema best = null;
		double bestValue = 0;
		for (ExperimentSchema e  : eSet) {
			if (ignore != null)
				if (e.equals(ignore))
					continue;
			
			try {
				Term root = getTermInDepth(e.getTargetOntology().getTerms(false).get(0),2);
				if (root.getTerms().isEmpty())
					continue;
				if (outNeeded && root.getTerms().size() < 2)
					continue;
				
				OBTermMatch matcher = new OBTermMatch();
				MatchInformation mi = null;
				try {
					mi = matcher.match(qOnto, e.getTargetOntology(), false);
				} catch (Exception e2) {
					System.err.println("Failed at " + e.getID() + ": " + e.getTargetOntology().getName());
				}
				
				if (mi != null) {
					double avg = getAvgOfMaxMatchValueForTerms(candidateTerms, mi);
					if (avg > bestValue) {
						bestValue = avg;
						best = e;
					}
				}
				
			} catch (Exception e2) {
				System.err.println("Failed for " + e.getTargetOntology().toString());
			}
			
		}		
		return best;
	}
	
	private Term getTermInDepth(Term t, int i) {
		if (i == 0)
			return t;
		
		return getTermInDepth(t.getTerms().get(0),i-1);
	}
	
	public List<Statistic> runExperiment(Set<ExperimentSchema> eSet) {

		List<Statistic> res = new ArrayList<Statistic>();
		DummyStatistic stat = new DummyStatistic();
		stat.setName("WSComposition");
		stat.setHeader(new String[]{"Query 1","Query 2","Service 1","Service 2","Numer matches",
				"Numer terms in Q1","Numer terms in Q2"});
		
		List<String[]> statData= new ArrayList<>();
		
		/*
		 * For each pair
		 */
		for (Map<String, String> q1 : this.pairs.keySet()) {
			Map<String, String> q2 = this.pairs.get(q1);
			
			ExperimentSchema e1 = getBestForQuery(eSet,q1,true,null);
			ExperimentSchema e2 = getBestForQuery(eSet,q2,false,e1);

			Term e1Out = getTermInDepth(e1.getTargetOntology().getTerms(false).get(0),2).getTerms().get(1);
			Term e2In = getTermInDepth(e2.getTargetOntology().getTerms(false).get(0),2).getTerms().get(0);
			
			Ontology e1OutOnto = new Ontology(e1Out.getName());
			e1OutOnto.setLight(true);
			e1OutOnto.addTerm(e1Out);

			Ontology e2InOnto = new Ontology(e2In.getName());
			e2InOnto.setLight(true);
			e2InOnto.addTerm(e2In);

			OBTermMatch matcher = new OBTermMatch();
			MatchInformation mi1 = null;
			MatchInformation mi2 = null;
			try {
				mi1 = matcher.match(e1OutOnto, e2InOnto, false);
				if (mi1 != null) {
					OBDominants secondMatcher = new OBDominants();
					mi2 = secondMatcher.match(mi1);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			if (mi2 != null) {
				statData.add(new String[]{q1.get("title"),q2.get("title"),
						e1.getTargetOntology().toString(),
						e2.getTargetOntology().toString(),
						String.valueOf(mi2.getCopyOfMatches().size()),
						String.valueOf(mi2.getOriginalCandidateTerms().size()),
						String.valueOf(mi2.getOriginalTargetTerms().size())
				});
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
		
		this.pairs = new HashMap<>();
		
		/*
		 * First query: 1Ve_4qka
		 */
		Map<String, String> query1 = new HashMap<>();
		Map<String, String> query2 = new HashMap<>();

		/*
		 * Second query: 1An_kynn
		 */
		query1 = new HashMap<>();
		query1.put("title","Measure Processing");
		query1.put("input","Internal order");
		query1.put("output","Order,inv. profile,Investment profile");
		query2 = new HashMap<>();
		query2.put("title","Measure planning");
		query2.put("input","order");
		query2.put("output","measure,settlement");
		this.pairs.put(query1,query2);

		/*
		 * Third query: 1Ex_dwdy
		 */
		query1 = new HashMap<>();
		query1.put("title","Budget Execution");
		query1.put("input","Budget release,budget values");
		query1.put("output","Invoice,expenditure budget");
		query2 = new HashMap<>();
		query2.put("title","Fiscal Year Change Operations Funds Management");
		query2.put("input","fiscal year change operations,invoice,expenditure,budget");
		query2.put("output","residual budget,evaluations,balances");
		this.pairs.put(query1,query2);

		/*
		 * Fourth query: 1Pe_lu4d
		 */
		query1 = new HashMap<>();
		query1.put("title","Processing Offer of Work Contract");
		query1.put("input","applicant,offer of work contract");
		query1.put("output","offer of work contract");
		query2 = new HashMap<>();
		query2.put("title","Preparation for Hiring");
		query2.put("input","offer of work contract");
		query2.put("output","applicant");
		this.pairs.put(query1,query2);

		/*
		 * Fifth query: 1Qu_bxuo
		 */
		query1 = new HashMap<>();
		query1.put("title","Maintenance Planning");
		query1.put("input","Maintenance plan");
		query1.put("output","Dates due package,Maintenance package,Service order,Maintenance order,Maintenance call, Maintenance plan calls,Inspection lot");
		query2 = new HashMap<>();
		query2.put("title","Service Order");
		query2.put("input","service order,maintenance item,maintenance task list");
		query2.put("output","order,service order,inspection lot,maintenance plan call");
		this.pairs.put(query1,query2);
		
		this.fixedPairs.put("Measure Processing", "{http://sap.com/xi/IS-PS-CA/Global2}CitizenServiceArrangementERPCreateRequestConfirmation_In");
		this.fixedPairs.put("Measure planning", "{http://sap.com/xi/IS-PS-CA/Global2}CitizenServiceProductERPCreateRequestConfirmation_In");

		this.fixedPairs.put("Budget Execution", "{http://sap.com/xi/IS-PS-CA/Global2}CitizenServiceProductERPCreateRequestConfirmation_In");
		this.fixedPairs.put("Fiscal Year Change Operations Funds Management", "{http://sap.com/xi/BARISK}CVM_CreditPortfolioModelOut");

		this.fixedPairs.put("Processing Offer of Work Contract", "{http://sap.com/xi/IS-PS-CA/Global2}CitizenServiceArrangementERPCreateRequestConfirmation_In");
		this.fixedPairs.put("Preparation for Hiring", "{http://sap.com/xi/IS-PS-CA/Global2}CitizenServiceArrangementERPCalculateQueryResponse_In");

		this.fixedPairs.put("Maintenance Planning", "{http://sap.com/xi/APPL/Global2}MaintenancePlanERPByIDQueryResponse_In");
		this.fixedPairs.put("Service Order", "{http://sap.com/xi/APPL/Global2}MaintenancePlanERPCreateRequestConfirmation_In");

		return true;
	}

	public String getDescription() {
		String desc = "Match input and outputs of WS descriptions";
		return desc;
	}
	
}

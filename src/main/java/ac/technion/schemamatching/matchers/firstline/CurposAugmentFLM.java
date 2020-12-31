package ac.technion.schemamatching.matchers.firstline;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.StringUtilities;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.curpos.CorpusDataManager;
import ac.technion.schemamatching.curpos.CurposTerm;
import ac.technion.schemamatching.curpos.CurposTermSimilarityMeausre;
import ac.technion.schemamatching.curpos.MatchesCurpos;
import ac.technion.schemamatching.curpos.MatchesCurpos.EntryMaptFitnessComparator;
import ac.technion.schemamatching.matchers.MatcherType;
import ac.technion.schemamatching.testbed.OREDataSetEnum;

public class CurposAugmentFLM implements FirstLineMatcher {

	public CurposAugmentFLM(){
		dsType = OREDataSetEnum.getByDbid(1);
		threshold = 0.7;
		termsPullPerTerm = 5;
	}
	
	public CurposAugmentFLM(OREDataSetEnum dsType, double threshold, int termsPullPerTerm){
		this.dsType = dsType;
		this.threshold = threshold;
		this.termsPullPerTerm =termsPullPerTerm;
	}
	
	private OREDataSetEnum dsType;
	private double threshold;
	private int termsPullPerTerm;
	// At some later point this should hold a smarter similarity measure, maybe even a leaner
	private CurposTermSimilarityMeausre mainSimilarityMeasure = new NGramCurposTermNameSimilarity();
	// same as the other one but meant to use for the language model, 
	// thought maybe we would use a test is there a match in corpus which the other wont have
	private LmMetaCurposTermSimilarity mainLmSimilarityMeasure = new LmMetaCurposTermSimilarity();
	
	@Override
	public String getName() {
		return "Curpos Augment";
	}

	@Override
	public boolean hasBinary() {
		return false;
	}

	@Override
	public MatchInformation match(Ontology candidate, Ontology target,
			boolean binary) {
		
		MatchesCurpos curpos = CorpusDataManager.LoadMatchesCurpos(dsType);
		if (curpos == null) return null;
		
		MatchInformation res = new MatchInformation(candidate,target);
		Vector<Term> candList = candidate.getTerms(true);
		Vector<Term> targList = target.getTerms(true);
		
		Hashtable<Term,Hashtable<CurposTerm,Double>> candidates = buildTablesForTerms(candList,curpos);
		
		Hashtable<Term,Hashtable<CurposTerm,Double>> targets = buildTablesForTerms(targList,curpos);
		
		mainLmSimilarityMeasure.curpos = curpos;
		for (Entry<Term,Hashtable<CurposTerm,Double>> cand:candidates.entrySet()){
			for (Entry<Term,Hashtable<CurposTerm,Double>> targ:targets.entrySet()){
				double effectiveness = getModelsSimilarity(cand.getValue(),targ.getValue());
				res.updateMatch(targ.getKey(), cand.getKey(), effectiveness);
			}
		}
		
		return res;
	}

	private double getModelsSimilarity(Hashtable<CurposTerm, Double> model1,
			Hashtable<CurposTerm, Double> model2) {
		List<SimilarityResults> simResults = new ArrayList<>(model1.size()+model2.size());
		getMaxSimResults(model1, model2, simResults);
		getMaxSimResults(model2, model1, simResults);
		
		double sum = 0.0;
		// Calculate Average
		for (SimilarityResults curr:simResults)
			sum += (curr.simResult * curr.targConfidence * curr.candConfidence);
		
		double avg = sum/(simResults.size());
		return avg;
	}

	private void getMaxSimResults(Hashtable<CurposTerm, Double> modelInQuery,
			Hashtable<CurposTerm, Double> modelToSearchIn,
			List<SimilarityResults> simResults) {
		for (Entry<CurposTerm, Double> m1:modelInQuery.entrySet()){
			SimilarityResults maxResult = new SimilarityResults();
			for (Entry<CurposTerm, Double> m2:modelToSearchIn.entrySet()){
				double sim = mainLmSimilarityMeasure.MeausreSimilarity(m1.getKey(), m2.getKey());
				if (sim > maxResult.simResult)
				{
					maxResult.candConfidence = m1.getValue();
					maxResult.targConfidence = m2.getValue();
					maxResult.simResult = sim;
				}
			}
			simResults.add(maxResult);
		}
	}

	private Hashtable<Term,Hashtable<CurposTerm,Double>> buildTablesForTerms(Vector<Term> terms, MatchesCurpos curpos)
	{
		Hashtable<Term,Hashtable<CurposTerm,Double>> table = new Hashtable<>();
		for (Term t :terms)
		{
			CurposTerm curpTerm = new CurposTerm(t);
			Hashtable<CurposTerm, Double> termMap =curpos.SelectTermTable(curpTerm, termsPullPerTerm, threshold);
			if (termMap.size() < termsPullPerTerm){
				if (termMap.size() > 0) agumentMap(termMap, curpos);
				else agumentMapBySimilarityMatcher(curpTerm,termMap, curpos);
			}
				
			table.put(t,termMap);
			
		}
		return table;
	}
	
	private void agumentMapBySimilarityMatcher(CurposTerm term,Hashtable<CurposTerm, Double> termMap, MatchesCurpos curpos) {
		termMap.put(term, 1.0);
		CurposTerm similar = curpos.FindSimilarTerm(term,mainSimilarityMeasure);
		if (similar == null) return;
		Hashtable<CurposTerm, Double> simTermMap =curpos.SelectTermTable(similar, termsPullPerTerm, threshold);
		if (simTermMap.size() < termsPullPerTerm){
			if (simTermMap.size() > 0) agumentMap(simTermMap, curpos);
		}
		termMap.putAll(simTermMap);
	}

	private void agumentMap(Hashtable<CurposTerm, Double> termMap, MatchesCurpos curpos) {
		List<Map.Entry<CurposTerm, Double>> additionsPossible = new LinkedList<>();
		List<CurposTerm> additionsPossibleKeys = new LinkedList<>();
		for (Entry<CurposTerm, Double> entry : termMap.entrySet())
		{
			Hashtable<CurposTerm, Double> entryMap = curpos.SelectTermTable(entry.getKey(), termsPullPerTerm, threshold);
			for (Entry<CurposTerm, Double> augmentCandidate:entryMap.entrySet()){
				if 	((!termMap.containsKey(augmentCandidate.getKey())) && 
					(!additionsPossibleKeys.contains(augmentCandidate.getKey())) && 
				 	(augmentCandidate.getValue() * entry.getValue() > threshold)){
					additionsPossible.add(new AbstractMap.SimpleEntry<CurposTerm, Double>(augmentCandidate.getKey(), augmentCandidate.getValue() * entry.getValue()));
					additionsPossibleKeys.add(augmentCandidate.getKey());
				}
			}
		}
		Collections.sort(additionsPossible, new EntryMaptFitnessComparator());
		while (additionsPossible.size() > 0 && termMap.size() < termsPullPerTerm){
			int lastI = additionsPossible.size() -1;
			Entry<CurposTerm, Double> e = additionsPossible.remove(lastI);
			termMap.put(e.getKey(),e.getValue());
		}
		
	}

	@Override
	public String getConfig() {
		String config = "default";
		return config;
	}

	@Override
	public MatcherType getType() {
		return MatcherType.CORPUS;
	}

	@Override
	public int getDBid() {
		return 22;
	}
	
	static public class NGramCurposTermNameSimilarity implements CurposTermSimilarityMeausre{

		@Override
		public double MeausreSimilarity(CurposTerm t1, CurposTerm t2) {
			String s1 = t1.getName();
			String s2 = t2.getName();
			return StringUtilities.getNGramEffectivity(s1, s2, 3); // 3 - because in the article it was found to be sufficient 
		}
	}
	
	public class LmMetaCurposTermSimilarity implements CurposTermSimilarityMeausre{

		public LmMetaCurposTermSimilarity(){
			simMeasures = new LinkedList<CurposTermSimilarityMeausre>();
		}
		
		MatchesCurpos curpos;
		List<CurposTermSimilarityMeausre> simMeasures;
		
		@Override
		public double MeausreSimilarity(CurposTerm t1, CurposTerm t2) {
			
			double corpConfidence = curpos.getLevelOfConfidence(t1, t2);
			if (corpConfidence > threshold) return corpConfidence;
				
			double metaSim = mainSimilarityMeasure.MeausreSimilarity(t1, t2);
			
			if (corpConfidence < (1-threshold)) return metaSim;
			return 0.5*corpConfidence + 0.5*metaSim;
		}
	}
	
	static public class SimilarityResults
	{
		public double candConfidence;
		public double targConfidence;
		public double simResult;
	}
	
	
}

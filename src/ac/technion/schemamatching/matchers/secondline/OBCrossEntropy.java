package ac.technion.schemamatching.matchers.secondline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.schemamatching.statistics.MatchCompetitorDeviation;
import ac.technion.schemamatching.util.ce.CEModel;
import ac.technion.schemamatching.util.ce.CEObjective;
import ac.technion.schemamatching.util.ce.CESample;
import ac.technion.schemamatching.util.ce.CrossEntropyOptimizer;
import ac.technion.schemamatching.util.ce.CrossEntropyOptimizer.CEOptimizationResult;

/**
 * A second line matcher that finds a match using cross entropy optimization.
 * Able to produce either a 1:1 or 1:n matching. 
 */
public class OBCrossEntropy implements SecondLineMatcher{
	
	
	private int sampleSize = 10000;
	private double ro = 0.01;
	private double smoothAlpha = 0.7;
	private int stopAfter = 10;
	private boolean isOne2OneMatch = true;
	private int numSamplerThreads = 100;

	@Override
	public String getName() {
		return "CrossEntropy";
	}

	@Override
	public MatchInformation match(MatchInformation mi) {
		CrossEntropyOptimizer ceo = new CrossEntropyOptimizer(sampleSize, ro, stopAfter, numSamplerThreads);
		CEObjective objective = new OBObjective(mi);
		CEOptimizationResult result = ceo.optimize(objective, new OBModel(mi, smoothAlpha, isOne2OneMatch));
		return ((OBSample)result.bestSample).getMatchInformation();
	}

	@Override
	public String getConfig() {
		return "sampleSize="+sampleSize+", ro="+ro+", smoothAlpha="+smoothAlpha+", stopAfter="+stopAfter+", isOne2OneMatch="+isOne2OneMatch+", numSamplerThreads="+numSamplerThreads;
	}

	@Override
	public int getDBid() {
		return 13;
	}

	@Override
	public boolean init(Properties properties) {
		if (properties != null){
			sampleSize = Integer.parseInt(properties.getProperty("sampleSize", "10000"));
			ro = Double.parseDouble(properties.getProperty("ro", "0.01"));
			smoothAlpha = Double.parseDouble(properties.getProperty("smoothAlpha", "0.7"));
			stopAfter = Integer.parseInt(properties.getProperty("stopAfter", "10"));
			isOne2OneMatch = Boolean.parseBoolean(properties.getProperty("isOne2OneMatch", "true"));
			numSamplerThreads = Integer.parseInt(properties.getProperty("numSamplerThreads", "100"));
		}
		return true;
	}
	
	
	class OBSample implements CESample{
		
		private double value;
		private MatchInformation mi;
		
		public OBSample(MatchInformation mi) {
			this.mi = mi;
		}

		@Override
		public double getValue() {
			return value;
		}

		@Override
		public void setValue(double value) {
			this.value = value;
		}

		public MatchInformation getMatchInformation() {
			return mi;
		}

		public boolean hasMatchedPair(long candId, long targetId) {
			List<Match> match = mi.getCopyOfMatches();
			if (match != null){
				for (Match m : match){
					if (m.getCandidateTerm().getId() == candId && 
							m.getTargetTerm().getId() == targetId) return true;
				}
			}
			return false;
		}

	}
	
	
	class OBObjective implements CEObjective{
		
		private MatchInformation mi;
		
		public OBObjective(MatchInformation mi){
			this.mi = mi;
		}

		@Override
		public boolean isMaximized() {
			return true;
		}

		@Override
		public double evaluate(CESample sample) {
			OBSample _sample = (OBSample)sample;
			MatchInformation cemi = _sample.getMatchInformation();
			double totalMatchWeight = cemi.getTotalMatchWeight();
			//int numMatchedPairs = cemi.getNumMatches();
			MatchCompetitorDeviation mcd = new MatchCompetitorDeviation();
			mcd.init(null, mi, cemi);
			double mcdVal = Double.parseDouble(mcd.getData().get(0)[1]);
			return totalMatchWeight * mcdVal;//Math.sqrt(1.0/numMatchedPairs) Haggai change 25/8/2014 --> this
			//normalization was pushed to be internal in MCD
		}
		
	}
	
	class OBModel implements CEModel{
		
		class CandPair implements Cloneable, Comparable<CandPair>{
			int cand;
			int target;
			long candId;
			long targetId;
			double prob;
			double weight;
			
			public CandPair(double prob, long candId, long targetId, double weight) {
				super();
				this.prob = prob;
				this.candId = candId;
				this.targetId = targetId;
				this.weight = weight;
			}

			@Override
			protected Object clone() throws CloneNotSupportedException {
				return new CandPair(prob,candId,targetId,weight);
			}

			@Override
			public int compareTo(CandPair c) {
				if (c.prob > this.prob) return 1;
				else if (c.prob < this.prob) return -1;
				return 0;
			}
			
			
		}
	
		
		final ArrayList<CandPair> cands = new ArrayList<CandPair>();
		final MatchInformation mi;
		final double smoothAlpha;
		final boolean isOne2OneMatch;
		
		OBModel(MatchInformation mi, double smoothAlpha, boolean isOne2OneMatch){
			this.mi = mi;
			this.smoothAlpha = smoothAlpha;
			this.isOne2OneMatch = isOne2OneMatch;
		}

		@Override
		public void maxEntropy() {//max entropy distribution
			ArrayList<Match> match = mi.getCopyOfMatches();
			for (Match _m : match){
				cands.add(new CandPair(0.5,_m.getCandidateTerm().getId(),_m.getTargetTerm().getId(),_m.getEffectiveness()));
			}
		}

		@Override
		public void update(double gammaT, List<CESample> samples, CEObjective objective) {
			for (CandPair cand : cands){
				double lowerSum = 0;
				double upperSum = 0;
				for (CESample sample : samples){
					if (objective.isMaximized() ? sample.getValue() >= gammaT : sample.getValue() <= gammaT){
						lowerSum++;
						if (((OBSample)sample).hasMatchedPair(cand.candId,cand.targetId)){
							upperSum++;
						}
					}
				}
				
				double prob = lowerSum > 0 ? upperSum/lowerSum : 0;
				cand.prob = (smoothAlpha*prob) + (1-smoothAlpha)*cand.prob;
			}
			
		}

		@Override
		@SuppressWarnings("all")
		public CESample drawRandomSample() {
			ArrayList<CandPair> activeCands = (ArrayList<CandPair>)cands.clone();
			//start with some random order
			Collections.shuffle(activeCands);
			ArrayList<CandPair> pairs = new ArrayList<CandPair>();
			Iterator<CandPair> candItr = activeCands.iterator();
			while(candItr.hasNext()){
				CandPair select = candItr.next();
				//toss coin ~Bernulli(select.prob)
				if (1 - Math.random() <= select.prob){
					pairs.add(select);
					//remove all active competitors
					removeCompetitors(select,activeCands.iterator());	
					candItr = activeCands.iterator();
				}
			}
			
			MatchInformation mmi = new MatchInformation(mi.getCandidateOntology(), mi.getTargetOntology());
			mmi.setMatches(preparePairs(pairs));
			
			return new OBSample(mmi);
			
		}
		
		
		private void removeCompetitors(CandPair select, Iterator<CandPair> candItr){
			while (candItr.hasNext()){
				CandPair check = candItr.next();
				if (isOne2OneMatch && (check.targetId == select.targetId || check.candId == select.candId)){//1:1 match
					//remove competitor to have a correct match
					candItr.remove();
				}else if (!isOne2OneMatch && check.targetId == select.targetId){//1:n match
					//remove competitor to have a correct match
					candItr.remove();
				}
			}
		}
	   
	    private ArrayList<Match> preparePairs(ArrayList<CandPair> pairs)
	    {
	    	ArrayList<Match> m = new ArrayList<Match>();
	        MatchMatrix mm =  mi.getMatrix();
	        
	        for (CandPair pair : pairs)
	        {    	
	            Term cTerm = mm.getTermByID(pair.candId, true);
	            Term tTerm = mm.getTermByID(pair.targetId, false);
	            m.add(new Match(tTerm,cTerm,pair.weight));
	        }
	        return m;
	    }
		
	}

}

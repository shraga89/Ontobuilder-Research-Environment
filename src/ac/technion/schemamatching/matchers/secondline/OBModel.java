package ac.technion.schemamatching.matchers.secondline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.schemamatching.util.ce.CEModel;
import ac.technion.schemamatching.util.ce.CEObjective;
import ac.technion.schemamatching.util.ce.CESample;

public class OBModel implements CEModel{
		
		class CandPair implements Cloneable, Comparable<CandPair>{
			int cand;
			int target;
			String candId;
			String targetId;
			double prob;
			double weight;
			
			public CandPair(double prob, String candId, String targetId, double weight) {
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
		
		public OBModel(MatchInformation mi, double smoothAlpha, boolean isOne2OneMatch){
			this.mi = mi;
			this.smoothAlpha = smoothAlpha;
			this.isOne2OneMatch = isOne2OneMatch;
		}

		@Override
		public void maxEntropy() {//max entropy distribution
			if (isOne2OneMatch) {
				maxEntropyOneToOne();
			}else {
				maxEntropyOneToMany();
			}
		}
		
		private void maxEntropyOneToOne() {
			ArrayList<Match> match = mi.getCopyOfMatches();
			for (Match _m : match){
				cands.add(new CandPair(0.5,_m.getCandidateTerm().getName(),_m.getTargetTerm().getName(),_m.getEffectiveness()));
			}
		}
		
		private void maxEntropyOneToMany() {
			ArrayList<Match> match = mi.getCopyOfMatches();
			//should be per number of targets (on row) of a given candidate
			for (Match _m : match){
				cands.add(new CandPair(1f/mi.getMatrix().getRowCount(),_m.getCandidateTerm().getName(),_m.getTargetTerm().getName(),_m.getEffectiveness()));
			}
		}
		
		@Override
		public void update(double gammaT, CESample[] samples, CEObjective objective, ForkJoinPool pool) {
			if (pool != null) {
				parallelUpdate(gammaT, samples, objective, pool);
			}else {
				serialUpdate(gammaT, samples, objective);
			}
		}

		
		private void serialUpdate(double gammaT, CESample[] samples, CEObjective objective) {
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
		
		
		class CandUpdateTask extends RecursiveAction{

			private static final long serialVersionUID = -616155776060985082L;
			
			private CandPair cand;	
			private double gammaT;
			private CESample[] samples;
			private CEObjective objective;

			public CandUpdateTask(CandPair cand, double gammaT,
					CESample[] samples, CEObjective objective) {
				super();
				this.cand = cand;
				this.gammaT = gammaT;
				this.samples = samples;
				this.objective = objective;
			}

			@Override
			protected void compute() {
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
		
		
		private void parallelUpdate(double gammaT, CESample[] samples, CEObjective objective, ForkJoinPool pool) {
			ArrayList<CandUpdateTask> updaters = new ArrayList<CandUpdateTask>();
			for (CandPair cand : cands){
				CandUpdateTask updater = new CandUpdateTask(cand, gammaT, samples, objective);
				updaters.add(updater);
				pool.execute(updater);
			}
			
			for (CandUpdateTask updater : updaters){
				updater.join();
			}
		}

//		@Override
//		@SuppressWarnings("all")
//		public CESample drawRandomSample() {
//			ArrayList<CandPair> activeCands = (ArrayList<CandPair>)cands.clone();
//			//start with some random order
//			Collections.shuffle(activeCands);
//			ArrayList<CandPair> pairs = new ArrayList<CandPair>();
//			Iterator<CandPair> candItr = activeCands.iterator();
//			while(candItr.hasNext()){
//				CandPair select = candItr.next();
//				
//				boolean selected = false;
//				if (isOne2OneMatch) {
//					//toss coin ~Bernulli(select.prob)
//					selected = Math.random() <= select.prob;
//				}else {
//					//toss multinomial
//					double prob = Math.random();
//					ArrayList<CandPair> targets = getCandTargets(select, activeCands);
//					double cdf = 0;
//					for (CandPair target : targets) {
//						cdf += target.prob;
//						if (cdf >= prob) {
//							select = target;
//							selected = true;
//							break;
//						}
//					}
//					
//					if (!selected) {
//						select = targets.get(targets.size() - 1);
//						selected = true;
//					}
//				}
//				
//				
//				if (selected){
//					pairs.add(select);
//					//remove all active competitors
//					removeCompetitors(select,activeCands.iterator());	
//					candItr = activeCands.iterator();
//				}
//			}
//			
//			MatchInformation mmi = new MatchInformation(mi.getCandidateOntology(), mi.getTargetOntology());
//			mmi.setMatches(preparePairs(pairs));
//			
//			return new OBSample(mmi);
//			
//		}
		
		
		@Override
		@SuppressWarnings("all")
		public CESample drawRandomSample() {
			ArrayList<CandPair> activeCands = (ArrayList<CandPair>)cands.clone();
			ArrayList<CandPair> pairs = new ArrayList<CandPair>();
			while(!activeCands.isEmpty()){
				int next = (int)(Math.random() * activeCands.size());
				CandPair select = activeCands.get(next);
				
				boolean selected = false;
				if (isOne2OneMatch) {
					//toss coin ~Bernulli(select.prob)
					selected = Math.random() <= select.prob;
				}else {
					//toss multinomial
					double prob = Math.random();
					ArrayList<CandPair> targets = getCandTargets(select, activeCands);
					double cdf = 0;
					for (CandPair target : targets) {
						cdf += target.prob;
						if (cdf >= prob) {
							select = target;
							selected = true;
							break;
						}
					}
					
					if (!selected) {
						select = targets.get(targets.size() - 1);
						selected = true;
					}
				}
				
				activeCands.remove(next);
				if (selected){
					pairs.add(select);
					//remove all active competitors
					removeCompetitors(select,activeCands.iterator());	
				}
			}
			
			MatchInformation mmi = new MatchInformation(mi.getCandidateOntology(), mi.getTargetOntology());
			mmi.setMatches(preparePairs(pairs));
			
			return new OBSample(mmi);
			
		}
		
		public ArrayList<CandPair> getCandTargets(CandPair p, ArrayList<CandPair> activeCands){
			String cand = p.candId;
			ArrayList<CandPair> targets = new ArrayList<OBModel.CandPair>();
			for (CandPair ac : activeCands) {
				if (ac.candId.equals(cand)) {
					targets.add(ac);
				}
			}
			Collections.sort(targets);
			return targets;
		}
		
		
		private void removeCompetitors(CandPair select, Iterator<CandPair> candItr){
			while (candItr.hasNext()){
				CandPair check = candItr.next();
				if (isOne2OneMatch && (check.targetId.equals(select.targetId) || check.candId.equals(select.candId))){//1:1 match
					//remove competitor to have a correct match
					candItr.remove();
				}else if (!isOne2OneMatch && check.candId.equals(select.candId)){//1:n match
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
	            Term cTerm = mm.getTermByName(pair.candId, mm.getCandidateTerms());
	            Term tTerm = mm.getTermByName(pair.targetId, mm.getTargetTerms());
	            m.add(new Match(tTerm,cTerm,pair.weight));
	        }
	        return m;
	    }
	    
}
		


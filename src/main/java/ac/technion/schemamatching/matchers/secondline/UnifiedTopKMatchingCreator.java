package ac.technion.schemamatching.matchers.secondline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;

public class UnifiedTopKMatchingCreator {
	
	public static double selectionThreshold = 0.0;
	public static int groupCountThresholdExhaustiveSearch = 8;
	public static int groupPartCountThresholdPatitiningHeuristic = 5;
	
	public enum WEIGHTING {
		SIMILARITY,
		OCCURRENCE,
		OCCURRENCE_EQUAL
	}
	
	
	List<MatchInformation> matchings = new ArrayList<MatchInformation>();
	
	MatchMatrix unifiedTopKGraph = null;
	
	public WEIGHTING weighting = UnifiedTopKMatchingCreator.WEIGHTING.OCCURRENCE;

	public UnifiedTopKMatchingCreator() {
	}

	public UnifiedTopKMatchingCreator(WEIGHTING w) {
		this.weighting = w;
	}
	
	public void addMatching(int k, MatchInformation res) {
		this.matchings.add(k-1, res);
	}
	
	public void buildGraph() {

		MatchMatrix first = this.matchings.get(0).getMatrix();
		
		this.unifiedTopKGraph = new MatchMatrix(
				first.getColCount(), 
				first.getRowCount(),
				first.getCandidateTerms(),
				first.getTargetTerms());
						
		if (this.weighting.equals(UnifiedTopKMatchingCreator.WEIGHTING.SIMILARITY)) {
			for (MatchInformation mi : this.matchings) {
				for (Match m : mi.getCopyOfMatches()) {
					this.unifiedTopKGraph.setMatchConfidence(
							m.getCandidateTerm(), m.getTargetTerm(), mi.getMatchConfidence(m.getCandidateTerm(), m.getTargetTerm()));
				}
			}
		}
		else if (this.weighting.equals(UnifiedTopKMatchingCreator.WEIGHTING.OCCURRENCE)) {
			for (int i = 0; i < this.matchings.size(); i++) {
				MatchInformation mi = this.matchings.get(i);
				double k = this.matchings.size();
				double conf = (2.0*(k-i)) / (k* (k + 1.0));
				for (Match m : mi.getCopyOfMatches()) {
					this.unifiedTopKGraph.setMatchConfidence(
							m.getCandidateTerm(), m.getTargetTerm(), this.unifiedTopKGraph.getMatchConfidence(m.getCandidateTerm(), m.getTargetTerm()) + conf);
				}
			}
		}
		else if (this.weighting.equals(UnifiedTopKMatchingCreator.WEIGHTING.OCCURRENCE_EQUAL)) {
			for (int i = 0; i < this.matchings.size(); i++) {
				MatchInformation mi = this.matchings.get(i);
				double k = this.matchings.size();
				for (Match m : mi.getCopyOfMatches()) {
					double conf = 1.0 / k;
					this.unifiedTopKGraph.setMatchConfidence(
							m.getCandidateTerm(), m.getTargetTerm(), this.unifiedTopKGraph.getMatchConfidence(m.getCandidateTerm(), m.getTargetTerm()) + conf);
				}
			}
		}
		
//		Term t1 = this.unifiedTopKGraph.getTermByName("email (email)");
//		Term t2 = this.unifiedTopKGraph.getTermByName("contactEmail (contactEmail)");
//	
//		System.out.println(this.unifiedTopKGraph.getMatchConfidence(t2,t1));
		
		
	}

				
	double[][] probForMatch = null;
	
	public void deriveClusters() {
		
		this.probForMatch = new double[this.unifiedTopKGraph.getColCount()][this.unifiedTopKGraph.getRowCount()];
		
		double confSum = 0;
		
		for (Term t : this.unifiedTopKGraph.getTargetTerms())
			for (Term c : this.unifiedTopKGraph.getCandidateTerms())
				confSum += this.unifiedTopKGraph.getMatchConfidence(c, t);
		
		double[] confSumTarget = new double[this.unifiedTopKGraph.getRowCount()];
		double[] confSumCandidate = new double[this.unifiedTopKGraph.getColCount()];
		
		for (Term t : this.unifiedTopKGraph.getTargetTerms()) {
			for (Term c : this.unifiedTopKGraph.getCandidateTerms()) {
				confSumTarget[this.unifiedTopKGraph.getTermIndex(null, t, false)] += this.unifiedTopKGraph.getMatchConfidence(c, t);
				confSumCandidate[this.unifiedTopKGraph.getTermIndex(null, c, true)] += this.unifiedTopKGraph.getMatchConfidence(c, t);
			}
		}
		
		for (Term t : this.unifiedTopKGraph.getTargetTerms()) {
			for (Term c : this.unifiedTopKGraph.getCandidateTerms()) {
				double conf = (confSumTarget[this.unifiedTopKGraph.getTermIndex(null, t, false)] * confSumCandidate[this.unifiedTopKGraph.getTermIndex(null, c, true)]) / (2.0 * confSum);
				probForMatch[this.unifiedTopKGraph.getTermIndex(null, c, true)][this.unifiedTopKGraph.getTermIndex(null, t, false)] = conf;
			}
		}
		
		Set<Cluster> clusters = new HashSet<UnifiedTopKMatchingCreator.Cluster>();
		
		Set<Term> toOptTarget = new HashSet<Term>();
		Set<Term> toOptCandidate = new HashSet<Term>();
		
		for (Term t : this.unifiedTopKGraph.getTargetTerms()) {
			Set<Term> matched = new HashSet<Term>();
			for (Term c : this.unifiedTopKGraph.getCandidateTerms()) {
				if (this.unifiedTopKGraph.getMatchConfidence(c, t) > 0)
					matched.add(c);
			}
			if (matched.isEmpty())
				clusters.add(new Cluster(t,null));
			else if (matched.size() > 1) {
				toOptTarget.add(t);
				toOptCandidate.addAll(matched);
			}
		}
		
		for (Term c : this.unifiedTopKGraph.getCandidateTerms()) {
			Set<Term> matched = new HashSet<Term>();
			for (Term t : this.unifiedTopKGraph.getTargetTerms()) {
				if (this.unifiedTopKGraph.getMatchConfidence(c, t) > 0)
					matched.add(t);
			}
			if (matched.isEmpty())
				clusters.add(new Cluster(null,c));
			else if (matched.size() > 1) {
				toOptTarget.addAll(matched);
				toOptCandidate.add(c);
			}
		}

		for (Term t : this.unifiedTopKGraph.getTargetTerms()) {
			Set<Term> matched = new HashSet<Term>();
			for (Term c : this.unifiedTopKGraph.getCandidateTerms()) {
				if (this.unifiedTopKGraph.getMatchConfidence(c, t) > 0)
					matched.add(c);
			}
			if (matched.size() == 1 && !toOptTarget.contains(t)) {
					clusters.add(new Cluster(t,matched.iterator().next()));
			}
		}
		
		Set<Set<Term>> groupsUnchecked = extractIndependentGroups(toOptTarget, toOptCandidate);
		
		System.out.println("Cluster: groups");
		
		/*
		 * Check whether group is small enough to check for exhaustive check 
		 * or heuristic check with all partitionings of the respective nodes. 
		 * If not, remove link with minimal weight to 
		 * split up the group.
		 */
		Set<Set<Term>> groupsChecked = new HashSet<Set<Term>>();
		for (Set<Term> group : groupsUnchecked) 
			groupsChecked.addAll(splitUpGroupIfNeeded(group));
		
		for (Set<Term> group : groupsChecked) {
			/*
			 * can we go for exhaustive search?
			 */
			if (group.size() <= groupCountThresholdExhaustiveSearch) {
				System.out.print(group.size()  + "()" + " ");
				double maxMod = 0;
				Set<Cluster> best = null;

				Set<Set<Set<Term>>> partitionings = getAllPartitionings(new ArrayList<Term>(group));
				for (Set<Set<Term>> partitioning : partitionings) {
					Set<Cluster> tmpClusters = new HashSet<UnifiedTopKMatchingCreator.Cluster>();
					for (Set<Term> partition : partitioning) {
						Cluster c = new Cluster();
						for (Term t : partition) {
							int id = this.unifiedTopKGraph.getTermIndex(null, t, false);
							if (id == -1)
								c.candidateTerms.add(t);
							else
								c.targetTerms.add(t);
						}
						tmpClusters.add(c);
					}
					double mod = computeModularity(tmpClusters);
					if (mod > maxMod) {
						maxMod = mod;
						best = tmpClusters;
					}
				}
				if (best != null)
					clusters.addAll(best);
			}
			/*
			 * no, focus on heuristic
			 */
			else {
				List<Term> groupTarget = new ArrayList<Term>(group);
				groupTarget.removeAll(this.unifiedTopKGraph.getCandidateTerms());
				List<Term> groupCandidate = new ArrayList<Term>(group);
				groupCandidate.removeAll(this.unifiedTopKGraph.getTargetTerms());
								
				System.out.print(group.size()  + "(" + groupTarget.size() + ")" + " ");
				
				Set<Set<Set<Term>>> partitioningsTar = getAllPartitionings(groupTarget);
				Set<Set<Set<Term>>> partitioningsCan = getAllPartitionings(groupCandidate);
				
				Set<Cluster> potentialClusters = new HashSet<UnifiedTopKMatchingCreator.Cluster>();
				for (Set<Set<Term>> partitioningTar : partitioningsTar) {
					for (Set<Set<Term>> partitioningCan : partitioningsCan) {
						for (Set<Term> partitionTar : partitioningTar) {
							for (Set<Term> partitionCan : partitioningCan) {
								Cluster c = new Cluster();
								c.targetTerms = partitionTar;
								c.candidateTerms = partitionCan;
								potentialClusters.add(c);
							}
						}
					}
				}

				for (Term tar: groupTarget) {
					Cluster c = new Cluster(tar, null);
					potentialClusters.add(c);
				}
				for (Term cand: groupCandidate) {
					Cluster c = new Cluster(null, cand);
					potentialClusters.add(c);
				}
				
				Set<Cluster> selectedClusters = new HashSet<UnifiedTopKMatchingCreator.Cluster>();

				Set<Term> consideredTerms = new HashSet<Term>();
				while (!consideredTerms.containsAll(group)) {
					/*
					 * Get the best still qualifying to be added
					 */
					double maxMod = 0;
					Cluster best = null;
					
					potential:
					for (Cluster c : potentialClusters) {
						boolean qualifies = true;
						for (Cluster selected : selectedClusters) {
							for (Term t : selected.targetTerms)
								qualifies &= !(c.targetTerms.contains(t));
							for (Term t : selected.candidateTerms)
								qualifies &= !(c.candidateTerms.contains(t));
							if (!qualifies)
								continue potential;
						}
						
						Set<Cluster> tmpClusters = new HashSet<UnifiedTopKMatchingCreator.Cluster>(selectedClusters);
						tmpClusters.add(c);
						double mod = computeModularity(tmpClusters);
						if (mod > maxMod) {
							maxMod = mod;
							best = c;
						}
					}
					if (best != null) {
						clusters.add(best);
						consideredTerms.addAll(best.targetTerms);
						consideredTerms.addAll(best.candidateTerms);
					}
				}
			}
		}

		System.out.print("\n");

		for (Cluster c : clusters) 
			judgeClustersQuality(c);
		
	}
	
	protected Set<Set<Term>> splitUpGroupIfNeeded(Set<Term> group) {
		List<Term> groupTarget = new ArrayList<Term>(group);
		groupTarget.removeAll(this.unifiedTopKGraph.getCandidateTerms());
		List<Term> groupCandidate = new ArrayList<Term>(group);
		groupCandidate.removeAll(this.unifiedTopKGraph.getTargetTerms());
		
		Set<Set<Term>> result = new HashSet<Set<Term>>();
		
		if ((groupTarget.size() <= groupPartCountThresholdPatitiningHeuristic) && (groupCandidate.size() <= groupPartCountThresholdPatitiningHeuristic)) {
			if (groupTarget.size() > 0 && groupCandidate.size() > 0)
				result.add(group);
			return result;
		}
		
		// find min confidence larger than 0
		double minConf = 1;
		Term minTar = null;
		Term minCand = null;
		for (Term tar : groupTarget) {
			for (Term cand : groupCandidate) {
				double conf = this.unifiedTopKGraph.getMatchConfidence(cand, tar);
				if (conf != 0) {
					minConf = Math.min(minConf, conf);
					if (minConf == conf) {
						minTar = tar;
						minCand = cand;
					}
				}
			}
		}
		this.unifiedTopKGraph.setMatchConfidence(minCand, minTar, 0);
		Set<Set<Term>> smallerGroups = extractIndependentGroups(groupTarget, groupCandidate);
		
		for (Set<Term> smallerGroup : smallerGroups) {
			result.addAll(splitUpGroupIfNeeded(smallerGroup));
		}
		return result;
	}


	Map<Cluster, Double> clusterQualities = new HashMap<Cluster, Double>();
	
	private void judgeClustersQuality(Cluster c) {
		
		double internalSum = 0;
		
		for (Term tar : c.targetTerms) 
			for (Term cand : c.candidateTerms) 
				internalSum += this.unifiedTopKGraph.getMatchConfidence(cand, tar);
	
		double internalQual = (c.targetTerms.size() > 0 && c.candidateTerms.size() > 0)? internalSum / (1.0 * (c.targetTerms.size() * c.candidateTerms.size())) : 1;
		
		double extSumOne = 0;
		for (Term tar : c.targetTerms) {
			double extSumTar = 0;
			double countExtTar = 0;
			
			for (Term cand : this.unifiedTopKGraph.getCandidateTerms()) { 
				double conf = this.unifiedTopKGraph.getMatchConfidence(cand, tar);
				
				if ((!c.candidateTerms.contains(cand)) && conf > 0) {
					countExtTar++;
					extSumTar += conf;
				}
			}
			if (countExtTar > 0)
				extSumOne += extSumTar / countExtTar;
		}
		
		double extSumTwo = 0;
		for (Term cand : c.candidateTerms) {
			double extSumCand = 0;
			double countExtCand = 0;
			
			for (Term tar : this.unifiedTopKGraph.getTargetTerms()) { 
				double conf = this.unifiedTopKGraph.getMatchConfidence(cand, tar);
				
				if ((!c.targetTerms.contains(tar)) && conf > 0) {
					countExtCand++;
					extSumCand += conf;
				}
			}
			if (countExtCand > 0)
				extSumTwo += extSumCand / countExtCand;
		}
		
		double cSize = c.targetTerms.size() + c.candidateTerms.size();
		double externalQual = 1.0 - (extSumOne / cSize) - (extSumTwo / cSize);
		this.clusterQualities.put(c, (internalQual + externalQual)/2);
	}


	private <X> Set<Set<Set<X>>>  getAllPartitionings(List<X> group) {

		Set<Set<Set<X>>> partitions = new HashSet<Set<Set<X>>>();

		List<Integer> sList = new ArrayList<Integer>();
		List<Integer> mList = new ArrayList<Integer>();
			
		for (int i = 0; i < group.size(); i++) {
			sList.add(0,1);
			mList.add(0,1);
		}
		
	    partitions.add(buildPartitionSet(sList,group));
	    
	    while (getNextPartitionings(sList,mList,group.size()))
		    partitions.add(buildPartitionSet(sList,group));
		
		return partitions;
	}
	
	private <X> Set<Set<X>> buildPartitionSet(List<Integer> sList, List<X> group) {
		Set<Set<X>> partitioning = new HashSet<Set<X>>();
		int pa = 1;
		for (int i = 0; i < group.size(); i++) {
			if (sList.get(i) > pa)
				pa = sList.get(i);
		}
		
	    for (int p = pa; p >= 1; --p) {
			Set<X> partition = new HashSet<X>();
	    	for (int i = 0; i < group.size(); ++i)
	    		if (sList.get(i) == p)
	    			partition.add(group.get(i));
	    	partitioning.add(partition);
	    }		
		
	    return partitioning;
	}
	

	private boolean getNextPartitionings(List<Integer> sList, List<Integer> mList, int n) {
		int i = 0;
		sList.set(i,sList.get(i)+1);
		while ((i < n - 1) && (sList.get(i) > mList.get(i) + 1)) {
			sList.set(i,1);
			++i;
			sList.set(i,sList.get(i)+1);
		}
		
		if (i == n - 1)
			return false;
		
		
		int max = sList.get(i);
		if (mList.get(i) > max) 
			max = mList.get(i);
		
		for (int j = i - 1; j >= 0; --j)
			mList.set(j,max);	
		
		return true;
	}

	private Set<Set<Term>> extractIndependentGroups(Collection<Term> toOptTarget, Collection<Term> toOptCandidate) {
		Set<Set<Term>> result = new HashSet<Set<Term>>();
		
		Set<Term> processed = new HashSet<Term>();
		while (!processed.containsAll(toOptTarget)) {
			Iterator<Term> it = toOptTarget.iterator();
			Term start = it.next();
			while (processed.contains(start))
				start = it.next();

			Set<Term> group = new HashSet<Term>();
			Set<Term> toCheck = new HashSet<Term>();
			toCheck.add(start);
			while (!toCheck.isEmpty()) {
				Term t = toCheck.iterator().next();
				toCheck.remove(t);
				Set<Term> matched = new HashSet<Term>();
				for (Term c : toOptCandidate) {
					if (this.unifiedTopKGraph.getMatchConfidence(c, t) > 0) {
						if (!group.contains(c))
							matched.add(c);
						
					}
				}
				
				group.add(t);
				group.addAll(matched);
				
				for (Term c : matched) {
					for (Term t2 : toOptTarget) {
						if (this.unifiedTopKGraph.getMatchConfidence(c, t2) > 0)
							if (!group.contains(t2))
								toCheck.add(t2);
					}
				}
			}
			result.add(group);
			processed.addAll(group);
		}
		
		return result;
	}
		
	public class Cluster {
		
		public Set<Term> targetTerms = new HashSet<Term>();
		public Set<Term> candidateTerms = new HashSet<Term>();
		
		public Cluster() {
			
		}
		
		public Cluster(Term tar, Term cand) {
			if (tar != null)
				targetTerms.add(tar);
			if (cand != null)
				candidateTerms.add(cand);
		}
	}

	private double computeModularity(Set<Cluster> clusters) {
		double result = 0;
		for (Cluster c : clusters) 
			result += computeModularity(c);
		
		return result;
	}
	
	private double computeModularity(Cluster c) {
		double result = 0.0;
		for (Term i : c.targetTerms) 
			for (Term j : c.candidateTerms) 
				result += this.unifiedTopKGraph.getMatchConfidence(j, i) 
				- probForMatch[this.unifiedTopKGraph.getTermIndex(null, j, true)][this.unifiedTopKGraph.getTermIndex(null, i, false)];
		
		return result;
	}

	
	public MatchInformation getResultingMatching() {
		MatchInformation res = new MatchInformation(this.matchings.iterator().next().getCandidateOntology(),this.matchings.iterator().next().getTargetOntology());

		for (Cluster c : this.clusterQualities.keySet()) {
			double conf = this.clusterQualities.get(c);
			
			if (conf < selectionThreshold)
				continue;
			
			for (Term tar : c.targetTerms) {
				for (Term cand : c.candidateTerms) {
					res.updateMatch(tar, cand, conf);
				}
			}
		}
		
		return res;
	}

	
	

}

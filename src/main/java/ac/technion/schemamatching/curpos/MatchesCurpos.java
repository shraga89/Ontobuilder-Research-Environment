package ac.technion.schemamatching.curpos;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * 
 */
public class MatchesCurpos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6274228571673909306L;
	private Hashtable<CurposTerm, Hashtable<CurposTerm, TermMatchInfo>> innerCurpos;

	public MatchesCurpos() {
		innerCurpos = new Hashtable<>();
	}

	Hashtable<CurposTerm,Hashtable<CurposTerm,TermMatchInfo>> getInnerStructure(){
		return innerCurpos;
	}
	
	/*
	 * Add the level of fitness between 2 terms, if the terms do not exist yet,
	 * add them
	 */
	public void add(CurposTerm term1, CurposTerm term2, double levelOfFitness) {
		Hashtable<CurposTerm, TermMatchInfo> termCurp = getOrCreateNewTermEntry(term1);

		TermMatchInfo info = (termCurp.containsKey(term2)) ? termCurp
				.get(term2) : new TermMatchInfo(0.0, 0);
		info.confidence = (info.confidence * info.repetitions + levelOfFitness)
				/ ((double) (info.repetitions + 1));
		info.repetitions++;

		termCurp.put(term2, info);

		termCurp = getOrCreateNewTermEntry(term2);

		termCurp.put(term1, info);
	}

	private Hashtable<CurposTerm, TermMatchInfo> getOrCreateNewTermEntry(
			CurposTerm t) {
		if (!innerCurpos.containsKey(t)) {
			Hashtable<CurposTerm, TermMatchInfo> termCurp = new Hashtable<CurposTerm, TermMatchInfo>();
			termCurp.put(t, new TermMatchInfo(1.0, 1));
			innerCurpos.put(t, termCurp);
			return termCurp;
		}
		return innerCurpos.get(t);
	}

	public boolean Exsits(CurposTerm term) {
		return innerCurpos.containsKey(term);
	}

	public double getLevelOfConfidence(CurposTerm term1, CurposTerm term2) {
		if (!innerCurpos.containsKey(term1))
			return 0.0;
		Hashtable<CurposTerm, TermMatchInfo> table = innerCurpos.get(term1);
		if (!table.containsKey(term2))
			return 0.0;
		return table.get(term2).confidence;
	}

	public double getNumOfRepetitions(CurposTerm term1, CurposTerm term2) {
		if (!innerCurpos.containsKey(term1))
			return 0.0;
		Hashtable<CurposTerm, TermMatchInfo> table = innerCurpos.get(term1);
		if (!table.containsKey(term2))
			return 0.0;
		return table.get(term2).repetitions;
	}

	/*
	 * Return a Hashtable<Double,CurposTerm> that fit the term
	 * 
	 * @Param term The term to which to find the matches
	 * 
	 * @Param count The maximum number of terms to return, will return those
	 * with the highest fitness
	 * 
	 * @Param threshold The minimum fitness acceptable
	 */
	public Hashtable<CurposTerm, Double> SelectTermTable(CurposTerm term,
			int count, double threshold) {
		Hashtable<CurposTerm, Double> retVal = new Hashtable<>();
		if (innerCurpos.containsKey(term)) {
			List<Map.Entry<CurposTerm, Double>> inversed = new LinkedList<>(); // HashBiMap.create(count);
			Hashtable<CurposTerm, TermMatchInfo> termTable = innerCurpos
					.get(term);
			for (Map.Entry<CurposTerm, TermMatchInfo> pair : termTable
					.entrySet()) {
				if (pair.getValue().confidence > threshold)
					inversed.add(new AbstractMap.SimpleEntry<CurposTerm, Double>(
							pair.getKey(), pair.getValue().confidence));
			}
			Collections.sort(inversed, new EntryMaptFitnessComparator());
			while (inversed.size() > count)
				inversed.remove(0);

			for (Map.Entry<CurposTerm, Double> pair : inversed)
				retVal.put(pair.getKey(), pair.getValue());
		}
		return retVal;
	}

	public CurposTerm FindSimilarTerm(CurposTerm orig,
			CurposTermSimilarityMeausre measurementUtil) {
		CurposTerm mostSim = null;
		double maxSim = 0.0;
		for (CurposTerm t1 : innerCurpos.keySet()) {
			double sim = measurementUtil.MeausreSimilarity(orig, t1);
			if (sim > maxSim) {
				maxSim = sim;
				mostSim = t1;
			}
		}

		return mostSim;
	}

	static public class EntryMaptFitnessComparator implements
			Comparator<Map.Entry<CurposTerm, Double>> {

		@Override
		public int compare(Map.Entry<CurposTerm, Double> o1,
				Map.Entry<CurposTerm, Double> o2) {
			return (o1.getValue() > o2.getValue() ? 1 : (o1.getValue() == o2
					.getValue() ? 0 : -1));
		}
	}

	public static class TermMatchInfo implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1088404961569031047L;

		public TermMatchInfo(double confidence, long repetitions) {
			this.confidence = confidence;
			this.repetitions = repetitions;
		}

		public double confidence;
		public long repetitions;

	}
}

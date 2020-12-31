/**
 * 
 */
package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * @author Tomer Sagi
 * MD captures the notion of completeness by returning a 
 * normalized distance between the given result and the 
 * exact match only over dimensions corresponding to expected 
 * matched attribute pairs. 
 * NMD completes the picture by presenting a normalized 
 * distance only over non-match dimensions of the task, 
 * thus capturing the amount of noise in a specific solution. 
 * Both functions are reversed to retain the intuitive 
 * interpretation where a higher result is preferred. 
 * Since normalization expressions are based upon the task's 
 * exact match vectors, they remain constant between 
 * different match results and thus the functions retain 
 * the sub-additivity property of distance measures.
 * Assumes exact-match vectors are binary. 
 *
 */
public class MatchDistance implements K2Statistic {

	String[] header = new String[]{"instance","MD","NMD"};
	private List<String[]> data = new ArrayList<String[]>();
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#getHeader()
	 */
	@Override
	public String[] getHeader() {
		return header;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#getName()
	 */
	@Override
	public String getName() {
		return "Match Distance";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#getData()
	 */
	@Override
	public List<String[]> getData() {
		return data ;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#init(java.lang.String, ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	@Override
	public boolean init(String instanceDescription, MatchInformation mi) {
		return false;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.K2Statistic#init(java.lang.String, ac.technion.iem.ontobuilder.matching.match.MatchInformation, ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	@Override
	public boolean init(String instanceDescription, MatchInformation mi,
			MatchInformation exactMatch) {
		String[] res = new String[3];
		res[0] = instanceDescription;
		Set<Match> exact = new HashSet<Match>(exactMatch.getCopyOfMatches());
		double mdDenom = Math.sqrt((double)exact.size());
		double nmdDenom = Math.sqrt((double)((long)exactMatch.getCandidateOntologyTermsTotal()*(long)exactMatch.getTargetOntologyTermsTotal()-exact.size()));
		double mdDist = 0.0;
		double nmdDist = 0.0;
		for (Match m : mi.getCopyOfMatches())
		{
			double eVal = exactMatch.getMatchConfidence(m.getCandidateTerm(), m.getTargetTerm());
			if (eVal>0.0) 
				exact.remove(m);
			double val = m.getEffectiveness(); 
			mdDist+=Math.pow(eVal-eVal*val,2.0);
			nmdDist+=Math.pow((1-eVal)*eVal-(1-eVal)*val,2.0);
		}
		//Add 1 distances for unmatched exact matches
		mdDist+=(double)exact.size();
		
		//finish
		double md = 1.0-(mdDenom ==0 ? 1.0 : Math.sqrt(mdDist)/mdDenom); 
		double nmd = 1.0-(nmdDenom ==0 ? 1.0 : Math.sqrt(nmdDist)/nmdDenom);
		res[1] = Double.toString(md);
		res[2] = Double.toString(nmd);
		data.add(res);
		return true;
	}

}

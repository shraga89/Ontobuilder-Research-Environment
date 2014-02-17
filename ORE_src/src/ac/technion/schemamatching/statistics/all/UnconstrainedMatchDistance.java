/**
 * 
 */
package ac.technion.schemamatching.statistics.all;

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
 * reference vector, they remain constant between 
 * different match results and thus the functions retain 
 * the sub-additivity property of distance measures. 
 * Unconstrained version of @link{MatchDistance} allowing 
 * non-binary reference vectors. 
 */
public class UnconstrainedMatchDistance implements K2Statistic {

	String[] header = new String[]{"instance","NED","UMD","UNMD"};
	private List<String[]> data = new ArrayList<String[]>();
	private double threshold = 0.5; //Threshold for consideration in "Match" dimensions.  
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
		return "Unconstrained Match Distance";
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
		String[] res = new String[4];
		res[0] = instanceDescription;
		Set<Match> exact = new HashSet<Match>(exactMatch.getCopyOfMatches());
		double mdDenom = 0.0;
		double nmdDenom = (double)((long)exactMatch.getCandidateOntologyTermsTotal() //TODO: fix for partial vectors MD
				*(long)exactMatch.getTargetOntologyTermsTotal()-mi.getNumMatches());
		double mdDist = 0.0;
		double nmdDist = 0.0;
		double dist = 0.0;
		for (Match m : mi.getCopyOfMatches())
		{
			double eVal = exactMatch.getMatchConfidence(m.getCandidateTerm(), m.getTargetTerm());
			if (eVal>0.0) 
				exact.remove(m);
			double val = m.getEffectiveness(); 
			if (eVal>=threshold)
			{
				mdDist+=Math.pow(val-eVal,2.0) ;
				mdDenom+=Math.pow(eVal,2.0);
				nmdDenom-=1.0;
			}
			else
			{
				nmdDist+=Math.pow(val-eVal,2.0) ;
				nmdDenom+=Math.pow(1.0-eVal,2.0);
			}
			dist +=Math.pow(val-eVal,2.0) ;
		}
		//Add distances for unmatched exact matches
		for (Match e : exact)
		{
			double eVal = e.getEffectiveness(); 
			if (eVal>=threshold)
			{
				mdDist+=Math.pow(eVal,2.0) ;
				mdDenom+=Math.pow(eVal,2.0);
				nmdDenom-=1.0;
			}
			else
			{
				nmdDist+=Math.pow(eVal,2.0) ;
				nmdDenom+=Math.pow(1-eVal,2.0);
			}
			dist +=Math.pow(eVal,2.0) ;
		}
		
		
		//finish
		double md = 1.0-(mdDenom ==0 ? 1.0 : Math.sqrt(mdDist)/Math.sqrt(mdDenom)); 
		double nmd = 1.0-(nmdDenom ==0 ? 1.0 : Math.sqrt(nmdDist)/Math.sqrt(nmdDenom));
		double d = 1.0 - (mdDenom + nmdDenom==0 ? 1.0 : 
			Math.sqrt(dist)/ 
			Math.sqrt(mdDenom+nmdDenom));
		res[1] = Double.toString(d);
		res[2] = Double.toString(md);
		res[3] = Double.toString(nmd);
		
		data.add(res);
		return true;
	}

}

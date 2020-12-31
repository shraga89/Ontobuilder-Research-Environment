/**
 * 
 */
package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.List;

import ac.technion.iem.ontobuilder.core.ontology.Term;
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
	private Term limit=null; //Used to limit evaluation to a single target term
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
		double mdDenom = 0.0;
		double nmdDenom = 0.0;
		double mdDist = 0.0;
		double nmdDist = 0.0;
		double dist = 0.0;
		
		for (Term c : exactMatch.getOriginalCandidateTerms())
			for (Term t : exactMatch.getOriginalTargetTerms())
			{
				if (limit!=null && !t.equals(limit)) //Skip unrelated matches if limited
					continue;
				double eVal = exactMatch.getMatchConfidence(c, t);
				double rVal = mi.getMatchConfidence(c, t);
				double eBarVal = (eVal>=threshold?0.0:1.0);
				double eBarBarVal = (1.0-eBarVal);
				mdDist+=Math.pow(eBarBarVal*rVal-eBarBarVal*eVal,2.0);
				mdDenom+=Math.pow(eBarBarVal*eVal,2.0);
				nmdDist+=Math.pow(eBarVal*eVal-eBarVal*rVal,2.0);
				nmdDenom+=Math.pow(eBarVal-eVal,2.0);
				dist +=Math.pow(rVal-eVal,2.0) ;
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

	/**
	 * @return the limit
	 */
	public Term getLimit() {
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(Term limit) {
		this.limit = limit;
	}

}

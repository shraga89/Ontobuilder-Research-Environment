/**
 * 
 */
package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.List;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Collection of K=1 Informed statistics 
 * designed to profile datasets and pairs. 
 * @author Tomer Sagi
 *
 */
public class K1Informed implements K2Statistic{

	private String[] header = {"Instance","Candidate Terms", "Target Terms", 
			"Matches", "Non-matches", "Unmatched Candidate Terms", "Unmatched Target Terms"
			, "1:n Matched Candidate Terms", "1:n Matched Target Terms"};
	private List<String[]> data = new ArrayList<String[]>();

	@Override
	public String[] getHeader() {
		return header ;
	}

	@Override
	public String getName() {
		return "K1Informed";
	}

	@Override
	public List<String[]> getData() {
		return data ;
	}

	@Override
	public boolean init(String instanceDescription, MatchInformation mi) {
		String[] res = new String[9];
		res[0] = instanceDescription;
		res[1] = Integer.toString(mi.getCandidateOntologyTermsTotal());
		res[2] = Integer.toString(mi.getTargetOntologyTermsTotal());
		res[3] = Integer.toString(mi.getNumMatches());
		res[4] = Integer.toString((mi.getCandidateOntologyTermsTotal()*mi.getTargetOntologyTermsTotal())-mi.getNumMatches());
		res[5] = Integer.toString(mi.getMismatchesCandidateOntology().size());
		res[6] = Integer.toString(mi.getMismatchesTargetOntology().size());
		int mult = 0;
		for (Term t : mi.getOriginalCandidateTerms())
		{
			ArrayList<Match> a = mi.getMatchesForTerm(t, true);
			if (a!=null && a.size()>1)
				mult++;
		}
		res[7] = Integer.toString(mult);
		mult = 0;
		for (Term t : mi.getOriginalTargetTerms())
		{
			ArrayList<Match> a = mi.getMatchesForTerm(t, false);
			if (a!=null && a.size()>1)
				mult++;
		}
		res[8] = Integer.toString(mult);
		data.add(res);
		return true;
	}

	@Override
	public boolean init(String instanceDescription, MatchInformation mi,
			MatchInformation exactMatch) {
		
	init(instanceDescription,exactMatch);
		return true;
	}

}

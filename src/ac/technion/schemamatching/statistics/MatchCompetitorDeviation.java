package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.List;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;

/**
 * Measures the match deviation from its average competitors based on the decisions 
 * made by some 2LM.
 */
public class MatchCompetitorDeviation implements K2Statistic{
	
	ArrayList<String[]> data = null;
	String[] header = null;

	@Override
	public String[] getHeader() {
		return header;
	}

	@Override
	public String getName() {
		return "Match Competitor Deviation Statistic";
	}

	@Override
	public List<String[]> getData() {
		return data;
	}

	@Override
	public boolean init(String instanceDescription, MatchInformation mi) {
		return false;
	}

	@Override
	public boolean init(String instanceDescription, MatchInformation _1LM_MI, MatchInformation _2LM_MI) {
		MatchMatrix mm = _1LM_MI.getMatrix();
		double[][] mmat = mm.getMatchMatrix();
		int rows = mm.getRowCount();
		int cols = mm.getColCount();
		List<Match> match = _2LM_MI.getCopyOfMatches();
		
		double mcd = 0;
		
		for (int i=0;i<match.size();i++){
			int rowIndex = mm.getTermIndex(mm.getTargetTerms(),match.get(i).getTargetTerm(), false);
			int colIndex = mm.getTermIndex(mm.getCandidateTerms(),match.get(i).getCandidateTerm(), true);
			if (rowIndex == -1 || colIndex == -1) continue;
			double sumPair = 0;
			int numCompetitors = 0;
			
			for (int j=0;j<cols;j++){
				if (j!=colIndex){
					sumPair += mmat[rowIndex][j];
					numCompetitors++;
				}
			}
			
			for (int j=0;j<rows;j++){
					sumPair += mmat[j][colIndex];
					numCompetitors++;
			}
			
			double pivotMean = numCompetitors > 0 ? sumPair/numCompetitors : 0;
			mcd += Math.pow(mmat[rowIndex][colIndex] - pivotMean,2);
		}
	
		header = new String[]{"instance","MCD"};
		data = new ArrayList<String[]>();
		data.add(0, new String[] {instanceDescription,Double.toString(!match.isEmpty() ? Math.sqrt(mcd/match.size()) : 0)});//Haggai update 25/8/2014
		//it is critical to normalize 

		return true;
	}

}

package ac.technion.schemamatching.statistics.predictors;

import java.util.ArrayList;
import java.util.List;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.schemamatching.statistics.K2Statistic;

/**
 * Measures the match deviation from its average competitors based on the decisions 
 * made by some 2LM.
 */
public class MCDAPredictor implements K2Statistic{
	
	ArrayList<String[]> data = null;
	String[] header = null;

	@Override
	public String[] getHeader() {
		return header;
	}

	@Override
	public String getName() {
		return "Match Competitor Deviation Attribute Predictor";
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
			for (int rowindex =0; rowindex < rows; rowindex++)
				{
				int colIndex = mm.getTermIndex(mm.getCandidateTerms(),match.get(i).getCandidateTerm(), true);
				double sumPair = 0;
				int numCompetitors = cols;
				if (colIndex==-1) continue;
				for (int j=0;j<cols;j++){
					sumPair += mmat[rowindex][j];
					}
				double pivotMean = numCompetitors > 0 ? sumPair/numCompetitors : 0;
				mcd += Math.pow(mmat[rowindex][colIndex] - pivotMean,2);
				}
					
		}
		
		header = new String[]{"instance","MCDAPredictor"};
		data = new ArrayList<String[]>();
		data.add(0, new String[] {instanceDescription,Double.toString(Math.sqrt(mcd))});

		return true;	

	}

}

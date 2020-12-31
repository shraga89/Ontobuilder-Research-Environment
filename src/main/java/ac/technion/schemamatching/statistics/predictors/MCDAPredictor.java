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
		int cols = mm.getColCount();
		int rows = mm.getRowCount();
		List<Match> match = _2LM_MI.getCopyOfMatches();
		header = new String[]{"instance","MCDAPredictor"};
		data = new ArrayList<String[]>();
		double mcd = 0;
		for (int i=0;i<match.size();i++){
				{
				int rowIndex = mm.getTermIndex(mm.getTargetTerms(),match.get(i).getTargetTerm(), false);
				int colIndex = mm.getTermIndex(mm.getCandidateTerms(),match.get(i).getCandidateTerm(), true);
				if (rowIndex == -1 || colIndex == -1) continue;
				//Row Attribute Predictions
				double sumPair = 0;
				int numCompetitors = cols;
				for (int j=0;j<cols;j++){
					sumPair += mmat[rowIndex][j];
					}
				double pivotMean = numCompetitors > 0 ? sumPair/numCompetitors : 0;
				mcd = Math.pow(mmat[rowIndex][colIndex] - pivotMean,2);
				data.add(0, new String[] {instanceDescription + ",Target," + match.get(i).getTargetTerm().getId()
						,Double.toString(!match.isEmpty() ? Math.sqrt(mcd) : 0)});
				mcd=0;
				//Col Attribute Predictors
				sumPair = 0;
				numCompetitors = rows;
				for (int j=0;j<rows;j++){
					sumPair += mmat[j][colIndex];
					}
				pivotMean = numCompetitors > 0 ? sumPair/numCompetitors : 0;
				mcd = Math.pow(mmat[rowIndex][colIndex] - pivotMean,2);
				data.add(0, new String[] {instanceDescription + ",Candidate," + match.get(i).getCandidateTerm().getId()
						,Double.toString(!match.isEmpty() ? Math.sqrt(mcd) : 0)});				
				mcd=0;
				}
					
		}

		return true;	

	}

}

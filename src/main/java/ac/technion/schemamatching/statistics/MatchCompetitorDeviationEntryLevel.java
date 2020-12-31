package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.List;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;

/**
 * Measures the match deviation from its average competitors based on the decisions 
 * made by some 2LM. Returns an entry Level result
 */
public class MatchCompetitorDeviationEntryLevel implements K2Statistic{
	
	ArrayList<String[]> data = null;
	MatchInformation valMI; // an alternative MI based on the MCD instead of VAL 
	String[] header = null;

	@Override
	public String[] getHeader() {
		return header;
	}

	@Override
	public String getName() {
		return "Match Competitor Deviation Entry Level Predictor";
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
		header = new String[]{"instance","MCD"};
		data = new ArrayList<String[]>();
		MatchMatrix mm = _1LM_MI.getMatrix();
		double[][] mmat = mm.getMatchMatrix();
		valMI = new MatchInformation(_1LM_MI.getCandidateOntology(), _1LM_MI.getTargetOntology());
		
		int rows = mm.getRowCount();
		int cols = mm.getColCount();
		List<Match> match = _2LM_MI.getCopyOfMatches();
		
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
			double mcdVal = Math.pow(mmat[rowIndex][colIndex] - pivotMean,2);
			String[] res = new String[header.length];
			long cID = match.get(i).getCandidateTerm().getId();
			long tID = match.get(i).getTargetTerm().getId();
			res[0] = instanceDescription + "," + cID + "," + tID;
			res[1] = Double.toString(mcdVal);
			data.add(res);
			//TODO consider normalizing by match size
			
			valMI.updateMatch(match.get(i).getTargetTerm(),match.get(i).getCandidateTerm(), mcdVal);
		}
	
		
		//data.add(0, new String[] {instanceDescription,Double.toString(!match.isEmpty() ? Math.sqrt(mcd/match.size()) : 0)});//Haggai update 25/8/2014
		//it is critical to normalize in the match size
		return true;
	}

	/**
	 * @return the valMI
	 */
	public MatchInformation getValMI() {
		return valMI;
	}

}

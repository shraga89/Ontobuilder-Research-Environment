package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

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
	boolean one2one = true;
	
	public MatchCompetitorDeviation() {
		this(true);
	}
	
	public MatchCompetitorDeviation(boolean one2one) {
		this.one2one = one2one;
	}
	

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
		//if (one2one) 
			return one2oneMCD(instanceDescription,_1LM_MI, _2LM_MI);
		//else return one2manyMCD(instanceDescription,_1LM_MI, _2LM_MI);
	}
	
	
	private boolean one2oneMCD(String instanceDescription, MatchInformation _1LM_MI, MatchInformation _2LM_MI) {
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
			double numZero = 0;
			
			for (int j=0;j<cols;j++){
				//if (mmat[rowIndex][j] == 0) continue;
				if (j!=colIndex){
						sumPair += mmat[rowIndex][j];
						//if (mmat[rowIndex][j] <= 0.1) numZero++;
						numCompetitors++;
				}
			}
			
			for (int j=0;j<rows;j++){
				        //if (mmat[j][colIndex] == 0) continue;
						sumPair += mmat[j][colIndex];
						//if (mmat[j][colIndex] <= 0.1) numZero++;
						numCompetitors++;
			}
			
			double pivotMean = numCompetitors > 0 ? sumPair/numCompetitors : 0;
			//System.out.println(numCompetitors);
			//if (pivotMean <= 0.7) {
				mcd += Math.pow(mmat[rowIndex][colIndex] - pivotMean,2);
			//}else {
				//System.out.println("high pivot at ("+rowIndex+","+colIndex+"): "+pivotMean);
			//}
		}
	
		header = new String[]{"instance","MCD"};
		data = new ArrayList<String[]>();
		data.add(0, new String[] {instanceDescription,Double.toString(!match.isEmpty() ? Math.sqrt(mcd/match.size()) : 0)});//Haggai update 25/8/2014
		//it is critical to normalize 

		return true;
	}
	
	
	
	private boolean one2manyMCD(String instanceDescription, MatchInformation _1LM_MI, MatchInformation _2LM_MI) {
		
		MatchMatrix mm = _1LM_MI.getMatrix();
		double[][] mmat = mm.getMatchMatrix();
		List<Match> match = _2LM_MI.getCopyOfMatches();
		
		HashMap<Integer,HashSet<Integer>> row2ColsMap = new HashMap<Integer,HashSet<Integer>>();
		
		double mcd = 0;
		
		for (int i=0;i<match.size();i++){
			int rowIndex = mm.getTermIndex(mm.getTargetTerms(),match.get(i).getTargetTerm(), false);
			int colIndex = mm.getTermIndex(mm.getCandidateTerms(),match.get(i).getCandidateTerm(), true);
			if (rowIndex == -1 || colIndex == -1) continue;
			HashSet<Integer> colInxs = row2ColsMap.get(rowIndex);
			if (colInxs == null) {
				colInxs = new HashSet<Integer>();
				row2ColsMap.put(rowIndex,colInxs);
			}
			colInxs.add(colIndex);	
		}

		HashMap<Integer,Double> row2SumMap = new HashMap<Integer,Double>();
		
		for (Entry<Integer,HashSet<Integer>> target : row2ColsMap.entrySet()) {
			//if (target.getValue().isEmpty()) continue;
			double sum = 0;
			for (Integer cand : target.getValue()) {
				sum += mmat[target.getKey()][cand];
			}
			row2SumMap.put(target.getKey(), sum);	
		}
		
		for (Entry<Integer,Double> target : row2SumMap.entrySet()) {
			Set<Integer> competitors = getCompetitorTargets(target.getKey(),row2ColsMap,mmat);
			double targetVal = row2SumMap.get(target.getKey());
			double avg = targetVal;
			for (Integer competitor : competitors) {
				avg += row2SumMap.get(competitor);
			}
			avg /= (1 + competitors.size());
			mcd += Math.pow(targetVal - avg, 2);
		}
		
		
        mcd = Math.sqrt(mcd/row2ColsMap.size());
        //System.out.println(mcd);
		header = new String[]{"instance","MCD"};
		data = new ArrayList<String[]>();
		data.add(0, new String[] {instanceDescription,Double.toString(!match.isEmpty() ?  mcd : 0)});
		//it is critical to normalize 

		return true;
	}
	
	private Set<Integer> getCompetitorTargets(int target, HashMap<Integer,HashSet<Integer>> row2ColsMap, double[][] mat){
		HashSet<Integer> cands = row2ColsMap.get(target);
		HashSet<Integer> competitors = new HashSet<Integer>();
		for (Entry<Integer,HashSet<Integer>> check : row2ColsMap.entrySet()) {
			if (check.getKey() == target) continue;
			for (Integer cand : cands) {
				if (mat[target][cand] > 0) {
					competitors.add(check.getKey());
					break;
				}
			}
			
		}
		return competitors;
	}

}

package ac.technion.schemamatching.statistics;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Assumes matrices are of the same size and calculates their product Normed by L2
 * @author Tomer Sagi
 *
 */
public class L2similarity implements K2Statistic {
	private ArrayList<String[]> data;
	private String[] header;
	
	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "L2Similarity With Golden Mapping";
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public boolean init(String instanceDescription, MatchInformation mi) {
		return false; //Golden statistics don't implement this method
	}

	public boolean init(String instanceDescription, MatchInformation mi, MatchInformation exactMatch) {
		data = new ArrayList<String[]>();
		header = new String[]{"instance","L2Similarity"};
		double[][] m = mi.getMatrix().getMatchMatrix();
		double[][] e = exactMatch.getMatrix().getMatchMatrix();
		double lengthOfM = mi.getOverallMatchConfidence()*mi.getTotalMatches();
		double lengthOfE = exactMatch.getOverallMatchConfidence()*mi.getTotalMatches();
		double sumProduct = 0.0;
		for (int r=0;r<e.length;r++)
			for (int c=0;c<e[r].length;c++)
				sumProduct+=m[r][c]*e[r][c];
		Double res = (lengthOfE*lengthOfM==0?0:sumProduct/(lengthOfE*lengthOfM));
		data.add(new String[] {instanceDescription, res.toString()});
		return true;
	}

}

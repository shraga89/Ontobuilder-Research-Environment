package ac.technion.schemamatching.statistics;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Assumes matrices are of the same size and calculates their distance Normed by L2
 * @author Tomer Sagi
 *
 */
public class L2distance implements K2Statistic {
	private ArrayList<String[]> data;
	private String[] header;
	
	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "L2Distance";
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public boolean init(String instanceDescription, MatchInformation mi) {
		return false; //Golden statistics don't implement this method
	}

	public boolean init(String instanceDescription, MatchInformation mi, MatchInformation exactMatch) {
		data = new ArrayList<String[]>();
		header = new String[]{"instance","L2Distance"};
		double[][] m = mi.getMatrix().getMatchMatrix();
		double[][] e = exactMatch.getMatrix().getMatchMatrix();
		assert(m.length == e.length);
		double lengthOfM = mi.getOverallMatchConfidence()*mi.getTotalMatches();
		double lengthOfE = exactMatch.getOverallMatchConfidence()*mi.getTotalMatches();
		double sumDistance = 0.0;
		for (int r=0;r<e.length;r++)
			for (int c=0;c<e[r].length;c++)
				sumDistance+=Math.pow(m[r][c]-e[r][c],2.0);
		Double res = (lengthOfE*lengthOfM==0?0:Math.sqrt(sumDistance));
		data.add(new String[] {instanceDescription, res.toString()});
		return true;
	}

}

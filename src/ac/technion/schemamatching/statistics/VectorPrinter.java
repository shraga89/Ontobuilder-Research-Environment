/**
 * 
 */
package ac.technion.schemamatching.statistics;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * @author Tomer Sagi
 *
 */
public class VectorPrinter implements GoldenStatistic{

	private ArrayList<String[]> data;
	private String[] header;

	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "VectorPrinter";
	}

	public ArrayList<String[]> getData() {
		return data;
	}

	public boolean init(String instanceDescription, MatchInformation mi) {
		//Golden Statistics don't implement this
		return false;
	}

	public boolean init(String instanceDescription, MatchInformation mi,
			MatchInformation exactMatch) {
		MatchMatrix exactM = exactMatch.getMatrix();
		int vLen = exactM.getRowCount()*exactM.getColCount();
		
		//Create Header
		header = new String[vLen+1];
		header[vLen] = "instance";
		
		//Create Data Row
		data = new ArrayList<String[]>();
		String[] v = new String[vLen+1];
		if (mi.getMatchMatrix().length<=exactMatch.getMatchMatrix().length)
			try {
				mi = ConversionUtils.expandMatrix(mi,exactMatch);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		//Fill header and data
		int k=0;
		for (int i = 0;i<exactM.getRowCount();i++)
			for (int j = 0;j<exactM.getColCount();j++)
				{
					v[k] = Double.toString(mi.getMatrix().getMatchConfidenceAt(i,j));
					header[k++] = "R" + Integer.toString(i) + "C" + Integer.toString(j);
					
				}
		
		
		v[vLen] = instanceDescription;
		data.add(v);
		return true;
	}

}

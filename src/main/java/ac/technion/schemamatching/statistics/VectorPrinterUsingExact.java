/**
 * 
 */
package ac.technion.schemamatching.statistics;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * @author Tomer Sagi
 *
 */
public class VectorPrinterUsingExact implements K2Statistic{

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
		//int vLen = exactM.getRowCount()*exactM.getColCount();
		int vLen = exactM.getColCount();
		
		//Create Header
		header = new String[vLen+3];
		header[0] = "instance";
		
		//Create Data list
		data = new ArrayList<String[]>();
		
		if (mi.getMatchMatrix().length<=exactMatch.getMatchMatrix().length)
			try {
				mi = ConversionUtils.expandMatrix(mi,exactMatch);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		//Fill header : [instanceName, TargetTerm, CandidateTerm1.provenance,...,CandidateTermN.provenance] 
		
		for (Term c : exactM.getCandidateTerms())
		{
			header[1] = "TargetTermID";
			header[2] = "TargetTermProvenance";
			int tID = exactM.getTermIndex(exactM.getCandidateTerms(), c, true);
			header[tID+3] = c.getProvenance();
		}
		
		//Fill data
		for (Term t : exactM.getTargetTerms())
		{
		String[] v = new String[vLen+3];
		v[0] = instanceDescription;
		v[1] = "'" + Long.toString(t.getId()) + "'";
		v[2] = t.getProvenance();
			for (Term c : exactM.getCandidateTerms())
				{
					v[3+exactM.getTermIndex(exactM.getCandidateTerms(), c, true)] = Double.toString(mi.getMatrix().getMatchConfidence(c,t));
				}
			data.add(v);
		}
		
		return true;
	}

}

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
 * Calculates TruePositive (TP), TrueNegative(TN)
 * FalsePositive (FN), FalseNegative (FN)
 * for each matrix entry
 */
public class EntryGolden implements K2Statistic{

	private ArrayList<String[]> data;
	private String[] header;

	public String[] getHeader() {
		return header;
	}

	public String getName() {
		return "EntryGolden";
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

		//Create Header
		header = new String[] {"instance","TP","TN","FP","FN","Res"};
		
		//Create Data list
		data = new ArrayList<String[]>();
		
		if (mi.getMatchMatrix().length<=exactMatch.getMatchMatrix().length)
			try {
				mi = ConversionUtils.expandMatrix(mi,exactMatch);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		//Fill data
		for (Term t : exactM.getTargetTerms())
		{
			for (Term c : exactM.getCandidateTerms())
			{
				boolean m = (mi.getMatchConfidence(c, t)>0);
				boolean e = (exactM.getMatchConfidence(c, t)>0);
				String v[] = new String[]{instanceDescription + "," + t.getId()+ "," + c.getId()
						,(new Integer((m==e&&m==true?1:0))).toString()
						,(new Integer((m==e&&m==false?1:0))).toString()
						,(new Integer((m!=e&&m==true?1:0))).toString()
						,(new Integer((m!=e&&m==false?1:0))).toString()
						,new String((m==e&&m==true?"TP":(m==e&&m==false?"TN":(m!=e&&m==true?"FP":"FN"))))};
				data.add(v);
			}
		}
		return true;
	}

}

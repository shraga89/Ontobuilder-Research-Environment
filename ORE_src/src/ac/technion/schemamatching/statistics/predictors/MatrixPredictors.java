/**
 * 
 */
package ac.technion.schemamatching.statistics.predictors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.statistics.all.Statistic;

/**
 * Calculates various predictions on a similarity matrix
 * with no golden mapping
 * @author Tomer Sagi
 *
 */
public class MatrixPredictors implements Statistic {

	HashMap<String,Predictor> predictors = new HashMap<String,Predictor>();
	private static String name = "Matrix Predictors";
	private String[] header;
	private ArrayList<String[]> data = new ArrayList<String[]>();
	private HashSet<Term> stopTerms  = new HashSet<Term>();
	
	public MatrixPredictors()
	{
		predictors.put("BMPredictor", new BMPredictor());
		predictors.put("BMMPredictor", new BMMPredictor());
		predictors.put("LMMPredictor", new LMMPredictor());
		predictors.put("STDEVPredictor", new STDEVPredictor());
		predictors.put("MaxPredictor", new MaxPredictor());
		predictors.put("AvgConfPredictor", new AvgPredictor());
		header = new String[predictors.size()+2];
		predictors.keySet().toArray(header);
		header[predictors.size()] = "Dominants";
		header[predictors.size()+1] = "InstanceDesc";
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.Statistic#getHeader()
	 */
	public String[] getHeader() {
		return header;
	}
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.Statistic#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.Statistic#getData()
	 */
	public ArrayList<String[]> getData() {
		return data;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.Statistic#init(com.modica.ontology.match.MatchInformation)
	 */
	public boolean init(String instanceDesc, MatchInformation mi) {
		// TODO Find a way to do this efficiently for sparse matrices
		HashMap<Term,HashSet<Term>> toVisit = new HashMap<Term,HashSet<Term>>();
		for (Match m : mi.getCopyOfMatches())
		{
			Term t = m.getTargetTerm();
			if (stopTerms.contains(t)) continue; //Skip target terms in stop list
			HashSet<Term> candTerms = (toVisit.containsKey(t)?
					toVisit.get(t):new HashSet<Term>());
			candTerms.add(m.getCandidateTerm());
		}//TODO limit predictor evaluation to toVisit
		double[][] mm = mi.getMatchMatrix();
		if (mm == null || mm.length == 0) return false;
		
		for (Predictor p : predictors.values())
			p.init(mm.length,mm[0].length);
		for (int row =0; row < mm.length; row++)
		{
			for (Predictor p : predictors.values())
				p.newRow();
			for (int col = 0; col < mm[0].length; col++)
			{
				for (Predictor p : predictors.values())
					p.visitColumn(mm[row][col]);
			}
		}		
		// Calculate results
		// L2 Similarity = sum(Xij*Yij)/(sqrt(sum(Xij^2))*sqrt(sum(Xij^2)))

		String[] res = new String[predictors.size()+2];
		int i=0;
		for (Predictor p : predictors.values())
			res[i++]=Double.toString(p.getRes());
			
		res [predictors.size()] = calcHarmony(mm).toString();
		res[predictors.size()+1] = instanceDesc;
		data.add(res);
		return true;
	}
	
	/**
	 * Calculates the "Harmony" property for similarity matrices. 
	 * assuming 1:1 using no. of dominant matches / max(no. of attributes in schema a, no. attributes in schema b)
	 * @param mm
	 * @return
	 */
	Double calcHarmony(double[][] mm)
	{
		int rows = mm.length;
		int cols = mm[0].length;
		//For each row get max value and column it appears in
		int maxColumnInRow[] = new int[rows];
		double maxRowVal[]=new double[rows];
		//For each column get max value and row it appears in
		int maxRowInColumn[] = new int[cols];
		double maxColVal[]=new double[cols];
		for (int i= 0;i<rows;i++)
		{
			
			for (int j=0;j<cols;j++)
			{
				if (maxRowVal[i]<mm[i][j])
				{
					maxColumnInRow[i]=j;
					maxRowVal[i]=mm[i][j];
				}
				
				if (maxColVal[j]<mm[i][j])
				{
					maxRowInColumn[j]=i;
					maxColVal[j]=mm[i][j];
				}
			}
			
		}
		
		//for each row i: if the max column's max row equals i then +1.0
		Double res = 0.0;
		for (int i=0;i<rows;i++)
		{
			if (maxRowVal[i]==maxColVal[maxColumnInRow[i]] && maxRowInColumn[maxColumnInRow[i]]==i)
				res+=1.0;
		}
		
		double denom = Math.max((double)rows,(double)cols);
		return res/denom;
	}

	/**
	 * @return the stopTerms
	 */
	public HashSet<Term> getStopTerms() {
		return stopTerms;
	}

	/**
	 * @param stopTerms the stopTerms to set
	 */
	public void setStopTerms(HashSet<Term> stopTerms) {
		this.stopTerms = stopTerms;
	}

}

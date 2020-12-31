/**
 * 
 */
package ac.technion.schemamatching.util;

import java.util.ArrayList;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Provides methods for manipulating similarity vectors
 * @author Tomer Sagi
 *
 */
public class SimilarityVectorUtils {
	
	public static Double[] makeArray(MatchInformation mi)
	{
		int rows = mi.getMatrix().getRowCount();
		int cols = mi.getMatrix().getColCount();
		Double[] res = new Double[rows*cols];
		for (int i = 0; i<rows; i++)
			for (int j=0;j<cols; j++)
			{
				//if (mi.getMatchMatrix()[i][j]>0.1) 
					res[i*cols+j] = mi.getMatchMatrix()[i][j];
			}
		return res;
	}
	
	/**
	 * Makes an arraylist of arrays each representing a matrix row
	 * @param mi
	 * @return
	 */
	public static ArrayList<Double[]> makeRowArrayList(MatchInformation mi)
	{
		ArrayList<Double[]> res = new ArrayList<Double[]>();
		int rows = mi.getMatrix().getRowCount();
		int cols = mi.getMatrix().getColCount();
		for (int i = 0; i<rows; i++)
		{
			Double[] rowArray = new Double[cols];
			for (int j=0;j<cols; j++)
			{
				//if (mi.getMatchMatrix()[i][j]>0.1) 
				rowArray[j] = mi.getMatchMatrix()[i][j];
			}
			res.add(rowArray);
		}
		return res;
	}
	
	/**
	 * Makes an arraylist of arrays each representing a matrix Column
	 * @param mi
	 * @return
	 */
	public static ArrayList<Double[]> makeColumnArrayList(MatchInformation mi)
	{
		ArrayList<Double[]> res = new ArrayList<Double[]>();
		int rows = mi.getMatrix().getRowCount();
		int cols = mi.getMatrix().getColCount();
		for (int i = 0; i<cols; i++)
		{
			Double[] colArray = new Double[rows];
			for (int j=0;j<rows; j++)
			{
				//if (mi.getMatchMatrix()[i][j]>0.1) 
				colArray[j] = mi.getMatchMatrix()[j][i];
			}
			res.add(colArray);
		}
		return res;
	}
	
	
	/**
	 * N implementation of dot product
	 * @param vRes
	 * @param vExact
	 * @return
	 */
	public static double calcDotProduct(Double[] vRes,Double[] vExact)
	{
		assert(vRes.length==vExact.length);
		double res = 0;
		for (int i = 0 ; i<vRes.length; i++ )
			res+= vRes[i]*vExact[i];
		return res;
	}
	
	/**
	 * Calculates the L1 Normed length of the supplied vector
	 * @param vRes
	 * @return
	 */
	public static double calcL1Length(Double[] vRes)
	{
		double res = 0;
		for (int i = 0 ; i<vRes.length; i++ )
			res+= vRes[i];
		return Math.sqrt(res);
	}
	
	/**
	 * Calculates the L2 Normed length of the supplied vector
	 * @param v1
	 * @return
	 */
	public static double calcL2Length(double[] v1)
	{
		double res = 0;
		for (int i = 0 ; i<v1.length; i++ )
			res+= Math.pow(v1[i],2);
		assert(res>=0);
		return Math.sqrt(res);
	}

}

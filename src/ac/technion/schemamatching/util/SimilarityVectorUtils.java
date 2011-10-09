/**
 * 
 */
package ac.technion.schemamatching.util;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Provides methods for manipulating similarity vectors
 * @author Tomer Sagi
 *
 */
public class SimilarityVectorUtils {
	
	public static double[] makeArray(MatchInformation mi)
	{
		int rows = mi.getMatrix().getRowCount();
		int cols = mi.getMatrix().getColCount();
		double[] res = new double[rows*cols];
		for (int i = 0; i<rows; i++)
			for (int j=0;j<cols; j++)
			{
				//if (mi.getMatchMatrix()[i][j]>0.1) 
					res[i*cols+j] = mi.getMatchMatrix()[i][j];
			}
		return res;
	}
	
	/**
	 * N implementation of dot product
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double calcDotProduct(double[] v1,double[] v2)
	{
		assert(v1.length==v2.length);
		double res = 0;
		for (int i = 0 ; i<v1.length; i++ )
			res+= v1[i]*v2[i];
		return res;
	}
	
	/**
	 * Calculates the L1 Normed length of the supplied vector
	 * @param v1
	 * @return
	 */
	public static double calcL1Length(double[] v1)
	{
		double res = 0;
		for (int i = 0 ; i<v1.length; i++ )
			res+= v1[i];
		return res;
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

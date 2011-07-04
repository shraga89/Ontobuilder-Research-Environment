/**
 * 
 */
package ac.technion.schemamatching.statistics;

import java.util.ArrayList;


import com.modica.ontology.match.MatchInformation;

/**
 * Calculates various predictions on a similarity matrix
 * with no golden mapping
 * @author tomer_s
 *
 */
public class MatrixPredictors implements Statistic {

	private static String[] header = {"SPID","SMID","L1DistBinary","L2DistBinary","L2SimilarityBinary","L1Dist1:1","L2Dist1:1","L2Similarity1:1"};
	private static String name = "Matrix Predictors";
	private ArrayList<String[]> data = new ArrayList<String[]>();
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
		//Fill comparison matrices
		String[] res = new String[7];
		double[][] mm = mi.getMatchMatrix();
		if (mm == null) return false;
		double[][] closest1to1 = new double[mm.length][mm[0].length];
		double sumL1distBin=0;
		double sumL2distBin=0;
		double sumL2ResSize=0;
		double sumL2BinSize=0;
		double sumL21to1Size=0;
		double sumL1dist1to1=0;
		double sumL2dist1to1=0;
		double sumProductBin=0;
		double sumProduct1to1=0;
		for (int row =0; row < mm.length; row++)
			for (int col = 0; col < mm[0].length; col++)
			{
				double closestBinary = (mm[row][col]<0.5?0.0:1.0);
				closest1to1[row][col] = (mm[row][col]<0.5?0.0:1.0); //TODO How do we find the closest 1:1?
				sumL1distBin+= Math.abs(closestBinary - mm[row][col]);
				sumL2distBin+= Math.pow(closestBinary - mm[row][col],2);
				sumL1dist1to1+= Math.abs(closest1to1[row][col] - mm[row][col]);
				sumL2dist1to1+= Math.pow(closest1to1[row][col] - mm[row][col],2);
				sumL2ResSize+= Math.pow(mm[row][col],2);
				sumL2BinSize+= Math.pow(closestBinary,2);
				sumL21to1Size+= Math.pow(closest1to1[row][col],2);
				sumProductBin += closestBinary*mm[row][col];
				sumProduct1to1 += closest1to1[row][col]*mm[row][col];
			}
				
		// Calculate results
		// L1 Distance = sum(abs(Xij-Yij))
		res[0] = instanceDesc;
		res[1] = Double.toString(sumL1distBin);
		res[2] = Double.toString(sumL1dist1to1);
		// L2 Distance = sqrt(sum((Xij-Yij)^2))
		res[3] = Double.toString(Math.sqrt(sumL2distBin));
		res[4] = Double.toString(Math.sqrt(sumL2dist1to1));
		// L2 Similarity = sum(Xij*Yij)/(sqrt(sum(Xij^2))*sqrt(sum(Xij^2)))
		res[5] = Double.toString(sumProductBin/(Math.sqrt(sumL2ResSize)*Math.sqrt(sumL2BinSize)));
		res[6] = Double.toString(sumProduct1to1/(Math.sqrt(sumL2ResSize)*Math.sqrt(sumL21to1Size)));
		data.add(res);
		return true;
	}

}

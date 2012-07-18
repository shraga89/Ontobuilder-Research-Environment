package ac.technion.schemamatching.statistics;


import java.util.ArrayList;

import ac.technion.schemamatching.statistics.predictors.Predictor;

/**
 * Calculates the population standard deviation over the vector
 * @author Tomer Sagi
 *
 */
public class STDEVPredictor implements Predictor{

	private double sum;
	private double zeros;
	private ArrayList<Double> nonZeros;
	private long len;
	
	public String getName() 
	{
		return "STDevPredictor";
	}

	public void newRow() 
	{
		
	}

	public void visitColumn(double val) {
		sum+=val;
		len++;
		if (val>0)
			nonZeros.add(new Double(val));
		else
			zeros++;
	}

	public void init(int rows, int cols) 
	{
		nonZeros = new ArrayList<Double>();
		sum =0;
		zeros = 0;
		len = 0;
	}

	public double getRes() 
	{
		double res = 0.0;
		double avg = sum/len;
		for (Double nonZ : nonZeros)
		{
			res+=Math.pow(nonZ-avg, 2);
		}
		res+=zeros*Math.pow(avg, 2);
		return Math.sqrt(res/len);
	}

}

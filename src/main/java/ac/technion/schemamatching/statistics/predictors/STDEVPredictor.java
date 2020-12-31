package ac.technion.schemamatching.statistics.predictors;


import java.util.ArrayList;


/**
 * Calculates the population standard deviation over the vector 
 * each row at a time and returns the average
 * @author Tomer Sagi
 *
 */
public class STDEVPredictor implements Predictor{

	private double sum;
	private long len;
	private double rowSum = 0.0;
	private double rowLen = 0.0;
	private double zeros;
	private ArrayList<Double> nonZeros;
	
	
	public String getName() 
	{
		return "STDevPredictor";
	}

	public void newRow() 
	{
		if (rowLen>0)
		{
			double res = 0.0;
			double avg = rowSum/rowLen;
			for (Double nonZ : nonZeros)
			{
				res+=Math.pow(nonZ-avg, 2);
			}
			res+=zeros*Math.pow(avg, 2);
			sum+=Math.sqrt(res/rowLen);
			len++;
		}
		rowSum=0.0;
		rowLen=0.0;
		nonZeros = new ArrayList<Double>();
		zeros= 0;
		
	}

	public void visitColumn(double val) {
		rowSum+=val;
		rowLen++;
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
		newRow();
//		double res = 0.0;
//		double avg = sum/len;
//		for (Double nonZ : nonZeros)
//		{
//			res+=Math.pow(nonZ-avg, 2);
//		}
//		res+=zeros*Math.pow(avg, 2);
//		return Math.sqrt(res/len);
		if (len==0)
			return 0;
		return sum/len;
	}

}

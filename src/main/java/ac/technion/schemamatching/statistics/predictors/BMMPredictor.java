package ac.technion.schemamatching.statistics.predictors;


import java.util.TreeSet;

/**
 * Implementation of the @link{Predictor} interface
 * Returns the cosine similarity between the result vector and
 * the closest binary vector having at least k  entries equaling 1
 * Finds this vector by setting all entries over 0.5 to 1 and then
 * completing to k entries by taking the top entries below 0.5 and
 * rounding them to 1. 
 * @author Tomer Sagi
 *
 */
public class BMMPredictor implements Predictor{

	private double prod;
	private double resLen;
	private double refLen;
	private int overPoint5;
	int k;
	private TreeSet<Double> mh = new TreeSet<Double>();

	public String getName() 
	{
		return "BMMPredictor";
	}

	public void newRow() 
	{
	
	}

	public void visitColumn(double val) {
		double ref = 0.0;
		if (val>=0.5)
		{
			ref = 1.0;
			overPoint5+=1;
		}
		else
			mh.add(val);
		
		prod += val*ref;
		//System.out.println(val+","+ref+","+prod+","+resLen+","+refLen);
		resLen += Math.pow(val,2);
		refLen += ref;
	}

	public void init(int rows, int cols) {
		k = Math.max(rows, cols);
		overPoint5 = 0;
		prod = 0.0;
		resLen = 0.0;
		refLen = 0.0;
	}

	public double getRes() {
		//check num over 0.5
		if (overPoint5<k)
		{
			for (int i=overPoint5;i<=k;i++)
			{
				if (mh.isEmpty()) break;
				double val = mh.pollLast();
				double ref = 1.0;
				prod += val*ref;
				refLen += ref;
				//System.out.println(val+","+ref+","+prod+","+resLen+","+refLen);
			}
		}
		double res = (Math.sqrt(resLen)*Math.sqrt(refLen)==0?0.0:prod/(Math.sqrt(resLen)*Math.sqrt(refLen)));
		return res;
	}

}

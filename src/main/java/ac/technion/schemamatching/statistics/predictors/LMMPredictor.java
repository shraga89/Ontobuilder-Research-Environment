package ac.technion.schemamatching.statistics.predictors;


import java.util.TreeSet;

public class LMMPredictor implements Predictor{

	private double prod;
	private double resLen;
	private double refLen;
	private int overPoint5;
	private boolean firstRow = true;
	private TreeSet<Double> mh = new TreeSet<Double>();

	public String getName() 
	{
		return "LMMPredictor";
	}

	public void newRow() 
	{
		//Check if last row had a value over 0.5, if not, add the largest to the result
		if (overPoint5==0 && !firstRow)
		{
			double val = mh.last();
			double ref = 1.0;
			prod += val*ref;
			refLen += ref;
		}
		
		//reset for new row
		if (!firstRow)
		{mh.clear();
		overPoint5 = 0;}
		else
			firstRow = false;
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
		resLen += Math.pow(val,2);
		refLen += ref;
	}

	public void init(int rows, int cols) {
		overPoint5 = 0;
		prod = 0.0;
		resLen = 0.0;
		refLen = 0.0;
	}

	public double getRes() {
		//check last row
		if (overPoint5==0)
		{
			double val = mh.last();
			double ref = 1.0;
			prod += val*ref;
			refLen += ref;
		}
		double res = (Math.sqrt(resLen)*Math.sqrt(refLen)==0?0.0:prod/(Math.sqrt(resLen)*Math.sqrt(refLen)));
		return res;
	}

}

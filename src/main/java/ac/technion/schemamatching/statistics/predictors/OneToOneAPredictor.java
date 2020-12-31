/**
 * 
 */
package ac.technion.schemamatching.statistics.predictors;

/**
 * @author Tomer Sagi
 * Calculates L2 similarity to the closest Binary vactor having 
 * for each row exactly one atribute set to one (intended for single attribute use)
 * 
 */
public class OneToOneAPredictor implements Predictor {
	private double prod;
	private double resLen;
	private double refLen;
	private double max; 
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#getName()
	 */
	public String getName() {
		return "OneToOneAPredictor";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#newRow()
	 */
	public void newRow() {
		
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#visitColumn(double)
	 */
	public void visitColumn(double val) {
		resLen += Math.pow(val,2);
		max = Math.max(val, max);
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#init(int, int)
	 */
	public void init(int rows, int cols) 
	{
		max = 0;
		resLen = 0;
		refLen = 0;
		prod = 0;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#getRes()
	 */
	public double getRes() {
		refLen = 1.0;
		prod = max;
		double res = (Math.sqrt(resLen)*Math.sqrt(refLen)==0?0.0:prod/(Math.sqrt(resLen)*Math.sqrt(refLen)));
		return res;
	}

}

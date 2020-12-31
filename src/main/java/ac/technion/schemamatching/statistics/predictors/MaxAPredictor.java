package ac.technion.schemamatching.statistics.predictors;

/**
 * simple Predictor which calculates max of vector and returns it
 * @author Tomer Sagi
 *
 */
public class MaxAPredictor implements Predictor {

	private double max = 0.0; 
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#getName()
	 */
	public String getName() {
		return "MaxAPredictor";
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
		max = Math.max(val, max);
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#init(int, int)
	 */
	public void init(int rows, int cols) 
	{
		max = 0;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#getRes()
	 */
	public double getRes() {
		return max;
	}

}
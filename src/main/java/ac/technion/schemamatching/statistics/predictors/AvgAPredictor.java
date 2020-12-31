package ac.technion.schemamatching.statistics.predictors;


public class AvgAPredictor implements Predictor {
	private double sum = 0.0;
	private double len = 0.0;
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#getName()
	 */
	public String getName() {
		return "AvgAPredictor";
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
		sum+=val;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#init(int, int)
	 */
	public void init(int rows, int cols) {
		len = (double)rows * cols;
		sum = 0.0;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#getRes()
	 */
	public double getRes() {
		return (len==0?0:sum/len);
	}

}

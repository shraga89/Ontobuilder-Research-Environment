package ac.technion.schemamatching.statistics.predictors;


/**
 * Predictor which calculates avg confidence for non-zero values
 * @author Tomer Sagi
 *
 */
public class AvgPredictor implements Predictor {

	private double sum = 0.0;
	private int nonZero = 0;
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#getName()
	 */
	public String getName() {
		return "AvgConfPredictor";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#newRow()
	 */
	public void newRow(){}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#visitColumn(double)
	 */
	public void visitColumn(double val) {
		if (val > 0.0 ){
			nonZero++;
			sum+=val;
			}
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#init(int, int)
	 */
	public void init(int rows, int cols){}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#getRes()
	 */
	public double getRes() {
		return (nonZero==0?0.0:sum/(double)nonZero);
	}

}
/**
 * 
 */
package ac.technion.schemamatching.statistics.predictors;

/**
 * @author Tomer Sagi
 * <p>Interface for classes that implement a predictor evaluation function</p>
 *
 */
public interface Predictor 
{
	/**
	 * Predictor Name
	 * @return String describing the predictor
	 */
	public String getName();
	
	/**
	 * Indicator used by the dataStructure to inform the predictor of a new matrix row
	 */
	public void newRow();
	
	/**
	 * Visitor used by the dataStructure to give the predictor the next value in the matrix
	 * @param val
	 */
	public void visitColumn(double val);
	
	/**
	 * Initializer
	 * @param rows
	 * @param cols
	 */
	public void init(int rows, int cols);
	
	/**
	 * 
	 * @return Predictor result
	 */
	public double getRes();
}

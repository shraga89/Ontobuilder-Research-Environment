package ac.technion.schemamatching.util.ce;

/**
 * Represents a single solution sample
 */
public interface CESample{
	/**
	 * Returns the sample's objective value
	 */
	public double getValue();
	/**
	 * Sets the sample's objective value
	 * @param value
	 */
	public void setValue(double value);
}

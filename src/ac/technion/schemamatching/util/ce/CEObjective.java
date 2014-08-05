package ac.technion.schemamatching.util.ce;

/**
 * Represents any optimization objective
 */
public interface CEObjective {
	/**
	 * Flags whether we wish to maximize the objective (true) 
	 * or minimize it (false)
	 * @return flag
	 */
	public boolean isMaximized();
	/**
	 * Called for evaluating the objective value of a single sample
	 * @param sample
	 * @return objective value
	 */
	public double evaluate(CESample sample);
}

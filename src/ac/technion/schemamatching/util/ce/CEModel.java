package ac.technion.schemamatching.util.ce;

import java.util.concurrent.ForkJoinPool;


/**
 * Represents a solution space model. Solutions with the CE method are usually modeled by
 * vectors, graph paths, etc...
 */
public interface CEModel {
	/**
	 * Initiates the model to the maximum entropy
	 */
	public void maxEntropy();
	/**
	 * Called when model need to be updated due to new evidence on its solution space
	 * @param gammaT 
	 * @param sample
	 * @param objective
	 */
	public void update(double gammaT, CESample[] sample, CEObjective objective, ForkJoinPool pool);
	/**
	 * Called for generating a random solution sample from the model
	 * @return random sample
	 */
	public CESample drawRandomSample();
}

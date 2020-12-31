package ac.technion.schemamatching.experiments.holistic.network;

/**
 * Collection of functions that are used to aggregate two values
 * on a network path.
 * 
 * @author matthias weidlich
 *
 */
public class NetworkAggregationFunctions {

	public enum NetworkAggregationType {
		Multiplication
	}
	
	public static double applyAggregationFunction(double value1, double value2, NetworkAggregationType type) {
		switch (type) {
		case Multiplication:
			return value1 * value2;
		default:
			return -1;
		}
	}
	
}

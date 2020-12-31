package ac.technion.schemamatching.experiments.holistic.network;

/**
 * Functions to evaluate the link strength in a network based on the 
 * probability of the respective match.
 * 
 * @author matthias weidlich
 *
 */
public class NetworkStrengthFunctions {

	public enum NetworkStrengthType {
		Certainty, Uncertainty, Decisiveness, Indecisiveness
	}
	
	public static double applyStrengthFunction(double matchValue, NetworkStrengthType type) {
		switch (type) {
		case Certainty:
			return applyCertaintyFunction(matchValue);
		case Uncertainty:
			return applyUncertaintyFunction(matchValue);
		case Decisiveness:
			return applyDecisivenessFunction(matchValue);
		case Indecisiveness:
			return applyIndecisivenessFunction(matchValue);
		default:
			return -1;
		}
	}
	
	public static double applyCertaintyFunction(double matchValue) {
		return matchValue * matchValue;
	}
	
	public static double applyUncertaintyFunction(double matchValue) {
		return (1d - matchValue) * (1d - matchValue);
	}

	public static double applyDecisivenessFunction(double matchValue) {
		return 4d * (matchValue - 0.5) * (matchValue - 0.5);
	}
	
	public static double applyIndecisivenessFunction(double matchValue) {
		return (-4d * (matchValue - 0.5) * (matchValue - 0.5)) + 1d;
	}

}

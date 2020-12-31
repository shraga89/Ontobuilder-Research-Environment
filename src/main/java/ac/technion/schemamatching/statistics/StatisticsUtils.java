package ac.technion.schemamatching.statistics;

public class StatisticsUtils {

	/*
	 * Floating point computation is not precise in Java, so that 
	 * statistics may get slightly above 1.0 or below 0.0 even if normalized.
	 * If the deviation is less than the threshold, we set the value to the
	 * respective bound.
	 */
	public static double NORMALIZATION_ERROR_THRESHOLD = 0.0000001;

	public static double setDoubleValueInUnitBounds(double v) {
		if ((v <= 1.0) && (v >= 0.0))
			return v;
		
		if (v > 1.0)
			if (v - 1.0 < NORMALIZATION_ERROR_THRESHOLD)
				return 1.0;		
		
		if (v < 0.0)
			if (v + 1.0 < 1.0 - NORMALIZATION_ERROR_THRESHOLD)
				return 0.0;		
		
		return v;
	}
	

}

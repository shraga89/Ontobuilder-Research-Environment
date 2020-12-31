package ac.technion.schemamatching.statistics.predictors;


public class BMPredictor implements Predictor{

	private double prod;
	private double resLen;
	private double refLen;

	public String getName() {
		return "BMPredictor";
	}

	public void newRow() {
		
		
	}

	public void visitColumn(double val) {
		double ref = (val>=0.5?1.0:0.0);
		prod += val*ref;
		resLen += Math.pow(val,2);
		refLen += ref;
	}

	public void init(int rows, int cols) {
		prod = 0.0;
		resLen = 0.0;
		refLen = 0.0;
	}

	public double getRes() {
		double res = (Math.sqrt(resLen)*Math.sqrt(refLen)==0?0.0:prod/(Math.sqrt(resLen)*Math.sqrt(refLen)));
		return res;
	}

}

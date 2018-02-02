package ac.technion.schemamatching.statistics.predictors;

import java.util.Arrays;

public class NormPredictor implements Predictor{

//	PCAHandler pca;
	PCAHandlerSVD pca;
	String norm;
	/**
	 * Regular sum "informative" Eigen Values
	 * @param handler
	 */
	public NormPredictor(PCAHandlerSVD handler, String norm){
		pca = handler;
		this.norm = norm;
	}

	public String getName() {
		return "Matrix Norm Predictor";
	}

	public void newRow() {
	}

	public void visitColumn(double val) {

	}
	
	public void init(int rows, int cols) {
	}

	
	public double getRes() {
		double val = 0.0;
		val = pca.getNorm(norm);
		return val;
	}
}

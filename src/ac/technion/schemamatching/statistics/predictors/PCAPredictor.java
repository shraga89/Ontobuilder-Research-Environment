package ac.technion.schemamatching.statistics.predictors;

import java.util.Arrays;

public class PCAPredictor implements Predictor{

//	PCAHandler pca;
	PCAHandlerSVD pca;
	double[][] matchMatrix;
	int rowCount;
	int colCount;
	int eigenValue;
	
//	/**
//	 * Regular sum "informative" Eigen Values
//	 * @param handler
//	 */
//	public PCAPredictor(PCAHandler handler){
//		pca = handler;
//		eigenValue = 0;
//	}
//	/**
//	 * Specific pc
//	 * @param handler
//	 * @param eigenValueNum pc number
//	 */
//	public PCAPredictor(PCAHandler handler, int eigenValueNum){
//		pca = handler;
//		eigenValue = eigenValueNum;
//	}
	/**
	 * Regular sum "informative" Eigen Values
	 * @param handler
	 */
	public PCAPredictor(PCAHandlerSVD handler){
		pca = handler;
		eigenValue = 0;
	}
	/**
	 * Specific pc
	 * @param handler
	 * @param eigenValueNum pc number
	 */
	public PCAPredictor(PCAHandlerSVD handler, int eigenValueNum){
		pca = handler;
		eigenValue = eigenValueNum;
	}
	public String getName() {
		return "Principal Components Analysis Predictor";
	}

	public void newRow() {
		rowCount++;
		colCount = -1;
	}

	public void visitColumn(double val) {
		colCount++;
		matchMatrix[rowCount][colCount] = val; 
	}
	

	public void init(int rows, int cols) {
		matchMatrix = new double[rows+1][cols+1];
		rowCount = -1;
		colCount = -1;
	}

	
	public double getRes() {
		double val = 0.0;
		if (eigenValue == 0){
			val = pca.analyzeEigenValues();
		}
		else{
			val = pca.getEigenValue(eigenValue);
		}
		return val;
	}
}

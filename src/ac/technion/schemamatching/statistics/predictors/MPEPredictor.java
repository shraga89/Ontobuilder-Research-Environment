package ac.technion.schemamatching.statistics.predictors;

import java.util.ArrayList;
import java.util.Arrays;

public class MPEPredictor implements Predictor{

	double[] rowsEntropy;
	int rowNumber;
	double rowEntropy;
	
	public String getName() {
		return "Mean Pure Entropy Predictor";
	}

	public void newRow() {
		if (rowNumber == -1){
			rowNumber++;
			return;
		}
		rowsEntropy[rowNumber]=rowEntropy;
		rowNumber++;
		rowEntropy = 0.0;
	}

	public void visitColumn(double val) {
		double logVal = 0.0;
		if (val > 0){
			logVal = Math.log(val) / Math.log(2);
		}
		rowEntropy-= val*logVal;
	}
	

	public void init(int rows, int cols) {
		rowsEntropy = new double[rows];
		rowNumber = -1;
		rowEntropy = 0.0;
	}

	
	public double getRes() {
		double res = 0.0;
		for (double row : rowsEntropy){
			res += row;
		}
		res /= rowsEntropy.length;
		return res;
	}
}

package ac.technion.schemamatching.statistics.predictors;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class PCAHandlerSVD{
	double[][] mat;
	Matrix mReg;
	SingularValueDecomposition svd;
	double[] eigenVals;
	double[] orderedEigenVals;
	
	public PCAHandlerSVD(double[][] mat){
		this.mat = mat;
		mReg = new Matrix(mat);
		SingularValueDecomposition svd = new SingularValueDecomposition(mReg);
		eigenVals = svd.getSingularValues();
		orderedEigenVals = new double[eigenVals.length];
		int i = 0;
		for (double val : eigenVals){
			orderedEigenVals[i] = val;
			i++;
		}
	}
	
	public double getNorm(String norm){
		switch (norm){
		case "Norm1": return mReg.norm1();
		case "Norm2": return mReg.norm2();
		case "NormF": return mReg.normF();
		case "NormInf": return mReg.normInf();
		default: return 0.0;
		}
	}
	
	
	public double analyzeEigenValues(){
//		Horn's Parallel Analysis to determine number of components
		double[] montecarlo = new double[orderedEigenVals.length];
		double sumInformativeEigenValues = 0.0;
		for (int i=0; i<orderedEigenVals.length;i++){
			if (orderedEigenVals[i]>montecarlo[i]){
				sumInformativeEigenValues+=orderedEigenVals[i];
			}
		}
		return sumInformativeEigenValues;
	}

	public double getEigenValue(int numOfValue){
		return orderedEigenVals[numOfValue];
	}
	
	public double[] getAllEigenValues(){
		return orderedEigenVals;
	}
	
}

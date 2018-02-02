package ac.technion.schemamatching.statistics.predictors;

import java.util.Arrays;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import Jama.SingularValueDecomposition;
import flanagan.analysis.PCA;

public class PCAHandler {
	PCA pca;
	double[][] mat;
	public PCAHandler(double[][] mat){
		pca = new PCA();
		this.mat = mat;
		setData();
	}
	
	public void setData(){
		pca.enterScoresAsRowPerItem(mat);
	}
	
	public double analyzeEigenValues(){
//		Horn's Parallel Analysis to determine number of components
		double[] montecarlo = pca.monteCarloMeans();
		double[] eigenvalues = pca.orderedEigenValues();
		double sumInformativeEigenValues = 0.0;
		for (int i=0; i<eigenvalues.length;i++){
			if (eigenvalues[i]>montecarlo[i]){
				sumInformativeEigenValues+=eigenvalues[i];
			}
		}
		return sumInformativeEigenValues;
	}

	public double getEigenValue(int numOfValue){
		double[] values = Arrays.copyOfRange(pca.orderedEigenValues(), 0, numOfValue+1);
		return values[numOfValue];
	}
	
	public double[] getAllEigenValues(){
//		System.out.println("eigen values:");
//		System.out.println(pca.orderedEigenValues()[0]);
		double[] eigenVals = pca.orderedEigenValues();
//		for (double val : eigenVals){
//			System.out.println(val);
//		}
		return eigenVals;
	}
	
}

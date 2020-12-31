package ac.technion.schemamatching.statistics.predictors;

public class VNEPredictor implements Predictor{

//	PCAHandler pca;
	PCAHandlerSVD pca;
	double[][] matchMatrix;
	int rowCount;
	int colCount;
	int eigenValue;
	public String getName() {
		return "Von Neumann Entropy Predictor";
	}

//	public VNEPredictor(PCAHandler handler){
//		pca = handler;
//	}

	public VNEPredictor(PCAHandlerSVD handler){
		pca = handler;
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
		double[] eigenValues = pca.getAllEigenValues();
		double sumEntropy = 0.0;
		for (double eigenValue : eigenValues){
			double logEigenValue = 0.0;
			//CHANGE!!!!!!!!!!!!!!!! (>=1
			if (eigenValue>0){
				logEigenValue = Math.log(eigenValue) / Math.log(2);
			}
//			System.out.println(eigenValue +" * "+ logEigenValue);
			sumEntropy += (eigenValue)*(logEigenValue);
		}
//		System.out.println(-1*sumEntropy);
		return sumEntropy;
	}
}

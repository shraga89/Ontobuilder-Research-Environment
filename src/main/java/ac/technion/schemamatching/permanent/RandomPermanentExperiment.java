

package ac.technion.schemamatching.permanent;

import ac.technion.iem.ontobuilder.matching.utils.PermanentCalculator;


public class RandomPermanentExperiment {

	public static double[][] generateRandomMatrix(int size, double x, double y){
		double[][] matrix = new double[size][size];
		for (int i=0;i<size;i++){
			for (int j=0;j<size;j++){
				if (i == j){
					matrix[i][j] = x + Math.random()*(1-x);//~U[x,1]
				}else{
					matrix[i][j] = y*Math.random();//~U[0,y]
				}
				//System.out.print("["+matrix[i][j]+"]");
			}
			//System.out.println();
		}
		return matrix;
	}
	
	public static void main(String[] args){
		double step = 0.1;
		int size = 10;
		double[][] matrix;
		PermanentCalculator calc;//num repeats
		for (double x=0;x<=1;x+=step){
			for (double y=1;y>=0;y-=step){
				for (int k=0;k<1;k++){
					matrix = generateRandomMatrix(size, x, y);
					calc = new PermanentCalculator(size, matrix);
					System.out.println(x+"\t"+y+"\t"+calc.getPermanentValue());
				}
			}
		}
	}
}

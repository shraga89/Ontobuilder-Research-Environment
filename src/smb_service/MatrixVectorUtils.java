package smb_service;
public class MatrixVectorUtils {
	public static double[] DotProduct(double[] v1, double[] v2)
	{
		assert v1.length == v2.length;
		double[] res = new double[v1.length];
		for (int i = 0; i < v1.length; ++i)
		{
			res[i] = v1[i] * v2[i];
		}
		return res;
	}
}

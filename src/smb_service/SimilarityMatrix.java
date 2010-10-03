package smb_service;
import java.io.Serializable;
import java.util.HashMap;
import JSci.maths.matrices.DoubleMatrix;

public class SimilarityMatrix  implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7441165823900750558L;
	long candidateSchemaID;
	long targetSchemaID;
	HashMap<Long,Integer> candidateTermMap;
	HashMap<Long,Integer> targetTermMap;
	DoubleMatrix similarityM;
	int lastCandidateItem; //position of Last filled item in HashMap
	int lastTargetItem;
	
	/**
	 * Constructor of similarityMatrix, assumes not null parameters
	 * Assumes either full HashMaps or a completely empty ones
	 * @param candidateID: ID of candidate schema
	 * @param targetID: ID of target schema
	 * @param candidateTerms: HashMap<Long,Integer> mapping candidate schema Terms to their rows in the matrix
	 * @param targetTerms: HashMap<Long,Integer> mapping target schema Terms to their columns in the matrix
	 * @param inSimilarityM: double[][] similarity matrix. If filled, must correspond with HashMaps
	 */
	public SimilarityMatrix(long candidateID,long targetID,HashMap<Long,Integer> candidateTerms,HashMap<Long,Integer> targetTerms,double[][] inSimilarityM)
	{
		candidateSchemaID = candidateID;
		targetSchemaID = targetID;
		candidateTermMap = candidateTerms;
		targetTermMap = targetTerms;
		similarityM = new DoubleMatrix(inSimilarityM);
		lastCandidateItem = candidateTermMap.size()-1;
		lastTargetItem = targetTermMap.size()-1;
	}
	
	public SimilarityMatrix() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Return a double[] Array representing the requested column in the similarity Matrix
	 * @param j : Column index to return
	 * @return : double[] Array representing the requested column in the similarity Matrix
	 */
	public double[] getColumn(int j)
	{
		double[] res = new double[similarityM.rows()];
		for (int i=0;i<similarityM.rows();i++) 
			res[i] = similarityM.getElement(i,j);
		return res;	
	}
	
	/**
	 * Return a double[] Array representing the requested row in the similarity Matrix
	 * @param i : Row index to return
	 * @return : double[] Array representing the requested row in the similarity Matrix
	 */
	public double[] getRow(int i)
	{
		double[] res = new double[similarityM.columns()];
		for (int j=0;j<similarityM.columns();j++) res[j] = similarityM.getElement(i,j);
		return res;	
	}
	
	/**
	 * Sort matrix in place according to new ordering of terms
	 * @param candidateTermOrder
	 * @param targetTermOrder
	 */
	public void sortByNewMaps(HashMap<Long,Integer> candidateTermOrder,HashMap<Long,Integer> targetTermOrder )
	{
		// TODO STUB
	}
	
	/**
	 * Reduce matrix to the size of the last filed item in each row and column
	 */
	public void shrink()
	{
		DoubleMatrix newM = new DoubleMatrix(lastCandidateItem+1, lastTargetItem+1);
		for (int i=0;i<=lastCandidateItem;i++)
			for (int j=0;j<=lastTargetItem;j++)
			{
				if (j==3)
					newM.setElement(i, j,similarityM.getElement(i, j) );
				else
					newM.setElement(i, j,similarityM.getElement(i, j) );
			}
				
		similarityM = newM;
	}
}
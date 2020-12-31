package ac.technion.schemamatching.statistics.predictors;

import java.util.HashMap;

/**
 * Predictor which calculates max of rows and columns and returns  the sum(max)/count(rows + columns)
 * @author Tomer Sagi
 *
 */
public class MaxPredictor implements Predictor {

	private HashMap<Integer,Double> maxRows = new HashMap<Integer,Double>();
	private HashMap<Integer,Double> maxCols = new HashMap<Integer,Double>();
	private Integer rows = 0;
	private Integer cols = 0;
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#getName()
	 */
	public String getName() {
		return "MaxPredictor";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#newRow()
	 */
	public void newRow() 
	{
		cols = 0;
		rows++;
		maxRows.put(rows, 0.0);
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#visitColumn(double)
	 */
	public void visitColumn(double val) {
		cols++;
		if (rows==1) //First row, create column hash 
		{
			maxCols.put(cols, val);
		}
		else
		{
			maxCols.put(cols, Math.max(val,maxCols.get(cols)));
		}
		
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#init(int, int)
	 */
	public void init(int rows, int cols) 
	{
		rows = 0;
		cols = 0;
		
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#getRes()
	 */
	public double getRes() {
		Double sumMax = 0.0;
		for (Double max : maxRows.values())
			sumMax+=max;
		for (Double max : maxCols.values())
			sumMax+=max;
		return sumMax/((double)rows+cols);
	}

}
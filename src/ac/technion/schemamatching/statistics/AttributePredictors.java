/**
 * 
 */
package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.HashMap;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * Calculates various predictions on an attribute vector
 * with no golden mapping
 * @author Tomer
 *
 */
public class AttributePredictors implements Statistic {

	HashMap<String,Predictor> predictors = new HashMap<String,Predictor>();
	private static String name = "Attribute Predictors";
	private String[] header;
	private ArrayList<String[]> data = new ArrayList<String[]>();
	
	public AttributePredictors()
	{
		predictors.put("BNAPredictor", new BMPredictor());
		predictors.put("OneToOneAPredictor", new OneToOneAPredictor());
		predictors.put("STDEVPredictor", new STDEVPredictor());
		predictors.put("MaxAPredictor", new MaxAPredictor());
		predictors.put("AvgAPredictor", new AvgAPredictor());
		header = new String[predictors.size()+2];
		predictors.keySet().toArray(header);
		header[predictors.size()] = "Covariance Coefficient";
		header[predictors.size()+1] = "InstanceDesc";
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.Statistic#getHeader()
	 */
	public String[] getHeader() {
		return header;
	}
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.Statistic#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.Statistic#getData()
	 */
	public ArrayList<String[]> getData() {
		return data;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.Statistic#init(com.modica.ontology.match.MatchInformation)
	 */
	public boolean init(String instanceDesc, MatchInformation mi) {
		double[][] mm = mi.getMatchMatrix();
		if (mm == null || mm.length == 0) return false;
		//Row Attribute Predictions
		for (int row =0; row < mm.length; row++)
		{
			for (Predictor p : predictors.values())
				{p.init(1,mm[row].length);
				p.newRow();}
			for (int col = 0; col < mm[0].length; col++)
			{
				for (Predictor p : predictors.values())
					p.visitColumn(mm[row][col]);
			}
			String[] res = new String[predictors.size()+2];
			int i=0;
			for (Predictor p : predictors.values())
				res[i++]=Double.toString(p.getRes());
				
			double stdevp = predictors.get("STDEVPredictor").getRes();
			double avg = predictors.get("AvgAPredictor").getRes();
			res [predictors.size()] = Double.toString((avg==0?0.0:stdevp/avg));
			res[predictors.size()+1] = instanceDesc + "_R" + row;
			data.add(res);
		}
		for (int col =0; col < mm[0].length; col++)
		{
			for (Predictor p : predictors.values())
				{p.init(1,mm.length);
				p.newRow();}
			for (int row = 0; row < mm.length; row++)
			{
				for (Predictor p : predictors.values())
					p.visitColumn(mm[row][col]);
			}
			String[] res = new String[predictors.size()+2];
			int i=0;
			for (Predictor p : predictors.values())
				res[i++]=Double.toString(p.getRes());
				
			double stdevp = predictors.get("STDEVPredictor").getRes();
			double avg = predictors.get("AvgAPredictor").getRes();
			res [predictors.size()] = Double.toString((avg==0?0.0:stdevp/avg));
			res[predictors.size()+1] = instanceDesc + "_C" + col;
			data.add(res);
		}
		return true;
	}

}

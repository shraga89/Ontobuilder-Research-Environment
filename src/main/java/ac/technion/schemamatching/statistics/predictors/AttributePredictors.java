/**
 * 
 */
package ac.technion.schemamatching.statistics.predictors;

import java.util.ArrayList;
import java.util.HashMap;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.schemamatching.statistics.Statistic;

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
		//Make Reverse Hash to lookup termID by row / col:
		HashMap<Integer, Long> candidateIDs = makeReverseHash(mi, true);
		HashMap<Integer, Long> targetIDs = makeReverseHash(mi, false);
		HashMap<Integer, String> candProv = makeProvenanceHash(mi, true);
		HashMap<Integer, String> targProv = makeProvenanceHash(mi, false);
		
		//Make Reverse Hash to lookup termProvnance by row / col:
		
				
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
			res[predictors.size()+1] = instanceDesc + ",Target," + targetIDs.get(row) + "," + targProv.get(row);
			data.add(res);
		}
		//Col Attribute Predictors
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
			res[predictors.size()+1] = instanceDesc + ",Candidate," + candidateIDs.get(col)+ "," + candProv.get(col);
			data.add(res);
		}
		return true;
	}

	/**
	 * Creates an index->ID hash
	 * @param mi
	 * @param candidateIDs
	 * @return
	 */
	private HashMap<Integer, Long> makeReverseHash(MatchInformation mi,boolean candidate) {
		HashMap<Integer, Long> indexIDs = new HashMap<Integer, Long>();
		MatchMatrix m = mi.getMatrix();
		ArrayList<Term> termList = (candidate?m.getCandidateTerms():m.getTargetTerms()) ;
		for (Term t : termList)
		{
			Integer ind = m.getTermIndex(termList, t,candidate);
			assert (ind!=null); 
			indexIDs.put(ind, t.getId());
		}
		return indexIDs;
	}
	
	/**
	 * Creates an index->ID hash
	 * @param mi
	 * @param candidateIDs
	 * @return
	 */
	private HashMap<Integer, String> makeProvenanceHash(MatchInformation mi,boolean candidate) {
		HashMap<Integer, String> indexProvenances = new HashMap<>();
		MatchMatrix m = mi.getMatrix();
		ArrayList<Term> termList = (candidate?m.getCandidateTerms():m.getTargetTerms()) ;
		for (Term t : termList)
		{
			Integer ind = m.getTermIndex(termList, t,candidate);
			assert (ind!=null); 
			indexProvenances.put(ind, t.getProvenance());
		}
		return indexProvenances;
	}

}

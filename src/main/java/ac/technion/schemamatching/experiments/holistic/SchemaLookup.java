/**
 * 
 */
package ac.technion.schemamatching.experiments.holistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.OBTermMatch;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.DummyStatistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.predictors.AvgAPredictor;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictors;
import ac.technion.schemamatching.statistics.predictors.Predictor;
import ac.technion.schemamatching.statistics.predictors.STDEVPredictor;
import ac.technion.schemamatching.testbed.ExperimentSchema;

/**
 * @author Tomer Sagi
 *
 */
public class SchemaLookup implements HolisticExperiment{
	public List<Statistic> runExperiment(Set<ExperimentSchema> eSet) 
	{
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		OBTermMatch m = new OBTermMatch();
		//prepare all matches
		for (ExperimentSchema target : eSet)
		{
			HashMap<Integer,MatchInformation> miList = new HashMap<Integer,MatchInformation>(); 
			for (ExperimentSchema candidate : eSet)
			{
				if (target.getID()!=candidate.getID())
				{
					MatrixPredictors mv = new MatrixPredictors();
					MatchInformation mi = m.match(candidate.getTargetOntology(), target.getTargetOntology(),false);
					miList.put(candidate.getID(),mi);
					mv.init(target.getID() + "," + candidate.getID(), mi);
					res.add(mv);
				}
			}
			DummyStatistic ds = new DummyStatistic();
			ds.setName("multi-schema-normed");
			ds.setHeader(new String[]{"target,candidate,rankByL1Norm"});
			ds.setData(simpleRankAll(target,miList));
			res.add(ds);
			
			DummyStatistic ds2 = new DummyStatistic();
			ds2.setName("multi-schema-prediction-normed");
			ds2.setHeader(new String[]{"target,candidate,rankByMultiPred"});
			ds2.setData(predictRankAll(target,miList));
			res.add(ds2);
		}
		return res;
	}

	/**
	 * Creates term-schema matrix similar to term-document matrix
	 * in IR. Rows are terms in the target schema, columns are candidate 
	 * schemas. Values in matrix are the max similarity between any 
	 * term in schema j and term i from the target schema. 
	 * Calculates various rankings on this matrix. 
	 * @param target
	 * @param miList
	 * @return
	 */
	private ArrayList<String[]> simpleRankAll(ExperimentSchema target,
			HashMap<Integer, MatchInformation> miList) {
		//Populate term-schema matrix and prepare norms
		ArrayList<Term> tTerms = new ArrayList<Term>(target.getTargetOntology().getTerms(true));
		ArrayList<Integer> candidateIDs = new ArrayList<Integer>(miList.keySet());
		double[][] termSchema = new double[tTerms.size()][miList.size()];
		HashMap<Integer,Double> l1Norms = new HashMap<Integer,Double>();
		double maxNorm = 0.0;
		for (int row = 0 ; row <tTerms.size();row++)
		{
			Term t = tTerms.get(row);
			double l1Norm = 0.0;
			for (int col = 0 ; col < miList.size();col++)
			{
				MatchMatrix mm = miList.get(candidateIDs.get(col)).getMatrix();
				double val = mm.getMaxConfidence(t,false);
				termSchema[row][col] = val;
				//Prepare norms
				l1Norm+=val;
			}
			l1Norms.put(row, l1Norm);
			maxNorm = Math.max(maxNorm, l1Norm);
		}
		//normalize norms
				if (maxNorm == 0.0) maxNorm = 1.0;
				for (Integer i : l1Norms.keySet())
					l1Norms.put(i, l1Norms.get(i)/maxNorm);

		//Calc prediction
		ArrayList<String[]> res = new ArrayList<String[]>();
		double n = ((double)tTerms.size());
		for (int col =0 ;col<miList.size();col++)
		{
			String candID = candidateIDs.get(col).toString();
			double sum = 0.0;
			for (int row = 0 ; row <tTerms.size();row++)
			{
				if (l1Norms.get(row)>0)
					sum+= termSchema[row][col]/l1Norms.get(row);
			}
				
			res.add(new String[]{Integer.toString(target.getID()), candID, new Double(sum/n).toString()});
		}
		return res ;
	}
	
	/**
	 * Creates term-schema matrix similar to term-document matrix
	 * in IR. Rows are terms in the target schema, columns are candidate 
	 * schemas. Values in matrix are the max similarity between any 
	 * term in schema j and term i from the target schema. 
	 * Calculates various rankings on this matrix. 
	 * @param target
	 * @param miList
	 * @return
	 */
	private ArrayList<String[]> predictRankAll(ExperimentSchema target,
			HashMap<Integer, MatchInformation> miList) {
		//Populate term-schema matrix and prepare norms
		ArrayList<Term> tTerms = new ArrayList<Term>(target.getTargetOntology().getTerms(true));
		ArrayList<Integer> candidateSchemas = new ArrayList<Integer>(miList.keySet()); 
		HashMap<Term,HashMap<Integer,Double>> attributePredictions = generatePredictions(target,miList);

		double[][] termSchema = new double[tTerms.size()][miList.size()];
		HashMap<Integer,Double> l1Norms = new HashMap<Integer,Double>();
		double maxNorm = 0.0; 
		for (int row = 0 ; row <tTerms.size();row++)
		{
			Term t = tTerms.get(row);
			double l1Norm = 0.0;
			//Iterate over schemas, calc attribute prediction for each target attribute
			for (int col = 0 ; col < miList.size();col++)
			{
				double val = attributePredictions.get(t).get(candidateSchemas.get(col)).doubleValue(); 
				termSchema[row][col] = val;
				//Prepare norms
				l1Norm+=val;
			}
			l1Norms.put(row, l1Norm);
			maxNorm = Math.max(maxNorm, l1Norm);
		}
		//normalize norms
		if (maxNorm == 0.0) maxNorm = 1.0;
		HashMap<Term,Double> weights = new HashMap<Term,Double>();
		for (int i=0;i<tTerms.size();i++)
			weights.put(tTerms.get(i), 1.0-l1Norms.get(i)/maxNorm);
		
		//Calc prediction using matrix predictors on reduced mi objects
		ArrayList<String[]> res = new ArrayList<String[]>();
		for(Integer candID : miList.keySet())
		{
			MatchInformation mi = miList.get(candID);
			for (Match m :mi.getCopyOfMatches())
			{
				Term t = m.getTargetTerm();
				mi.updateMatch(t, m.getCandidateTerm()
						, m.getEffectiveness()*weights.get(t));
			}
			MatrixPredictors mp = new MatrixPredictors();
			mp.init("Predicted" + candID.toString(), mi);
			String[] predResults = mp.getData().get(0);
			/* Dominants	.455
AvgConfPredictor	.212
STDEVPredictor	-.347
LMMPredictor	-.123
BMPredictor	.050
*/
			/*predictors.put("BMPredictor", new BMPredictor());0
		predictors.put("BMMPredictor", new BMMPredictor());1
		predictors.put("LMMPredictor", new LMMPredictor());2
		predictors.put("STDEVPredictor", new STDEVPredictor());3
		predictors.put("MaxPredictor", new MaxPredictor());4
		predictors.put("AvgConfPredictor", new AvgPredictor());5
		Dom6*/
			double prediction = .05*Double.parseDouble(predResults[0])
					-.123*Double.parseDouble(predResults[2])
					-.347*Double.parseDouble(predResults[3])
					+.212*Double.parseDouble(predResults[5])
					+.455*Double.parseDouble(predResults[6]);
			res.add(new String[]{Integer.toString(target.getID()), candID.toString()
					, new Double(prediction).toString()});
		}
		return res ;
	}

	/**
	 * Generates a term-schema matrix such that for each term a map of candidateID->prediction
	 * is returned. 
	 * @param target
	 * @param miList
	 * @return
	 */
	private HashMap<Term, HashMap<Integer, Double>> generatePredictions(
			ExperimentSchema target, HashMap<Integer, MatchInformation> miList) {
		HashMap<Term,HashMap<Integer,Double>> res = new HashMap<Term,HashMap<Integer,Double>>();
		Predictor[] preds = new Predictor[]{new AvgAPredictor(),
		new STDEVPredictor()};
		for (Integer id : miList.keySet())
		{
			MatchInformation mi = miList.get(id);
			double[][] mm = mi.getMatchMatrix();
			for (Term t : target.getTargetOntology().getTerms(true))
			{
				int trow = mi.getMatrix().getTermIndex(mi.getOriginalTargetTerms(), t, false);
				int cols =mm[trow].length;
				for (Predictor p : preds)
				{
					p.init(1, cols );
					p.newRow();
				}
				for (int col=0;col<cols;col++)
				{
					for (Predictor p : preds)
						p.visitColumn(mm[trow][col]);
				}
				double prediction = 8.33*preds[1].getRes()-15.42*preds[0].getRes();
				HashMap<Integer,Double> termPreds = (res.containsKey(t)?res.get(t):new HashMap<Integer,Double>());
				termPreds.put(id, prediction);
				res.put(t, termPreds);
			}
			
		}
		return res;
	}

	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		return true;
}

	public String getDescription() {
		String desc = "1.Select 10 random schemas each time from our 249 webforms dataset" + 
					  "2.Match with all the rest and predict using a matrix predictor ensemble tuned on Precision." + 
					  "3.Calculate precision @5, precision @ 10 and precision @ 20 on  webforms where a relevant " +
					  "webform is one of the same domain of the one being looked up " +
					  "(we have domain classification for all 249 schemas to 21 domains)." +
					  "A random algorithm would have between 10-20% Precision @10.";
		return desc;
	}
	
	static double Gamma(double z)
	{
	double tmp1 = Math.sqrt(2*Math.PI/z);
	double tmp2 = z + 1.0/(12 * z - 1.0/(10*z));
	tmp2 = Math.pow(tmp2/Math.E, z);
	return tmp1 * tmp2;
	}

}

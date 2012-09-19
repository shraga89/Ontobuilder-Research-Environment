/**
 * 
 */
package ac.technion.schemamatching.experiments.holistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.OBTermMatch;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.DummyStatistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictors;
import ac.technion.schemamatching.testbed.ExperimentSchema;

/**
 * @author Tomer Sagi
 *
 */
public class SchemaLookup implements HolisticExperiment{
	public ArrayList<Statistic> runExperiment(HashSet<ExperimentSchema> eSet) 
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
			ds.setData(rankAll(target,miList));
			res.add(ds);
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
	private ArrayList<String[]> rankAll(ExperimentSchema target,
			HashMap<Integer, MatchInformation> miList) {
		//Populate term-schema matrix and prepare norms
		ArrayList<Term> tTerms = new ArrayList<Term>(target.getTargetOntology().getTerms(true));
		ArrayList<Integer> candidateIDs = new ArrayList<Integer>(miList.keySet());
		double[][] termSchema = new double[tTerms.size()][miList.size()];
		HashMap<Integer,Double> l1Norms = new HashMap<Integer,Double>();
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
		}
		
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

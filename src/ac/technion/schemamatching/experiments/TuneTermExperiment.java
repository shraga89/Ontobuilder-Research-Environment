/**
 * 
 */
package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.BestMappingsWrapper;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingsUtilities;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;
import ac.technion.schemamatching.matchers.FirstLineMatcher;
import ac.technion.schemamatching.matchers.OBTermMatch;
import ac.technion.schemamatching.matchers.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * @author Tomer Sagi
 * This experiments generates tuning information for Term
 * using Precision, Recall, NBPrecision and NBRecall by
 * varying the weights of the 2 components in 0.05 increments
 * over a given schema pair
 *
 */
public class TuneTermExperiment implements MatchingExperiment 
{
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		ArrayList<Statistic> res = new ArrayList<Statistic>(); 
		double weightNGram;
		for (int i=0;i<11;i++)
		{ 
			weightNGram = 0.05*i;
			String instanceDescription = esp.getSPID() + "," + Double.toString(weightNGram); 
			//Run Term using these weights on supplied experiment schema pair
			OBTermMatch obt = new OBTermMatch(weightNGram);
			MatchInformation mi = obt.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			//Generate non-binary statistics
			NBGolden nbg = new NBGolden();
			nbg.init(instanceDescription, mi, esp.getExact());
			res.add(nbg);
			
			//2ndLine match using MWBG
			BestMappingsWrapper.matchMatrix = mi.getMatrix();	
			SchemaTranslator st = BestMappingsWrapper.GetBestMapping("Max Weighted Bipartite Graph");
			assert (st!=null);
			st.importIdsFromMatchInfo(mi,true);
			MatchInformation mwbg = mi.clone(); 
			try {
				ConversionUtils.fillMI(mwbg,st);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//Generate binary statistics
			BinaryGolden bg = new BinaryGolden();
			bg.init(instanceDescription + "mwbg", mwbg,esp.getSTExact());
			res.add(bg);
			//2ndLine match using Threshold (0.5)
			MatchInformation miTH = mi.clone(); 
			SchemaTranslator tmp = new SchemaTranslator(miTH);
			SchemaTranslator th = SchemaMatchingsUtilities.getSTwithThresholdSensitivity(tmp, 0.5);
			try {
				ConversionUtils.fillMI(miTH,th);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Generate binary statistics
			BinaryGolden thbg = new BinaryGolden();
			thbg.init(instanceDescription + "Threshold(0.5)", miTH,esp.getSTExact());
			res.add(thbg);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		//no init needed
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		String desc = "This experiments generates tuning information for Term " 
						+ "using Precision, Recall, NBPrecision and NBRecall by " 
						+ " varying the weights of the 2 coomponents in 0.05 increments "
						+ " over a given schema pair";
		return desc;
	}

}

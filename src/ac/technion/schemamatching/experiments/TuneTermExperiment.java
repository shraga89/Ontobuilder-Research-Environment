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
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
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
		double weightNGram = 0.4;
		double weightJaro = 0.2;
		double stringNameWeight = 0.25;
		double wordNameWeight = 0.75;
		double stringLabelWeight = 0;
		double wordLabelWeight = 0;
		for (double i=0;i<=100;i+=10)
		{ 
			wordNameWeight = i/100;
			stringNameWeight= (100-i)/100;
			//wordLabelWeight = (100-i)/200;
			//for (double j=0;j<=(100-i);j+=10)
			//{
			//stringLabelWeight = (100-i)/200;
			//wordNameWeight = (100-i-j)/200;
			//wordLabelWeight = (100-i-j)/200;
			
			String instanceDescription = esp.getSPID() + "," + Double.toString(stringNameWeight) + "," + Double.toString(stringLabelWeight); 
			//Run Term using these weights on supplied experiment schema pair
			OBTermMatch obt = new OBTermMatch(weightNGram,weightJaro, wordNameWeight, stringNameWeight, stringLabelWeight, wordLabelWeight);
			MatchInformation mi = obt.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			//Generate non-binary statistics
			NBGolden nbg = new NBGolden();
			nbg.init(instanceDescription, mi, esp.getExact());
			res.add(nbg);
			//2ndLine match using Threshold (0.25)
			MatchInformation miTH = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology()); 
			SchemaTranslator tmp = new SchemaTranslator(mi);
			SchemaTranslator th = SchemaMatchingsUtilities.getSTwithThresholdSensitivity(tmp, 0.25);
			th.importIdsFromMatchInfo(mi, true);
			try {
				ConversionUtils.fillMI(miTH,th);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Generate binary statistics
			BinaryGolden thbg = new BinaryGolden();
			thbg.init(instanceDescription + ",Threshold(0.25)", miTH,esp.getExact());
			res.add(thbg);
			
			//2ndLine match using Threshold (0.2)
			MatchInformation miTH2 = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
			th = SchemaMatchingsUtilities.getSTwithThresholdSensitivity(tmp, 0.2);
			th.importIdsFromMatchInfo(mi, true);
			try {
				ConversionUtils.fillMI(miTH2,th);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//debug
//			MatchListPrinter miMLP = new MatchListPrinter();
//			miMLP.init(instanceDescription + "mi", mi);
//			res.add(miMLP);
//			MatchListPrinter thMLP = new MatchListPrinter();
//			thMLP.init(instanceDescription + "miTH", miTH);
//			res.add(thMLP);
			
			//Generate binary statistics
			BinaryGolden th2bg = new BinaryGolden();
			th2bg.init(instanceDescription + ",Threshold(0.2)", miTH2,esp.getExact());
			res.add(th2bg);
			
			//2ndLine match using MWBG
			BestMappingsWrapper.matchMatrix = mi.getMatrix();	
			SchemaTranslator st = BestMappingsWrapper.GetBestMapping("Max Weighted Bipartite Graph");
			assert (st!=null);
			st.importIdsFromMatchInfo(mi,true);
			MatchInformation mwbg = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology()); 
			try {
				ConversionUtils.fillMI(mwbg,st);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//Generate binary statistics
			BinaryGolden bg = new BinaryGolden();
			bg.init(instanceDescription + ",mwbg", mwbg,esp.getExact());
			res.add(bg);
			
			}
		//}
		return res;
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

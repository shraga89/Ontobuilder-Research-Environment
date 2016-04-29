/**
 * 
 */
package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsWrapper;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingAlgorithmsRunner;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.OBMaxDelta;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.matchers.secondline.UnifiedTopKMatchingCreator;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MappingPrinter;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.VectorPrinterUsingExact;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.util.ConversionUtils;

/**
 * @author Roee Shraga
 * 
 *
 */
public class TopKexp implements PairWiseExperiment {

	private ArrayList<FirstLineMatcher> flM = new ArrayList<FirstLineMatcher>();
	private ArrayList<SecondLineMatcher> slM = new ArrayList<SecondLineMatcher>(); 
	public int k = 5;
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.testbed.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		MatchInformation res = null;
		MatchInformation mi = null;
		MatchInformation mi1 = null;
		String instanceDesc = "";
        UnifiedTopKMatchingCreator uni = new UnifiedTopKMatchingCreator();
        uni.weighting = UnifiedTopKMatchingCreator.WEIGHTING.OCCURRENCE;
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		for (FirstLineMatcher m : flM)
		{
			mi = esp.getSimilarityMatrix(m, false);	
			K2Statistic b2 = new BinaryGolden();
			instanceDesc =  esp.getID() + "," + m.getName();
			OBMaxDelta slm = new OBMaxDelta(0);
			mi1 = slm.match(mi);
			b2.init(instanceDesc, mi1,esp.getExact());
			evaluations.add(b2);
			MappingPrinter mp = new MappingPrinter();
			mp.init(instanceDesc, mi1, esp.getExact());
			evaluations.add(mp);
			try {
				SchemaMatchingAlgorithmsRunner.setAccumulationMode(true);
				SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(mi);

				System.out.println("Derive top " + k);
        	
				for (int i = 1; i <= k; i++) {
					res = smw.getNextBestMatching();
					System.out.println(" " + i);
					ConversionUtils.zeroNonMatched(res);
					uni.addMatching(i, res);
        	}
	      }
			catch (Exception e) {
		    	  e.printStackTrace();
		      }
		}
	      
        
		System.out.print("\n");
        uni.buildGraph();
        uni.deriveClusters();
		//calculate Precision and Recall
		K2Statistic b2 = new BinaryGolden();
		instanceDesc =  esp.getID() + ", Top K Matching";
		b2.init(instanceDesc, uni.getResultingMatching() ,esp.getExact());
		evaluations.add(b2);
		MappingPrinter mp = new MappingPrinter();
		mp.init(instanceDesc, mi1, esp.getExact());
		evaluations.add(mp);
		
		return evaluations;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		this.flM = flM;
		this.slM = slM;
		if (properties.containsKey("topK"))
		{
			k = Integer.parseInt((String)properties.get("topK"));
			return true;
		}
		System.err.println("OBTopK 2LM could not find the required " +
				"property 'topK' in the property file");
		return false;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return null;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#summaryStatistics()
	 */
	public ArrayList<Statistic> summaryStatistics() {
		return null;
	}

}

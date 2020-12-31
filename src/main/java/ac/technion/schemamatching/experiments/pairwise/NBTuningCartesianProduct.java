/**
 * 
 */
package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.OBTermMatch;
import ac.technion.schemamatching.matchers.secondline.OBMaxDelta;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MatchDistance;
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * @author Tomer Sagi
 * Experiment in automated tuning conducted for NB journal paper. 
 * Creates cartesian product of configurations of Term tunable parameters. Runs on all pairs in DS and 
 * calculates all NB and Binary measures using MaxDelta 0.1 as the match selector
 */
public class NBTuningCartesianProduct implements PairWiseExperiment {

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment#runExperiment(ac.technion.schemamatching.testbed.ExperimentSchemaPair)
	 */
	@Override
	public List<Statistic> runExperiment(ExperimentSchemaPair esp) {
		Ontology c = esp.getCandidateOntology();
		c.normalize();
		Ontology t = esp.getTargetOntology();
		t.normalize();
		
		List<Statistic> stats = new ArrayList<>();
		//generate cartesian product of configurations
		double bin[] = new double[] {0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
		boolean[] bbin = new boolean[]{true,false}; 
		
		int i=0;
		//For each valid configuration
		for (double nGramWeight : bin) {
			for (double jaroWinklerWeight : bin) {
				if (nGramWeight+jaroWinklerWeight>1.0)
					continue;
				for (double wordNameWeight : bin) {
					double stringNameWeight = 1-wordNameWeight;
					for (double wordLabelWeight : bin)
					{
						double stringLabelWeight = 1-wordLabelWeight; 
						for (boolean useSoundex : bbin) {
							for (short useAvg : new short[] {0,1,2}) {
								for (short nGram : new short[]{2,3,4}) {
									//Run 1LM
									OBTermMatch ta = new OBTermMatch(nGramWeight, jaroWinklerWeight, wordNameWeight, stringNameWeight, stringLabelWeight, wordLabelWeight,useSoundex,useAvg,nGram  );
									long start = System.currentTimeMillis();
									MatchInformation mi = ta.match(c, t, false);
									long duration = System.currentTimeMillis()-start;
									System.err.println("Completed" + i + " in " + duration);
									String instanceDesc = "" + nGramWeight + "," + jaroWinklerWeight + "," + wordNameWeight + "," + 
									wordLabelWeight + "," + useSoundex + "," + useAvg + "," + nGram; 
//									//count and output number of generated configurations
									i++;
									K2Statistic nbStat = new NBGolden();
									nbStat.init(instanceDesc, mi, esp.getExact());
									stats.add(nbStat);
									
									K2Statistic md = new MatchDistance();
									md.init(instanceDesc, mi, esp.getExact());
									stats.add(nbStat);
									
									//apply 2LM and calculate Binary stats
									OBMaxDelta maxDelta = new OBMaxDelta(0.1);
									MatchInformation mi2 = maxDelta.match(mi);
									K2Statistic bStat = new BinaryGolden();
									bStat.init(instanceDesc, mi2, esp.getExact());
									stats.add(bStat);
								}
							}
						}
					}
				}
			}
		}
		
		System.out.println("Number of configurations generated=" + i);
		return stats;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList, java.util.ArrayList)
	 */
	@Override
	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Cartesian product of Term congif option values";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment#summaryStatistics()
	 */
	@Override
	public List<Statistic> summaryStatistics() {
		return null;
	}

}

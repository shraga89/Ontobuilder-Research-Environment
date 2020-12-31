/**
 * 
 */
package ac.technion.schemamatching.experiments.pairwise;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.io.matchimport.CSVMatchImporter;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MappingPrinter;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.UnconstrainedMatchDistance;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * @author Tomer Sagi
 * Evaluates all first line matchers supplied against 
 * multiple human responses for schema pairs supplied.
 * Assumes the schemapair folder contains a folder named humans.
 * The folder should contain a set of files, each representing 
 * the opinion of a single human judge on a single schema pair.
 * File suffix should match the schemapair file name.
 * (e.g. Excel2CRM.mapping matches J1Excel2CRM.mapping, J2Excel2CRM.mapping, etc.) 
 *
 */
public class NBNBEvaluationExperiment implements PairWiseExperiment {

	ArrayList<FirstLineMatcher> flm = new ArrayList<FirstLineMatcher>();
	ArrayList<MatchInformation> humanOpinions = new ArrayList<MatchInformation>(); 
	Properties p = null;
	private ArrayList<SecondLineMatcher> slm = new ArrayList<SecondLineMatcher>();
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment#runExperiment(ac.technion.schemamatching.testbed.ExperimentSchemaPair)
	 */
	@Override
	public List<Statistic> runExperiment(ExperimentSchemaPair esp) {
		
		//Get human judges opinions
		CSVMatchImporter cf = new CSVMatchImporter();
		
		File spExact = new File(OBExperimentRunner.getOER().getDsurl() + OBExperimentRunner.getOER().getDoc().getSPPathBySPID(esp.getID()));
		File humanDir = new File(spExact.getParentFile(),"humans");
		final String suffix = spExact.getName();
		FilenameFilter thisFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(suffix))
					return true;
				return false;
			}
		};
		
		for (File f : humanDir.listFiles(thisFilter ))
			humanOpinions.add(cf.importMatch(esp.getExact(),f));
		
		//Generate aggregated similarity vector from opinions
		HashMap<Match,Integer> aggVec = new HashMap<Match,Integer>();
		for (MatchInformation mi : humanOpinions)
			for (Match m : mi.getCopyOfMatches())
			{
				if (m.getEffectiveness()==0)
					continue;
				if (!aggVec.containsKey(m))
					aggVec.put(m,new Integer(0));
				int i = aggVec.get(m).intValue();
				i++;
				aggVec.put(m,new Integer(i));
			}
		
		MatchInformation aggMI = new MatchInformation(esp.getCandidateOntology(), esp.getTargetOntology());
		
		//count>=4
//		int cnt =0;
//		for (Match m : aggVec.keySet())
//		{
//			if (aggVec.get(m)>=4)
//				System.err.println(m.toString() + "IntVal=" + aggVec.get(m));
//				cnt++;
//		}
//		System.err.println("Num aggregate votes 50% or over:" + cnt);
		for (Match m : aggVec.keySet())
			m.setEffectiveness(aggVec.get(m).doubleValue()/((double)humanOpinions.size()));

		aggMI.setMatches(new ArrayList<Match>(aggVec.keySet()));
		Statistic mp = new MappingPrinter();
		mp.init(esp.getID() + ",ReferenceVec", aggMI);
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		res.add(mp);
		
		//Match using all FLM and evaluate 
		for (FirstLineMatcher f : flm)
		{
			MatchInformation fMI = f.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
			K2Statistic umd = new UnconstrainedMatchDistance();
			String instance = esp.getID() + "," + f.getName();
			umd.init(instance, fMI,aggMI);
			res.add(umd);
			Statistic mpf = new MappingPrinter();
			mpf.init(instance,fMI);
			res.add(mpf);
			
			//output major disagreements in UMD
			for (Term t : esp.getTargetOntology().getTerms(true))
			{
				MatchInformation pmi = new MatchInformation(esp.getCandidateOntology(), esp.getTargetOntology());
				
				ArrayList<Match> matches = fMI.getMatchesForTerm(t, false);
				if (matches !=null)
					pmi.setMatches(matches );
				
				MatchInformation aggPMI = new MatchInformation(esp.getCandidateOntology(), esp.getTargetOntology());
				
				ArrayList<Match> expectedMatches = aggMI.getMatchesForTerm(t, false);
				if (expectedMatches!=null)
					aggPMI.setMatches(expectedMatches );
				
				K2Statistic partialUmd = new UnconstrainedMatchDistance();
				((UnconstrainedMatchDistance)partialUmd).setLimit(t);
				partialUmd.init(esp.getID() + "," + f.getName() + "," + t.getProvenance(), pmi,aggPMI);
				res.add(partialUmd);
			}
			
			//Using all second line matchers
			for (SecondLineMatcher s : slm)
			{
				//Second Line Match
				if (p == null || !s.init(p)) 
					System.err.println("Initialization of " + s.getName() + 
							"failed, we hope the author defined default values...");
				double startTime = System.currentTimeMillis();
				MatchInformation mi1 = s.match(fMI);
				double endTime = System.currentTimeMillis();
				System.out.println(s.getName() + " Runtime: " + (endTime - startTime));
				//calculate Precision and Recall
				String instanceDesc =  instance + "," + s.getName()+ "," + s.getConfig();
				//Calculate MatchDistance
				K2Statistic md2 = new UnconstrainedMatchDistance();
				md2.init(instanceDesc, mi1,aggMI);
				res.add(md2);
			}
		}
		
		
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList, java.util.ArrayList)
	 */
	@Override
	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		flm = flM;
		slm  = slM;
		p = properties;
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment#getDescription()
	 */
	@Override
	public String getDescription() {
		return "This experiments evaluates non-binary results of FLM against non-binary reference vectors.";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment#summaryStatistics()
	 */
	@Override
	public List<Statistic> summaryStatistics() {
		return null;
	}

}

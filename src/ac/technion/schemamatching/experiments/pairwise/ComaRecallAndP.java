package ac.technion.schemamatching.experiments.pairwise;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import ac.technion.iem.ontobuilder.io.matchimport.MappingMatchImporter;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.experiments.holistic.HolisticExperiment;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.SimMatrixShell;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchema;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;



public class ComaRecallAndP implements HolisticExperiment {
	private SimMatrixShell flM;
	private Properties properties;
	private OBExperimentRunner myOer;

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer,Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		/*Using the supplied first line matcher list and second line matcher list allows run-time 
		changes to matchers used in the experiment*/
		this.flM = new SimMatrixShell();
		this.properties = properties;
		this.myOer = oer;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return "Calc Statistic to Coma3";
	}
	
	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}

	@Override
	public List<Statistic> runExperiment(Set<ExperimentSchema> hashSet) {
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		//TODO extract dir to property file
		File dir = new File("C:\\Users\\רועי\\Dropbox\\MCD Predictor and 2lm\\Coma vanila version\\results\\PO");
		for (File f : dir.listFiles())
		{
			int spid = Integer.parseInt(f.getName().replace(".mapping", "")); //TODO error checking on filename structure
			int dsid = 17; 	//TODO extract dsid to property file
			try {
				ExperimentSchemaPair esp = new ExperimentSchemaPair(spid, dsid);
				flM.setPath(dir.getAbsolutePath(),f.getName());
				flM.setImporter(new MappingMatchImporter());
				MatchInformation mi = flM.match(esp.getCandidateOntology(),esp.getTargetOntology(),false);
				K2Statistic binPR = new BinaryGolden();
				binPR.init(dsid + "," + spid, mi, esp.getExact());
				evaluations.add(binPR);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			
		}
		
		return evaluations;
	}



}

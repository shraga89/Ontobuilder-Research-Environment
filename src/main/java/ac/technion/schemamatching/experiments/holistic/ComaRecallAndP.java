package ac.technion.schemamatching.experiments.holistic;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import ac.technion.iem.ontobuilder.io.matchimport.MappingMatchImporter;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.SimMatrixShell;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchema;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;


/**
 * Loads coma-style mapping files and calculates various statistics over them. 
 * Requires the following properties to be supplied in a property file:
 * directory=path\to\mappinFiles
 * dataset=<dDSID (e.g.17 for purchase order>
 * Assumes filename is SPID.mapping 
 * @author Tomer Sagi
 *
 */
public class ComaRecallAndP implements HolisticExperiment {
	private SimMatrixShell flM;
	private Properties properties;


	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer,Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		this.flM = new SimMatrixShell();
		this.properties = properties;
		if (!properties.containsKey("directory"))
		{
				System.err.println("Missing property directory in property file.");
				return false;
		}
		if (!properties.containsKey("dataset"))
		{
			System.err.println("Missing property dataset in property file.");
			return false;
		}
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return "Calc Statistics for Coma3";
	}
	
	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}

	@Override
	public List<Statistic> runExperiment(Set<ExperimentSchema> hashSet) {
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		File dir = new File(properties.getProperty("directory"));
		if (!dir.exists() || ! dir.isDirectory())
		{
			System.err.println("Supplied directory " + dir.getPath() + " not found or is not a directory.");
			return evaluations;
		}
		for (File f : dir.listFiles())
		{
			if (!f.getName().endsWith(".mapping"))
			{
				System.err.println("File " + f.getName() + " skipped, expected .mapping extension");
				continue;
			}
			int spid = Integer.parseInt(f.getName().replace(".mapping", "")); 
			int dsid = Integer.parseInt((String)properties.get("dataset")); 	
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

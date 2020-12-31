package ac.technion.schemamatching.experiments.pairwise;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import de.wdilab.coma.integration.COMA_API;
import de.wdilab.coma.structure.MatchResult;
import ac.technion.iem.ontobuilder.io.matchimport.MappingMatchImporter;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.ExperimentDocumenter;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * @authors: Yifat Salman, Hila Arobas and Nofar Piterman
 * This experiment performs schema matching using the Coma system.
 * The arguments for the experiment are the same as for all the other experiments
 * except the -f and -s parameters which are not necessary, since they are not
 * been used by the Coma system. In addition the experiment requires a properties
 * file. The output is array of Statistics.
 * The properties file contains the path to a folder which the Coma results are
 * saved, and from where the Coma results are been read. The properties file contains
 * the following line: "directory = <path to folder location>".
 */


public class ComaExperiments implements PairWiseExperiment {
	
	private Properties properties;

	@Override
	public List<Statistic> runExperiment(ExperimentSchemaPair esp) {
		try {
			ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
			
			File dir = new File(properties.getProperty("directory"));
			// clean the directory from previous results
			final File[] files = dir.listFiles();
	        for(File f: files) {
	        	f.delete();
	        }
			if (!dir.exists() || ! dir.isDirectory())
			{
				System.err.println("Supplied directory " + dir.getPath() + " not found or is not a directory.");
				return evaluations;
			}
			
			String providerSrc = esp.getCandidateOntology().getFile().getCanonicalPath();
			String providerTrg = esp.getTargetOntology().getFile().getCanonicalPath();
			
			ExperimentDocumenter ed = OBExperimentRunner.getOER().getDoc();
			int pid = ed.getPairID((int)esp.getCandidateID(),(int)esp.getTargetID(),false);

			// activate the Coma system and get MatchResult
			COMA_API api = new COMA_API();
			MatchResult res = api.matchModelsDefault(providerSrc, providerTrg);
			
			PrintWriter writer = new PrintWriter(properties.getProperty("directory") + pid + ".mapping", "UTF-8");
			// this is done in order to adjust the result file to the importer constraints
			for (int i=0 ; i<7 ; i++){
				writer.println("a");
			}
			writer.println(res.toString());
			writer.close();
			
			int dsid = esp.getDataSetType().getDatasetDBid();
			
			for (File f : dir.listFiles())
			{
				if (!f.getName().endsWith(".mapping"))
				{
					System.err.println("File " + f.getName() + " skipped, expected .mapping extension");
					continue;
				}
				int spid = Integer.parseInt(f.getName().replace(".mapping", "")); 
								
				MappingMatchImporter importer = new MappingMatchImporter();
				// casting the MatchResult (Coma output) to MatchInformation (BinaryGolden input) 
				MatchInformation mi= importer.importMatch(new MatchInformation(esp.getCandidateOntology(),esp.getTargetOntology()), f);
				K2Statistic binPR = new BinaryGolden();
				binPR.init(dsid + "," + spid, mi, esp.getExact());
				evaluations.add(binPR);
				
			}
			
			return evaluations;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		this.properties = properties;
		return true;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Statistic> summaryStatistics() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

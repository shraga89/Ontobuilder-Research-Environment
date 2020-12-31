/**
 * 
 */
package ac.technion.schemamatching.experiments.pairwise;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * @author Tomer Sagi
 * The following experiment class converts the given schema pair to the ontobuilder format
 * Output is directed to supplied output directory via properties file
 *
 */
public class ConvertXSDtoOBXml implements PairWiseExperiment {

	private File outDir;
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.testbed.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		if (outDir == null || !outDir.isDirectory())
		{
			System.err.println("Invalid output directory supplied");
			return null;
		}
		
		Ontology c = esp.getCandidateOntology();
		String cFName = c.getName() + ".xml";
		Ontology t = esp.getTargetOntology();
		String tFName = t.getName() + ".xml";
		
		//Create pair directory, e.g. absoluteagency.xml_www.dating.com.xml_EXACT
		String pName = cFName + "_" + tFName + "_EXACT";
		File pairDir = new File(pName);
		if (!pairDir.exists()) pairDir.mkdir();
		
		//Create ontology files
		try {
			c.saveToXML(new File(pairDir,cFName));
			t.saveToXML(new File(pairDir,tFName));
		} catch (IOException e) {
			System.err.println("Ontology file creation failed for: " + pName);
			e.printStackTrace();
			return null;
		}
				
		//Create exact match file
		try {
			esp.getExact().saveToXML(new File(pairDir,pName + ".xml"));
		} catch (IOException e) {
			System.err.println("Schema Pair file creation failed for: " + pName);
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		outDir = new File(properties.getProperty("outDir"));
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		String desc = "This experiment class converts the given schema pair to the ontobuilder xml format \n " +
						" Output is directed to supplied output directory via properties file";
		return desc;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#summaryStatistics()
	 */
	public ArrayList<Statistic> summaryStatistics() {
		return null;
	}

}

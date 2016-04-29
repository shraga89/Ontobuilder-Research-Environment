
package ac.technion.schemamatching.experiments.pairwise;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * This simple match experiment is intended as a tutorial for 
 * new developers displaying the major features in ORE. 
 * The experiment matches a given schema pair using all 1LM and 2LM 
 * supplied and returns precision and recall 
 * @author Tomer Sagi
 *
 */
public class ComaTimingParse implements PairWiseExperiment {
	private Properties properties;
	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		// Using all 1st line matchers 
		String line=null;
		ArrayList<String[]> data = new ArrayList<String[]>();
		File file = new File(properties.getProperty("input"));
		if (!file.exists())
		{
			System.err.println("Supplied file " + file.getPath() + " not found or is not a directory.");
		}
		
		//File file= new File("C:\\technion\\work\\COMA++\\coma 3.0 ce v3\\po_timing.txt");//get from properties
		String[] header = new String[]{"Matcher1", "Matcher2", "Matcher3", "Matcher4", "Complex Matcher","Matcher6","Matrix Size"};
		BufferedReader br=null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder strbuild= new StringBuilder();
		for (String i : header){
			strbuild.append(i+"\t");
		}
		strbuild.append("\n");
		try {
			while ((line=br.readLine())!=null){
				if (line.contains("execute matcher Matcher")){
					strbuild.append(line.split("\t")[1].split(" ")[0]+"\t");
					continue;
				}
				if(line.contains("execute matcher ComplexMatcher")){
					strbuild.append(line.split("\t")[1].split(" ")[0]+"\t");
					continue;
				}
				if (line.contains("execute matcher Matcher")){
					strbuild.append(line.split("\t")[1].split(" ")[0]+"\t");
					continue;
				}
					if(line.contains("MatchCount:")){
						strbuild.append(line.split(" ")[1].split(" ")[0]+"\t");
						continue;
					}	
				if(line.contains("workflow")){
					strbuild.append("\n");
					continue;
				}
			}
			File out_path = new File(properties.getProperty("output"));
			if (!out_path.exists() || ! out_path.isDirectory())
			{
				System.err.println("Supplied directory " + out_path.getPath() + " not found or is not a directory.");
			}
			String out_file = out_path + "\\" + file.getName().split(".txt")[0] + "-output.tsv";
			FileWriter fw= new FileWriter(out_file);//get from properties
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(strbuild);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		return evaluations;
	
	}
	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer,Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		this.properties=properties;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return "Coma Timing";
	}
	
	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}

}

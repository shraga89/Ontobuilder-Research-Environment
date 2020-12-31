/**
 * 
 */
package ac.technion.schemamatching.experiments.holistic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchema;

/**
 * @author Tomer Sagi
 * Copies all files in the experiment set to an out folder defined in a properties file
 *
 */
public class SchemaFileExtraction implements HolisticExperiment {

	private File outDir;

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.holistic.HolisticExperiment#runExperiment(java.util.Set)
	 */
	@Override
	public List<Statistic> runExperiment(Set<ExperimentSchema> hashSet) {
		if (outDir == null)
			return null;
		for (ExperimentSchema s : hashSet)
		{
			String fName = s.getTargetOntology().getName(); 
			if (fName.substring(fName.length()-4, fName.length())!=".xml")
				fName = fName + ".xml";
			File n = new File(outDir,fName);
			try {
				copyFile(s.getTargetOntology().getFile(),n);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.holistic.HolisticExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList, java.util.ArrayList)
	 */
	@Override
	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		if (properties == null || !properties.containsKey("outDir"))
		{
			System.err.println("Properties file missing or key named outDir with path not found");
			return false;
		}
		outDir = new File((String)properties.getProperty("outDir"));
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.holistic.HolisticExperiment#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}

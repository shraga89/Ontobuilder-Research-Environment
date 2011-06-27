/**
 * 
 */
package ac.technion.schemamatching.experiments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.infomata.data.DataFile;
import com.infomata.data.DataRow;
import com.infomata.data.TabFormat;

import ac.technion.schemamatching.statistics.Statistic;

/**
 * @author Tomer Sagi
 *
 */
public class ClarityExperiment implements MatchingExperiment {

	public ArrayList<Statistic> runExperiment(
			ExperimentSchemaPair esp) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<OtherMatcher> om) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Outputs a tab delimited file of the supplied name to the supplied path
	 * From an ArrayList of string arrays each arrayList item representing a row 
	 * and each String array item representing a column.  
	 * @param outputPath
	 * @param res
	 * @param fName
	 * * @deprecated TODO move to SMB experiment
	 * @throws IOException
	 */
	private static void outputArrayListofStringArrays(File outputPath,
			ArrayList<String[]> res, String fName) throws IOException {
		DataFile write = DataFile.createWriter("8859_1", false);
		write.setDataFormat(new TabFormat());			
		File outputCOFile = new File(outputPath,fName );
		write.open(outputCOFile);
		for (int i=0;i<res.size();i++)
		{
			DataRow row = write.next();
			String rRow[] = res.get(i);
			for (int j=0;j<rRow.length;j++) row.add(rRow[j]);
		}
		write.close();
	}
}

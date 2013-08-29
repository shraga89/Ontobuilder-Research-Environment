package ac.technion.schemamatching.curpos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Properties;

import ac.technion.schemamatching.testbed.OREDataSetEnum;

public final class CorpusDataManager {

	private static Properties propertiesFile;
	
	public static void setPropertiesFile(Properties newFile){
		propertiesFile = newFile;
	}
	
	private static final String curposFileKeyFormat = "%sCurposFileName";
	
	public static MatchesCurpos LoadMatchesCurpos(OREDataSetEnum dsType){
		String curposFile;
		try {
		String curposFileKey = String.format(curposFileKeyFormat, dsType.name());
		curposFile = propertiesFile.getProperty(curposFileKey);
		}catch (Exception ex){
			System.err.println("Failed to get curpos key for:" + dsType.name());
			ex.printStackTrace();
			return null;
		}
		
		MatchesCurpos curpos;
		FileInputStream fin = null;
		 ObjectInputStream ois = null;
		try{
			
			fin = new FileInputStream(curposFile);
			    ois = new ObjectInputStream(fin);
			    curpos = (MatchesCurpos)ois.readObject();
		}catch (Exception ex){
			System.err.println("Failed to read curpos from" + curposFile);
			System.err.println("Exception: " + ex.getMessage());
			return null;
		}finally{
			try {
				if (fin != null)
					fin.close();
				if (ois != null)
					ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return curpos;
	}
	
	public static boolean SaveMatchesCurpos(MatchesCurpos curpos, OREDataSetEnum dsType){
		String curposFile;
		try {
		String curposFileKey = String.format(curposFileKeyFormat, dsType.name());
		curposFile = propertiesFile.getProperty(curposFileKey);
		}catch (Exception ex){
			System.err.println("Failed to get curpos key for:" + dsType.name());
			ex.printStackTrace();
			return false;
		}
		
		FileOutputStream  output = null;
		try{
			output = new FileOutputStream (getFile(curposFile)) ;
			ObjectOutput out = new ObjectOutputStream(output);
			out.writeObject(curpos);
		}catch (Exception ex){
			System.err.println("Failed to write curpos to" + curposFile);
			ex.printStackTrace();
			return false;
		} finally{
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * Checks for existence of filepath supplied and creates the folder tree if needed
	 * @param resultFolder
	 * @return
	 */
	private static File getFile(String filePath) {
		File entireFile = new File(filePath);
		File testFolder = entireFile.getParentFile();
		if (!testFolder.exists()) {
			boolean success = testFolder.mkdirs();
			if (!success) {
				System.err
						.println("Unable to create folder");
				return null;
			}
		}
		return entireFile;
	}
}

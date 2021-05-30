package ac.technion.schemamatching.curpos;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ac.technion.schemamatching.curpos.MatchesCurpos.TermMatchInfo;
import ac.technion.schemamatching.testbed.OREDataSetEnum;

public final class CorpusDataManager {
	
	private static Properties propertiesFile;
	
	public static void setPropertiesFile(Properties newFile){
		propertiesFile = newFile;
	}
	
	private static final String curposFileKeyFormat = "%sCurposFileName";
	private static final String KEY = "key";
	private static final String TABLE = "table";
	private static final String TERM = "term";
	private static final String CONFIDENCE = "confidence";
	private static final String REPETITIONS = "repetitions";
	
	
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

		return ReadFromJson(curposFile);
	}
	
	@SuppressWarnings("unchecked")
	private static JSONArray WriteToJson  (MatchesCurpos curpos){
		Hashtable<CurposTerm,Hashtable<CurposTerm,TermMatchInfo>> inner =  curpos.getInnerStructure();
		JSONArray list = new JSONArray();
		
		for (Entry<CurposTerm,Hashtable<CurposTerm,TermMatchInfo>> entry : inner.entrySet()){
			CurposTerm t1 = entry.getKey();
			JSONObject obj = new JSONObject();
			obj.put("key", t1.toJSON());
			//add to values if the object have some////////
			
			JSONArray innerList = new JSONArray();
			for (Entry<CurposTerm,TermMatchInfo> innerEntry : entry.getValue().entrySet()){
				JSONObject innerObj = new JSONObject();
				
				CurposTerm t2 = innerEntry.getKey();
				
				innerObj.put("term", t2.toJSON());
				innerObj.put("repetitions",innerEntry.getValue().repetitions);
				innerObj.put("confidence",innerEntry.getValue().confidence);
				innerList.add(innerObj);
			}
			obj.put("table", innerList);
			list.add(obj);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private static MatchesCurpos ReadFromJson  (String curposFile){    /////note the void!!!!//////MatchesCurpos////////
		JSONParser parser = new JSONParser();
		MatchesCurpos outerCurpos = new MatchesCurpos();
		try {
			Hashtable<CurposTerm,Hashtable<CurposTerm,TermMatchInfo>> corpus = outerCurpos.getInnerStructure();
			Object obj = parser.parse(new FileReader(curposFile));
			// loop array
			JSONArray jsonArray = (JSONArray) obj;

			for (JSONObject object : (Iterable<JSONObject>) jsonArray) {
				JSONObject innerJsonObject = (JSONObject) object.get(KEY);
				CurposTerm term = CurposTerm.fromJSON(innerJsonObject);

				Hashtable<CurposTerm, TermMatchInfo> table = new Hashtable<>();
				JSONArray tableJsonArray = (JSONArray) object.get(TABLE);
				for (JSONObject value : (Iterable<JSONObject>) tableJsonArray) {
					JSONObject inTableTerm = (JSONObject) value.get(TERM);
					CurposTerm otherTerm = CurposTerm.fromJSON(inTableTerm);
					double conf = (double) value.get(CONFIDENCE);
					long rep = (long) value.get(REPETITIONS);
					TermMatchInfo tmi = new TermMatchInfo(conf, rep);
					table.put(otherTerm, tmi);
				}

				corpus.put(term, table);
			}
		}
		catch (Exception ex){
			System.err.println("Failed to read curpos from" + curposFile);
			System.err.println("Exception: " + ex.getMessage());
		}
		return outerCurpos;
	}
	
	public static boolean SaveMatchesCurpos(MatchesCurpos curpos, OREDataSetEnum dsType){
		File curposFile;
		try { 							//write to file
		String curposFileKey = String.format(curposFileKeyFormat, dsType.name());
		curposFile = getFile(propertiesFile.getProperty(curposFileKey));
		}catch (Exception ex){
			System.err.println("Failed to get curpos key for:" + dsType.name());
			ex.printStackTrace();
			return false;
		}
		
		
		JSONArray list =  WriteToJson  (curpos);
		 //write to JSON file	 
		try {
			assert curposFile != null;
			FileWriter file = new FileWriter(curposFile);
			file.write(list.toJSONString());
			file.flush();
			file.close();
	 
		} catch (IOException e) {
			System.err.println("Failed to write curpos to" + curposFile.getPath());
			e.printStackTrace();
			return (false);
		}
		
		/* write to binary file
		FileOutputStream  output = null;
		try{
			output = new FileOutputStream (curposFile) ;
			ObjectOutput out = new ObjectOutputStream(output);
			out.writeObject(curpos);
		}catch (Exception ex){
			System.err.println("Failed to write curpos to" + curposFile.getPath());
			ex.printStackTrace();
			return false;
		} finally{
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		return true;
	}
	
	/**
	 * Checks for existence of filepath supplied and creates the folder tree if needed
	 * @param filePath result folder path
	 * @return true if successfully created result folder structure
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

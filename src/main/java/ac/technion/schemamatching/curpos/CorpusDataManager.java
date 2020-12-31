package ac.technion.schemamatching.curpos;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
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
		
		MatchesCurpos curpos = ReadFromJson(curposFile);
		
		/*
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
		}*/
		
		return curpos;
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
			 
			Iterator<JSONObject> iterator = jsonArray.iterator();
			
			while (iterator.hasNext()) {
				JSONObject jsonObject = (JSONObject) iterator.next(); 
				JSONObject innerJsonObject = (JSONObject) jsonObject.get(KEY);
				CurposTerm term = CurposTerm.fromJSON(innerJsonObject);
			
				Hashtable<CurposTerm,TermMatchInfo> table = new Hashtable<CurposTerm,TermMatchInfo>();
				JSONArray tableJsonArray = (JSONArray) jsonObject.get(TABLE);
				Iterator<JSONObject> tableIterator = tableJsonArray.iterator();
				while (tableIterator.hasNext()) {
					JSONObject innerTableJsonObject = (JSONObject) tableIterator.next();
					JSONObject inTableTerm = (JSONObject) innerTableJsonObject.get(TERM);
					CurposTerm otherTerm = CurposTerm.fromJSON(inTableTerm);
					double conf = (double)innerTableJsonObject.get(CONFIDENCE);
					long rep = (long)innerTableJsonObject.get(REPETITIONS);
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

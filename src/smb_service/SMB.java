/**
 * 
 */
package smb_service;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import smb_service.Field;
import smb_service.Field.FieldType;

import JSci.maths.matrices.DoubleMatrix;

import com.infomata.data.DataFile;
import com.infomata.data.DataRow;
import com.infomata.data.TabFormat;



/**
 * @author Tomer Sagi
 * @version 1.1
 * 
 *
 */

public class SMB {


	
	/**
	 * @param args : [E || R || L] [URL] [Matching System Code] 
	 * Optional arguments:
	 * [Contrasting power] integer between 2 and 10 - default 2
	 * [Weakening Threshold] double between 1 (Weaken everything that is not perfect clarity) and 0 (Weaken nothing) : default 0.025
	 * [Weakening reduction factor] Integer between 1 (no reduction - full weakening) and 100 (Insignificant weakening) : default = 8
	 * SMB can be run in 3 modes - Learn, Enhance and Recommend
	 * Enhance mode receives a URL that is assumed to contain 
	 * a schema pair, a list of matchers and a set of Similarity Matrices
	 * created by these matchers.
	 * SMB returns to this path enhanced matrices according to run-time predictions
	 * on these interim results w.r.t the final correspondence. 
	 * 
	 * Recommend mode receives a schema pair and a list of matchers
	 * SMB returns to this path a recommended weighting of the 
	 * matcher ensemble according to previous training runs.  
	 * 
	 * Learn mode receives a schema pair, a correspondence list
	 * and an exact match. SMB classifies this example and uses it to 
	 * update it's internal database and recommended ensemble weights 
	 * by training over all schema pairs belonging to the same class of this new example.
	 * 
	 *  When running the system in Recommend and Learn modes the user must supply a system code to identify the matching system in SMB database
	 */
	
	public SMB() {
		// load from properties file
		Properties pMap = PropertyLoader.loadProperties("resources");
		db = new DBInterface(Integer.parseInt((String)pMap.get("dbmstype")),(String)pMap.get("host"),(String)pMap.get("dbname"),(String)pMap.get("username"),(String)pMap.get("pwd"));
		clarityAlpha = Double.parseDouble((String)pMap.get("clarityAlpha"));
		clarityBeta =  Double.parseDouble((String) pMap.get("clarityBeta"));
		ws = new SMB.WorkingSet();
		tmpPath = (String)pMap.get("tempFilePath");
	}
	
	/**
	 * Supplied a path to a directory, this method validates the existence of the files in the 
	 * interface specification and the number of tab delimited field to be the same as expected
	 * Returns the validated files in a HashMap of Name->File 
	 * @param path - path of directory where interface files are found
	 * @param ReccomendMode - Validate file structure vs. recommend mode specs or Learn mode specs
	 */
	public HashMap<String,File> validateFS(String path, HashMap<String, Integer> specs) {
		HashMap<String,File> fileMap = new HashMap<String,File>();
		
		//Check arguments
		if (path == null)
		{
			System.err.print("No path supplied");
			System.exit(0);
		}
		// check path
		File filePath = new File(path);
		if (filePath==null)
		{
			System.err.print("Invalid path");
			System.exit(0);
		}
		File[] aSubDirectories = filePath.listFiles();
		if (aSubDirectories == null) 
	    {
	    	System.err.print("No files in path");
			System.exit(0);
	    }
		
		// Check files in path according to relevant specs
	      for (int i = 0; i < aSubDirectories.length; i++){
	    	if (specs.containsKey(aSubDirectories[i].getName()))
	    	{
	    		int fNum = specs.get(aSubDirectories[i].getName());
	    		validateF(aSubDirectories[i],fNum);
	    		fileMap.put(aSubDirectories[i].getName(), aSubDirectories[i]);
	    	}
	      }
	      if (fileMap.size() != specs.size())
	      {
	    	System.err.print("Invalid no. of files, found:" + fileMap.size() + " expected:" + specs.size());
			System.exit(0);
	      }
	      return fileMap;
	}
	
	private void validateF(File f,Integer fNum)
	{
		BufferedReader readbuffer;
		String strRead = "";
		String splitArray[];
		try {
			readbuffer = new BufferedReader(new FileReader(f.getPath()));
			strRead=readbuffer.readLine();
			if (strRead == null)
			{
				System.err.print("Empty file: " + f.getName());
    			// TODO replace with a later error check for empty file
				System.exit(0);
			}
			splitArray = strRead.split("\t");
			if (splitArray.length != fNum)
    		{
    			System.err.print("Invalid no. of fields in file: " + f.getName() + ", found:" + splitArray.length + " expected: " + fNum);
    			System.exit(0);
    		}
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			System.err.print("Error reading file: " + f.getName());
			e.printStackTrace();
		}
	}
	
	private ArrayList<String[]> readFile(File f)
	{
		BufferedReader readbuffer;
		String strRead;
		String splitArray[];
		ArrayList<String[]> res = new ArrayList<String[]>();
		try {
			readbuffer = new BufferedReader(new FileReader(f.getPath()));
			strRead=readbuffer.readLine();
			while (strRead != null){
				splitArray = strRead.split("\t");
	    		res.add(splitArray);
	    		strRead=readbuffer.readLine();
				}
		
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public void learnMode()
	{
		//Initialize learn mode specs
		specs.put("Schema.tab",13);
		specs.put("Item.tab",6);
		specs.put("BasicConfigurations.tab",2);
		specs.put("CorrespondenceResult.tab",5);
		specs.put("ExactMatch.tab",4);
		lws = new LearnWorkingSet();
	}
	
	public void enhanceMode()
	{
		//Initialize enhance mode specs
		specs.put("Schema.tab",13);
		specs.put("Item.tab",6);
		specs.put("BasicConfigurations.tab",2);
		specs.put("MatchingResult.tab",6);
	}
	
	public void recommendMode()
	{
		//Initialize recommend mode specs
		specs.put("Schema.tab",13);
		specs.put("Item.tab",6);
		specs.put("BasicConfigurations.tab",2);
		
	}
	
	/**
	 * Helper class for SMB_service, used to store information on schemata and calling system 
	 * @author Tomer Sagi
	 *
	 */
	private class WorkingSet
	{
		
		public WorkingSet()
		{
			candidateSchema = new HashMap<String,Long>();
			targetSchema = new HashMap<String,Long>();
			candidateSchemaItems = new HashMap<Long,String>();
			targetSchemaItems = new HashMap<Long,String>();
			basicConfigurations = new HashMap<Long, String>();
			similarityMatrices = new HashMap<Long, SimilarityMatrix>();
		}
		HashMap<String, Long> candidateSchema; //Candidate Schema properties
		HashMap<String, Long> targetSchema; //Target Schema properties
		HashMap<Long, String> candidateSchemaItems; //Each item identified by an ID and characterized by a HashMap of properties.
		HashMap<Long, String> targetSchemaItems;
		HashMap<Long, String> basicConfigurations; //ID and name of matching system configurations
		HashMap<Long, SimilarityMatrix> similarityMatrices; //Configuration ID and similarity matrices (used only in Enhance mode)
	}
			
	DBInterface db;
	String tmpPath;
	HashMap<String,Integer> specs = new HashMap<String,Integer>();
	WorkingSet ws;
	LearnWorkingSet lws;
	double clarityAlpha = 0.5;
	double clarityBeta = 0.5;
	double weberPower = 2; 
	double weakenThreshold = 0.15;
	double weakenReduction = 10;
	int sysCode;
	
	private static String[] schemaPropertyTemplate = {"ID","Name","Source","Lang","isReal","OriginalML","MaxHeight","SubclassRels","AssocRels","Atts","Classes","VisibleItems","Instances"};
	//private static String[] schemaItemPropertyTemplate = {"SchemaID","ItemID","DataType","Name","Composite","Partial"}; Depreciated, moved to a simpler mode where items are only identified by ID and NAME 
	
	public static void main(String[] args) {
		//TODO add class interface instead of using main
		SMB smb = new SMB(); 
		if (args[0].equalsIgnoreCase("E")) //Enhance mode 
		{
			smb.enhanceMode();
			loadWorkingSet(args[1], smb,true);
			
			//Get optional parameters from args
			if (args.length > 3) smb.weberPower = Double.parseDouble(args[3]);
			if (args.length > 4) smb.weakenThreshold = Double.parseDouble(args[4]);
			if (args.length > 5) smb.weakenReduction = Double.parseDouble(args[5]);
			
			//Enhance similarity matrices using matching performance predictors
			Iterator<Long> it = smb.ws.similarityMatrices.keySet().iterator();
			while (it.hasNext())
			{
				SimilarityMatrix tmpM = smb.ws.similarityMatrices.get(it.next());
				clarityS(tmpM,smb.clarityAlpha,smb.clarityBeta,smb.weberPower,smb.weakenThreshold, smb.weakenReduction);
				
			}
			
			//output enhanced Matrices to input path
			outputMatricesToFile(args[1], smb);
			System.out.println ("done enhancing");
			
		}
		else if (args[0].equalsIgnoreCase("L")) //Learn mode
		{	
			if (!smb.setSystem(args[2])) System.exit(0);
			smb.learnMode();
			// Load files into data structure
			loadLearnWorkingSet(args[1], smb);

			//Classify schema pairs, record classes, load into DB as new examples of the class
			ArrayList<Integer> classes = new ArrayList<Integer>();  
			for (int i=0;i<smb.lws.schemaPairList.size();i++)
			{
				Classify(smb.lws.schemaPairList.get(i));
				if (!classes.contains(smb.lws.schemaPairList.get(i).taskClass))
					classes.add(smb.lws.schemaPairList.get(i).taskClass);
				smb.loadSchemaPairToDB(smb.lws.schemaPairList.get(i));
			}
			
			//for each class of schema pairs Lookup schema pairs of this class in DB and load into lws
			for (int i=0;i<classes.size();i++)
			{
				smb.extractClassPairsFromDB(classes.get(i),smb.tmpPath);
				SMB tmpSmb = new SMB();
				tmpSmb.learnMode();
				loadLearnWorkingSet(smb.tmpPath, tmpSmb);
				
				try {
					// Train on all pairs and output optimal ensemble weighting
					SMBTrain.SerializeLws(tmpSmb.lws, "exampleLWS.txt");
					SMBTrain smbTrainer = new SMBTrain(tmpSmb.lws);
					HashMap<Long,Double> res = smbTrainer.Train();
					//Update DB with result of training
					smb.loadWeightedEnsembleToDB(res,classes.get(i));
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
	
			System.out.print("Training Completed Sucessfully");
		}
		else if (args[0].equalsIgnoreCase("R")) //Recommend mode 
		{
			if (!smb.setSystem(args[2])) System.exit(0);
			smb.recommendMode();
			loadWorkingSet(args[1], smb, false);
			SchemaPair sp = new SchemaPair(new Schema(smb.ws.candidateSchema.get("ID"),"",smb.ws.candidateSchema,smb.ws.candidateSchemaItems),new Schema(smb.ws.targetSchema.get("ID"),"",smb.ws.targetSchema,smb.ws.targetSchemaItems));
			
			//Classify schema pair
			Classify(sp);
			
			//Lookup ensemble weighting for this class 
			HashMap<Long,Double> weightedEnsemble = smb.lookupEnsemble(sp.taskClass);
			
			//output ensemble weighting to supplied URL
			outputWeightsToFile(args[1], smb, weightedEnsemble);	
		}
		else
		{
			System.err.print("Invalid first argument supplied, use R for Recommend mode, E for Enhance mode and L for Learn mode");
			System.exit(0);
		}
		
		

	}

	/**
	 * Checks in the database for the system code supplied, sets the local field
	 * sysCode with the supplied sysCode
	 * @param sysCode : System Code registered in the Database
	 * @return : returns true if succeeded, false otherwise
	 */
	private boolean setSystem(String systemCode) {
		sysCode = Integer.parseInt(systemCode);
		HashMap<Integer,String> systems = getRegisteredSystems();
		if (systems.containsKey(sysCode)) return true;
		//logical else
		System.err.print("Unrecognized Matching System code supplied. The following matching systems are registered with SMB:\n");		
		Iterator<Integer> iterator = systems.keySet().iterator();   
	    while (iterator.hasNext()) {   
		   String key = iterator.next().toString();   
		   String value = systems.get(key).toString();   		    
		   System.err.println(key + " " + value);   
		}
		return false;
	}

	/**
	 * Connects to the database and returns the list of registered matching systems
	 * @return List of matching systems
	 */
	private HashMap<Integer, String> getRegisteredSystems() {
		String sql = "SELECT * FROM systems";
		ArrayList<String[]> dbRes = db.runSelectQuery(sql,2);
		HashMap<Integer, String> res = new HashMap<Integer, String>();
		for (int i=0;i<dbRes.size();i++) res.put(Integer.parseInt(dbRes.get(i)[0]),dbRes.get(i)[1]);
		return res;
	}

	/**
	 * Loads a weighted ensemble for a class to the database. If the class exists 
	 * for the current matching system, deletes current ensemble weights before replacing
	 * with new ones.
	 * If class does not exist, creates it.
	 * 
	 * @param ensembleWeights
	 * @param classCode
	 */
	private void loadWeightedEnsembleToDB(HashMap<Long, Double> ensembleWeights, Integer classCode) {
		
		// Delete existing weights if exist
		String sql = "DELETE FROM matcherweights WHERE sysCode=" + sysCode + " AND classCode=" + classCode + ";";
		db.runDeleteQuery(sql);
		// Insert new weights
		HashMap<Field, Object> values = new HashMap<Field, Object>();
		Field mID = new Field("mID", Field.FieldType.LONG);
		Field w = new Field("weight", Field.FieldType.DOUBLE );
		values.put(new Field("sysCode", Field.FieldType.INT ), sysCode);
		values.put(new Field("classCode", Field.FieldType.INT ), classCode);
		
		Iterator<Long> iterator = ensembleWeights.keySet().iterator();   
	    while (iterator.hasNext()) 
	    {
	    	Long key = iterator.next();
	    	values.put(mID , key);
	    	values.put(w, ensembleWeights.get(key));
	    	db.insertSingleRow(values, "matcherweights");
	    }
	}

	/**
	 * This class extracts schema pairs from the database into files that are deposited in the supplied path
	 * Files are in SMB interface format and include Schema.tab, Item.tab, BasicConfigurations.tab
	 * CorrespondenceResult.tab and ExactMatch.tab
	 * @param classCode : class for which the schema pairs are extracted
	 * @param path : URL at which the files are deposited
	 */
	private void extractClassPairsFromDB(Integer classCode, String path) {
		//Extract Schemata of relevant schema pairs and output to Schema.tab
		String sql = "SELECT SchemaID,SchemaName,Source,Lang,isReal,OriginalML,MaxHeight,SubclassRels,AssocRels,Atts,Classes, VisibleItems,Instances " +
				" FROM schemata, schemapairs " +
				" WHERE (schemata.schemaID = schemapairs.candidateSchemaID OR schemata.schemaID = schemapairs.targetSchemaID) AND classCode = " + classCode + " AND schemata.sysCode = " + sysCode + " AND schemapairs.sysCode = " + sysCode + ";";
		extractFromDB(sql,"Schema.tab",13,path);
		
		//Extract Items of relevant schema pairs and output to Item.tab
		sql = "SELECT DISTINCT terms.SchemaID, Tid, TType , TName, KnownComposite, KnownPartial" +
		" FROM schemapairs, schemata , terms " +
		" WHERE (schemata.schemaID = schemapairs.candidateSchemaID OR schemata.schemaID = schemapairs.targetSchemaID)" +
		" AND schemapairs.classCode = " + classCode + " AND schemapairs.sysCode = " + sysCode +
		" AND schemata.sysCode=" + sysCode + " AND terms.sysCode=" + sysCode + ";";
		extractFromDB(sql,"Item.tab",6,path);
		
		//Extract Mappings of relevant schema pairs and current matching system and output to CorrespondenceResult.tab
		sql = "SELECT MatcherID, schemapairs.candidateSchemaID,	candidateTermID, schemapairs.targetSchemaID, targetTermID" +
				" FROM schemapairs, mapping" +
				" WHERE mapping.candidateSchemaID = schemapairs.candidateSchemaID AND mapping.targetSchemaID = schemapairs.targetSchemaID AND mapping.sysCode = " + sysCode +
				" AND schemapairs.sysCode = " + sysCode + ";";
		extractFromDB(sql,"CorrespondenceResult.tab",5,path);
		
		//Extract Matchers in use in the mappings and output to BasicConfigurations.tab
		sql = "SELECT DISTINCT matchers.MatcherID, MatcherName" +
		" FROM matchers, mapping" +
		" WHERE mapping.MatcherID = matchers.MatcherID AND mapping.sysCode = " + sysCode +
		" AND matchers.sysCode = " + sysCode + ";";
		extractFromDB(sql,"BasicConfigurations.tab",2,path);
		
		//Extract ExactMatches of relevant schema pairs and output to ExactMatch.tab
		sql = "SELECT exactmatch.candidateSchemaID,	candidateTermID, exactmatch.targetSchemaID, targetTermID" +
		" FROM schemapairs, exactmatch" +
		" WHERE exactmatch.candidateSchemaID = schemapairs.candidateSchemaID AND exactmatch.targetSchemaID = schemapairs.targetSchemaID " +
		" AND exactmatch.sysCode = " + sysCode + " AND schemapairs.sysCode = " + sysCode + ";";
		extractFromDB(sql,"ExactMatch.tab",4,path);
		
	}

	/**
	 * Extracts an sql statement from the database as a tab delimited file
	 * @param sql : Select statement returning requried records
	 * @param fileName : name of file to output
	 * @param numFields : number of fields in the file
	 * @param path : path to put the file at
	 */
	private void extractFromDB(String sql, String fileName, int numFields, String path) 
	{
		ArrayList<String[]> dbRes = db.runSelectQuery(sql, numFields);
		DataFile write = DataFile.createWriter("8859_1", false);
		write.setDataFormat(new TabFormat());
		File outputPath = new File(path);
		File outputFile = new File(outputPath, fileName);
		outputFile.delete();
		try 
		{
			write.open(outputFile);
			for (int i=0;i<dbRes.size();i++)
			{
				DataRow row = write.next();
				String[] rowStr = dbRes.get(i);
				for (int j=0;j<numFields;j++)
					row.add(rowStr[j]);
			}
			write.close();
			dbRes.clear();
		} catch (IOException e) {
			
			e.printStackTrace();
			System.exit(0);
		}
		
	}

	/**
	 * Loads a schema pair into the database by adding the schemata, pair, items, correspondences and exact matches
	 * @param schemaPair 
	 */
	private void loadSchemaPairToDB(SchemaPair schemaPair) 
	{
		// Delete Schemata if exist and (re)insert to DB
		String sql = "DELETE FROM schemata " +
				"WHERE (SchemaID=" + schemaPair.candidateSchema.schemaID + " OR " +
				"SchemaID=" + schemaPair.targetSchema.schemaID + ") AND sysCode = " + sysCode + ";";
		db.runDeleteQuery(sql);
		
		HashMap<Field, Object> values = new HashMap<Field, Object>();
		Field fsysCode = new Field("sysCode",FieldType.INT);
		values.put(fsysCode, sysCode);
		Field SchemaID = new Field("SchemaID", Field.FieldType.LONG);
		values.put(SchemaID, schemaPair.candidateSchema.schemaID);
		Field SchemaName = new Field("SchemaName",Field.FieldType.STRING);
		values.put(SchemaName, schemaPair.candidateSchema.schemaName);
		for (int i=2;i<schemaPropertyTemplate.length;i++)
			values.put(new Field(schemaPropertyTemplate[i],FieldType.LONG), schemaPair.candidateSchema.features.get(schemaPropertyTemplate[i]));
		db.insertSingleRow(values, "schemata");
		
		values.clear();
		values.put(fsysCode, sysCode);
		values.put(SchemaID, schemaPair.targetSchema.schemaID);
		values.put(SchemaName, schemaPair.targetSchema.schemaName);
		for (int i=2;i<schemaPropertyTemplate.length;i++)
			values.put(new Field(schemaPropertyTemplate[i],FieldType.LONG), schemaPair.targetSchema.features.get(schemaPropertyTemplate[i]));
		db.insertSingleRow(values, "schemata");
		
		// Delete schemaPair (if exists) and (re) insert to DB
		sql = "DELETE FROM schemapairs " +
			"WHERE candidateSchemaID=" + schemaPair.candidateSchema.schemaID + " AND " +
					"targetSchemaID=" + schemaPair.targetSchema.schemaID + " AND sysCode = " + sysCode + ";";
		db.runDeleteQuery(sql);
		values.clear();
		values.put(fsysCode, sysCode);
		values.put(new Field("candidateSchemaID",FieldType.LONG), schemaPair.candidateSchema.schemaID);
		values.put(new Field("targetSchemaID",FieldType.LONG), schemaPair.targetSchema.schemaID);
		values.put(new Field("classCode",FieldType.INT), schemaPair.taskClass);
		db.insertSingleRow(values, "schemapairs");
		
		// Delete Terms (if exists) and (re) insert to DB
		sql = "DELETE FROM terms " +
		"WHERE (SchemaID=" + schemaPair.candidateSchema.schemaID + " OR " +
		"SchemaID=" + schemaPair.targetSchema.schemaID + ") AND sysCode = " + sysCode + ";";
		db.runDeleteQuery(sql);
		values.clear();
		values.put(fsysCode, sysCode);
		values.put(SchemaID, schemaPair.candidateSchema.schemaID);
		Field tID = new Field("Tid",FieldType.LONG);
		Field tName = new Field("TName",FieldType.STRING);
		Iterator<Long> it = schemaPair.candidateSchema.terms.keySet().iterator();
		while (it.hasNext())
		{
			long id = it.next();
			values.put(tID, id);
			values.put(tName,schemaPair.candidateSchema.terms.get(id));
			db.insertSingleRow(values, "terms");
		}
		
		values.clear();
		values.put(fsysCode, sysCode);
		values.put(SchemaID, schemaPair.targetSchema.schemaID);
		it = schemaPair.targetSchema.terms.keySet().iterator();
		while (it.hasNext())
		{
			long id = it.next();
			values.put(tID, id);
			values.put(tName,schemaPair.targetSchema.terms.get(id));
			db.insertSingleRow(values, "terms");
		}
		// Add any matchers that do not exist in DB
		sql = "SELECT MatcherID FROM matchers WHERE sysCode = " + sysCode + ";";
		ArrayList<String[]> dbRes = db.runSelectQuery(sql, 1);
		Set<Long> m = lws.basicConfigurations.keySet();
		for (int i=0;i<dbRes.size();i++)
			if (m.contains(Long.parseLong(dbRes.get(i)[0])))
				m.remove(Long.parseLong(dbRes.get(i)[0]));
		
		Field mID = new Field("MatcherID",FieldType.LONG);
		Field mName = new Field("MatcherName",FieldType.STRING);
		it = m.iterator();
		while (it.hasNext())
		{
			long mKey = it.next();
			values.clear();
			values.put(mID, mKey);
			values.put(mName, lws.basicConfigurations.get(mKey));
			values.put(fsysCode, sysCode);
			db.insertSingleRow(values, "matchers");
		}
		
		// Delete Correspondences (if exist) and (re) insert to DB non 0 correspondences
		sql = "DELETE FROM mapping " +
		"WHERE candidateSchemaID=" + schemaPair.candidateSchema.schemaID + " AND " +
				"targetSchemaID=" + schemaPair.targetSchema.schemaID + " AND sysCode = " + sysCode + ";";
		db.runDeleteQuery(sql);
		values.clear();
		values.put(fsysCode, sysCode);
		values.put(new Field("candidateSchemaID",FieldType.LONG), schemaPair.candidateSchema.schemaID);
		values.put(new Field("targetSchemaID",FieldType.LONG), schemaPair.targetSchema.schemaID);
		Field candidateTermID = new Field("candidateTermID",FieldType.LONG);
		Field targetTermID = new Field("targetTermID",FieldType.LONG);
		it = schemaPair.correspondenceSet.keySet().iterator();
		while (it.hasNext())
		{
			Long keyMID = it.next();
			SimilarityMatrix SM = schemaPair.correspondenceSet.get(keyMID);
			Iterator<Long> cIt = SM.candidateTermMap.keySet().iterator();
			while (cIt.hasNext())
			{
				long cTermID = cIt.next();
				Iterator<Long> tIt = SM.targetTermMap.keySet().iterator();
				while (tIt.hasNext())
				{
					long tTermID = tIt.next();
					double val = SM.similarityM.getElement(SM.candidateTermMap.get(cTermID), SM.targetTermMap.get(tTermID));
					if (val!=0)
					{
						values.put(candidateTermID, cTermID);
						values.put(targetTermID, tTermID);
						values.put(mID, keyMID);
						db.insertSingleRow(values, "mapping");
					}										
				}
			}
		}

	
		// Delete ExactMatch (if exists) and (re) insert to DB non 0 correspondences
		sql = "DELETE FROM exactmatch " +
		"WHERE candidateSchemaID=" + schemaPair.candidateSchema.schemaID + " AND " +
				"targetSchemaID=" + schemaPair.targetSchema.schemaID + " AND sysCode = " + sysCode + ";";
		db.runDeleteQuery(sql);
		values.remove(mID);
		SimilarityMatrix EM = schemaPair.exactMatch;
		Iterator<Long> cIt = EM.candidateTermMap.keySet().iterator();
		while (cIt.hasNext())
		{
			long cTermID = cIt.next();
			Iterator<Long> tIt = EM.targetTermMap.keySet().iterator();
			while (tIt.hasNext())
			{
				long tTermID = tIt.next();
				double val = EM.similarityM.getElement(EM.candidateTermMap.get(cTermID), EM.targetTermMap.get(tTermID));
				if (val!=0)
				{
					values.put(candidateTermID, cTermID);
					values.put(targetTermID, tTermID);
					db.insertSingleRow(values, "exactmatch");
				}										
			}
		}

		
	}

	/**
	 * Classifies a schema pair into a matching task class. The class is represented as an
	 * integer and is documented within the schema pair instance
	 * @param schemaPair Schema Matching task to be classified
	 */
	private static void Classify(SchemaPair schemaPair) {
		// TODO method stub : Choose classification / Clustering algorithm and find a good implementation of it
		long candItemNum = schemaPair.candidateSchema.features.get("VisibleItems");
		long targItemNum = schemaPair.targetSchema.features.get("VisibleItems");
		double itemNoDiff = Math.abs(candItemNum-targItemNum)/(Math.max(candItemNum,targItemNum) == 0 ? 1 : Math.max(candItemNum,targItemNum));
		if (itemNoDiff < 0.25)
			schemaPair.taskClass = 2;
		else
			if (itemNoDiff < 0.5)
				schemaPair.taskClass = 3;
			else
				schemaPair.taskClass = 1;
	}

	private static void loadLearnWorkingSet(String strFilePath, SMB smb) 
	{
		HashMap<String, File> fileMap;
		//Validate URL and file structure
		fileMap = smb.validateFS(strFilePath,smb.specs);
		
		//Read schemata from files
		ArrayList<String[]> schemata = smb.readFile(fileMap.get("Schema.tab"));
		String[] currentSchema;
		for (int i=0;i<schemata.size();i++)
		{
			currentSchema = schemata.get(i);
			HashMap<String,Long> features = new HashMap<String,Long>();
			for (int j = 0; j < currentSchema.length;j++)
				if (j>1) //Skip Schema Name and ID
					features.put(schemaPropertyTemplate[j], Long.parseLong(currentSchema[j]));
			smb.lws.schemata.put(Long.parseLong(currentSchema[0]),new Schema(Long.parseLong(currentSchema[0]), currentSchema[1], features, new HashMap<Long,String>()));
		}
			
		//Read items from files
		ArrayList<String[]> itemArray = smb.readFile(fileMap.get("Item.tab"));
		String[] itemString;
		for (int i=0; i<itemArray.size();i++)
		{
			itemString = itemArray.get(i);
			if (smb.lws.schemata.containsKey(Long.parseLong(itemString[0])))
			{
				Schema s = smb.lws.schemata.get(Long.parseLong(itemString[0]));
				if (s.terms.containsKey(Long.parseLong(itemString[1])))
					System.err.print("Duplicate Item ID:" + itemString[1] + " for Item " + itemString[1] + " " + itemString[3] + ". Item will be skipped \n");
				else
					s.terms.put(Long.parseLong(itemString[1]), itemString[3]);
			}
			else //Schema ID of item doesn't match any schema
				System.err.print("Unrecognized Schema ID:" + itemString[0] + " for Item " + itemString[1] + " " + itemString[3] + ". Item will be skipped \n");
		}
		
		//Read basic configurations from file
		ArrayList<String[]> configArray = smb.readFile(fileMap.get("BasicConfigurations.tab"));
		for (int i=0; i<configArray.size();i++)
			smb.lws.basicConfigurations.put(Long.parseLong(configArray.get(i)[0]), configArray.get(i)[1]);
		
		//Read Exact Matches from file
		ArrayList<String[]> matrices = smb.readFile(fileMap.get("ExactMatch.tab"));
		String strExactMatch[];
		for (int i=0;i<matrices.size();i++)
		{
			strExactMatch = matrices.get(i);
			//Validate item and schema id's
			if (!smb.lws.schemata.containsKey(Long.parseLong(strExactMatch[0])))
			{
				System.err.print("Unrecognized candidate schema ID in exact match row:" + strExactMatch[0] + " " + strExactMatch[1]+ " " + strExactMatch[2] + "etc... \n");
				continue;
			}
			
			if (!smb.lws.schemata.containsKey(Long.parseLong(strExactMatch[2])))
			{
				System.err.print("Unrecognized target schema ID in exact match row:" + strExactMatch[0] + " " + strExactMatch[1]+ " " + strExactMatch[2] + " " + strExactMatch[3]+ " "+ "etc... \n");
				continue;
			}
			
			Schema candS =  smb.lws.schemata.get(Long.parseLong(strExactMatch[0]));
			if (!candS.terms.containsKey(Long.parseLong(strExactMatch[1])))
			{
				System.err.print("Unrecognized candidate schema item ID in exact match row:" + strExactMatch[0] + " " + strExactMatch[1]+ " " + strExactMatch[2] + " " + strExactMatch[3]+ " etc... \n");
				continue;
			}
			
			Schema targetS =  smb.lws.schemata.get(Long.parseLong(strExactMatch[2]));
			if (!targetS.terms.containsKey(Long.parseLong(strExactMatch[3])))
			{
				System.err.print("Unrecognized target schema item ID in exact match row:" + strExactMatch[0] + " " + strExactMatch[1]+ " " + strExactMatch[2] + " " + strExactMatch[3]+ "etc... \n");
				continue;
			}
			
			//Locate Schema Pair, if doesn't exist error message
			SchemaPair sp = null;
			Iterator<SchemaPair> itr = smb.lws.schemaPairList.iterator();
			while (itr.hasNext())
			{
				sp = itr.next();
				if (sp.candidateSchema.schemaID == Long.parseLong(strExactMatch[0]))
					break;
				if (!itr.hasNext()) sp = null;
			}
			if (sp == null)
			{
				sp = new SchemaPair(candS,targetS);
				smb.lws.schemaPairList.add(sp);
			}
				
			
			//Update entry in matrix, update item to matrix position map
			String[] tmp = new String[5];
			tmp[0] = "0";
			for (int ai=1;ai<5;ai++) tmp[ai] = strExactMatch[ai-1];
			updateCorrespondence(tmp, sp.exactMatch);
		}
		
		//Read Correspondences from file
		matrices = smb.readFile(fileMap.get("CorrespondenceResult.tab"));
		String strCorrespondence[];
		for (int i=0;i<matrices.size();i++)
		{
			strCorrespondence = matrices.get(i);
			//Validate configuration ID
			if (!smb.lws.basicConfigurations.containsKey(Long.parseLong(strCorrespondence[0])))
			{
				System.err.print("Unrecognized configuration ID in correspondence row:" + strCorrespondence[0] + " " + strCorrespondence[1]+ " " + strCorrespondence[2] + "etc... \n");
				continue;
			}
			
			//Validate item and schema ids
			if (!smb.lws.schemata.containsKey(Long.parseLong(strCorrespondence[1])))
			{
				System.err.print("Unrecognized candidate schema ID in correspondence row:" + strCorrespondence[0] + " " + strCorrespondence[1]+ " " + strCorrespondence[2] + "etc... \n");
				continue;
			}
			
			if (!smb.lws.schemata.containsKey(Long.parseLong(strCorrespondence[3])))
			{
				System.err.print("Unrecognized target schema ID in correspondence row:" + strCorrespondence[0] + " " + strCorrespondence[1]+ " " + strCorrespondence[2] + " " + strCorrespondence[3]+ " " + strCorrespondence[4]+ "etc... \n");
				continue;
			}
			
			Schema candS =  smb.lws.schemata.get(Long.parseLong(strCorrespondence[1]));
			if (!candS.terms.containsKey(Long.parseLong(strCorrespondence[2])))
			{
				System.err.print("Unrecognized candidate schema item ID in correspondence row:" + strCorrespondence[0] + " " + strCorrespondence[1]+ " " + strCorrespondence[2] + " " + strCorrespondence[3]+ " " + strCorrespondence[4]+ "etc... \n");
				continue;
			}
			
			Schema targetS =  smb.lws.schemata.get(Long.parseLong(strCorrespondence[3]));
			if (!targetS.terms.containsKey(Long.parseLong(strCorrespondence[4])))
			{
				System.err.print("Unrecognized target schema item ID in correspondence row:" + strCorrespondence[0] + " " + strCorrespondence[1]+ " " + strCorrespondence[2] + " " + strCorrespondence[3]+ " " + strCorrespondence[4]+ "etc... \n");
				continue;
			}
			
			//Locate Schema Pair, if doesn't exist yet, error message
			SchemaPair sp = null;
			Iterator<SchemaPair> itr = smb.lws.schemaPairList.iterator();
			while (itr.hasNext())
			{
				sp = itr.next();
				if (sp.candidateSchema.schemaID == Long.parseLong(strCorrespondence[1]))
					break;
				if (!itr.hasNext()) sp = null;
			}
			if (sp == null) 
			{
				System.err.print("Schema Pair doesn't have an exactmatch, cannot train on row:" + strCorrespondence[0] + " " + strCorrespondence[1]+ " " + strCorrespondence[2] + " " + strCorrespondence[3]+ " " + strCorrespondence[4]+ "etc... \n");
				break;
			}
			
			//Find correspondence matrix, create if doesn't exist
			SimilarityMatrix tempSim;
			if (!sp.correspondenceSet.containsKey(Long.parseLong(strCorrespondence[0])))
			{
				tempSim = new SimilarityMatrix(sp.candidateSchema.schemaID,sp.targetSchema.schemaID,new HashMap<Long,Integer>(),new HashMap<Long,Integer>(),new double[sp.candidateSchema.terms.size()][sp.targetSchema.terms.size()]);
				sp.correspondenceSet.put(Long.parseLong(strCorrespondence[0]), tempSim);
			}			
			else
				tempSim = sp.correspondenceSet.get(Long.parseLong(strCorrespondence[0]));
			
			//Update entry in matrix, update item to matrix position map
			updateCorrespondence(strCorrespondence, tempSim);
				
		}

	}

	/**
	 * Loads working set. Validates file structures 
	 * Reads data from files and initializes working set. 
	 * Runs basic data quality checks.
	 * @param strFilePath
	 * @param smb
	 * @param loadSimilarityM : If true, will load similarity matrices (useful for enhance mode)
	 */
	private static void loadWorkingSet(String strFilePath, SMB smb,boolean loadSimilarityM) {
		HashMap<String, File> fileMap;
		//Validate URL and file structure
		fileMap = smb.validateFS(strFilePath,smb.specs);
		
		//Read schemata from files
		ArrayList<String[]> schemata = smb.readFile(fileMap.get("Schema.tab"));
		if (schemata.size()>2) System.err.print("Warning: More than 2 schemata supplied, only first 2 schemata will be processed as a pair");
		String schemaProperties[] = schemata.get(0);
		for (int i = 0; i < schemaProperties.length;i++)
			if (i!=1) //Skip Schema Name
				smb.ws.candidateSchema.put(schemaPropertyTemplate[i], Long.parseLong(schemaProperties[i]));
		
		schemaProperties = schemata.get(1);
		if (smb.ws.candidateSchema.get("ID") == Long.parseLong(schemaProperties[0]))
		{
			System.err.print("Error: Target schema is the same as candidate schema");
			System.exit(0);
		}
			
		for (int i = 0; i < schemaProperties.length;i++)
			if (i!=1) //Skip Schema Name
				smb.ws.targetSchema.put(schemaPropertyTemplate[i],Long.parseLong(schemaProperties[i]));
		
		//Read items from files
		ArrayList<String[]> itemArray = smb.readFile(fileMap.get("Item.tab"));
		String[] itemString;
		for (int i=0; i<itemArray.size();i++)
		{
			itemString = itemArray.get(i);
			if (Long.parseLong(itemString[0])==smb.ws.candidateSchema.get("ID"))
				insertItem(smb.ws.candidateSchemaItems, itemString);
			else if (Long.parseLong(itemString[0])==smb.ws.targetSchema.get("ID"))
				insertItem(smb.ws.targetSchemaItems, itemString);
			else //Schema ID of item doesn't match neither candidate nor target schema
				System.err.print("Unrecognized Schema ID:" + itemString[0] + " for Item " + itemString[1] + " " + itemString[3] + ". Item will be skipped \n");
		}
		
		//Read basic configurations from file
		ArrayList<String[]> configArray = smb.readFile(fileMap.get("BasicConfigurations.tab"));
		for (int i=0; i<configArray.size();i++)
			smb.ws.basicConfigurations.put(Long.parseLong(configArray.get(i)[0]), configArray.get(i)[1]);
		
		if (loadSimilarityM)
		{
			//Read Similarity Matrices from files
			ArrayList<String[]> matrices = smb.readFile(fileMap.get("MatchingResult.tab"));
			String strCorrespondence[];
			for (int i=0;i<matrices.size();i++)
			{
				strCorrespondence = matrices.get(i);
				if (!smb.ws.similarityMatrices.containsKey(Long.parseLong(strCorrespondence[0])))
				//First time this configuration is encountered in the file, create a similarity matrix for it
				{
					double[][] newSimDouble = new double[smb.ws.candidateSchemaItems.size()][smb.ws.targetSchemaItems.size()];
					HashMap<Long,Integer> candMap = new HashMap<Long,Integer>();
					HashMap<Long,Integer> targMap = new HashMap<Long,Integer>();
					SimilarityMatrix newSim = new SimilarityMatrix(Long.parseLong(strCorrespondence[1]), Long.parseLong(strCorrespondence[3]),candMap,targMap , newSimDouble);
					smb.ws.similarityMatrices.put(Long.parseLong(strCorrespondence[0]),newSim);
				}
				//Validate item and schema ids
				if (smb.ws.candidateSchema.get("ID")!=Long.parseLong(strCorrespondence[1]))
				{
					System.err.print("Unrecognized candidate schema ID in similarity row:" + strCorrespondence[0] + " " + strCorrespondence[1]+ " " + strCorrespondence[2] + "etc... \n");
					continue;
				}
				
				if (smb.ws.targetSchema.get("ID")!=Long.parseLong(strCorrespondence[3]))
				{
					System.err.print("Unrecognized target schema ID in similarity row:" + strCorrespondence[0] + " " + strCorrespondence[1]+ " " + strCorrespondence[2] + " " + strCorrespondence[3]+ " " + strCorrespondence[4]+ "etc... \n");
					continue;
				}
				
				if (!smb.ws.candidateSchemaItems.containsKey(Long.parseLong(strCorrespondence[2])))
				{
					System.err.print("Unrecognized candidate schema item ID in similarity row:" + strCorrespondence[0] + " " + strCorrespondence[1]+ " " + strCorrespondence[2] + " " + strCorrespondence[3]+ " " + strCorrespondence[4]+ "etc... \n");
					continue;
				}
				
				if (!smb.ws.targetSchemaItems.containsKey(Long.parseLong(strCorrespondence[4])))
				{
					System.err.print("Unrecognized target schema item ID in similarity row:" + strCorrespondence[0] + " " + strCorrespondence[1]+ " " + strCorrespondence[2] + " " + strCorrespondence[3]+ " " + strCorrespondence[4]+ "etc... \n");
					continue;
				}
				
				//Update entry in matrix, update item to matrix position map
				SimilarityMatrix tempSim = smb.ws.similarityMatrices.get(Long.parseLong(strCorrespondence[0]));
				updateCorrespondence(strCorrespondence, tempSim);
			}
			//Shrink matrix - remove rows and columns that are all zero's
			Iterator<Long> it = smb.ws.similarityMatrices.keySet().iterator();
			while (it.hasNext())
				smb.ws.similarityMatrices.get(it.next()).shrink();
		}
		
	}

	/**
	 * Helper method for the loadSorkingSet and LoadLearnWorkingSet Classes
	 * Updates a correspondence / similarity value from a file row
	 * @param strCorrespondence
	 * @param tempSim
	 */
	private static void updateCorrespondence(String[] strCorrespondence, SimilarityMatrix tempSim) {
		//Locate term positions, if not registered yet, update new positions
		Long candTerm = Long.parseLong(strCorrespondence[2]);
		Long targetTerm = Long.parseLong(strCorrespondence[4]);
		if (!tempSim.candidateTermMap.containsKey(candTerm))
			{
				tempSim.lastCandidateItem++;
				tempSim.candidateTermMap.put(candTerm,tempSim.lastCandidateItem);
			}
		if (!tempSim.targetTermMap.containsKey(targetTerm))
			{
				tempSim.lastTargetItem++;
				tempSim.targetTermMap.put(targetTerm,tempSim.lastTargetItem);
			}	
		
		int row = tempSim.candidateTermMap.get(candTerm);
		int col = tempSim.targetTermMap.get(targetTerm);
		if (tempSim.similarityM.getElement(row, col)!=0)
		{
			System.err.print("Duplicate matching in row:" + strCorrespondence[0] + " " + strCorrespondence[1]+ " " + strCorrespondence[2] + " " + strCorrespondence[3]+ " " + strCorrespondence[4]+ "etc... \n");
		}
		
		// update matrix
		Double val = 1.0; //default value for correspondence (0 or 1) matrices
		if (strCorrespondence.length == 6) val = Double.parseDouble(strCorrespondence[5]); //Use row value in similarity matrices
		if (val > 1)
		{
			String rw = strCorrespondence[0] + " " + strCorrespondence[1]+ " " + strCorrespondence[2] + " " + strCorrespondence[3]+ " " + strCorrespondence[4]+ " " + strCorrespondence[5];
			System.err.println("Invalid value detected. Matching confidence values should be between 0 and 1. Row: " + rw);
			System.exit(1);
		}
			
		tempSim.similarityM.setElement(row, col,val );
	}

	/**
	 * Outputs the weighted ensemble to weightedConfigurations.tab
	 * @param strOutputPath : Output path in which file will be created
	 * @param smb : SMB instance containing the configurations
	 * @param weightedEnsemble : HashMap of configurations and their weights
	 */
	private static void outputWeightsToFile(String strOutputPath, SMB smb,
			HashMap<Long, Double> weightedEnsemble) {
		try 
		{
			//Create output file
			DataFile write = DataFile.createWriter("8859_1", false);
			write.setDataFormat(new TabFormat());
			File outputPath = new File(strOutputPath);
			File outputWCFile = new File(outputPath,"weightedConfigurations.tab");
			write.open(outputWCFile);
			
			//Write result
			Set<Long> s = weightedEnsemble.keySet();
			Iterator<Long> itr = s.iterator();
			while (itr.hasNext())
			{
				DataRow row = write.next();
				Long key = (Long) itr.next();
				row.add(smb.ws.candidateSchema.get("ID")); //Candidate schema ID
				row.add(smb.ws.targetSchema.get("ID")); //Target schema ID
				row.add(key.toString()); //Configuration ID
				row.add(weightedEnsemble.get(key).toString()); //Weight
			}
			write.close();
		}
		catch (Exception e)
		{
			System.err.print("Weighted Configuration File Creation Failed \n");  
			e.printStackTrace();
		 }
	}

	/**
	 * Outputs the enhanced matrices to EnhancedSimilarityMatrix.tab filters out any 0 corespondences
	 * @param strOutputPath : Output path in which file will be created
	 * @param smb : SMB instance containing the configurations
	 */
	private static void outputMatricesToFile(String strOutputPath, SMB smb) {
		try 
		{
			DataFile write = DataFile.createWriter("8859_1", false);
			write.setDataFormat(new TabFormat());
			File outputPath = new File(strOutputPath);
			File outputSMFile = new File(outputPath,"EnhancedSimilarityMatrix.tab");
			write.open(outputSMFile);
			Long configurationIDs[] = smb.ws.similarityMatrices.keySet().toArray(new Long[1]);
			for (int m=0;m<configurationIDs.length;m++)
			{
				SimilarityMatrix tmpM = smb.ws.similarityMatrices.get(configurationIDs[m]);
				
				Set<Long> candTermKeySet = tmpM.candidateTermMap.keySet();
				Long candTerms[] = new Long[candTermKeySet.size()]; 
				candTermKeySet.toArray(candTerms);
				
				Set<Long> targTermKeySet = tmpM.targetTermMap.keySet();
				Long targTerms[] = new Long[targTermKeySet.size()]; 
				targTermKeySet.toArray(targTerms);
				double elem = 0;
				for (int i=0;i<candTerms.length;i++)
				{
					for (int j=0;j<targTerms.length;j++)
						{
						elem = tmpM.similarityM.getElement(tmpM.candidateTermMap.get(candTerms[i]), tmpM.targetTermMap.get(targTerms[j]));
						if (elem != 0)
						{
							DataRow row = write.next();
							row.add(Long.toString(configurationIDs[m])); //Configuration ID
							row.add(Long.toString(tmpM.candidateSchemaID)); //Candidate Schema ID
							row.add(Long.toString(candTerms[i])); //Candidate Term ID
							row.add(Long.toString(tmpM.targetSchemaID)); //Target Schema ID
							row.add(Long.toString(targTerms[j])); //Target Term ID
							row.add(Double.toString(tmpM.similarityM.getElement(tmpM.candidateTermMap.get(candTerms[i]), tmpM.targetTermMap.get(targTerms[j])))); //Score
						}
						}
				}
				}
				write.close();
		}
			catch (Exception e)
			  {
				System.err.print("Enhanced Similarity Matrix File Creation Failed");  
				e.printStackTrace();
			  }
	}

	/**
	 * Load configuration weights from DB for supplied classCode
	 * @param classCode : code of matching task class for which the ensemble weights should be looked up 
	 * @return : HashMap of weights for each matcher
	 */
	private HashMap<Long, Double> lookupEnsemble(int classCode) 
			
	{
		String sql = "SELECT mID, weight" +
				"FROM matcherweights" +
				"WHERE sysCode = " + sysCode + "AND classCode = " + classCode + ";";
		ArrayList<String[]> dbRes = db.runSelectQuery(sql, 2);
		HashMap<Long, Double> res = new HashMap<Long, Double>();
		for (int i=0;i<dbRes.size();i++) res.put(Long.parseLong(dbRes.get(i)[0]), Double.parseDouble(dbRes.get(i)[1]));
		return res;
	}

	
	/**
	 * Clarity based schema enhancement algorithm, receives a similarity matrix (inSM) generated
	 * by a first line matcher (a.k.a similarity measure) and parameters alpha and beta. calculates
	 * for each row and each column a clarity score. Creates a contrasted version of the matrix 
	 * and then updates the confidence score in each 
	 * matrix cell by a linear combination of the original score and the row and column clarity
	 * scores multiplied by the contrasted matrix. 
	 * @param inSM similarity matrix on which the algorithm works. modifies inplace
	 * @param alpha : (depreciated) A double between 0 and 1, larger alpha means more emphasis on row clarity 
	 * @param beta : (depreciated) A double between 0 and 1, larger beta means higher emphasis on original score 
	 * @param weberp : weber power value
	 * @param weakenReduction2 
	 * @param weakenThreshold2 
	 */
	private static void clarityS(SimilarityMatrix inSM, double alpha, double beta, double weberp, double weakenThreshold, double weakenReduction)
	{	
		double tmpElem = 0;
		int rows = inSM.candidateTermMap.size();
		int cols = inSM.targetTermMap.size();
		double rowClarity[] = new double[rows];
		double colClarity[] = new double[cols];
		boolean useWeakening = true; //TODO parameterize from file
		
		// Calculate row clarity for each row
		for (int i=0;i<rows;i++)
			rowClarity[i] = calcArrayClarity3(inSM.getRow(i));

		// Calculate column clarity for each column
		for (int j=0;j<cols;j++)
			colClarity[j] = calcArrayClarity3(inSM.getColumn(j));
		
		// Create contrasted Matrices
		double[][] rowContMatrix = new double[rows][cols];
		for (int i=0;i<rows;i++)
		{
//			if (i==64)
//				rowContMatrix[i] = calcContrasted(inSM.getRow(i),weberp);
//			else
				rowContMatrix[i] = calcContrasted(inSM.getRow(i),weberp);
		}
			
		
		double[][] colContMatrix = new double[cols][rows];
		for (int i=0;i<cols;i++)
		{
//			if (i==26)
//				colContMatrix[i] = calcContrasted(inSM.getColumn(i),weberp);
//			else
				colContMatrix[i] = calcContrasted(inSM.getColumn(i),weberp);
		}
			
		
		//transpose column matrix
		DoubleMatrix colContrasted = (DoubleMatrix) (new DoubleMatrix(colContMatrix)).transpose();
		
		// Update scores in matrix
		for (int i=0;i<rows;i++)
			for (int j=0;j<cols;j++)
			{
				tmpElem = inSM.similarityM.getElement(i, j);
				if (i==2)
					if (j==143)
						break;
				
				double rowModifierWeight = 0.0;
				double colModifierWeight = 0.0;
				double rowModifier = 0.0;
				double colModifier = 0.0;
				
				if (tmpElem != 0)
				{
					if (rowClarity[i]<weakenThreshold && useWeakening)
					{
						rowModifierWeight = (1-rowClarity[i])/weakenReduction; //using /8 to weaken weakening
						DescriptiveStatistics DS = new DescriptiveStatistics();
						double[] rw = inSM.getRow(i);
						for (int k=0;k<rw.length;k++) 
							if (rw[k]!=0)
								DS.addValue(rw[k]);
						rowModifier = (-DS.getMin());
					}
					else
					{
						rowModifierWeight = rowClarity[i]; 
						rowModifier = rowClarity[i]*rowContMatrix[i][j];
					}
					
					if (colClarity[j]<weakenThreshold)
					{
						colModifierWeight = (1-colClarity[j])/weakenReduction; //using /8 to weaken weakening
						DescriptiveStatistics DS = new DescriptiveStatistics();
						double[] cl = inSM.getColumn(j);
						for (int k=0;k<cl.length;k++) 
							if(cl[k]!=0)
								DS.addValue(cl[k]);
						colModifier = -DS.getMin();
					}
					else
					{
						colModifierWeight = colClarity[j]; 
						colModifier = colClarity[j]*colContrasted.getElement(i, j);
					}
					tmpElem = tmpElem*(1-rowModifierWeight/2-colModifierWeight/2)+ 0.5*rowModifierWeight*rowModifier+0.5*colModifierWeight*colModifier;
					if (tmpElem<0) tmpElem = 0;
					//tmpElem = tmpElem*(1-rowClarity[i]/2-colClarity[j]/2)+ 0.5*rowClarity[i]*rowContMatrix[i][j]+0.5*colClarity[j]*colContrasted.getElement(i, j);
				}
				inSM.similarityM.setElement(i, j, tmpElem);
			}
				
	}

	/**
	 * Calculate a contrasted version of an array using weber contrast function
	 * @param arr : Input array of doubles between -inf and inf
	 * @param weberp : power to raise the weber function
	 * @return : Contrasted array of doubles between 0 and 1
	 */
private static double[] calcContrasted(double[] arr, double weberp) 
{
	double[] res = new double[arr.length];	
	
	DescriptiveStatistics DS = new DescriptiveStatistics();
	
	// Contrast using Weber Contrast function : (weberpower^(value - avg))/avg
	for (int j=0;j<arr.length;j++) 
		if (arr[j]!=0) 
			DS.addValue(arr[j]);
	for (int j=0;j<arr.length;j++) 
		if (arr[j]!=0)
			res[j] = Math.pow(weberp,(arr[j]-DS.getMean())) / DS.getMean();
			//res[j] = Math.pow(weberp,Math.abs(arr[j]-DS.getMean())) / DS.getMean();
	
	//Normalize between 0 and 1
	DS.clear();
	for (int j=0;j<arr.length;j++) 
		if (res[j]!=0) 
			DS.addValue(res[j]);
	double minVal = DS.getMin();
	double maxVal = DS.getMax();
	for (int j=0;j<arr.length;j++) 
		if (res[j]!=0)
			if (maxVal-minVal==0)
				res[j] = arr[j];
			else
				res[j] = (res[j] - minVal)/(maxVal-minVal);
	return res;
}

	/**
	 * Calculate an array's clarity using Var function
	 * @param array : Array to calculate clarity on
	 * @return : double representing the clarity score
	 */
	@SuppressWarnings("unused")
	private static double calcArrayClarity0(double[] array) 
	{
		double tmpVectorSum;
		double tmpVar;
		tmpVectorSum = 0;
		tmpVar =0;
		for (int j=0;j<array.length;j++) tmpVectorSum += array[j];
		for (int j=0;j<array.length;j++) tmpVar += Math.pow((array[j] - tmpVectorSum/array.length),2);
		return tmpVar/(array.length-1);
	}
	
	/**
	 * Calculate an array's clarity using stdev function and an optimal stdev of vector 100000
	 * @param array : Array to calculate clarity on
	 * @return : double representing the clarity score
	 */
	@SuppressWarnings("unused")
	private static double calcArrayClarity1(double[] array) 
	{
		
		double res = 0.0;
		DescriptiveStatistics optDS = new DescriptiveStatistics();
		optDS.addValue(1.0);
		for (int j=1;j<array.length;j++) optDS.addValue(0);
		
		DescriptiveStatistics DS = new DescriptiveStatistics();
		for (int j=0;j<array.length;j++) DS.addValue(array[j]);
		
		res = 1-java.lang.Math.abs(optDS.getStandardDeviation()-DS.getStandardDeviation())/optDS.getStandardDeviation();
		return res;
	}

	/**
	 * Calculate an array's clarity using Entropy function
	 * @param array : Array to calculate clarity on
	 * @return : double representing the clarity score
	 */
	@SuppressWarnings("unused")
	private static double calcArrayClarity2(double[] array) 
	{
		double tmpVectorSum;
		tmpVectorSum = 0;
		for (int j=0;j<array.length;j++) tmpVectorSum += (array[j]==0 ? 0 : array[j]*Math.log(1/array[j]));
		return ((tmpVectorSum==0||2*(1/tmpVectorSum)>1) ? 1: 2*(1/(tmpVectorSum)));
	}
	
	/**
	 * Calculate an array's clarity using kurtosis function
	 * @param array : Array to calculate clarity on
	 * @return : double representing the clarity score
	 */
	@SuppressWarnings("unused")
	private static double calcArrayClarity3(double[] array) 
	{
		double res = 1.0;
		DescriptiveStatistics optDS = new DescriptiveStatistics();
		optDS.addValue(1.0);
		for (int j=1;j<array.length;j++) 
			if(array[j]!=0)
				optDS.addValue(0);
		
		DescriptiveStatistics DS = new DescriptiveStatistics();
		for (int j=0;j<array.length;j++) 
			if(array[j]!=0) 
				DS.addValue(array[j]);
		if (DS.getN() < 2) return res;

		//TODO Check why Kurtosis of 1.0 and 0.9 is undefined
		//if (new Double(0.0/0.0).equals(DS.getKurtosis())) return 1;
		
		//res = 1-java.lang.Math.abs(optDS.getKurtosis()-DS.getKurtosis())/optDS.getKurtosis();
		//res = 1-java.lang.Math.abs(DS.getN() - (DS.getKurtosis()<0?0:DS.getKurtosis()))/DS.getN();
		res = (DS.getStandardDeviation()/DS.getMean()) / (optDS.getStandardDeviation()/optDS.getMean());
		return res;
	}

	
	/**
	 * Helper method - receive a Hashmap and an Item string and insert the string to the hashmap
	 * @param schemaItemHash - Schema Item Hashmap to insert the item into
	 * @param itemString - String representation of the Item
	 */
	private static void insertItem(HashMap<Long, String> schemaItemHash, String[] itemString) {
		if (schemaItemHash.containsKey( Long.parseLong(itemString[1])))
		{
			System.err.print("Warning: Duplicate Item detected: " + itemString + "Item will be skipped \n");
			return;
		}
		schemaItemHash.put(Long.parseLong(itemString[1]), itemString[3]);
	}
}


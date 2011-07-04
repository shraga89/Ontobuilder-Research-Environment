/**
 * The schemamatching.experiments package includes experiments in schema matching
 * on the ontobuilder schema matching system and other utilities developed at the Technion schema matching research group. 
 * Experiments are run on a dataset library documented in a mysql database.  
 */
package ac.technion.schemamatching.experiments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import smb_service.PropertyLoader;
import technion.iem.schemamatching.dbutils.DBInterface;
import ac.technion.schemamatching.statistics.Statistic;

import java.util.Calendar;
import java.text.SimpleDateFormat;

import com.infomata.data.CSVFormat;
import com.infomata.data.DataFile;
import com.infomata.data.DataRow;
/**
 * The class provides tools for running schema matching experiments.
 * @author Tomer Sagi
 * @author Nimrod Busany 
 */
public class OBExperimentRunner { //TODO - make experiment runner a singleton

	protected String dsurl;
	protected DBInterface db;
	private ArrayList<ExperimentSchemaPair> dataset;
	protected HashMap<Integer,String> frstLineMatchers;
	protected HashMap<Integer,String> scndLineMatchers;
	protected ExperimentDocumenter doc;
	protected OntoBuilderWrapper obw;
	private Properties properties;
	private ArrayList<OtherMatcher> om;
	public static HashMap<Integer,String> measures = new HashMap<Integer,String>();
	public static HashMap<Integer,String> matchers = new HashMap<Integer,String>();
	public static HashMap<String,Integer> reversedmeasures = new HashMap<String,Integer>();
	public static HashMap<String,Integer> reversedmatchers = new HashMap<String,Integer>();
	/**
	 * Base constructor
	 * @param db
	 * @param datasetURL
	 */
	public OBExperimentRunner(DBInterface db,String datasetURL)
	{
		this.db = db;
		dsurl = datasetURL;
		File dsFolder = new File(dsurl);
		if (dsFolder == null || !dsFolder.isDirectory()) error("Supplied dataset url is invalid or unreachable");
		obw = new OntoBuilderWrapper();
		doc = new ExperimentDocumenter(dsurl+ " " + getTime(), db);
		fillHashMeasures();
	}
	

	/**
	 * Class constructor for an experiment on a specific schema pair
	 * @param db
	 * @param datasetURL
	 * @param schemaPairID
	 */
	public OBExperimentRunner(DBInterface db,String datasetURL,int schemaPairID)
	{
		this(db,datasetURL);
		dataset = selectExperiments(1,schemaPairID, 0, null);
		doc = new ExperimentDocumenter(datasetURL + "  " + getTime() ,this.db); 
	}
	
	/**
	 * Class constructor for an experiment on K random schemas from a specific dataset and list of domains 
	 * @param db - shouldn't this be included in the Property file?
	 * @param datasetURL
	 * @param datasetID
	 * @param domainCodes
	 * @param size number of scemapairs you wish to include in your experiment
	 */
	public OBExperimentRunner(DBInterface db,String datasetURL,Integer datasetID,HashSet<Integer> domainCodes,Integer size){
		this(db,datasetURL);
		dataset = selectExperiments(size,0, datasetID, domainCodes); 
	}

	//returns current time
	private String getTime() {
		Calendar cal = Calendar.getInstance();
		String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
	    return sdf.format(cal.getTime());
	}
	
	/**
	 * Main method runs an experiment according to the supplied parameters
	 * @param args[0]  - outputPath folder in which temporary files will be saved
	 * @param args[1] Experiment Type compared against enum ExperimentType
	 * @param args[2] K - number of experiments for clarity, set 0 to use a specific ID
	 * @param args[3] mode for the SMB (E,L,R)
	 * @param args[4] schema pair ID (ignored unless K is 0
	 * @param args[5] datasetID
	 * @param args[6] domainCodes - a string in the following format "2,3,4,2" (without the Quotation mark)
	 */
	public static void main(String[] args) throws NumberFormatException, Exception 
	{
		checkInputParameters(args);
		File outputPath = new File(args[0]); // folder in which temporary files will be saved
	    Properties pMap = PropertyLoader.loadProperties("ob_interface");
	    DBInterface myDB = new DBInterface(Integer.parseInt((String)pMap.get("dbmstype")),(String)pMap.get("host"),(String)pMap.get("dbname"),(String)pMap.get("username"),(String)pMap.get("pwd"));


	    OBExperimentRunner myExpRunner;
	    if (Integer.valueOf(args[2])==0)
	    	myExpRunner = new OBExperimentRunner(myDB,(String)pMap.get("schemaPath"),Integer.valueOf(args[4]));  
	    else{
	    	Integer datasetID = Integer.valueOf(args[5]);
	    	Integer size = Integer.valueOf(args[2]);
	    	HashSet<Integer> domainCodes = parseDomainCodes(args[6]);
	    	myExpRunner = new OBExperimentRunner(myDB,(String)pMap.get("schemaPath"),datasetID,domainCodes,size);
	    	}
	    myExpRunner.runExperiment(ExperimentType.valueOf(args[1]), outputPath);
  		  
	 }
	

	/**
	 * @param DomainCodes - a string in the following format "2,3,4,2" (without the Quotation mark)
	 * @return HashSet<Integer>
	 */
	private static HashSet<Integer> parseDomainCodes(String DomainCodes) {
		String[] st = DomainCodes.split(",");
		HashSet<Integer> DomainCodesHash = new HashSet<Integer>();
		for (String s : st){
			DomainCodesHash.add(Integer.valueOf(s));
	    }
		return DomainCodesHash;
	}


	private static void checkInputParameters(String[] args) {
		if (args[0]==null) {System.err.println("Please enter an output folder path");System.exit(0);}
		if ( Integer.valueOf(args[2])==0 && args[4]==null ){System.err.println("Please enter an number of experiment to sample or a spid");System.exit(0);}
		if (Integer.valueOf(args[2])<0) {System.err.println("Illigal number of experiments to sample");System.exit(0);}
		if ( Integer.valueOf(args[2])==0 && findSPID(args[4]) ){System.err.println("SPID wasn't found");System.exit(0);}
		if (args[3]!="E" && args[3]!="L" && args[3]!="R") {System.err.println("wrong error code: \n E - for enhanced; R - for ;L - for");System.exit(0);}
	}
	
	private static boolean findSPID(String spid) {
		Properties pMap = PropertyLoader.loadProperties("ob_interface");
	    DBInterface myDB = new DBInterface(Integer.parseInt((String)pMap.get("dbmstype")),(String)pMap.get("host"),(String)pMap.get("dbname"),(String)pMap.get("username"),(String)pMap.get("pwd"));
	    String sql = "SELECT `SchemaID` FROM schemata WHERE SchemaID='" + spid +"';";
		return (myDB.runSelectQuery(sql, 1).get(0)[0]!=null);
}

	public ExperimentDocumenter getDoc() {
		return doc;
	}

	public ArrayList<ExperimentSchemaPair> getDS() 
	{return dataset;}
	
	
	/**
	 * Selects K random Schema Matching Experiments from the database and loads into OB objects
	 * A Schema matching experiment Includes a schema pair and exact match.
	 * Documents the experiment in the database, (Experiment and ExperimentSchemaPairs) and 
	 * if the terms are not documented, adds them as well to the terms table.
	 * Note: This method assumes that schema files were not changed (hence if a schema was parsed before,
	 * it will not be parsed again and changes will not be detected  
	 * @param K no. of random experiments to load, put 0 for a specific schema pair
	 * @param spid get a specific schema pair, ignored if K <> 0
	 * @param datasetID default value 1 (Ontobuilder Dataset) if not null, the experiment will be loaded from the supplied dataset code
	 * @param domainCodes A list of domainCodes to limit the random schema selection with. If null, all domains are used. 
	 * @return ArrayList of Schema Experiments. 
	 */
	private ArrayList<ExperimentSchemaPair> selectExperiments(int K, int spid, int datasetID, HashSet<Integer> domainCodes) 
	{
		if (datasetID == 0) datasetID = 1;
		ArrayList<ExperimentSchemaPair> ds = new ArrayList<ExperimentSchemaPair>();
		String sql = "FROM `schemapairs`, schemata WHERE (schemapairs.`CandidateSchema` =" + 
					 " schemata.`SchemaID` OR schemapairs.`TargetSchema` = schemata.`SchemaID`) AND schemapairs.DSID = " + Integer.toString(datasetID) +  
					 (domainCodes.isEmpty()?";" : " AND ( schemata.`domainCode` IN (" + domainCodes.toString().substring(1,domainCodes.toString().length()-1) + ") )");
		
		ArrayList<String[]> NumberOfSchemaPairs =  db.runSelectQuery("SELECT COUNT( DISTINCT spid) " + sql, 1);
		//check the number of available experiments is larger then k
		if (K>Integer.valueOf(NumberOfSchemaPairs.get(0)[0])) 
			error("No. of experiments requested is larger than the no. of schema pairs in the dataset");
		//extracting pairs from the DB
		if (spid!=0) sql = "SELECT spid FROM schemapairs WHERE SPID = " + spid + ";" ;
		else sql  = "SELECT DISTINCT spid " + sql +" ORDER BY RAND() LIMIT " + String.valueOf(K); 
		ArrayList<String[]> k_Schemapairs =  db.runSelectQuery(sql, 1);
		for (String[] strPair : k_Schemapairs)
		{
			ExperimentSchemaPair schemasExp = new ExperimentSchemaPair(this, Integer.parseInt(strPair[0]));
			ds.add(schemasExp);
		}
		//document the new experiment in to DB 
		this.getDoc().writeExperimentsToDB(ds,k_Schemapairs,dsurl); 
		return ds;
	}

	/**
	 * Sends the provided error message to the err stream and exits with code 1
	 * @param msg
	 */
	protected static void error(String msg) 
	{
		System.err.println(msg);
		System.exit(1);
		
	}
	
	public static String truncate(String value, int length)
	{
	  if (value != null && value.length() > length)
	    value = value.substring(0, length);
	  return value;
	}
	
	public DBInterface getDB()
	{
	  return db;
	}
	
	/**
	 * Runs experiment e on the loaded dataset and writes the result to the file object supplied 
	 * @param e Experiment to run
	 * @param resultFolder File object in which to create the results. Folder will be created if not exists
	 */
	public void runExperiment(ExperimentType et,File resultFolder)
	{
		MatchingExperiment e = et.getExperiment();
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		e.init(this, properties, om);
		resultFolder = getFolder(resultFolder);
		for (ExperimentSchemaPair esp : dataset)
		{
			res.addAll(e.runExperiment(esp));
		}
		formatStatistics(res, resultFolder);	
	}
	
	/**
	 * Checks for existence of filepath supplied and creates the folder tree if needed
	 * @param resultFolder
	 * @return
	 */
	private File getFolder(File testFolder) {
		if (!testFolder.exists()) {
			boolean success = testFolder.mkdirs();
			if (!success) {
				System.err
						.println("Unable to create folder");
				return null;
			}
		} else {
				for (File testFile : testFolder.listFiles()) {
					testFile.delete();
				}
		}
		return testFolder;
	}


	/**
	 * Collects statistics by type and outputs in CSV format to folder specified
	 * @param res
	 * @param resultFolder 
	 */
	private void formatStatistics(ArrayList<Statistic> res, File resultFolder) {
		// Collect statistics by type
		HashMap<String,ArrayList<Statistic>> collected = new HashMap<String,ArrayList<Statistic>>();
		for (Statistic s : res)
		{
			ArrayList<Statistic> tmp;
			if (collected.containsKey(s.getName()))
				tmp = collected.get(s);
			else
				tmp = new ArrayList<Statistic>();
			
			tmp.add(s);
		}
		
		//Output a file for each statistic
		for (String statName : collected.keySet())
		{
			File f = new File(resultFolder,statName + ".csv");
			String[] header = collected.get(statName).get(0).getHeader();
			ArrayList<String[]> data = new ArrayList<String[]>();
			for (Statistic s : collected.get(statName))
			{
				data.addAll(s.getData());
			}
			outputAsCSV(header,data,f);
		}
		
	}

	/**
	 * Takes header and data and outputs to CSV file
	 * @param header
	 * @param data
	 * @param f
	 */
	private void outputAsCSV(String[] header, ArrayList<String[]> data, File f) 
	{
		DataFile write = DataFile.createWriter("8859_1", false);
		write.setDataFormat(new CSVFormat());			
		try {
			write.open(f);
		
		DataRow row = write.next();
		for (int i=0;i<header.length;i++) row.add(header[i]);
		for (int i=0;i<data.size();i++)
		{
			row = write.next();
			String rRow[] = data.get(i);
			for (int j=0;j<rRow.length;j++) row.add(rRow[j]);
		}
		write.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void fillHashMeasures() {
		//fill in the firstlines table
		measures.put(1,"Term Match");
		measures.put(2,"Value Match");
		measures.put(3,"Term and Value Match");
		measures.put(4,"Combined Match");
		measures.put(5,"Precedence Match");
		measures.put(6,"Graph Match");
		measures.put(7,"Similarity Flooding Algorithm");
		//fill in the firstlines reversed table
		reversedmeasures.put("Term Match",1);
		reversedmeasures.put("Value Match",2);
		reversedmeasures.put("Term and Value Match",3);
		reversedmeasures.put("Combined Match",4);
		reversedmeasures.put("Precedence Match",5);
		reversedmeasures.put("Graph Match",6);
		reversedmeasures.put("Similarity Flooding Algorithm",7);		
		//fill in second line matchers' table
		matchers.put(1,"Max Weighted Bipartite Graph");
		matchers.put(2,"Stable Marriage");
		matchers.put(3,"Dominants");
		matchers.put(4,"Intersection");
		matchers.put(5,"Union");
		//fill in second line matchers' reversed table
		reversedmatchers.put("Max Weighted Bipartite Graph",1);
		reversedmatchers.put("Stable Marriage",2);
		reversedmatchers.put("Dominants",3);
		reversedmatchers.put("Intersection",4);
		reversedmatchers.put("Union",5);
	}
	
}




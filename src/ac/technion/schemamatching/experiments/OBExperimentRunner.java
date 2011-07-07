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
	protected ExperimentDocumenter doc;
	protected OntoBuilderWrapper obw;
	private Properties properties;
	private ArrayList<OtherMatcher> om;
	public HashMap<Integer,String> measures = new HashMap<Integer,String>();
	public HashMap<Integer,String> matchers = new HashMap<Integer,String>();
	public HashMap<String,Integer> reversedmeasures = new HashMap<String,Integer>();
	public HashMap<String,Integer> reversedmatchers = new HashMap<String,Integer>();
	/**
	 * Main method runs an experiment according to the supplied parameters
	 * @param args[0] outputPath folder in which temporary files will be saved
	 * @param args[1] Experiment Type compared against enum ExperimentType
	 * @param args[2] K - number of experiments schema pairs, set 0 to use a specific SPID
	 * @param args[3] schema pair ID (ignored unless K is 0
	 * @param args[4] datasetID (for random K)
	 * @param args[5] domainCodes - (optional) string in the following format "2,3,4,2" (without the Quotation mark)
	 */
	public static void main(String[] args) 
	{
		checkInputParameters(args);
		File outputPath = new File(args[0]); // folder in which temporary files will be saved
	    Properties pMap = PropertyLoader.loadProperties("ob_interface");
	    DBInterface myDB = new DBInterface(Integer.parseInt((String)pMap.get("dbmstype")),(String)pMap.get("host"),(String)pMap.get("dbname"),(String)pMap.get("username"),(String)pMap.get("pwd"));
	
	
	    OBExperimentRunner myExpRunner;
	    if (Integer.valueOf(args[2])==0)
	    	myExpRunner = new OBExperimentRunner(myDB,(String)pMap.get("schemaPath"),Integer.valueOf(args[3]), args.toString());  
	    else{
	    	Integer datasetID = Integer.valueOf(args[4]);
	    	Integer size = Integer.valueOf(args[2]);
	    	HashSet<Integer> domainCodes= null;
	    	if (args.length==6)
	    	{domainCodes = parseDomainCodes(args[5]);}
	    	myExpRunner = new OBExperimentRunner(myDB,(String)pMap.get("schemaPath"),datasetID,domainCodes,size, "Experiment Type: " + args[1]+ " k=" + args[2] + " SPID: " + args[3] + " Dataset: " + args[4]);
	    	}
	    myExpRunner.runExperiment(ExperimentType.valueOf(args[1]), outputPath);
		  
	 }


	/**
	 * Base constructor
	 * @param db
	 * @param datasetURL
	 * @param experimentDescription
	 */
	private OBExperimentRunner(DBInterface db,String datasetURL)
	{
		this.db = db;
		dsurl = datasetURL;
		File dsFolder = new File(dsurl);
		if (dsFolder == null || !dsFolder.isDirectory()) error("Supplied dataset url is invalid or unreachable");
		obw = new OntoBuilderWrapper();
		fillHashMeasures();
	}
	

	/**
	 * Class constructor for an experiment on a specific schema pair
	 * @param db
	 * @param datasetURL
	 * @param schemaPairID
	 * @param experimentDescription TODO
	 */
	public OBExperimentRunner(DBInterface db,String datasetURL,int schemaPairID, String experimentDescription)
	{
		this(db,datasetURL);
		dataset = selectExperiments(1,schemaPairID, 0, null);
		doc = new ExperimentDocumenter(this,experimentDescription); 
	}
	
	/**
	 * Class constructor for an experiment on K random schemas from a specific dataset and list of domains 
	 * @param db - shouldn't this be included in the Property file?
	 * @param datasetURL
	 * @param datasetID
	 * @param domainCodes (Optional list of domain codes to use for random schema pair selection
	 * @param size number of scemapairs you wish to include in your experiment
	 * @param experimentDescription
	 */
	public OBExperimentRunner(DBInterface db,String datasetURL,Integer datasetID,HashSet<Integer> domainCodes,Integer size, String experimentDescription){
		this(db,datasetURL);
		if (domainCodes==null) domainCodes = new HashSet<Integer>();
		dataset = selectExperiments(size,0, datasetID, domainCodes); 
		doc = new ExperimentDocumenter(this,experimentDescription);
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
		if (args[0]==null) {error("Please enter an output folder path");}
		if ( Integer.valueOf(args[2])==0 && args[3]==null ){error("Please enter an number of experiment to sample or a spid");}
		if (Integer.valueOf(args[2])<0) {error("Illegal number of experiments to sample");}
		if ( Integer.valueOf(args[2])==0 && !findSPID(args[3]) ){error("SPID wasn't found");}
		try {
				ExperimentType.valueOf(args[1]);
		}
		catch (Exception e)
		{
			error("Invalid Matching Experiment Type.");
		}
		
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
		String sql = "FROM `schemapairs`, `schemata` WHERE (`schemapairs`.`CandidateSchema` =" + 
					 " `schemata`.`SchemaID` OR `schemapairs`.`TargetSchema` = `schemata`.`SchemaID`) AND `schemapairs`.`DSID` = " + Integer.toString(datasetID) +  
					 (domainCodes.isEmpty()?"" : " AND ( schemata.`domainCode` IN (" + domainCodes.toString().substring(1,domainCodes.toString().length()-1) + ") )");
		
		ArrayList<String[]> NumberOfSchemaPairs =  db.runSelectQuery("SELECT COUNT( DISTINCT spid) " + sql, 1);
		//check the number of available experiments is larger then k
		if (K>Integer.valueOf(NumberOfSchemaPairs.get(0)[0])) 
			error("No. of experiments requested is larger than the no. of schema pairs in the dataset");
		//extracting pairs from the DB
		if (spid!=0) sql = "SELECT spid FROM schemapairs WHERE SPID = " + spid + ";" ;
		else sql  = "SELECT DISTINCT spid " + sql +" ORDER BY RAND() LIMIT " + String.valueOf(K) + ";"; 
		ArrayList<String[]> k_Schemapairs =  db.runSelectQuery(sql, 1);
		for (String[] strPair : k_Schemapairs)
		{
			ExperimentSchemaPair schemasExp = new ExperimentSchemaPair(this, Integer.parseInt(strPair[0]));
			ds.add(schemasExp);
		}
		//document the new experiment in to DB 
		//this.getDoc().writeExperimentsToDB(ds,k_Schemapairs,dsurl); 
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
		measures.put(0,"Term Match");
		measures.put(1,"Value Match");
		measures.put(2,"Term and Value Match");
		measures.put(3,"Combined Match");
		measures.put(4,"Precedence Match");
		measures.put(5,"Graph Match");
		measures.put(6,"Similarity Flooding Algorithm");
		//fill in the firstlines reversed table
		reversedmeasures.put("Term Match",0);
		reversedmeasures.put("Value Match",1);
		reversedmeasures.put("Term and Value Match",2);
		reversedmeasures.put("Combined Match",3);
		reversedmeasures.put("Precedence Match",4);
		reversedmeasures.put("Graph Match",5);
		reversedmeasures.put("Similarity Flooding Algorithm",6);		
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




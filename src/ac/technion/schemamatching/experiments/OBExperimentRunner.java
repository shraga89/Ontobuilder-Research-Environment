/**
 * The schemamatching.experiments package includes experiments in schema matching
 * on the ontobuilder schema matching system and other utilities developed at the Technion schema matching research group. 
 * Experiments are run on a dataset library documented in a mysql database.  
 */
package ac.technion.schemamatching.experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import technion.iem.schemamatching.dbutils.DBInterface;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;
import ac.technion.schemamatching.matchers.FLMList;
import ac.technion.schemamatching.matchers.SLMList;
import ac.technion.schemamatching.matchers.FirstLineMatcher;
import ac.technion.schemamatching.matchers.SecondLineMatcher;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.util.PropertyLoader;

import com.infomata.data.CSVFormat;
import com.infomata.data.DataFile;
import com.infomata.data.DataRow;

/**
 * The class provides tools for running schema matching experiments.
 * @author Tomer Sagi
 * @author Nimrod Busany 
 * @category Singleton
 */
public class OBExperimentRunner { 

	private static OBExperimentRunner oer;
	private HashMap<Long,ArrayList<ExperimentSchemaPair>> experimentDatasets = new HashMap<Long,ArrayList<ExperimentSchemaPair>>();
	private HashMap<Long,ExperimentDocumenter> experimentDocumenters = new HashMap<Long,ExperimentDocumenter>();
	private String dsurl;
	protected DBInterface db;
	protected OntoBuilderWrapper obw;
	private XmlFileHandler xfh;
	
	/**
	 * Main method runs an experiment according to the supplied parameters
	 * @param args[0] run as command line (cmd) or console application (console)
	 * @param args[1] output path
	 * @param args[2] Experiment Type compared against enum ExperimentType
	 * @param args[3] K - number of experiments schema pairs, set 0 to use a specific SPID
	 * @param args[4] schema pair ID (ignored unless K is 0
	 * @param args[5] datasetID (for random K)
	 * @param args[6] -d:domainCodes - (optional) string in the following format "2,3,4,2" (without the Quotation mark)
	 * or -flm:First Line Matcher Codes or -p:properties file used to configure the experiment
	 * 
	 */
	public static void main(String[] args) 
	{
		OBExperimentRunner myExpRunner = getOER();
		File outputPath = null;
		ExperimentType et = null;
		ArrayList<ExperimentSchemaPair> dataset = new ArrayList<ExperimentSchemaPair>();
		String expDesc = "None provided";
		HashSet<Integer> dc =new HashSet<Integer>();
		ArrayList<FirstLineMatcher> flm = null;
		ArrayList<SecondLineMatcher> slm = null; 
		Properties pFile = null;
		if (args[0].equalsIgnoreCase("cmd"))
		{
			myExpRunner.checkInputParameters(args);
			outputPath = new File(args[1]); // folder in which temporary files will be saved
			et = ExperimentType.valueOf(args[2]);
			if (args.length>6)
	    	{
	    		//iterate over optional arguments
	    		for (int i=6;i<args.length;i++)
	    		{
	    			char p = args[i].charAt(1);
	    			String arguments = args[i].substring(3);
	    			switch(p)
	    			{
	    				case 'd': dc = parseDomainCodes(arguments); break;
	    				case 'f': flm = parseFLMids(arguments); break;
	    				case 's': slm = parseSLMids(arguments); break;
	    				case 'p': pFile = PropertyLoader.loadProperties(arguments); break; 
	    			}
	    		}
	    		
	    	}
			if (Integer.valueOf(args[3])==0){
				dataset =oer.selectExperiments(0,Integer.valueOf(args[4]), 0, dc );}  
			else{
		    	Integer datasetID = Integer.valueOf(args[5]);
		    	Integer size = Integer.valueOf(args[3]);
		    	expDesc =  "Experiment Type: " + args[2]+ " k=" + args[3] + " SPID: " + args[4] + " Dataset: " + args[5];
		    	if (dc == null) dc = new HashSet<Integer>();
				dataset = oer.selectExperiments(size,0, datasetID, dc);
	    	}
			if (flm == null) 
    		{
    			flm = new ArrayList<FirstLineMatcher>();
    			flm.addAll(FLMList.getIdFLMHash().values());
    		}
	    	if (slm == null) 
    		{
    			slm = new ArrayList<SecondLineMatcher>();
    			slm.addAll(SLMList.getIdSLMHash().values());
    		}
	    	
		}
		else if (args[0].equalsIgnoreCase("console"))
		{
			//TODO initialize console application
		}
		else
		{
			printGeneralInstructions();
			error("invalid 1st parameter supplied");
		}
		Long eid = myExpRunner.initExperiment(dataset, expDesc);
		myExpRunner.runExperiment(et,eid, outputPath,flm,slm,pFile);
	 }
	
	/**
	 * 
	 * @param slmCodes
	 * @return
	 */
	private static ArrayList<SecondLineMatcher> parseSLMids(String slmCodes) {
		String[] st = slmCodes.split(",");
		ArrayList<SecondLineMatcher> slm = new ArrayList<SecondLineMatcher>();
		HashMap<Integer, SecondLineMatcher> hash = SLMList.getIdSLMHash();
		for (String id : st)
		{
			int intID = Integer.parseInt(id);
			if (hash.containsKey(intID))
				slm.add(hash.get(intID));
		}
		return slm;
	}

	/**
	 * Receives a parameter string 
	 * @param flmCodes assumed to contain a comma seperated list of first line matcher codes
	 * @return list of FLM found to match codes given
	 */
	private static ArrayList<FirstLineMatcher> parseFLMids(String flmCodes) {
		String[] st = flmCodes.split(",");
		ArrayList<FirstLineMatcher> flm = new ArrayList<FirstLineMatcher>();
		HashMap<Integer, FirstLineMatcher> hash = FLMList.getIdFLMHash();
		for (String id : st)
		{
			int intID = Integer.parseInt(id);
			if (hash.containsKey(intID))
				flm.add(hash.get(intID));
		}
		return flm;
	}

	public OntoBuilderWrapper getOBW()
	{
		return obw;
	}

	private static void printGeneralInstructions() 
	{
		System.out.println("Usage: java -jar OBExperimentRunner.jar <cmd/console> [cmd param args...]");
		System.out.println("Command line mode parameters:");
		System.out.println("1: output path");
		System.out.println("2: Experiment Type compared against enum ExperimentType");
		System.out.println("3: K - number of experiments schema pairs, set 0 to use a specific SPID");
		System.out.println("4: schema pair ID (ignored unless K is 0");
		System.out.println("5: datasetID (for random K)");
		System.out.println("6: -d:domainCodes - (optional) string in the following format \"-d:3,4,2\" (without the Quotation marks)");
		System.out.println("7: -f:first line matcher ids - (optional) string in the following format \"-f:3,4,2\" (without the Quotation marks)");
		System.out.println("8: -s:second line matcher ids - (optional) string in the following format \"-s:1,2,4\" (without the Quotation marks)");
	}


	/**
	 * Base constructor, private (Singleton)
	 */
	private OBExperimentRunner()
	{
		Properties pMap = new Properties();
		try {
			pMap.load(new FileInputStream("ob_interface.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println((String)pMap.get("dbmstype") + " " + (String)pMap.get("host") + " " + (String)pMap.get("dbname") + " " + (String)pMap.get("username") + " " + (String)pMap.get("pwd"));
	    db = new DBInterface(Integer.parseInt((String)pMap.get("dbmstype")),(String)pMap.get("host"),(String)pMap.get("dbname"),(String)pMap.get("username"),(String)pMap.get("pwd"));
		dsurl = (String)pMap.get("schemaPath");
		File dsFolder = new File(dsurl);
		if (dsFolder == null || !dsFolder.isDirectory()) error("Supplied dataset url is invalid or unreachable");
		obw = new OntoBuilderWrapper();
		xfh = new XmlFileHandler();

	}
	
	/**
	 * Getter method instead of constructor - Singleton design Pattern
	 * @return
	 */
	public static synchronized OBExperimentRunner getOER()
	{
		if (oer  == null)
			oer = new OBExperimentRunner();
		return oer;
	}
	
	public OBExperimentRunner clone()throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
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


	private void checkInputParameters(String[] args) {
		if (args[0]==null) {error("Please enter an output folder path");}
		if ( Integer.valueOf(args[3])==0 && args[4]==null ){error("Please enter an number of experiment to sample or a spid");}
		if (Integer.valueOf(args[3])<0) {error("Illegal number of experiments to sample");}
		if ( Integer.valueOf(args[3])==0 && !findSPID(args[4]) ){error("SPID wasn't found");}
		try {
				ExperimentType.valueOf(args[2]);
		}
		catch (Exception e)
		{
			error("Invalid Matching Experiment Type.");
		}
		
	}
	
	private boolean findSPID(String spid) {
	    String sql = "SELECT `SPID` FROM schemapairs WHERE SPID='" + spid +"';";
		return (db.runSelectQuery(sql, 1).get(0)[0]!=null);
}

	/**
	 * Returns the ExperimentDocumenter object for a given experiment ID
	 * @param eid Experiment ID
	 * @return ExperimentDocumenter object
	 */
	public ExperimentDocumenter getDoc(Long eid) {
		return experimentDocumenters.get(eid);
	}

	/**
	 * Returns the dataset for a supplied experiment id
	 * @param eid
	 * @return
	 */
	public ArrayList<ExperimentSchemaPair> getDS(Long eid) 
	{return experimentDatasets.get(eid);}
	
	
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
		if (spid!=0) sql = "SELECT spid, dsid FROM schemapairs WHERE SPID = " + spid + ";" ;
		else sql  = "SELECT DISTINCT spid, schemapairs.dsid " + sql +" ORDER BY RAND() LIMIT " + String.valueOf(K) + ";"; 
		ArrayList<String[]> k_Schemapairs =  db.runSelectQuery(sql, 2);
		for (String[] strPair : k_Schemapairs)
		{
			ExperimentSchemaPair schemasExp = new ExperimentSchemaPair(this, Integer.parseInt(strPair[0]),Integer.parseInt(strPair[1]));
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
	public static void error(String msg) 
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
	 * Runs experiment on the loaded dataset and writes the result to the file object supplied 
	 * @param eid Experiment to run
	 * @param resultFolder File object in which to create the results. Folder will be created if not exists
	 * @param flm list of first line matchers to use
	 * @param slm list of second line matchers to use
	 * @param properties file for configuring the experiment
	 */
	public void runExperiment(ExperimentType et, Long eid,File resultFolder, ArrayList<FirstLineMatcher> flm, ArrayList<SecondLineMatcher> slm,Properties pFile)
	{
		ArrayList<ExperimentSchemaPair> dataset = getDS(eid);
		MatchingExperiment e = et.getExperiment();
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		e.init(this, pFile, flm, slm);
		resultFolder = getFolder(resultFolder);
		int i = 0;
		for (ExperimentSchemaPair esp : dataset)
		{
			res.addAll(e.runExperiment(esp));
			System.out.println("finished " + Integer.toString(++i) + " out of " + Integer.toString(dataset.size()));
		}
		formatStatistics(res, resultFolder);	
	}
	
	public Long initExperiment(ArrayList<ExperimentSchemaPair> dataset,String desc)
	{
		ExperimentDocumenter ed= new ExperimentDocumenter(dataset,desc);
		Long eid = ed.getEid();
		experimentDatasets.put(eid, dataset);
		experimentDocumenters.put(eid, ed);
		return eid;
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
				tmp = collected.get(s.getName());
			else
				tmp = new ArrayList<Statistic>();
			
			tmp.add(s);
			collected.put(s.getName(), tmp);
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

	public void setDsurl(String dsurl) {
		this.dsurl = dsurl;
	}

	public String getDsurl() {
		return dsurl;
	}

	public void setXfh(XmlFileHandler xfh) {
		this.xfh = xfh;
	}

	public XmlFileHandler getXfh() {
		return xfh;
	}
}




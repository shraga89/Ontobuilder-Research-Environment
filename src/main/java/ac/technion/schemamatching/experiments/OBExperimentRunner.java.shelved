/**
 * The schemamatching.experiments package includes experiments in schema matching
 * on the ontobuilder schema matching system and other utilities developed at the Technion schema matching research group. 
 * Experiments are run on a dataset library documented in a mysql database.  
 */
package ac.technion.schemamatching.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeMap;

import technion.iem.schemamatching.dbutils.DBInterface;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;
import ac.technion.schemamatching.experiments.holistic.HolisticExperiment;
import ac.technion.schemamatching.experiments.holistic.HolisticExperimentEnum;
import ac.technion.schemamatching.experiments.pairwise.PairExperimentEnum;
import ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment;
import ac.technion.schemamatching.matchers.firstline.FLMList;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SLMList;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchema;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.testbed.OREDataSetEnum;
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
	private HashMap<Long,ArrayList<? extends ExperimentSchema>> experimentDatasets = new HashMap<Long,ArrayList<? extends ExperimentSchema>>();
	protected DBInterface db;
	private ExperimentDocumenter experimentDocumenter = new ExperimentDocumenter();
	private String dsurl;
	protected OntoBuilderWrapper obw;
	private XmlFileHandler xfh;
	
	/**
	 * Main method runs an experiment according to the supplied parameters
	 * @param args[0] run as command line (cmd) or console application (console)
	 * @param args[1] output path
	 * @param args[2] Experiment Type compared against enum types
	 *  PairExperimentEnum and HolisticExperimentEnum. If found in Pair,
	 *  a dataset of schema pairs will be created, if found in Holistic 
	 *  a dataset of schemas will be created.  
	 * @param args[3] K - number of experiments schema pairs
	 * @param args[4] schema pair ID Set ( e.g. 1,2,3  or 1 )  (ignored if K <> 0)
	 * @param args[5] datasetID (for random K)
	 * @param args[6] -d:domainCodes - (optional) string in the following format "2,3,4,2" (without the Quotation mark)
	 * or -f:First Line Matcher Codes or -p:properties file used to configure the experiment or -s:second line matcher codes (from db or enum)
	 * or -l:list of schema pair ids to be used in experiment (file name containing the list)
	 * 
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) 
	{
		OBExperimentRunner myExpRunner = getOER();
		File outputPath = null;
		PairExperimentEnum pe = null;
		HolisticExperimentEnum he = null;
		ArrayList<ExperimentSchema> dataset = new ArrayList<ExperimentSchema>();
		String expDesc = "None provided";
		HashSet<Integer> dc =new HashSet<Integer>();
		ArrayList<FirstLineMatcher> flm = null;
		ArrayList<SecondLineMatcher> slm = null; 
		Properties pFile = null;
		ArrayList<Long> spList = new ArrayList<Long>();
		boolean pairMode = true;
		if (args[0].equalsIgnoreCase("cmd"))
		{
			myExpRunner.checkInputParameters(args);
			outputPath = new File(args[1]); // folder in which temporary files will be saved
			if (!outputPath.exists()) fatalError("Output path not found");
			for (PairExperimentEnum e : PairExperimentEnum.values())
				if (e.name().equalsIgnoreCase(args[2]))
					pe = PairExperimentEnum.valueOf(args[2]); 
			
			for (HolisticExperimentEnum e : HolisticExperimentEnum.values())
				if (e.name().equalsIgnoreCase(args[2]))
				{
					he = HolisticExperimentEnum.valueOf(args[2]);
					pairMode = false;
				}
			
			if (pe==null && he == null) fatalError("Experiment type" + args[2] + " not recognized ");
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
	    				case 'l': spList = extractSPList(arguments);
	    			}
	    		}
	    		
	    	}
			if (spList.size()>0){ //Use manual list of Schema Pairs from file
				String spids = spList.get(0).toString();
				for (int i=1 ; i<spList.size();i++)
					spids = spids + "," + spList.get(i).toString();
				dataset =oer.selectExperiments(0,spids, Integer.valueOf(args[5]), dc,pairMode );}
			else if (Integer.valueOf(args[3])<=0){
				dataset =oer.selectExperiments(Integer.valueOf(args[3]),args[4], 0, dc ,pairMode);}  
			else{
		    	Integer datasetID = Integer.valueOf(args[5]);
		    	Integer size = Integer.valueOf(args[3]);
		    	expDesc =  "Experiment Type: " + args[2]+ " k=" + args[3] + " SPID: " + args[4] + " Dataset: " + args[5];
		    	if (dc == null) dc = new HashSet<Integer>();
				dataset = oer.selectExperiments(size,"0", datasetID, dc,pairMode);
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
		try{
			outputPath = new File(args[1]); // folder in which temporary files will be saved
		}
		catch (Exception e){
			fatalError("Output path not found"); 
			
		}
		
		//	if (!outputPath.exists()) fatalError("Output path not found"); 
			Scanner input = new Scanner(System.in);
			System.out.println("Welcome to the Ontobuilder Research Environment");
			System.out.println("Please select one of the following options:");
			System.out.println("1.Run Experiment");
			System.out.println("2.Quit");
		    String option=input.nextLine();
		    if (option.equals("")){
		    	option="1";
		    }
			if (option.equals("2")){
				System.out.println("Thank you for using the Ontobuilder Research Environment");
				return;
			}
			if (option.equals("1")) {
				System.out.println("Let's start:");
				System.out.println("Please select The type of Experiment you want to run "
						+ "from the the following options:(default = 1)");
				System.out.println("1. PairWiseExperiment");
				System.out.println("2. HolisticExperiment");
				option=input.nextLine();
			    if (option.equals("")){
			    	option="1";
			    }
				if (option.equals("2")) {
					pairMode=false;}
				if (pairMode) {
					System.out.println("Please select The Experiment you want to run "
							+ "from the the following options: (default = 0)");
					int ExperimentID;
					for (PairExperimentEnum e : PairExperimentEnum.values()){
						System.out.println(e.ordinal()+". "+e.name()+ " description: ");
						System.out.println("  -  "+e.getExperiment().getDescription());
					}		
					option=input.nextLine();
					if (option.equals("")){
						ExperimentID=0;
					}
					else{
						ExperimentID=Integer.parseInt(option);
					}
					
					for (PairExperimentEnum e : PairExperimentEnum.values()){
						if (ExperimentID==e.ordinal()){
							pe = e;}
					} 
					System.out.println("The Experiment you chose is: "+pe.name());
					}
				else{
					System.out.println("Please select The Experiment you want to run "
							+ "from the the following options:");
					int ExperimentID;
					for (HolisticExperimentEnum e : HolisticExperimentEnum.values()){
						System.out.println(e.ordinal()+". "+e.name()+ " description: ");
						System.out.println("  -  "+e.getExperiment().getDescription());
					}
					
					//ExperimentID=input.nextInt();
					ExperimentID = Integer.parseInt(input.nextLine());
					for (HolisticExperimentEnum e : HolisticExperimentEnum.values()){
						if (ExperimentID==e.ordinal()){
							he = e;}
					} 
					System.out.println("The Experiment you chose is: "+he.name());
				}
					System.out.println("Please select The Dataset you want to work with: (default = 1)");
					int datasetID;
					for (OREDataSetEnum d : OREDataSetEnum.values()){
						System.out.println(d.getDatasetDBid()+". "+d.name());
					}
					option=input.nextLine();
					if (option.equals("")){
						datasetID=1;
					}
					else{
						datasetID=Integer.parseInt(option);
					}
					System.out.println("Please select number (default = 1) of experiments schema pairs "
							+ "you want to work with:(for specific pairs choose 0)");
					int K=0;
					option=input.nextLine();
					if (option.equals("")){
						K=1;
					}
					else{
						K=Integer.parseInt(option);
					}
					String spid = null;
					if (K==0) {
						System.out.println("Please select Schema pair ID Set:"); 
						spid=input.nextLine();
					}
					if (K==0) {
						dataset =oer.selectExperiments(0,spid, 1, dc,pairMode);}
					else if (datasetID==0) {
						dataset =oer.selectExperiments(K,"0", 0, dc ,pairMode);}
					else {
						dataset = oer.selectExperiments(K,"0", datasetID, dc,pairMode);
					}
					String name = null;
					if (pairMode){
						name= pe.name(); 
					}
					else name= he.name();
					expDesc =  "Experiment Type: " + name + " k=" + K + " SPID: " + spid + " Dataset: " + datasetID;
					System.out.println("Please Select a comma separated set of FLM "
							+ "from the the following options: ");
					System.out.println("(for example: 0,1,5 will run an experiment "
							+ " using FLM numbers 0, 1 and 5 (default = 0))");	
					HashMap<Integer,String> FLM1 = new HashMap<Integer,String>(); 
					for (FLMList f : FLMList.values()){
						FLM1.put(f.getFLM().getDBid(),f.getFLM().getDBid()+ ". "+ f.name());
					}
					Map<Integer, String> FLMSorted = new TreeMap<Integer, String>(FLM1);
					for (String f : FLMSorted.values()){
						System.out.println(f);
					}
					String FlmWanted=null;
					FlmWanted = input.nextLine();
					if (option.equals("")){
						FlmWanted="0";
					}
					
					flm = parseFLMids(FlmWanted);
					
					System.out.println("Please Select a comma separated set of SLM "
							+ "from the the following options: ");
					System.out.println("(for example: 1,2,6 will run an experiment "
							+ " using SLM numbers 1, 2 and 7 (default = 1))");	
					HashMap<Integer,String> SLMSorted = new HashMap<Integer,String>(); 
					Integer MaxKey=0;
					for (SLMList s : SLMList.values()){
						SLMSorted.put(s.getSLM().getDBid(),s.getSLM().getDBid()+ ". "+ s.name());
						if (s.getSLM().getDBid()>MaxKey){ 
							MaxKey=s.getSLM().getDBid();
						}
					}
					MaxKey++;
					SLMSorted.put(MaxKey,MaxKey+". I don't want to use any SLM");
					for (String s : SLMSorted.values()){
						System.out.println(s);
					}
					String SlmWanted=null;
					SlmWanted = input.nextLine();
					if (option.equals("")){
						SlmWanted="1";
					}

					if (SlmWanted.equals(MaxKey.toString())) {
						System.out.println("Notice: No SLM were selected!");
					}
					else{
						slm = parseSLMids(SlmWanted);
					}
			}
			else {
				System.err.println(option+ " was not an option");
				System.exit(0);
			}
			printMainMenu();
			//TODO handle input
			
		}
		else
		{
			printGeneralInstructions();
			fatalError("invalid 1st parameter supplied");
		}
		Long eid = myExpRunner.initExperiment(dataset, expDesc);
		if (pairMode)
			
			myExpRunner.runPairWiseExperiment(pe,eid, outputPath,flm,slm,pFile);
		else //holistic mode
			myExpRunner.runHolisticExperiment(he,eid, outputPath,flm,slm,pFile);
	 }
	
	/**
	 * Runs the supplied holistic experiment
	 * @param he
	 * @param eid
	 * @param outputPath
	 * @param flm
	 * @param slm
	 * @param pFile
	 */
	private void runHolisticExperiment(HolisticExperimentEnum he, Long eid,
			File outputPath, ArrayList<FirstLineMatcher> flm,
			ArrayList<SecondLineMatcher> slm, Properties pFile) {
		@SuppressWarnings("unchecked")
		ArrayList<ExperimentSchema> dataset = (ArrayList<ExperimentSchema>) getDS(eid);
		HolisticExperiment e = he.getExperiment();
		e.init(this, pFile, flm, slm);
		outputPath = getFolder(outputPath);
		e.init(getOER(), pFile, flm, slm);
		List<Statistic> eRes = e.runExperiment(new HashSet<ExperimentSchema>(dataset));
		formatStatistics(eRes, outputPath);
		
	}

	/**
	 * Extracts list of schema pairs from supplied file. 
	 * @param arguments file name assumed to be in program root directory
	 * @return list of schema pair IDs
	 */
	private static ArrayList<Long> extractSPList(String arguments) {
		ArrayList<Long> res = new ArrayList<Long>();
		File spFile = new File("./" + arguments);
		int lineNumber = 0;
		if (!spFile.exists())
		{
			System.err.println("Invalid schema pair list file supplied:" + arguments);
		}
		else
		{
			BufferedReader br;
			try 
			{
				br = new BufferedReader(new FileReader(spFile));
			
				String strLine = "";
				while ((strLine = br.readLine()) != null && strLine.trim().length()>0) {
					lineNumber++;
					res.add(Long.parseLong(strLine));
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Read " + lineNumber + "lines from " + arguments );
		return res;
	}

	private static void printMainMenu() {
	
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
	 * @param flmCodes assumed to contain a comma separated list of first line matcher codes
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
			pMap.load(new FileInputStream("oreConfig/ob_interface.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println((String)pMap.get("dbmstype") + " " + (String)pMap.get("host") + " " + (String)pMap.get("dbname") + " " + (String)pMap.get("username") + " " + (String)pMap.get("pwd"));
	    db = new DBInterface(Integer.parseInt((String)pMap.get("dbmstype")),(String)pMap.get("host"),(String)pMap.get("dbname"),(String)pMap.get("username"),(String)pMap.get("pwd"));
		dsurl = (String)pMap.get("schemaPath");
		File dsFolder = new File(dsurl);
		if (dsFolder == null || !dsFolder.isDirectory()) fatalError("Supplied dataset url is invalid or unreachable");
		obw = new OntoBuilderWrapper();
		xfh = new XmlFileHandler();
		experimentDocumenter = new ExperimentDocumenter();

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
		if (args[0]==null) {fatalError("Please enter an output folder path");}
		if ( Integer.valueOf(args[3])==0 && args[4]==null ){fatalError("Please enter an number of experiment to sample or a spid");} 
		if (Integer.valueOf(args[3])<0) {fatalError("Illegal number of experiments to sample");}
		//TODO: fix this to work with schemata as well if ( Integer.valueOf(args[3])==0 && !findSPID(args[4]) ){fatalError("SPID wasn't found");}
		try {
				PairExperimentEnum.valueOf(args[2]);
		}
		catch (Exception e)
		{
			try {
				HolisticExperimentEnum.valueOf(args[2]);
		}
			catch (Exception e1)
		{
			fatalError("Invalid Matching Experiment Type.");
		}}
		
	}

	/**
	 * Returns the ExperimentDocumenter object for a given experiment ID
	 * @param eid Experiment ID
	 * @return ExperimentDocumenter object
	 */
	public ExperimentDocumenter getDoc() {
		return experimentDocumenter;
	}

	/**
	 * Returns the dataset for a supplied experiment id
	 * @param eid
	 * @return
	 */
	public ArrayList<? extends ExperimentSchema> getDS(Long eid) 
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
	 * @param pairMode if true assumes given schema IDs and returns schema pairs 
	 * @return ArrayList of Schema Experiments. 
	 */
	private ArrayList<ExperimentSchema> selectExperiments(int K, String spid, int datasetID, HashSet<Integer> domainCodes,boolean pairMode) 
	{
		if (datasetID == 0) datasetID = 1;
		ArrayList<ExperimentSchema> ds = new ArrayList<ExperimentSchema>();
		String sql = "";
		if (pairMode)
		{
			sql = "FROM `schemapairs`, `schemata` WHERE (`schemapairs`.`CandidateSchema` =" + 
					 " `schemata`.`SchemaID` OR `schemapairs`.`TargetSchema` = `schemata`.`SchemaID`) AND (`schemapairs`.`DSID` = ";
		}
		else
		{
			sql = "FROM `schemata` WHERE (`schemata`.`DSID` = ";
		}
		sql = sql + Integer.toString(datasetID) + ")" 
			  + (domainCodes.isEmpty()?"" : " AND ( schemata.`domainCode` IN ("
			  + domainCodes.toString().substring(1,domainCodes.toString().length()-1) + ") )");

		ArrayList<String[]> numberOfItems =  
				db.runSelectQuery("SELECT COUNT( DISTINCT " 
				+ (pairMode?"spid":"SchemaID") + ") " + sql, 1);
		//check the number of available experiments is larger then k
		if ((K>Integer.valueOf(numberOfItems.get(0)[0])) || ( K==0 && spid.split(",").length> Integer.valueOf(numberOfItems.get(0)[0]))) 
			fatalError("No. of experiments requested is larger than the no. of schema" 
					+ (pairMode?" pairs":"s") + " in the dataset");
		//extracting experiments from the DB
		if (K>0)
		{
			sql  = "SELECT DISTINCT " 
				+ (pairMode?"spid,schemapairs.DSID ":" SchemaID,DSID " ) + sql +" ORDER BY RAND() LIMIT " + String.valueOf(K) + ";"; 
		}
		else
		{
			if (spid!="0") 
				if (pairMode)
					sql = "SELECT spid, dsid FROM schemapairs WHERE SPID in (" + spid + ");" ;
				else
					sql = "SELECT SchemaID, DSID FROM schemata WHERE SchemaID in (" + spid + ");" ;
		}
		
		ArrayList<String[]> k_results =  db.runSelectQuery(sql, 2);
		for (String[] strRes : k_results)
		{
			int id = Integer.parseInt(strRes[0]);
			int dsid = Integer.parseInt(strRes[1]);
			ExperimentSchema exp;
			try {
			if (pairMode)
				
					exp = new ExperimentSchemaPair(id,dsid);
			else
				exp = new ExperimentSchema(id,dsid);
			
			ds.add(exp);
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
				System.err.println("Schema pair " + id + "skipped");
			}
			
		}

		return ds;
	}

	/**
	 * Sends the provided error message to the err stream and exits with code 1
	 * @param msg
	 */
	public static void fatalError(String msg) 
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
	 * Runs a pairwise experiment on the loaded dataset and writes the result to the file object supplied 
	 * @param eid Experiment to run
	 * @param resultFolder File object in which to create the results. Folder will be created if not exists
	 * @param flm list of first line matchers to use
	 * @param slm list of second line matchers to use
	 * @param properties file for configuring the experiment
	 */
	public void runPairWiseExperiment(PairExperimentEnum et, Long eid,File resultFolder, ArrayList<FirstLineMatcher> flm, ArrayList<SecondLineMatcher> slm,Properties pFile)
	{
		@SuppressWarnings("unchecked")
		ArrayList<ExperimentSchemaPair> dataset = (ArrayList<ExperimentSchemaPair>) getDS(eid);
		PairWiseExperiment e = et.getExperiment();
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		e.init(this, pFile, flm, slm);
		resultFolder = getFolder(resultFolder);
		int i = 0;
		for (ExperimentSchemaPair esp : dataset)
		{
			System.out.println("Starting " + esp.getID());
			List<Statistic> eRes = e.runExperiment(esp);
			if (eRes != null) res.addAll(eRes);
			System.out.println("finished " + esp.getID() + " : " + Integer.toString(++i) + " out of " + Integer.toString(dataset.size()));
		}
		List<Statistic> eRes = e.summaryStatistics();
		if (eRes != null) res.addAll(eRes);
		formatStatistics(res, resultFolder);	
	}
	
	public Long initExperiment(ArrayList<? extends ExperimentSchema> dataset,String desc)
	{
		long eid = experimentDocumenter.documentExperiment(desc, dataset);
		experimentDatasets.put(eid, dataset);
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
//		} else {
//				for (File testFile : testFolder.listFiles()) {
//					testFile.delete();
//				}
		}
		return testFolder;
	}


	/**
	 * Collects statistics by type and outputs in CSV format to folder specified
	 * @param res
	 * @param resultFolder 
	 */
	private void formatStatistics(List<Statistic> res, File resultFolder) {
		// Collect statistics by type
		HashMap<String,List<Statistic>> collected = new HashMap<String,List<Statistic>>();
		for (Statistic s : res)
		{
			List<Statistic> tmp;
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
			File f = new File(resultFolder,System.currentTimeMillis()+ statName + ".csv");
			String[] header = collected.get(statName).get(0).getHeader();
			ArrayList<String[]> data = new ArrayList<String[]>();
			for (Statistic s : collected.get(statName))
			{
				if (s.getData() != null)
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




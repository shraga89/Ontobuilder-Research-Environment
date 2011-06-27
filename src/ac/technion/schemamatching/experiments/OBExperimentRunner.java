/**
 * The schemamatching.experiments package includes experiments in schema matching
 * on the ontobuilder schema matching system and other utilities developed at the Technion schema matching research group. 
 * Experiments are run on a dataset library documented in a mysql database.  
 */
package ac.technion.schemamatching.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import schemamatchings.meta.match.MatchedAttributePair;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.util.BestMappingsWrapper;
import schemamatchings.util.MappingAlgorithms;
import schemamatchings.util.SchemaTranslator;
import smb_service.PropertyLoader;
import smb_service.SMB;
import technion.iem.schemamatching.dbutils.DBInterface;
import ac.technion.schemamatching.statistics.Statistic;

import com.infomata.data.DataFile;
import com.infomata.data.DataRow;
import com.infomata.data.TabFormat;
import com.modica.ontology.Ontology;
import com.modica.ontology.match.MatchInformation;

import java.util.Calendar;
import java.text.SimpleDateFormat;
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
	//Used to get from a matcher to it's ID
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
		//TODO externalize to Main(args[]) -- not sure what you mean by that, externalize what to main? 
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
	 * @param args[1] Experiment Type : "Clarity" or other?
	 * @param args[2] K - number of experiments for clarity, set 0 to use a specific ID
	 * @param args[3] mode for the SMB (E,L,R)
	 * @param args[4] schema pair ID (ignored unless K is 0
	 * @param args[5] datasetID
	 * @param args[6] HashSet<Integer> domainCodes - a string in the following format "2,3,4,2" (without the Quotation mark)
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
	    long eid = myExpRunner.getDoc().getEid();
	    /* TODO: 3 major refactoring required here. Create an experiment interface class, define what it includes and how it is documented in the DB.
	     * Extract experiment loading into a method. Extract the SMBservice / Clarity experiment into a method.   
	    */
  		  
	    Ontology target;
	    Ontology candidate;
	    String[] availableMatchers =  MatchingAlgorithms.ALL_ALGORITHM_NAMES;
        MatchInformation firstLineMI[]= new MatchInformation[availableMatchers.length];
	    String[] available2ndLMatchers = MappingAlgorithms.ALL_ALGORITHM_NAMES;
        SchemaTranslator secondLineST[] = new SchemaTranslator[available2ndLMatchers.length*availableMatchers.length];
	    int sysCode = 1; //Ontobuilder sysCode
		
	    // Make sure all matchers and similarity measures are documented in the DB with the right matcher ID
	    ArrayList<String[]> SMIDs = myExpRunner.getDoc().documentSimilarityMeasures(availableMatchers,sysCode );
	    // -------- recreated the function at ExperimentDocumenter, still depricated because I didn't understand your
	    // not about the enum or hashtable
	    
	    
	    
        ArrayList<String[]> MIDs = myExpRunner.getDoc().documentMatchers(available2ndLMatchers, sysCode);
        
        // 2 For each experiment in the list:
	    for (ExperimentSchemaPair schemasExp : myExpRunner.getDS()) 
	    {
			// 2.1 load from file into OB objects
	        target = schemasExp.getTargetOntology();
	        candidate = schemasExp.getCandidateOntology();
	        int spid = schemasExp.getSPID();
	        schemasExp.AddInfoAboutSchemaToDB(myExpRunner.db);
	        //2.2 1st line match using all available matchers in OB // missing similarity flooding -> adjustment were made lines: 74-77;
	        int counter = 0;
	        for (int m=0;m<availableMatchers.length;m++)
	        {				
				System.out.println ("Starting " + counter);
				firstLineMI[m] = schemasExp.getSimilarityMatrix(reversedmeasures.get(availableMatchers[m])); 
				BestMappingsWrapper.matchMatrix = firstLineMI[m].getMatrix();	
				// 2.3 2nd line match using all available matchers in OB with original matrix and document in DB   	
				for (int mp=0;mp<available2ndLMatchers.length;mp++)
				{   
					//get a matching from db, if 2nd line match not in db, perform matching
					ArrayList<String[]> mapping = myExpRunner.getMappings(available2ndLMatchers[mp],availableMatchers[m],spid,sysCode);
						if (mapping.isEmpty())
						{
							
							mapping = new ArrayList<String[]>();
							System.out.println ("doing " + counter + "." + available2ndLMatchers[mp] );
							secondLineST[mp*(m+1)] = BestMappingsWrapper.GetBestMapping(available2ndLMatchers[mp]);
							if (secondLineST[mp*(m+1)]==null)
							{
								System.err.println("empty match spid:" + spid + " smid: " +  availableMatchers[m] + "matcher:" + available2ndLMatchers[mp]);
								continue;
							}
							secondLineST[mp*(m+1)].importIdsFromMatchInfo(firstLineMI[m],true);
							
							for (MatchedAttributePair match : secondLineST[mp*(m+1)].getMatchedPairs())
							{
								String[] e = {Long.toString(spid),"","",Long.toString(myExpRunner.getDoc().getMID(available2ndLMatchers[mp],sysCode)),Long.toString(myExpRunner.getDoc().getSMID(availableMatchers[m],sysCode))};
								e[1] = Long.toString(match.id1);
								e[2] = Long.toString(match.id2);
								mapping.add(e);
							}
							
						}
						myExpRunner.getDoc().upload2ndLineMatchToDB(availableMatchers[m], available2ndLMatchers[mp],false, sysCode, spid, mapping,eid);
					}
					System.out.println ("Finished 2nd line matching " + counter);
					counter++;
	        }//end for of 1st line matcher
	        //TODO move this whole section to the ClarityExperiment
	        if (args[1].equals("Clarity"))
	        {
			//  2.4 Output schema pair, term list, list of matchers and matches to URL    
	        	outputArrayListofStringArrays(outputPath,SMIDs,"BasicConfigurations.tab");
	        	//order of schemas: Candidate and then target
	        	ArrayList<String[]> schemaRes = new ArrayList<String[]>();


	        	schemaRes.addAll(myExpRunner.db.runSelectQuery("SELECT `SchemaID`, `SchemaName`, `source`,`language`,`Real`,`Language`,`Max_Height_of_the_class_hierarchy`,`Number_of_association_relationships`, `Number_of_attributes_in_Schema`,  `Number_of_classes`,  `Number_of_visible_items`,  `Number_of_instances` FROM schemata,schemapairs,datasets WHERE schemapairs.DSID = datasets.DSID AND SchemaID = schemapairs.TargetSchema AND schemapairs.SPID = " + spid + ";", 12));
	        	
	        	outputArrayListofStringArrays(outputPath,schemaRes,"Schema.tab");
	        	outputArrayListofStringArrays(outputPath,myExpRunner.db.runSelectQuery("SELECT `SchemaID`, `Tid`, `DomainNumber`, `TName`, `Known Composite`, `Known Partial`" +
	        									"FROM `terms` WHERE SchemaID = " + schemasExp.getCandidateID() + " OR SchemaID = " + schemasExp.getTargetID() + ";", 6),"Item.tab");
	        	outputArrayListofStringArrays(outputPath,myExpRunner.db.runSelectQuery("SELECT `similaritymatrices`.`SMID` , `similaritymatrices`.`CandidateSchemaID` , `similaritymatrices`.`CandidateTermID` , `similaritymatrices`.`TargetSchemaID` , `similaritymatrices`.`TargetTermID` , `similaritymatrices`.`confidence`" +
	        								" FROM `experimentschemapairs` INNER JOIN `schemapairs` ON (`experimentschemapairs`.`SPID` = `schemapairs`.`SPID`)" +
	        								" INNER JOIN `similaritymatrices` ON (`schemapairs`.`TargetSchema` = `similaritymatrices`.`TargetSchemaID`) AND (`schemapairs`.`CandidateSchema` = `similaritymatrices`.`CandidateSchemaID`)" +
	        								" INNER JOIN `similaritymeasures` ON (`similaritymeasures`.`SMID` = `similaritymatrices`.`SMID`)" +
	        								" WHERE (`similaritymeasures`.`System` = " + sysCode + ") AND (EID = " + eid + ") AND `schemapairs`.`SPID` = " + schemasExp.getSPID() + ";", 6),"MatchingResult.tab");
	      		   		

			//2.5 run SMB_service in Enhance mode
    		SMB smb = new SMB();
    		smb.enhance(outputPath.getPath(), outputPath.getPath(), 2.0, .2, 2.0);

			//  2.6 load enhanced matching result into OB object
    			//load enhanced similarity matrix to ArrayList
    			ArrayList<String[]> enhancedMatrices = readFile(new File(outputPath,"EnhancedSimilarityMatrix.tab"));
    			//Split array list by SMIDs
    			HashMap<Long,ArrayList<String[]>> similarityMeasureMatrices = new HashMap<Long,ArrayList<String[]>>();
    			for (String[] sm : SMIDs) similarityMeasureMatrices.put(Long.parseLong(sm[0]), new ArrayList<String[]>());
    			for (String[] matrixRow : enhancedMatrices) similarityMeasureMatrices.get(Long.parseLong(matrixRow[0])).add(matrixRow);
    			//create MI object for each arraylist using createMIfromArrayList
    			HashMap<Long,MatchInformation> EnhancedMI = new HashMap<Long,MatchInformation>();
    			for ( long sm : similarityMeasureMatrices.keySet()) EnhancedMI.put(sm, DBInterface.createMIfromArrayList(candidate, target,similarityMeasureMatrices.get(sm) ) );
			// 2.7 2nd line match using all available matchers in OB with enhanced matrix
    			int eCounter = 0;
    			for (String[] smRow : SMIDs)
    			{
    				
    				long sm = Long.parseLong(smRow[0]);
    				String SMName = smRow[1];
    				// Update enhanced matrix and mapping results to db
					// Since these are enhanced matrices use true
    				//TODO Replace this with documentation of an experiment schema pair
    				myExpRunner.getDoc().loadSMtoDB(EnhancedMI.get(sm),schemasExp,true, (int)sm);
					//document ClarityScore.tab
	    			ArrayList<String[]> clarityRes = readFile(new File(outputPath,"ClarityScore.tab"));
	    			myExpRunner.getDoc().loadClarityToDB(clarityRes,schemasExp,(int)sm);
    				for(String secondLineM : available2ndLMatchers)
    				{
    					// Match
    					ArrayList<String[]> mapping = new ArrayList<String[]>();
						System.out.println ("doing enhanced" + eCounter + "." + secondLineM );
						SchemaTranslator st = BestMappingsWrapper.GetBestMapping(secondLineM);
						st.importIdsFromMatchInfo(EnhancedMI.get(sm),true);
						for (MatchedAttributePair match : st.getMatchedPairs())
						{
							String[] e = {Long.toString(spid),"","",Long.toString(myExpRunner.getDoc().getMID(secondLineM,sysCode)),smRow[0]};
							e[1] = Long.toString(match.id1);
							e[2] = Long.toString(match.id2);
							mapping.add(e);
						}
						
						// using false to separate the non enhanced from the enhanced result 
						myExpRunner.getDoc().upload2ndLineMatchToDB(SMName, secondLineM,true, sysCode, spid, mapping,eid);
					}
    				eCounter++;
    			}
	        }// end clarity experiment type
    			 
    	  
	  }// end for loop of experiment
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
	
	@SuppressWarnings("unchecked")
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
	 * if schema pair is mapped by this 2ndLineMatcher retrieve the mapping
	 * @param secondLineM 2nd Line Matcher Name
	 * @param SMName Similarity Measure Name (1st line matcher)
	 * @param spid Schema Pair ID
	 * @param sysCode Matching system code
	 * @deprecated TODO Mappings are experiment specific, consider if needed at all
	 * @return mapping : ArrayList of [SPID,CandidateTermID,TargetTermID,MatcherID,SMID] arrays
	 */
	private ArrayList<String[]> getMappings(String secondLineM, String SMName, long spid, int sysCode) 
	{
		String sql = " SELECT DISTINCT `mapping`.`SPID`, `mapping`.`CandidateTermID` , `mapping`.`TargetTermID` , `mapping`.`MatcherID`, `mapping`.`SMID` FROM `similaritymeasures`" +
					" INNER JOIN `mapping` ON (`similaritymeasures`.`SMID` = `mapping`.`SMID`)" +
					" INNER JOIN `matchers` ON (`matchers`.`MatcherID` = `mapping`.`MatcherID`)" +
					" WHERE (`mapping`.`SPID` = " + spid + " AND `similaritymeasures`.`MeasureName` = '" + SMName +
					" ' AND `matchers`.`MatcherName` = '" + secondLineM + "' AND `similaritymeasures`.`System` = " + sysCode +
					" AND `matchers`.`System` = " + sysCode + "1);";
		return db.runSelectQuery(sql, 5);
	}
	
	
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

	
	

	/**
	 * readFile supplied into ArrayList of string arrays
	 * @param f File to read
	 * @return Array list of string arrays
	 * @deprecated TODO move to SMBService experiment
	 */
	private static ArrayList<String[]> readFile(File f)
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
	 * @param resultFile File object to which to append the result. File will be created if not exists
	 */
	public void runExperiment(ExperimentType et,File resultFile)
	{
		MatchingExperiment e = et.getExperiment();
		ArrayList<Statistic> res = new ArrayList<Statistic>();
		e.init(this, properties, om);
		for (ExperimentSchemaPair esp : dataset)
		{
			res.addAll(e.runExperiment(esp));
		}
		resultFile = formatStatistics(res);	
	}
	
	/**
	 * Collects statistics by type and outputs in CSV format
	 * @param res
	 * @return
	 */
	private File formatStatistics(ArrayList<Statistic> res) {
		// TODO Auto-generated method stub
		return null;
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




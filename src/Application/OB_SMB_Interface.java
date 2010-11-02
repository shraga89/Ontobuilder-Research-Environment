/**
 * 
 */
package Application;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import schemamatchings.meta.match.MatchedAttributePair;
import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.ontobuilder.OntoBuilderWrapperException;
import schemamatchings.util.BestMappingsWrapper;
import schemamatchings.util.MappingAlgorithms;
import schemamatchings.util.SchemaMatchingsUtilities;
import schemamatchings.util.SchemaTranslator;
import smb_service.DBInterface;
import smb_service.Field;
import smb_service.Field.FieldType;
import smb_service.PropertyLoader;
import smb_service.SMB;

import com.infomata.data.DataFile;
import com.infomata.data.DataRow;
import com.infomata.data.TabFormat;
import com.modica.ontology.Ontology;
import com.modica.ontology.Term;
import com.modica.ontology.match.MatchInformation;
/**
 * @author Tomer Sagi and Nimrod Busany
 * Input: K - number of experiments to run (an integer)
 */
public class OB_SMB_Interface {

	/**
	 * Main method will load K experiments from an existing DB
	 * Input: args:[url - used for storing temporary files, K - number of experiments to run]
	 * 
	 */
	static double TIMEOUT = 20 * 1000; 
	static String DSURL = "C:\\Ontologies\\Ontology Pairs and Exact Mappings\\";
	public static void main(String[] args) throws NumberFormatException, Exception 
	{
		
	// 1 Load K experiments into an experiment list
	    
	    File outputPath = new File(args[0]); // folder in which temporary files will be saved
	    Properties pMap = PropertyLoader.loadProperties("resources");
	    DBInterface db = new DBInterface(Integer.parseInt((String)pMap.get("dbmstype")),(String)pMap.get("host"),(String)pMap.get("dbname"),(String)pMap.get("username"),(String)pMap.get("pwd"));
		ArrayList<SchemasExperiment> ds = UploadKExperiments(db,Integer.parseInt(args[1]));
		SchemasExperiment schemasExp = new SchemasExperiment();
  		System.out.println("DS size is: " + ds.size());
	    System.exit(1);
	    writeBasicConfigurations(schemasExp.getSubDir().getAbsolutePath(),schemasExp.getSPID(),outputPath);
	    //if (flag) System.out.println("Exists");
	    //else System.out.println("Don't Exists");
  		
	   
	    Ontology target;
	    Ontology candidate;
	    OntoBuilderWrapper obw = new OntoBuilderWrapper();
	    SchemaTranslator boostingBestMapping = null;
	    SchemaTranslator exactMapping;
	    double boostPrecision = 0.0;
	    double boostRecall = 0.0;
	    String sOutput;
      
	    String[] availableMatchers =  MatchingAlgorithms.ALL_ALGORITHM_NAMES;
        MatchInformation firstLineMI[]= new MatchInformation[availableMatchers.length];
        SchemaTranslator firstLineST[] = new SchemaTranslator[availableMatchers.length];
        MatchMatrix firstLineMM[]= new MatchMatrix[availableMatchers.length];
	    String[] available2ndLMatchers = MappingAlgorithms.ALL_ALGORITHM_NAMES;
        SchemaTranslator secondLineST[] = new SchemaTranslator[available2ndLMatchers.length*availableMatchers.length];
	    
		// TODO 2 For each experiment in the list:
	    for (int i = 0; i < ds.size(); ++i) {  //size
			// TODO 2.1 load from file into OB objects
	        schemasExp = ds.get(i);
	        target = schemasExp.getTargetOntology();
	        candidate = schemasExp.getCandidateOntology();
	        exactMapping = schemasExp.getExactMapping();
	        
	        
			// TODO 2.2 1st line match using all available matchers in OB // missing similarity flooding -> adjustment were made lines: 74-77;
	        try {
	        
	        for (int m=0;m<availableMatchers.length;m++)
	        {				
					firstLineMI[m] = obw.matchOntologies(candidate, target,availableMatchers[m]);
					// boolean was set to null as default choice
					firstLineST[m] = new SchemaTranslator(firstLineMI[m]);
					firstLineST[m].importIdsFromMatchInfo(firstLineMI[m],true);
	        }
	        // TODO 2.3 2nd line match using all available matchers in OB with original matrix
	       
	        int mp2=0;
	        for (int m=0;m<availableMatchers.length;m++)
	        {
	        	BestMappingsWrapper.matchMatrix = firstLineMI[m].getMatrix();
	        	//writeMatchMatrixToDB (firstLineMI[m],schemasExp,DBInterface);
	        	firstLineMM[m] = firstLineMI[m].getMatrix();
	        	for (int mp=0;mp<available2ndLMatchers.length;mp++) // Note: I changed from: for (int mp=0;mp<available2ndLMatchers.length*availableMatchers.length;mp++)
		        {   
		        	if (BestMappingsWrapper.GetBestMapping(available2ndLMatchers[mp])!=null)
		        		{
		        		secondLineST[mp2] = BestMappingsWrapper.GetBestMapping(available2ndLMatchers[mp]);
				        secondLineST[mp2].importIdsFromMatchInfo(firstLineMI[m],true);
				        secondLineST[mp2].setAlgorithm(m,mp,availableMatchers[m],available2ndLMatchers[mp]);
				        int conf = secondLineST[mp2].getConfigurationNum();
				        String st = secondLineST[mp2].getConfiguration();
				        //BasicConfigurationTable[] values;
				        mp2++;
		        		}
		        	else {continue;};
		        	//if (System.currentTimeMillis()-time > TIMEOUT) break;		        	
		        }
		        //if (System.currentTimeMillis()-time > TIMEOUT) break;
	        }
	        System.out.println (mp2 + " combinations of matchers");
	        } catch (OntoBuilderWrapperException e) {
				e.printStackTrace();
			}
			// TODO 2.4 Output schema pair, term list, list of matchers and matches to URL     	
	      	try 
	      		{
	      		//writeItems(firstLineMM,schemasExp.getCandidateId() , DBInterface, true);
	      		//writeItems(firstLineMM,schemasExp.getTargetId() , db, false);
	      		//writeMatchingResult(secondLineST,target.getName(), candidate.getName());   		
    			//writeSchema(target,candidate);
	      		}
    		catch (Exception e)
    			  {
    				System.err.print("Enhanced Similarity Matrix File Creation Failed");  
    				e.printStackTrace();
    			  }
			// TODO 2.5 run SMB_service with args: E URL
    		//SMB smb = new SMB();
    		//SMB.SMBRun (db,"E","C:\\Documents and Settings\\Administrator\\Desktop\\project\\frames",null,null,null,null);

			// TODO 2.6 load enhanced matching result into OB object
	        	//Look at LoadWorkingSet from SMB.java
			// TODO 2.7 2nd line match using all available matchers in OB with enhanced matrix
	        
			// TODO 2.8 Calculate Precision and recall for each matrix (regular and enhanced) and output to txt file) 
	        boostPrecision = SchemaMatchingsUtilities.calculatePrecision(exactMapping, boostingBestMapping);
	        boostRecall = SchemaMatchingsUtilities.calculateRecall(exactMapping, boostingBestMapping);
	        
	        sOutput = candidate.getName() + "\t";
	        sOutput += target.getName() + "\t";
	        sOutput += boostPrecision + "\t";
	        sOutput += boostRecall + "\t";
	       
	        try{
	          //outPutFile.write(sOutput);
	        }catch(Exception e){
	          System.out.println("1. " + e.toString());
	        } 

	      //}
	    //}
	    //catch (Exception e) {
	      //System.out.println("2. " + e.toString());
	    //}

	    try{
	    //  outPutFile.flush();
	    }catch(Exception e){
	      System.out.println("3. " + e.toString());
	    }  
	  }

	}

	private static void writeMatchMatrixToDB(MatchInformation matchInformation,
			// TODO
		SchemasExperiment schemasExp, DBInterface db) {
		// check if this experiments was run before, if so, only update the similarities/or skip
		// else write the matrix to DB
		String sql  = "SELECT EID FROM experiments"; 
		ArrayList<String[]> experiments = db.runSelectQuery(sql, 1);
		Iterator<String[]> it = experiments.iterator();	
		while (it.hasNext()){
			if (Long.valueOf(it.next()[0]) == schemasExp.getSPID()) return;
		}
	}

	private static ArrayList<SchemasExperiment> UploadKExperiments(DBInterface db, int K) throws Exception {
		
		ArrayList<SchemasExperiment> ds = new ArrayList<SchemasExperiment>();
		String sql = "SELECT COUNT(*) FROM schemapairs";
		ArrayList<String[]> NumberOfSchemaPairs =  db.runSelectQuery(sql, 1);
		//check the number of available experiments is larger then k
		if (K>Integer.valueOf(NumberOfSchemaPairs.get(0)[0])) throw new Exception("K is too large");
		//extracting pairs from the DB
		sql  = "SELECT * FROM schemapairs ORDER BY RAND() LIMIT ";
		sql = sql.concat(String.valueOf(K)); 
		ArrayList<String[]> k_Schemapairs =  db.runSelectQuery(sql, 5);
		for (int i=0;i<K;i++){
		String full_url = DSURL;
		String url = parseFolderPathFromSchemapairs((k_Schemapairs.get(i))[4]);
		full_url = full_url.concat(url);
		File f = new File(full_url);
		SchemasExperiment schemasExp = new SchemasExperiment(f);
		ds.add(schemasExp);
		}
		//document the new experiment in to DB 
		writeExperimentsToDB(db,ds);
		return ds;
	}

	//this function returns false if writing experiment into DB fails (due to missing ontology files, etc.)
	private static boolean writeExperimentsToDB(DBInterface db, ArrayList<SchemasExperiment> ds) {
		
		//document the experiment into experiment table
		String sql  = "SELECT MAX(EID) FROM experiments";
		ArrayList<String[]> LastEID =  db.runSelectQuery(sql, 1);
		//settings experiments ID (will be a sequential number to the last EID) 
		long currentEID;
		//if the table is empty
		if (LastEID.get(0)[0]==null) currentEID=1;
		else currentEID = (long)Integer.valueOf(LastEID.get(0)[0])+1;
		
		HashMap<Field,Object> values = new HashMap<Field,Object>();	
		Object obj = new Object();
		
		Field f = new Field ("EID", FieldType.LONG );
		values.put(f, (Object)currentEID );
		
		f = new Field ("RunDate", FieldType.TIME );
		Time time = new Time(1);
		values.put(f, (Object)time);

		f = new Field ("ExperimentDesc", FieldType.STRING);
		String str = ("SMB(E," + DSURL + ",s)");
		values.put(f, (Object)str);
		
		db.insertSingleRow(values, "experiments");

		//document the experiment into experimentschemapairs table
		Iterator <SchemasExperiment> it = ds.iterator();
		while (it.hasNext()){
			values = new HashMap<Field,Object>();	
			obj = new Object();
		
			f = new Field ("EID", FieldType.LONG );
			values.put(f, (Object)currentEID);
			//Time time = new Time(1);
		
			f = new Field ("SPID", FieldType.LONG);
			long SPID =  it.next().getSPID();
			values.put(f, (Object)SPID);
		
			f = new Field ("Training", FieldType.BOOLEAN);
			boolean training =  false;
			values.put(f, (Object)training);

			db.insertSingleRow(values, "experimentschemapairs");
		//parse ontologies terms
			try {
				Ontology Candidate = it.next().getCandidateOntology();
				Ontology Target = it.next().getTargetOntology();
		
				long CandidateID = it.next().getOntologyDBId(Candidate.getName(),db);
				long TargetID = it.next().getOntologyDBId(Target.getName(),db);
		
				if ((CandidateID == -1) || (TargetID==-1))
			return false;
	
		//before parsing the given ontology, we check that they don't already exist in out DB
				if (checkOntologyTermsExistenceAtDB(CandidateID,db))
					System.out.println ("Ontology: " + Candidate.getName() + " was alreay parsed");
				else
					WriteTermsToDB(CandidateID, Candidate, db);
		
				if (checkOntologyTermsExistenceAtDB(TargetID,db))
					System.out.println ("Ontology: " + Candidate.getName() + " was alreay parsed");
				else 
					WriteTermsToDB(TargetID, Target, db);
		
			}
			catch (Throwable e){
				return false;
			}
		}
		return true;
		
	}
		
	//this method checks if the ontology was parsed into terms or not
	private static boolean checkOntologyTermsExistenceAtDB(long OntologyId,DBInterface db) {
		//check if this field was already inserted into the table
		String sql = "SELECT SchemaID From terms WHERE Tid=" + OntologyId + ";";
		ArrayList<String[]> existInDB =  db.runSelectQuery(sql, 1);
		Iterator<String[]> exists = existInDB.iterator();	
		if (exists.hasNext())
			return true;
		return false;
	}

	private static void WriteTermsToDB(long OntologyID, Ontology ontology,
			DBInterface db) {
		
		Vector<Term> terms = ontology.getModel().getTerms();
		Iterator<Term> it = terms.iterator();
		
		while (it.hasNext()){
			
		HashMap<Field,Object> values = new HashMap<Field,Object>();	
		Term t = (Term) it.next();
		
		Field f = new Field ("SchemaID", FieldType.LONG );
		values.put(f, (Object)OntologyID);
		
		long id = SchemasExperiment.PJWHash(t.getName());
		//Only put into DB Terms with names (meaning name =! empty string)
		if  (!checkTermExistenceAtDB(db,id,OntologyID))
			{
			f = new Field ("Tid", FieldType.LONG );
			values.put(f, (Object)(id) );
		
			f = new Field ("TName", FieldType.STRING );
			values.put(f, (Object)t.getName());
		
			f = new Field ("TType", FieldType.INT);
			values.put(f, (Object)getDomainNumber(t.getDomain().toString()));
		
			db.insertSingleRow(values,"terms");
			}
		else {
			System.out.println("Term: " + t.getName() + " from ontology " + ontology.getName() +  " was parsed");
		}
		}
	}

	private static boolean checkTermExistenceAtDB(DBInterface db, long id,
			long ontologyID) {
	String sql = "SELECT SchemaID,Tid From terms WHERE Tid=" + id +  " AND SchemaID=" + ontologyID + ";";
	ArrayList<String[]> existInDB =  db.runSelectQuery(sql, 1);
	Iterator<String[]> exists = existInDB.iterator();
	
	if (exists.hasNext())
		return true;
	return false;
	}

	//private static void writeToDB(DBInterface db, HashMap<Field,Object> values, String table) throws SQLException {
	//	db.insertSingleRow(values, table);		
	//}

	private static String parseFolderPathFromExperiment(String url) {
		String str[] = url.split(",");
		String str2[] = str[1].split("/");
		return str2[0];
	}
	
	private static String parseFolderPathFromSchemapairs(String url) {
		String str[] = url.split("/");
		return str[0];
	}


	private static void writeSchema(Ontology target, Ontology candidate) throws IOException {
		DataFile write = DataFile.createWriter("8859_1", false);
		write.setDataFormat(new TabFormat());
		File outputPath = new File("c:\\smb");
		File outputSMFile = new File(outputPath,"Schema.tab");
		write.open(outputSMFile);
		DataRow row = write.next();
		printSchema(row,target);
		row = write.next();
		printSchema(row,candidate);
		row = write.next();
		write.close();
	}

	private static void printSchema(DataRow row, Ontology target) {
		row.add(target.getModel().getId());// Schema ID
		row.add(target.getName() + "  " + target.getModel().getName()+ "  " + target.getModel().getTitle());// Schema Name
		row.add("x");//Source: 1=Web From, 2=Relational DB, 3=Web Service Description
		row.add("x");//Language: int -> From codepage specification
		row.add("x");//Real: Boolean-> True if generated from a production system, False if generated via synthetic manipulation
		row.add("x");//Original modeling language: 1: XSD, 2: OWL, 3: RDF, 4: ProprietyDTD
		row.add(target.getHeight());//Max Height of the class hierarchy (Numerical Discrete)
		row.add("x");//# of subclass relationships
		row.add("x");//#of association relationships
		row.add(target.getComponentCount()+ " " + target.getComponentCount() + "  " + target.getModel().getTermsCount() + "  " + target.getTermsCount());//# of attributes in Schema
		row.add(target.getModel().getClassesCount());//# of classes
		row.add("x");//# of visible items
		row.add("x");//# of instances
		
	}

	@SuppressWarnings("unchecked")
	private static void writeItems(MatchMatrix[] firstLineMM, String schemaId, DBInterface db, boolean isTarget) throws IOException {
		DataFile write = DataFile.createWriter("8859_1", false);
		write.setDataFormat(new TabFormat());
		File outputPath = new File("c:\\smb");
		File outputSMFile = new File(outputPath,"item.tab");
		write.open(outputSMFile);
		DataRow row = write.next();
		Term s;
		boolean existsInDB = false;
		int i=0;
		
		//check if target terms have been entered into the DB
		ArrayList<String[]> schemaIds = db.runSelectQuery("SELECT SchemaId FROM terms", 1);
		Iterator it = schemaIds.iterator();
		while (it.hasNext()){
			if ((schemaIds.get(i))[0].contains(schemaId)  )
			{
				existsInDB = true;
				break;
			}
			i++;
		}
		if (!existsInDB)writeItemsToDB();
		
		i=0;
		ArrayList <Term> targeTerms;
		if (isTarget)targeTerms = firstLineMM[i].getTargetTerms();
		else targeTerms = firstLineMM[i].getCandidateTerms();
		for (i=0;i<targeTerms.size();i++)
		{
			row.add (schemaId); // schema id
			row.add (targeTerms.get(i).getId()); // trem id
			row.add (targeTerms.get(i).getName()); // trem name
			row.add (targeTerms.get(i).getDomain().getName()); // term type
			int domainNum = getDomainNumber(targeTerms.get(i).getDomain().getName());
			row.add (domainNum); // Categorical Discrete
			row.add (0);
			row.add (0);
			row=write.next();
		}
		row.add ("other way");
		// TODO double check correctness of all fields, convert type to int, notice that date is test (what to do);
		String sql = "SELECT * FROM terms Where SchemaId = " +  schemaId;
		ArrayList<String[]> Terms = db.runSelectQuery( sql , 4);
		it = Terms.iterator();
		i=0;
		while (it.hasNext()){
			row.add ((schemaIds.get(i))[0]); // ontology id
			row.add ((schemaIds.get(i))[1]); // term id
			row.add ((schemaIds.get(i))[2]); // trem name
			row.add ((schemaIds.get(i))[3]); // Categorical Discrete
			row=write.next();
			i++;
		}
		write.close();
	}

	private static void writeItemsToDB() {
		// TODO Auto-generated method stub
		
	}

	private static int getDomainNumber(String domain) {
		if ( domain.equalsIgnoreCase("Text")) 
			return 1;
		if (domain.equalsIgnoreCase("Choice")) 
			return 2;
		if (domain.equalsIgnoreCase("Date") || domain.equalsIgnoreCase("Time")) 
			return 3;
		if (domain.equalsIgnoreCase("Integer") || domain.equalsIgnoreCase("Pinteger") || domain.equalsIgnoreCase("Ninteger") )
			return 4;
		if (domain.equalsIgnoreCase("Float") || domain.equalsIgnoreCase("Number") ) 
			return 5;
		if (domain.equalsIgnoreCase("Float") || domain.equalsIgnoreCase("Number")) 
			return 5;
		if (domain.equalsIgnoreCase("Boolean")) 
			return 6;
		if (domain.equalsIgnoreCase("Url")) 
			return 7;
		if (domain.equalsIgnoreCase("Email")) 
			return 8;		
		return 0;
	}

	private static void writeMatchingResult(SchemaTranslator[] secondLineST, String target, String candidate) throws IOException {
		DataFile write = DataFile.createWriter("8859_1", false);
		write.setDataFormat(new TabFormat());
		File outputPath = new File("c:\\smb");
		File outputSMFile = new File(outputPath,"MatchingResult.tab");
		write.open(outputSMFile);
		DataRow row = write.next();
		int i=0;
		while (secondLineST[i]!=null)
		{
		MatchedAttributePair[] attibuteArray = secondLineST[i].getMatchedPairs();
		for (int j=0; j<attibuteArray.length; j++)
		{
		row.add(secondLineST[i].getConfigurationNum()); 
		row.add(secondLineST[i].getConfiguration());
		row.add(target);
		row.add(attibuteArray[j].id1); 
		row.add(candidate);
		row.add(attibuteArray[j].id2);
		row.add(attibuteArray[j].getMatchedPairWeight());  //check with tomer
		row=write.next();
		}
		i++;
		}
		write.close();
	}

	private static void writeBasicConfigurations(String url, long EID, File outputPath) throws IOException {
		DataFile write = DataFile.createWriter("8859_1", false);
		write.setDataFormat(new TabFormat());
		File outputSMFile = new File(outputPath,"BasicConfigurations.tab");
		write.open(outputSMFile);
		DataRow row = write.next();
		row.add(EID); //Configuration ID
		row.add("SMB(E," + url + ",s)"); //Configuration name
		write.close();
	}

	/*
	 * Example of a method to create a tab delimited file
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
				//row.add(smb.ws.candidateSchema.get("ID")); //Candidate schema ID
				//row.add(smb.ws.targetSchema.get("ID")); //Target schema ID
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
	 * Receives an Ontology object, returns a Hashmap with ambiguity values for each term
	 * @category NisB
	 * @param o Ontology to parse
	 */
	@SuppressWarnings("unused")
	private static HashMap<Long,Integer> generateAmbiguity(Ontology o, DBInterface db)
	{
		HashMap<Long, Integer> res = new HashMap<Long, Integer>();
		
		return res;
		
	}
}





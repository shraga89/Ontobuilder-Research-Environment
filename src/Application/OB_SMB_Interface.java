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
import com.modica.ontology.OntologyClass;
import com.modica.ontology.Term;
import com.modica.ontology.match.Match;
import com.modica.ontology.match.MatchInformation;

/**
 * @author Tomer Sagi and Nimrod Busany 1
 * Input: K - number of experiments to run (an integer)
 */

public class OB_SMB_Interface {

	/**
	 * @param args[0] Output folder 
+	 * @param args[1] Experiment Type : "Clarity" or other?
+	 * @param args[2] K - number of experiments for clarity
+	 * @param args[3] mode for the SMB (E,L,R)
	 * Note: Set paramaters for connecting the DB and set the path of the schema matching at the resouces.properties 
	 */
	static double TIMEOUT = 20 * 1000;
	public static String DSURL = "";
	private static long eid;
	public static void main(String[] args) throws NumberFormatException, Exception 
	{
		File outputPath = new File(args[0]); // folder in which temporary files will be saved
	    Properties pMap = PropertyLoader.loadProperties("resources");
	    DBInterface db = new DBInterface(Integer.parseInt((String)pMap.get("dbmstype")),(String)pMap.get("host"),(String)pMap.get("dbname"),(String)pMap.get("username"),(String)pMap.get("pwd"));
	    DSURL = (String)pMap.get("schemaPath"); 
	
	    
	    
	// 1 Load K experiments into an experiment list
	    
	    ArrayList<SchemasExperiment> ds = loadKExperiments(db,Integer.parseInt(args[2]));
		SchemasExperiment schemasExp = new SchemasExperiment();
  		System.out.println("DataSet size is: " + ds.size());
	    //writeBasicConfigurations(schemasExp.getSubDir().getAbsolutePath(),schemasExp.getSPID(),outputPath);
  		
	    Ontology target;
	    Ontology candidate;
	    OntoBuilderWrapper obw = new OntoBuilderWrapper();
	    SchemaTranslator exactMapping;
	    String sOutput;
      
	    String[] availableMatchers =  MatchingAlgorithms.ALL_ALGORITHM_NAMES;
        MatchInformation firstLineMI[]= new MatchInformation[availableMatchers.length];
        SchemaTranslator firstLineST[] = new SchemaTranslator[availableMatchers.length];
        MatchMatrix firstLineMM[]= new MatchMatrix[availableMatchers.length];
	    String[] available2ndLMatchers = MappingAlgorithms.ALL_ALGORITHM_NAMES;
        SchemaTranslator secondLineST[] = new SchemaTranslator[available2ndLMatchers.length*availableMatchers.length];
	    int sysCode = 1; //Ontobuilder sysCode
		// Make sure all matchers and similarity measures are documented in the DB with the right SMID
        ArrayList<String[]> SMIDs = documentSimilarityMeasures(db,availableMatchers,sysCode );
        ArrayList<String[]> MIDs = documentMatchers(db,available2ndLMatchers,sysCode);
        
		// 2 For each experiment in the list:
	    for (int i = 0; i < ds.size(); ++i) {  //size
			// 2.1 load from file into OB objects
	        schemasExp = ds.get(i);
	        target = schemasExp.getTargetOntology();
	        candidate = schemasExp.getCandidateOntology();
	        exactMapping = schemasExp.getExactMapping();
	        //TODO 1.add to each schema experiments the SPID and the onology's ID4
	        AddInfoAboutSchemaToDB((long)schemasExp.getTargetID(),target,db);
	        AddInfoAboutSchemaToDB((long)schemasExp.getCandidateID(),candidate,db);
	        //writeBasicConfigurations(url, EID, outputPath);
	        //2.2 1st line match using all available matchers in OB // missing similarity flooding -> adjustment were made lines: 74-77;
	        try {
	        int counter = 0;
	        for (int m=0;m<availableMatchers.length;m++)
	        {				
					System.out.println ("Starting " + counter);
					//check if we haven't run current first line matcher on this pair before, if we didn't run matchers, run; 
					 
					if (!checkIfSchemaPairWasMatched(schemasExp.getSPID(),db))
						{
						firstLineMI[m] = obw.matchOntologies(candidate, target,availableMatchers[m]);
						//firstLineST[m] = new SchemaTranslator(firstLineMI[m]);
						//firstLineST[m].importIdsFromMatchInfo(firstLineMI[m],true);
						firstLineMM[m] = firstLineMI[m].getMatrix();
						loadSMtoDB(firstLineMI[m],schemasExp,m,db);
						//TODO find a way to travers the terms correctly
						}
					else // TODO: if already matched, retrieve from DB 
						{
							ArrayList<String[]> firstLineMMfromDB = getSimilarityMatrix(db,availableMatchers[m], sysCode, (long) schemasExp.getSPID());
							MatchInformation mi = new MatchInformation();
							mi.setCandidateOntology(candidate);
							mi.setTargetOntology(target);
							ArrayList<Match> matches = new ArrayList<Match>();
							for (String[] matchFromDB : firstLineMMfromDB) matches.add(new Match(candidate.getTermByID(Long.parseLong(matchFromDB[1])), target.getTermByID(Long.parseLong(matchFromDB[3])),Double.parseDouble(matchFromDB[4])));
							mi.setMatches(matches );
						}
					BestMappingsWrapper.matchMatrix = firstLineMI[m].getMatrix();
					if (m==availableMatchers.length-1)
						updateWasMatchedWithAllMatchers(schemasExp.getSPID(),db);
					
					//searchIfSMIDwasUsedOnPair(availableMatchers[m],db);
					// 2.3 2nd line match using all available matchers in OB with original matrix   	
					for (int mp=0;mp<available2ndLMatchers.length;mp++) // Note: I changed from: for (int mp=0;mp<available2ndLMatchers.length*availableMatchers.length;mp++)
						{   
						if (BestMappingsWrapper.GetBestMapping(available2ndLMatchers[mp])!=null)
		        			{
							System.out.println ("doing " + counter + "." + available2ndLMatchers[mp] );
							secondLineST[mp*(m+1)] = BestMappingsWrapper.GetBestMapping(available2ndLMatchers[mp]);
							secondLineST[mp*(m+1)].importIdsFromMatchInfo(firstLineMI[m],true);
							secondLineST[mp*(m+1)].setAlgorithm(m,mp,availableMatchers[m],available2ndLMatchers[mp]);
							int conf = secondLineST[mp*(m+1)].getConfigurationNum();
							String st = secondLineST[mp*(m+1)].getConfiguration();
							//BasicConfigurationTable[] values;
		        		}
						else {continue;};
		        		//if (System.currentTimeMillis()-time > TIMEOUT) break;		        	
						}
					System.out.println ("Finshed " + counter);
					counter++;
		        //if (System.currentTimeMillis()-time > TIMEOUT) break;
	        	}
	        } catch (OntoBuilderWrapperException e) {
				e.printStackTrace();
			}
			//  2.4 Output schema pair, term list, list of matchers and matches to URL    
	        try 
	      		{
	        	outputArrayListofStringArrays(outputPath,SMIDs,"BasicConfigurations.tab");
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
    		SMB smb = new SMB();
    		smb.enhance(outputPath.getPath(), outputPath.getPath(), 2.0, .2, 2.0);

			// TODO 2.6 load enhanced matching result into OB object
	        	//Look at LoadWorkingSet from SMB.java
			// TODO 2.7 2nd line match using all available matchers in OB with enhanced matrix
	        
			// TODO 2.8 Calculate Precision and recall for each matrix (regular and enhanced) and output to txt file) 
    		System.out.println ("Success");
    		System.exit(0);
    		SchemaTranslator boostingBestMapping = null;
    	    double boostPrecision = SchemaMatchingsUtilities.calculatePrecision(exactMapping, boostingBestMapping);
    	    double boostRecall = SchemaMatchingsUtilities.calculateRecall(exactMapping, boostingBestMapping);
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

	/**
	 * Return the similarity matrix of the supplied schema pair using the similarity measure supplied
	 * If the matrix isn't documented in the db, returns nul
	 * @param db Database to search in
	 * @param smid Similarity measure (1st line matcher) ID to look for
	 * @param spid Schema Pair ID to look for
	 * @return String array {CandidateSchemaID,CandidateTermID,TargetSchemaID,TargetTermID,confidence} or null if data isn't found
	 */
	private static ArrayList<String[]> getSimilarityMatrix(DBInterface db,String smName, int sysCode, long spid) 
	{
		String sql = "SELECT `similaritymatrices`.`CandidateSchemaID` , `similaritymatrices`.`CandidateTermID` " +
				", `similaritymatrices`.`TargetSchemaID` , `similaritymatrices`.`TargetTermID` , `similaritymatrices`.`confidence` " +
				" FROM `schemamatching`.`schemapairs` INNER JOIN `schemamatching`.`similaritymatrices` ON (`schemapairs`.`TargetSchema` = `similaritymatrices`.`TargetSchemaID`) AND (`schemapairs`.`CandidateSchema` = `similaritymatrices`.`CandidateSchemaID`)" +
				"WHERE (`similaritymeasures`.`MeasureName` = '" + smName + "' AND `similaritymeasures`.`System` = " + sysCode + " AND `schemapairs`.`SPID` = " + spid + ");";
		return db.runSelectQuery(sql, 5);
	}
	
	/**
	 * Make sure (2nd Line) matchers supplied exist in the DB 
	 * @param db DB Interface to look inside 
	 * @param availableMatchers List of matchers to lookup
	 * @param sysCode system code of matchers
	 * @return list of MatcherID and matcherName pairs as a String array
	 */
	private static ArrayList<String[]> documentMatchers(DBInterface db,
			String[] availableMatchers, int sysCode) 
	{
		for (String matcherName : availableMatchers)
		{
			String sql = "SELECT MatcherID FROM matchers WHERE System = " + sysCode + " AND MatcherName='" + matcherName + "'";
			if (db.runSelectQuery(sql, 2).size()==0)
				{
				
					HashMap<Field, Object> values = new HashMap<Field, Object>();
					values.put(new Field("MatcherName",FieldType.STRING), matcherName);
					values.put(new Field("System",FieldType.INT), new Integer(sysCode));
					db.insertSingleRow(values , "matchers");
				}
		}
		
		String sql = "SELECT MatcherID, MatcherName FROM matchers WHERE System = " + sysCode + ";";
		return db.runSelectQuery(sql, 2);
	}

	/**
	 * Make sure similarity measures supplied exist in the DB 
	 * @param db DB Interface to look inside 
	 * @param availableMatchers List of matchers to lookup
	 * @param sysCode system code of matchers
	 * @return list of SimilarityMeasure and matcherName pairs as a String array
	 */
	private static ArrayList<String[]> documentSimilarityMeasures(DBInterface db,
			String[] availableMatchers,int sysCode) 
	{
		for (String matcherName : availableMatchers)
		{
			String sql = "SELECT SMID, MeasureName FROM similaritymeasures WHERE System = " + sysCode + " AND MeasureName='" + matcherName + "'";
			if (db.runSelectQuery(sql, 2).size()==0)
				{
				
					HashMap<Field, Object> values = new HashMap<Field, Object>();
					values.put(new Field("MeasureName",FieldType.STRING), matcherName);
					values.put(new Field("System",FieldType.INT), new Integer(sysCode));
					db.insertSingleRow(values , "similaritymeasures");
				}
		}
		
		String sql = "SELECT SMID, MeasureName FROM similaritymeasures WHERE System = " + sysCode + ";";
		return db.runSelectQuery(sql, 2);
	}

	private static void updateWasMatchedWithAllMatchers(double spid,DBInterface db) 
	{
		String sql = "UPDATE experimentschemapairs SET WasMatched = TRUE WHERE EID = " + eid + " AND SPID = " + spid + ";"; 
		db.runUpdateQuery(sql);
	}

	/**
	 * This method gets a spid of an schemapair and looks if this pair was parsed before and stored in the DB. if so, returns true
	 * @param FirstSchemaMacher
	 * @return boolean
	 */
	
	private static boolean checkIfSchemaPairWasMatched(double schemaPairId,DBInterface db) {
		String sql  = "SELECT WasMatched FROM experimentschemapairs WHERE SPID='" + schemaPairId + "';"; 
		ArrayList<String[]> schemaList = db.runSelectQuery(sql, 1);
		if (schemaList.size()>0)
				return true;
		return false;
	}

	/**
		 * This method gets an ontology and ontology's id and updates into the DB additional info about the schema
		 * @param schemaID 
		 * @param Onotology
		 * @throws Exception
		 */
	private static void AddInfoAboutSchemaToDB(long schemaID, Ontology ontology,DBInterface db) throws Exception {
		
		String sql  = "SELECT * FROM schemata WHERE SchemaID=\"" + schemaID + "\";"; 
		String[] schemaList = db.runSelectQuery(sql, 12).get(0);
		HashMap<Field,Object> schemaValues = new HashMap<Field,Object>();	
		//check the flag of the ontology to see if it was parsed before
		if (Double.valueOf(schemaList[11])==1)	return;
			schemaValues.put(new Field ("SchemaID", FieldType.INT ),Integer.parseInt(schemaList[0]));	
			schemaValues.put(new Field ("SchemaName", FieldType.STRING ), schemaList[1]);
			schemaValues.put(new Field ("DSID", FieldType.INT ), Integer.valueOf(schemaList[2]));
			schemaValues.put(new Field ("DS_SchemaID", FieldType.INT ), 0);
			schemaValues.put(new Field ("path", FieldType.STRING ), schemaList[4]);
			//Calculated Fields
			schemaValues.put(new Field ("Max_Height_of_the_class_hierarchy", FieldType.INT), ontology.getHeight());
			int NumOfClasses = getNumberOfClasses (ontology);
			ArrayList <Integer> A = calculateTermsHiddenAssociationInOntology (ontology);
			int size = ontology.getModel().getTerms().size();
			schemaValues.put(new Field ("Number_of_classes", FieldType.INT ),NumOfClasses);			
			schemaValues.put(new Field ("Number_of_association_relationships", FieldType.INT ), A.get(2));
			schemaValues.put(new Field ("Number_of_attributes_in_Schema", FieldType.INT ), A.get(0) );
						
			//For webforms only recursively count terms that the ontology class is not "hidden" 
			schemaValues.put(new Field ("Number_of_visible_items", FieldType.INT ), A.get(1)-A.get(0)); 
			schemaValues.put(new Field ("Number_of_instances", FieldType.INT ), 0); //Ontobuilder doesn't support instances	
			schemaValues.put(new Field ("Was_Fully_Parsed", FieldType.INT ), 0);
			//sql = "DELETE FROM schemata_copy WHERE SchemaID=" + ontologyid; 
			//db.runDeleteQuery(sql);
			db.insertSingleRow(schemaValues, "schemata_copy");

		}
	
	

	
	/**
	 * This method given an ontology will calculate (by iterating recursively) all of it's terms, hidden terms, and associations
	 * Remark: We assume that is a unique path to each term (so terms arn't being counted more than once)
	 * @param Onotology
	 * Return: ArrayList<integer> A: A(0) number of terms
	 * 								 A(1) number of hidden terms
	 * 								 A(2) number of association
	 * @throws Exception
	 */
	
	private static ArrayList<Integer> calculateTermsHiddenAssociationInOntology(Ontology ontology) {
		
		int TotalNumOfTerms = 0;
		int counthiddens = 0;
		int Associationcount = 0;
		for (int i=0;i<ontology.getTermsCount();i++){
			Term t = ontology.getTerm(i);
			ArrayList <Integer> B = countTermsChildren (t);
			TotalNumOfTerms+= B.get(0);
			counthiddens+=B.get(1);
			Associationcount += B.get(2);
			if (t.getSuperClass().getName().contains("hidden"))
				counthiddens++;
			for (int j=0;j<t.getRelationshipsCount();j++)
				if (!t.getRelationship(j).getName().contains("is child of") && !t.getRelationship(j).getName().contains("is parent of"))	
					Associationcount++;
		}		
		ArrayList<Integer> A = new ArrayList<Integer>();
		A.add(TotalNumOfTerms);
		A.add(counthiddens);
		A.add(Associationcount);
		return A;
	}

		
	/**
	 * This method's returns the number of the children from a given term (not only terms)
	 * function will run recursively until it gets to all the leafs reachable from the term
	 * Remark: We assume that a single entity in the graph has only one parent
	 * @param term
	 * @throws Exception
	 */
	
	private static ArrayList <Integer> countTermsChildren(Term t) {
		
		int tCount = t.getTermsCount();
		int countNumberOfDecendants=0;
		int hidden=0;
		int countAssociation = 0;
		for (int i=0; i<tCount;i++){
			ArrayList <Integer> B = countTermsChildren(t.getTerm(i));
			countNumberOfDecendants+=B.get(0);
			hidden+=B.get(1);
			if (t.getTerm(i).getSuperClass().getName().contains("hidden"))	
				hidden++;
			for (int j=0;j<t.getRelationshipsCount();j++)
				if (!t.getRelationship(j).getName().contains("is child of") && !t.getRelationship(j).getName().contains("is parent of"))	
					countAssociation++;
		}
		ArrayList <Integer> A = new ArrayList<Integer>();
		A.add(1+countNumberOfDecendants);
		A.add(hidden);
		A.add(countAssociation);
		return A;
	}
	
	/**
	 * This method's returns the number of subclasses within an onotology
	 * @param Onotology
	 * @throws Exception
	 */
	
	private static int getNumberOfClasses(Ontology ontology) {
		Vector v = ontology.getModel().getClasses();
		//OntologyClass c = (OntologyClass) v.get(0);
		Iterator<OntologyClass> it = v.iterator();
		int count = v.size();
		while (it.hasNext()){
			OntologyClass c = it.next();
			count+=c.getSubClassesCount() + iterativeCountingOfClasses (c);
		}		
		return count;
	}
	
	/**
	 * This method's returns the number of subclasses from an OntologyClass
	 * iterate recursively from the given OntologyClass to its subclasses until it reaches subclasses with no subclasses 
	 * @param OntologyClass
	 * @throws Exception
	 */

	private static int iterativeCountingOfClasses(OntologyClass c) {
		int count = 0;
		count+= c.getSubClassesCount();
		for (int i =0; i<c.getSubClassesCount(); i++){
			count+=iterativeCountingOfClasses(c.getSubClass(i));
		}
		return count;
	}
	
	/**
	 * Outputs a tab delimited file of the supplied name to the supplied path
	 * From an ArrayList of string arrays each arrayList item representing a row 
	 * and each String array item representing a column.  
	 * @param outputPath
	 * @param res
	 * @param fName
	 * @throws IOException
	 */
	private static void outputArrayListofStringArrays(File outputPath,
			ArrayList<String[]> res, String fName) throws IOException {
		DataFile write = DataFile.createWriter("8859_1", false);
		write.setDataFormat(new TabFormat());			
		File outputCOFile = new File(outputPath,fName );
		write.open(outputCOFile);
		for (int i=0;i<res.size();i++)
		{
			DataRow row = write.next();
			String rRow[] = res.get(i);
			for (int j=0;j<rRow.length;j++) row.add(rRow[j]);
		}
		write.close();
	}

	private static void writeMatchMatrixToDB(MatchInformation matchInformation, SchemasExperiment schemasExp, DBInterface db) {
		// TODO
		// check if this experiments was run before, if so, only update the similarities/or skip
		// else write the matrix to DB
		String sql  = "SELECT EID FROM experiments"; 
		ArrayList<String[]> experiments = db.runSelectQuery(sql, 1);
		Iterator<String[]> it = experiments.iterator();	
		while (it.hasNext()){
			if (Long.valueOf(it.next()[0]) == schemasExp.getSPID()) return;
		}
	}

	/**
	 * Selects K random Schema Matching Experiments from the database and loads into OB objects
	 * A Schema matching experiment Includes a schema pair and exact match.
	 * Documents the experiment in the database, (Experiment and ExperimentSchemaPairs) and 
	 * if the terms are not documented, adds them as well to the terms table.
	 * Note: This method assumes that schema files were not changed (hence if a schema was parsed before,
	 * it will not be parsed again and changes will not be detected  
	 * @param db
	 * @param K no. of random experiments to load
	 * @return ArrayList of Schema Experiments. 
	 * @throws Exception if K is larger than the no. of available schema pairs in the db
	 */
	private static ArrayList<SchemasExperiment> loadKExperiments(DBInterface db, int K) throws Exception {
		
		ArrayList<SchemasExperiment> ds = new ArrayList<SchemasExperiment>();
		String sql = "SELECT COUNT(*) FROM schemapairs";
		ArrayList<String[]> NumberOfSchemaPairs =  db.runSelectQuery(sql, 1);
		//check the number of available experiments is larger then k
		if (K>Integer.valueOf(NumberOfSchemaPairs.get(0)[0])) throw new Exception("K is too large");
		//extracting pairs from the DB
		sql  = "SELECT * FROM schemapairs ORDER BY RAND() LIMIT " + String.valueOf(K); 
		ArrayList<String[]> k_Schemapairs =  db.runSelectQuery(sql, 5);
		for (int i=0;i<K;i++){
		String full_url = DSURL;
		String url = parseFolderPathFromSchemapairs((k_Schemapairs.get(i))[4]);
		full_url = full_url.concat(url);
		File f = new File(full_url);
		SchemasExperiment schemasExp = new SchemasExperiment(f,Long.valueOf(k_Schemapairs.get(i)[0]),Long.valueOf(k_Schemapairs.get(i)[2]),Long.valueOf(k_Schemapairs.get(i)[3]));
		ds.add(schemasExp);
		}
		//document the new experiment in to DB 
		eid = writeExperimentsToDB(db,ds,k_Schemapairs);
		return ds;
	}

	/**
	 * This method gets a schema pair ID (SPID) and returns one of the ontology's in the pair
	 * @param db
	 * @param spid - schema pairs Id at the DB
	 * @param targetOrCandidate - use true for target false for candidate
	 * @return schema ID (double) from DB, and -1 if failed to find the SPID at the DB
	 * 	 */
	private static double getExperminetSchemaIDFromDB(double spid, DBInterface db, boolean targetOrCandidate) {
		String sql = "SELECT * FROM schemapairs";
		ArrayList<String[]> SPIDs =  db.runSelectQuery(sql, 5);
		for (int i=0;i<SPIDs.size();i++){
			if ( spid == Integer.valueOf(SPIDs.get(i)[0]) ){
				if (targetOrCandidate==true)
					return Double.valueOf(SPIDs.get(i)[2]);
				if (targetOrCandidate==false)
					return Double.valueOf(SPIDs.get(i)[3]);
			}
		}
		return -1;
	}
	/**
	 * Adds an experiment to the experiments table and the schemapairs to the experiment schema pairs table
	 * Otherwise 
	 * @param db
	 * @param ds
	 * @param k_Schemapairs
	 * @return Experiment ID from db. Returns null if writing experiment into DB fails (due to missing ontology files, etc.)
	 */
	private static long writeExperimentsToDB(DBInterface db, ArrayList<SchemasExperiment> ds, ArrayList<String[]> k_Schemapairs) {
		
		//document the experiment into experiment table
		String sql  = "SELECT MAX(EID) FROM experiments";
		ArrayList<String[]> LastEID =  db.runSelectQuery(sql, 1);
		//settings experiments ID (will be a sequential number to the last EID) 
		long currentEID;
		//if the table is empty
		if (LastEID.get(0)[0]==null) currentEID=1;
		else currentEID = (long)Integer.valueOf(LastEID.get(0)[0])+1;
		
		HashMap<Field,Object> values = new HashMap<Field,Object>();	
		
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
		//k_Schemapairs holds the info from the DB (ontology's id, etc)
		Iterator <String[]> it2 = k_Schemapairs.iterator();
		while (it.hasNext() && it2.hasNext()){
			SchemasExperiment schemasexperiment = it.next();
			String[] s = it2.next();
			values = new HashMap<Field,Object>();	
			
			f = new Field ("EID", FieldType.LONG );
			values.put(f, (Object)currentEID);
			//Time time = new Time(1);
		
			f = new Field ("SPID", FieldType.LONG);
			long SPID =  (long)Integer.valueOf(s[0]);
			values.put(f, (Object)SPID);
		
			f = new Field ("Training", FieldType.BOOLEAN);
			boolean training =  false;
			values.put(f, (Object)training);
			
			f = new Field ("WasMatched", FieldType.BOOLEAN);
			boolean WasMatched =  false;
			values.put(f, (Object)WasMatched);

			db.insertSingleRow(values, "experimentschemapairs");
			/*
		//parse ontologies terms
			try {
				Ontology Candidate = schemasexperiment.getCandidateOntology();
				Ontology Target = schemasexperiment.getTargetOntology();
				long CandidateID = (long)Integer.valueOf(s[3]);
				long TargetID = (long)Integer.valueOf(s[2]); //it.next().getOntologyDBId(Target.getName(),db);
		
		//before parsing a given ontology, check it isn't already in the DB
				if (!checkOntologywasParsedtoDB(CandidateID,db))
					System.out.println ("Ontology: " + Candidate.getName() + "have been parsed before ");
				else
					WriteTermsToDB(CandidateID, Candidate, db);
		
				if (!checkOntologywasParsedtoDB(TargetID,db))
					System.out.println ("Ontology: " + Candidate.getName() + "have been parsed before ");
				else 
					WriteTermsToDB(TargetID, Target, db);
		
			}
			catch (Throwable e){
				System.out.println ("Failed  " + e.getLocalizedMessage());
				return false;
			}*/
		}
		return currentEID;
		
	}
		
	//this method checks if the ontology was parsed into terms or not
	private static boolean checkOntologywasParsedtoDB(long OntologyId,DBInterface db) {
		//check if this field was already inserted into the table
		String sql = "SELECT SchemaID From terms WHERE Tid=" + OntologyId + ";";
		ArrayList<String[]> existInDB =  db.runSelectQuery(sql, 1);
		Iterator<String[]> exists = existInDB.iterator();	
		if (exists.hasNext())
			return true;
		return false;
	}

	
	/**
	 *Receives an Ontology and parses all terms to DB
	 * @param db, Ontology - 
	 * @param OntologyID - due to the structure of the DB, the Id that we choose for each of the ontology's may not agree with the one stored at the ontonlog fields (for example, when building the onotolgy object through ontobuilder)
	 **/
	
	private static void WriteTermsToDB(long OntologyID, Ontology ontology, DBInterface db) {
		
		Vector<Term> terms = ontology.getModel().getTerms();
		Iterator<Term> it = terms.iterator();
		
		while (it.hasNext()){
			
		HashMap<Field,Object> values = new HashMap<Field,Object>();	
		Term t = it.next();
		
		Field f = new Field ("SchemaID", FieldType.LONG );
		values.put(f, (Object)OntologyID);
		
		long id = SchemasExperiment.PJWHash(t.getName());
		//Only put into DB Terms with names (=> (t.getName() =! empty) string)
		//prevent duplication of terms
		if  (!checkTermExistenceAtDB(db,id,OntologyID))
			{
			f = new Field ("Tid", FieldType.LONG );
			values.put(f, (Object)(id) );
		
			f = new Field ("TName", FieldType.STRING );
			values.put(f, (Object)t.getName());
		
			f = new Field ("DomainNumber", FieldType.INT);
			values.put(f, (Object)getDomainNumber(t.getDomain().getName()));
		
			f = new Field ("DomainName", FieldType.STRING );
			values.put(f, (Object)t.getDomain().getName());
		
			db.insertSingleRow(values,"terms");
			}
		}
	}
	
	
	/**
	 *Receives an single term and parses it to DB, if terms already exist returns without documenting it
	 * @param db
	 * @ Term  
	 * @param OntologyID - ID of the onotology the term belongs to
	 */
	
	private static void writeTermToDB(long OntologyID, Term term, DBInterface db) {
		
			
		HashMap<Field,Object> values = new HashMap<Field,Object>();	
		
		Field f = new Field ("SchemaID", FieldType.LONG );
		values.put(f, (Object)OntologyID);
		
		long id = term.getId();
		//Only put into DB Terms with names (=> (t.getName() =! empty) string)
		//prevent duplication of terms
		if  (!checkTermExistenceAtDB(db,id,OntologyID))
			{
			f = new Field ("Tid", FieldType.LONG );
			values.put(f, (id) );
		
			f = new Field ("TName", FieldType.STRING );
			values.put(f, term.getName());
		
			f = new Field ("DomainNumber", FieldType.INT);
			values.put(f, getDomainNumber(term.getDomain().getName()));
		
			f = new Field ("DomainName", FieldType.STRING );
			values.put(f, term.getDomain().getName());
			
			db.insertSingleRow(values,"terms");
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
	
	private static void writeItems(MatchMatrix[] firstLineMM, String schemaId, DBInterface db, boolean isTarget) throws IOException {
		DataFile write = DataFile.createWriter("8859_1", false);
		write.setDataFormat(new TabFormat());
		File outputPath = new File("c:\\smb");
		File outputSMFile = new File(outputPath,"item.tab");
		write.open(outputSMFile);
		DataRow row = write.next();
		Term s;
		int i=0;
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
		// TODO double check correctness of all fields, convert type to int, notice that date is test (what to do);
		write.close();
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
		//row.add(attibuteArray[j].id1); 
		row.add(candidate);
		//row.add(attibuteArray[j].id2);
		//row.add(attibuteArray[j].getMatchedPairWeight());  //check with tomer
		row=write.next();
		}
		i++;
		}
		write.close();
	}

	/**
	 * This method gets a MatchInformation and SchemaTranslator and outputs the matched result (matched terms and their similarity value) to DB
	 * @param MatchInformation - holds a set of matches
	 * @param SerialNumOfMatcher - according to the serial number described in the DB, under similaritymeasures;
	 * @param SchemasExperiment - holds the 2 ontology we match (used to get their IDs)
	 * @Remark, when storing an id we since we decide on the id of a term 
	 * 	 */
	private static void loadSMtoDB(MatchInformation firstLineMI, SchemasExperiment schemasExp, int SerialNumOfMatcher, DBInterface db) throws IOException {

		ArrayList matches = firstLineMI.getMatches();
		Iterator it = matches.iterator();
		while (it.hasNext()){
			Match match = (Match)it.next();
			Term cadidateTerm = match.getCandidateTerm();
			Term targetTerm = 	match.getTargetTerm();
			
			//write the term to the DB
			
			writeTermToDB((long)schemasExp.getTargetID(),targetTerm,db);
			writeTermToDB((long)schemasExp.getCandidateID(),cadidateTerm,db);
							
			HashMap<Field,Object> values = new HashMap<Field,Object>();	
				
			values.put(new Field ("TargetSchemaID", FieldType.LONG ), (long)schemasExp.getTargetID());
			values.put(new Field ("TargetTermID", FieldType.LONG ), (long)targetTerm.getId());
			values.put(new Field ("CandidateSchemaID", FieldType.LONG ), (long)schemasExp.getCandidateID());
			values.put(new Field ("CandidateTermID", FieldType.LONG ), (long)cadidateTerm.getId());
			values.put(new Field ("SMID", FieldType.LONG ), (long)SerialNumOfMatcher);
			values.put(new Field ("confidence", FieldType.DOUBLE ), match.getEffectiveness());
			values.put(new Field ("TTermName", FieldType.STRING ), targetTerm.getName());
			values.put(new Field ("CTermName", FieldType.STRING ), cadidateTerm.getName());
			
			//System.out.println(match.getEffectiveness());
			
		}
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
				Long key = itr.next();
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
	
}





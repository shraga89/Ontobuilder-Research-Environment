/**
 * 
 */
package Application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import schemamatchings.meta.match.MatchedAttributePair;
import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.util.BestMappingsWrapper;
import schemamatchings.util.MappingAlgorithms;
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
	private static DBInterface db;
	public static void main(String[] args) throws NumberFormatException, Exception 
	{
		File outputPath = new File(args[0]); // folder in which temporary files will be saved
	    Properties pMap = PropertyLoader.loadProperties("resources");
	    db = new DBInterface(Integer.parseInt((String)pMap.get("dbmstype")),(String)pMap.get("host"),(String)pMap.get("dbname"),(String)pMap.get("username"),(String)pMap.get("pwd"));
	    DSURL = (String)pMap.get("schemaPath"); 
	
	    
	    
	// 1 Load K experiments into an experiment list and document experiment and schema pairs in db
	    
	    ArrayList<SchemasExperiment> ds = loadKExperiments(Integer.parseInt(args[2]));
  		System.out.println("DataSet size is: " + ds.size());
  		
	    Ontology target;
	    Ontology candidate;
	    OntoBuilderWrapper obw = new OntoBuilderWrapper();
	    SchemaTranslator exactMapping;
	    String[] availableMatchers =  MatchingAlgorithms.ALL_ALGORITHM_NAMES;
        MatchInformation firstLineMI[]= new MatchInformation[availableMatchers.length];
        MatchMatrix firstLineMM[]= new MatchMatrix[availableMatchers.length];
	    String[] available2ndLMatchers = MappingAlgorithms.ALL_ALGORITHM_NAMES;
        SchemaTranslator secondLineST[] = new SchemaTranslator[available2ndLMatchers.length*availableMatchers.length];
	    int sysCode = 1; //Ontobuilder sysCode
	    int smbSysCode = 2; //SMB enhancement results are documented under a different sysCode
		// Make sure all matchers and similarity measures are documented in the DB with the right matcher ID
        ArrayList<String[]> SMIDs = documentSimilarityMeasures(availableMatchers,sysCode );
        documentSimilarityMeasures(availableMatchers,smbSysCode );
        documentMatchers(available2ndLMatchers, sysCode);
        documentMatchers(available2ndLMatchers, smbSysCode);
        
		// 2 For each experiment in the list:
	    for (SchemasExperiment schemasExp : ds) 
	    {
			// 2.1 load from file into OB objects
	        target = schemasExp.getTargetOntology();
	        candidate = schemasExp.getCandidateOntology();
	        exactMapping = schemasExp.getExactMapping();
	        long spid = schemasExp.getSPID();
			//document exact match in db if doesn't exist
	        uploadExactMatch(exactMapping, spid);
	        	        
	        //TODO 1.add to each schema experiments the SPID and the onology's ID4
	        AddInfoAboutSchemaToDB(schemasExp.getTargetID(),target,db);
	        AddInfoAboutSchemaToDB(schemasExp.getCandidateID(),candidate,db);
	        //2.2 1st line match using all available matchers in OB // missing similarity flooding -> adjustment were made lines: 74-77;
	        int counter = 0;
	        for (int m=0;m<availableMatchers.length;m++)
	        {				
					System.out.println ("Starting " + counter);
					//check if we haven't run current first line matcher on this pair before, if we didn't run matchers, run
					if (!checkIfSchemaPairWasMatched(spid))
						{
						firstLineMI[m] = obw.matchOntologies(candidate, target,availableMatchers[m]);
						loadSMtoDB(firstLineMI[m],schemasExp,m);
						//TODO find a way to travers the terms correctly
						}
					else // if already matched, retrieve from DB 
						{
							ArrayList<String[]> firstLineMMfromDB = getSimilarityMatrix(availableMatchers[m],sysCode,spid);
							MatchInformation mi = createMIfromArrayList(candidate,target, firstLineMMfromDB);
							firstLineMI[m] = mi;
						}
					firstLineMM[m] = firstLineMI[m].getMatrix();
					BestMappingsWrapper.matchMatrix = firstLineMI[m].getMatrix();
					if (m==availableMatchers.length-1)
						updateWasMatchedWithAllMatchers(spid);
					
					// 2.3 2nd line match using all available matchers in OB with original matrix and document in DB   	
					for (int mp=0;mp<available2ndLMatchers.length;mp++)
					{   
						//get a matching from db, if 2nd line match not in db, perform matching
						ArrayList<String[]> mapping = getMappings(available2ndLMatchers[mp],availableMatchers[m],spid,sysCode);
						if (mapping == null)
						{
							String[] e = {Long.toString(spid),"","",Long.toString(getMID(available2ndLMatchers[mp],sysCode)),Long.toString(getSMID(availableMatchers[m],sysCode))};
							mapping = new ArrayList<String[]>();
							System.out.println ("doing " + counter + "." + available2ndLMatchers[mp] );
							secondLineST[mp*(m+1)] = BestMappingsWrapper.GetBestMapping(available2ndLMatchers[mp]);
							secondLineST[mp*(m+1)].importIdsFromMatchInfo(firstLineMI[m],true);
							
							for (MatchedAttributePair match : secondLineST[mp*(m+1)].getMatchedPairs())
							{
								e[1] = Long.toString(match.id1);
								e[2] = Long.toString(match.id2);
								mapping.add(e);
							}
							
						}
						upload2ndLineMatchToDB(availableMatchers[m], available2ndLMatchers[mp],sysCode, spid, mapping);
					}
					System.out.println ("Finished 2nd line matching " + counter);
					counter++;
	        }//end for of 1st line matcher
			//  2.4 Output schema pair, term list, list of matchers and matches to URL    
	        	outputArrayListofStringArrays(outputPath,SMIDs,"BasicConfigurations.tab");
	        	outputArrayListofStringArrays(outputPath,db.runSelectQuery("SELECT `SchemaID`, `SchemaName`, `Max_Height_of_the_class_hierarchy`,`Number_of_association_relationships`, `Number_of_attributes_in_Schema`,  `Number_of_classes`,  `Number_of_visible_items`,  `Number_of_instances` FROM schemata,schemapairs WHERE (schemata.SchemaID = schemapairs.CandidateSchema OR schemata.SchemaID = schemapairs.TargetSchema) AND schemapairs.SPID = " + spid + ";", 2),"Schema.tab");
	        	outputArrayListofStringArrays(outputPath,db.runSelectQuery("SELECT `SchemaID`, `Tid`, `DomainNumber`, `TName`, `Known Composite`, `Known Partial`" +
	        									"FROM `schemamatching`.`terms` WHERE SchemaID = " + schemasExp.getCandidateID() + " OR SchemaID = " + schemasExp.getTargetID() + ";", 6),"Item.tab");
	        	outputArrayListofStringArrays(outputPath,db.runSelectQuery("SELECT `similaritymatrices`.`SMID` , `similaritymatrices`.`CandidateSchemaID` , `similaritymatrices`.`CandidateTermID` , `similaritymatrices`.`TargetSchemaID` , `similaritymatrices`.`TargetTermID` , `similaritymatrices`.`confidence`" +
	        								" FROM  `schemamatching`.`experimentschemapairs` INNER JOIN `schemamatching`.`schemapairs` ON (`experimentschemapairs`.`SPID` = `schemapairs`.`SPID`)" +
	        								" INNER JOIN `schemamatching`.`similaritymatrices` ON (`schemapairs`.`TargetSchema` = `similaritymatrices`.`TargetSchemaID`) AND (`schemapairs`.`CandidateSchema` = `similaritymatrices`.`CandidateSchemaID`)" +
	        								" INNER JOIN `schemamatching`.`similaritymeasures` ON (`similaritymeasures`.`SMID` = `similaritymatrices`.`SMID`)" +
	        								" WHERE (`similaritymeasures`.`System` = " + sysCode + ") AND (EID = " + eid + ");", 6),"MatchingResult.tab");
	      		   		

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
    			for ( long sm : similarityMeasureMatrices.keySet()) EnhancedMI.put(sm, createMIfromArrayList(candidate, target,similarityMeasureMatrices.get(sm) ) ); 
			// 2.7 2nd line match using all available matchers in OB with enhanced matrix
    			for (String[] smRow : SMIDs)
    			{
    				long sm = Long.parseLong(smRow[0]);
    				String SMName = smRow[1];
    				for(String secondLineM : available2ndLMatchers)
    				{
    					// Match
    					String[] e = {Long.toString(spid),"","",Long.toString(getMID(secondLineM,sysCode)),smRow[0]};
    					ArrayList<String[]> mapping = new ArrayList<String[]>();
						System.out.println ("doing " + counter + "." + secondLineM );
						SchemaTranslator st = BestMappingsWrapper.GetBestMapping(secondLineM);
						st.importIdsFromMatchInfo(EnhancedMI.get(sm),true);
						
						for (MatchedAttributePair match : st.getMatchedPairs())
						{
							e[1] = Long.toString(match.id1);
							e[2] = Long.toString(match.id2);
							mapping.add(e);
						}
						// Update enhanced matrix and mapping results to db
						// TODO since these are enhanced matrices  think how to document them without running over the non-enhanced matrices
						loadSMtoDB(EnhancedMI.get(sm),schemasExp,(int)sm);
						
						// using the smbSysCode to separate the non enhanced from the enhanced result 
						upload2ndLineMatchToDB(SMName, secondLineM,smbSysCode, spid, mapping);
					}
    			}
    			 
    	  
	  }// end for loop of experiment
	}
	/**
	 * Receive an array list of matches and 2 ontologies and upload them to an Ontobuilder Match Information object
	 * @param candidate Ontology object of candidate schema
	 * @param target Ontology object of target schema
	 * @param matchList [SMID,CandidateSchemaID,CandidateTermID,TargetSchemaID, TargetTermID,Confidence]
	 * @return Ontobuilder Match Information object with supplied matches
	 */
	private static MatchInformation createMIfromArrayList(Ontology candidate,Ontology target, ArrayList<String[]> matchList) {
		MatchInformation mi = new MatchInformation();
		mi.setCandidateOntology(candidate);
		mi.setTargetOntology(target);
		ArrayList<Match> matches = new ArrayList<Match>();
		for (String[] match : matchList) matches.add(new Match(candidate.getTermByID(Long.parseLong(match[1])), target.getTermByID(Long.parseLong(match[3])),Double.parseDouble(match[4])));
		mi.setMatches(matches );
		return mi;
	}

	/**
	 * If exact match is not documented in db, document it
	 * @param exactMapping SchemaTranslator object with mappings between terms. In each pair, assuming first is candidate and second is target
	 * @param spid Schema Pair ID
	 */
	private static void uploadExactMatch(SchemaTranslator exactMapping,
			long spid) {
		String sql = "SELECT SPID FROM exactmatches WHERE SPID = " + spid + ";";
		if (db.runSelectQuery(sql,1).isEmpty())
		{
		    HashMap<Field, Object> values = new HashMap<Field, Object>();
		    values.put(new Field("SPID",FieldType.LONG),spid);
		    Field targTerm = new Field("TargetTermID",FieldType.LONG);
		    Field candTerm = new Field("CandidateTermID",FieldType.LONG);
		    for (MatchedAttributePair match : exactMapping.getMatchedPairs())
		    {
		    	values.put(candTerm, match.id1);
		    	values.put(targTerm, match.id2);
		    	db.insertSingleRow(values, "exactmatches");
		    }
		}
	}

	/**
	 * @param measureName similarity measure (first line matcher) name
	 * @param matcherName 2cnd Line matcher name 
	 * @param sysCode matching system code
	 * @param spid schema pair id
	 * @param mapping ArrayList of mappings : [SPID,CandidateTermID,TargetTermID,MatcherID,SMID]
	 */
	private static void upload2ndLineMatchToDB(String measureName,String matcherName, int sysCode,
			long spid, ArrayList<String[]> mapping) {
		HashMap<Field, Object> values = new HashMap<Field, Object>();
		values.put(new Field("EID", FieldType.LONG),eid);
		values.put(new Field("SPID",FieldType.LONG),spid);
		values.put(new Field("MatcherID",FieldType.LONG), getMID(matcherName,sysCode));
		values.put(new Field("SMID",FieldType.LONG), getSMID(measureName,sysCode));
		Field candTerm = new Field("CandidateTermID",FieldType.LONG);
		Field targTerm = new Field("TargetTermID",FieldType.LONG);
		for (String[] mappingRow : mapping) 
		{
			values.put(candTerm, Long.parseLong(mappingRow[1]));
			values.put(targTerm, Long.parseLong(mappingRow[2]));
			db.insertSingleRow(values , "mapping");
		}
	}

	/**
	 * Get the similarity measure (first line matcher) ID for the supplied name and system
	 * @param SMName similarity measure name
	 * @param sysCode matching system
	 * @return
	 */
	private static Long getSMID(String SMName, int sysCode) 
	{
		String sql = "SELECT `SMID`, FROM `schemamatching`.`similaritymeasures` " +
					" WHERE `MeasureName` = '" + SMName + "' AND `System` = " + sysCode + ";";
		return Long.parseLong(db.runSelectQuery(sql, 1).get(0)[0]);
	}

	/**
	 * Get the (2nd Line) matcher ID for the supplied name and system
	 * @param MName Matcher Name
	 * @param sysCode Matching System
	 * @return
	 */
	private static Long getMID(String MName, int sysCode) 
	{
		String sql = "SELECT `MatcherID` FROM `schemamatching`.`matchers`" + 
					 " WHERE `MatcherName` = '" + MName + "' AND `System` = " + sysCode +  ";";
		return Long.parseLong(db.runSelectQuery(sql, 1).get(0)[0]);
	}

	/**
	 * if schema pair is mapped by this 2ndLineMatcher retrieve the mapping
	 * @param secondLineM 2nd Line Matcher Name
	 * @param SMName Similarity Measure Name (1st line matcher)
	 * @param spid Schema Pair ID
	 * @param sysCode Matching system code
	 * @return mapping : ArrayList of [SPID,CandidateTermID,TargetTermID,MatcherID,SMID] arrays
	 */
	private static ArrayList<String[]> getMappings(String secondLineM, String SMName, long spid, int sysCode) 
	{
		String sql = "SELECT `mapping`.`SPID`, `mapping`.`CandidateTermID` , `mapping`.`TargetTermID` , `mapping`.`MatcherID`, `mapping`.`SMID` FROM `schemamatching`.`similaritymeasures`" +
					" INNER JOIN `schemamatching`.`mapping` ON (`similaritymeasures`.`SMID` = `mapping`.`SMID`)" +
					" INNER JOIN `schemamatching`.`matchers` ON (`matchers`.`MatcherID` = `mapping`.`MatcherID`)" +
					" WHERE (`mapping`.`SPID` = " + spid + "AND `similaritymeasures`.`MeasureName` = '" + SMName +
					" ' AND `matchers`.`MatcherName` = '" + secondLineM + "' AND `similaritymeasures`.`System` = " + sysCode +
					" AND `matchers`.`System` = " + sysCode + "1);";
		return db.runSelectQuery(sql, 5);
	}

	/**
	 * Return the similarity matrix of the supplied schema pair using the similarity measure supplied
	 * If the matrix isn't documented in the db, returns nul
	 * @param smName Similarity measure (1st line matcher) name to look for
	 * @param sysCode code of matching system
	 * @param spid Schema Pair ID to look for
	 * @return String array {SMID,CandidateSchemaID,CandidateTermID,TargetSchemaID,TargetTermID,confidence} or null if data isn't found
	 */
	private static ArrayList<String[]> getSimilarityMatrix(String smName,int sysCode, long spid) 
	{
		String sql = "SELECT `similaritymatrices`.`SMID`,`similaritymatrices`.`CandidateSchemaID` , `similaritymatrices`.`CandidateTermID` " +
				", `similaritymatrices`.`TargetSchemaID` , `similaritymatrices`.`TargetTermID` , `similaritymatrices`.`confidence` " +
				" FROM `schemapairs` INNER JOIN `similaritymatrices` ON (`schemapairs`.`TargetSchema` = `similaritymatrices`.`TargetSchemaID`) AND (`schemapairs`.`CandidateSchema` = `similaritymatrices`.`CandidateSchemaID`)" +
				"WHERE (`similaritymeasures`.`MeasureName` = '" + smName + "' AND `similaritymeasures`.`System` = " + sysCode + " AND `schemapairs`.`SPID` = " + spid + ");";
		return db.runSelectQuery(sql, 5);
	}
	
	/**
	 * Make sure (2nd Line) matchers supplied exist in the DB 
	 * @param availableMatchers List of matchers to lookup
	 * @param sysCode system code of matchers
	 * @return list of MatcherID and matcherName pairs as a String array
	 */
	private static ArrayList<String[]> documentMatchers(String[] availableMatchers, int sysCode) 
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
	 * @param availableMatchers List of matchers to lookup
	 * @param sysCode system code of matchers
	 * @return list of SimilarityMeasure and matcherName pairs as a String array
	 */
	private static ArrayList<String[]> documentSimilarityMeasures(String[] availableMatchers,int sysCode) 
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

	private static void updateWasMatchedWithAllMatchers(double spid) 
	{
		String sql = "UPDATE experimentschemapairs SET WasMatched = TRUE WHERE EID = " + eid + " AND SPID = " + spid + ";"; 
		db.runUpdateQuery(sql);
	}

	/**
	 * This method gets a spid of an schemapair and looks if this pair was parsed before and stored in the DB. if so, returns true
	 * @param FirstSchemaMacher
	 * @return boolean
	 */
	
	private static boolean checkIfSchemaPairWasMatched(double schemaPairId) {
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
	
	@SuppressWarnings("unchecked")
	private static int getNumberOfClasses(Ontology ontology) {
		Vector<OntologyClass> v = ontology.getModel().getClasses();
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

	/**
	 * Selects K random Schema Matching Experiments from the database and loads into OB objects
	 * A Schema matching experiment Includes a schema pair and exact match.
	 * Documents the experiment in the database, (Experiment and ExperimentSchemaPairs) and 
	 * if the terms are not documented, adds them as well to the terms table.
	 * Note: This method assumes that schema files were not changed (hence if a schema was parsed before,
	 * it will not be parsed again and changes will not be detected  
	 * @param K no. of random experiments to load
	 * @return ArrayList of Schema Experiments. 
	 * @throws Exception if K is larger than the no. of available schema pairs in the db
	 */
	private static ArrayList<SchemasExperiment> loadKExperiments(int K) throws Exception {
		
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
		eid = writeExperimentsToDB(ds,k_Schemapairs);
		return ds;
	}

	/**
	 * Adds an experiment to the experiments table and the schemapairs to the experiment schema pairs table
	 * Otherwise 
	 * @param ds
	 * @param k_Schemapairs
	 * @return Experiment ID from db. Returns null if writing experiment into DB fails (due to missing ontology files, etc.)
	 */
	private static long writeExperimentsToDB(ArrayList<SchemasExperiment> ds, ArrayList<String[]> k_Schemapairs) {
		
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
	
	private static void WriteTermsToDB(long OntologyID, Ontology ontology) {
		
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
		if  (!checkTermExistenceAtDB(id,OntologyID))
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
	 * @ Term  
	 * @param OntologyID - ID of the onotology the term belongs to
	 */
	
	private static void writeTermToDB(long OntologyID, Term term) {
		
			
		HashMap<Field,Object> values = new HashMap<Field,Object>();	
		
		Field f = new Field ("SchemaID", FieldType.LONG );
		values.put(f, (Object)OntologyID);
		
		long id = term.getId();
		//Only put into DB Terms with names (=> (t.getName() =! empty) string)
		//prevent duplication of terms
		if  (!checkTermExistenceAtDB(id,OntologyID))
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
	
	private static boolean checkTermExistenceAtDB(long id, long ontologyID) {
	String sql = "SELECT SchemaID,Tid From terms WHERE Tid=" + id +  " AND SchemaID=" + ontologyID + ";";
	ArrayList<String[]> existInDB =  db.runSelectQuery(sql, 1);
	if (!existInDB.isEmpty())
		return true;
	return false;
	}

	//private static void writeToDB(DBInterface db, HashMap<Field,Object> values, String table) throws SQLException {
	//	db.insertSingleRow(values, table);		
	//}

	private static String parseFolderPathFromSchemapairs(String url) {
		String str[] = url.split("/");
		return str[0];
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


	/**
	 * This method gets a MatchInformation and SchemaTranslator and outputs the matched result (matched terms and their similarity value) to DB
	 * @param SerialNumOfMatcher - according to the serial number described in the DB, under similaritymeasures;
	 * @param MatchInformation - holds a set of matches
	 * @param SchemasExperiment - holds the 2 ontology we match (used to get their IDs)
	 * @Remark, when storing an id we since we decide on the id of a term 
	 * 	 */
	@SuppressWarnings("unchecked")
	private static void loadSMtoDB(MatchInformation firstLineMI, SchemasExperiment schemasExp, int SerialNumOfMatcher) throws IOException 
	{
		//TODO fix this (term documentation should probably move to somewhere else
		ArrayList<Match> matches = firstLineMI.getMatches();
		Iterator<Match> it = matches.iterator();
		while (it.hasNext()){
			Match match = it.next();
			Term cadidateTerm = match.getCandidateTerm();
			Term targetTerm = 	match.getTargetTerm();
			
			//write the term to the DB
			
			writeTermToDB((long)schemasExp.getTargetID(),targetTerm);
			writeTermToDB((long)schemasExp.getCandidateID(),cadidateTerm);
							
			HashMap<Field,Object> values = new HashMap<Field,Object>();	
				
			values.put(new Field ("TargetSchemaID", FieldType.LONG ), (long)schemasExp.getTargetID());
			values.put(new Field ("TargetTermID", FieldType.LONG ), (long)targetTerm.getId());
			values.put(new Field ("CandidateSchemaID", FieldType.LONG ), (long)schemasExp.getCandidateID());
			values.put(new Field ("CandidateTermID", FieldType.LONG ), (long)cadidateTerm.getId());
			values.put(new Field ("SMID", FieldType.LONG ), (long)SerialNumOfMatcher);
			values.put(new Field ("confidence", FieldType.DOUBLE ), match.getEffectiveness());
			values.put(new Field ("TTermName", FieldType.STRING ), targetTerm.getName());
			values.put(new Field ("CTermName", FieldType.STRING ), cadidateTerm.getName());
			db.insertSingleRow(values, "similaritymatrices");
		}
	}
	
	/**
	 * readFile supplied into ArrayList of string arrays
	 * @param f File to read
	 * @return Array list of string arrays
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
	
}





/**
 * 
 */
package ac.technion.schemamatching.experiments;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;

import schemamatchings.meta.match.MatchedAttributePair;
import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.util.SchemaTranslator;
import technion.iem.schemamatching.dbutils.DBInterface;
import technion.iem.schemamatching.dbutils.Field;
import technion.iem.schemamatching.dbutils.Field.FieldType;

import ac.technion.schemamatching.experiments.ExperimentSchemaPair;
import ac.technion.schemamatching.experiments.OBExperimentRunner;

import com.modica.ontology.Term;
import com.modica.ontology.match.Match;
import com.modica.ontology.match.MatchInformation;

/**
 * @author Tomer Sagi
 * @author Nimrod Busany
 * The class provides methods for documenting a schema matching
 * experiment in the schema matching database
 *
 */
public class ExperimentDocumenter 
{
	private DBInterface db;
	private long eid;
	private ArrayList<ExperimentSchemaPair> myExperiments;
	
	/**
	 * Class constructor creates an experiment in the DB and stores it's eid
	 * @param experimentDescription
	 */
	public ExperimentDocumenter(OBExperimentRunner myExpRunner)
	{
		SchemaTranslator exactMapping;
		ArrayList<ExperimentSchemaPair> badSE = new ArrayList<ExperimentSchemaPair>();
		    for (ExperimentSchemaPair schemasExp : myExpRunner.getDS()) 
		    {
				// 2.1 load from file into OB objects
		    	exactMapping = schemasExp.getExactMapping();
		        long spid = schemasExp.getSPID();
		        if (exactMapping == null )
		        {	
		        	badSE.add(schemasExp);
		        	System.err.println("Bad spid: "  + spid);
		        	continue;
		        }
		    
		        uploadExactMatch(exactMapping, spid, myExpRunner.getDB());
		        
		    }
		    
	        myExpRunner.getDS().removeAll(badSE);
	  		System.out.println("DataSet size is: " + myExpRunner.getDS().size());
	}

	
	/**
	 * If exact match is not documented in db, document it
	 * @param exactMapping SchemaTranslator object with mappings between terms. In each pair, assuming first is candidate and second is target
	 * @param spid Schema Pair ID
	 */
	private static void uploadExactMatch(SchemaTranslator exactMapping, long spid, DBInterface db) {
		String sql = "SELECT SPID FROM exactmatches WHERE SPID = " + spid + ";";
		if (db.runSelectQuery(sql,1).isEmpty())
		{
		    HashMap<Field, Object> values = new HashMap<Field, Object>();
		    values.put(new Field("SPID",FieldType.LONG),spid);
		    Field targTerm = new Field("TargetTermID",FieldType.LONG);
		    Field candTerm = new Field("CandidateTermID",FieldType.LONG);
		    for (MatchedAttributePair match : exactMapping.getMatchedPairs())
		    {
		    	values.put(candTerm, match.id2);
		    	values.put(targTerm, match.id1);
		    	if (db.runSelectQuery("SELECT * FROM exactmatches WHERE SPID='" + spid + "' AND TargetTermID='" + match.id1 + "' AND CandidateTermID='"+ match.id2 + "';" , 4).isEmpty())
		    		db.insertSingleRow(values, "exactmatches");
		    }
		}
	}
	
	/**
	 * Class constructor creates an experiment in the DB and stores it's eid
	 * @param experimentDescription
	 */
	public ExperimentDocumenter(String experimentDescription,DBInterface db)
	{
		this.db = db;
		//set the current EID to be one past the largest EID 
		eid = (Integer.getInteger((db.runSelectQuery("",1)).get(0)[0]) )+1; //write a query which retrieves the maximal EID in the db
		HashMap<Field, Object> values = new HashMap<Field, Object>();
	    values.put(new Field("EID",FieldType.LONG),eid);
	    values.put(new Field("Experiment Desc",FieldType.STRING),experimentDescription);
	    db.insertSingleRow(values, "EID Table"); //enter the name of the table 
		
	}

	public ArrayList<ExperimentSchemaPair> getMyExperiments() {
		return myExperiments;
	}

	/**
	 * documents Experiment schema pairs in DB (including terms)
	 * @param myExperiments
	 */
	public void setMyExperiments(ArrayList<ExperimentSchemaPair> myExperiments) 
	{
		this.myExperiments = myExperiments;
		//TODO stub
	}
	
	/**
	 * Documents the similarity matrix created in the experiment 
	 * @param spid Schema Pair ID
	 * @param mi - holds a similarity matrix
	 * @param SMID - according to the hash of the similarity measure
	 */
	public void documentSimMatrix(long spid, MatchInformation mi)
	{
		String algorithm = mi.getAlgorithm().getName();
		long SMID = OBExperimentRunner.reversedmeasures.get(algorithm);
		MatchMatrix simMatrix = mi.getMatrix();
		ArrayList<Term> candidateTerms = simMatrix.getCandidateTerms();
		ArrayList<Term> targetTerms = simMatrix.getTargetTerms();
		for (Term candidateTerm : candidateTerms){
			for (Term targetTerm : targetTerms){
			double confidence = simMatrix.getMatchConfidence(candidateTerm, candidateTerm);
			HashMap<Field, Object> values = new HashMap<Field, Object>();
		    values.put(new Field("EID",FieldType.LONG),eid);
		    values.put(new Field("SPID",FieldType.LONG),spid);
		    values.put(new Field("CandidateTerm",FieldType.STRING),candidateTerm.getName());
		    values.put(new Field("TargetTerm",FieldType.STRING),targetTerm.getName());
		    values.put(new Field("Confidence",FieldType.STRING),confidence);
		    db.insertSingleRow(values, "similairty"); //enter the name of the table 
			}
		}
	} 
	
	/**
	 * Documents the similarity matrix created in the experiment 
	 * @param spid
	 * @param mi
	 */
	public void documentSimMatrices(HashMap<Long,MatchInformation> miCollection)
	{
		for (Long spid : miCollection.keySet())
		{
			documentSimMatrix(spid,miCollection.get(spid));
		}
	}
	
	public void documentMapping(long spid, MatchInformation mi)
	{
		//TODO stub
	}

	public long getEid() {
		return eid;
	}
		
	/**
	 * This method gets a MatchInformation and SchemaTranslator and outputs the matched result (matched terms and their similarity value) to DB
	 * @param SerialNumOfMatcher - according to the serial number described in the DB, under similaritymeasures;
	 * @param MatchInformation - holds a set of matches
	 * @param ExperimentSchemaPair - holds the 2 ontology we match (used to get their IDs)
	 * @Remark, when storing an id we since we decide on the id of a term 
	 * 	 */
	@SuppressWarnings("unchecked")
	public void loadSMtoDB(MatchInformation firstLineMI, ExperimentSchemaPair schemasExp,boolean enhanced, int SerialNumOfMatcher) throws IOException 
	{
		ArrayList<Match> matches = firstLineMI.getMatches();
		HashMap<Field,Object> values = new HashMap<Field,Object>();
		values.put(new Field ("TargetSchemaID", FieldType.LONG ), (long)schemasExp.getTargetID());
		values.put(new Field ("CandidateSchemaID", FieldType.LONG ), (long)schemasExp.getCandidateID());
		values.put(new Field ("enhanced", FieldType.BOOLEAN ), enhanced);
		values.put(new Field ("SMID", FieldType.LONG ), (long)SerialNumOfMatcher);
		Field tTermID = new Field ("TargetTermID", FieldType.LONG );
		Field cTermID = new Field ("CandidateTermID", FieldType.LONG );
		Field conf = new Field ("confidence", FieldType.DOUBLE );	
		for (Match match : matches)
		{
			Term candidateTerm = match.getCandidateTerm();
			Term targetTerm = 	match.getTargetTerm();
			
			//write the term to the DB
			writeTermToDB(schemasExp.getTargetID(),targetTerm);
			writeTermToDB(schemasExp.getCandidateID(),candidateTerm);
			
			values.put(tTermID , targetTerm.getId());
			values.put(cTermID, candidateTerm.getId());
			values.put(conf , match.getEffectiveness());
			if ((Double)values.get(conf)>1)
			{
				System.err.println("oops");
			}
			db.insertSingleRow(values, "similaritymatrices");
		}
	}
	
	/**
	 * Adds an experiment to the experiments table and the schemapairs to the experiment schema pairs table
	 * Otherwise 
	 * @param myExpRunner.getDS()
	 * @param k_Schemapairs
	 * @return Experiment ID from db. Returns null if writing experiment into DB fails (due to missing ontology files, etc.)
	*/
	public long writeExperimentsToDB(ArrayList<ExperimentSchemaPair> ds, ArrayList<String[]> k_Schemapairs, String dsurl) {
		
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
		String str = ("SMB(E," + dsurl + "  " + time.toString()+ ",s)");
		values.put(f, (Object)str);
		
		db.insertSingleRow(values, "experiments");

		//document the experiment into experimentschemapairs table
		//k_Schemapairs holds the info from the DB (ontology's id, etc)
		for(String[] s : k_Schemapairs)
		{
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
		}
		return currentEID;
		
	}
	
	/**
	 *Receives an single term and parses it to DB, if terms already exist returns without documenting it
	 * @param Term  
	 * @param OntologyID - ID of the onotology the term belongs to
	 */
	
	private void writeTermToDB(long OntologyID, Term term) {
		
			
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
	
	/**
	  * @param id
	 * @param ontologyID
	 * @return
	 */
	public boolean checkTermExistenceAtDB(long id, long ontologyID) {
	String sql = "SELECT SchemaID,Tid From terms WHERE Tid=" + id +  " AND SchemaID=" + ontologyID + ";";
	ArrayList<String[]> existInDB =  db.runSelectQuery(sql, 1);
	if (!existInDB.isEmpty())
		return true;
	return false;
	}
		
	/**
	 * @param domain
	 * @return
	 */
	public static int getDomainNumber(String domain) {
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
	 * @param measureName similarity measure (first line matcher) name
	 * @param matcherName 2cnd Line matcher name 
	 * @param sysCode matching system code
	 * @param spid schema pair id
	 * @param mapping ArrayList of mappings : [SPID,CandidateTermID,TargetTermID,MatcherID,SMID]
	 * @param eid experiment id
	 */
	public void upload2ndLineMatchToDB(String measureName,String matcherName, boolean enhanced, int sysCode,
			long spid, ArrayList<String[]> mapping, Object eid) {
		HashMap<Field, Object> values = new HashMap<Field, Object>();
		values.put(new Field("EID", FieldType.LONG),eid);
		values.put(new Field("SPID",FieldType.LONG),spid);
		values.put(new Field("MatcherID",FieldType.LONG), getMID(matcherName,sysCode));
		values.put(new Field("SMID",FieldType.LONG), getSMID(measureName,sysCode));
		Field candTerm = new Field("CandidateTermID",FieldType.LONG);
		Field targTerm = new Field("TargetTermID",FieldType.LONG);
		values.put(new Field("enhanced",FieldType.BOOLEAN),enhanced);
		for (String[] mappingRow : mapping) 
		{
			values.put(candTerm, Long.parseLong(mappingRow[1]));
			values.put(targTerm, Long.parseLong(mappingRow[2]));
			db.insertSingleRow(values , "mapping");
		}
	}
	
	/**
	 * Get the (2nd Line) matcher ID for the supplied name and system
	 * @param MName Matcher Name
	 * @param sysCode Matching System
	 * @return
	 */
	public Long getMID(String MName, int sysCode) 
	{
		String sql = "SELECT `MatcherID` FROM `matchers`" + 
					 " WHERE `MatcherName` = '" + MName + "' AND `System` = " + sysCode +  ";";
		return Long.parseLong(db.runSelectQuery(sql, 1).get(0)[0]);
	}

	/**
	 * Get the similarity measure (first line matcher) ID for the supplied name and system
	 * @param SMName similarity measure name
	 * @param sysCode matching system
	 * @return
	 */
	public Long getSMID(String SMName, int sysCode) 
	{
		String sql = "SELECT `similaritymeasures`.`SMID` FROM `similaritymeasures` WHERE `similaritymeasures`.`MeasureName`= '"+SMName+"' AND `similaritymeasures`.`System`='" + sysCode + "';";
		return Long.parseLong(db.runSelectQuery(sql, 1).get(0)[0]);
	}

	
	/**
	 * Uploads clarity values recieved from SMB_Service to db
	 * @param clarityRes ArrayList of rows recieved from Clarity.tab result of SMB matching
	 * @param schemasExp Schema Expirment object to get schema ID, EID and SPID
	 * @param sm Similarity Measure ID
	 */
	public void loadClarityToDB(ArrayList<String[]> clarityRes,
			ExperimentSchemaPair schemasExp, int sm) 
	{
		HashMap<Field, Object> values = new HashMap<Field, Object>(); 
		Field schemaID = new Field("SchemaID",FieldType.LONG);
		Field termID = new Field("TermID",FieldType.LONG);
		Field val = new Field("ClarityScore",FieldType.DOUBLE);
		values.put(new Field("SMID",FieldType.LONG), new Long(sm));
		values.put(new Field("EID",FieldType.LONG), eid);
		values.put(new Field("SPID",FieldType.LONG), schemasExp.getSPID());
		for (String[] row : clarityRes)
		{
			values.put(schemaID, Long.parseLong(row[1]));
			values.put(termID, Long.parseLong(row[2]));
			values.put(val, Double.parseDouble(row[3]));
			db.insertSingleRow(values, "clarityscore");
		}
		
	}
}

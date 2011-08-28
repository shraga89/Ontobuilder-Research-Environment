/**
 * 
 */
package ac.technion.schemamatching.experiments;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import technion.iem.schemamatching.dbutils.DBInterface;
import technion.iem.schemamatching.dbutils.Field;
import technion.iem.schemamatching.dbutils.Field.FieldType;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.ExperimentSchemaPair;
import ac.technion.schemamatching.experiments.OBExperimentRunner;


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
	
	/**
	 * Class constructor creates an experiment in the DB and stores it's eid
	 * @param experimentDescription
	 */
	public ExperimentDocumenter(ArrayList<ExperimentSchemaPair> dataset, String experimentDescription)
	{
		this(experimentDescription);
		documentDataset(dataset);
	}


	/**
	 * @param dataset
	 */
	private void documentDataset(ArrayList<ExperimentSchemaPair> dataset) {
		MatchInformation exactMapping;
		HashMap<Field, Object> values = new HashMap<Field, Object>();
		Field spID = new Field("SPID",FieldType.INT);
		values.put(new Field("EID",FieldType.LONG), eid);
		ArrayList<ExperimentSchemaPair> badSE = new ArrayList<ExperimentSchemaPair>();
		    for (ExperimentSchemaPair schemasExp : dataset) 
		    {
				// 2.1 load from file into OB objects
		    	exactMapping = schemasExp.getExact();
		        long spid = schemasExp.getSPID();
		        if (exactMapping == null )
		        {	
		        	badSE.add(schemasExp);
		        	System.err.println("Bad spid: "  + spid);
		        	continue;
		        }
		        
				//Document experiment schemapairs
		        values.put(spID, schemasExp.getSPID());
				db.insertSingleRow(values, "experimentschemapairs");
				//Document schema parameters
				AddInfoAboutSchemaToDB(schemasExp.getCandidateID(), schemasExp.getCandidateOntology());
				AddInfoAboutSchemaToDB(schemasExp.getTargetID(), schemasExp.getTargetOntology());
				//Document term parameters
				for (Object o : exactMapping.getOriginalCandidateTerms())
					writeTermToDB(schemasExp.getCandidateID(), (Term)o);
				for (Object o : exactMapping.getOriginalTargetTerms())
					writeTermToDB(schemasExp.getTargetID(), (Term)o);
				//Document exact match
		        uploadExactMatch(exactMapping, spid);
		        
		    }
		    
	        dataset.removeAll(badSE);
	  		System.out.println("DataSet size is: " + dataset.size());
	}

	
	/**
	 * Class constructor creates an experiment in the DB and stores it's eid
	 * @param experimentDescription
	 */
	private ExperimentDocumenter(String experimentDescription)
	{
		this.db = OBExperimentRunner.getOER().getDB();
		//set the current EID to be one past the largest EID 
		eid = (Integer.parseInt((db.runSelectQuery("SELECT Max(EID) FROM experiments;",1)).get(0)[0]))+1; //write a query which retrieves the maximal EID in the db
		HashMap<Field, Object> values = new HashMap<Field, Object>();
	    values.put(new Field("EID",FieldType.LONG),eid);
	    values.put(new Field("ExperimentDesc",FieldType.STRING),experimentDescription);
	    db.insertSingleRow(values, "experiments"); //enter the name of the table 
		
	}


	/**
	 * If exact match is not documented in db, document it
	 * @param exactMapping SchemaTranslator object with mappings between terms. In each pair, assuming first is candidate and second is target
	 * @param spid Schema Pair ID
	 */
	private void uploadExactMatch(MatchInformation exactMapping, long spid) {
		String sql = "SELECT SPID FROM exactmatches WHERE SPID = " + spid + ";";
		if (db.runSelectQuery(sql,1).isEmpty())
		{
		    HashMap<Field, Object> values = new HashMap<Field, Object>();
		    values.put(new Field("SPID",FieldType.LONG),spid);
		    Field targTerm = new Field("TargetTermID",FieldType.LONG);
		    Field candTerm = new Field("CandidateTermID",FieldType.LONG);
		    for (Object o : exactMapping.getMatches())
		    {
		    	Match match = (Match)o;
		    	values.put(candTerm, match.getCandidateTerm().getId());
		    	values.put(targTerm, match.getTargetTerm().getId());
		    	if (db.runSelectQuery("SELECT * FROM exactmatches WHERE SPID='" + spid + "' AND TargetTermID='" + match.getTargetTerm().getId() + "' AND CandidateTermID='"+ match.getCandidateTerm().getId() + "';" , 4).isEmpty())
		    		db.insertSingleRow(values, "exactmatches");
		    }
		}
	}
	
	/**
	 * Documents a similarity matrix created in an experiment
	 * Note that this is only required if some manipulation was 
	 * done on a similarity matrix generated by a 1st line matchers. 
	 * Regular similarity matrices generated by 1st linematchers are
	 * automatically documented when ExperimentSchemaPair.getSimilarityMatrix(int smid)
	 * is invoked. 
	 * @param spid Schema Pair ID
	 * @param mi - holds a similarity matrix
	 * @param step - used to differentiate between several matrices created in an experiment
	 */
	public void documentSimMatrix(long spid, MatchInformation mi,int step)
	{
		HashMap<Field, Object> values = new HashMap<Field, Object>();
	    values.put(new Field("EID",FieldType.LONG),eid);
	    values.put(new Field("SPID",FieldType.LONG),spid);
	    values.put(new Field("step",FieldType.INT),spid);
		for (Object o : mi.getMatches()){
			Match m = (Match)o;
			double confidence = m.getEffectiveness();
			values.put(new Field("CandidateTerm",FieldType.LONG),m.getCandidateTerm().getId());
		    values.put(new Field("TargetTerm",FieldType.LONG),m.getTargetTerm().getId());
		    values.put(new Field("Confidence",FieldType.DOUBLE),confidence);
		    db.insertSingleRow(values, "experimentsm");
		}
	} 
	
	
	/**
	 * Documents a mappings from an experiment in the database
	 * @param spid schema pair id on which experiment was performed
	 * @param smid 1st line matcher that generated the similarity matrix
	 * @param mid 2nd line matcher id with which mapping was performed
	 * @param step Differentiators to allow separation between different results in the same experiment
	 * @param mi Mapping result to document
	 */
	public void documentMapping(long spid,int smid,int mid, int step, MatchInformation mi)
	{
		HashMap<Field, Object> values = new HashMap<Field, Object>();
	    values.put(new Field("EID",FieldType.LONG),eid);
	    values.put(new Field("SPID",FieldType.LONG),spid);
	    values.put(new Field("MID",FieldType.INT),mid);
	    values.put(new Field("SMID",FieldType.INT),smid);
	    values.put(new Field("step",FieldType.INT),step);
		for (Object o : mi.getMatches())
		{
			Match m = (Match)o;
			values.put(new Field("CandidateTerm",FieldType.LONG),m.getCandidateTerm().getId());
		    values.put(new Field("TargetTerm",FieldType.LONG),m.getTargetTerm().getId());
		    db.insertSingleRow(values, "experimentmap");
		}
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
	 * @deprecated
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
	 * @deprecated obsolete, use documentMapping instead
	 */
	public void upload2ndLineMatchToDB(String measureName,String matcherName, boolean enhanced, int sysCode,
			long spid, ArrayList<String[]> mapping) {
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
	 * @deprecated should be generalized to document experiment statistics in the db
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
	
	/**
	 * if schema pair is mapped by this 2ndLineMatcher retrieve the mapping
	 * @param secondLineM 2nd Line Matcher Name
	 * @param SMName Similarity Measure Name (1st line matcher)
	 * @param spid Schema Pair ID
	 * @param sysCode Matching system code
	 * @deprecated Mappings are experiment specific, consider if needed at all
	 * @return mapping : ArrayList of [SPID,CandidateTermID,TargetTermID,MatcherID,SMID] arrays
	 */
	public ArrayList<String[]> getMappings(String secondLineM, String SMName, long spid, int sysCode) 
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
	 * This method gets an ontology and ontology's id and updates into the DB additional info about the schema
	 * @param schemaID 
	 * @param Onotology
	 * @throws Exception
	 */
	private void AddInfoAboutSchemaToDB(long schemaID, Ontology ontology)
	{
		String sql  = "SELECT Was_Fully_Parsed FROM schemata WHERE SchemaID=" + schemaID + ";"; 
		String[] schemaList = db.runSelectQuery(sql, 1).get(0);
		//check the flag of the ontology to see if it was parsed before
		if (Double.valueOf(schemaList[0])==1)	return;
		int NumOfClasses = getNumberOfClasses (ontology);
		ArrayList <Integer> A = calculateTermsHiddenAssociationInOntology (ontology);
		//For webforms only recursively count terms that the ontology class is not "hidden" 
		sql = "UPDATE schemata SET `Was_Fully_Parsed` = '1',`Max_Height_of_the_class_hierarchy`= '" + ontology.getClassHeight() + 
		"', `Number_of_classes` =  '" + NumOfClasses + "', `Number_of_association_relationships` = '" + A.get(2) + 
		"', `Number_of_attributes_in_Schema`= '" + A.get(0) + "', `Number_of_visible_items` = '" +  (A.get(1)-A.get(0)) + 
		"', `Number_of_instances` = '" + 0 + "' WHERE SchemaID = '" + schemaID + "';";
		db.runUpdateQuery(sql);
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
		Vector<OntologyClass> v = ontology.getClasses(true);
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
	private static int iterativeCountingOfClasses(OntologyClass c) 
	{
		int count = 0;
		count+= c.getSubClassesCount();
		for (int i =0; i<c.getSubClassesCount(); i++){
			count+=iterativeCountingOfClasses(c.getSubClass(i));
		}
		return count;
}
}


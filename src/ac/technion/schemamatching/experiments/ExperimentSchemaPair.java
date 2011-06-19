package ac.technion.schemamatching.experiments;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapperException;
import schemamatchings.util.SchemaMatchingsUtilities;
import schemamatchings.util.SchemaTranslator;
import technion.iem.schemamatching.dbutils.DBInterface;

import com.modica.ontology.*;
import com.modica.ontology.match.MatchInformation;

/**
 * <p>Title: Schema Pair Matching Experiment</p>
 *
 * <p>Description: A Schema experiment consists of 2 schemas and an optional exact match.  
 *
 * @author Tomer Sagi (Version 1.0 by Anan Marie)
 * @version 2.0
 */

public class ExperimentSchemaPair {


	/**
	 * Class Constructor. Loads schema pairs, ontologies and exact match if exists
	 * @param inExpRunner
	 * @param spid
	 * @param candidateID
	 * @param targetID
	 */
	public ExperimentSchemaPair(OBExperimentRunner inExpRunner,int spid) 
	{
		parent = inExpRunner;
		SPID = spid;
		loadXML();
		basicMatrices = new HashMap<Integer,MatchInformation>();
	}	

	public boolean hasExactMatch(){
		return (exactMapping == null);
	}
	
	public int getSPID (){
	  return this.SPID;
	}

	public Ontology getTargetOntology() { 
		return target;
	}
	
	public Ontology getCandidateOntology()	{
		return candidate;
	}
	
	public SchemaTranslator getExactMapping() {
		return exactMapping;
	}

  
  /**
   * Create schemaExp objects and adds them to the dataset. Each object includes
   * A target Schema, Candidate Schema and Exact mapping.
   * Assumes that each sub-directory includes 2 schemas and an exact match with .xml type
   * Assumes exact match ends with the string "xml_EXACT.xml"
   */
  private void loadXML() 
  {
	  //Load candidate ontology
	  String sql = "SELECT path FROM schemapairs WHERE SchemaID = " + candidateID + ";";
	  ArrayList<String[]> res = parent.db.runSelectQuery(sql, 1);
	  if (res.isEmpty()) OBExperimentRunner.error("No url recieved from the database for schema no." + Integer.toString(candidateID));
      String sTargetOnologyFileName = parent.dsurl + res.get(0)[0];
      try {target = parent.obw.readOntologyXMLFile(sTargetOnologyFileName ,false);}
      catch (Exception e) {
		  e.printStackTrace();
		  OBExperimentRunner.error("XML Load failed on:" + sTargetOnologyFileName);
      }
      
      //Load target ontology
      sql = "SELECT path FROM schemapairs WHERE SchemaID = " + targetID + ";";
	  res = parent.db.runSelectQuery(sql, 1);
	  if (res.isEmpty()) OBExperimentRunner.error("No url recieved from the database for schema no." 
			  + Integer.toString(targetID));
      String sCandidateOnologyFileName = parent.dsurl + res.get(0)[0];
      try {candidate = parent.obw.readOntologyXMLFile(sCandidateOnologyFileName ,false);}
      catch (Exception e) {
		  e.printStackTrace();
		  OBExperimentRunner.error("XML Load failed on:" + res.get(0)[0]);
      }

      //Load exact match
      	sql = "SELECT path FROM schemapairs WHERE spid = " + SPID + ";";
		res = parent.db.runSelectQuery(sql, 1);
		if (res.isEmpty())
			exactMapping = null;
		else
		{
			String sExactMappingFileName = parent.dsurl + res.get(0)[0];
    	  try 
    	  {
    		  mi = parent.obw.matchOntologies(candidate, target, MatchingAlgorithms.TERM);
    		  exactMapping = SchemaMatchingsUtilities.readXMLBestMatchingFile(sExactMappingFileName,mi.getMatrix());
    		  exactMapping.importIdsFromMatchInfo(mi, true);
    	  }
    	  catch (OntoBuilderWrapperException e){	
    		  e.printStackTrace(); 
    		  OBExperimentRunner.error("Failed to match schema pair: " + Integer.toString(SPID));
    	  } 
    	  catch (Exception e) 
    	  {
			e.printStackTrace();
			OBExperimentRunner.error("XML Load failed on:" + sExactMappingFileName);
    	  }
      }
    }
  	
  /**
   * Return a basic similarity matrix using the supplied SMID
   * Retrieves from the db if it exists, otherwise creates it and documents it in the DB
   * @param smid
   * @param db
   * @param obw
   * @param smName 
   * @return
   */
  	public MatchInformation getSimilarityMatrix(int smid)
  	{
  		if (!basicMatrices.containsKey(smid))
  		{
  		 	MatchInformation mi = null;
			if (!checkIfSchemaPairWasMatched(SPID,smid,parent.db))
  		 	{
  		 		try {
					mi = parent.obw.matchOntologies(candidate, target, parent.frstLineMatchers.get(smid));
				} catch (OntoBuilderWrapperException e) {
					e.printStackTrace();
				}
  		 	}
  		 	else
  		 	{mi = DBInterface.createMIfromArrayList(candidate, target, getSimilarityMatrixFromDB(smid,SPID, parent.db));}
  		 	basicMatrices.put(smid, mi);
  		}
  		
  		return basicMatrices.get(smid);
  	}

	public long getTargetID() 
	{
		return targetID;
	}

	public long getCandidateID() 
	{
		return candidateID;
	}
	
	 public static long PJWHash(String str)

	    {

	       long BitsInUnsignedInt = (long)(4 * 8);
	       long ThreeQuarters     = (long)((BitsInUnsignedInt  * 3) / 4);
	       long OneEighth         = (long)(BitsInUnsignedInt / 8);
	       long HighBits          = (long)(0xFFFFFFFF) << (BitsInUnsignedInt - OneEighth);
	       long hash              = 0;
	       long test              = 0;
	       for(int i = 0; i < str.length(); i++)
	       {
	          hash = (hash << OneEighth) + str.charAt(i);
	          if((test = hash & HighBits)  != 0)
	          {
	             hash = (( hash ^ (test >> ThreeQuarters)) & (~HighBits));
	          }
	       }
	       return hash;
	    }
	
	 /**
		 * This method receives  a name of an ontology and returns its ID from the DB (Schema Table)
		 * @param name - Name of the ontonolgy
		 * @param db - an open connection to the DB
		 * @return ID, if wasn't able to find the ontology return -1
		 */
	 public long getOntologyDBId (String name, DBInterface db) {
		String sql = "SELECT SchemaID From schemata WHERE SchemaName= \"" + name + "\";";
		ArrayList<String[]> SchameID =  db.runSelectQuery(sql, 1);
		long Id=0;
		try {
			Id = Long.valueOf(SchameID.get(0)[0]);
		}
		catch (IndexOutOfBoundsException e){
			System.out.println ("Ontology not found:");
			return -1;
		}
			return Id;
	 	}

  private OBExperimentRunner parent;
  private Ontology target;
  private Ontology candidate;
  private int targetID;
  private int candidateID;
  private SchemaTranslator exactMapping;
  private int SPID;
  private HashMap<Integer,MatchInformation> basicMatrices;
  private MatchInformation mi;
  
  /**
	 * Return the similarity matrix of the supplied schema pair using the similarity measure supplied
	 * If the matrix isn't documented in the db, returns null
	 * @param smid code of matching algorithm
	 * @param spid Schema Pair ID to look for
	 * @param db 
	 * @return String array {SMID,CandidateSchemaID,CandidateTermID,TargetSchemaID,TargetTermID,confidence} or null if data isn't found
	 */
	private ArrayList<String[]> getSimilarityMatrixFromDB(int smid, long spid, DBInterface db) 
	{
		
		ArrayList<String[]> res = null;
		String sql = "SELECT `similaritymatrices`.`SMID`,`similaritymatrices`.`CandidateSchemaID` , `similaritymatrices`.`CandidateTermID`, `similaritymatrices`.`TargetSchemaID` , `similaritymatrices`.`TargetTermID` , `similaritymatrices`.`confidence` FROM `schemapairs` INNER JOIN `similaritymatrices` ON (`schemapairs`.`TargetSchema` = `similaritymatrices`.`TargetSchemaID`) AND (`schemapairs`.`CandidateSchema` = `similaritymatrices`.`CandidateSchemaID`) WHERE (`similaritymatrices`.`SMID` = '" + smid + "' AND `schemapairs`.`SPID` = " + spid + ");";
		res = db.runSelectQuery(sql, 6);
		return res;
	}
	
	/**
	 * This method checks if a schema pair was matched before with this matcher 
	 * and stored in the DB. if so, returns true
	 * @param schemaPairId Schema Pair ID
	 * @param smid Similarity Measure ID
	 * @param db DBInterface to use to check in
	 * @return boolean 
	 */
	private static boolean checkIfSchemaPairWasMatched(int schemaPairId,Integer smid,DBInterface db) {
		String sql  = "SELECT `similaritymatrices`.`confidence` FROM `schemamatching`.`schemapairs` INNER JOIN `schemamatching`.`similaritymatrices` " +
        			  " ON (`schemapairs`.`TargetSchema` = `similaritymatrices`.`TargetSchemaID`) AND (`schemapairs`.`CandidateSchema` = `similaritymatrices`.`CandidateSchemaID`) " +
        			  " WHERE (`similaritymatrices`.`SMID` = " + smid + " AND `schemapairs`.`SPID` = " + schemaPairId + ");"; 
		ArrayList<String[]> schemaList = db.runSelectQuery(sql, 1);
		if (!schemaList.isEmpty())
				return true;
		return false;
	}

	
		/**
		 * This method gets an ontology and ontology's id and updates into the DB additional info about the schema
		 * @param schemaID 
		 * @param Onotology
		 * @throws Exception
		 */
	public void AddInfoAboutSchemaToDB(DBInterface db) throws Exception 
	{
		int i=0;
		long schemaID;
		while(i<1){
			if (i==0) schemaID = (long)targetID;
			else schemaID = (long)candidateID;
			String sql  = "SELECT * FROM schemata WHERE SchemaID=\"" + schemaID + "\";"; 
			String[] schemaList = db.runSelectQuery(sql, 13).get(0);
			//check the flag of the ontology to see if it was parsed before
			if (Double.valueOf(schemaList[12])==1)	{i++; continue;}
			Ontology ontology;
			if (i==0) ontology = target;
			else ontology = candidate;
			int NumOfClasses = getNumberOfClasses (ontology);
			ArrayList <Integer> A = calculateTermsHiddenAssociationInOntology (ontology);
			//For webforms only recursively count terms that the ontology class is not "hidden" 
			sql = "UPDATE schemata SET `Was_Fully_Parsed` = '1',`Max_Height_of_the_class_hierarchy`= '" + ontology.getHeight() + 
			"', `Number_of_classes` =  '" + NumOfClasses + "', `Number_of_association_relationships` = '" + A.get(2) + 
			"', `Number_of_attributes_in_Schema`= '" + A.get(0) + "', `Number_of_visible_items` = '" +  (A.get(1)-A.get(0)) + 
			"', `Number_of_instances` = '" + 0 + "' WHERE SchemaID = '" + schemaID + "';";
			db.runUpdateQuery(sql);
			i++;
			}
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
	
	

}
  

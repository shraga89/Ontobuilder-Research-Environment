package ac.technion.schemamatching.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapperException;
import schemamatchings.util.SchemaMatchingsUtilities;
import technion.iem.schemamatching.dbutils.DBInterface;

import ac.technion.schemamatching.matchers.FirstLineMatcher;

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
	
	public MatchInformation getExact() {
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
	  //Get ids
	  String sql = "SELECT CandidateSchema, TargetSchema FROM schemapairs WHERE SPID = " + SPID + ";";
	  ArrayList<String[]> res = parent.db.runSelectQuery(sql, 2);
	  candidateID = Integer.parseInt(res.get(0)[0]);
	  targetID = Integer.parseInt(res.get(0)[1]);
	  //Load candidate ontology
	  sql = "SELECT filePath FROM schemata WHERE SchemaID = " + candidateID + ";";
	  res = parent.db.runSelectQuery(sql, 1);
	  if (res.isEmpty()) OBExperimentRunner.error("No url recieved from the database for schema no." + Integer.toString(candidateID));
      String sTargetOnologyFileName = parent.dsurl + res.get(0)[0];
      try {target = parent.obw.readOntologyXMLFile(sTargetOnologyFileName ,false);}
      catch (Exception e) {
		  e.printStackTrace();
		  OBExperimentRunner.error("XML Load failed on:" + sTargetOnologyFileName);
      }
      
      //Load target ontology
      sql = "SELECT filePath FROM schemata WHERE SchemaID = " + targetID + ";";
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
    		  exactMapping = parent.obw.matchOntologies(candidate, target, MatchingAlgorithms.TERM);
    		  SchemaMatchingsUtilities.readXMLBestMatchingFile(sExactMappingFileName,exactMapping.getMatrix());
    		  //exactMapping.importIdsFromMatchInfo(mi, true);
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
   * Return a basic similarity matrix using the supplied @link{FirstLineMatcher}
   * Retrieves from the db if it exists, otherwise creates it and documents it in the DB
   * @param flm 
   * @return
   */
  	public MatchInformation getSimilarityMatrix(FirstLineMatcher flm)
  	{
  		int smid = flm.getDBid();
  		if (!basicMatrices.containsKey(smid))
  		{
  		 	MatchInformation mi = null;
			if (!checkIfSchemaPairWasMatched(SPID,smid,parent.db))
  		 	{
				mi = flm.match(candidate, target, false);
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
  private MatchInformation exactMapping;
  private int SPID;
  private HashMap<Integer,MatchInformation> basicMatrices;
  
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

}
  

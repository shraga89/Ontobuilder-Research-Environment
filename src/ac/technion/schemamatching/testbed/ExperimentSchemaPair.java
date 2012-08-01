package ac.technion.schemamatching.testbed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import technion.iem.schemamatching.dbutils.DBInterface;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.io.imports.Importer;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.util.ConversionUtils;


/**
 * <p>Title: Schema Pair Matching Experiment</p>
 *
 * <p>Description: A Schema experiment consists of 2 schemas and an optional exact match.  
 *
 * @author Tomer Sagi (Version 1.0 by Anan Marie)
 * @version 2.1
 */

public class ExperimentSchemaPair {


	/**
	 * Class Constructor. Loads schema pairs, ontologies and exact match if exists
	 * @param spid
	 * @param candidateID
	 * @param targetID
	 */
	public ExperimentSchemaPair(int spid,int dsid) 
	{
		SPID = spid;
		dsEnum = OREDataSetEnum.getByDbid(dsid);
		load(); 
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
  private void load() 
  {
	  
	  //get paths
	  String exactMatchPath;
	  String candPath;
	  String targPath;
	  Importer imp = dsEnum.getImporter();
	  String sql = "SELECT CandidateSchema, TargetSchema, DSID, path FROM schemapairs WHERE SPID = " + SPID + ";";
	  ArrayList<String[]> res = OBExperimentRunner.getOER().getDB().runSelectQuery(sql, 4);
	  candidateID = Integer.parseInt(res.get(0)[0]);
	  targetID = Integer.parseInt(res.get(0)[1]);
	  int dsid = Integer.parseInt(res.get(0)[2]);
	  exactMatchPath = res.get(0)[3];
	  
	  //For ontobuilder webform dataset (DSID ==1) the path of the schemas is parsed from the path of the exact match
	  if (dsid==1)
	  {
		//For example: WebForm/edit.travel.yahoo.com.xml_www.klm.com.xml_EXACT/edit.travel.yahoo.com.xml_www.klm.com.xml_EXACT.xml
		  String pairFolder = exactMatchPath.split("/")[1];
		  candPath =  OBExperimentRunner.getOER().getDsurl() + exactMatchPath.split("/")[0] +  File.separatorChar + pairFolder + File.separatorChar + pairFolder.split("_")[0];
		  targPath =  OBExperimentRunner.getOER().getDsurl() + exactMatchPath.split("/")[0] +  File.separatorChar + pairFolder + File.separatorChar + pairFolder.split("_")[1];;
	  }
	  else //Non ontobuilder webform dataset
	  {
		  //get target path from db
		  sql = "SELECT filePath FROM schemata WHERE SchemaID = " + targetID + ";";
		  res = OBExperimentRunner.getOER().getDB().runSelectQuery(sql, 1);
		  if (res.isEmpty()) OBExperimentRunner.error("No url recieved from the database for schema no." + Integer.toString(candidateID));
		  targPath = OBExperimentRunner.getOER().getDsurl() + res.get(0)[0];
		  //get candidate path from db
		  sql = "SELECT filePath FROM schemata WHERE SchemaID = " + candidateID + ";";
		  res = OBExperimentRunner.getOER().getDB().runSelectQuery(sql, 1);
		  if (res.isEmpty()) OBExperimentRunner.error("No url recieved from the database for schema no." 
				  + Integer.toString(targetID));
		  candPath = OBExperimentRunner.getOER().getDsurl() + res.get(0)[0];
	  }
	  //load schemas to ontologies
	  target = loadOntologyFromPath(targPath, imp);
      candidate = loadOntologyFromPath(candPath, imp);
      
      //load exact match
      try {
    	  if (dsEnum.isHasExact())
    		  exactMapping = dsEnum.getMatchImp().importMatch(new MatchInformation(candidate,target), new File(OBExperimentRunner.getOER().getDsurl() + exactMatchPath));
    	  else
    		  exactMapping = null;    	  
    	  }
    	  catch (Exception e) {
			e.printStackTrace();
			OBExperimentRunner.error("XML Load failed on:" + exactMatchPath);}
      }

/**
 * @param schemaFilePath
 * @param imp
 * @return 
 */
private Ontology loadOntologyFromPath(String schemaFilePath, Importer imp) {
	try {
		  	File schemaFile = new File(schemaFilePath);
		  	if (dsEnum.isHasInstances())
		  	{
		  		File instanceFile = new File(schemaFilePath.substring(0, schemaFilePath.length()-4) + ".xml");
		  		return imp.importFile(schemaFile,instanceFile);
		  	}
		  	else
		  		return imp.importFile(schemaFile);}
      catch (Exception e) {
		  e.printStackTrace();
		  OBExperimentRunner.error("File Load failed on:" + schemaFilePath);
      }
	return null;
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
  			if (!this.dsEnum.isSupportsDBLookUp())
  	  		{
  	  			System.err.println("The " + dsEnum.name() + 
  	  					" dataset does not support database lookup, matching ontologies instead.");
  	  			mi = flm.match(candidate, target, false);
  	  		}
  			else if (!OBExperimentRunner.getOER().getDoc().checkIfSchemaPairWasMatched(SPID,smid))
  		 	{
				mi = flm.match(candidate, target, false);
				assert(mi!=null);
				//If match information dimensions are reduced, expand them
				if (mi.getCandidateOntology().getAllTermsCount()>mi.getMatrix().getColCount())
				{ConversionUtils.fillMI(mi);}
				//document similarity matrix
				try {
					OBExperimentRunner.getOER().getDoc().loadSMtoDB(mi, this, smid);
				} catch (IOException e) {
					e.printStackTrace();
				}
  		 	}
  		 	else
  		 	{mi = DBInterface.createMIfromArrayList(candidate, target, getSimilarityMatrixFromDB(smid,SPID, OBExperimentRunner.getOER().getDB()));}
			assert(mi!=null);
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

  private Ontology target;
  private Ontology candidate;
  private int targetID;
  private int candidateID;
  private MatchInformation exactMapping;
  private int SPID;
  private HashMap<Integer,MatchInformation> basicMatrices;
  private OREDataSetEnum dsEnum;
  
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
		String sql = "SELECT `similaritymatrices`.`SMID`,`similaritymatrices`." +
				"`CandidateSchemaID` , `similaritymatrices`.`CandidateTermID`, " +
				"`similaritymatrices`.`TargetSchemaID` , `similaritymatrices`." +
				"`TargetTermID` , `similaritymatrices`.`confidence` " +
				"FROM `schemapairs` INNER JOIN `similaritymatrices` " +
				"ON (`schemapairs`.`TargetSchema` = `similaritymatrices`.`TargetSchemaID`) " +
				"AND (`schemapairs`.`CandidateSchema` = `similaritymatrices`.`CandidateSchemaID`)" +
				" WHERE (`similaritymatrices`.`SMID` = '" + smid + "' " +
						"AND `schemapairs`.`SPID` = " + spid + ");";
		res = db.runSelectQuery(sql, 6);
		return res;
	}
	
	public String toString()
	{
		return Integer.toString(SPID);
	}
}
  

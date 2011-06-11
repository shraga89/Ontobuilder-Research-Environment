/**
 * 
 */
package technion.iem.schemamatching.experiments;

import java.util.ArrayList;
import java.util.HashMap;

import schemamatchings.meta.match.MatchedAttributePair;
import schemamatchings.util.SchemaTranslator;
import technion.iem.schemamatching.dbutils.DBInterface;
import technion.iem.schemamatching.dbutils.Field;
import technion.iem.schemamatching.dbutils.Field.FieldType;
import technion.iem.schemamatching.experiments.ExperimentSchemaPair;
import technion.iem.schemamatching.experiments.OBExperimentRunner;

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
	 * @param mi
	 */
	public void documentSimMatrix(long spid, MatchInformation mi)
	{
		//TODO stub
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

	public int getEid() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	/**
	 * Make sure similarity measures supplied exist in the DB 
	 * @param availableMatchers List of matchers to lookup
	 * @param sysCode system code of matchers
	 * @deprecated TODO create as an enum type or fill into a hashmap or something
	 * @return list of SimilarityMeasure and matcherName pairs as a String array
	 */
	public ArrayList<String[]> documentSimilarityMeasures(String[] availableMatchers,int sysCode) 
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
}

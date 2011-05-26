/**
 * 
 */
package schemamatching.experiments;

import java.util.ArrayList;
import java.util.HashMap;

import technion.iem.schemamatching.dbutils.DBInterface;

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
	private ArrayList<SchemasExperiment> myExperiments;
	
	/**
	 * Class constructor creates an experiment in the DB and stores it's eid
	 * @param experimentDescription
	 */
	public ExperimentDocumenter(String experimentDescription,DBInterface db)
	{
		//TODO stub
		this.db = db;
	}

	public ArrayList<SchemasExperiment> getMyExperiments() {
		return myExperiments;
	}

	/**
	 * documents Experiment schema pairs in DB (including terms)
	 * @param myExperiments
	 */
	public void setMyExperiments(ArrayList<SchemasExperiment> myExperiments) 
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

}

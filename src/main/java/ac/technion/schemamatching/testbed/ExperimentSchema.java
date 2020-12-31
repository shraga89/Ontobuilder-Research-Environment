package ac.technion.schemamatching.testbed;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import ac.technion.schemamatching.DBInterface.DBInterface;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.io.imports.Importer;
import ac.technion.schemamatching.experiments.OBExperimentRunner;


/**
 * <p>Title: Schema for Matching Experiment</p>
 *
 * @author Tomer Sagi
 */

public class ExperimentSchema {


	/**
	 * Class Constructor. Loads schema as ontology
	 * @param schemaID databse id of schema to be loaded
	 * @param dsid data set ID of schema
	 * @throws FileNotFoundException when one of the ontologies isn't found
	 */
	public ExperimentSchema(int schemaID,int dsid) throws FileNotFoundException 
	{
		ID = schemaID;
		dsEnum = OREDataSetEnum.getByDbid(dsid);
		load(); 
	}
	
	public ExperimentSchema(Ontology o, int dsid) {
		this.o = o;
		dsEnum = OREDataSetEnum.getByDbid(dsid);
	}
	
	protected ExperimentSchema() 
	{
		ID = 0;
	}
	

	public int getID (){
	  return this.ID;
	}

	public Ontology getTargetOntology() { 
		return o;
	}
	
  /**
   * Imports schema to ontology object
 * @throws FileNotFoundException when supplied DSUrl and filepath are not found
   */
  private void load() throws FileNotFoundException 
  {
	  
	  //get paths
	  Importer imp = dsEnum.getImporter();
	  //get target path from db
	  String sql = "SELECT filePath FROM schemata WHERE SchemaID = " + ID + ";";
	o = loadOntologyFromPath(ExperimentSchemaPair.getSchemaPath(sql), imp);
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
		  OBExperimentRunner.fatalError("File Load failed on:" + schemaFilePath);
      }
	return null;
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

  private Ontology o;
  protected int ID;
  protected OREDataSetEnum dsEnum;
  
	public String toString()
	{
		return Integer.toString(ID);
	}
}
  

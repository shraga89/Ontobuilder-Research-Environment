package Application;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.StringTokenizer;

import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.ontobuilder.OntoBuilderWrapperException;
import schemamatchings.util.SchemaMatchingsUtilities;
import schemamatchings.util.SchemaTranslator;
//import Application.Documenter;

import com.modica.ontology.*;
import com.modica.ontology.match.MatchInformation;
import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * <p>Title: Schema Pair Matching Experiment</p>
 *
 * <p>Description: A Schema experiment consists of 2 schemas and an optional exact match</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Tomer Sagi (Version 1.0 by Anan Marie)
 * @version 1.1
 */

public class SchemasExperiment {
  public SchemasExperiment() {
  }
   
  public SchemasExperiment(File inSubDir) 
  {
	 subDir = inSubDir;
	 NumberOfExperimnet++;
	 this.EID = PJWHash(subDir.getAbsolutePath());
	 //String configuration = "(E," +  subDir + ",No system code)";
	 //configurationID = OB_SMB_Interface.PJWHash(configuration);
	 //configurations.put(configurationID , configuration); 
  }

  public Date getDate(){
	  return this.date;
  }
  //function returns Experiments ID
  public long getEID (){
	  return this.EID;
  }
  
  public String getConfiguration(){
	  return (String)configurations.get(configurationID);
  }

  public int getDSID(){
	  return this.DSID;
  }
  
  public void setDSID(int DSIDKey){
	  this.DSID = DSIDKey;
  }
  
  public long getCondigurationID(){
	  return this.configurationID;
  }
  
  public Ontology getTargetOntology() {
    if (target == null) loadXML();  
	return target;
  }
  public Ontology getCandidateOntology() {
	if (candidate == null) loadXML();
    return candidate;
  }
  public SchemaTranslator getExactMapping() {
	if (exactMapping == null) loadXML();
    return exactMapping;
  }

  public void setTargetOntology(Ontology targetIn) {
    target = targetIn;
  }
  public void setCandidateOntology(Ontology candidateIn) {
    candidate = candidateIn;
  }
  public void setExactMapping(SchemaTranslator exactMappingIn) {
    exactMapping = exactMappingIn;
  }

  
  /**
   * Create schemaExp objects and adds them to the dataset. Each object includes
   * A target Schema, Candidate Schema and Exact mapping.
   * Assumes that each sub-directory includes 2 schemas and an exact match with .xml type
   * Assumes exact match ends with the string "xml_EXACT.xml"
   */
  private void loadXML() {
    OntoBuilderWrapper ontoBuilderWrapper = new OntoBuilderWrapper();
    File[] aXmlFiles = subDir.listFiles();
    if (aXmlFiles == null) {
      return;
    }
    else {
      String sTargetOnologyName = null;
      String sCandidateOntologyName = null;
      String sExactMappingFileName = null;
      
      //String sTargetOnologyFileName = null;
      //String sCandidateOntologyFileName = null;

      for (int i = 0; i < aXmlFiles.length; i++) {
        File sXmlFile = aXmlFiles[i];
        String sXmlFileName = sXmlFile.getName();
        if (sXmlFileName.matches(".*xml_.*xml_EXACT.xml")) {
          StringTokenizer st = new StringTokenizer(sXmlFileName, "_");
          if (st.countTokens() != 3) {
            return;
          }
          sExactMappingFileName = sXmlFile.getPath();
          sCandidateOntologyName = st.nextToken();
          sTargetOnologyName = st.nextToken();
          break;
        }
      }
      for (int i = 0; i < aXmlFiles.length; i++) {
        File sXmlFile = aXmlFiles[i];
        String sXmlFileName = sXmlFile.getName();
        if (sXmlFileName.equals(sTargetOnologyName)) {
          sTargetOnologyFileName = sXmlFile.getPath();
        }
        if (sXmlFileName.equals(sCandidateOntologyName)) {
          sCandidateOntologyFileName = sXmlFile.getPath();
        }
      }
     
      try {target = ontoBuilderWrapper.readOntologyXMLFile(sTargetOnologyFileName,false);}
      catch (Exception e) 
      {
    	  if (target == null)
          {
        	  try
        	  {
//        		  Documenter doc = new Documenter();
//        		  doc.normalizeOntology(sTargetOnologyFileName);
        	  }
        	  catch (Exception e1)
        	  {
    		  System.out.println("XML Load failed on:" + sTargetOnologyFileName);
    		  e1.printStackTrace();
        	  System.exit(0);
        	  }
          }    	  
      }
      try {candidate = ontoBuilderWrapper.readOntologyXMLFile(sCandidateOntologyFileName,false);}
      
      catch (Exception e) 
      {
    	  if (target == null)
          {
        	  try
        	  {
//        		  Documenter doc = new Documenter();
//        		  doc.normalizeOntology(sCandidateOntologyFileName);
        	  }
        	  catch (Exception e1)
        	  {
    		  System.out.println("XML Load failed on:" + sCandidateOntologyFileName);
    		  e1.printStackTrace();
        	  System.exit(0);
        	  }
          }    	  
      }
      
      try
	  {
    	if (exactMapping == null) 
    	{
    		long mm_gen_time = System.currentTimeMillis();
    		OntoBuilderWrapper obw = new OntoBuilderWrapper();
    		try {
    		MatchInformation mi = obw.matchOntologies(candidate, target, MatchingAlgorithms.TERM);
    		mm =  mi.getMatrix();
    		}
    		catch (OntoBuilderWrapperException e)
    		{
    			e.printStackTrace();
    		}
    		mm_gen_time = System.currentTimeMillis() - mm_gen_time;
    	    System.out.println("MatchMatrix generation Time: " + mm_gen_time);
    	    exactMapping = SchemaMatchingsUtilities.readXMLBestMatchingFile(sExactMappingFileName);
    	    //exactMapping = SchemaMatchingsUtilities.readXMLBestMatchingFile(sExactMappingFileName,mm);
    	}
      }
      catch (Exception e) 
      {
    	  if (exactMapping == null)
          {
        	  try 
        	  {
//        		  Documenter doc = new Documenter();
//        		  doc.normalizeExactMatch(sExactMappingFileName, candidate, target);
        	  }
        	  catch (Exception e1)
        	  {
        		  System.out.println("XML Load failed on:" + sExactMappingFileName);
        		  e1.printStackTrace();
        		  System.exit(0);
        	  }
          }    	  
      }
      String s = sExactMappingFileName.toString() + sCandidateOntologyFileName + sTargetOnologyFileName ; 
      this.EID = PJWHash(s);
    	  
    }
  	}
  	
  	public MatchMatrix getMatchMatrix() {
  	if (mm == null) loadXML(); 
	return mm;
}

  	public void setMatchMatrix(MatchMatrix mm) {
	this.mm = mm;
}

	public File getSubDir() {
		return subDir;
	}

	public void setSubDir(File insubDir) {
		subDir = insubDir;
	}

	public String getCandidatePath()
	{
	return sCandidateOntologyFileName;
	}
	
	public String getTargetPath()
	{
	return sTargetOnologyFileName;
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
  Ontology target;
  Ontology candidate;
  SchemaTranslator exactMapping;
  MatchMatrix mm;
  File subDir; //Path of subdirectory where schema pair xml reside
  private String sTargetOnologyFileName = null;
  private String sCandidateOntologyFileName = null;
  private int DSID = 0; //default values is "not specifies"
  private long configurationID; // default according to table configurationTyps
  private long EID;
  private Date date = new Date(1);
  private static long NumberOfExperimnet = 0;
  private static HashMap configurations = new HashMap();
  
}
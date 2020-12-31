package ac.technion.schemamatching.Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;

import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.DBInterface.DBInterface;

public class OreClientIcd 
{
	
	protected DBInterface db;
	
	protected String Values_Separator = "@";
	protected String Element_Separator = ":";
	protected String Request_Separator = "#";
	protected String Error = "Error:";
	
	   
	public OreClientIcd() 
	   {
		   Properties pMap = new Properties();
			try {
				pMap.load(new FileInputStream("oreConfig/ob_interface.properties"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println((String)pMap.get("dbmstype") + " " + (String)pMap.get("host") + " " + (String)pMap.get("dbname") + " " + (String)pMap.get("username") + " " + (String)pMap.get("pwd"));
		    db = new DBInterface(Integer.parseInt((String)pMap.get("dbmstype")),(String)pMap.get("host"),(String)pMap.get("dbname"),(String)pMap.get("username"),(String)pMap.get("pwd"));
			
	   }
	
   
	
	public String HandleRequest(SchemamatchingClient client , String request)
	{
		String returnValue = request + Request_Separator;
		if(request.startsWith("GetDatasetToMatchWiteExactMatch"))
		{
			returnValue = returnValue + GetDatasetToMatchWhitExactMatch();
		}
		if(request.startsWith("GetDatasetToMatch"))
		{
			returnValue = returnValue + GetDatasetToMatch();
		}
		else if(request.startsWith("GetFirstSchemasToMatchWiteExactMatch"))
		{
			String[] array = request.split(",");
			String datasetId = array[1];
			returnValue = returnValue + GetFirstSchemasToMatchWhitExactMatch(datasetId);
		}
		else if(request.startsWith("GetFirstSchemasToMatch"))
		{
			String[] array = request.split(",");
			String datasetId = array[1];
			returnValue = returnValue + GetFirstSchemasToMatch(datasetId);
			//returnValue = returnValue + GetFirstSchemasToMatchWhitExactMatch(datasetId);
		}
		else if(request.startsWith("GetSecondSchemasToMatchWiteExactMatch"))
		{
			String[] array = request.split(",");
			String datasetId = array[1];
			String firstSchemaId = array[2];
			returnValue = returnValue + GetSecondSchemasToMatch(datasetId,firstSchemaId);
		}
		else if(request.startsWith("GetSLMParameterDefaultValue")){
			String[] array = request.split(",");
			String matcherId = array[1];
			returnValue = returnValue + GetSLMParameterDefaultValue(matcherId);
		}
		else if(request.startsWith("GetSLMParameter")){
			returnValue = returnValue + GetSLMParameter();
		}
		else if(request.startsWith("GetFLMParameter")){
			returnValue = returnValue + GetFLMParameter();
		}
		else if(request.startsWith("GetFLM")){
			returnValue = returnValue + GetFLM();
		}
		else if(request.startsWith("GetSLM")){
			returnValue = returnValue + GetSLM();
		}
		else if(request.startsWith("GetPredictors")){
			returnValue = returnValue + GetPredictors();
		}
		
		else if(request.startsWith("RunExperiment")){
			String[] array = request.split(",");
			String datasetId = array[1];
			String firstSchemaId = array[2];
			String secondSchemaId = array[3];
			String flmId = array[4];
			String slmId = array[5];
			String prediction = array[6];
			returnValue = returnValue + RunExperiment(client.ClientFolder,datasetId,firstSchemaId,secondSchemaId,flmId,slmId,prediction);
		}
		else if(request.startsWith("GetOutputFile")){
			String[] array = request.split(",");
			String fileName = array[1];
			returnValue = returnValue + GetOutputFile(client,fileName);
		}
		return returnValue;
	}
	
    private String GetOutputFile(SchemamatchingClient client,String fileName) 
    {
    	try 
    	{
	    	String listString = "";
	    	File myFile = new File(client.ClientFolder + "\\" + fileName);
	    	int length = (int) myFile.length();
	        byte[] mybytearray = new byte[length];
	       // BufferedOutputStream outToClient = new BufferedOutputStream(client.getSocket().getOutputStream());
	        OutputStream outToClient = client.getSocket().getOutputStream();
	        FileInputStream fis = null;
	        fis = new FileInputStream(myFile);
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        bis.read(mybytearray, 0, length);
	        byte[] toSend = new byte[length + 4]; 
	        
	        byte[] sizeArray = ByteBuffer.allocate(4).putInt(length).array();
	        
	        System.out.println("myFile.length(): " + length);//PRINT IT OUT TO THE SCREEN
	        
	        for(int i=0 ; i<4 ; i++)
	        {
	        	toSend[i] = sizeArray[i];
	        }
	        for(int i=0 ; i< length; i++)
	        {
	        	toSend[i+4] = mybytearray[i];
	        }
	        
	        outToClient.write(toSend, 0, length + 4);
	        outToClient.flush();
	        fis.close();
	        bis.close();
	        myFile.delete();
	        return "True";
    	} 
    	catch (FileNotFoundException ex) 
        {
            return "False";
        }
        catch (IOException ex) 
        {
        	return "False";
        }
	}

	protected String GetDatasetToMatch()
    {
    	ArrayList<String[]> list =  db.runSelectQuery("SELECT * FROM datasets", 6);
    	if(list.size() == 0)
    	{
    		return Error + "There is no dataset " ;
    	}
    	
    	String listString = "";
		for (String[] s : list)
		{
			listString += s[0] + Element_Separator + s[1] + Values_Separator;
		}
		return listString.substring(0, listString.length() - 1) ;
    }

    protected String GetDatasetToMatchWhitExactMatch()
    {
    	String query = "SELECT DSID FROM schemapairs group by DSID";
    	System.out.println("Run query: " + query);
    	ArrayList<String[]> list = db.runSelectQuery(query,1);
    	
    	if(list.size() == 0)
    	{
    		return Error + "There is no dataset " ;
    	}
    	
    	String cond = "";
    	for (String[] value : list)
		{
    		cond += " DSID=" + value[0] + " OR";
		}
    	cond = cond.substring(0, cond.length() - 3) ;
    	
    	query = "SELECT * FROM datasets where " + cond;
    	System.out.println("Run query: " + query);
    	
    	list =  db.runSelectQuery("SELECT * FROM datasets where " + cond, 6);
    	String listString = "";
		for (String[] s : list)
		{
			listString += s[0] + Element_Separator + s[1] + Values_Separator;
		}
		return listString.substring(0, listString.length() - 1) ;
    }
    
    protected String GetFirstSchemasToMatchWhitExactMatch(String datasetId)
    {
    	String query = "SELECT DISTINCT TargetSchema FROM schemapairs where DSID="+datasetId;
    	System.out.println("Run query: " + query);
    	ArrayList<String[]> list = db.runSelectQuery(query,1);

    	if(list.size() == 0)
    	{
    		return Error + "There is no Schemas in dataset " + datasetId;
    	}
    	
    	String cond = "";
    	for (String[] value : list)
		{
    		cond += " SchemaID=" + value[0] + " OR";
    		
		}
    	cond = cond.substring(0, cond.length() - 3) ;
    	
    	query = "SELECT SchemaID,SchemaName,DSID "
    			+ "FROM schemata "
    			+ "where "+ cond + " AND DSID=" + datasetId;
    	System.out.println("Run query: " + query);
    	list =  db.runSelectQuery(query, 3);
    	
    	String listString = "";

		for (String[] s : list)
		{
			listString += s[0] + Element_Separator + s[1] + Values_Separator;
		}
		return listString.substring(0, listString.length() - 1) ;
    }
    
    protected String GetFirstSchemasToMatch(String datasetId)
    {
    	String query = "SELECT SchemaID,SchemaName,DSID "
    			+ "FROM schemata "
    			+ "where DSID=" + datasetId;
    	System.out.println("Run query: " + query);
    	
    	ArrayList<String[]> list =  db.runSelectQuery(query, 3);
    	
    	if(list.size() == 0)
    	{
    		return Error + "There is no Schemas in dataset " + datasetId;
    	}
    	
    	String listString = "";

		for (String[] s : list)
		{
			listString += s[0] + Element_Separator + s[1] + Values_Separator;
		}
		return listString.substring(0, listString.length() - 1) ;
    }

    
    protected String GetSecondSchemasToMatch(String datasetId, String firstSchemaId)
    {
    	String query = "SELECT CandidateSchema "
    			+ "FROM schemapairs "
    			+ "where TargetSchema=" + firstSchemaId + " AND DSID=" + datasetId;
    	System.out.println("Run query: " + query);
    	
    	ArrayList<String[]> SecondSchemaIdlist =  db.runSelectQuery(query, 1);
    	if(SecondSchemaIdlist.size() == 0)
    	{
    		return Error + "There is no Schemas in dataset " + datasetId;
    	}
    	
    	String cond = "";
    	for (String[] secondSchemaId : SecondSchemaIdlist)
		{
    		cond += " SchemaID= " + secondSchemaId[0] + " OR";
		}
    	cond = cond.substring(0, cond.length() - 3) ;
    	
    	query = "SELECT SchemaID,SchemaName,DSID "
    			+ "FROM schemata "
    			+ "where DSID=" + datasetId + " AND " + cond;
    	System.out.println("Run query: " + query);
    	ArrayList<String[]> list = db.runSelectQuery(query, 3);
    	
    	if(list.size() == 0)
    	{
    		return Error + "There is no Schemas in dataset " + datasetId;
    	}
    	
    	String listString = "";
		for (String[] s : list)
		{
		listString += s[0] + Element_Separator + s[1] + Values_Separator;
		}
		String returnValue = listString.substring(0, listString.length() - 1) ;
		System.out.println("returnValue from GetSecondSchemasToMatch: " + returnValue);
		return returnValue;
    }


    
    protected String GetFLM()
    {
    	String query = "SELECT * FROM similaritymeasures";
    	System.out.println("Run query: " + query);
    	
    	ArrayList<String[]> list =  db.runSelectQuery(query, 3);
    	if(list.size() == 0)
    	{
    		return Error + "There is no FLM";
    	}
    	String listString = "";

		for (String[] s : list)
		{
			listString += s[0] + Element_Separator + s[1] + Values_Separator;
		}
		
		String returnValue = listString.substring(0, listString.length() - 1) ;
		System.out.println("returnValue from GetFLM: " + returnValue);
		return returnValue;

    }
      
    protected String GetFLMParameter()
    {
    	String query = "SELECT * FROM similaritymeasuresparameter";
    	System.out.println("Run query: " + query);
    	
    	ArrayList<String[]> list =  db.runSelectQuery(query, 4);
    	if(list.size() == 0)
    	{
    		return Error + "There is no FLM Parameter";
    	}
    	String listString = "";

		for (String[] s : list)
		{
			listString += s[1] + Element_Separator + s[2] + Values_Separator;
		}
		
		String returnValue = listString.substring(0, listString.length() - 1) ;
		System.out.println("returnValue from GetFLMParameter: " + returnValue);
		return returnValue;

    }
    protected String GetPredictors()
    {
    	String query = "SELECT * FROM predictors";
    	System.out.println("Run query: " + query);
    	
    	ArrayList<String[]> list =  db.runSelectQuery(query, 3);
    	
    	if(list.size() == 0)
    	{
    		return Error + "There is no predictors";
    	}
    	String listString = "";

		for (String[] s : list)
		{
			listString += s[0] + Element_Separator + s[1] + Element_Separator + s[2] + Values_Separator;
		}
		
		String returnValue = listString.substring(0, listString.length() - 1) ;
		System.out.println("returnValue from GetSLM: " + returnValue);
		return returnValue;
    	
    }
    
    
    protected String GetSLM()
    {
    	String query = "SELECT * FROM matchers";
    	System.out.println("Run query: " + query);
    	
    	ArrayList<String[]> list =  db.runSelectQuery(query, 4);
    	
    	if(list.size() == 0)
    	{
    		return Error + "There is no SLM";
    	}
    	String listString = "";

		for (String[] s : list)
		{
			listString += s[0] + Element_Separator + s[1] + Values_Separator;
		}
		
		String returnValue = listString.substring(0, listString.length() - 1) ;
		System.out.println("returnValue from GetSLM: " + returnValue);
		return returnValue;
    }
    
    protected String GetSLMParameter()
    {
    	String query = "SELECT * FROM matchersparameter";
    	System.out.println("Run query: " + query);
    	
    	ArrayList<String[]> list =  db.runSelectQuery(query, 5);
    	
    	String listString = "";
    	
    	if(list.size() == 0)
    	{
    		return Error + "There is no SLM Parameter";
    	}
    	
		for (String[] s : list)
		{
			listString += s[1] + Element_Separator + s[2] + Values_Separator;
		}
		
		String returnValue = listString.substring(0, listString.length() - 1) ;
		System.out.println("returnValue from GetSLMParameter: " + returnValue);
		return returnValue;
    }
    
    protected String GetSLMParameterDefaultValue(String matcherId)
    {
    	String query = "SELECT * FROM matchersparameter WHERE MatcherID = " + matcherId;
    	System.out.println("Run query: " + query);
    	
    	ArrayList<String[]> list =  db.runSelectQuery(query, 5);
    	
    	String listString = "";
    	
    	if(list.size() == 0)
    	{
    		return Error + "There is no SLM Parameter";
    	}
    	
		for (String[] s : list)
		{
			listString += s[2] + Element_Separator + s[4] + Values_Separator;
		}
		
		String returnValue = listString.substring(0, listString.length() - 1) ;
		System.out.println("returnValue from GetSLMParameterDefaultValue: " + returnValue);
		return returnValue;
    	
    }
    
    
    protected String RunExperiment(String clientDir , 
    		String datasetId, 
    		String firstSchemaId, 
    		String secondSchemaId, 
    		String flmId , 
    		String slmId,
    		String prediction)
    {
    	String query = "SELECT SPID " +
    	    	"FROM schemapairs "+
    	    	"where DSID=" + datasetId + " AND TargetSchema=" + firstSchemaId + " AND CandidateSchema=" + secondSchemaId ;
    	System.out.println("Run query: " + query);
    	ArrayList<String[]> list =  db.runSelectQuery(query, 1);
    	
    	if(list.size() == 0)
    	{
    		return Error + "There is no schemapairs for : TargetSchema= " + firstSchemaId + " AND CandidateSchema=" + secondSchemaId;
    	}
    	
    	String schemaPairId = list.get(0)[0];
    	
    	String[] flmSplit = flmId.split(Values_Separator);
    	String[] slmSplit = slmId.split(Values_Separator);
    	
    	String propertiesfile = CreatePropertiesFile( clientDir,flmSplit,slmSplit,prediction);
    	if( propertiesfile == null )
    	{
    		return Error + "Cannot create Properties in the server";
    	}
    	
    
    	String runCommand;
    	String[] args;
    	
    	if( ! slmSplit[0].contains("-1") )
    	{
    		args = new String[9];
        	args[0] = "cmd";
        	args[1] = clientDir;
        	args[2] = ( (prediction.contains("-1") ) ? "CsharpExp":"PredictorCsharp");
        	args[3] = "0";
        	args[4] = schemaPairId;
        	args[5] = "0";
        	args[6] = "-f:" + flmSplit[0];
        	args[7] = "-s:" + slmSplit[0];
        	args[8] = "-p:" + propertiesfile; //need to add properties file containing client request
        	
        	runCommand = "Run Command = ";
        	runCommand += " args[0] = " + args[0];
        	runCommand += " args[1] = " + args[1];
        	runCommand += " args[2] = " + args[2];
        	runCommand += " args[3] = " + args[3];
        	runCommand += " args[4] = " + args[4];
        	runCommand += " args[5] = " + args[5];
        	runCommand += " args[6] = " + args[6];
    		runCommand += " args[7] = " + args[7];
    		runCommand += " args[8] = " + args[8];
    	}
    	else
    	{
    		args = new String[8];
        	args[0] = "cmd";
        	args[1] = clientDir;
        	args[2] = ( (prediction.contains("-1") ) ? "CsharpExp":"PredictorCsharp");
        	args[3] = "0";
        	args[4] = schemaPairId;
        	args[5] = "0";
        	args[6] = "-f:" + flmSplit[0];
        	args[7] = "-p:" + propertiesfile; //need to add properties file containing client request
    		
        	
        	runCommand = "Run Command = ";
        	runCommand += " args[0] = " + args[0];
        	runCommand += " args[1] = " + args[1];
        	runCommand += " args[2] = " + args[2];
        	runCommand += " args[3] = " + args[3];
        	runCommand += " args[4] = " + args[4];
        	runCommand += " args[5] = " + args[5];
        	runCommand += " args[6] = " + args[6];
    		runCommand += " args[7] = " + args[7];
    	}
    	
    	
    	System.out.println(runCommand);
    	
    	try
    	{
    		OBExperimentRunner.main(args);
    	}
    	catch(Exception e)
    	{
    		return Error + "OBExperimentRunner failed , " + e.getMessage();
    	}
    	
    
    	String listString = "";
    	File clientDirectory = new File(clientDir);
    	String files[] = clientDirectory.list();
  	    for (String file : files) 
  	    {
  	    	if(file.contains("propertiesfile.properties"))
  	    	{
  	    		continue;
  	    	}
  		  File myFile = new File( clientDir + "\\" + file );
  		  file = file.replace(Values_Separator, "_");
  		  File renameFile = new File( clientDir + "\\" + file );
  		  myFile.renameTo(renameFile);
  		  
  		  myFile = new File( clientDir + "\\" + file );
  		  int size = (int) myFile.length();
  		  System.out.println("File Name : " + file + " File length " + myFile.length() + " Send len = " + size);
  		  listString +=  file +  Element_Separator + size + Values_Separator;
  	    }
  	    String returnValue = listString.substring(0, listString.length() - 1) ;
  	    System.out.println("returnValue from RunExperiment : " + returnValue);
    	return returnValue;
    }
    
    private String CreatePropertiesFile(String clientDir,String[] flmProperties,String[] slmProperties,String prediction)
    {
    	try
    	{
    		String propertiesfile = clientDir + "\\propertiesfile.properties";
	    	File file = new File( propertiesfile);
	    	
	    	if (!file.exists()) {
				file.createNewFile();
			}
	    	
	    	FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
				
			for (int i = 1; i < flmProperties.length; i++) 
	    	{
				String prop = flmProperties[i];
				String[] splitProp = prop.split(Element_Separator);
				String propName = splitProp[0];
				String propValue= splitProp[1];
				String content =propName + "=" + propValue +"\n";
				bw.write(content);
			}
			
	    	for (int i = 1; i < slmProperties.length; i++) 
	    	{
				String prop = slmProperties[i];
				String[] splitProp = prop.split(Element_Separator);
				String propName = splitProp[0];
				String propValue= splitProp[1];
				String content =propName + "=" + propValue +"\n";
				bw.write(content);
			}
	    	
	    	if(! prediction.contains("-1") )
	    	{
	    		String content = "predictor=" + prediction +"\n";
				bw.write(content);
	    	}
	    	
	    	bw.close();
	    	fw.close();
	    	
	    	return propertiesfile;
    	}
    	catch(Exception e)
    	{
    		return null;
    	}
    }
    
    
}

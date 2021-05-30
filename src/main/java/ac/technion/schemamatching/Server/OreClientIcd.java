package ac.technion.schemamatching.Server;

import ac.technion.schemamatching.DBInterface.DBInterface;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import org.apache.jena.base.Sys;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Properties;

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
			} catch (IOException e) {
				e.printStackTrace();
			}
		   System.out.println(pMap.get("dbmstype") + " " + pMap.get("host") + " " + pMap.get("dbname") + " " + pMap.get("username") + " " + pMap.get("pwd"));
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
			File myFile = new File(client.ClientFolder + "\\" + fileName);
	    	int length = (int) myFile.length();
	        byte[] mybytearray = new byte[length];
	       // BufferedOutputStream outToClient = new BufferedOutputStream(client.getSocket().getOutputStream());
	        OutputStream outToClient = client.getSocket().getOutputStream();
	        FileInputStream fis;
	        fis = new FileInputStream(myFile);
	        BufferedInputStream bis = new BufferedInputStream(fis);
	        if (bis.read(mybytearray, 0, length)==-1)
	        	System.err.println("Failed to read file");
	        byte[] toSend = new byte[length + 4]; 
	        
	        byte[] sizeArray = ByteBuffer.allocate(4).putInt(length).array();
	        
	        System.out.println("myFile.length(): " + length);//PRINT IT OUT TO THE SCREEN

			System.arraycopy(sizeArray, 0, toSend, 0, 4);
			if (length >= 0) System.arraycopy(mybytearray, 0, toSend, 4, length);
	        
	        outToClient.write(toSend, 0, length + 4);
	        outToClient.flush();
	        fis.close();
	        bis.close();
	        if (!myFile.delete())
	        	System.err.printf("Temporary directory %s was not deleted \n",myFile);
	        return "True";
    	} catch (IOException ex)
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
    	
    	StringBuilder listString = new StringBuilder();
		for (String[] s : list)
		{
			listString.append(s[0]).append(Element_Separator).append(s[1]).append(Values_Separator);
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
    	
    	StringBuilder cond = new StringBuilder();
    	for (String[] value : list)
		{
    		cond.append(" DSID=").append(value[0]).append(" OR");
		}
    	cond = new StringBuilder(cond.substring(0, cond.length() - 3));
    	
    	query = "SELECT * FROM datasets where " + cond;
    	System.out.println("Run query: " + query);
    	
    	list =  db.runSelectQuery("SELECT * FROM datasets where " + cond, 6);
    	StringBuilder listString = new StringBuilder();
		for (String[] s : list)
		{
			listString.append(s[0]).append(Element_Separator).append(s[1]).append(Values_Separator);
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
    	
    	StringBuilder cond = new StringBuilder();
    	for (String[] value : list)
		{
    		cond.append(" SchemaID=").append(value[0]).append(" OR");
    		
		}
    	cond = new StringBuilder(cond.substring(0, cond.length() - 3));
    	
    	query = "SELECT SchemaID,SchemaName,DSID "
    			+ "FROM schemata "
    			+ "where "+ cond + " AND DSID=" + datasetId;
    	System.out.println("Run query: " + query);
    	list =  db.runSelectQuery(query, 3);
    	
    	StringBuilder listString = new StringBuilder();

		for (String[] s : list)
		{
			listString.append(s[0]).append(Element_Separator).append(s[1]).append(Values_Separator);
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
    	
    	StringBuilder listString = new StringBuilder();

		for (String[] s : list)
		{
			listString.append(s[0]).append(Element_Separator).append(s[1]).append(Values_Separator);
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
    	
    	StringBuilder cond = new StringBuilder();
    	for (String[] secondSchemaId : SecondSchemaIdlist)
		{
    		cond.append(" SchemaID= ").append(secondSchemaId[0]).append(" OR");
		}
    	cond = new StringBuilder(cond.substring(0, cond.length() - 3));
    	
    	query = "SELECT SchemaID,SchemaName,DSID "
    			+ "FROM schemata "
    			+ "where DSID=" + datasetId + " AND " + cond;
    	System.out.println("Run query: " + query);
    	ArrayList<String[]> list = db.runSelectQuery(query, 3);
    	
    	if(list.size() == 0)
    	{
    		return Error + "There is no Schemas in dataset " + datasetId;
    	}
    	
    	StringBuilder listString = new StringBuilder();
		for (String[] s : list)
		{
		listString.append(s[0]).append(Element_Separator).append(s[1]).append(Values_Separator);
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
    	StringBuilder listString = new StringBuilder();

		for (String[] s : list)
		{
			listString.append(s[0]).append(Element_Separator).append(s[1]).append(Values_Separator);
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
    	StringBuilder listString = new StringBuilder();

		for (String[] s : list)
		{
			listString.append(s[1]).append(Element_Separator).append(s[2]).append(Values_Separator);
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
    	StringBuilder listString = new StringBuilder();

		for (String[] s : list)
		{
			listString.append(s[0]).append(Element_Separator).append(s[1]).append(Element_Separator).append(s[2]).append(Values_Separator);
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
    	StringBuilder listString = new StringBuilder();

		for (String[] s : list)
		{
			listString.append(s[0]).append(Element_Separator).append(s[1]).append(Values_Separator);
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
    	
    	StringBuilder listString = new StringBuilder();
    	
    	if(list.size() == 0)
    	{
    		return Error + "There is no SLM Parameter";
    	}
    	
		for (String[] s : list)
		{
			listString.append(s[1]).append(Element_Separator).append(s[2]).append(Values_Separator);
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
    	
    	StringBuilder listString = new StringBuilder();
    	
    	if(list.size() == 0)
    	{
    		return Error + "There is no SLM Parameter";
    	}
    	
		for (String[] s : list)
		{
			listString.append(s[2]).append(Element_Separator).append(s[4]).append(Values_Separator);
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
    	
    
    	StringBuilder listString = new StringBuilder();
    	File clientDirectory = new File(clientDir);
    	String[] files = clientDirectory.list();
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
  		  listString.append(file).append(Element_Separator).append(size).append(Values_Separator);
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

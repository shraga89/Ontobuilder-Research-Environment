/**
 * 
 */
package Application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import schemamatchings.meta.match.MatchedAttributePair;
import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.ontobuilder.OntoBuilderWrapperException;
import schemamatchings.util.BestMappingsWrapper;
import schemamatchings.util.MappingAlgorithms;
import schemamatchings.util.SchemaMatchingsUtilities;
import schemamatchings.util.SchemaTranslator;
import smb_service.*;
import smb_service.Field.FieldType;

import com.infomata.data.DataFile;
import com.infomata.data.DataRow;
import com.infomata.data.TabFormat;
import com.modica.ontology.Ontology;
import com.modica.ontology.Term;
//import com.modica.ontology.algorithm.boosting.Dataset;
import com.modica.ontology.match.*;
/**
 * @author tomer_s
 * Input: K - number of experiments to run (an integer)
 */
public class OB_SMB_Interface {

	/**
	 * @param args
	 */
	static double TIMEOUT = 20 * 1000; 
	public static void main(String[] args) throws NumberFormatException, Exception {
		
		
		
		// TODO 1 Load X experiments into an experiment list
	    
	    SMB smb = new SMB();
		ArrayList<SchemasExperiment> ds = UploadKExperiments(smb,Integer.parseInt(args[0]));
		SchemasExperiment schemasExp = new SchemasExperiment();
  		System.out.println("DS size is: " + ds.size());
	    
	    //writeBasicConfigurations(schemasExp.getSubDir().getAbsolutePath(),schemasExp.getEID());
	    //if (flag) System.out.println("Exists");
	    //else System.out.println("Don't Exists");
  		
	   
	    Ontology target;
	    Ontology candidate;
	    OntoBuilderWrapper obw = new OntoBuilderWrapper();
	    SchemaTranslator boostingBestMapping = null;
	    SchemaTranslator exactMapping;
	    double boostPrecision = 0.0;
	    double boostRecall = 0.0;
	    String sOutput;
      
	    String[] availableMatchers =  MatchingAlgorithms.ALL_ALGORITHM_NAMES;
        MatchInformation firstLineMI[]= new MatchInformation[availableMatchers.length];
        SchemaTranslator firstLineST[] = new SchemaTranslator[availableMatchers.length];
        MatchMatrix firstLineMM[]= new MatchMatrix[availableMatchers.length];
	    String[] available2ndLMatchers = MappingAlgorithms.ALL_ALGORITHM_NAMES;
        SchemaTranslator secondLineST[] = new SchemaTranslator[available2ndLMatchers.length*availableMatchers.length];
	    
		// TODO 2 For each experiment in the list:
	    for (int i = 0; i < ds.size(); ++i) {  //size
			// TODO 2.1 load from file into OB objects
	        schemasExp = ds.get(i);
	        target = schemasExp.getTargetOntology();
	        candidate = schemasExp.getCandidateOntology();
	        exactMapping = schemasExp.getExactMapping();
	        
	        
			// TODO 2.2 1st line match using all available matchers in OB // missing similarity flooding -> adjustment were made lines: 74-77;
	        try {
	        
	        for (int m=0;m<availableMatchers.length;m++)
	        {				
					firstLineMI[m] = obw.matchOntologies(candidate, target,availableMatchers[m]);
					// boolean was set to null as default choice
					firstLineST[m] = new SchemaTranslator(firstLineMI[m]);
					firstLineST[m].importIdsFromMatchInfo(firstLineMI[m],true);
	        }
	        // TODO 2.3 2nd line match using all available matchers in OB with original matrix
	       
	        int mp2=0;
	        for (int m=0;m<availableMatchers.length;m++)
	        {
	        	BestMappingsWrapper.matchMatrix = firstLineMI[m].getMatrix();
	        	firstLineMM[m] = firstLineMI[m].getMatrix();
	        	for (int mp=0;mp<available2ndLMatchers.length;mp++) // Note: I changed from: for (int mp=0;mp<available2ndLMatchers.length*availableMatchers.length;mp++)
		        {   
		        	if (BestMappingsWrapper.GetBestMapping(available2ndLMatchers[mp])!=null)
		        		{
		        		secondLineST[mp2] = BestMappingsWrapper.GetBestMapping(available2ndLMatchers[mp]);
				        secondLineST[mp2].importIdsFromMatchInfo(firstLineMI[m],true);
				        secondLineST[mp2].setAlgorithm(m,mp,availableMatchers[m],available2ndLMatchers[mp]);
				        int conf = secondLineST[mp2].getConfigurationNum();
				        String st = secondLineST[mp2].getConfiguration();
				        //BasicConfigurationTable[] values;
				        mp2++;
		        		}
		        	else {continue;};
		        	//if (System.currentTimeMillis()-time > TIMEOUT) break;		        	
		        }
		        //if (System.currentTimeMillis()-time > TIMEOUT) break;
	        }
	        System.out.println (mp2 + " combinations of matchers");
	        } catch (OntoBuilderWrapperException e) {
				e.printStackTrace();
			}
			// TODO 2.4 Output schema pair, term list, list of matchers and matches to URL     	
	      	try 
	      		{
	      		//writeItems(firstLineMM,schemasExp.getCandidateId() , smb, true);
	      		//writeItems(firstLineMM,schemasExp.getTargetId() , smb, false);
	      		//writeMatchingResult(secondLineST,target.getName(), candidate.getName());   		
    			//writeSchema(target,candidate);
	      		}
    		catch (Exception e)
    			  {
    				System.err.print("Enhanced Similarity Matrix File Creation Failed");  
    				e.printStackTrace();
    			  }
			// TODO 2.5 run SMB_service with args: E URL

    		//SMB.SMBRun (smb,"E","C:\\Documents and Settings\\Administrator\\Desktop\\project\\frames",null,null,null,null);

			// TODO 2.6 load enhanced matching result into OB object
	        	//Look at LoadWorkingSet from SMB.java
			// TODO 2.7 2nd line match using all available matchers in OB with enhanced matrix
	        
			// TODO 2.8 Calculate Precision and recall for each matrix (regular and enhanced) and output to txt file) 
	        boostPrecision = SchemaMatchingsUtilities.calculatePrecision(exactMapping, boostingBestMapping);
	        boostRecall = SchemaMatchingsUtilities.calculateRecall(exactMapping, boostingBestMapping);
	        
	        sOutput = candidate.getName() + "\t";
	        sOutput += target.getName() + "\t";
	        sOutput += boostPrecision + "\t";
	        sOutput += boostRecall + "\t";
	       
	        try{
	          //outPutFile.write(sOutput);
	        }catch(Exception e){
	          System.out.println("1. " + e.toString());
	        } 

	      //}
	    //}
	    //catch (Exception e) {
	      //System.out.println("2. " + e.toString());
	    //}

	    try{
	    //  outPutFile.flush();
	    }catch(Exception e){
	      System.out.println("3. " + e.toString());
	    }  
	  }

	}

	private static ArrayList<SchemasExperiment> UploadKExperiments(SMB smb, int K) throws Exception {
		
		ArrayList<SchemasExperiment> ds = new ArrayList<SchemasExperiment>();
		String sql  = "SELECT * FROM schemapairs"; 
		ArrayList<String[]> schemapairs =  smb.getDB().runSelectQuery(sql, 5);
		sql  = "SELECT EID FROM experiments"; 
		ArrayList<String[]> experiments =  smb.getDB().runSelectQuery(sql, 1);
		Iterator<String[]> it = experiments.iterator();
		Random randomGenerator = new Random();
		HashMap<Integer,Integer> hashMap = new HashMap<Integer,Integer>();
		int randomInt = 0;
		
		//check the number of available experiments is larger then k
		if (K>schemapairs.size()) throw new Exception("K is too large"); 
		
		//at each iteration create a new experiments from scemapair table
		while (ds.size()<K)
		{	
		//Randomize a new number 
		boolean newNumber = false;
		while (newNumber==false)	{
			//draw a new random number
			randomInt = randomGenerator.nextInt(schemapairs.size());
			//check that this number wasn't generated yet
			if ( hashMap.get(randomInt) != null ) {continue;}
			else {
			newNumber = true;
			hashMap.put(randomInt, 1);
			}		
		}
		
		String url = parseFolderPathFromSchemapairs((schemapairs.get(randomInt))[4]);
		String SPID = schemapairs.get(randomInt)[0];
		
		File f = new File(url);
		SchemasExperiment schemasExp = new SchemasExperiment(f,Long.parseLong(SPID));
		
		//Document into DB if first time to open schemapair
		writeExperimentsToDB (smb,schemasExp);
		ds.add(schemasExp);
		}		
		return ds;
	}

	//this function returns true if the expeirment have already been created at the DB 
	private static boolean writeExperimentsToDB(SMB smb, SchemasExperiment schemaexp) {
		
		String sql  = "SELECT EID FROM experiments"; 
		ArrayList<String[]> EIDs =  smb.getDB().runSelectQuery(sql, 1);
		Iterator<String[]> it = EIDs.iterator();

		//if experiment was created before (meaning the this folder have already been traversed return true
		while (it.hasNext()){
			if (Long.valueOf(it.next()[0]) == schemaexp.getEID()){
				return true;
				}
		}
		
		// if experiment doesn't exist document experiment into DB 
		HashMap values = new HashMap();	
		Object obj = new Object();
		
		Field f = new Field ("EID", FieldType.LONG );
		values.put(f, (Object)schemaexp.getEID() );
		
		f = new Field ("RunDate", FieldType.DATE );
		values.put(f, (Object)schemaexp.getDate());
		
		f = new Field ("ExperimentDesc", FieldType.STRING);
		String str = ("SMB(E," + schemaexp.getSubDir() + ",s)");
		values.put(f, (Object)str);
		
		smb.getDB().insertSingleRow(values, "experiments");	
		return false;
		}
		

	private static String parseFolderPathFromExperiment(String url) {
		String str[] = url.split(",");
		String str2[] = str[1].split("/");
		return str2[0];
	}
	
	private static String parseFolderPathFromSchemapairs(String url) {
		String str[] = url.split("/");
		return str[0];
	}


	private static void writeSchema(Ontology target, Ontology candidate) throws IOException {
		DataFile write = DataFile.createWriter("8859_1", false);
		write.setDataFormat(new TabFormat());
		File outputPath = new File("c:\\smb");
		File outputSMFile = new File(outputPath,"Schema.tab");
		write.open(outputSMFile);
		DataRow row = write.next();
		printSchema(row,target);
		row = write.next();
		printSchema(row,candidate);
		row = write.next();
		write.close();
	}

	private static void printSchema(DataRow row, Ontology target) {
		row.add(target.getModel().getId());// Schema ID
		row.add(target.getName() + "  " + target.getModel().getName()+ "  " + target.getModel().getTitle());// Schema Name
		row.add("x");//Source: 1=Web From, 2=Relational DB, 3=Web Service Description
		row.add("x");//Language: int -> From codepage specification
		row.add("x");//Real: Boolean-> True if generated from a production system, False if generated via synthetic manipulation
		row.add("x");//Original modeling language: 1: XSD, 2: OWL, 3: RDF, 4: ProprietyDTD
		row.add(target.getHeight());//Max Height of the class hierarchy (Numerical Discrete)
		row.add("x");//# of subclass relationships
		row.add("x");//#of association relationships
		row.add(target.getComponentCount()+ " " + target.getComponentCount() + "  " + target.getModel().getTermsCount() + "  " + target.getTermsCount());//# of attributes in Schema
		row.add(target.getModel().getClassesCount());//# of classes
		row.add("x");//# of visible items
		row.add("x");//# of instances
		
	}

	private static void writeItems(MatchMatrix[] firstLineMM, String schemaId, SMB smb, boolean isTarget) throws IOException {
		DataFile write = DataFile.createWriter("8859_1", false);
		write.setDataFormat(new TabFormat());
		File outputPath = new File("c:\\smb");
		File outputSMFile = new File(outputPath,"item.tab");
		write.open(outputSMFile);
		DataRow row = write.next();
		Term s;
		boolean existsInDB = false;
		int i=0;
		
		//check if target terms have been entered into the DB
		DBInterface db = smb.getDB();
		ArrayList<String[]> schemaIds = db.runSelectQuery("SELECT SchemaId FROM terms", 1);
		Iterator it = schemaIds.iterator();
		while (it.hasNext()){
			if ((schemaIds.get(i))[0].contains(schemaId)  )
			{
				existsInDB = true;
				break;
			}
			i++;
		}
		if (!existsInDB)writeItemsToDB();
		
		i=0;
		ArrayList <Term> targeTerms;
		if (isTarget)targeTerms = firstLineMM[i].getTargetTerms();
		else targeTerms = firstLineMM[i].getCandidateTerms();
		for (i=0;i<targeTerms.size();i++)
		{
			row.add (schemaId); // schema id
			row.add (targeTerms.get(i).getId()); // trem id
			row.add (targeTerms.get(i).getName()); // trem name
			row.add (targeTerms.get(i).getDomain().getName()); // term type
			int domainNum = getDomainNumber(targeTerms.get(i).getDomain().getName());
			row.add (domainNum); // Categorical Discrete
			row.add (0);
			row.add (0);
			row=write.next();
		}
		row.add ("other way");
		// TODO double check correctness of all fields, convert type to int, notice that date is test (what to do);
		String sql = "SELECT * FROM terms Where SchemaId = " +  schemaId;
		ArrayList<String[]> Terms = db.runSelectQuery( sql , 4);
		it = Terms.iterator();
		i=0;
		while (it.hasNext()){
			row.add ((schemaIds.get(i))[0]); // ontology id
			row.add ((schemaIds.get(i))[1]); // term id
			row.add ((schemaIds.get(i))[2]); // trem name
			row.add ((schemaIds.get(i))[3]); // Categorical Discrete
			row=write.next();
			i++;
		}
		write.close();
	}

	private static void writeItemsToDB() {
		// TODO Auto-generated method stub
		
	}

	private static int getDomainNumber(String domain) {
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

	private static void writeMatchingResult(SchemaTranslator[] secondLineST, String target, String candidate) throws IOException {
		DataFile write = DataFile.createWriter("8859_1", false);
		write.setDataFormat(new TabFormat());
		File outputPath = new File("c:\\smb");
		File outputSMFile = new File(outputPath,"MatchingResult.tab");
		write.open(outputSMFile);
		DataRow row = write.next();
		int i=0;
		while (secondLineST[i]!=null)
		{
		MatchedAttributePair[] attibuteArray = secondLineST[i].getMatchedPairs();
		for (int j=0; j<attibuteArray.length; j++)
		{
		row.add(secondLineST[i].getConfigurationNum()); 
		row.add(secondLineST[i].getConfiguration());
		row.add(target);
		row.add(attibuteArray[j].id1); 
		row.add(candidate);
		row.add(attibuteArray[j].id2);
		row.add(attibuteArray[j].getMatchedPairWeight());  //check with tomer
		row=write.next();
		}
		i++;
		}
		write.close();
	}

	private static void writeBasicConfigurations(String url, long EID) throws IOException {
		DataFile write = DataFile.createWriter("8859_1", false);
		write.setDataFormat(new TabFormat());
		File outputPath = new File("c:\\smb");
		File outputSMFile = new File(outputPath,"BasicConfigurations.tab");
		write.open(outputSMFile);
		DataRow row = write.next();
		row.add(EID); //Configuration ID
		row.add("SMB(E," + url + ",s)"); //Configuration name
		write.close();
	}

	/*
	 * Example of a method to create a tab delimited file
	 */
	private static void outputWeightsToFile(String strOutputPath, SMB smb,
			HashMap<Long, Double> weightedEnsemble) {
		try 
		{
			//Create output file
			DataFile write = DataFile.createWriter("8859_1", false);
			write.setDataFormat(new TabFormat());
			File outputPath = new File(strOutputPath);
			File outputWCFile = new File(outputPath,"weightedConfigurations.tab");
			write.open(outputWCFile);
			
			//Write result
			Set<Long> s = weightedEnsemble.keySet();
			Iterator<Long> itr = s.iterator();
			while (itr.hasNext())
			{
				DataRow row = write.next();
				Long key = (Long) itr.next();
				//row.add(smb.ws.candidateSchema.get("ID")); //Candidate schema ID
				//row.add(smb.ws.targetSchema.get("ID")); //Target schema ID
				row.add(key.toString()); //Configuration ID
				row.add(weightedEnsemble.get(key).toString()); //Weight
			}
			write.close();
		}
		catch (Exception e)
		{
			System.err.print("Weighted Configuration File Creation Failed \n");  
			e.printStackTrace();
		 }
	}
}





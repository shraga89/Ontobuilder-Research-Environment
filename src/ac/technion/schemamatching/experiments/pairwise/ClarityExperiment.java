/**
 * 
 */
package ac.technion.schemamatching.experiments.pairwise;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import com.infomata.data.DataFile;
import com.infomata.data.DataRow;
import com.infomata.data.TabFormat;

import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

/**
 * @author Tomer Sagi
 * Runs SMB Service in enhance mode on a set of schema pairs. Uses different 2nd line matchers
 * on the result and compares to a base line of applying the 2nd line matchers to the
 * similarity matrices without enhancement. 
 */
public class ClarityExperiment implements PairWiseExperiment {

	private OBExperimentRunner myExpRunner;
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) 
	{
		/*Ontology target;
	    Ontology candidate;
	    String[] availableMatchers =  MatchingAlgorithms.ALL_ALGORITHM_NAMES;
        MatchInformation firstLineMI[]= new MatchInformation[availableMatchers.length];
	    String[] available2ndLMatchers = MappingAlgorithms.ALL_ALGORITHM_NAMES;
        SchemaTranslator secondLineST[] = new SchemaTranslator[available2ndLMatchers.length*availableMatchers.length];
	    int sysCode = 1; //Ontobuilder sysCode
	    File outputPath = null; //TODO load from properties
	    // Make sure all matchers and similarity measures are documented in the DB with the right matcher ID
	    ArrayList<String[]> SMIDs = getSimilarityMeasures(availableMatchers,sysCode );
	    // -------- recreated the function at ExperimentDocumenter, still depricated because I didn't understand your
	    // not about the enum or hashtable
	    
	    
	    
    
        
        // 2 For each experiment in the list:
	    for (ExperimentSchemaPair schemasExp : myExpRunner.getDS()) 
	    {
			// 2.1 load from file into OB objects
	        target = schemasExp.getTargetOntology();
	        candidate = schemasExp.getCandidateOntology();
	        long spid = schemasExp.getSPID();
	        //2.2 1st line match using all available matchers in OB // missing similarity flooding -> adjustment were made lines: 74-77;
	        int counter = 0;
	        for (int m=0;m<availableMatchers.length;m++)
	        {				
				System.out.println ("Starting " + counter);
				firstLineMI[m] = schemasExp.getSimilarityMatrix(myExpRunner.reversedmeasures.get(availableMatchers[m])); 
				BestMappingsWrapper.matchMatrix = firstLineMI[m].getMatrix();	
				// 2.3 2nd line match using all available matchers in OB with original matrix and document in DB   	
				for (int mp=0;mp<available2ndLMatchers.length;mp++)
				{   
					//get a matching from db, if 2nd line match not in db, perform matching
					ArrayList<String[]> mapping = myExpRunner.getDoc().getMappings(available2ndLMatchers[mp],availableMatchers[m],spid,sysCode);
						if (mapping.isEmpty())
						{
							
							mapping = new ArrayList<String[]>();
							System.out.println ("doing " + counter + "." + available2ndLMatchers[mp] );
							secondLineST[mp*(m+1)] = BestMappingsWrapper.GetBestMapping(available2ndLMatchers[mp]);
							if (secondLineST[mp*(m+1)]==null)
							{
								System.err.println("empty match spid:" + spid + " smid: " +  availableMatchers[m] + "matcher:" + available2ndLMatchers[mp]);
								continue;
							}
							secondLineST[mp*(m+1)].importIdsFromMatchInfo(firstLineMI[m],true);
							
							for (MatchedAttributePair match : secondLineST[mp*(m+1)].getMatchedPairs())
							{
								String[] e = {Long.toString(spid),"","",Long.toString(myExpRunner.getDoc().getMID(available2ndLMatchers[mp],sysCode)),Long.toString(myExpRunner.getDoc().getSMID(availableMatchers[m],sysCode))};
								e[1] = Long.toString(match.id1);
								e[2] = Long.toString(match.id2);
								mapping.add(e);
							}
							
						}
						myExpRunner.getDoc().upload2ndLineMatchToDB(availableMatchers[m], available2ndLMatchers[mp],false, sysCode, spid, mapping);
					}
					System.out.println ("Finished 2nd line matching " + counter);
					counter++;
	        }//end for of 1st line matcher
	        //TODO move this whole section to the ClarityExperiment
			//  2.4 Output schema pair, term list, list of matchers and matches to URL    
        	try {
				outputArrayListofStringArrays(outputPath,SMIDs,"BasicConfigurations.tab");
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
        	//order of schemas: Candidate and then target
        	ArrayList<String[]> schemaRes = new ArrayList<String[]>();


        	schemaRes.addAll(myExpRunner.db.runSelectQuery("SELECT `SchemaID`, `SchemaName`, `source`,`language`,`Real`,`Language`,`Max_Height_of_the_class_hierarchy`,`Number_of_association_relationships`, `Number_of_attributes_in_Schema`,  `Number_of_classes`,  `Number_of_visible_items`,  `Number_of_instances` FROM schemata,schemapairs,datasets WHERE schemapairs.DSID = datasets.DSID AND SchemaID = schemapairs.TargetSchema AND schemapairs.SPID = " + spid + ";", 12));
        	try {
        	outputArrayListofStringArrays(outputPath,schemaRes,"Schema.tab");
        	outputArrayListofStringArrays(outputPath,myExpRunner.db.runSelectQuery("SELECT `SchemaID`, `Tid`, `DomainNumber`, `TName`, `Known Composite`, `Known Partial`" +
        									"FROM `terms` WHERE SchemaID = " + schemasExp.getCandidateID() + " OR SchemaID = " + schemasExp.getTargetID() + ";", 6),"Item.tab");
        	
				outputArrayListofStringArrays(outputPath,myExpRunner.db.runSelectQuery("SELECT `similaritymatrices`.`SMID` , `similaritymatrices`.`CandidateSchemaID` , `similaritymatrices`.`CandidateTermID` , `similaritymatrices`.`TargetSchemaID` , `similaritymatrices`.`TargetTermID` , `similaritymatrices`.`confidence`" +
											" FROM `experimentschemapairs` INNER JOIN `schemapairs` ON (`experimentschemapairs`.`SPID` = `schemapairs`.`SPID`)" +
											" INNER JOIN `similaritymatrices` ON (`schemapairs`.`TargetSchema` = `similaritymatrices`.`TargetSchemaID`) AND (`schemapairs`.`CandidateSchema` = `similaritymatrices`.`CandidateSchemaID`)" +
											" INNER JOIN `similaritymeasures` ON (`similaritymeasures`.`SMID` = `similaritymatrices`.`SMID`)" +
											" WHERE (`similaritymeasures`.`System` = " + sysCode + ") AND (EID = " + myExpRunner.getDoc().getEid() + ") AND `schemapairs`.`SPID` = " + schemasExp.getSPID() + ";", 6),"MatchingResult.tab");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
      		   		

		//2.5 run SMB_service in Enhance mode
		SMB smb = new SMB();
		smb.enhance(outputPath.getPath(), outputPath.getPath(), 2.0, .2, 2.0);

		//  2.6 load enhanced matching result into OB object
			//load enhanced similarity matrix to ArrayList
			ArrayList<String[]> enhancedMatrices = readFile(new File(outputPath,"EnhancedSimilarityMatrix.tab"));
			//Split array list by SMIDs
			HashMap<Long,ArrayList<String[]>> similarityMeasureMatrices = new HashMap<Long,ArrayList<String[]>>();
			for (String[] sm : SMIDs) similarityMeasureMatrices.put(Long.parseLong(sm[0]), new ArrayList<String[]>());
			for (String[] matrixRow : enhancedMatrices) similarityMeasureMatrices.get(Long.parseLong(matrixRow[0])).add(matrixRow);
			//create MI object for each arraylist using createMIfromArrayList
			HashMap<Long,MatchInformation> EnhancedMI = new HashMap<Long,MatchInformation>();
			for ( long sm : similarityMeasureMatrices.keySet()) EnhancedMI.put(sm, DBInterface.createMIfromArrayList(candidate, target,similarityMeasureMatrices.get(sm) ) );
		// 2.7 2nd line match using all available matchers in OB with enhanced matrix
			int eCounter = 0;
			for (String[] smRow : SMIDs)
			{
				
				long sm = Long.parseLong(smRow[0]);
				String SMName = smRow[1];
				// Update enhanced matrix and mapping results to db
				// Since these are enhanced matrices use true
				//TODO Replace this with documentation of an experiment schema pair
				try {
					myExpRunner.getDoc().loadSMtoDB(EnhancedMI.get(sm),schemasExp,true, (int)sm);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//document ClarityScore.tab
    			ArrayList<String[]> clarityRes = readFile(new File(outputPath,"ClarityScore.tab"));
    			myExpRunner.getDoc().loadClarityToDB(clarityRes,schemasExp,(int)sm);
				for(String secondLineM : available2ndLMatchers)
				{
					// Match
					ArrayList<String[]> mapping = new ArrayList<String[]>();
					System.out.println ("doing enhanced" + eCounter + "." + secondLineM );
					SchemaTranslator st = BestMappingsWrapper.GetBestMapping(secondLineM);
					st.importIdsFromMatchInfo(EnhancedMI.get(sm),true);
					for (MatchedAttributePair match : st.getMatchedPairs())
					{
						String[] e = {Long.toString(spid),"","",Long.toString(myExpRunner.getDoc().getMID(secondLineM,sysCode)),smRow[0]};
						e[1] = Long.toString(match.id1);
						e[2] = Long.toString(match.id2);
						mapping.add(e);
					}
					
					// using false to separate the non enhanced from the enhanced result 
					myExpRunner.getDoc().upload2ndLineMatchToDB(SMName, secondLineM,true, sysCode, spid, mapping);
				}
				eCounter++;
			}
    			 
    	  
	  }// end for loop of experiment
*/
		return null;
	}

	public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) 
	{
		// TODO Auto-generated method stub
		myExpRunner = oer;
		return false;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Outputs a tab delimited file of the supplied name to the supplied path
	 * From an ArrayList of string arrays each arrayList item representing a row 
	 * and each String array item representing a column.  
	 * @param outputPath
	 * @param res
	 * @param fName
	 * @throws IOException
	 */
	private static void outputArrayListofStringArrays(File outputPath,
			ArrayList<String[]> res, String fName) throws IOException {
		DataFile write = DataFile.createWriter("8859_1", false);
		write.setDataFormat(new TabFormat());			
		File outputCOFile = new File(outputPath,fName );
		write.open(outputCOFile);
		for (int i=0;i<res.size();i++)
		{
			DataRow row = write.next();
			String rRow[] = res.get(i);
			for (int j=0;j<rRow.length;j++) row.add(rRow[j]);
		}
		write.close();
	}
	

	/**
	 * readFile supplied into ArrayList of string arrays
	 * @param f File to read
	 * @return Array list of string arrays
	 */
	private static ArrayList<String[]> readFile(File f)
	{
		BufferedReader readbuffer;
		String strRead;
		String splitArray[];
		ArrayList<String[]> res = new ArrayList<String[]>();
		try {
			readbuffer = new BufferedReader(new FileReader(f.getPath()));
			strRead=readbuffer.readLine();
			while (strRead != null){
				splitArray = strRead.split("\t");
	    		res.add(splitArray);
	    		strRead=readbuffer.readLine();
				}
		
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	

	/**
	 * @param availableMatchers
	 * @param sysCode
	 * @return
	 */
	private ArrayList<String[]> getSimilarityMeasures(
			String[] availableMatchers, int sysCode) {
		/*ArrayList<String[]> res = new ArrayList<String[]>();
		for (Integer sm : myExpRunner.measures.keySet())
			res.add(new String[] {sm.toString(),myExpRunner.measures.get(sm)});
		return res;*/
		return null;
	}

	public ArrayList<Statistic> summaryStatistics() {
		//unused
		return null;
	}
}

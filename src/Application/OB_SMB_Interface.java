/**
 * 
 */
package Application;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.ontobuilder.OntoBuilderWrapperException;
import schemamatchings.util.BestMappingsWrapper;
import schemamatchings.util.MappingAlgorithms;
import schemamatchings.util.SchemaMatchingsUtilities;
import schemamatchings.util.SchemaTranslator;
import smb_service.SMB;

import com.infomata.data.DataFile;
import com.infomata.data.DataRow;
import com.infomata.data.TabFormat;
import com.modica.ontology.Ontology;
//import com.modica.ontology.algorithm.boosting.Dataset;
import com.modica.ontology.match.MatchInformation;


/**
 * @author tomer_s
 *
 */
public class OB_SMB_Interface {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		
		// TODO 1 Load X experiments into an experiment list
		SchemasExperiment schemasExp = new SchemasExperiment();
	    ArrayList<SchemasExperiment> ds = new ArrayList<SchemasExperiment>();
	    ds.add(schemasExp);
	    
	    int size = ds.size();
	    Ontology target;
	    Ontology candidate;
	    OntoBuilderWrapper obw = new OntoBuilderWrapper();
	    SchemaTranslator boostingBestMapping = null;
	    SchemaTranslator exactMapping;
	    double boostPrecision = 0.0;
	    double boostRecall = 0.0;
	    String sOutput;
      
	      

		// TODO 2 For each experiment in the list:
	    for (int i = 0; i < 2; ++i) {  //size
			// TODO 2.1 load from file into OB objects
	        schemasExp = ds.get(i);
	        exactMapping = schemasExp.getExactMapping();
	        target = schemasExp.getTargetOntology();
	        candidate = schemasExp.getCandidateOntology();
			// TODO 2.2 1st line match using all available matchers in OB
	        String[] availableMatchers =  MatchingAlgorithms.ALL_ALGORITHM_NAMES;
	        MatchInformation firstLineMI[]= new MatchInformation[availableMatchers.length];
	        SchemaTranslator firstLineST[] = new SchemaTranslator[availableMatchers.length];
	        MatchMatrix firstLineMM[]= new MatchMatrix[availableMatchers.length];
	        for (int m=0;m<availableMatchers.length;m++)
	        {
	        	try {
					firstLineMI[m] = obw.matchOntologies(candidate, target,availableMatchers[m]);
				} catch (OntoBuilderWrapperException e) {
					e.printStackTrace();
				}
	        	firstLineST[m].importIdsFromMatchInfo(firstLineMI[m],true);
	        }
	        	
	        
			// TODO 2.3 2nd line match using all available matchers in OB with original matrix
	        String[] available2ndLMatchers = MappingAlgorithms.ALL_ALGORITHM_NAMES;
	        SchemaTranslator secondLineST[] = new SchemaTranslator[availableMatchers.length*availableMatchers.length];
	        for (int m=0;m<availableMatchers.length;m++)
	        {
	        	BestMappingsWrapper.matchMatrix = firstLineMI[m].getMatrix();
		        for (int mp=0;mp<available2ndLMatchers.length*availableMatchers.length;mp++)
		        {
		        	secondLineST[mp] = BestMappingsWrapper.GetBestMapping(available2ndLMatchers[mp]);
		            secondLineST[mp].importIdsFromMatchInfo(firstLineMI[m],true);
		        }
	        }
			// TODO 2.4 Output schema pair, term list, list of matchers and matches to URL
	        
			// TODO 2.5 run SMB_service with args: E URL
	        
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

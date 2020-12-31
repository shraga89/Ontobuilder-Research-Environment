package ac.technion.schemamatching.test;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

public class OntologyInvariance {
	
	private ExperimentSchemaPair ESPair;
	public OntologyInvariance(ExperimentSchemaPair ESP){
        FileOutputStream fileOut,fileOut1;
        this.ESPair = ESP;
		try {
			fileOut = new FileOutputStream("c:\\TEMP\\TargetOntology.ser");
	        ObjectOutputStream Out_Target = new ObjectOutputStream(fileOut);
			fileOut1 = new FileOutputStream("c:\\TEMP\\CandidateOntology.ser");
	        ObjectOutputStream Out_Candidate = new ObjectOutputStream(fileOut1);
	        Out_Target.writeObject(ESP.getTargetOntology());
	        Out_Candidate.writeObject(ESP.getCandidateOntology());
	        Out_Target.close();
	        Out_Candidate.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
/* returns 1 if the ontologys remained the same
 * returns 2 if the ontologys didn't remained the same
 */
	public int test_FLM(FirstLineMatcher FLM){
        FileInputStream fileIn,fileIn1;
		try {
			fileIn = new FileInputStream("c:\\TEMP\\TargetOntology.ser");
	        ObjectInputStream In_Target = new ObjectInputStream(fileIn);
			fileIn1 = new FileInputStream("c:\\TEMP\\CandidateOntology.ser");
	        ObjectInputStream In_Candidate = new ObjectInputStream(fileIn1);
	        Ontology orig_target = (Ontology) In_Target.readObject();
	        Ontology orig_candidate = (Ontology) In_Candidate.readObject();
	        In_Target.close();
	        In_Candidate.close();
	        fileIn.close();
	        fileIn1.close();
	        FLM.match(ESPair.getCandidateOntology(),ESPair.getTargetOntology(),true);
	        if ((orig_target.equals(ESPair.getTargetOntology())) && 
			(orig_candidate.equals(ESPair.getCandidateOntology()))){
				return 1;	
			}
			else{
				if (orig_target.getTerms(false).equals(ESPair.getTargetOntology().getTerms(false))){
					return 2;
				}
				if (orig_candidate.getTerms(false).equals(ESPair.getCandidateOntology().getTerms(false))){
					return 3;
				}
				if (orig_candidate.getRelationships().equals(ESPair.getCandidateOntology().getRelationships())){
					return 4;
				}
				return 5;}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return 5;

	}
	public int test_SLM(SecondLineMatcher SLM){
        FileInputStream fileIn,fileIn1;
		try {
			fileIn = new FileInputStream("c:\\TEMP\\TargetOntology.ser");
	        ObjectInputStream In_Target = new ObjectInputStream(fileIn);
			fileIn1 = new FileInputStream("c:\\TEMP\\CandidateOntology.ser");
	        ObjectInputStream In_Candidate = new ObjectInputStream(fileIn1);
	        Ontology orig_target = (Ontology) In_Target.readObject();
	        Ontology orig_candidate = (Ontology) In_Candidate.readObject();
	        In_Target.close();
	        In_Candidate.close();
	        fileIn.close();
	        fileIn1.close();
	        MatchInformation mi=ESPair.getExact();
	        SLM.match(mi);
	        if ((orig_target.equals(mi.getTargetOntology())) && 
			(orig_candidate.equals(mi.getCandidateOntology()))){
				return 1;	
			}
			else{
				return 2;}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        
        return 2;
	}
	public static void main(String[] args) {
		
	}

}

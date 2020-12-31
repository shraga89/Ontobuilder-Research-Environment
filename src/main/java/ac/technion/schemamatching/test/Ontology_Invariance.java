package ac.technion.schemamatching.test;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

public class Ontology_Invariance {
	
	private ExperimentSchemaPair ESPair;
	public Ontology_Invariance(ExperimentSchemaPair ESP){
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public boolean test_FLM(FirstLineMatcher FLM){
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
				System.out.println("the Ontologies remained the same");
				return true;	
			}
			else{
				System.out.println("Error! the Ontologies have changed");
				return false;}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}
	public static void main(String[] args) {
		
	}

}

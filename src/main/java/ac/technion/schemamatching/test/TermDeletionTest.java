package ac.technion.schemamatching.test;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;

public class TermDeletionTest {

	
	public static void main(String[] args){
		
		Ontology o = new Ontology();
		Term t = new Term("t");
		Term chT = new Term("child");
		t.addTerm(chT);
//		OntologyClass c = new OntologyClass("dummy");
//		c.setOntology(o.getModel());
//		t.setOntology(o.getModel());
//		t.setSuperClass(c);
		o.addTerm(t);
		System.out.println("after insert term, # terms::"+o.getAllTermsCount());
		o.removeTerm(chT);
		System.out.println("after remove term, # terms::"+o.getAllTermsCount());
	}
}

package ac.technion.schemamatchings.test;
import com.modica.ontology.Ontology;
import com.modica.ontology.Term;

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
		System.out.println("after insert term, # terms::"+o.getModel().getTermsCount());
		o.removeTerm(chT);
		System.out.println("after remove term, # terms::"+o.getModel().getTermsCount());
	}
}

/**
 * 
 */
package ac.technion.schemamatching.util;

import java.util.HashSet;
import java.util.Set;

import com.modica.ontology.Ontology;
import com.modica.ontology.Term;

import eu.nisb.project.graph.NisbGraph;
import eu.nisb.project.graph.inmemory.InMemoryStore;
import eu.nisb.project.objects.Attribute;
import eu.nisb.project.objects.Concept;
import eu.nisb.project.objects.NisbObjectFactory;

/**
 * This class contains utilities for converting schemas and ontologies between 
 * matching systems
 * @author Tomer Sagi
 *
 */
public class ConversionUtils {
	
	/**
	 * Utility to convert an ontobuilder ontology into a NisBGraph. 
	 * Currently only converts the term hierarchy of the ontology
	 * Term.Domain.Name->Attribute.Type
	 * Term.Name + "_" Term.Attributes("Name")->Attribute.Name
	 * Attribute provenance: nisb://syntheticOntobuilder/ + "Ontology Name" + "/" TermName + "/" subTermName ...
	 * If two attributes with the same name exist - 1 will be added at the end  
	 * @param o Ontology to convert
	 * @param target NisbGraph to populate
	 * @return if no NisbGraph is provided, returns a new NisbGraph populated with the ontology objects
	 */
	public static NisbGraph convertOntologytoNisBGraph(Ontology o,NisbGraph target)
	{
		NisbGraph res;
		if (target == null)
			res = new InMemoryStore();
		else
			res = target;
		
		HashSet<Attribute> conceptAtts = new HashSet<Attribute>(); 
		for (int i=0 ; i<o.getTermsCount();i++)
		{
			Term t = o.getTerm(i);
			addTermToNisbGraphRec(t,res,"nisb://syntheticOntobuilder//"+ o.getName(),conceptAtts);
		}
		Concept c=NisbObjectFactory.createConcept(o.getName(),conceptAtts,false);
		res.addObject(c);
		return res;
	}

	/**
	 * Recursive function to add a term t and all it's sub-terms to the provided NisbGraph (res).
	 * All created attributes are recorded in the conceptAtts collection
	 * @param t Term to add
	 * @param res NisbGraph in which to create the term
	 * @param provenance Current provenance up to this term
	 * @param conceptAtts Collection of attributes that is maintained in order to eventually create the concept in the calling method. 
	 * @return
	 */
	private static Attribute addTermToNisbGraphRec(Term t, NisbGraph res,String provenance, HashSet<Attribute> conceptAtts) 
	{
		Set<Attribute> subAttributes = new HashSet<Attribute>();
		String attProvenance = provenance + "//" + t.getName();
		String uri = NisbObjectFactory.createAttributeUri(attProvenance);
		if (res.getObject(uri) != null)
			attProvenance = attProvenance + "1"; //TODO this is a bad idea
		String tName = (t.getName()==null?"":t.getName())  + (t.getAttributeValue("name")==null?"":"_" + t.getAttributeValue("name"));
		// + (t.getValue()==null||t.getValue().equals("")?"":"_"+ t.getValue())
		String tType = (t.getDomain().getName());
		Attribute a = NisbObjectFactory.createAttribute(tName , tType ,attProvenance, subAttributes );
		
		for (int i=0 ; i<t.getTermsCount();i++)
		{
			Term subT = t.getTerm(i);
			subAttributes.add(addTermToNisbGraphRec(subT,res,attProvenance,conceptAtts));
		}
		a.setSubAttributes(subAttributes);
		res.addObject(a);
		conceptAtts.add(a);
		return a;
	}
	
}

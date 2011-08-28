/**
 * 
 */
package ac.technion.schemamatching.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

import com.google.common.collect.HashBiMap;
import eu.nisb.project.graph.NisbGraph;
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
	 * @return BiMap with TermID to AttributeURI mappings
	 */
	public static HashBiMap<Long, String> convertOntologytoNisBGraph(Ontology o,NisbGraph target)
	{
		HashBiMap<Long,String> res = HashBiMap.create();
		HashSet<Attribute> conceptAtts = new HashSet<Attribute>(); 
		for (int i=0 ; i<o.getTermsCount();i++)
		{
			Term t = o.getTerm(i);
			addTermToNisbGraphRec(res,t,target,"nisb://syntheticOntobuilder//"+ o.getName(),conceptAtts);
		}
		Concept c=NisbObjectFactory.createConcept(o.getName(),conceptAtts,false);
		target.addObject(c);
		return res;
	}

	/**
	 * Recursive function to add a term t and all it's sub-terms to the provided NisbGraph (res).
	 * All created attributes are recorded in the conceptAtts collection
	 * @param res2 BiMap in which to record the term ID to Attribute URI mappings
	 * @param t Term to add
	 * @param res NisbGraph in which to create the term
	 * @param provenance Current provenance up to this term
	 * @param conceptAtts Collection of attributes that is maintained in order to eventually create the concept in the calling method. 
	 * @return
	 */
	private static Attribute addTermToNisbGraphRec(HashBiMap<Long,String> res2, Term t, NisbGraph res,String provenance, HashSet<Attribute> conceptAtts) 
	{
		Set<Attribute> subAttributes = new HashSet<Attribute>();
		String tName = (t.getName()==null?"":t.getName())  + (t.getAttributeValue("name")==null?"":"_" + t.getAttributeValue("name"));
		String attProvenance = provenance + "//" + tName;
		String uri = NisbObjectFactory.createAttributeUri(attProvenance);
		if (res.getObject(uri) != null)
			attProvenance = attProvenance + "1"; //TODO this is a bad idea
		
		// + (t.getValue()==null||t.getValue().equals("")?"":"_"+ t.getValue())
		String tType = (t.getDomain().getName());
		Attribute a = NisbObjectFactory.createAttribute(tName , tType ,attProvenance, subAttributes );
		res.addObject(a);
		res2.put(t.getId(), a.getUri());
		conceptAtts.add(a);
		for (int i=0 ; i<t.getTermsCount();i++)
		{
			Term subT = t.getTerm(i);
			subAttributes.add(addTermToNisbGraphRec(res2,subT,res,attProvenance,conceptAtts));
		}
		a.setSubAttributes(subAttributes);
		return a;
	}
	
	/**
	 * For every match selected makes confidence 1. The rest are set to 0. 
	 * @param mi
	 */
	public static void binarize(MatchInformation mi)
	{
		MatchMatrix res = new MatchMatrix();
		res.copyWithEmptyMatrix(mi.getMatrix());
		for (Object o : mi.getMatches())
		{
			Match m = (Match)o;
			m.setEffectiveness(1.0);
			res.setMatchConfidence(m.getCandidateTerm(), m.getTargetTerm(), 1.0);
		}
		mi.setMatrix(res);
	}
	
	/**
	 * Updates MatchInformation object with matches found in schema translator object
	 * Assumes term id's in matched attribute pairs exist in candidate and target ontologies
	 * @param mi
	 * @param st
	 * @throws Exception if terms in schema translator object are not found in ontologies in the match information object
	 */
	
	public static void fillMI(MatchInformation mi, SchemaTranslator st) throws Exception
	{
		for (MatchedAttributePair map : st.getMatchedPairs())
		{
			Term cTerm = mi.getCandidateOntology().getTermByID(map.id1);
			if (cTerm==null) throw new Exception("Candidate Term with id:" + Long.toString(map.id1)+ " could not be found in "+ mi.getCandidateOntology().getName());
			Term tTerm = mi.getTargetOntology().getTermByID(map.id2);
			if (tTerm==null) throw new Exception("Target Term with id:" + Long.toString(map.id2)+ " could not be found in "+ mi.getTargetOntology().getName());
			double confidence = map.getMatchedPairWeight();
			Match m = new Match(tTerm,cTerm,confidence);
			mi.addMatch(m);
			mi.getMatrix().setMatchConfidence(cTerm, tTerm, confidence);
		}
	}

	/**
	 * Fills missing terms in the MatchMatrix object of the supplied 
	 * MatchInformation object so that the matrix is n X m.
	 * @param mi
	 */
	public static void fillMI(MatchInformation mi) {
		ArrayList<Term> candTerms = new ArrayList<Term>();
		candTerms.addAll(mi.getCandidateOntology().getTerms(true));
		ArrayList<Term> targetTerms = new ArrayList<Term>();
		targetTerms.addAll(mi.getTargetOntology().getTerms(true));
		MatchMatrix mm = new MatchMatrix(candTerms.size(),targetTerms.size(), candTerms, targetTerms);
		for (Object o : mi.getMatches())
		{
			Match m = (Match)o;
			mm.setMatchConfidence(m.getCandidateTerm(), m.getTargetTerm(), m.getEffectiveness());
		}
		mi.setMatrix(mm);
	}
	
}

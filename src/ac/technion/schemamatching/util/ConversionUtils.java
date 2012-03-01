/**
 * 
 */
package ac.technion.schemamatching.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import smb_service.Schema;
import smb_service.SimilarityMatrix;


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
			if (cTerm==null) 
			{
				if (mi.getTargetOntology().getTermByID(map.id1)!=null) //Ontologies are reversed in database
				{
					throw new Exception("reversed ontologies");
				}
				else
					throw new Exception("Candidate Term with id:" + Long.toString(map.id1)+ " could not be found in "+ mi.getCandidateOntology().getName());
					
			}
			
			Term tTerm = mi.getTargetOntology().getTermByID(map.id2);
			if (tTerm==null) 
				throw new Exception("Target Term with id:" + Long.toString(map.id2)+ " could not be found in "+ mi.getTargetOntology().getName());
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
		for (Term c : candTerms)
			for (Term t : targetTerms)
			{
				double conf = mi.getMatrix().getMatchConfidence(c, t);
				mm.setMatchConfidence(c, t,(conf==-1?0:conf) );
			}
//		for (Object o : mi.getMatches())
//		{
//			Match m = (Match)o;
//			mm.setMatchConfidence(m.getCandidateTerm(), m.getTargetTerm(), m.getEffectiveness());
//		}
		mi.setMatrix(mm);
	}
	
	/**
	 * Takes match matrix of smaller matrix and expands it to the size of the larger matrix
	 * @param small MAtchInformation object containing a match matrix which is a subset of the one in big
	 * @param big MatchInformation object containing a larger match matrix 
	 * @throws Exception if small matchInformation object's match matrix 
	 * is larger than big or if terms from small are missing in big
	 */
	public static MatchInformation expandMatrix(MatchInformation small, MatchInformation big) throws Exception
	{
		if (small.getMatchMatrix().length>big.getMatchMatrix().length) throw new Exception("Match Information declared as small is larger than the one declared as big");
		MatchInformation newMI = big.clone();
		MatchMatrix newMM = newMI.getMatrix();
		newMM.copyWithEmptyMatrix(big.getMatrix());
		MatchMatrix sMM = small.getMatrix();
		for (Term c : sMM.getCandidateTerms())
			for (Term t : sMM.getTargetTerms())
			{
				double conf = sMM.getMatchConfidence(c, t);
				if (conf==-1) throw new Exception("Candidate term: " + c.getName() + " or target term: " + t.getName() + " not found in big match matrix.");
				newMM.setMatchConfidence(c, t, conf);
			}
		newMI.setMatches(small.getMatches());
		return newMI;
	}
	
	/**
	 * Updates match matrix. Sets all unmatched entries to 0
	 * @param mi
	 */
	public static void zeroNonMatched(MatchInformation mi)
	{
	
		MatchMatrix mm = new MatchMatrix();
		mm.copyWithEmptyMatrix(mi.getMatrix());
		for (Match m : mi.getMatches())
			mm.setMatchConfidence(m.getCandidateTerm(), m.getTargetTerm(), m.getEffectiveness());
	}
	
	/**
	 * Converts from an Ontology to the Schema format specified in @link{SMB}
	 * @param o
	 * @return
	 */
	public static Schema ontology2schema(Ontology o)
	{
		//TODO complete this from the feature documenter in ORE
		HashMap<String, Long> features = new HashMap<String, Long>();

		HashMap<Long, String> terms = new HashMap<Long, String>();
		for (Term t : o.getTerms(true))
			terms.put(t.getId(), t.getName());
		
		Schema res = new Schema(o.getId(), o.getName(),features,terms);
		return res;
	}
	
	/**
	 * Converts from a matchInformation object to the SimilarityMatrix class specified in @link{SMB}
	 * @param mi Match Information Object to be converted
	 * @return
	 */
	public static SimilarityMatrix mi2simMatrix(MatchInformation mi)
	{
		HashMap<Long,Integer> candidateTerms = new HashMap<Long,Integer>();
		HashMap<Long,Integer> targetTerms =  new HashMap<Long,Integer>();
		MatchMatrix mm = mi.getMatrix();
		int i=0,j=0;
		double[][] inSimilarityM = new double[mm.getCandidateTerms().size()][mm.getTargetTerms().size()];
		for (Term c : mm.getCandidateTerms())
		{
			candidateTerms.put(c.getId(),i);
			for (Term t : mm.getTargetTerms())
			{
				targetTerms.put(t.getId(), j);
				inSimilarityM[i][j] = mm.getMatchConfidence(c, t);
				j++;
			}
			i++;
			j=0;
		}

		SimilarityMatrix res = new SimilarityMatrix(mi.getCandidateOntology().getId(),mi.getTargetOntology().getId(),candidateTerms,targetTerms,inSimilarityM);
		
		return res;
	}

	/**
	 * Restores confidence values in matches res from values in mi
	 * Used to fix 2LM which set confidence values to 1
	 * @param res
	 * @param mi
	 */
	public static void restoreConfidence(MatchInformation res,
			MatchInformation mi) {
		for (Match m: res.getMatches())
		{
			double conf = mi.getMatchConfidence(m.getTargetTerm(), m.getCandidateTerm());
			m.setEffectiveness((conf==0.0?1:conf));
		}
		
	}
}

/**
 * 
 */
package ac.technion.schemamatching.matchers.firstline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.schemamatching.util.ConversionUtils;

import com.google.common.collect.HashBiMap;
import com.sap.research.amc.utils.cli.MatcherType;
import com.sap.research.amc.utils.cli.MatchingConfiguration;
import com.sap.research.amc.utils.cli.MatchingPerformer;
import com.sap.research.amc.utils.cli.NisbSimilarityMatrix;

import eu.nisb.project.graph.NisbGraph;
import eu.nisb.project.graph.inmemory.InMemoryStore;
import eu.nisb.project.objects.Attribute;
import eu.nisb.project.objects.Correspondence;

/**
 * Wrapper class for NisB Auto Mapping Core - Token Path Algorithm
 * @author Tomer Sagi
 *
 */
public class AMCTokenPath implements FirstLineMatcher {

	protected Collection<MatcherType> matcher;
	protected MatchingConfiguration conf;

	public AMCTokenPath()
	{
		matcher = Arrays.asList(com.sap.research.amc.utils.cli.MatcherType.TOKENPATH);
		conf = new MatchingConfiguration(matcher);
		conf.setThreshold(0.1f);
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getName()
	 */
	public String getName() {
		return "AMC Token Path";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#hasBinary()
	 */
	public boolean hasBinary() {
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#match(com.modica.ontology.Ontology, com.modica.ontology.Ontology, boolean)
	 */
	public MatchInformation match(Ontology candidate, Ontology target, boolean binary) {
		final NisbGraph srcNG = new InMemoryStore(); 
		HashBiMap<Long, String> candMap = ConversionUtils.convertOntologytoNisBGraph(candidate, srcNG);
		final NisbGraph trg = new InMemoryStore(); 
		HashBiMap<Long, String> targMap = ConversionUtils.convertOntologytoNisBGraph(target, trg);
		MatchInformation res = new MatchInformation(candidate,target);
		ArrayList<Term> candList = new ArrayList<Term>();
		candList.addAll(candidate.getTerms(true));
		ArrayList<Term> targList = new ArrayList<Term>();
		targList.addAll(target.getTerms(true));
		MatchMatrix mm = new MatchMatrix(candList.size(),targList.size(),candList,targList);
		if (binary)
		{
			List<Correspondence> matchList = MatchingPerformer.performMatch(conf, srcNG, trg);
			for(Correspondence c : matchList)
				for (Attribute l : c.getLeftAttributes())
					for (Attribute r : c.getRightAttributes())
						{
							Term t = target.getTermByID(targMap.inverse().get(r.getUri()));
							Term tc = candidate.getTermByID(candMap.inverse().get(l.getUri()));
							mm.setMatchConfidence(tc, t, c.getQuality());
						}
		}
		else
		{
			NisbSimilarityMatrix mat = MatchingPerformer.getSimilarityMatrix(matcher, srcNG, trg);
			Set<Attribute> all_source = srcNG.getAllAttributes();
			Set<Attribute> all_target = trg.getAllAttributes();
			for (Attribute srcAtt : all_source) {
				for (Attribute trgAtt : all_target) {
					Term t = target.getTermByID(targMap.inverse().get(trgAtt.getUri()));
					Term c = candidate.getTermByID(candMap.inverse().get(srcAtt.getUri()));
					double confidence =  mat.getSimilarity(srcAtt, trgAtt);
					if (confidence > 0.1)
					{
						mm.setMatchConfidence(c, t, confidence);
					}
				}
			}
		}
		res.setMatrix(mm);
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getConfig()
	 */
	public String getConfig() {
		return "default config";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getType()
	 */
	public ac.technion.schemamatching.matchers.MatcherType getType() {
		return  ac.technion.schemamatching.matchers.MatcherType.STRUCTURAL_PARENTCHILD;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getDBid()
	 */
	public int getDBid() {
		return 7;
	}

}

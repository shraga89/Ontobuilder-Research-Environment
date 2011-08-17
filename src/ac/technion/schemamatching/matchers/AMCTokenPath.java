/**
 * 
 */
package ac.technion.schemamatching.matchers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ac.technion.schemamatching.util.ConversionUtils;

import com.modica.ontology.Ontology;
import com.modica.ontology.match.MatchInformation;
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
		ConversionUtils.convertOntologytoNisBGraph(candidate, srcNG);
		final NisbGraph trgNG = new InMemoryStore(); 
		ConversionUtils.convertOntologytoNisBGraph(target, trgNG);
		MatchInformation res = new MatchInformation();
		res.setCandidateOntology(candidate);
		res.setTargetOntology(target);
		if (binary)
		{
			List<Correspondence> matchList = MatchingPerformer.performMatch(conf, srcNG, trgNG);
			for(Correspondence c : matchList)
				for (Attribute l : c.getLeftAttributes())
					for (Attribute r : c.getRightAttributes())
						res.addMatch(target.getTermByID(Long.parseLong(r.getUri())),candidate.getTermByID(Long.parseLong(l.getUri())) , c.getQuality());
		}
		else
		{
			NisbSimilarityMatrix mat = MatchingPerformer.getSimilarityMatrix(matcher, srcNG, trgNG);
			Set<Attribute> all_source = srcNG.getAllAttributes();
			Set<Attribute> all_target = trgNG.getAllAttributes();
			for (Attribute srcAtt : all_source) {
				for (Attribute trgAtt : all_target) {
					res.addMatch(target.getTermByID(Long.parseLong(trgAtt.getUri())), candidate.getTermByID(Long.parseLong(srcAtt.getUri())), mat.getSimilarity(srcAtt, trgAtt));
				}
			}
		}
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

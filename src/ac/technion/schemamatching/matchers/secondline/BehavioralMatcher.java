/**
 * 
 */
package ac.technion.schemamatching.matchers.secondline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MatchCompetitorDeviation;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.predictors.AttributePredictors;
import ac.technion.schemamatching.statistics.predictors.AvgAPredictor;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictors;

/**
 * @author Tomer Sagi
 * Given an extended human matcher matrix returns a matrix where the confidence
 * of matches supplied at a later match time are reduced
 *
 */
public class BehavioralMatcher implements SecondLineMatcher {

	private boolean linear;
	private float a = -1/120f;
	private float b = 1.0f;
	private float limit = 62.0f;
	private MatchInformation fmi=null;
	private float t = 0.1f;
	Map<String,Float> slope=null;
	Map<String,Float> intersect= null;
	
	/**
	 * When supplied, saves the given FMI and returns a matrix which complements matched one
	 * using the given one
	 * @param fmi
	 */
	public BehavioralMatcher(MatchInformation fmi) {
		this.fmi = fmi;

	}
	public BehavioralMatcher(MatchInformation fmi, Map<String,Float> slope, Map<String,Float> intersect) {
		this.fmi = fmi;
		this.intersect = intersect;
		this.slope = slope;

	}

	public BehavioralMatcher() {
		
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.secondline.SecondLineMatcher#getName()
	 */
	@Override
	public String getName() {
		return "BehavioralMatcher";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.secondline.SecondLineMatcher#match(ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	@Override
	public MatchInformation match(MatchInformation mi) {
		return match(mi, "");
	}
	
	public MatchInformation match(MatchInformation mi, String participant) {
		MatchInformation res = mi.clone();

		for (Match m : mi.getCopyOfMatches())
		{
			Properties props = m.getProps();
			if (props==null) {
				System.err.println("Behavioral predictor didn't find extended properties in match " + m.toString());
				continue;
			}
			double diff = (Double)props.get("diff");
			if (slope != null){
				participant = String.valueOf((Integer.parseInt(participant)));
				if (slope.containsKey(participant)){
					a = slope.get(participant);
					b = intersect.get(participant);	
				}
			}
//			System.out.println(participant +" "+ a +" " + " "+ b);
			double weight = a*diff+b;
			if (diff > limit){
				weight = 0.0;
			}
			weight = weight <0.0f ? 0.0f : weight;
			double eff = m.getEffectiveness()*weight + (this.fmi==null ? 0.0f : fmi.getMatchConfidence(m.getCandidateTerm(), m.getTargetTerm())*(1.0-weight));
			res.updateMatch(m.getTargetTerm(), m.getCandidateTerm(), eff);
		}
		if (this.fmi!=null){
			for (Match m : fmi.getCopyOfMatches()){
				if (res.getMatchConfidence(m.getCandidateTerm(), m.getTargetTerm()) != -1){
					double eff = m.getEffectiveness();
					if (eff >= t){
						res.updateMatch(m.getTargetTerm(), m.getCandidateTerm(), eff);
					}
				}
			}
		}
		return res;
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.secondline.SecondLineMatcher#getConfig()
	 */
	@Override
	public String getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.secondline.SecondLineMatcher#getDBid()
	 */
	@Override
	public int getDBid() {
		// TODO update in DB and ENUM
		return 99;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.secondline.SecondLineMatcher#init(java.util.Properties)
	 */
	@Override
	public boolean init(Properties properties) {
		//props: linear / algorithmic. For linear: a and b, for logarithmic: base of log.
		linear = properties.getOrDefault("behavioral.linear", "true").equals("true");
		a = Float.parseFloat((String)properties.getOrDefault("behavioral.a", "-0.05f"));
		b = Float.parseFloat((String)properties.getOrDefault("behavioral.b", "1.0f"));
		t = Float.parseFloat((String)properties.getOrDefault("t", "0.9f"));
		System.out.println(t);
		return true;
	}

}

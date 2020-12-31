package ac.technion.schemamatching.matchers.secondline;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.statistics.MatchCompetitorDeviation;
import ac.technion.schemamatching.util.ce.CEObjective;
import ac.technion.schemamatching.util.ce.CESample;

public class OBObjective implements CEObjective{
	
	private MatchInformation mi;
	private double mcdCoff = 0.5;
	private boolean isOne2one;
	
	public OBObjective(MatchInformation mi, double mcdCoff, boolean isOne2one){
		this.mi = mi;
		this.mcdCoff = mcdCoff;
		this.isOne2one = isOne2one;
	}

	@Override
	public boolean isMaximized() {
		return true;
	}

	@Override
	public double evaluate(CESample sample) {
		OBSample _sample = (OBSample)sample;
		MatchInformation cemi = _sample.getMatchInformation();
		double totalMatchWeight = cemi.getTotalMatchWeight();
		//int numMatchedPairs = cemi.getNumMatches();
		MatchCompetitorDeviation mcd = new MatchCompetitorDeviation(isOne2one);
		mcd.init(null, mi, cemi);
		double mcdVal = Double.parseDouble(mcd.getData().get(0)[1]);
		//double beta = 1- mcdCoff;
		//return (1 + beta*beta)*((mcdVal*totalMatchWeight)/(beta*beta*mcdVal + totalMatchWeight));
		return Math.pow(totalMatchWeight,1 - mcdCoff) * Math.pow(mcdVal,mcdCoff);//Math.sqrt(1.0/numMatchedPairs) Haggai change 25/8/2014 --> this
		//normalization was pushed to be internal in MCD
	}
	
}

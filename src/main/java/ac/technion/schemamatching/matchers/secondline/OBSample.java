package ac.technion.schemamatching.matchers.secondline;

import java.util.List;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.util.ce.CESample;

public class OBSample implements CESample{
	
	private double value;
	private MatchInformation mi;
	
	public OBSample(MatchInformation mi) {
		this.mi = mi;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public void setValue(double value) {
		this.value = value;
	}

	public MatchInformation getMatchInformation() {
		return mi;
	}

	public boolean hasMatchedPair(String candId, String targetId) {
		List<Match> match = mi.getCopyOfMatches();
		if (match != null){
			for (Match m : match){
				if (m.getCandidateTerm().getName().equals(candId) && 
						m.getTargetTerm().getName().equals(targetId)) return true;
			}
		}
		return false;
	}

}

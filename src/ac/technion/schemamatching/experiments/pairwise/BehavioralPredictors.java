/**
 * 
 */
package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.statistics.Statistic;

/**
 * @author Tomer Sagi
 * Returns predictors based upon human behavioral information
 * Assumes matches contain the properties elapsed and diff  
 *
 */
public class BehavioralPredictors implements Statistic {

	String[] header = new String[]{"not init"};
	List<String[]> data = new ArrayList<>();
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#getHeader()
	 */
	@Override
	public String[] getHeader() {
		return header;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#getName()
	 */
	@Override
	public String getName() {
		return "Behavioral Predictors";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#getData()
	 */
	@Override
	public List<String[]> getData() {
		return data;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#init(java.lang.String, ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	@Override
	public boolean init(String instanceDescription, MatchInformation mi) {
		header = new String[]{"SPID,PID","TargetTermID","AvgElapsed","MaxElapsed","AvgDiff","MaxDiff"};
		//init maps
		HashMap<Long,Double> avgElapsed = new HashMap<>();
		HashMap<Long,Integer> countT = new HashMap<>();
		HashMap<Long,Double> maxElapsed = new HashMap<>();
		HashMap<Long,Double> avgDiff = new HashMap<>();
		HashMap<Long,Double> maxDiff = new HashMap<>();
		for (Term t : mi.getTargetOntology().getTerms(true))
		{
			Long id = t.getId();
			countT.put(id, 0);
			avgElapsed.put(id, new Double(0.0));
			maxElapsed.put(id, new Double(0.0));
			avgDiff.put(id, new Double(0.0));
			maxDiff.put(id, new Double(0.0));
		}
		
		for (Match m : mi.getCopyOfMatches())
		{
			Properties props = m.getProps();
			if (props==null)
			{
				System.err.println("Behavioral predictor didn't find extended properties in match");
				continue;
			}
			Long id = m.getTargetTerm().getId();
			int cnt = countT.get(id);
			double elapsed = (Double)props.get("elapsed");
			double diff = (Double)props.get("diff");
			if (cnt==0)	{
				avgElapsed.put(id,elapsed);
				maxElapsed.put(id,elapsed);
				avgDiff.put(id, diff);
				maxDiff.put(id, diff);
			} else {
				avgElapsed.put(id, (avgElapsed.get(id)*cnt+elapsed) / (cnt+1));
				maxElapsed.put(id, Math.max(maxElapsed.get(id),elapsed));
				avgDiff.put(id, (avgDiff.get(id)*cnt+diff) / (cnt+1));
				maxDiff.put(id, Math.max(maxDiff.get(id),diff));
			}
				
			cnt++;
			countT.put(id, cnt);
				
		}
		
		for (Term t : mi.getTargetOntology().getTerms(true))
		{
			Long id = t.getId();
			String[] res = new String[]{instanceDescription,Long.toString(t.getId())
					,avgElapsed.get(id).toString(),maxElapsed.get(id).toString()
					,avgDiff.get(id).toString(),maxDiff.get(id).toString()};
			data.add(res );
		}
		return true;
	}

}

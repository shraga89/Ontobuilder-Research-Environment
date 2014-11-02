/**
 * 
 */
package ac.technion.schemamatching.statistics;

import java.util.ArrayList;
import java.util.List;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;

/**
 * designed for OBCrossEntropy in order to measure 
 * The number of iterations it took OBCrossEntropy to converge
 */
public class NumIterations implements K2Statistic {

	String[] header = new String[]{"instance","The number of iterations it took OBCrossEntropy to converge"};
	Integer numIterations=0;
	private List<String[]> data = new ArrayList<String[]>();
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
		return "Number of iterations";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#getData()
	 */
	@Override
	public List<String[]> getData() {
		return data ;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Statistic#init(java.lang.String, ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	@Override
	public boolean init(String instanceDescription, MatchInformation mi) {
		String[] Iterations=new String[2];
		Iterations[0]=instanceDescription;
		Iterations[1]=numIterations.toString();
		data.add(Iterations);
		return true;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.K2Statistic#init(java.lang.String, ac.technion.iem.ontobuilder.matching.match.MatchInformation, ac.technion.iem.ontobuilder.matching.match.MatchInformation)
	 */
	@Override
	public boolean init(String instanceDescription, MatchInformation mi,
			MatchInformation exactMatch) {
		return false;
	}
	public void addNumOfIter(int num){
		numIterations=num;
	}

}

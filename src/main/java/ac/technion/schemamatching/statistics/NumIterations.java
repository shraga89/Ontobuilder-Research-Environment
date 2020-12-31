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

	String[] header = new String[]{"instance","number of iterations", "Time","Objective","Numcands","Numtargets","Matrixdim"};
	Integer numIterations=0;
	Integer timeIterations=0;
	double objective=0.0;
	Integer numcands=0;
	Integer numtargets=0;
	Integer matrixdim=0;
	
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
		String[] Iterations=new String[7];
		Iterations[0]=instanceDescription;
		Iterations[1]=numIterations.toString();
		Iterations[2]=timeIterations.toString();
		Iterations[3]= Double.toString(objective);
		Iterations[4]=numcands.toString();
		Iterations[5]=numtargets.toString();
		Iterations[6]=matrixdim.toString();
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
	public void addNumOfIter(int num,long time,double Objective,int Numcands,int Numtargets,int Matrixdim){
		numIterations=num;
	    timeIterations = (int) time;
		objective=Objective;
		numcands=Numcands;
		numtargets=Numtargets;
		matrixdim=Matrixdim;
	}

}

/**
 * 
 */
package ac.technion.schemamatching.statistics.predictors;

import java.util.ArrayList;
import java.util.HashMap;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.schemamatching.statistics.Statistic;

/**
 * Calculates various entry predictions on a similarity matrix
 * with no golden mapping
 * @author Tomer
 *
 */
public class EntryPredictors implements Statistic {

	private static String name = "Entry Predictors";
	private String[] header;
	private ArrayList<String[]> data = new ArrayList<String[]>();
	
	public EntryPredictors()
	{
		//DBN: Compare to vector with entry set to 1
		//DSM: Set entry to max and all others unchanged
		//DLA: Set all entries over this one to it's value
		//Conf: Use value
		
		header = new String[]{"InstanceDesc","DBN","DSM","DLA","Val"};
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.Statistic#getHeader()
	 */
	public String[] getHeader() {
		return header;
	}
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.Statistic#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.Statistic#getData()
	 */
	public ArrayList<String[]> getData() {
		return data;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.Statistic#init(com.modica.ontology.match.MatchInformation)
	 */
	public boolean init(String instanceDesc, MatchInformation mi) {
		
		double[][] confM = mi.getMatchMatrix();
		if (confM == null || confM.length == 0) return false;
		//Make Reverse Hash to lookup termID by row / col:
		HashMap<Integer, Long> candidateIDs = makeReverseHash(mi, true);
		HashMap<Integer, Long> targetIDs = makeReverseHash(mi, false);
				
		//Row Attribute Predictions
		for (int row =0; row < confM.length; row++)
			for (int col =0; col < confM[row].length; col++)
			{
				//Init
				String[] res = new String[header.length];
				long cID = targetIDs.get(new Integer(row));
				long tID = candidateIDs.get(new Integer(col));
				res[0] = instanceDesc + "," + cID + "," + tID;
				
				//vector variables: length and dotproduct with entry vector
				double val = confM[row][col];
				double entryVecLength = 0.0;
				
				double dsmVecLength = 0.0; double maxVal = val;
				double dlaVecLength = 0.0;
				
				double binVecDotProduct = val; //val X 1.0, all other entries are set to 0.0
				double dsmVecDotProduct = 0.0;
				double dlaVecDotProduct = 0.0;
				
				
				//iterate over other entries in vector (row and column of entry)
				for (int r = 0; r<confM.length ; r++)
				{
					if (r==row) continue;
					double entryVecVal=confM[r][col];
					entryVecLength+=entryVecVal*entryVecVal;
					maxVal = Math.max(maxVal, entryVecVal);
					dsmVecLength+=Math.pow(entryVecVal,2.0);
					dsmVecDotProduct+=entryVecVal*entryVecVal;
					
					double newDlaVal = (entryVecVal < val?val :entryVecVal); 
					dlaVecLength+=Math.pow(newDlaVal,2.0);
					dlaVecDotProduct+=newDlaVal*entryVecVal;
				}
				for (int c = 0; c<confM[row].length ; c++)
				{
					if (c==col) continue;
					double entryVecVal=confM[row][c];
					entryVecLength+=entryVecVal*entryVecVal;
					maxVal = Math.max(maxVal, entryVecVal);
					dsmVecLength+=Math.pow(entryVecVal,2.0);
					dsmVecDotProduct+=entryVecVal*entryVecVal;
					
					double newDlaVal = (entryVecVal < val?val :entryVecVal); 
					dlaVecLength+=Math.pow(newDlaVal,2.0);
					dlaVecDotProduct+=newDlaVal*entryVecVal;
				}
				//add values for entry
				double binVecLength = 1.0; //all other entries are set to 0.0
				dsmVecLength+=maxVal*maxVal;
				dsmVecDotProduct+=maxVal*val;
				dlaVecLength+=val*val;
				dlaVecDotProduct+=val*val;
				entryVecLength+=val*val;
				
				//For 0 length vectors, set prediction to 0.0
				double dbn  = (val==0.0?0.0:binVecDotProduct
						/Math.sqrt(binVecLength*entryVecLength));
				res[1] = new Double(dbn).toString();
				double dsm = (entryVecLength==0?0.0:dsmVecDotProduct
						/Math.sqrt(dsmVecLength*entryVecLength));
				res[2] = new Double(dsm).toString();
				double dla = (val==0&&entryVecLength==0?0.0:dlaVecDotProduct
						/Math.sqrt(dlaVecLength*entryVecLength));
				res[3] =  new Double(dla).toString();
				res[4] = new Double(val).toString();
				data.add(res);
				assert(dbn<=1.0);
				assert(dla<=1.0);
				assert(dsm<=1.0);
				assert(val<=1.0);
			}
		return true;
	}

	/**
	 * @param mi
	 * @param candidateIDs
	 * @return
	 */
	private HashMap<Integer, Long> makeReverseHash(MatchInformation mi,boolean candidate) {
		HashMap<Integer, Long> indexIDs = new HashMap<Integer, Long>();
		MatchMatrix m = mi.getMatrix();
		ArrayList<Term> termList = (candidate?m.getCandidateTerms():m.getTargetTerms()) ;
		for (Term t : termList)
		{
			Integer ind = m.getTermIndex(termList, t,candidate);
			assert (ind!=null); 
			indexIDs.put(ind, t.getId());
		}
		return indexIDs;
	}

}

package ac.technion.schemamatching.experiments;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.secondline.OBCrossEntropy;
import ac.technion.schemamatching.matchers.secondline.OBCrossEntropy.OBCrossEntropyResult;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.MCC;


public class CE4SMUsage {

	
	public static void main(String[] args){

		//Result of some 1LM	
		MatchInformation mi = null;
		
		//Exact match
		MatchInformation exact = null;

		//New 2LM 
		SecondLineMatcher obce = new OBCrossEntropy();
		obce.init(null);
		System.out.println(obce.getConfig());
		MatchInformation obceMatch = obce.match(mi);
		System.out.println("********* OBCE *********");
        //Basic quality measures
		BinaryGolden statistic = new BinaryGolden();
		statistic.init(null, obceMatch, exact);
		String[] header = statistic.getHeader();
		String[] data = statistic.getData().get(0);
		for (int i=0;i<header.length;i++){
			System.out.print(header[i]+": "+data[i]+", ");
		}
		System.out.println();

		//New MCC measure
		MCC mcc = new MCC();
		mcc.init(null, obceMatch, exact);
		header = mcc.getHeader();
		data = mcc.getData().get(0);
		for (int i=0;i<header.length;i++){
			System.out.print(header[i]+": "+data[i]+", ");
		}
		System.out.println();
		
		//Obtain run statistics
		OBCrossEntropyResult result = ((OBCrossEntropy)obce).getOBCrossEntropyResult();
		System.out.println("Num iterations: "+result.numIterations);
		System.out.println("Time: "+result.time);
		System.out.println("Objective: "+result.getOptimalObjectiveValue());
		System.out.println("Num cands: "+result.getNumCands());
		System.out.println("Num targets: "+result.getNumTargets());
		System.out.println("Matrix dim: "+result.getMatrixDim());
	}
}

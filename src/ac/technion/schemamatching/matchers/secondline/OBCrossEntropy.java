package ac.technion.schemamatching.matchers.secondline;

import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.util.ConversionUtils;
import ac.technion.schemamatching.util.ce.CEObjective;
import ac.technion.schemamatching.util.ce.CrossEntropyOptimizer;
import ac.technion.schemamatching.util.ce.CrossEntropyOptimizer.CEOptimizationResult;

/**
 * A second line matcher that finds a match using cross entropy optimization.
 * Able to produce either a 1:1 or 1:n matching. 
 */
public class OBCrossEntropy implements SecondLineMatcher{
	
	
	private int sampleSize = 10000;
	private double ro = 0.01;
	private double smoothAlpha = 0.7;
	private int stopAfter = 10;
	private boolean isOne2OneMatch = true;
	private OBCrossEntropyResult result = null;
	private double mcdCoff = 0.5;

	@Override
	public String getName() {
		return "Ontobuilder CrossEntropy";
	}

	@Override
	public MatchInformation match(MatchInformation mi) {
		//apply Matrix Cleaning
		MatchInformation CleanedMatrix = new MatchInformation(mi.getCandidateOntology(),mi.getTargetOntology());
		CleanedMatrix.setMatrix(mi.getMatrix());
		ConversionUtils.zeroWeightsByThresholdAndRemoveMatches(CleanedMatrix, 0.01);
		ConversionUtils.limitToKMatches(CleanedMatrix, 10);
		
		CrossEntropyOptimizer ceo = new CrossEntropyOptimizer(sampleSize, ro, stopAfter);
		CEObjective objective = new OBObjective(CleanedMatrix,mcdCoff,isOne2OneMatch);
		synchronized(this){
		   result = new OBCrossEntropyResult(ceo,ceo.optimize(objective,
				   new OBModel(CleanedMatrix, smoothAlpha, isOne2OneMatch)), CleanedMatrix);
		}
		return ((OBSample)result.bestSample).getMatchInformation();
	}

	@Override
	public String getConfig() {
		return "sampleSize="+sampleSize+", ro="+ro+", smoothAlpha="+smoothAlpha+", stopAfter="+stopAfter+", isOne2OneMatch="+isOne2OneMatch+", mcdCoff="+mcdCoff;
	}

	@Override
	public int getDBid() {
		return 8;
	}

	@Override
	public boolean init(Properties properties) {
		if (properties != null){
			sampleSize = Integer.parseInt(properties.getProperty("sampleSize"));
			ro = Double.parseDouble(properties.getProperty("ro"));
			smoothAlpha = Double.parseDouble(properties.getProperty("smoothAlpha"));
			stopAfter = Integer.parseInt(properties.getProperty("stopAfter"));
			isOne2OneMatch = Boolean.parseBoolean(properties.getProperty("isOne2OneMatch"));
		}
		return true;
	}
	
	
	public synchronized OBCrossEntropyResult getOBCrossEntropyResult(){
		return result;
	}
	
	
    public class OBCrossEntropyResult extends CEOptimizationResult{
    	
    	private final int numCands;
    	private final int numTargets;
    	private final int matrixDim;

		public OBCrossEntropyResult(final CrossEntropyOptimizer CEO, final CEOptimizationResult nativeResult, final MatchInformation mi) {
			CEO.super(nativeResult.bestSample,nativeResult.numIterations,nativeResult.time);
			numCands = mi.getMatrix().getColCount();
			numTargets = mi.getMatrix().getRowCount();
			matrixDim = numCands*numTargets;
		}

		public int getNumCands() {
			return numCands;
		}

		public int getNumTargets() {
			return numTargets;
		}

		public int getMatrixDim() {
			return matrixDim;
		}
		
		public double getOptimalObjectiveValue() {
			return bestSample.getValue();
		}
    	
    }


}

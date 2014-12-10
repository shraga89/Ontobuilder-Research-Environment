package ac.technion.schemamatching.util.ce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A generic stochastic optimization utility based on the Cross Entropy Method
 * @see http://iew3.technion.ac.il/CE/
 */
public class CrossEntropyOptimizer{

	/* Sample size */
	protected int N = 1000;
	/* ro parameters - controls the "elite" sample size for adaptive learning */
	protected double ro = 0.01;

	/* Number of sampler threads */
	protected int numSamplerThreads = 100;

	/* Stopping criterion parameters 
	 * Counts number of iterations with no change in CE run 
	 * Specifies how many runs to count with have the same outcome to count for
	 * stopping
	 */
	protected int stopParameter;
	/* Counts number of max. iterations CE is allowed to run */
	protected final int MAX_ITERATIONS = 1000;
	
	/* verbose flag */
	public static boolean verbose = true;
	
	/**
	 * A bean that holds the result of CE optimizer
	 */
	public class CEOptimizationResult{

		public final CESample bestSample;
		public final int numIterations;
		public final long time;

		public CEOptimizationResult(CESample bestSample, int numIterations, long time) {
			this.bestSample = bestSample;
			this.numIterations = numIterations;
			this.time = time;
		}

	}


	/**
	 * Creates a new CE optimizer instance
	 * @param sampleSize same size
	 * @param ro adaptive learning parameter
	 * @param stopAfter stopping criterion
	 * @param numSamplerThreads num sampling threads
	 */
	public CrossEntropyOptimizer(int sampleSize, double ro, int stopAfter, int numSamplerThreads) {
		this.ro = ro;
		this.N = sampleSize;
		this.stopParameter = stopAfter;
		this.numSamplerThreads = numSamplerThreads;
	}
	

	/**
	 * Executes the CE optimization
	 * @param objective objective function to optimize
	 * @param model solution space model
	 * @return optimization result
	 */
	public CEOptimizationResult optimize(CEObjective objective, CEModel model) {
		long time = System.currentTimeMillis();
		List<CESample> samples;
		model.maxEntropy();
		/* Keeps the best sample by far */
		CESample bestSample = null;
		int numOfTheSameOutcome = 0;
		int t = 1;// iteration counter
		/* Holds the last calculated gammaT */
		double lastGammaT = -1;

		while (true) {
			// draw samples
			samples = drawRandomSamples(model,N, objective);
			//sort samples by objective value
			sortSamplesByObjective(samples, objective);
			//keep the best intermediate sample
			CESample currentBest = samples.get(samples.size() - 1);
			bestSample = updateBestSample(bestSample, currentBest, objective);
			// calculate gammaT parameter
			double gammaT = calculateGammaT(samples);
			// update model
			model.update(gammaT, samples, objective);
			//check stop criterion
			StopCriterionResult check = isStopReached(objective, currentBest.getValue(),lastGammaT,t,numOfTheSameOutcome);
			if (check.canStop){
				break;
			}else{
				lastGammaT = check.lastGammaT;
				numOfTheSameOutcome = check.numOfTheSameOutcome;
			}

			t++;
		}

		return new CEOptimizationResult(bestSample,t,System.currentTimeMillis() - time);
	}

    /**
     * Draws a random sample of a given size from a given solution space model
     * @param model solution space model
     * @param size sample size
     * @return sample
     */
	public List<CESample> drawRandomSamples(CEModel model, int size, CEObjective objective){
		ArrayList<CESample> sample = new ArrayList<CESample>();
		// draw samples
		SamplerThread[] threads = new SamplerThread[numSamplerThreads];
		for (int i=0;i<numSamplerThreads;i++){
			threads[i] = new SamplerThread(model,size/numSamplerThreads,objective);
			threads[i].start();
		}

		for (int i=0;i<numSamplerThreads;i++){
			try {
				threads[i].join();
			} catch (InterruptedException e) {}

			sample.addAll(threads[i].getSample());
		}

		return sample;
	}


	
	protected double calculateGammaT(List<CESample> sample){
		CESample s = sample.get(((int) Math.floor((1 - ro)* N)));
		return s.getValue();
	}

	protected void sortSamplesByObjective(List<CESample> sample, CEObjective objective) {
		Collections.sort(sample, new CESampleComparator(objective.isMaximized()));
	}


	protected StopCriterionResult isStopReached(CEObjective objective, double localOptima, double lastGammaT, int t, int numOfTheSameOutcome){
		if (t ==  MAX_ITERATIONS) return new StopCriterionResult(true,lastGammaT,numOfTheSameOutcome);
		//algorithm stop condition check
		if (objective.isMaximized() ? localOptima <= lastGammaT : localOptima >= lastGammaT) {
			numOfTheSameOutcome++;
			if (verbose) System.out.print(".");
		} else {//
			numOfTheSameOutcome = 0;
			lastGammaT = localOptima;
			if (verbose) System.out.println(localOptima);
		}

		if (numOfTheSameOutcome == stopParameter){
			return new StopCriterionResult(true,lastGammaT,numOfTheSameOutcome);// simulation finished...
		} else{
			return new StopCriterionResult(false,lastGammaT,numOfTheSameOutcome);
		}
	}



	protected CESample updateBestSample(CESample bestSample, CESample bestIterationSample, CEObjective objective){
		if (bestSample == null || (objective.isMaximized() && bestSample.getValue() < bestIterationSample.getValue()) ||
				(!objective.isMaximized() && bestSample.getValue() > bestIterationSample.getValue())){
			return bestIterationSample;
		}else{
			return bestSample;
		}
	}


	class CESampleComparator implements Comparator<CESample>{

		private boolean max;

		CESampleComparator(boolean max){
			this.max = max;
		}

		@Override
		public int compare(CESample s1, CESample s2) {
			if (s1.getValue() > s2.getValue()) return max ? 1 : -1;
			if (s1.getValue() < s2.getValue()) return max ? -1 : 1;
			else return 0;
		}

	}


	class StopCriterionResult{

		final boolean canStop;
		final double lastGammaT;
		final int numOfTheSameOutcome;

		StopCriterionResult(boolean canStop, double lastGammaT, int numOfTheSameOutcome) {
			this.canStop = canStop;
			this.lastGammaT = lastGammaT;
			this.numOfTheSameOutcome = numOfTheSameOutcome;
		}	

	}


	class SamplerThread extends Thread{
		private ArrayList<CESample> sample;
		private CEModel model;
		private int n;
		private CEObjective objective;

		public SamplerThread(CEModel model, int n, CEObjective objective) {
			super();
			this.model = model;
			this.n = n;
			this.objective = objective;
		}

		public void run(){
			sample = new ArrayList<CESample>();
			for (int i=0;i<n;i++){
				CESample s = model.drawRandomSample();
				s.setValue(objective.evaluate(s));
				sample.add(s);
//				if (verbose){
//					System.out.println("Sample: "+s.getValue()+" (of "+sample.size()+")");
//				}
			}
		}

		public ArrayList<CESample> getSample() {
			return sample;
		}


	}



}

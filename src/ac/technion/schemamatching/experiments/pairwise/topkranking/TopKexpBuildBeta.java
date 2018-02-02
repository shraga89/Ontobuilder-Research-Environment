/**
 * 
 */
package ac.technion.schemamatching.experiments.pairwise.topkranking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import com.infomata.data.DataFile;

import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.OBMaxDelta;
import ac.technion.schemamatching.matchers.secondline.OBThreshold;
import ac.technion.schemamatching.matchers.secondline.OBmwbg;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MappingPrinter;
import ac.technion.schemamatching.statistics.MatchCompetitorDeviation;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictors;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import cern.jet.random.Beta;
import cern.jet.random.Uniform;
import flanagan.analysis.PCA;

/**
 * @author Roee Shraga
 * 
 *
 */
public class TopKexpBuildBeta implements PairWiseExperiment {

	private ArrayList<FirstLineMatcher> flM = new ArrayList<FirstLineMatcher>();
	private ArrayList<SecondLineMatcher> slM = new ArrayList<SecondLineMatcher>(); 
	public int k = 10;
	public String method = "full";
	public Boolean UseExact = false;
	public Boolean allIn1 = false;
	
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.testbed.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		MatchInformation res = null;
		MatchInformation mi = null;
		MatchInformation mi1 = null;
		String instanceDesc = Integer.toString(esp.getID()) + " ";
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		HashMap<MatchInformation, String> matchMatrices = new HashMap<MatchInformation, String>();
		ArrayList<MatchInformation> onlyMatchMatrix = new ArrayList<MatchInformation>();
		System.out.println("Configuration: ");
		System.out.println("k= " + k);
		cern.jet.random.engine.RandomEngine engine = new cern.jet.random.engine.MersenneTwister(); 
		Beta betaPos = new cern.jet.random.Beta(0.6406, 0.2040, engine);
		Beta betaNeg = new cern.jet.random.Beta(2.6452, 16.3139, engine);
		Uniform uni = new cern.jet.random.Uniform(0.0, 1.0, engine);
		mi = esp.getExact();
		double conf = mi.getCandidateOntologyTermsTotal()*mi.getTargetOntologyTermsTotal();
		conf = (mi.getNumMatches()/conf)*3;
		int matixCount = 0;
		MatchInformation matrixToAdd = new MatchInformation(mi.getCandidateOntology(), mi.getTargetOntology());
		ArrayList<Term> cand = mi.getOriginalCandidateTerms();
		ArrayList<Term> targ = mi.getOriginalTargetTerms();
		for (Term tC : cand){
			for (Term tT : targ){
				double temp = mi.getMatchConfidence(tC, tT);
				if (temp == 1.0){
					temp = betaPos.nextDouble();
				}
				else{
//					temp = betaNeg.nextDouble();
					if (uni.nextDouble()<conf){
						temp = betaNeg.nextDouble();
					}
				}
				matrixToAdd.updateMatch(tT, tC, temp);
			}
		}
		matixCount++;
		SecondLineMatcher s = new OBmwbg();
		String second = s.getName();
		System.out.println(second);
//		K2Statistic b3 = new BinaryGolden();
		instanceDesc =  esp.getID() +" Beta " +s.getName()+ ", Top K Matching";
		MappingPrinter mpr = new MappingPrinter();
		mpr.init(instanceDesc, matrixToAdd, esp.getExact());
		evaluations.add(mpr);
		MatchInformation topMatch = s.match(mi);
		MatchInformation matrixToAdd1 = topMatch.clone();
		matchMatrices.put(matrixToAdd1, instanceDesc + " - Top 1");
		onlyMatchMatrix.add(matrixToAdd1);
		matixCount++;
//		b3.init(instanceDesc+" - Top 1", topMatch ,esp.getExact());
//		evaluations.add(b3);
//		MappingPrinter mp1 = new MappingPrinter();
//		mp1.init(instanceDesc+" - Top 1", topMatch, esp.getExact());
//		evaluations.add(mp1);
		MatchInformation curMI = mi;
		MatchInformation nextTopMatch = mi;
		Double topScore = 0.0;
		Match topRemoveMatch = null;
		for (int i=1;i<k;i++){
			ArrayList<Match> curMatches = topMatch.getCopyOfMatches();
			for (Match match2remove : curMatches){
				ArrayList<Match> tempMatches = mi.getCopyOfMatches();
				tempMatches.remove(match2remove);
				curMI.clearMatches();
//				System.out.println(curMI);
				curMI.setMatches(tempMatches);
				if (curMI.getNumMatches()>0){
					curMI = s.match(curMI);	
				}
				Double tempScore = calcScore(curMI);
				if (tempScore>topScore){
					topScore = tempScore;
					topRemoveMatch = match2remove;
					nextTopMatch.clearMatches();
					nextTopMatch.setMatches(curMI.getCopyOfMatches());
				}
			}
			MatchInformation matrixToAdd2 = nextTopMatch.clone(); 
			matchMatrices.put(matrixToAdd2, instanceDesc +" - Top " + (i+1));
			onlyMatchMatrix.add(matrixToAdd2);
			matixCount++;
			topMatch = nextTopMatch;
			topScore = 0.0;
			topRemoveMatch = null;
			}
			if (!allIn1){
				if (UseExact) {
					matchMatrices.put(esp.getExact(), "Exact Match");
					onlyMatchMatrix.add(esp.getExact());
				}
				evaluations = writeData2File(onlyMatchMatrix, matchMatrices, esp, "C:\\ORE\\ontobuilder-research-environment\\topkRanking\\New predictors\\beta\\BetaDistWebFormsOld\\"+method+"Set.txt", mi, evaluations);				
				matchMatrices.clear();
				onlyMatchMatrix.clear();
			}
		return evaluations;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(ac.technion.schemamatching.experiments.OBExperimentRunner, java.util.Properties, java.util.ArrayList, java.util.ArrayList)
	 */
	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		this.flM = flM;
		this.slM = slM;
		if (properties != null){
			if (properties.containsKey("topK"))
			{
				k = Integer.parseInt((String)properties.get("topK"));
			}
			if (properties.containsKey("topKmethod")){
				method = (String)properties.get("topKmethod");
			}
			if (properties.containsKey("UseExact"))
			{
				UseExact = Boolean.parseBoolean((String)properties.get("UseExact"));
			}
			if (properties.containsKey("allIn1"))
			{
				allIn1 = Boolean.parseBoolean((String)properties.get("allIn1"));
				return true;
			}
			System.err.println("OBTopK 2LM could not find the required " +
					"property 'topK' in the property file");
			return false;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
	 */
	public String getDescription() {
		return null;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#summaryStatistics()
	 */
	public ArrayList<Statistic> summaryStatistics() {
		return null;
	}
	
	public Double calcScore(MatchInformation miCalc){
		ArrayList<Match> matches = miCalc.getCopyOfMatches();
//		System.out.println(matches);
		Double sum = 0.0;
		for (Match m : matches){
			sum += m.getEffectiveness();
		}
		Double avg = sum/matches.size();
//		System.out.println(avg);
		return avg;
	}
	
	public ArrayList<Statistic> writeData2File(ArrayList<MatchInformation> onlyMatchMatrix, HashMap<MatchInformation, String> matchMatrices, final ExperimentSchemaPair esp, String filename, MatchInformation flmm, ArrayList<Statistic> evaluations){
		File f = new File(filename);
		if(!f.exists()){
		   createFile(f);
		}
		  try {
		    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
		    // rank matrices
//		    System.out.println(matchMatrices.keySet());
		    List<MatchInformation> keys = onlyMatchMatrix;
//		    System.out.println(keys);
		    Collections.sort(keys, new Comparator<MatchInformation>(){
		        @Override
		        public int compare(MatchInformation m1, MatchInformation m2) {
			    	double precision1 = m1.getPrecision(esp.getExact());
			    	double recall1 = m1.getRecall(esp.getExact());
			    	double trueRank1 = precision1 > 0 || recall1 > 0 ? 2 * (precision1 * recall1) / (precision1 + recall1) : 0;
			    	double precision2 = m2.getPrecision(esp.getExact());
			    	double recall2 = m2.getRecall(esp.getExact());
			    	double trueRank2 = precision2 > 0 || recall2 > 0 ? 2 * (precision2 * recall2) / (precision2 + recall2) : 0;
			    	if (trueRank1 == trueRank2){
				    	if (m1.compareTo(esp.getExact())==0){
				    		trueRank1 = trueRank1+1;
				    	}
				    	if (m2.compareTo(esp.getExact())==0){
				    		trueRank1 = trueRank2+1;
				    	}
			    	}

			    	return Double.compare(trueRank2, trueRank1);
		        }
		    });
		    int rank = keys.size()+1;
//		    int rank = 1;
//		    System.out.println(keys);
		    double lastF = Integer.MAX_VALUE;
//		    double lastF = -1;
		    for (MatchInformation m : keys){
		    	double precision = m.getPrecision(esp.getExact());
		    	double recall = m.getRecall(esp.getExact());
		    	double F = precision > 0 || recall > 0 ? 2 * (precision * recall) / (precision + recall) : 0;
		    	if (lastF > F){
		    		rank--;
//		    		rank = 0;
		    	}
				K2Statistic bin = new BinaryGolden();
				bin.init(matchMatrices.get(m), m ,esp.getExact());
				evaluations.add(bin);
				MappingPrinter mpr = new MappingPrinter();
				mpr.init(matchMatrices.get(m), m, esp.getExact());
//				if ((matchMatrices.get(m).contains(" - Top 1")) && (!matchMatrices.get(m).contains(" - Top 10"))){
//					evaluations.add(mpr);
//				}
//		    	System.out.println(m);
//		    	System.out.println(F);
		    	String line = Integer.toString(rank) + " ";
		    	lastF = F;
		    	line += "qid:" + esp.getID() + " ";
		    	Statistic  preds = new MatrixPredictors();
		    	K2Statistic mcd = new MatchCompetitorDeviation();
		    	preds.init("notImportant", m);
		    	mcd.init("notImportant", flmm, m);
		    	List<String[]> temp = preds.getData();
		    	String[] features = temp.get(0);
		    	String[] mcdFeatures = mcd.getData().get(0);
		    	int j=1;
		    	for (;j<features.length;j++){
//		    		System.out.println(features[j-1]);
		    		line += Integer.toString(j) + ":" + features[j-1] + " ";
		    	}
		    	for (;j<mcdFeatures.length;j++){
//		    		System.out.println(Integer.toString(j)+ features[j-1]);
		    		line += Integer.toString(j) + ":" + mcdFeatures[1] + " ";
		    	}
//		    	for (double val : getEigenValues(5,m.getMatchMatrix())){
//		    		line += Integer.toString(j) + ":" + val + " ";
//		    		j++;
//		    	}
		    	line += "# Precision=";
		    	line += Double.toString(precision);
		    	line += " Recall=";
		    	line += Double.toString(recall);
		    	line += " F1=";
		    	line += Double.toString(F);
		    	line += " Matrix: ";
		    	line += matchMatrices.get(m);
		    	out.println(line);
		    }
		    out.close();
		    return evaluations;
		  }catch (IOException e){
		      e.printStackTrace();
		      return null;
		  }
		
	}
	void createFile(File f){
		  File parentDir = f.getParentFile();
		  try{
		    parentDir.mkdirs(); 
		    f.createNewFile();
		  }catch(Exception e){
		    e.printStackTrace();
		  }
	}
}

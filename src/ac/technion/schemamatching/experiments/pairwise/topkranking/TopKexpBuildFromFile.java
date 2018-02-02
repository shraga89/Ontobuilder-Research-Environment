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

import com.infomata.data.DataFile;

import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.experiments.pairwise.PairWiseExperiment;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.OBMaxDelta;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MappingPrinter;
import ac.technion.schemamatching.statistics.MatchCompetitorDeviation;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.predictors.MatrixPredictors;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import flanagan.analysis.PCA;

/**
 * @author Roee Shraga
 * 
 *
 */
public class TopKexpBuildFromFile implements PairWiseExperiment {

	private ArrayList<FirstLineMatcher> flM = new ArrayList<FirstLineMatcher>();
	private ArrayList<SecondLineMatcher> slM = new ArrayList<SecondLineMatcher>(); 
	public int k = 5;
	public String method = "train";
	public Boolean UseExact = false;
	public Boolean allIn1 = false;
	public String DataSet = "webForms";
	public String sep = "/";
	public String homeDir = "C:/ORE/ontobuilder-research-environment";
	
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.testbed.ExperimentSchemaPair)
	 */
	public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) {
		MatchInformation res = null;
		MatchInformation mi = null;
		MatchInformation mi1 = null;
		String instanceDesc = "";
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		HashMap<MatchInformation, String> matchMatrices = new HashMap<MatchInformation, String>();
		ArrayList<MatchInformation> onlyMatchMatrix = new ArrayList<MatchInformation>();
		System.out.println("Configuration: ");
		System.out.println("k= " + k);
		System.out.println("method= " + method);
		System.out.println("Use exact match in ranking? " + UseExact);
		System.out.println("Use all flm+slm combinations together? " + allIn1);
		for (FirstLineMatcher m : flM)
		{
			String first = m.getName();
			System.out.println(first);
			mi = esp.getSimilarityMatrix(m, false);	
			K2Statistic b2 = new BinaryGolden();
			instanceDesc =  esp.getID() + " " + m.getName();
			OBMaxDelta slm = new OBMaxDelta(0);
			mi1 = slm.match(mi);
//			getEigenValues(5, mi1.getMatchMatrix());
			b2.init(instanceDesc, mi1,esp.getExact());
			evaluations.add(b2);
			MappingPrinter mp = new MappingPrinter();
			mp.init(instanceDesc, mi1, esp.getExact());
			evaluations.add(mp);
			int matixCount = 0;
			MatchInformation matrixToAdd = mi1.clone();
			matchMatrices.put(matrixToAdd, instanceDesc);
			onlyMatchMatrix.add(matrixToAdd);
			matixCount++;
			
//			CREATE S AS A 2LM
			
			String second = "";
			System.out.println(second);
			
			MatchInformation topMatch = getSLMfromFile(first, second, esp.getID(), "C:\\ORE\\ontobuilder-research-environment\\topkRanking\\ceRes\\PO.csv");
			
			
			K2Statistic b3 = new BinaryGolden();
			instanceDesc =  esp.getID()+ " " + m.getName() +" " +second+ ", Top K Matching";
			MatchInformation matrixToAdd1 = topMatch.clone();
//				TRANSFORMATION TO BINARY:
//				MatchInformation matrixToAdd1 = new MatchInformation(esp.getCandidateOntology(), esp.getTargetOntology());
//				for (Match match : topMatch.getCopyOfMatches()){
//					if (match.getEffectiveness()>0.0){
//						match.setEffectiveness(1.0);
//					}
//					matrixToAdd1.updateMatch(match.getTargetTerm(), match.getCandidateTerm(), match.getEffectiveness());
//				}
			matchMatrices.put(matrixToAdd1, instanceDesc + " - Top 1");
			onlyMatchMatrix.add(matrixToAdd1);
			matixCount++;
			b3.init(instanceDesc+" - Top 1", topMatch ,esp.getExact());
			evaluations.add(b3);
			MappingPrinter mp1 = new MappingPrinter();
			mp1.init(instanceDesc+" - Top 1", topMatch, esp.getExact());
			evaluations.add(mp1);
			MatchInformation curMI = mi;
			MatchInformation nextTopMatch = mi;
			Double topScore = 0.0;
			@SuppressWarnings("unused")
			Match topRemoveMatch = null;
			for (int i=1;i<k;i++){
				ArrayList<Match> curMatches = topMatch.getCopyOfMatches();
				for (Match match2remove : curMatches){
					ArrayList<Match> tempMatches = mi.getCopyOfMatches();
					tempMatches.remove(match2remove);
					curMI.clearMatches();
//					System.out.println(curMI);
					curMI.setMatches(tempMatches);
//					if (curMI.getNumMatches()>0){
//						curMI = s.match(curMI);	
//					}
					Double tempScore = calcScore(curMI);
					if (tempScore>topScore){
						topScore = tempScore;
						topRemoveMatch = match2remove;
						nextTopMatch.clearMatches();
						nextTopMatch.setMatches(curMI.getCopyOfMatches());
					}
				}
				K2Statistic bin = new BinaryGolden();
				bin.init(instanceDesc +" - Top " + (i+1), nextTopMatch ,esp.getExact());
				evaluations.add(bin);
				MappingPrinter mpr = new MappingPrinter();
				mpr.init(instanceDesc +" - Top " + (i+1), nextTopMatch, esp.getExact());
				evaluations.add(mpr);
//				System.out.println(matchMatrices.put(nextTopMatch, instanceDesc +" - Top " + (i+1)));
				MatchInformation matrixToAdd2 = nextTopMatch.clone(); 
//				TRANSFORMATION TO BINARY:
//				MatchInformation matrixToAdd2 = new MatchInformation(esp.getCandidateOntology(), esp.getTargetOntology());
//				for (Match match : nextTopMatch.getCopyOfMatches()){
//					if (match.getEffectiveness()>0.0){
//						match.setEffectiveness(1.0);
//					}
//					matrixToAdd2.updateMatch(match.getTargetTerm(), match.getCandidateTerm(), match.getEffectiveness());
//				}
				matchMatrices.put(matrixToAdd2, instanceDesc +" - Top " + (i+1));
				onlyMatchMatrix.add(matrixToAdd2);
//				System.out.println(onlyMatchMatrix.get(i+1));
//				System.out.println(nextTopMatch);
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
//					writeData2File(onlyMatchMatrix, matchMatrices, esp, homeDir+sep+"topkRanking"+sep+"New predictors"+sep+DataSet+sep+first+" + "+ second+" "+ DataSet+sep+method+"Set.txt", mi);
					writeData2File(onlyMatchMatrix, matchMatrices, esp, "C:/ORE/ontobuilder-research-environment/test/"+method+"Set.txt", mi);
					matchMatrices.clear();
					onlyMatchMatrix.clear();
				}
		}
		if (allIn1){
			if (UseExact) {
				matchMatrices.put(esp.getExact(), "Exact Match");
				onlyMatchMatrix.add(esp.getExact());
			}
			writeData2File(onlyMatchMatrix, matchMatrices, esp, homeDir+sep+"topkRanking"+sep+"New predictors"+sep+DataSet+sep+ DataSet+sep+method+"Set.txt", mi);
		}
		return evaluations;
	}

	private MatchInformation getSLMfromFile(String first, String string, int i, String string2) {
		
		return null;
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
			if (properties.containsKey("DataSet"))
			{
				DataSet = (String)properties.get("DataSet");
			}
			if (properties.containsKey("sep"))
			{
				sep = (String)properties.get("sep");
			}
			if (properties.containsKey("homeDir"))
			{
				homeDir = (String)properties.get("homeDir");
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
	
	public void writeData2File(ArrayList<MatchInformation> onlyMatchMatrix, HashMap<MatchInformation, String> matchMatrices, final ExperimentSchemaPair esp, String filename, MatchInformation flmm){
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
		    		System.out.println(Integer.toString(j) + features[j-1]);
		    		line += Integer.toString(j) + ":" + features[j-1] + " ";
		    	}
		    	for (;j<mcdFeatures.length;j++){
		    		System.out.println(Integer.toString(j)+ features[j-1]);
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
		  }catch (IOException e){
		      e.printStackTrace();
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

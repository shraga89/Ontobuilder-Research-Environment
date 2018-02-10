package ac.technion.schemamatching.experiments.pairwise.topkranking;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import com.hp.hpl.jena.util.FileUtils;

import au.com.bytecode.opencsv.CSVReader;
import ciir.umass.edu.eval.Evaluator;
import ciir.umass.edu.features.FeatureManager;

public class topKRank {
	public static void build_model(String folder){
		String[] args = new String[14];
		args[0] = "-train";
		args[1] = folder + "\\trainSet.txt";
		args[2] = "-test";
		args[3] = folder + "\\testSet.txt";
		args[4] = "-validate";
		args[5] = folder + "\\validSet.txt";
		args[6] = "-ranker";
		args[7] = "6";
		args[8] = "-metric2t";
		args[9] = "ERR@3";
		args[10] = "-metric2T";
		args[11] = "ERR@3";
		args[12] = "-save";
		args[13] = "mymodel.txt";
//		args[14] = "-tvs";
//		args[15] = "0.8";
//		args[14] = "-feature";
//		args[15] = folder + "\\features.txt";
		
		Evaluator.main(args);
	}
	
	public static void evaluate_model(String folder, String model, Integer relLevels){
		String[] args = new String[8];
		args[0] = "-load";
		args[1] = folder + "\\models\\" + model;
//		args[0] = "-idv";
//		args[1] = folder + "\\output\\baseline.err3.txt";
		args[2] = "-test";
		args[3] = folder + "\\testSet.txt";
		args[4] = "-metric2T";
		args[6] = "";
		args[7] = "";
		ArrayList<String> eval = new ArrayList<String>();
		eval.add("err");
		eval.add("ndcg");
		ArrayList<String> ks = new ArrayList<String>();
//		ks.add("5");
		ks.add("10");
		for (String e : eval){
			for (String k : ks){
				args[5] = e + "@" + k;
				if (e.equals("err")){
					args[6] = "-gmax";
					args[7] = relLevels.toString();
				}
				try {
					Evaluator.main(args);
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
	}
	
	public static void Comparing_models(String folder){
		List<String> results = new ArrayList<String>();
		String[] args2 = new String[6];
//		args[0] = "-load";
//		args[1] = "mymodel.txt";
		args2[0] = "-idv";
		args2[1] = folder + "\\output\\baseline.err3.txt";
		args2[2] = "-test";
		args2[3] = folder + "\\fullSet.txt.shuffled";
		args2[4] = "-metric2T";
		args2[5] = "NDCG@5";
		Evaluator.main(args2);
		File[] files = new File(folder + "\\models").listFiles(); 
		for (File file : files) {
		    if (file.isFile()) {
		        results.add(file.getName());
		    }
		}
		String[] args = new String[8];
		args[0] = "-idv";
		args[2] = "-test";
		args[3] = folder + "\\fullSet.txt.shuffled";
		args[4] = "-load";
		args[6] = "-metric2T";
		args[7] = "NDCG@5";
		for (String model: results){
			args[1] = folder + "\\output\\" + model + ".txt";
			args[5] = folder + "\\models\\" + model;
			Evaluator.main(args);
		}
		String[] args1 = new String[4];
		args1[0] = "-all";
		args1[1] = folder + "\\output\\";
		args1[2] = "-base";
		args1[3] = "baseline.err3.txt";
		Analyzer.main(args1);
		
	}
	
	public static void build_model_5_fold(String folder, Integer relLevels){
		String[] args = new String[28];
		args[0] = "-train";
		args[1] = folder + "\\fullSet.txt.shuffled";
		args[2] = "-ranker";
		args[3] = "6";
		args[4] = "-kcv";
		args[6] = "-kcvmd";
		args[7] = folder + "\\models\\";
		args[8] = "-kcvmn";
		args[10] = "-tvs";
		args[11] = "0.7";
		args[12] = "-metric2t";
		args[14] = "-metric2T";
		args[16] = "";
		args[17] = "";
		args[18] = "-tree";
		args[19] = "1000";
		args[20] = "-leaf";
		args[21] = "15";
		args[22] = "-estop";
		args[23] = "500";
		args[24] = "-mls";
		args[25] = "5";
		args[26] = "-norm";
		args[27] = "linear";
//		args[28] = "-feature";
//		args[29] = folder + "\\new_features.txt";
		ArrayList<String> numOfFolds = new ArrayList<String>();
//		numOfFolds.add("02");
		numOfFolds.add("05");
//		numOfFolds.add("10");
		ArrayList<String> eval = new ArrayList<String>();
		eval.add("err");
		eval.add("ndcg");
//		eval.add("P");
		ArrayList<String> ks = new ArrayList<String>();
//		ks.add("1");
//		ks.add("03");
//		ks.add("04");
//		ks.add("05");
//		ks.add("07");
//		ks.add("09");
		ks.add("10");
//		ks.add("11");
		for (String f : numOfFolds){
			args[5] = f;
			for (String e1 : eval){
				for (String e2 : eval){
					for (String k : ks){
						args[13] = e1 + "@" + k;
						args[15] = e2 + "@" + k;
						args[9] = e1 + "_" + e2 + "_" + k + "_" + f ;
//						if ((e1 == e2) && (e2 == "err") && (k=="5")) continue; 
						if (e2.equals("err") || e1.equals("err")){
							args[16] = "-gmax";
							args[17] = relLevels.toString();
						}
						Evaluator.main(args);
						try {
//							if (e2.equals("err") || e1.equals("err")){
//								args[16] = "-gmax";
//								args[17] = relLevels.toString();
//							}
//							Evaluator.main(args);
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
		}

	}
	
	public static void shuffle_data(String folder){
		String[] args = new String[5];
		args[0] = "-input";
		args[1] = folder + "\\fullSet.txt";
		args[2] = "-output";
		args[3] = folder;
		args[4] = "-shuffle";
		FeatureManager.main(args);
	}
	
	public static void shuffle_data_new(String folder){
		String[] args = new String[5];
		args[0] = "-input";
		args[1] = folder + "\\fullSet.txt";
		args[2] = "-output";
		args[3] = folder;
		args[4] = "-shuffle";
		FeatureManager.main(args);
	}
	
	public static void obtain_test_data(String folder, String model, Integer relLevels){
		String e1 = "";
		String e2 = "";
		Integer len = model.length();
		String f = model.substring(len-2, len);
		String k = model.substring(len-5, len-3);
//		if (model.substring(3,4).equals("e")){
//			e1 = "err";
//			if (model.substring(7,8).equals("e")){
//				e2 = "err";
//			}
//			else{
//				e2 = "ndcg";
//			}
//		}
//		else{
//			e1 = "ndcg";
//			if (model.substring(8,9).equals("e")){
//				e2 = "err";
//			}
//			else{
//				System.out.println(model);
//				e2 = "ndcg";
//			}
//		}
//		
//		System.out.println(e1 + "_" + e2 + "_" + k + "_" + f);
//		String[] args1 = new String[22];
//		args1[0] = "-train";
//		args1[1] = folder + "\\fullSet.txt.shuffled";
//		args1[2] = "-ranker";
//		args1[3] = "6";
//		args1[4] = "-kcv";
//		args1[6] = "-kcvmd";
//		args1[7] = folder;
//		args1[8] = "-kcvmn";
//		args1[10] = "-tvs";
//		args1[11] = "0.7";
//		args1[12] = "-metric2t";
//		args1[14] = "-metric2T";
//		args1[16] = "";
//		args1[17] = "";
//		args1[18] = "-tree";
//		args1[19] = "1000";
//		args1[20] = "-leaf";
//		args1[21] = "20";
//		args1[5] = f;
//		args1[13] = e1 + "@" + k;
//		args1[15] = e2 + "@" + k;
//		args1[9] = e1 + "_" + e2 + "_" + k + "_" + f ;
//		if (e2.equals("err") || e1.equals("err")){
//			args1[16] = "-gmax";
//			args1[17] = relLevels.toString();
//		}
//		Evaluator.main(args1);
		String[] args = new String[6];
		args[0] = "-input";
		args[1] = folder + "\\fullSet.txt.shuffled";
		args[2] = "-output";
		args[3] = folder;
		args[4] = "-k";
		args[5] = f;
		FeatureManager.main(args);
	}
	
	public static void re_rank_model(String folder, String model, String test){
		String[] args = new String[6];
		args[0] = "-load";
		args[1] = folder + "\\models\\" + model;
		args[2] = "-rank";
		args[3] = folder + "\\" +test;
		args[4] = "-score";
		args[5] = folder + "\\myscorefile.txt";
		System.out.println(model);
		Evaluator.main(args);
	}
	
	public static void rank_score_file(String folder, String model, String test) throws FileNotFoundException{
		FileReader input;
		try {
			input = new FileReader(folder + "\\myscorefile.txt");
			BufferedReader bufRead = new BufferedReader(input);
			String myLine = null;
			HashMap<Integer, HashMap<String, Double>> results = new HashMap<Integer,HashMap<String, Double>>();
			int count = 0;
			HashMap<Integer, Double> top0res = new HashMap<Integer, Double>();
			while ( (myLine = bufRead.readLine()) != null){
				String[] fullLine = myLine.split("\t");
				if (Integer.parseInt(fullLine[1]) == 0) top0res.put(Integer.parseInt(fullLine[0]), Double.parseDouble(fullLine[2]));
				if (results.containsKey(Integer.parseInt(fullLine[0]))){
					HashMap<String, Double> temp = results.get(Integer.parseInt(fullLine[0]));
					temp.put(fullLine[1], Double.parseDouble(fullLine[2]));
					results.put(Integer.parseInt(fullLine[0]),temp);
				}
				else{
					results.put(Integer.parseInt(fullLine[0]), new HashMap<String, Double>());
				}
			}
//			System.out.println(count);
			HashMap<Integer, TreeMap<String, Double>> sortedResults = new HashMap<Integer,TreeMap<String, Double>>();
			for (Integer key : results.keySet()){
//				ValueComparator bvc = new ValueComparator(results.get(key));
//				TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
				TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(results.get(key));
				sorted_map.putAll(results.get(key));
				sortedResults.put(key, sorted_map);
			}
			
//			System.out.println(sortedResults);
			for (Integer p: top0res.keySet()){
//				System.out.println(top0res.get(p));
//				System.out.println(sortedResults.get(p).firstEntry().getValue());
				if (top0res.get(p)>=sortedResults.get(p).firstEntry().getValue()){
					count++;
				}
			}
//			System.out.println(count);
			input.close();
			File file = new File(folder + "\\myscorefile.txt");
			file.delete();
			write2newFile(sortedResults, folder, model, test);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@SuppressWarnings("resource")
	public static void write2newFile(HashMap<Integer, TreeMap<String, Double>> sortedResults, String folder, String model, String test) throws IOException{
		HashMap<Integer, String> best4pair = new HashMap<Integer, String>();
		HashMap<Integer, TreeMap<String, Double>> smoothedSortedResults = smoothResults(sortedResults, folder + "\\" + test);
//		System.out.println(smoothedSortedResults);
		for (Integer key : smoothedSortedResults.keySet()){
			TreeMap<String, Double> temp = smoothedSortedResults.get(key);
//			System.out.println(key + "\t" + temp);
			String best = temp.firstKey();
//			Double bestValue = temp.firstEntry().getValue();
			best4pair.put(key, best);
		}
//		System.out.println(best4pair);
//		parse test file (to know which is which)
		HashMap<String, String> num2mat = new HashMap<String, String>();
		FileReader input = new FileReader(folder + "\\" + test);
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;
		String lastqid = "";
		int i=0;
		while ( (myLine = bufRead.readLine()) != null){
			String[] fullLine = myLine.split(" ");
			String qid = fullLine[1].split(":")[1];
			int best4qid = 0;
			best4qid = Integer.parseInt(best4pair.get(Integer.parseInt(qid)));
//			System.out.println(best4qid);
//			System.out.println(i);
			if (best4qid == i){
				String matName = myLine.substring(myLine.lastIndexOf("Matrix:")+8, myLine.length());
				num2mat.put(qid, matName);
			}
			if (!qid.equals(lastqid)){
				i=0;
			}
			lastqid = qid;
			i++;
		}
//		System.out.println(num2mat);
//		parse csv file (to know P,R,F measures)
		File dir = new File(folder);
	/////add binary golden file of test set!!!
		File[] matches = dir.listFiles(new FilenameFilter()
		{
		  public boolean accept(File dir, String name)
		  {
		     return name.endsWith("Binary Golden Statistic.csv");
		  }
		});
		CSVReader reader = new CSVReader(new FileReader(matches[0]));
		FileWriter origWriter = new FileWriter(folder + "\\Original Binary Golden Statistic "+model+".csv");
		List<String[]> myEntries = reader.readAll();
		reader.close();
		String key = null;
		int head=0;
		List<String[]> res = new ArrayList<String[]>();
		for (String[] l : myEntries){
			key = l[0].split(" ")[0];
			if (head == 0){
				res.add(l);
				head ++;
				continue;
			}
			if (num2mat.containsKey(key)){
				if (l[0].endsWith("Top K Matching - Top 1")){
					for (String s : l){
						origWriter.write(s.replace(',', ' ') + ",");
					}
					origWriter.write("\n");
				}
				if (l[0].equals(num2mat.get(key))){
					res.add(l);
				}
			}
		}
		origWriter.flush();
		origWriter.close();
		FileWriter writer = new FileWriter(folder + "\\L2R Binary Golden Statistic"+model+".csv");
		for (String[] l : res){
			for (String s : l){
				writer.write(s.replace(',', ' ') + ",");
//				System.out.print(s + ",");
			}
			writer.write("\n");
//			System.out.println();
		}
		writer.flush();
		writer.close();
	}
	
	private static HashMap<Integer, TreeMap<String, Double>> smoothResults(HashMap<Integer, TreeMap<String, Double>> sortedResults, String testSet) throws IOException {
		HashMap<Integer, TreeMap<String, Double>> smoothRes = new HashMap<Integer, TreeMap<String, Double>>();
		HashMap<String, String> features = getFeatures(testSet);
		System.out.println("Smoothing");
		for (Integer key : sortedResults.keySet()){
			TreeMap<String, Double> temp = sortedResults.get(key);
			TreeMap<String, Double> tempTree = new TreeMap<String, Double>(); 
			Double max = Collections.max(temp.values());
			Double min = Collections.min(temp.values());
			if (min == max) { 
				min = 0.0;
				max = 1.0;
			}
//			System.out.println(key);
			for (Entry<String, Double> entry : temp.entrySet()){
				Entry<String, Double> tempEntry = entry;
				String alpha = features.get(key+"-"+entry.getKey());
//				Normalize by former Ranking
//				System.out.println("avg:"+"\t"+Double.parseDouble(alpha.split("13:")[1].split(" ")[0]));
//				System.out.println("place:"+"\t"+entry.getKey());
//				System.out.println("lambda:"+"\t"+entry.getValue());
				Double alphaVal = 0.0;
//				if (!entry.getKey().equals("0")){
//					if (Math.random() > 0.3){
//						alphaVal = Double.parseDouble(alpha.split("3:")[1].split(" ")[0])/*/Double.parseDouble(entry.getKey())*/;
//					}
//				}
				//CESM
//				if (!entry.getKey().equals("0")){
//				alphaVal = 0.5*Double.parseDouble(alpha.split("7:")[1].split(" ")[0]);
//				alphaVal += 0.5*Double.parseDouble(alpha.split("3:")[1].split(" ")[0]);
//				}

//				
//				if (!entry.getKey().equals("0")){
//					alphaVal = Double.parseDouble(alpha.split("13:")[1].split(" ")[0])/*/(Double.parseDouble(entry.getKey()))*/;
//					if (entry.getKey().equals("1")){
//						if (Double.parseDouble(alpha.split("1:")[1].split(" ")[0]) > 1.0){
//							alphaVal += Double.parseDouble(alpha.split("1:")[1].split(" ")[0]);	
//						}
//					}
//				}
//				else{
//					alphaVal = -100.0;
//				}
//				alphaVal = Double.parseDouble(alpha.split("13:")[1].split(" ")[0]);///(Double.parseDouble(entry.getKey()));
				Double lambdaVal = entry.getValue();
//				System.out.println("conf:"+"\t"+alphaVal);
//				System.out.println("lambda:"+"\t"+lambdaVal);
				lambdaVal = (lambdaVal-min)/(max-min);
//				System.out.println("lambdaNorm:"+"\t"+ lambdaVal);
				
				//old preds
//				if (!alpha.split("Matrix:")[1].contains("Top K Matching")){
//					alphaVal = entry.getValue();	
//				}
//				else{
//					alphaVal = 0.7*entry.getValue() + 0.3*alphaVal;	
//				}
				
				
//				if (Math.random() > 0.8){
//					if (alpha.contains("13:")){
//						alphaVal = Double.parseDouble(alpha.split("13:")[1].split(" ")[0]);
//					}
//					else{
//						alphaVal = Double.parseDouble(alpha.split("3:")[1].split(" ")[0]);						
//					}
//
//				}
//				alphaVal = -Double.parseDouble(alpha.split("15:")[1].split(" ")[0]);
				alphaVal = 0.7*lambdaVal + 0.3*alphaVal;
//				alphaVal = lambdaVal*alphaVal;
//				System.out.println("newValue:"+"\t"+alphaVal);
//				System.out.println(alphaVal);
				tempEntry.setValue(alphaVal);
				tempTree.put(tempEntry.getKey(), tempEntry.getValue());
			}
//			System.out.println(tempTree);
			smoothRes.put(key, tempTree);
		}
		for (Integer key : smoothRes.keySet()){
			ValueComparator bvc = new ValueComparator(smoothRes.get(key));
			TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
			sorted_map.putAll(smoothRes.get(key));
			smoothRes.put(key, sorted_map);
		}
		return smoothRes;
	}


	private static HashMap<String, String> getFeatures(String testSet) throws IOException {
		FileReader input = new FileReader(testSet);
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;
		String lastqid = "";
		Integer i = 1;
		HashMap<String, String> featureMap = new HashMap<String, String> ();
		while ( (myLine = bufRead.readLine()) != null){
			String qid = myLine.split("qid:")[1].split(" ")[0];
//			System.out.println(qid);
			if (!qid.equals(lastqid)){
				i=1;
			}
			else{
				featureMap.put(qid + "-"+ i.toString(), myLine.split("qid:")[1]);
				i++;
			}
			lastqid = qid;
		}
		return featureMap;
	}

	public static void main(String[] args) throws IOException {
//		Kinfluenceresults\\webForms\\
		File[] files = new File("C:\\ORE\\ontobuilder-research-environment\\topkRanking\\New predictors\\OAEI\\new\\").listFiles();
		PrintStream stdout = System.out;
		for (File f : files){
			if (f.isDirectory()) {
				System.setOut(stdout);
				System.out.println(f);
//				bootstrapViaResampling(f.getAbsolutePath());
				Integer relLevels = changeLevels(f.getAbsolutePath());
//				new File(f+"\\output").mkdir();
//				new File(f+"\\models").mkdir();
//				for (File file: new File(f+"\\output").listFiles()) if (!file.isDirectory()) file.delete();
//				for (File file: new File(f+"\\models").listFiles()) if (!file.isDirectory()) file.delete();
//				shuffle_data_new(f.getAbsolutePath());
//				build_model_5_fold(f.getAbsolutePath(), relLevels);
//				System.out.println("Comparing models just started");
//				System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(f + "/comp.tsv")), true));
////				deleteSmallModels(f+"\\models");
//				Comparing_models(f.getAbsolutePath());
				System.setOut(stdout);
				String bestModel = extract_best_model(f.getAbsolutePath(), f + "/comp.tsv");
//				String bestModel = "f2.err_err_03_02";
				obtain_test_data(f.getAbsolutePath(), bestModel, relLevels);
				for (Integer i=1; i<=5; i++){
					bestModel = bestModel.substring(0, 1)+i.toString()+ bestModel.substring(2, bestModel.length());
					String testFileForThisModel = bestModel.substring(0, 1)+ i.toString() + ".test.fullSet.txt.shuffled";
					System.out.println(bestModel);
					System.out.println(testFileForThisModel);
//					System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(f + "/results.tsv")), true));
//					evaluate_model(f.getAbsolutePath(), bestModel, relLevels);
//					System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(f + "/results_orig.tsv")), true));
//					evaluate_given_list(f.getAbsolutePath(), "old_rank_model" , relLevels);
//					evaluate_given_list(f.getAbsolutePath(), "cem_rank_model" , relLevels);
					re_rank_model(f.getAbsolutePath(), bestModel, testFileForThisModel);
					try {
//						System.out.println("New Ranking!");
						rank_score_file(f.getAbsolutePath(), bestModel, testFileForThisModel);
//						break;
					} catch (FileNotFoundException e) {
						 //TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
//				String testFileForThisModel = bestModel.substring(0, 2) + ".test.fullSet.txt.shuffled";
//				File file = new File(f + "\\testSet.txt");
//				file.delete();
//				Files.copy(new File(f + "\\" + testFileForThisModel). toPath(),new File(f + "\\testSet.txt").toPath());
////				Files.copy(new File(f + "\\fullSet.txt").toPath(),new File(f + "\\testSet.txt").toPath());
//				System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(f + "/results.tsv")), true));
//				evaluate_model(f.getAbsolutePath(), bestModel, relLevels);
//				System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(f + "/results_orig.tsv")), true));
//				evaluate_given_list(f.getAbsolutePath(), "old_rank_model" , relLevels);
//				evaluate_given_list(f.getAbsolutePath(), "cem_rank_model" , relLevels);
//				re_rank_model(f.getAbsolutePath(), bestModel);
//				try {
////					System.out.println("New Ranking!");
//					rank_score_file(f.getAbsolutePath(), bestModel);
////					break;
//				} catch (FileNotFoundException e) {
//					 //TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}
//		String f = "C:\\ORE\\ontobuilder-research-environment\\topkRanking\\Old predictors\\webForms\\Ontobuilder Term Match + Ontobuilder MWBG webForms";
//		new File(folder+"\\output").mkdir();
//		new File(folder+"\\models").mkdir();
//		shuffle_data_new(folder);
//		build_model_5_fold(folder);
//		Comparing_models(folder);
//		Files.copy(new File(folder + "\\fullSet.txt.shuffled"). toPath(),new File(folder + "\\testSet.txt").toPath());
//		String bestModel = extract_best_model(f + "/comp.tsv");
//		re_rank_model(f, bestModel);
//		try {
//			rank_score_file(f, bestModel);
//		} catch (FileNotFoundException e) {
//			 //TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private static void evaluate_given_list(String folder, String model, Integer relLevels) {
		String[] args = new String[8];
		args[0] = "-load";
		args[1] = folder + "\\" + model;
		args[2] = "-test";
		args[3] = folder + "\\testSet.txt";
		args[4] = "-metric2T";
		args[6] = "";
		args[7] = "";
		ArrayList<String> eval = new ArrayList<String>();
		eval.add("err");
		eval.add("ndcg");
		ArrayList<String> ks = new ArrayList<String>();
//		ks.add("5");
		ks.add("10");
		for (String e : eval){
			for (String k : ks){
				args[5] = e + "@" + k;
				if (e.equals("err")){
					args[6] = "-gmax";
					args[7] = relLevels.toString();
				}
				try {
					Evaluator.main(args);
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
		String[] args1 = new String[6];
		args1[0] = "-load";
		args1[1] = folder + "\\" + model;
		args1[2] = "-rank";
		args1[3] = folder + "\\testSet.txt";
		args1[4] = "-score";
		args1[5] = folder + "\\score "+ model + ".txt";
		System.out.println(model);
		Evaluator.main(args1);
	}

	@SuppressWarnings("unchecked")
	private static void bootstrapViaResampling(String folder) throws IOException {
		FileReader input = new FileReader(folder + "\\fullSet.txt");
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;
		String lastqid = "";
		HashMap<String, ArrayList<String>> qMap = new HashMap<String, ArrayList<String>>();
		ArrayList<String> lines = new ArrayList<String>();
		while ( (myLine = bufRead.readLine()) != null){
			String qid = myLine.split("qid:")[1].split(" ")[0];
			if (lastqid.equals("")){
				lastqid = qid;
			}
			if (!qid.equals(lastqid)){
				qMap.put(lastqid, (ArrayList<String>) lines.clone());
				lines.clear();
			}
			lines.add(myLine);
			lastqid = qid;
		}
		qMap.put(lastqid, (ArrayList<String>) lines.clone());
		bufRead.close();		
		new File(folder + "\\oldfullSet1.txt").delete();
		Files.copy(new File(folder + "\\fullSet.txt"). toPath(),new File(folder + "\\oldfullSet1.txt").toPath());
//		new File(folder + "\\fullSet.txt").delete();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(folder + "\\fullSet.txt", true)));
		Random random= new Random();
		List<String> keys = new ArrayList<String>(qMap.keySet());
		for (Integer i=0; i<100; i++){
			String randomKey = keys.get(random.nextInt(keys.size()));
			ArrayList<String> value = qMap.get(randomKey);
//			System.out.println(randomKey);
			for (String v: value){
				out.println(v.replace(randomKey, randomKey + i.toString()));
			}
		}
		out.close();
		
	}

	private static int changeLevels(String folder) throws IOException {
		FileReader input = new FileReader(folder + "\\fullSet.txt");
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;
		int minLevel = 500;
		List<String> lines = new ArrayList<String>();
		while ( (myLine = bufRead.readLine()) != null){
//			if (!myLine.contains("13:")){
//				System.out.println(myLine);
//			}
			String[] fullLine = myLine.split(" ");
			int currLevel = Integer.parseInt(fullLine[0]);
			if (currLevel<minLevel){
				minLevel = currLevel;
			}
			if (currLevel<11){
				lines.add(myLine);
			}
		}
		bufRead.close();
		new File(folder + "\\oldfullSet.txt").delete();
		Files.copy(new File(folder + "\\fullSet.txt"). toPath(),new File(folder + "\\oldfullSet.txt").toPath());
		new File(folder + "\\fullSet.txt").delete();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(folder + "\\fullSet.txt", true)));
		Integer levels = 13;
		for (String line : lines){
			String[] fullLine = line.split(" qid");
			if (levels == 13){
				levels = Integer.parseInt(fullLine[0]) - minLevel;
//				continue;
			}
			if (Integer.parseInt(fullLine[0]) == levels){
//				continue;
			}
			fullLine[0] = Integer.toString(Integer.parseInt(fullLine[0]) - minLevel);
			out.println(fullLine[0] + " qid" + fullLine[1]);
		}
		out.close();
		levels += 1;
		return levels;
	}

	private static void deleteSmallModels(String modelsLoc) {
		File[] models = new File(modelsLoc).listFiles();
		for (File m : models){
			double size = (m.length()/1024);
			if (size<10){
				m.delete();
			}
		}
		
	}

	private static String extract_best_model(String folder, String comparingFile) throws IOException {
		FileReader input = new FileReader(comparingFile);
		@SuppressWarnings("resource")
		BufferedReader bufRead = new BufferedReader(input);		
		double bestRes = 0.0;
		String bestModel = "";
		String myLine = null;
		while ( (myLine = bufRead.readLine()) != null){
			String[] fullLine = myLine.split("\t");
			if ((fullLine.length<=1) || (fullLine[0].contains("System") || !fullLine[1].matches("-?\\d+(\\.\\d+)?"))){
				if ((fullLine.length<1) || (fullLine[0].contains("Detailed break down"))){
					break;
				}
			}
			else{
				double currRes = Double.parseDouble(fullLine[1]);
				String currModel = fullLine[0];
				if ((!currModel.contains("baseline.err3.txt [baseline]") && (currRes > bestRes))){
					bestRes = currRes;
					bestModel = currModel;
				}
			}
		}
		return bestModel.replace(".txt", "");
	}
}

class ValueComparator implements Comparator<String> {
    Map<String, Double> base;

    public ValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with
    // equals.
    public int compare(String a, String b) {
//    	System.out.println(a + "---" +b);
//    	System.out.println(base.get(a) + "---" +base.get(b));
        if (base.get(a) > base.get(b)) {
            return -1;
        }
        else if (base.get(a).floatValue() == base.get(b).floatValue()){
        	if (Integer.parseInt(a) > Integer.parseInt(b)){
        		return 1;
        	}
        	else{
        		return -1;
        	}
        }
        else {
        	return 1;
//			if (Math.random() > 0.2
//					
//					){
//				return 1;
//			}
//			else{
//				return -1;
//			}
        } // returning 0 would merge keys
    }
}

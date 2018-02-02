package ac.technion.schemamatching.experiments.pairwise;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.firstline.SimMatrixShell;
import ac.technion.schemamatching.matchers.secondline.BehavioralMatcher;
import ac.technion.schemamatching.matchers.secondline.OBDominants;
import ac.technion.schemamatching.matchers.secondline.OBMaxDelta;
import ac.technion.schemamatching.matchers.secondline.OBThreshold;
import ac.technion.schemamatching.matchers.secondline.OBmwbg;
import ac.technion.schemamatching.matchers.secondline.SLMList;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.BinaryGolden;
import ac.technion.schemamatching.statistics.K2Statistic;
import ac.technion.schemamatching.statistics.MappingPrinter;
import ac.technion.schemamatching.statistics.MatchDistance;
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.statistics.VerboseBinaryGolden;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;
import ac.technion.schemamatching.testbed.OREDataSetEnum;
/**
 * Compares performance of a weighted ensemble of Term-HM with Term-Behavioral2LM(HM)
 * Calcs precision, recall and L2 similarity measures 
 * @author Tomer Sagi
 *
 */
public class Behavioral2LMensemble implements PairWiseExperiment {

		private SimMatrixShell sms = new SimMatrixShell();
		private String pairPath = "";
		HashMap<Integer,ArrayList<File>> fileMap = new HashMap<Integer,ArrayList<File>>();
		private String slopePath = "";
		Map<String,Float> slope=new HashMap<>();
		Map<String,Float> intersect =new HashMap<>();
		private ArrayList<FirstLineMatcher> flM;
		private Properties properties;

		/*
		 * (non-Javadoc)
		 * @see ac.technion.schemamatching.experiments.MatchingExperiment#runExperiment(ac.technion.schemamatching.experiments.ExperimentSchemaPair)
		 */
		public ArrayList<Statistic> runExperiment(ExperimentSchemaPair esp) { 
			ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
			
			for (File f : fileMap.get(esp.getID()))
			{
				//Match by importing results from human matcher csv files
				MatchInformation hmi = null;
				if (!sms.setPath(f.getParent() ,f.getName()))
				{
					System.err.println("No file path found for pair:" + esp.getID());
					return evaluations;
				}
				System.out.println("Starting " + f.getName());
				sms.setImporter(OREDataSetEnum.Thalia.getMatchImp());
				hmi = sms.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
				//Calculate NBprecision, NBrecall prior to 2LM application
				String participant = f.getName().split("\\.")[0].split("_")[0];
				String iDesc = "" + esp.getID()+" , "+ participant;
//				if (participant.equals("0010") || participant.equals("1010") || participant.equals("3028"))
//					continue;
				if (participant.equals("1010") || participant.equals("3028"))
					continue;
				String instanceDesc = iDesc  + ", Human Matcher";
//				K2Statistic nb = new NBGolden();
//				nb.init(instanceDesc, hmi, esp.getExact());
//				evaluations.add(nb);
				
//				K2Statistic md = new MatchDistance();
//				md.init(instanceDesc, hmi,esp.getExact());
//				evaluations.add(md);
				
				instanceDesc = iDesc  + ", Human Matcher - 2LM applied";
				SecondLineMatcher b2LM = new BehavioralMatcher();
				b2LM.init(properties);
				MatchInformation hmiB = b2LM.match(hmi);
//				if (hmiB.getNumMatches() == 0){
//					continue;
//				}
//				K2Statistic nb2 = new NBGolden();
//				nb2.init(instanceDesc, hmiB,esp.getExact());
//				evaluations.add(nb2);
				
//				K2Statistic md2 = new MatchDistance();
//				md2.init(instanceDesc, hmiB,esp.getExact());
//				evaluations.add(md2);
				
				for (FirstLineMatcher flm : flM) {
					MatchInformation fmi =  flm.match(esp.getCandidateOntology(), esp.getTargetOntology(), false);
//					instanceDesc = iDesc +"," + flm.getName() + ", - raw";
//					K2Statistic nb3 = new NBGolden();
//					nb3.init(instanceDesc, fmi,esp.getExact());
//					evaluations.add(nb3);
					
//					K2Statistic md3 = new MatchDistance();
//					md3.init(instanceDesc, fmi,esp.getExact());
//					evaluations.add(md3);
					
					SecondLineMatcher b2LM2 = new BehavioralMatcher(fmi);
					
					MatchInformation fmi_hmi = b2LM2.match(hmi);
					
//					instanceDesc = iDesc  + "," + flm.getName() + " + hm";
//					K2Statistic nb4 = new NBGolden();
//					nb4.init(instanceDesc, fmi_hmi,esp.getExact());
//					evaluations.add(nb4);
//					
//					K2Statistic md4 = new MatchDistance();
//					md4.init(instanceDesc, fmi_hmi,esp.getExact());
//					evaluations.add(md4);
					
					instanceDesc =iDesc  + "," + flm.getName() + " + hm + NULL";
					SecondLineMatcher slm = new OBThreshold(0.01);
					MatchInformation flm_hmi_2LM = slm.match(fmi_hmi);
					
					K2Statistic bin1 = new BinaryGolden();
					bin1.init(instanceDesc, flm_hmi_2LM,esp.getExact());
					evaluations.add(bin1);
					
//					String instanceDesc1 =iDesc  + "," + flm.getName() + " + hm + TH";
//					SecondLineMatcher slm1 = new OBThreshold(0.5);
//					MatchInformation flm_hmi_2LM1 = slm1.match(fmi_hmi);
//					
//					String instanceDesc2 =iDesc  + "," + flm.getName() + " + hm + DOM";
//					SecondLineMatcher slm2 = new OBDominants();
//					MatchInformation flm_hmi_2LM2 = slm2.match(fmi_hmi);
//					
//					K2Statistic nb5 = new NBGolden();
//					nb5.init(instanceDesc, flm_hmi_2LM,esp.getExact());
//					evaluations.add(nb5);
//					
//					K2Statistic nb51 = new NBGolden();
//					nb51.init(instanceDesc1, flm_hmi_2LM1,esp.getExact());
//					evaluations.add(nb51);
//					
//					K2Statistic nb52 = new NBGolden();
//					nb52.init(instanceDesc2, flm_hmi_2LM2,esp.getExact());
//					evaluations.add(nb52);
					
//					K2Statistic md5 = new MatchDistance();
//					md5.init(instanceDesc, flm_hmi_2LM,esp.getExact());
//					evaluations.add(md5);
					

					
//					K2Statistic bin11 = new BinaryGolden();
//					bin11.init(instanceDesc1, flm_hmi_2LM1,esp.getExact());
//					evaluations.add(bin11);
//					
//					K2Statistic bin12 = new BinaryGolden();
//					bin12.init(instanceDesc1, flm_hmi_2LM2,esp.getExact());
//					evaluations.add(bin12);
					
					SecondLineMatcher max = new OBMaxDelta(0.1);
					SecondLineMatcher th = new OBThreshold(0.5);
					SecondLineMatcher dom = new OBDominants();
					
					String instanceDesc0 =iDesc  + "," + flm.getName() + " + NULL";
					MatchInformation flm_null = slm.match(fmi);
					
					instanceDesc =iDesc  + "," + flm.getName() + " + " + max.getName();
					MatchInformation flm_max = max.match(fmi);
					
					String instanceDesc1 =iDesc  + "," + flm.getName()+ " + " + th.getName();
					MatchInformation flm_th = th.match(fmi);
					
					String instanceDesc2 =iDesc  + "," + flm.getName() + " + " + dom.getName();
					MatchInformation flm_dom = dom.match(fmi);
					
					K2Statistic bin20 = new BinaryGolden();
					bin20.init(instanceDesc0, flm_null,esp.getExact());
					evaluations.add(bin20);
					
					K2Statistic bin2 = new BinaryGolden();
					bin2.init(instanceDesc, flm_max,esp.getExact());
					evaluations.add(bin2);
					
					K2Statistic bin21 = new BinaryGolden();
					bin21.init(instanceDesc1, flm_th,esp.getExact());
					evaluations.add(bin21);
					
					K2Statistic bin22 = new BinaryGolden();
					bin22.init(instanceDesc2, flm_dom,esp.getExact());
					evaluations.add(bin22);
				
//					MappingPrinter verbin0 = new MappingPrinter();
//					verbin0.init(instanceDesc0, flm_null,esp.getExact());
//					evaluations.add(verbin0);
//					
//					MappingPrinter verbin2 = new MappingPrinter();
//					verbin2.init(instanceDesc, flm_max,esp.getExact());
//					evaluations.add(verbin2);
//					
//					MappingPrinter verbin21 = new MappingPrinter();
//					verbin21.init(instanceDesc1, flm_th,esp.getExact());
//					evaluations.add(verbin21);
//					
//					MappingPrinter verbin22 = new MappingPrinter();
//					verbin22.init(instanceDesc2, flm_dom,esp.getExact());
//					evaluations.add(verbin22);
//					
					max = new OBMaxDelta(0.1);
					th = new OBThreshold(0.5);
					dom = new OBDominants();
					
					instanceDesc0 =iDesc  + ", hm" + " + NULL";
					MatchInformation hm_null = slm.match(hmi);
//					
					instanceDesc =iDesc  + ", hm" + " + " + max.getName();
					MatchInformation hm_max = max.match(hmi);
					
					instanceDesc1 =iDesc  + ", hm"+ " + " + th.getName();
					MatchInformation hm_th = th.match(hmi);
					
					instanceDesc2 =iDesc  + ", hm"+ " + " + dom.getName();
					System.out.println(hmi.getCopyOfMatches());
					MatchInformation hm_dom = dom.match(hmi);
					System.out.println(hm_dom.getCopyOfMatches());
//					
					K2Statistic bin30 = new BinaryGolden();
					bin30.init(instanceDesc0, hm_null,esp.getExact());
					evaluations.add(bin30);
//					
					K2Statistic bin3 = new BinaryGolden();
					bin3.init(instanceDesc, hm_max,esp.getExact());
					evaluations.add(bin3);
					
					K2Statistic bin31 = new BinaryGolden();
					bin31.init(instanceDesc1, hm_th,esp.getExact());
					evaluations.add(bin31);
					
					K2Statistic bin32 = new BinaryGolden();
					bin32.init(instanceDesc2, hm_dom,esp.getExact());
					evaluations.add(bin32);
//					
//					MappingPrinter verbin3 = new MappingPrinter();
//					verbin3.init(instanceDesc, hm_max,esp.getExact());
//					evaluations.add(verbin3);
//					
//					MappingPrinter verbin31 = new MappingPrinter();
//					verbin31.init(instanceDesc1, hm_th,esp.getExact());
//					evaluations.add(verbin31);
//					
//					MappingPrinter verbin32 = new MappingPrinter();
//					verbin32.init(instanceDesc2, hm_dom,esp.getExact());
//					evaluations.add(verbin32);

					
//					instanceDesc =iDesc  + ", hm + behavioral2LM + maxD";
//					MatchInformation hmiB_2LM = slm.match(hmiB);
//					
//					instanceDesc1 =iDesc  + ", hm + behavioral2LM + TH";
//					MatchInformation hmiB_2LM1 = slm1.match(hmiB);
//					
//					instanceDesc2 =iDesc  + ", hm + behavioral2LM + DOM";
//					MatchInformation hmiB_2LM2 = slm2.match(hmiB);
//					
//					K2Statistic bin4 = new BinaryGolden();
//					bin4.init(instanceDesc, hmiB_2LM,esp.getExact());
//					evaluations.add(bin4);
//					
//					K2Statistic bin41 = new BinaryGolden();
//					bin41.init(instanceDesc1, hmiB_2LM1,esp.getExact());
//					evaluations.add(bin41);
//					
//					K2Statistic bin42 = new BinaryGolden();
//					bin42.init(instanceDesc1, hmiB_2LM2,esp.getExact());
//					evaluations.add(bin42);
					
					instanceDesc0 =iDesc + "," + flm.getName() + " + hm + NULL + behavioral2LM";
					
					instanceDesc =iDesc + "," + flm.getName() + " + hm + maxD + behavioral2LM";
					
					instanceDesc1 =iDesc + "," + flm.getName() + " + hm + TH + behavioral2LM";
					
					instanceDesc2 =iDesc + "," + flm.getName() + " + hm + DOM + behavioral2LM";
					
					BehavioralMatcher b2LM2_null = new BehavioralMatcher(flm_null, slope, intersect);
					MatchInformation fmi_hmi_10 = b2LM2_null.match(hmi, participant);
//					MatchInformation fmi_hmi_10 = b2LM2_null.match(hm_null, participant);

					BehavioralMatcher b2LM2_max = new BehavioralMatcher(flm_max, slope, intersect);
					MatchInformation fmi_hmi_1 = b2LM2_max.match(hmi, participant);
//					MatchInformation fmi_hmi_1 = b2LM2_null.match(hm_max, participant);
					
					BehavioralMatcher b2LM2_th = new BehavioralMatcher(flm_th, slope, intersect);
					MatchInformation fmi_hmi_11 = b2LM2_th.match(hmi, participant);
//					MatchInformation fmi_hmi_11 = b2LM2_th.match(hm_th, participant);
					
					BehavioralMatcher b2LM2_dom = new BehavioralMatcher(flm_dom, slope, intersect);
					MatchInformation fmi_hmi_12 = b2LM2_dom.match(hmi, participant);
//					MatchInformation fmi_hmi_12 = b2LM2_dom.match(hm_dom, participant);
					
					K2Statistic bin4_10 = new BinaryGolden();
					bin4_10.init(instanceDesc0, fmi_hmi_10 ,esp.getExact());
					evaluations.add(bin4_10);
					
					K2Statistic bin4_1 = new BinaryGolden();
					bin4_1.init(instanceDesc, fmi_hmi_1 ,esp.getExact());
					evaluations.add(bin4_1);					
					
					K2Statistic bin4_11 = new BinaryGolden();
					bin4_11.init(instanceDesc1, fmi_hmi_11 ,esp.getExact());
					evaluations.add(bin4_11);
					
					K2Statistic bin4_12 = new BinaryGolden();
					bin4_12.init(instanceDesc2, fmi_hmi_12 ,esp.getExact());
					evaluations.add(bin4_12);
//					
//					MappingPrinter verbin4_1 = new MappingPrinter();
//					verbin4_1.init(instanceDesc, fmi_hmi_1 ,esp.getExact());
//					evaluations.add(verbin4_1);					
//					
//					MappingPrinter verbin4_11 = new MappingPrinter();
//					verbin4_11.init(instanceDesc1, fmi_hmi_11 ,esp.getExact());
//					evaluations.add(verbin4_11);
//					
//					MappingPrinter verbin4_12 = new MappingPrinter();
//					verbin4_12.init(instanceDesc2, fmi_hmi_12 ,esp.getExact());
//					evaluations.add(verbin4_12);
					
				}
			}
			return evaluations;
		}
		

		/*
		 * (non-Javadoc)
		 * @see ac.technion.schemamatching.experiments.MatchingExperiment#init(java.util.Properties, java.util.ArrayList)
		 */
		public boolean init(OBExperimentRunner oer, Properties properties, ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
			pairPath = properties.getProperty("pairPath");
			slopePath = properties.getProperty("slope");
			//iterate over files in path, and load to HashMap<spid,ArrayList<File>>
			File[] files = new File(pairPath).listFiles();
			Integer schemaPair = new Integer(0);
			System.out.println(new File(pairPath));
			for (File file : files)
			{
				System.out.println(file.getName());
				schemaPair = new Integer(Integer.parseInt(file.getName().split("\\.")[0].split("_")[1]));
				ArrayList<File> fileList = ( fileMap.containsKey(schemaPair) 
						? fileMap.get(schemaPair) : new ArrayList<File>());
				fileList.add(file);
				fileMap.put(schemaPair, fileList);
			}
			loadSlopes(slopePath);
			this.flM = flM;
			//using property files allows to modify experiment parameters at runtime
			this.properties = properties;			
			return true;
		}

		/*
		 * (non-Javadoc)
		 * @see ac.technion.schemamatching.experiments.MatchingExperiment#getDescription()
		 */
		public String getDescription() {
			return "Matrix Predictor Evaluation";
		}
		
		public ArrayList<Statistic> summaryStatistics() {
			//unused
			return null;
		}
		
		public void loadSlopes(String file){
            BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(file));
	            String line = "";
				while ((line = br.readLine()) != null) {
	                String[] row = line.split(",");
	                if (row[0].contains("ParticipantID")){
	                	continue;
	                }
	                slope.put(row[0], Float.parseFloat(row[2]));
	                intersect.put(row[0], Float.parseFloat(row[1]));
	                
	            }
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

}
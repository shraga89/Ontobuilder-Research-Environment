/**
 * 
 */
package ac.technion.schemamatching.experiments.holistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.experiments.holistic.network.NetworkStatisticsHandler;
import ac.technion.schemamatching.experiments.holistic.network.SchemaNetwork;
import ac.technion.schemamatching.matchers.firstline.FLMList;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.NBGolden;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.testbed.ExperimentSchema;

public class NetworkQualityExperiment implements HolisticExperiment{
	
	/*
	 * Configuration of the experiment
	 */
	
	// each schema is mapped to how many other schemas as part of the initialisation?
	private int density = 3;
	
//	private int feedbackBudget = 1000;
//	private int validatedPerEval = 10;
	
	private List<FirstLineMatcher> flM;
	private HashMap<String,Double> matcherWeights = new HashMap<String,Double>();

	public List<Statistic> runExperiment(Set<ExperimentSchema> eSet) {
		
		System.out.println("++++++++++++++++++++++++++++++++++++++");
		System.out.println("Create network...");
		SchemaNetwork network1 = new SchemaNetwork(eSet);
		System.out.println("++++++++++++++++++++++++++++++++++++++");
		System.out.println("Init network...");
		network1.initNetwork(this.flM, this.matcherWeights, this.density);
		
//		SchemaNetwork network2 = (SchemaNetwork) network1.clone();
//		SchemaNetwork network3 = (SchemaNetwork) network1.clone();
//		SchemaNetwork network4 = (SchemaNetwork) network1.clone();
//		SchemaNetwork network5 = (SchemaNetwork) network1.clone();
//		SchemaNetwork network6 = (SchemaNetwork) network1.clone();
//		SchemaNetwork evolvedNetwork; 
//		Set<Match> toExclude;
		
		NetworkStatisticsHandler networkStatisticsHandler = new NetworkStatisticsHandler();
		
		System.out.println("Do initial measuring...");
		networkStatisticsHandler.addStatistic(NBGolden.class, "Initial network", network1, network1.getSchemas());
		
		
//		System.out.println("#####################################################");
//		System.out.println("Run Baseline");
//		toExclude = new HashSet<Match>();
//		evolvedNetwork = NetworkEvolution.evolveNetwork(network1, new RandomRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb11 =  new NBGolden();
//		nb11.init("Baseline "  + feedbackBudget + "(" + validatedPerEval + ")", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb11);
//		evolvedNetwork = NetworkEvolution.evolveNetwork(evolvedNetwork, new RandomRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb12 =  new NBGolden();
//		nb12.init("Baseline "  + 2* feedbackBudget + "(" + validatedPerEval + ")", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb12);
//		evolvedNetwork = NetworkEvolution.evolveNetwork(evolvedNetwork, new RandomRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb13 =  new NBGolden();
//		nb13.init("Baseline "  + 3* feedbackBudget + "(" + validatedPerEval + ")", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb13);
//		evolvedNetwork = NetworkEvolution.evolveNetwork(evolvedNetwork, new RandomRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb14 =  new NBGolden();
//		nb14.init("Baseline "  + 4* feedbackBudget + "(" + validatedPerEval + ")", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb14);
//		
//		System.out.println("#####################################################");
//		System.out.println("Run CertaintyDegreeRanking");
//		toExclude = new HashSet<Match>();
//		evolvedNetwork = NetworkEvolution.evolveNetwork(network2, new CertaintyDegreeRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb21 =  new NBGolden();
//		nb21.init("CertaintyDegreeRanking 1", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb21);
//		evolvedNetwork = NetworkEvolution.evolveNetwork(evolvedNetwork, new CertaintyDegreeRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb22 =  new NBGolden();
//		nb22.init("CertaintyDegreeRanking 2", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb22);
//		evolvedNetwork = NetworkEvolution.evolveNetwork(evolvedNetwork, new CertaintyDegreeRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb23 =  new NBGolden();
//		nb23.init("CertaintyDegreeRanking 3", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb23);
//		evolvedNetwork = NetworkEvolution.evolveNetwork(evolvedNetwork, new CertaintyDegreeRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb24 =  new NBGolden();
//		nb24.init("CertaintyDegreeRanking 4", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb24);
//
//		System.out.println("#####################################################");
//		System.out.println("Run DecisivenessDegreeRanking");
//		toExclude = new HashSet<Match>();
//		evolvedNetwork = NetworkEvolution.evolveNetwork(network3, new DecisivenessDegreeRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb31 =  new NBGolden();
//		nb31.init("DecisivenessDegreeRanking 1", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb31);
//		evolvedNetwork = NetworkEvolution.evolveNetwork(evolvedNetwork, new DecisivenessDegreeRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb32 =  new NBGolden();
//		nb32.init("DecisivenessDegreeRanking 2", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb32);
//		evolvedNetwork = NetworkEvolution.evolveNetwork(evolvedNetwork, new DecisivenessDegreeRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb33 =  new NBGolden();
//		nb33.init("DecisivenessDegreeRanking 3", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb33);
//		evolvedNetwork = NetworkEvolution.evolveNetwork(evolvedNetwork, new DecisivenessDegreeRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb34 =  new NBGolden();
//		nb34.init("DecisivenessDegreeRanking 4", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb34);
//		
//		System.out.println("#####################################################");
//		System.out.println("Run IndecisivenessDegreeRanking");
//		toExclude = new HashSet<Match>();
//		evolvedNetwork = NetworkEvolution.evolveNetwork(network6, new IndecisivenessDegreeRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb61 =  new NBGolden();
//		nb61.init("IndecisivenessDegreeRanking 1", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb61);
//		evolvedNetwork = NetworkEvolution.evolveNetwork(evolvedNetwork, new IndecisivenessDegreeRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb62 =  new NBGolden();
//		nb62.init("IndecisivenessDegreeRanking 2", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb62);
//		evolvedNetwork = NetworkEvolution.evolveNetwork(evolvedNetwork, new IndecisivenessDegreeRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb63 =  new NBGolden();
//		nb63.init("IndecisivenessDegreeRanking 3", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb63);
//		evolvedNetwork = NetworkEvolution.evolveNetwork(evolvedNetwork, new IndecisivenessDegreeRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb64 =  new NBGolden();
//		nb64.init("IndecisivenessDegreeRanking 4", evolvedNetwork.getMatchResultForSchemas(), evolvedNetwork.getGoldStandard());
//		res.add(nb64);

//		System.out.println("#####################################################");
//		System.out.println("Run CertaintyBetweennessRanking");
//		toExclude = new HashSet<Match>();
//		evolvedNetwork = NetworkEvolution.evolveNetwork(network4, new CertaintyBetweennessRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb41 =  new NBGolden();
//		nb41.init("CertaintyBetweennessRanking 1", evolvedNetwork.getMI(), evolvedNetwork.getGoldStandard());
//		res.add(nb41);
//		evolvedNetwork = NetworkEvolution.evolveNetwork(evolvedNetwork, new CertaintyBetweennessRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb42 =  new NBGolden();
//		nb42.init("CertaintyBetweennessRanking 2", evolvedNetwork.getMI(), evolvedNetwork.getGoldStandard());
//		res.add(nb42);

//		System.out.println("#####################################################");
//		System.out.println("Run DecisivenessBetweennessRanking");
//		toExclude = new HashSet<Match>();
//		evolvedNetwork = NetworkEvolution.evolveNetwork(network5, new DecisivenessBetweennessRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb51 =  new NBGolden();
//		nb51.init("DecisivenessBetweennessRanking 1", evolvedNetwork.getMI(), evolvedNetwork.getGoldStandard());
//		res.add(nb51);
//		evolvedNetwork = NetworkEvolution.evolveNetwork(evolvedNetwork, new DecisivenessBetweennessRanking(), feedbackBudget, validatedPerEval, toExclude);
//		System.out.println("Do measuring...");
//		NBGolden nb52 =  new NBGolden();
//		nb52.init("DecisivenessBetweennessRanking 2", evolvedNetwork.getMI(), evolvedNetwork.getGoldStandard());
//		res.add(nb52);

		return networkStatisticsHandler.getStatistics();
	}

	public boolean init(OBExperimentRunner oer, Properties properties,
			ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM) {
		
		this.flM = flM;
		HashMap<Integer, FirstLineMatcher> flmHash = FLMList.getIdFLMHash();
		for (Object key : properties.keySet()) {
			String strKey =(String)key; 
			Integer mId = Integer.parseInt(strKey.substring(1));
			
			Double pWeight = Double.parseDouble((String)properties.get(key));
			matcherWeights.put(flmHash.get(mId).getName(), pWeight);
		}
		return true;
	}

	public String getDescription() {
		String desc = "Network Quality Experiment";
		return desc;
	}
	

}

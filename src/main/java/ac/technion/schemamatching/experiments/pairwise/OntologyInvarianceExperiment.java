package ac.technion.schemamatching.experiments.pairwise;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ac.technion.schemamatching.experiments.OBExperimentRunner;
import ac.technion.schemamatching.matchers.firstline.FirstLineMatcher;
import ac.technion.schemamatching.matchers.secondline.SecondLineMatcher;
import ac.technion.schemamatching.statistics.DummyStatistic;
import ac.technion.schemamatching.statistics.Statistic;
import ac.technion.schemamatching.test.OntologyInvariance;
import ac.technion.schemamatching.testbed.ExperimentSchemaPair;

public class OntologyInvarianceExperiment implements PairWiseExperiment{
	
	private ArrayList<FirstLineMatcher> flM;
	private ArrayList<SecondLineMatcher> slM;
	
	@Override
	public List<Statistic> runExperiment(ExperimentSchemaPair esp) {
		ArrayList<Statistic> evaluations = new ArrayList<Statistic>();
		OntologyInvariance oi = new OntologyInvariance(esp);
		DummyStatistic stat1= new DummyStatistic();
		stat1.setName("OntologyInvarianceExperimentFLM");
		stat1.setHeader(new String[]{"ID","CandidateOntology and TargetOntology","Flm", "PassedTest?", "problam"});
		List<String[]> statData1= new ArrayList<>();
		for (FirstLineMatcher f : flM)
		{
			int result=oi.test_FLM(f);
			if ((result==1)) {				//Passed test
				statData1.add(new String[]{String.valueOf(esp.getID()), 
						esp.getCandidateOntology().getName() + " and " + esp.getTargetOntology().getName(), 
						f.getName(),"Passed","No Problam"});
			}
			if ((result==2)) {				//did not Pass test
				statData1.add(new String[]{String.valueOf(esp.getID()), 
						esp.getCandidateOntology().getName() + " and " + esp.getTargetOntology().getName(), 
						f.getName(),"Didn't Pass","Change in Candidate Terms"});
			}
			if ((result==3)) {				//did not Pass test
				statData1.add(new String[]{String.valueOf(esp.getID()), 
						esp.getCandidateOntology().getName() + " and " + esp.getTargetOntology().getName(), 
						f.getName(),"Didn't Pass","Change in Target Terms"});
			}
			if ((result==4)) {				//did not Pass test
				statData1.add(new String[]{String.valueOf(esp.getID()), 
						esp.getCandidateOntology().getName() + " and " + esp.getTargetOntology().getName(), 
						f.getName(),"Didn't Pass","Unknown Problam + "});
			}
		}
		stat1.setData(statData1);
		evaluations.add(stat1);
		DummyStatistic stat2= new DummyStatistic();
		stat2.setName("OntologyInvarianceExperimentSLM");
		stat2.setHeader(new String[]{"ID","CandidateOntology and TargetOntology","Slm", "PassedTest?", "problam"});
		List<String[]> statData2= new ArrayList<>();
		for (SecondLineMatcher s : slM)
		{
			int result=oi.test_SLM(s);
			if ((result==1)) {				//Passed test
				statData2.add(new String[]{String.valueOf(esp.getID()), 
						esp.getCandidateOntology().getName() + " and " + esp.getTargetOntology().getName(), 
						s.getName(),"Passed",String.valueOf(result)});
			}
			if ((result==2)) {				//return did not Pass test
				statData2.add(new String[]{String.valueOf(esp.getID()), 
						esp.getCandidateOntology().getName() + " and " + esp.getTargetOntology().getName(), 
						s.getName(),"Didn't Pass",String.valueOf(result)});
			}
		}
		stat2.setData(statData2);
		evaluations.add(stat2);
		return evaluations;
	}

	@Override
	public boolean init(OBExperimentRunner oer, Properties properties,
						ArrayList<FirstLineMatcher> flM, ArrayList<SecondLineMatcher> slM, boolean isMemory) {
		this.flM=flM;
		this.slM=slM;
		return true;
	}

	@Override
	public String getDescription() {
		return "Ontology Invariance Experiment";
	}

	@Override
	public List<Statistic> summaryStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

}

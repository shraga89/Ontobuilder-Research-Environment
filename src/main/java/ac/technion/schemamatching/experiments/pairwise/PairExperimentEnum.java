/**
 * 
 */
package ac.technion.schemamatching.experiments.pairwise;

/**
 * @author Tomer Sagi
 * Lists pair wise schema matching experiment types available 
 * in the OntobuilderResearchFramework
 * 
 */
public enum PairExperimentEnum 
{
	SimpleMatch(new SimpleMatchExperiment())
	//,Clarity(new ClarityExperiment()) //Deprecated by Tomer Sagi 29/01/2014
	, MatrixPredictEval(new MatrixPredictorEvaluation()),Boosting(new BoostingExperiment())
	, TuneTerm(new TuneTermExperiment()), StaticEnsemble(new StaticEnsemble())
	, EntryPredictEval(new EntryPredictorEvaluation()), AttributePredictEval(new AttributePredictorEvaluation())
	, MatrixPredictorEnsemble(new MatrixPredictorEnsemble()),AttributePredictorEnsemble(new AttributePredictorEnsemble())
	, ClusteringMatches(new ClusteringMatches()),Drift(new Drift2LM())
	, EntryPredictorEnsemble(new EntryPredictorEnsemble())
	, ROCCurve(new ROCExperiment())
	, TopKClustering(new TopKClustering())
	, VectorPrinting(new VectorPrinting())
	, MappingPrinting(new MappingPrinting()) //changed for creating c# interface
	, SLMExample(new SecondLineMatchExample())
	, BetaNoise(new BetaNoiseExperiment())
	, ProcessModelPrediction (new ProcessModelPrediction())
	, ProcessModelSeparation (new ProcessModelSeparationEvaluation())
	, ProcessModelEvaluation (new ProcessModelEvaluation())
	, BuildMatchesCurpos(new MatchesCurposBuildExperiment())
	, SimpleMatchV(new SimpleMatchExperimentDiagnostic())
	, AguemntCuporsMatch(new MatchesCurposSimpleExperiment())
	, Profile(new SchemaPairProfiling())
	, NBNB(new NBNBEvaluationExperiment())
	, OntologyInvariance(new OntologyInvarianceExperiment())
	, HumanPredictorEval(new HumanPredictorEvaluation())
	, MaxDelta(new MaxDeltaOnEntryPredictors())
	, MaxDeltaEnsemble(new MaxDeltaOnEntryPredictorEnsemble())
	, NewVerbose(new SimpleMatchExperimentVerboseNew())
	, Cartesian ( new NBTuningCartesianProduct())
	, CsharpExp( new CsharpExp()), PredictorCsharp(new PredictorCsharp()),
	SSEnsemble(new SSEnsembleExperiment()),
	Human2LM (new Behavioral2LMensemble())
	;
	
	
	private PairExperimentEnum(PairWiseExperiment e)
	{
		exp = e;
	}
	public PairWiseExperiment getExperiment() 
	{
		return exp;
	}
	private final PairWiseExperiment exp;

}

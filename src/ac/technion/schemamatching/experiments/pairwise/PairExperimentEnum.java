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
	,Clarity(new ClarityExperiment()),MatrixPredictEval(new MatrixPredictorEvaluation()),Boosting(new BoostingExperiment())
	,TuneTerm(new TuneTermExperiment()), StaticEnsemble(new StaticEnsemble())
	,EntryPredictEval(new EntryPredictorEvaluation()), AttributePredictEval(new AttributePredictorEvaluation())
	,MatrixPredictorEnsemble(new MatrixPredictorEnsemble()),AttributePredictorEnsemble(new AttributePredictorEnsemble())
	,ClusteringMatches(new ClusteringMatches()),Drift(new Drift2LM())
	,EntryPredictorEnsemble(new EntryPredictorEnsemble())
	,ROCCurve(new ROCExperiment())
	,TopKClustering(new TopKClustering())
	,VectorPrinting(new VectorPrinting())
	,MappingPrinting(new MappingPrinting())
	,SLMExample(new SecondLineMatchExample())
	,BetaNoise(new BetaNoiseExperiment())
	,ProcessModelPrediction (new ProcessModelPrediction())
	,ProcessModelSeparation (new ProcessModelSeparationEvaluation())
	,ProcessModelEvaluation (new ProcessModelEvaluation())
	, BuildMatchesCurpos(new MatchesCurposBuildExperiment())
	,SimpleMatchV(new SimpleMatchExperimentDiagnostic())
	, AguemntCuporsMatch(new MatchesCurposSimpleExperiment());
	
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

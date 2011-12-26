/**
 * 
 */
package ac.technion.schemamatching.experiments;

/**
 * @author Tomer Sagi
 * Lists experiment types available in the OntobuilderResearchFramework
 * 
 */
public enum ExperimentType 
{
	Clarity(new ClarityExperiment()),MatrixPredictEval(new MatrixPredictorEvaluation()),Boosting(new BoostingExperiment())
	,TuneTerm(new TuneTermExperiment()), StaticEnsemble(new StaticEnsemble())
	,EntryPredictEval(new EntryPredictionEvaluation()), AttributePredictEval(new AttributePredictorEvaluation())
	,MatrixPredictorEnsemble(new MatrixPredictorEnsemble()),AttributePredictorEnsemble(new AttributePredictorEnsemble())
	,ClusteringMatches(new ClusteringMatches()),Drift(new Drift2LM())
	,EntryPredictorEnsemble(new EntryPredictorEnsemble())
	,ROCCurve(new ROCExperiment());
	private ExperimentType(MatchingExperiment e)
	{
		exp = e;
	}
	public MatchingExperiment getExperiment() 
	{
		return exp;
	}
	private final MatchingExperiment exp;

}

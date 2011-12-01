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
	,TuneTerm(new TuneTermExperiment())
	,RowPredictEval(new RowPredictionEvaluation()),EntryPredictEval(new EntryPredictionEvaluation()), AttributePredictEval(new AttributePredictorEvaluation())
	,MatrixPredictorEnsemble(new MatrixPredictorEnsemble()),RowPredictorEnsemble(new RowPredictorEnsemble())
	,ClusteringMatches(new ClusteringMatches()),Drift(new Drift2LM())
	,EntryPredictorEnsemble(new EntryPredictorEnsemble());
	private ExperimentType(MatchingExperiment e)
	{
		exp = e;
	}
	public MatchingExperiment getExperiment() {
		return exp;
	}
	private final MatchingExperiment exp;

}

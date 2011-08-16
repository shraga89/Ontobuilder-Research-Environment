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
	,RowPredictEval(new RowPredictionEvaluation()),EntryPredictEval(new EntryPredictionEvaluation())
	,MatrixPredictorEnsemble(new MatrixPredictorEnsemble()),RowPredictorEnsemble(new RowPredictorEnsemble())
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
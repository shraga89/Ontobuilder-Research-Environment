package ac.technion.schemamatching.statistics.predictors;


public enum PredictorList {AvgAPredictor(new AvgAPredictor()),BMMPredictor(new BMMPredictor()),
						   BMPredictor(new BMPredictor()),LMMPredictor(new LMMPredictor()),MaxPredictor(new MaxPredictor()),
						   MaxAPredictor(new MaxAPredictor()),OneToOnePredictor(new OneToOneAPredictor())
						   ,STDEVPredictor(new STDEVPredictor());

private Predictor myPredictor;

public Predictor getPredictor()
{
	return myPredictor;
}
private PredictorList(Predictor p)
{
	myPredictor = p;
}
	

}

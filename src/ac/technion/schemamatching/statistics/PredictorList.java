package ac.technion.schemamatching.statistics;

public enum PredictorList {AvgAPredictor(new AvgAPredictor()),BMMPredictor(new BMMPredictor()),
						   BMPredictor(new BMPredictor()),LMMPredictor(new LMMPredictor()),
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

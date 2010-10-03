package smb_service;

import java.util.ArrayList;

public interface IWeakClassifier {
	public abstract void Initialize(double[] sampleDistribution);
	public abstract ArrayList<Classification> Classify(ArrayList<SchemaPair> samples);
	public abstract double CalculateError();
}

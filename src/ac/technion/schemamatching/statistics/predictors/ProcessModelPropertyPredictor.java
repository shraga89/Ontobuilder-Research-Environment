package ac.technion.schemamatching.statistics.predictors;

import org.jbpt.petri.NetSystem;
import org.jbpt.petri.io.PNMLSerializer;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;


public class ProcessModelPropertyPredictor implements Predictor {

	public enum ProcessModelProperty {
		/*
		 * Text Syntax Properties
		 */
		AvgLengthOfLabels,
		
		/*
		 * Text Semantics Properties
		 */
		NumberLabelsWithTermInWordNet,
		
		/*
		 * Structural Properties
		 */
		Size,
		RPSTDepth,
		RPSTWidth,
		RPSTFragmentTypes,
		NodesInCycle,
		NumberOfConnectorTypes,
		NumberSourceNodes,
		NumberSinkNodes,
		AvgNodeDegree,
		MaxNodeDegree,
		
		/*
		 * Behavioural Properties
		 */
		SizeExclusivenessRelation,
		SizeStrictOrderRelation,
		SizeConcurrencyRelation,
		
	}
	
	private ProcessModelProperty currentProperty;
	
	private Ontology candidate;
	private Ontology target;
	
	private PNMLSerializer pnmlSerializer = new PNMLSerializer();

	
	public ProcessModelPropertyPredictor(Ontology candidate, Ontology target, ProcessModelProperty property) {
		this.candidate = candidate;
		this.target = target;
		this.currentProperty = property;
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#getName()
	 */
	public String getName() {
		return "Process Model Predictor";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#newRow()
	 */
	public void newRow() { }

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#visitColumn(double)
	 */
	public void visitColumn(double val) { }

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#init(int, int)
	 */
	public void init(int rows, int cols) { }

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.statistics.Predictor#getRes()
	 */
	public double getRes() {
		double score = 0.0;
		
		NetSystem net1 = pnmlSerializer.parse(candidate.getFile().getPath());
		NetSystem net2 = pnmlSerializer.parse(target.getFile().getPath());

		switch (this.currentProperty) {
		/*
		 * Text Syntax Properties
		 */
		case AvgLengthOfLabels:
			double lengthSum1 = 0.0;
			for (Term t : candidate.getTerms(true))
				lengthSum1 += t.getName().length();
			double avg1 = lengthSum1 / (double) candidate.getTermsCount();
			double lengthSum2 = 0.0;
			for (Term t : target.getTerms(true))
				lengthSum2 += t.getName().length();
			double avg2 = lengthSum2 / (double) target.getTermsCount();
			
			score = 1.0 - (
					(double)Math.abs(avg1 - avg2) / (double)Math.max(avg1, avg2));
			break;
			
		/*
		 * Text Semantics Properties
		 */
		
		/*
		 * Structural Properties
		 */
		case Size:
			score = 1.0 - (
					(double)Math.abs(candidate.getTermsCount() - target.getTermsCount()) 
							/ (double)Math.max(candidate.getTermsCount(), target.getTermsCount()));
			break;
			
		/*
		 * Behavioural Properties
		 */


		default:
			break;
		}
		
		return score;
	}

}
/**
 * 
 */
package ac.technion.schemamatching.experiments.holistic;


/**
 * @author Tomer Sagi
 * Lists holistic schema matching experiment types 
 * available in the OntobuilderResearchFramework
 * 
 */
public enum HolisticExperimentEnum 
{
	;
	
	private HolisticExperimentEnum(HolisticExperiment e)
	{
		exp = e;
	}
	public HolisticExperiment getExperiment() 
	{
		return exp;
	}
	private final HolisticExperiment exp;

}

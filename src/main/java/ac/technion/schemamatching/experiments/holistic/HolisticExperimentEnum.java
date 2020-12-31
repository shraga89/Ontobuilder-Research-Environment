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
	schemaLookup(new SchemaLookup()),
	simpleHMatch(new SimpleMatchNoExact()), 
	networkQuality(new NetworkQualityExperiment()),
	wsRetrieval(new WSRetrieval()),
	wsComposition(new WSComposition()),
	wsCompositionBiMatcher(new WSCompositionBiMatcher()),
	FileExtraction(new SchemaFileExtraction())
	,ComaRecall(new ComaRecallAndP()),
	holisticStat(new HolisticStat())
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

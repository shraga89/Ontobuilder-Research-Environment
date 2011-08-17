/**
 * 
 */
package ac.technion.schemamatching.matchers;

/**
 * This enum type lists the available @link{FirstLineMatcher}s in the Ontobuilder Research Environment. 
 * @author Tomer Sagi
 *
 */
public enum FLMList {AMCDataType(new AMCDataType()),AMCName(new AMCName())
	,AMCTokenPath(new AMCTokenPath()),OBGraphMatch(new OBGraphMatch())
	,OBPrecedence(new OBPrecedenceMatch()),OBSimilarityFlooding(new OBSimilarityFlooding())
	,OBTerm(new OBTermMatch()),OBValue(new OBValueMatch());

private FLMList(FirstLineMatcher flm)
{
	myFLM = flm;
}

public FirstLineMatcher getFLM() {
	return myFLM;
}
private FirstLineMatcher myFLM;


}

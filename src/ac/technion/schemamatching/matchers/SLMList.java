/**
 * 
 */
package ac.technion.schemamatching.matchers;

import java.util.HashMap;

/**
 * This enum type lists the available @link{SecondLineMatcher}s in the Ontobuilder Research Environment. 
 * @author Tomer Sagi
 *
 */
public enum SLMList {OBMWBG(new OBmwbg()),OBSM(new OBStableMarriage()),
	OBDom(new OBDominants()),OBIntersection(new OBIntersection()),
	OBUnion(new OBUnion()),OBThreshold025(new OBThreshold(0.25)),
	OBTopK(new OBTopK());

private SLMList(SecondLineMatcher slm)
{
	mySLM = slm;
}

public SecondLineMatcher getSLM() {
	return mySLM;
}

public static HashMap<Integer,SecondLineMatcher> getIdSLMHash()
{
	HashMap<Integer,SecondLineMatcher> res = new HashMap<Integer,SecondLineMatcher>();
	for (SLMList f : SLMList.values())
	{
		res.put(f.mySLM.getDBid(), f.mySLM);
	}
	return res;
}
private SecondLineMatcher mySLM;


}

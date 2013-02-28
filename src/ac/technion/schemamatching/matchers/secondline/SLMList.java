/**
 * 
 */
package ac.technion.schemamatching.matchers.secondline;

import java.util.HashMap;

/**
 * This enum type lists the available @link{SecondLineMatcher}s in the Ontobuilder Research Environment. 
 * @author Tomer Sagi
 *
 */
public enum SLMList {OBMWBG(new OBmwbg()),OBSM(new OBStableMarriage()),
	OBDom(new OBDominants()),OBIntersection(new OBIntersection()),
	OBUnion(new OBUnion()),OBThreshold015(new OBThreshold(0.15)),
	OBThreshold025(new OBThreshold(0.25)),
	OBThreshold050(new OBThreshold(0.50)),OBThreshold075(new OBThreshold(0.75)),
	OBTopK(new OBTopK()), OBMax(new OBMaxDelta(0.0)),OBMaxDelta005(new OBMaxDelta(0.05)), 
	OBMaxDelta01(new OBMaxDelta(0.1)), OBMaxDelta02(new OBMaxDelta(0.2)), OBMaxSim(new OBMaxSim());

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

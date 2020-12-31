package ac.technion.schemamatching.curpos;

public interface CurposTermSimilarityMeausre {
	
	/*
	 * This method should measure similarity between two CurposTerm objects
	 * The similarity value should be between 0 and 1, in any case it is expected to be larger or equal to 0.
	 * The similarity of (t1,t2) should equal the similarity of (t2,t1)
	 * The similarity of (t1,t1) should equal 1 (or any maximum value you choose)
	 */
	double MeausreSimilarity(CurposTerm t1, CurposTerm t2);
}

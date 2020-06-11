## Match Distance ##

MD (Match Distance) captures the notion of completeness by returning a 
 normalized distance between the given result and the 
 exact match only over dimensions corresponding to expected 
 matched attribute pairs. 

 NMD (Normalized Match Distance) completes the picture by presenting a normalized 
 distance only over non-match dimensions of the task, 
 thus capturing the amount of noise in a specific solution. 

 Both functions are reversed to retain the intuitive 
 interpretation where a higher result is preferred. 

 Since normalization expressions are based upon the task's 
 exact match vectors, they remain constant between 
 different match results and thus the functions retain 
 the sub-additivity property of distance measures.

 Assumes exact-match vectors are binary. 
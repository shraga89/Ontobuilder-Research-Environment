## Unconstrained Match Distance ##

MD captures the notion of completeness by returning a 
normalized distance between the given result and the 
exact match only over dimensions corresponding to expected
matched attribute pairs. 

NMD completes the picture by presenting a normalized 
distance only over non-match dimensions of the task, 
thus capturing the amount of noise in a specific solution. 

Both functions are reversed to retain the intuitive 
interpretation where a higher result is preferred. 

Since normalization expressions are based upon the task's 
reference vector, they remain constant between 
different match results and thus the functions retain 
the sub-additivity property of distance measures. 

Unconstrained version of [MatchDistance](https://bitbucket.org/tomers77/ontobuilder-research-environment/wiki/MatchDistance) allowing 
non-binary reference vectors. 
## Drift Experiment ##


This experiments calculates drift between 1LM and 2LM using L2 distance.

Drift Experiment is designed to evaluate the effect a 2LM has on the results of a 1LM.

Some 2LM may consistently cause the result to drift away from the correct result (the distance between the 2LM result vector and the exact match is larger then the distance between the 1LM result vector and the exact match when following specific 1LMs.

To facilitate this evaluation we require the results of evaluating the 1LM and the 2LM to consistent with each other and therefore require a distance measure based on similarity norm that ensures subadditivity.
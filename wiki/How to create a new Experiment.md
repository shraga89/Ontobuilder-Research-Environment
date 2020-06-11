## Creating a new Experiment: ##

** There are 2 kinds of experiments: Holistic and Pairwise. **

This explanation refers to creating a Pairwise experiment but creating Holistic experiment is pretty much the same.
["SimpleMatchExperiment"](https://bitbucket.org/tomers77/ontobuilder-research-environment/wiki/SimpleMatch) is a good example of a pairwise experiment.

To create a new Experiment follow these steps:

 1.Create a new class in                            
package ac.technion.schemamatching.experiments.pairwise called "YourExperimentNameExperiment" that implements PairWiseExperiment.
(* the compiler should automatically add the unimplemented methods).

2.Implement the methods :

* 2.1 runExperiment: Runs the experiment and calculates statistics   during the experiment and off experiment results.

* 2.2 init: Used to initialize a matching experiment. replaces a parameterized constructor

* 2.3 getDescription: Experiment Description 

* 2.4 summaryStatistics: Statistics sumarizing the experiment (for all schema pairs)
as needed according to interface PairWiseExperiment.

   3.Add "YourExperiment" to PairExperimentEnum by adding  YourExperiment(new YourExperimentNameExperiment()).

   4.In order to use YourExperiment use for example:
 
cmd C:\results YourExperiment 0 35 0 -f:0,1,2,3 -s:1,3
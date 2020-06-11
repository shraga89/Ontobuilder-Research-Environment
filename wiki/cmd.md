## Run as command line (cmd) ##

The arguments that can be entered are the following:

1. Run as command line (cmd).

2. Output path - the path to the directory where the results should appear.

3. Experiment Type compared against enum types PairExperimentEnum and HolisticExperimentEnum.

4. K - number of experiments schema pairs.

5. Schema pair ID Set ( e.g. 1,2,3 or 1 ) (ignored if K <> 0)

6. DatasetID (for random K). If running spacific schema pairs enter 0. [(See Datasets)](https://bitbucket.org/tomers77/ontobuilder-research-environment/wiki/Datasets)

7. One or more from the followings (optional):

-d:domainCodes - string in the following format "2,3,4,2" (without the Quotation mark)

-f:First Line Matcher Codes [(See Matchers)](https://bitbucket.org/tomers77/ontobuilder-research-environment/wiki/MatchingSystems)

-p:properties file used to configure the experiment

-s:second line matcher codes (from db or enum)

-l:list of schema pair ids to be used in experiment (file name containing the list)

For example: cmd C:\results SimpleMatch 0 35 0 -f:0,1,2,3 -s:1,3

(Runs Simple Match Experiment on Schema pair Set number 35 using

 Term, Value, TermValue and combined as First Line Matchers and

 Maximum Weighted Bipartite Graph (MWBG)  and Dominants as Second Line Matchers)
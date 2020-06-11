* The main Advantage of using this way is the relative simplicity of integrating the algorithm in the system. 
Although working is this way may cause writing functions that are already implemented in ontobuilder.  

To create a new FLM follow these steps:

1)	Create a new class in                            

package ac.technion.schemamatching.matchers.firstline called "YourFLMNameExperiment" that implements 

FirstLineMatcher.

(* the compiler should automatically add the unimplemented methods). And Implement those methods.

2) add the algorithm to similaritymeasures table in schemamatching database which is a part of ORE system.

3) add the algorithm to the algorithms list in the class FLMList.

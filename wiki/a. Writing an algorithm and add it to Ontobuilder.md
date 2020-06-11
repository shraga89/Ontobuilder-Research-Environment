To create a new FLM follow these steps:

1)	The new Algorithm class should Implement AbstractAlgorithm for the package 

ac.technion.iem.ontobuilder.matching.algorithms.line1.common.

 2) 	Create a new class in the Appropriate package according to its type: 
                          
2.2	ac.technion.iem.ontobuilder.matching.algorithms.line1.domain  

2.3	ac.technion.iem.ontobuilder.matching.algorithms.line1.misc

2.4	ac.technion.iem.ontobuilder.matching.algorithms.line1.pivot

2.5	ac.technion.iem.ontobuilder.matching.algorithms.line1.precedence

2.6	ac.technion.iem.ontobuilder.matching.algorithms.line1.term

 3) add a new entrance to the XML file that describes the algorithms that are in the system which is in
 
ontobuilder .core/config/matching It is also recommended to define Parameters to Adjust the algorithm while

 it's run from the XML file.
 
 4) Update the Component  Ontobuilder in ORE: you can also connect the ORE project to ontobuilder.matching

 project directly with right click on ORE project in Eclipse and choosing "build path->configure build path",

 then,  in "project" tab  add ontobuilder.matching project and in libraries tab remove the Reference to

 ontobuilder.matching.jar. notice: you need to copy the algorithms.xml file everytime you change it into 

OntobuilderResearchEnvironment/config/matching.


After developing, you should compile Ontobuiler with a right click on build.xml file which is located in the 

main folder of ontobuilder.build, choose Run As->Ant Build.


After compiling you should copy the new ontobuilder.matching.jar which is created in 

ontobuilder.build/Ontobuilder to OntbuilderResearchEnvironment/lib.

/**
 * 
 */
package ac.technion.schemamatching.matchers.firstline;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Vector;


import java.util.ArrayList;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.ontology.Attribute;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
//import ac.technion.schemamatching.curpos.CurposTerm;
import ac.technion.schemamatching.matchers.MatcherType;
import ac.technion.schemamatching.matchers.firstline.MaximalCliquesWithPivot.Vertex;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;


/**
 * Wrapper for default configurated Ontobuilder Term-Value Matcher
 * 
 * @author Tomer Sagi
 */


public class Stat implements FirstLineMatcher {
	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getName()
	 */
	private class Concept extends HashSet<Integer> {
		public double alpha;
		public HashMap<Integer,Double> betas = new HashMap<Integer,Double>();
	};

	private class Model extends ArrayList<Concept> {
		public ArrayList<Double> alphas = new ArrayList<Double>();
		double dsqure = 0;
		int degreesOfFreedom = 0;
		double chiprob = 0;
	};
	
	public class HashTableWithLinkedList extends Hashtable<Integer, LinkedList<Long>>{};
	
	private class Vocabulary
	{
		public Vocabulary(){}
		

		public HashTableWithLinkedList ProcessedToOREID = new HashTableWithLinkedList();
		
	}
	
	private class Schema 
	{
		public Schema(){}
		
		public HashMap<Long,Integer> OREToProcessedID = new HashMap<Long,Integer>();
		public HashMap<Long, String> OREIDName = new HashMap<Long, String>();
	}
	
	private ArrayList<Schema> processedSchemas = new ArrayList<Schema>();
	private Vocabulary V = new Vocabulary();
	private HashMap<Integer, String> dict = new HashMap<Integer, String>();
	private HashSet<Concept> allConcepts = new HashSet<Concept>();
	private HashSet<Model> models = new HashSet<Model>();
	private HashMap<Integer,Integer> countIDs = new HashMap<Integer, Integer>();
	private ArrayList<Integer> connAtter = new ArrayList<>(); 
	private Integer numOfSchemas = 0;
	
	private double alpha = 0.05;
	private Model chosenModel = new Model();
	//private MatchInformation res;
	//private Ontology[] ontologiesArray;
	
	private ArrayList<Ontology> ontologiesArray = new ArrayList<>();

	
	//for multiple ontologies implemntation
	private HashMap<Schema,Ontology> schemaToOntologyMap = new HashMap<Schema,Ontology>();
	private HashMap<Pair<Ontology,Ontology>,MatchInformation> res = new HashMap<Pair<Ontology,Ontology>,MatchInformation>();
	
	// private ArrayList<ArrayList<Vertex>> legalCliques = new
	// ArrayList<ArrayList<Vertex>>();
	//

	//public Stat() {
	//}

	public String getName() {
		return "Ontobuilder Statistical Matcher";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#hasBinary()
	 */
	public boolean hasBinary() {
		return true;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getConfig()
	 */
	public String getConfig() {
		String config = "TermWeight";
		return config;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ac.technion.schemamatching.matchers.FirstLineMatcher#getType()
	 */
	public MatcherType getType() {
		return MatcherType.SYNTACTIC;
	}

	public int getDBid() {
		return 20;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ac.technion.schemamatching.matchers.FirstLineMatcher#match(com.modica
	 * .ontology.Ontology, com.modica.ontology.Ontology, boolean)
	 */

	
	
	
	
	
//------------------------------------------MATCH FUNCTIONS----------------------------------------------
	public MatchInformation match(Ontology candidate, Ontology target,
			boolean binary) {
		//res = new MatchInformation(candidate, target);
		
		ArrayList<Ontology> schemaSet = new ArrayList<Ontology>();
		Ontology[] ontoArray = new Ontology[2];
		
		
		//ontoArray[0]=target;
		//ontoArray[1] = candidate;
		
		
		schemaSet.add(target);
		schemaSet.add(candidate);
		
		//schemaSet[0]=candidate;
		//schemaSet[1]=target;
		//schemaSet.add(candidate);
		//schemaSet.add(target);
		 
		//MatchInformation result=match(ontoArray, binary);
		//HashMap<Pair<Ontology,Ontology>,MatchInformation> result=match(ontoArray, binary);
		
		HashMap<Pair<Ontology,Ontology>,MatchInformation> result=match(schemaSet, binary);
		
		Pair<Ontology, Ontology> key = new MutablePair<Ontology, Ontology>(target, candidate);
		System.out.println(result.get(key).getCopyOfMatches());
		return result.get(key);
		
	}

	public HashMap<Pair<Ontology,Ontology>,MatchInformation> match(ArrayList<Ontology> schemaSet, boolean binary)
	//public MatchInformation match(Ontology[] schemaSet, boolean binary)
	{

	
		
		ontologiesArray=schemaSet;
	//	schemaSet.toArray(ontologiesArray);


		
		//result intializaition
		/*
		Vector<Term>terms0 = ontologiesArray[0].getTerms(true);
		Vector<Term>terms1 = ontologiesArray[1].getTerms(true);
		for (Term t : terms0){
			for(Term t1:terms1){
				res.updateMatch(t, t1, 0.0);
			}
		}*/
		
		//preprocess + concensus handeling
		preProcess(schemaSet);
		// Build Vocabulary
		buildV();

		// Build Concepts from schemas and return models which cover V and are not intersecting in the concepts
		buildC();
		
		
		//make chi2 test for each 
		dynamicSelection();

		//choose the best model by the chi2 test and put the matching terms in res
		chooseBestModel();
		
		//return results
		/*MatchInformation resVal = new MatchInformation(ontologiesArray[0], ontologiesArray[1]);
		for (Term t : terms0){
			for(Term t1:terms1){
				resVal.updateMatch(t, t1, res.getMatchConfidence(t, t1));
			}
		}*/
		return res;
		
	}
	

	//--------------------------------------------PRE PEOCESS--------------------------	
	
	/*
	 * this function gives each term a local id 
	 * then checks for concensus attributes and removes them from the local id's repository
	 * all concensus attributes receive 1.0 grade in the result matrix
	 */
	//private void preProcess(Ontology[] schemaSet)
	private void preProcess(ArrayList<Ontology> schemaSet)
	{
		//update number of schemas in input
		//numOfSchemas = schemaSet.length;
		numOfSchemas = schemaSet.size();
		
		HashMap<String,Integer> stringIDs = new HashMap<String, Integer>();
		for (Ontology currentOntology : schemaSet) 
		{
			for(Ontology otherOntology: schemaSet)
			{
				if (currentOntology.equals(otherOntology))
				{
					continue;
				}
				Pair<Ontology, Ontology> key = new MutablePair<Ontology, Ontology>(currentOntology, otherOntology);
				Pair<Ontology, Ontology> reverseKey = new MutablePair<Ontology, Ontology>(otherOntology, currentOntology );
				if (res.keySet().contains(key) || res.keySet().contains(reverseKey))
				{
					continue;
				}
				else
				{
					res.put(key, new MatchInformation(otherOntology, currentOntology )); // MatchInformation(candidate, target);
				}
				
				
			}
			//create local schema instance from ontology
			//each schem holds the term's ORE ID match with the term name and local Id
			Schema schema = new Schema();
			Vector<Term> terms = currentOntology.getTerms(true);
			System.out.println("Current Schema terms count = " + currentOntology.getAllTermsCount());
			for (int i=0; i<currentOntology.getAllTermsCount();i++)
			{
				long id = terms.get(i).getId();
				String name = terms.get(i).getName();
				name = name.toLowerCase();
				name = name.replaceAll("_","");
				if(name!=""){
					schema.OREIDName.put(id, name);
					stringIDs.put(name, -1);
				}
			
			}
			processedSchemas.add(schema);	
			schemaToOntologyMap.put(schema, currentOntology);
		}
		
		//now we give local ids to the terms by string matching
		
		
		//do strings match with 1 threshold
		int id = 0;
		
		for(String str : stringIDs.keySet())
		{
			HashSet<String> matchedStrings = new HashSet<String>();
			for(String other : stringIDs.keySet())
			{
				//threshold is 1
				if(stringMatchGrade(str, other)>=1)
				{
					matchedStrings.add(other);
				}
				
			}
			
			
			for(String matchedString  :matchedStrings)
			{
				stringIDs.put(matchedString, id);
			}
			countIDs.put(id, -1);			
			id++;			
		}
		
		//make the ORE id to local id match
		for(Schema schema : processedSchemas)
		{	
			for(long OREID: schema.OREIDName.keySet())
			{
				String str = schema.OREIDName.get(OREID);
				int ID = stringIDs.get(str);
				schema.OREToProcessedID.put(OREID, ID);
			}
		}
		
		for(Integer atrID : countIDs.keySet())
		{
			Integer counter=0;
			for(Schema schema : processedSchemas)
			{
				if(schema.OREToProcessedID.values().contains(atrID)){
					counter++;
				}
			}
			countIDs.put(atrID, counter);
			
			
		}
		
		concensusHandeling();
	}
	

	
	
	private double stringMatchGrade(String a, String b)
	{
		double grade=0;
		int lengthA = a.length();
		int lengthB = b.length();
		if(lengthA>lengthB){
			if (a.contains(b)){
				grade = (double)lengthB/(double)lengthA;
			}
		}
		else
		{
			if (b.contains(a)){
				grade = (double)lengthA/(double)lengthB;
			}
		}
		return grade;
		
		
		
	}
	
	
	/*
	 * handle concensus attributes
	 * removes local id's from schemas if all schemas agrees on this id
	 */
	private void concensusHandeling() {
		for (int id : countIDs.keySet()){
			int counter = 0;
			for (Schema s : processedSchemas ){
				boolean flag = false;
				for(Long ID : s.OREToProcessedID.keySet()){
					//check if the term got this local id
					if(s.OREToProcessedID.get(ID)==id){
						counter++;
						flag = true;
						break;
					}
				}
				if (flag == false){
					break;
				}
			}
			//if all schemas agrees on the id
			if (counter == processedSchemas.size()){
				connAtter.add(id);
			}
		}
		System.out.println("Concensus attributes are ");
			for(Integer localID: connAtter){
				//remove concensus id's from schema
				for(Schema s:processedSchemas){
					for(Long ID : s.OREToProcessedID.keySet())
					{
						if (s.OREToProcessedID.get(ID).intValue()==localID.intValue())
						{
							for (Schema otherSchema: processedSchemas)
							{
								if(s.equals(otherSchema))
								{
									continue;
								}
								for(Long OtherID : otherSchema.OREToProcessedID.keySet())
								{
									if(otherSchema.OREToProcessedID.get(OtherID).intValue()==localID.intValue())
									{
										String otherName = otherSchema.OREIDName.get(OtherID);
										String name = s.OREIDName.get(ID);
										
										updateRes(s, otherSchema, name, otherName);
									}
									
								}
							}
						}
					}
					
					//boolean flag = true;
					//while (flag)
					//{
						//flag = false;
						//long tmpID = 0;
						///for(Long ID : s.OREToProcessedID.keySet()){
							//if (s.OREToProcessedID.get(ID).intValue()==localID.intValue()){
								//flag = true;
								//tmpID = ID;
								//this code is for handeling the returning results for 2 ontologies
								//for more ontologies this code must be changed
								//for (Schema otherSchema: processedSchemas){
									//if(s.equals(otherSchema)){
										//continue;
									//}
									//boolean otherFlag = true;
									//while (otherFlag){
										//otherFlag = false;
										//long tmpOtherID = 0;
										//for(Long OtherID : otherSchema.OREToProcessedID.keySet()){
											//if(otherSchema.OREToProcessedID.get(OtherID).intValue()==localID.intValue()){
												//flag = true;
												//tmpOtherID = OtherID;
												//break;
											//}
										//}
										//String otherName = otherSchema.OREIDName.get(tmpOtherID);
										//String name = s.OREIDName.get(ID);
										
										//updateRes(s, otherSchema, name, otherName);
										
										/*
										Term currentTerm = new Term(name);
										Term otherTerm = new Term(otherName);
										
										Ontology currentOntology = schemaToOntologyMap.get(s);
										Ontology otherOntology = schemaToOntologyMap.get(otherSchema);
										
										Vector<Term> currentTerms = currentOntology.getTerms(true);
										Vector<Term> otherTerms= otherOntology.getTerms(true);
										
										//Vector<Term> terms0 = ontologiesArray[0].getTerms(true);
										//Vector<Term> terms1 = ontologiesArray[1].getTerms(true);
										
										currentTerm  = getTermByName(name, currentTerms);
										otherTerm = getTermByName(otherName, otherTerms);
										
										
									
										
										Pair<Ontology, Ontology> key =  new Pair<Ontology, Ontology>(currentOntology, otherOntology);
										Pair<Ontology, Ontology> reverseKey = new Pair<Ontology, Ontology>(otherOntology, currentOntology );
										
										if (res.keySet().contains(key))
										{
											res.get(key).updateMatch(currentTerm, otherTerm, 1.0);
										}
										else
										{
											res.get(reverseKey).updateMatch(currentTerm, otherTerm, 1.0);
										}
										
										//res.updateMatch(term0, term1, 1.0);
										
										 
										 */
										//otherSchema.OREIDName.remove(tmpOtherID);
										//otherSchema.OREToProcessedID.remove(tmpOtherID);
										
									//}								
								//}
								//break;
							//}
							
						//}
						//s.OREIDName.remove(tmpID);
						//s.OREToProcessedID.remove(tmpID);
					//}
				}
				V.ProcessedToOREID.remove(localID);
				
				for(Schema s:processedSchemas)
				{
					long key = 0;
					for (Long keyIterator:  s.OREToProcessedID.keySet())
					{
						if (s.OREToProcessedID.get(keyIterator).intValue() == localID.intValue()){
							key = keyIterator;
							break;
						}
					}
					s.OREIDName.remove(key);
					s.OREToProcessedID.remove(key);
				}
				
				System.out.print(localID + " ");
			}
			System.out.println("");				
	}
	
	
	
	
//----------------------------------BUILD V---------------------------------------------------------------------
	/*
	 * this function will build a vocabulary V which is the union of all local id's given by the algorithem
	 * to the attributes of the schemas
	 */
	private void buildV() 
	{
		for(Schema schema : processedSchemas)
		{
			for(long OREID:schema.OREToProcessedID.keySet())
			{
				int ID = schema.OREToProcessedID.get(OREID);
				if(V.ProcessedToOREID.containsKey(ID))
				{
					LinkedList<Long> list = V.ProcessedToOREID.get(ID);
					list.add(OREID);
				}
				else
				{
					LinkedList<Long> list = new LinkedList<Long>();
					list.add(OREID);
					V.ProcessedToOREID.put(ID, list);
				}
			}
		}
		System.out.println("V is");
		for(int id: V.ProcessedToOREID.keySet()){
			System.out.print(id + " ");
		}
		System.out.println("");
	}

	
	
//---------------------------------BUILD CONCEPTS & MODELS----------------------------------------------------------------
	
	/*
	 * this function will build the concepts and models
	 * concepts are attributes from different schemas which are foreign to each other
	 * e.g. if attribute 1 is not in the scema with attribute 2 then they can be in a concept
	 * Model is a collection of concepts
	 */
	
	private void buildC() 
	{
		HashSet<Integer> vertexesSet = new HashSet<Integer>();
		//build a graph which represents the relations betwwen each attribute in the schema
		//an edge will be added between two vertexes if the are foreign to each other
		String graph = buildGraph(vertexesSet);
		// find all cliques in the graph
		ArrayList<ArrayList<Vertex>> maxCliques = findCliques(graph, vertexesSet);
		// find subsets of all maximal cliques
		for (ArrayList<Vertex> set : maxCliques) {
			// extracts the subset
			ArrayList<ArrayList<Vertex>> subSets = getSubsets(set);
			// subSets is an ArrayList of all subsets
			//transform each subset into a concept
			for (ArrayList<Vertex> subset : subSets) {
				Concept c = new Concept();
				for(Vertex v : subset){
					c.add(v.getID());
				}
				
				allConcepts.add(c);
			}
		}
		System.out.println("number of concepts = " + allConcepts.size() );
		
		System.out.println("grinding the machine...." );

		
		
		//use the first function to really receive all covers of V
		//otherwise use the second function to get only the minimal set cover of V for the group of maximal concepts
		//for each maximal sized concept there will be a minimal set cover of V which 
		//is retrived by the greedy set cover algorithem which will start with this concept
		
		//models = getNonIntersectingModels(allConcepts);
		models = setCover(allConcepts);
		
	
		System.out.println("done! number of models = " + models.size() );
	}
		
	/*
	 * given a graph, this function will find all cliques in the graph
	 * an open code solution which implements the Bron-Kerbosch algorithm
	 */
	private ArrayList<ArrayList<Vertex>> findCliques(String graph, HashSet<Integer> vertexesSet) {
		BufferedReader bufReader = null;
		ArrayList<ArrayList<Vertex>> maxCliques = new ArrayList<ArrayList<Vertex>>();
		bufReader = new BufferedReader(new StringReader(graph));
		
		MaximalCliquesWithPivot ff = new MaximalCliquesWithPivot();
		
		try {
			int i = 0;
			ff.readNextGraph(bufReader, vertexesSet);
			ff.Bron_KerboschPivotExecute();
			maxCliques = ff.cliques;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Exiting : " + e);
		} finally {
			try {
				bufReader.close();
			} catch (Exception f) {

			}
		}
		return maxCliques;
	}

	
	/*
	 * this function will build a graph which represnt the relations between attributes
	 * an edge will be added between u and v if u and v are not in the same schema
	 */
	private String buildGraph(HashSet<Integer> vertexesSet) {
		// build graph
		String graph = new String();
		String edges = new String();
		int edgesCounter = 0;
		
		for(Schema s : processedSchemas){
			System.out.print("\n");
			for (Long ID : s.OREToProcessedID.keySet()) {
				System.out.print(s.OREToProcessedID.get(ID) + " ");
			}
			System.out.print("\n");
		}
		
		for(Schema s : processedSchemas)
		{
			
			for(Long ID : s.OREToProcessedID.keySet())
			{
				int processedID = s.OREToProcessedID.get(ID);
				vertexesSet.add(processedID);
				for (Schema otherSchema: processedSchemas)
				{
					if(otherSchema == s){
						continue;
					}
					boolean flag = false;
					for (Long otherID : otherSchema.OREToProcessedID.keySet()){
						if (otherSchema.OREToProcessedID.get(otherID) == processedID){
							flag = true;
						}
					}
					if (flag == true){
						continue;
					}
					
					for(Long otherID : otherSchema.OREToProcessedID.keySet())
					{
						int otherProcessedID = otherSchema.OREToProcessedID.get(otherID);
						
						boolean otherflag = false;
						for (Long localID : s.OREToProcessedID.keySet()){
							if (s.OREToProcessedID.get(localID) == otherProcessedID){
								otherflag = true;
							}
						}
						if (otherflag == true){
							continue;
						}
						
						vertexesSet.add(otherProcessedID);
						String edge = String.valueOf(processedID)+" "+String.valueOf(otherProcessedID)+"\n";
						String reverseEdge = String.valueOf(otherProcessedID)+" "+String.valueOf(processedID)+"\n";
						if(edges.contains(edge)||edges.contains(reverseEdge))
						{
							continue;
						}
						edges+=edge;
						edgesCounter++;
					}
				}

			}
		}
		
		graph=graph+String.valueOf(vertexesSet.size())+"\n";
		graph=graph+String.valueOf(edgesCounter)+"\n";
		graph+=edges;
		
		return graph;
	}


	
	private void printModel(Model m){
		for(Concept c : m){
			for(int i: c){
				System.out.print(i + " ");
			}
			System.out.println("");
		}
		
	}
	
	//checks if V is coverd by the input model
	private boolean VisCovered(Model m) {
		for (int ID: V.ProcessedToOREID.keySet()) {
			boolean flag = false;
			for (Concept c : m) {
				for (Integer i : c) {
					if (i.intValue()== ID) {
						flag = true;
						break;
					}
				}
				if (flag == true) {
					break;
				}
			}
			if (flag == false) {

				return false;
			}
		}
		return true;
	}

	//checks if concepts in the given model are foreign to each other
	private boolean noOverLapingConcepts(Model m) {

		for (Concept c : m) {
			for (Integer i : c) {
				for (Concept otherConcept : m) {
					if (c.equals( otherConcept)) {
						continue;
					}
					
					for(Integer j : otherConcept){
						if (i.intValue()==j.intValue()){

							return false;
						}
					}
				}
			}
		}
		return true;
	}

	//template to get subsets of given set
	private <T> ArrayList<ArrayList<T>> getSubsets(ArrayList<T> set) {
		ArrayList<ArrayList<T>> subsetCollection = new ArrayList<ArrayList<T>>();

		if (set.size() == 0) {
			subsetCollection.add(new ArrayList<T>());
		} else {
			ArrayList<T> reducedSet = new ArrayList<T>();

			reducedSet.addAll(set);

			T first = reducedSet.remove(0);
			ArrayList<ArrayList<T>> subsets = getSubsets(reducedSet);
			subsetCollection.addAll(subsets);

			subsets = getSubsets(reducedSet);

			for (ArrayList<T> subset : subsets) {
				subset.add(0, first);
			}

			subsetCollection.addAll(subsets);
		}

		return subsetCollection;
	}
	
/*
 * this function will return every model which covers V with foreign concepts
 * use this function only for small schemas otherwise you'll get an out of memory exception	
 */
private HashSet<Model> getNonIntersectingModels(HashSet<Concept> concepts){
		
		HashSet<Model> modelsCollection = new HashSet<Model>();
		Model emptySet = new Model();

		modelsCollection.add(emptySet);
								
		//make a set from each object T in the input set
		HashSet<Model> modelsFromInput = new HashSet<Model>();
		for(Concept c : concepts){
			if(c.size()!=0){
				Model localModel = new Model();
				localModel.add(c);
				modelsFromInput.add(localModel);
			}									
		}
		
		HashSet<Model> legalCollection = new HashSet<Model>();
		long i = 0;
		for (Model localModel : modelsFromInput){
			HashSet<Model> modelsCollectionCopy = new HashSet<Model>();
			modelsCollectionCopy.addAll(modelsCollection);
			Model emptyModel = new Model();
			modelsCollectionCopy.add(emptyModel);
			for (Model copiedModel: modelsCollectionCopy){
				i++;
				if (i % 1000000 == 0){
					System.out.println("Checked " + i + " models");
				}
				Model newModel = new Model();
				newModel.addAll(localModel);
				newModel.addAll(copiedModel);
				if(noOverLapingConcepts(newModel)==true){
				
					modelsCollection.add(newModel);
					if(VisCovered(newModel)==true){
						Model m = new Model();
						m.addAll(newModel);
						legalCollection.add(m);
					}														
				}								
			}
			modelsCollectionCopy = null;			
		}		
		return legalCollection;
	}

/*
 * this function will return a set of minimal concept number which covers V
 * the function is implementation of greedy minimal set cover
 * each set cover is foreign in concepts
 * the function will check who are the maximal sized concepts
 * for each maximal sized concept the greedy algorithm will start where the concept is already in the
 * output set
 */
private HashSet<Model> setCover(HashSet<Concept> concepts){

	HashSet<Model> mContainer = new HashSet<Model>();
	HashSet<Concept> maximalSizeConceptContainer = new HashSet<Concept>();
	//check for maximal sized concepts
	int maxSize = 0;
	for(Concept c: concepts){
		if (c.size()>maxSize){
			maxSize = c.size();
		}
	}
	for (Concept c: concepts){
		if (c.size()==maxSize){
			maximalSizeConceptContainer.add(c);
		}
	}
	for(Concept c: maximalSizeConceptContainer){
		Model m = new Model();
		HashSet<Concept> U = new HashSet<Concept>();
		U.addAll(concepts);
		HashSet<Integer> vValues = new HashSet<Integer>();
		vValues.addAll(V.ProcessedToOREID.keySet());
		m.add(c);
		//remove current maxmial sized concept from U - the input set
		U.remove(c);
		//remove c values from V
		for (Integer i : c){
			vValues.remove(i);
		}
		
		//remove all intersecting concepts with c from U
		boolean outerFlag = true;
		while (outerFlag){
			
			outerFlag = false;
			for(Concept c2: U){
				for (Integer i : c2){
					for (Integer j : c){
						if (i.intValue()==j.intValue()){
							outerFlag = true;
							break;
						}
					}
					if (outerFlag == true){
						break;
					}
				}
				if (outerFlag == true){
					U.remove(c2);
					break;
				}					
			}
		}
		
		//do greedy algorithm
		while (vValues.size()!=0){			
			int localMaxSize = 0;
			Concept maxSizedConcept = new Concept();
			for(Concept c2: U){
				if(c2.size()==0){
				continue;
				}
				if (c2.size() > localMaxSize){							
					localMaxSize = c2.size();
					maxSizedConcept = c2;							
					}
				}
				//add maximal sized concept to output set and remove from input set
				m.add(maxSizedConcept);
				U.remove(maxSizedConcept);
				
				//remove maximal concept values from V
				for (Integer i : maxSizedConcept){
					vValues.remove(i);
				}
				//remove all interceting concepts from input
				outerFlag = true;
				while (outerFlag){
					outerFlag = false;
					for(Concept c2: U){
						for (Integer i : c2){
							for (Integer j : maxSizedConcept){
								if (i.intValue()==j.intValue()){
									outerFlag = true;
									break;
								}
							}
							if (outerFlag == true){
								break;
							}
						}
						if (outerFlag == true){
							U.remove(c2);
							break;
						}					
					}
				}	
		}		
		mContainer.add(m);
	}	
	return mContainer;			
}
	

/***********************************CHI SQUARED TEST **************/
	/*
	 * this function will calculte chi2 test for each model
	 * although chooseBestModel should be inside this functino logicaly
	 * for readablity reasons we left it on the main "Match" function
	 */
	private void dynamicSelection(){
		System.out.println("calculating chi2 test for each model");
		long i =0;
		for(Model m: models){
			i++;
			if(i%10==0){
				System.out.println("done for " + i + " models");
			}
			//calculate alpha and betas for each concept
			//alpah = the probabilty for concept to be in a schema = (sum of apearances of each attribute from this concept in the given input)/(number of schemas)
			//beta = the probabilty of each attribute in a concept to be in a schema = (sum of apearances of this attribute in the input)/(sum of apearances of each attribute from this concept in the given input)
			
			for(Concept c: m){
				
				calculateAlphaBeta(c);
				m.alphas.add(c.alpha);			
			}
			// do some veriations of the data structures so chi2 test can be executed properly
			ArrayList<Concept> LessThanOne = new ArrayList<Concept>();
			ArrayList<Concept> EqualsOne = new ArrayList<Concept>();
			for(Concept c : m){
				if(c.alpha==1){
					EqualsOne.add(c);
				}
				else{
					LessThanOne.add(c);
				}
			}
			ArrayList<HashSet<HashSet<Integer>>> setsList = new ArrayList<HashSet<HashSet<Integer>>>();
			for (Concept c:EqualsOne)
			{
				setsList.add(setFromConcept(c, false));
			}
			
			for (Concept c:LessThanOne)
			{
				setsList.add(setFromConcept(c, true));
			}
			//build every schema that the model can build and activate some calculations on it
			CartesianProduct(setsList,m);
			
			//do chi2 test on m
			//m degrees of freedom are updated on CartesianProduct
			m.degreesOfFreedom--;
			ChiSquareUtils chi = new ChiSquareUtils();
			m.chiprob = chi.pochisq(m.dsqure, m.degreesOfFreedom);
			System.out.println("For Model");
			printModel(m);
			System.out.println("dsquare = " + m.dsqure + " degrees of freedom = " + m.degreesOfFreedom + " chiprob = " + m.chiprob);;
		}
	}
	
	/*
	 * this function will return how many apearances in the input are there for the given schema
	 */
	private int numOfApp(HashSet<Integer> modelSchema) {
		int counter =0;
		for(Schema s:processedSchemas){
			int vertexCounter = 0;
			for (Long ID: s.OREToProcessedID.keySet()){
				for(Integer i: modelSchema){
					if(i.intValue()==s.OREToProcessedID.get(ID).intValue()){
						vertexCounter++;
					}
				}
			}
			int schemaCounter =0;
			for(Integer i: modelSchema){
				for (Long ID: s.OREToProcessedID.keySet()){
					if(i.intValue()==s.OREToProcessedID.get(ID).intValue()){
						schemaCounter++;
					}
				}
			}
			if(schemaCounter==modelSchema.size() && vertexCounter == s.OREToProcessedID.size()){
				counter++;
			}
		
		}
	return counter;
}
/*
 * this function will return the probabilty that a given schema is created from a given model
 * if an attribute a from a concept c in the model is in the schema we multiply the current reuslt by
 * a.beta*c.alpha
 * 
 * if no attribute from c is in the given schema we multiply the current result by 1-c.alpha
 */
	private double instProb(HashSet<Integer> modelSchema, Model m) {
	double instantiationProb=1;
	for(Concept c:m){
		boolean flag = false;
		for (int i: c){
			for(int j: modelSchema){
				if (i==j){
					double alpah = c.alpha;
					double beta = c.betas.get(i);
					instantiationProb = instantiationProb*alpah*beta;
					flag = true;
					break;
				}
				
			}
			if(flag == true){
				break;
			}
			
		}
		if (flag == false){
			instantiationProb*=(1-c.alpha);
		}
	}
	return instantiationProb;
}

	//calculate alpha and betas for each concept
	//alpah = the probabilty for concept to be in a schema = (sum of apearances of each attribute from this concept in the given input)/(number of schemas)
	//beta = the probabilty of each attribute in a concept to be in a schema = (sum of apearances of this attribute in the input)/(sum of apearances of each attribute from this concept in the given input)
	private void calculateAlphaBeta(Concept c){
		Integer sumOfOjs = 0;
		for(int i: c){
			sumOfOjs+=countIDs.get(i);
		}
		
		for(int i: c){
			double beta = (double)countIDs.get(i)/(double)sumOfOjs;
			
			c.betas.put(i,beta);
		}
		
		double alpha = (double)sumOfOjs/(double)numOfSchemas;
		
		c.alpha=alpha;
	}
	
	/*
	 * this function choose the best model with the biggest chi2 test value
	 */
	private void chooseBestModel() {
		double maxchiprob = 0;
		for(Model m : models){
			if (m.chiprob > maxchiprob){
				chosenModel = m;
				maxchiprob = m.chiprob;
			}
		}
		
		//update results
		//this code need to be modified if the number of input ontologies>2
		for (Concept c: chosenModel){
			if (c.size()<2){
				continue;
			}
			else{
				HashMap<Integer, ArrayList<Schema>> schemaMappedByLocalID = new HashMap<Integer, ArrayList<Schema>>();
				for(Integer i: c)
				{
					for (Schema s : processedSchemas)
					{
						if (s.OREToProcessedID.values().contains(i))
						{
							if (schemaMappedByLocalID.get(i) == null)
							{
								ArrayList<Schema> schemaList = new ArrayList<Schema>();
								schemaList.add(s);
								schemaMappedByLocalID.put(i, schemaList);
							}
							else
							{
								ArrayList<Schema> schemaList = schemaMappedByLocalID.get(i);
								schemaList.add(s);
								schemaMappedByLocalID.put(i, schemaList);
							
							}
							//schemaMappedByLocalID.put(i, s);
							//break;
						}
					}
				}
				
				for (Integer i:schemaMappedByLocalID.keySet() )
				{
					for (Schema s: schemaMappedByLocalID.get(i))
					{
						for (Integer j: schemaMappedByLocalID.keySet())
						{
							if (i.equals(j))
							{
								continue;
							}
							for (Schema otherSchema :schemaMappedByLocalID.get(j))
							{
								String name ="";
								String otherName = "";
								
								for(Long ID : s.OREToProcessedID.keySet()){
									if(s.OREToProcessedID.get(ID).intValue()==i.intValue()){										
										name = s.OREIDName.get(ID);
										
									}
								}
								for(Long ID : otherSchema.OREToProcessedID.keySet()){
									if(otherSchema.OREToProcessedID.get(ID).intValue()==j.intValue()){										
										otherName = otherSchema.OREIDName.get(ID);
										
									}
								}
								updateRes(s, otherSchema, name, otherName);
								//Ontology currentOntology = schemaToOntologyMap.get(s);
								//Ontology otherOntology = schemaToOntologyMap.get(otherSchema);
								
								//Vector<Term> currentTerms = currentOntology.getTerms(true);
								//Vector<Term> otherTerms= otherOntology.getTerms(true);
								
								//Vector<Term> terms0 = ontologiesArray[0].getTerms(true);
								//Vector<Term> terms1 = ontologiesArray[1].getTerms(true);
								//Term currentTrem =  getTermByName(name, currentTerms);
																
								//Term otherTerm = getTermByName(otherName, otherTerms);
								
								
							}
							
						}
					}
					
				}
				/*
				HashMap<Integer, String> termMappedByLocalID = new HashMap<Integer, String>();
				for(Integer i : schemaMappedByLocalID.keySet()){
					String name;
					long OREID = 0;
					Schema s = schemaMappedByLocalID.get(i);
					for(Long ID : s.OREToProcessedID.keySet()){
						if(s.OREToProcessedID.get(ID).intValue()==i.intValue()){
							OREID=ID;
							name = s.OREIDName.get(ID);
							
							termMappedByLocalID.put(i,name);
							break;
						}
					}
				}
				Term term0 = null;
				Term term1 = null;
				Vector<Term> terms0 = ontologiesArray[0].getTerms(true);
				for(Term t: terms0){
					for(String localTermName: termMappedByLocalID.values()){
						String termName = t.getName().toLowerCase();
						termName = termName.replaceAll("_","");
						if(termName.equals(localTermName)){
							term0=t;
						}
					}
				}
				Vector<Term> terms1 = ontologiesArray[1].getTerms(true);
				for(Term t: terms1){
					for(String localTermName: termMappedByLocalID.values()){
						String termName = t.getName().toLowerCase();
						termName = termName.replaceAll("_","");
						if(termName.equals(localTermName)){
							term1=t;
						}
					}
				}
				res.updateMatch(term0, term1, 1.0);
				*/
			}
		}
		
		System.out.println("Chosen Model is:");
		printModel(chosenModel);
		
	}
	
	//auxilary function 
	private HashSet<HashSet<Integer>> setFromConcept(Concept c, boolean addEmptySet){
		HashSet<HashSet<Integer>> setOfSets = new HashSet<HashSet<Integer>>();
		for(int i: c){
			HashSet<Integer> newSet = new HashSet<Integer>();
			newSet.add(i);
			setOfSets.add(newSet);
		}
		if(addEmptySet==true){
			HashSet<Integer> newSet = new HashSet<Integer>();
			setOfSets.add(newSet);
		}
		return setOfSets;
	}
	
	private HashSet<HashSet<Integer>> CartesianProduct(HashSet<HashSet<Integer>> groupA, HashSet<HashSet<Integer>> groupB)
	{
		HashSet<HashSet<Integer>> newGroups= new HashSet<HashSet<Integer>>();
		for (HashSet<Integer> set: groupA)
		{
			for (HashSet<Integer> otherSet: groupB)
			{
				HashSet<Integer> newSet = new HashSet<Integer>();
				newSet.addAll(set);
				newSet.addAll(otherSet);
				newGroups.add(newSet);
			}
		}
		return newGroups;
	}
	
	//this function has built to enhance memory complexity issues
	//it will do calculations for the model while building schems that can be build from the model
	//with memory complexity of model.size^2 at most
	private void CartesianProduct(ArrayList<HashSet<HashSet<Integer>>> setsList, Model m)
	{
		int index=0;
		HashSet<Integer> res=new HashSet<Integer>();
		_CartesianProduct(setsList,index,res,m);
	}
	
	/*
	 * this is a recursion which calculates for each model its D2 - the chi2 test statisti
	 * the functino will build every schema possible for this model
	 * for each schema it will caluclate its instastion probabilty and add it to the model's instansiatio probality
	 * each schema will add 1 degree of freedon to the model
	 */
	private void _CartesianProduct(ArrayList<HashSet<HashSet<Integer>>> conceptList, int index, HashSet<Integer> res, Model m)
	{
		if(index >= conceptList.size())
		{
			return;
		}
		HashSet<HashSet<Integer>> concept=conceptList.get(index);
		for (HashSet<Integer> atr: concept){
			
			if(index == conceptList.size()-1)
			{
				//HashSet<Integer> modelSchema = new HashSet<Integer>();
				//modelSchema.addAll(res);
				//modelSchema.addAll(atr);
				res.addAll(atr);
				double dSqure = 0;
				//double instantiationProb = instProb(modelSchema, m);
			//	int numberOfAppearances = numOfApp(modelSchema);
				double instantiationProb = instProb(res, m);
				int numberOfAppearances = numOfApp(res);
				dSqure = (Math.pow(numberOfAppearances - numOfSchemas*instantiationProb,2)/((double)numOfSchemas*instantiationProb));
				m.dsqure+=dSqure;
				m.degreesOfFreedom++;
				res.removeAll(atr);
				
			}
			else
			{
				/*
				HashSet<Integer> resToSend = new HashSet<Integer>();
				resToSend.addAll(res);
				resToSend.addAll(atr);
				_CartesianProduct(conceptList,index+1,resToSend, m);
				*/
				res.addAll(atr);
				_CartesianProduct(conceptList,index+1,res, m);
				res.removeAll(atr);
				
			}

		}
	}
	

	
	private Term getTermByName(String name, Vector<Term>terms){
		Term result = null;
		for(Term t: terms){
			String termName = t.getName().toLowerCase();
			termName = termName.replaceAll("_","");
			if(termName.equals(name)){
				result = t;
			}
		}
		return result;
		
	}
	
	private void updateRes(Schema currentSchema, Schema otherSchema, String currentName, String otherName)
	{
		//Term currentTerm = new Term(name);
		//Term otherTerm = new Term(otherName);
		if (currentSchema == otherSchema)
		{
			return;
		}
		Ontology currentOntology = schemaToOntologyMap.get(currentSchema);
		Ontology otherOntology = schemaToOntologyMap.get(otherSchema);
		
		Vector<Term> currentTerms = currentOntology.getTerms(true);
		Vector<Term> otherTerms= otherOntology.getTerms(true);
		
		//Vector<Term> terms0 = ontologiesArray[0].getTerms(true);
		//Vector<Term> terms1 = ontologiesArray[1].getTerms(true);
		
		Term currentTerm  = getTermByName(currentName, currentTerms);
		Term otherTerm = getTermByName(otherName, otherTerms);
		
		
	/*	for(Term t: terms0){
			String termName = t.getName().toLowerCase();
			termName = termName.replaceAll("_","");
			if(termName.equals(name)){
				term0=t;
			}
		}
		for(Term t: terms1){
			String termName = t.getName().toLowerCase();
			termName = termName.replaceAll("_","");
			if(termName.equals(otherName)){
				term1=t;
			}
		}*/
		
		Pair<Ontology, Ontology> key =  new MutablePair<Ontology, Ontology>(currentOntology, otherOntology);
		Pair<Ontology, Ontology> reverseKey = new MutablePair<Ontology, Ontology>(otherOntology, currentOntology );
		
		if (res.keySet().contains(key))
		{
			res.get(key).updateMatch(currentTerm, otherTerm, 1.0);
		}
		else
		{
			res.get(reverseKey).updateMatch( otherTerm,currentTerm, 1.0);
		}
		
	}
}

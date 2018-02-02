package ac.technion.schemamatching.matchers.firstline;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;

import org.apache.lucene.search.Similarity;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mallardsoft.tuple.Pair;
import ac.technion.iem.ontobuilder.core.ontology.Attribute;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.OntologyClass;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;
import edu.cmu.lti.ws4j.util.PorterStemmer;
import edu.stanford.nlp.util.StringUtils;



public class GLUEMapOntologies implements FirstLineMatcher {

	@Override
	public String getName() {
		return "GLUE";
	}
	public GLUEMapOntologies()
	{}

	@Override
	public boolean hasBinary() {
		return false;
	}

	@Override
	public MatchInformation match(Ontology candidate, Ontology target, boolean binary) {
		Vector<Term> candidateTerms = candidate.getTerms(true);
		Vector<Term> targetTerms = target.getTerms(true);
		File candidateFile = candidate.getFile();
		System.out.println("working on candidate file "+candidateFile.getName());
		String fileDir = candidateFile.getParent();
		File candidateInstanceFile = new File(fileDir,candidate.getName().substring(0,candidate.getName().length()+(candidate.getName().contains(".xsd")?-4:0))+".xml");
		File targetFile=target.getFile();
		System.out.println("working on target file "+targetFile.getName());
		String targetDir=targetFile.getParent();
		File targetInstanceFile= new File(targetDir,target.getName().substring(0,target.getName().length()+(target.getName().contains(".xsd")?-4:0))+".xml");
		MatchInformation result = new MatchInformation(candidate,target);
		GLUEMatcher matcher = new GLUEMatcher(candidateTerms,targetTerms,candidateInstanceFile,targetInstanceFile,candidate, target, 1);
		HashMap<HashMap<Term, Term>, Double> similarity;
		if (matcher.getFlag() == 2){
			GLUEMatcher matcherName = new GLUEMatcher(candidateTerms,targetTerms,candidateInstanceFile,targetInstanceFile,candidate, target, 0);
			GLUEMatcher matcherContent = new GLUEMatcher(candidateTerms,targetTerms,candidateInstanceFile,targetInstanceFile,candidate, target, 1);
			HashMap<HashMap<Term, Term>, Double> similarityName = matcherName.GLUEAlgorithm();
			HashMap<HashMap<Term, Term>, Double> similarityContent = matcherContent.GLUEAlgorithm();
		    similarity = metaLearner(similarityName,similarityContent, candidateTerms, targetTerms);
		} else {
		    similarity = matcher.GLUEAlgorithm();
		}
		for (Term A: candidateTerms){
			Double maxEff = (double) 0;
			String maxB = "";
			for (Term B: targetTerms){
				HashMap <Term,Term> pairTerm= new HashMap<Term, Term>();
				pairTerm.put(A, B);
				Double eff = similarity.get(pairTerm);
				if (eff == null){
					result.updateMatch(B, A, 0);
					eff=(double)0;
				}
				else{
					result.updateMatch(B, A, eff);
				}
				if (eff >= maxEff){
					maxEff=eff;
					maxB=B.getName();
				}

			}
			System.out.println("A = " + A.getName() + " B = "+ maxB + " sim = " + maxEff.toString());
//			System.out.println(result.getCopyOfMatches());
			maxEff = (double)0;
			maxB = "";
		}	
		return result;
	}

	@Override
	public String getConfig() {
		return null;
	}

	@Override
	public MatcherType getType() {
		return MatcherType.INSTANCE;
	}

	@Override
	public int getDBid() {
		return 37;
	}
	

	
	/**
	 * calculates the combined probabilities for the meta learner : 85% for the content learner and 15% for the name learner
	 * @param similarityName
	 * @param similarityContent
	 * @param candidateTerms
	 * @param targetTerms
	 * @return similarity matrix for each pair of terms 
	 */
	public HashMap<HashMap<Term, Term>, Double> metaLearner(HashMap<HashMap<Term, Term>, Double> similarityName, HashMap<HashMap<Term, Term>, Double> similarityContent, Vector<Term> candidateTerms, Vector<Term> targetTerms){
		HashMap<HashMap<Term, Term>, Double> result = new HashMap<HashMap<Term, Term>, Double>(); 
		for (Term A: candidateTerms){
			for (Term B: targetTerms){
				HashMap <Term,Term> pairTerm= new HashMap<Term,Term>();
				pairTerm.put(A, B);
				Double effName = similarityName.get(pairTerm);
				Double effContent = similarityContent.get(pairTerm);
				if (effName == null || effContent== null){
					result.put(pairTerm, (double)0);
				}
				else{
					result.put(pairTerm, 0.15*effName + 0.85*effContent);
				}
			}
		}
		return result;
	}

	/**
	 * the main class that creates all the matchers (the 3 algorithms- name, content and meta)
	 * according to the flag received : 0- name learner; 1- content learner; 2- meta learner
	 */
class GLUEMatcher{
	public final File targetFile;
	public final File candidateFile;
	public final Vector<Term> candidateTerms;
	public final Vector<Term> targetTerms;
	public HashMap<String,Double> countInstancesPerTerm;
	private final String delimiter = "@";
	private final Integer learnerFlag; /*0- name learner; 1- content learner; 2- meta learner*/
	private ContentTermMatcher contentTermMatcherC;
	private ContentTermMatcher contentTermMatcherT;
	private NameTermMatcher nameTermMatcherC;
	private NameTermMatcher nameTermMatcherT;
	private final Ontology o1;
	private final Ontology o2;
	public GLUEMatcher(Vector<Term> cTerms,Vector<Term> tTerms,File candidateFile,File targetFile,Ontology o1, Ontology o2,  Integer learnerFlag){
		this.targetFile = targetFile;
		this.candidateFile = candidateFile;
		this.candidateTerms = cTerms;
		this.targetTerms= tTerms;
		this.countInstancesPerTerm = new HashMap<String, Double>();
		this.learnerFlag = learnerFlag;
		this.o1=o1;
		this.o2=o2;
	}
	public Integer getFlag(){
		return learnerFlag;
	}

	
/**
 * Create the name and content matchers
 */
	private void createMatchers(){
	    contentTermMatcherC = new ContentTermMatcher(candidateFile,candidateTerms,delimiter);
		contentTermMatcherT = new ContentTermMatcher(targetFile,targetTerms,delimiter);
		nameTermMatcherC = new NameTermMatcher(candidateFile, candidateTerms,delimiter);
		nameTermMatcherT = new NameTermMatcher(targetFile, targetTerms,delimiter);
		try 
		{ 
			contentTermMatcherC.createContentTermMatcher();
			contentTermMatcherT.createContentTermMatcher();
			nameTermMatcherC.createNameTermMatcher();
			nameTermMatcherT.createNameTermMatcher();
			
		}
		catch (ParserConfigurationException | SAXException | IOException e) {
			System.exit(1);
		}
	}

	/**
	 * Running the GLUE algorithm -
	 * 1. checks the flag that was set by the user, and use the right matcher
	 * 2. create a bag of words for each ontology
	 * 3. each term in every ontology - send the terms to the right learner
	 * @return sim - the similarity matrix
	 */
	public  HashMap<HashMap<Term, Term>, Double> GLUEAlgorithm(){	
		createMatchers();
		HashMap<String,ArrayList<ArrayList<String>>> bagFullO1 = new HashMap<String,ArrayList<ArrayList<String>>> ();
		HashMap<String,ArrayList<ArrayList<String>>> bagFullO2 = new HashMap<String,ArrayList<ArrayList<String>>> ();
		BayesLearner learnerO1O2;
		BayesLearner learnerO2O1;
		HashMap<String,Double> counterO1 = new HashMap<String,Double>();
		HashMap<String,Double> counterO2 = new HashMap<String,Double>();
		HashMap<String,ArrayList<String>> bagWordsO1 = new HashMap<String,ArrayList<String>>();
		HashMap<String,ArrayList<String>> bagWordsO2 = new HashMap<String,ArrayList<String>>();
		Double sumO1;
		Double sumO2;
		if (learnerFlag == 0){
			counterO1 = nameTermMatcherC.GetTermNameCounter();
			counterO2= nameTermMatcherT.GetTermNameCounter();
			sumO1=countNumInstances(counterO1);
			sumO2=countNumInstances(counterO2);
	
			learnerO1O2= new BayesLearner(nameTermMatcherC.GetTermNameBag(), nameTermMatcherC.GetTermNameCounter(), sumO1, learnerFlag); //classify from O2 to O1. take a d from O2, and A from O1
			learnerO2O1=new BayesLearner(nameTermMatcherT.GetTermNameBag(), nameTermMatcherT.GetTermNameCounter(),sumO2, learnerFlag);
			bagWordsO1 =nameTermMatcherC.GetTermNameBag();
			bagWordsO2 =nameTermMatcherT.GetTermNameBag();
			bagFullO1=nameTermMatcherC.GetTermFullName();
			bagFullO2=nameTermMatcherT.GetTermFullName();
		}
		else{
			counterO1 = contentTermMatcherC.GetTermInstanceCounter();
			counterO2= contentTermMatcherT.GetTermInstanceCounter();
			sumO1=countNumInstances(counterO1);
			sumO2=countNumInstances(counterO2);
			learnerO1O2 = new BayesLearner(contentTermMatcherC.GetTermInstanceBag(), contentTermMatcherC.GetTermInstanceCounter(),sumO1,learnerFlag);
			learnerO2O1 = new BayesLearner(contentTermMatcherT.GetTermInstanceBag(),contentTermMatcherT.GetTermInstanceCounter(),sumO2,learnerFlag);
			bagWordsO1 =contentTermMatcherC.GetTermInstanceBag();
			bagWordsO2 =contentTermMatcherT.GetTermInstanceBag();
			bagFullO1=contentTermMatcherC.GetTermFullInstance();
			bagFullO2=contentTermMatcherT.GetTermFullInstance();

		}
		Integer sizeO1O2 = GetO1O2BagWordsSize(bagWordsO1, bagWordsO2);
		HashMap<String, Double> ProbAB= new HashMap<String, Double>();
		HashMap<String, Double> ProbAnotB=new HashMap<String, Double>();
		HashMap<String, Double> ProbNotAB=new HashMap<String, Double>();
		HashMap<String, Double> ProbNotAnotB=new HashMap<String, Double>();
		for (String A :bagFullO1.keySet()){
			for (String B : bagFullO2.keySet()){
				Double counterABFromO2=(double) 0;
				Double counterAnotBFromO2=(double) 0;
				Double counterNotABFromO2=(double) 0;
				Double counterNotAnotBFromO2=(double) 0;
				Double counterABFromO1=(double) 0;
				Double counterAnotBFromO1=(double) 0;
				Double counterNotABFromO1=(double) 0;
				Double counterNotAnotBFromO1=(double) 0;
				ArrayList<ArrayList<String>> instancesFullListB = bagFullO2.get(B);
				int totalDSize =0;
				for (ArrayList<String> d: instancesFullListB){
					Boolean resO2= learnerO1O2.classify(d,A, sizeO1O2);
					if (resO2){
						counterABFromO2++;
					}
					else{
						counterNotABFromO2++;
					}
				}
				ArrayList<ArrayList<String>> instancesNotB = separateBnotB(bagFullO2,B);
				for (ArrayList<String> d : instancesNotB){
					Boolean resO2= learnerO1O2.classify(d,A, sizeO1O2);
					if (resO2){
						counterAnotBFromO2++;
					}
					else{
						counterNotAnotBFromO2++;
					}
				}
				
				ArrayList<ArrayList<String>> instancesFullListA = bagFullO1.get(A);
				for (ArrayList<String> d: instancesFullListA){
					
					Boolean resO1= learnerO2O1.classify(d,B, sizeO1O2);
					if (resO1){
						counterABFromO1++;
					}
					else{
						counterAnotBFromO1++;
					}
				}
				ArrayList<ArrayList<String>> instancesNotA = separateBnotB(bagFullO1,A);
				for (ArrayList<String> d : instancesNotA){
					Boolean resO1= learnerO2O1.classify(d,B, sizeO1O2);
					if (resO1){
						counterNotABFromO1++;
					}
					else{
						counterNotAnotBFromO1++;
					}
				}
				ProbAB.put(A.concat(B), calculateProb(counterABFromO1,counterABFromO2, counterO1.get(A),counterO2.get(B)));
				ProbNotAB.put(A.concat(B), calculateProb(counterNotABFromO1,counterNotABFromO2,countNumInstances(counterO1)-counterO1.get(A) ,counterO2.get(B)));
				ProbAnotB.put(A.concat(B), calculateProb(counterAnotBFromO1,counterAnotBFromO2, counterO1.get(A),countNumInstances(counterO2)-counterO2.get(B)));
				ProbNotAnotB.put(A.concat(B), calculateProb(counterNotAnotBFromO1,counterNotAnotBFromO2, countNumInstances(counterO1)-counterO1.get(A),countNumInstances(counterO2)-counterO2.get(B)));
			}
		}
		HashMap<HashMap<Term, Term>, Double> sim = new HashMap<HashMap<Term, Term>, Double>();
		Term tA;
		Term tB;
		for (String A :bagFullO1.keySet()){
			String[] arrayA = A.split("[@]");
			String Aname = arrayA[arrayA.length-1];
			tA = o1.findTerm(Aname);
			for (String B : bagFullO2.keySet()){
				String[] arrayB = B.split("[@]");
				String Bname = arrayB[arrayB.length-1];
				tB = o2.findTerm(Bname);
				HashMap <Term, Term> pairTerm = new HashMap<Term, Term>();
				pairTerm.put(tA, tB);
				double res = calculateSimilarity(ProbAB,ProbNotAB,ProbAnotB,ProbNotAnotB, A, B);
				sim.put(pairTerm, res);
			}
		}
		return sim;
		
	}
	

/**
 * calculates the similarity between two terms, using the joint probability 
 * @param ProbAB - the joint  probability of A and B 
 * @param ProbNotAB - the joint  probability of not A and B 
 * @param ProbAnotB - the joint  probability of  A and not B 
 * @param ProbNotAnotB - the joint  probability of not A and not B 
 * @param A - term A
 * @param B - term B
 * @return joint probability between A and B
 */
	private double calculateSimilarity(HashMap<String, Double> ProbAB,HashMap<String, Double> ProbNotAB, HashMap<String, Double> ProbAnotB, HashMap<String, Double> ProbNotAnotB, String A, String B ){
		String AB=A.concat(B);
		return ProbAB.get(AB)/(ProbAB.get(AB) +ProbAnotB.get(AB)+ProbNotAB.get(AB)); 
	}

	/**
	 * calculates the similarity between two terms, using the joint probability 
	 * @param termInstanceCounter - hash map of all the terms and the number of their instances
	 * @return the total number of instances 
	 */	
	private Double countNumInstances(HashMap<String,Double> termInstanceCounter){
		Double counter=(double) 0;
		for (String c:termInstanceCounter.keySet()){
			counter=counter+termInstanceCounter.get(c);
		}
		return counter;
	}
	
	
	/**
	 * calculates the joint probability as described in the article
	 */	
	private double calculateProb(Double a, Double b, Double c, Double d){
		return (a+b)/(c+d);
		
	}
	
	/**
	 * separates the instances of the ontology to those which belong to the term B and those which don't
	 * @param bagFull - bag of full instances per term
	 * @param B - a specific term B
	 * @return list of instances that don't belong to B
	 */	
	private ArrayList<ArrayList<String>> separateBnotB(HashMap<String,ArrayList<ArrayList<String>>> bagFull,String B ){
		ArrayList<ArrayList<String>> allItemsNotB = new ArrayList<ArrayList<String>>();
		for (String term: bagFull.keySet()){
			if (term.equals(B)){
				continue;
			}
			else {
				for (ArrayList<String> instance: bagFull.get(term)){
					allItemsNotB.add(instance);
				}
			}
		}
		return allItemsNotB;
	}
	
	/**
	 * counts the total number of words in both ontologies
	 * @param bagO1 - bag of words of instances from ontology O1
	 * @param bagO2 - bag of words of instances from ontology O2
	 * @return the total number of words
	 */	
	private Integer GetO1O2BagWordsSize(HashMap<String,ArrayList<String>> bagO1, HashMap<String,ArrayList<String>> bagO2){
		Set<String> bagO1O2 = new HashSet<String>();
		for (String key: bagO1.keySet()){
			ArrayList<String> value= bagO1.get(key);
			for (String word : value){
				bagO1O2.add(word);
			}		
		}
		for (String key: bagO2.keySet()){
			ArrayList<String> value= bagO2.get(key);
			for (String word : value){
				bagO1O2.add(word);
			}
		}
		return bagO1O2.size();
	}

	/**
	 * the class in charge of the name algorithm 
	 */		
class NameTermMatcher{
	private Vector<Term> terms;
	private String delimiter;
	private File instance;
	private HashMap<String,ArrayList<String>> termNameBag=new HashMap<String,ArrayList<String>>();
	private HashMap<String,Double> termNameCounter = new HashMap<String,Double>();
	private HashMap<String,ArrayList<ArrayList<String>>> termFullName = new HashMap<String,ArrayList<ArrayList<String>>>();
	public NameTermMatcher(File file, Vector<Term> terms, String delimiter){
		this.terms=terms;
		this.delimiter=delimiter;
		this.instance = file;
	}
	public HashMap<String,ArrayList<String>> GetTermNameBag(){
		return termNameBag;
	}
	
	public  HashMap<String,Double> GetTermNameCounter(){
		return termNameCounter;
	}
	
	public HashMap<String,ArrayList<ArrayList<String>>> GetTermFullName(){
		return termFullName;
	}

	/**
	 * returns two upper levels from the leaves
	 */	
	private HashSet<String> getLeafsGrandParent(){
		HashSet<String> leafs = new HashSet<String>();
		for (Term term:terms){
			if (term.getAllChildren().isEmpty()){
				Term parentTerm = term.getParent();
				if (parentTerm!=null){
					String parentTermName = term.getParent().getName();
					Term grandParent = parentTerm.getParent();
					if (grandParent!= null){
						String grandParentTermName = grandParent.getName();
						leafs.add(grandParentTermName+delimiter+parentTermName+delimiter+term.getName());
					}
					else {
					leafs.add(parentTermName+delimiter+term.getName());
					}
				}
				else{
					leafs.add(term.getName());
				}
			}
		}
		return leafs;
	}
	
	/**
	 * creates the "name" of each term as described in the article:
	 * the concatenation of the terms from the root to the leaf 
	 */	
	public void createNameTermMatcher() throws ParserConfigurationException, SAXException, IOException{
		HashSet<String> leafs = getLeafsGrandParent();
		for (String leaf: leafs){
			String[] bagLeafFull=leaf.split("[@, ,_,-]");
			ArrayList<String> bagLeafFullArrayList = new ArrayList<String>(Arrays.asList(bagLeafFull));
			ArrayList<ArrayList<String>> bagLeafFullArrayArrayList= new ArrayList<ArrayList<String>>();
			bagLeafFullArrayArrayList.add(bagLeafFullArrayList);
			termFullName.put(leaf, bagLeafFullArrayArrayList);
			String[] bagLeaf= leaf.split("[@, ,_,-]");
			ArrayList<String> bagLeafArrayList = new ArrayList<String>(Arrays.asList(bagLeaf));
			termNameBag.put(leaf, bagLeafArrayList);
			termNameCounter.put(leaf, (double) 3);
		} 
	}
}
/**
 * the class in charge of the content algorithm 
 */	
class ContentTermMatcher{
	private HashSet<String> visitedTermSet;
	private File instance;
	private HashMap<String,ArrayList<String>> termInstanceBag;
	private Vector<Term> terms;
	private String delimiter;
	private HashMap<String,Double> termInstanceCounter;
	private HashMap<String,ArrayList<ArrayList<String>>> termFullInstance;
	public ContentTermMatcher(File file,Vector<Term> terms, String delimiter){
		this.termInstanceBag = new HashMap<String, ArrayList<String>>();
		this.instance = file; 
		this.terms = terms;
		this.delimiter = delimiter;
		this.visitedTermSet = new HashSet<String>();
	    this.termInstanceCounter = new HashMap<String,Double>();
	    this.termFullInstance= new HashMap<String,ArrayList<ArrayList<String>>>();
	}
	
	public HashMap<String, ArrayList<String>> GetTermInstanceBag(){
		return termInstanceBag;
	}
	
	public  HashMap<String,Double> GetTermInstanceCounter(){
		return termInstanceCounter;
	}
	public HashMap<String,ArrayList<ArrayList<String>>> GetTermFullInstance(){
		return termFullInstance;
	}
	
	/**
	 * returns one upper level from the leaf
	 */	
	private HashSet<String> getLeafsParent(){
		HashSet<String> leafs = new HashSet<String>();
		for (Term term:terms){
			if (term.getAllChildren().isEmpty()){
				Term parentTerm = term.getParent();
				if (parentTerm!=null){
					String parentTermName = term.getParent().getName();
					leafs.add(parentTermName+delimiter+term.getName());
					
				}
				else{
					leafs.add(term.getName());
				}
			}
		}
		return leafs;
	}

	/**
	 * creates for each term a bag of words from all of it's instances
	 */
	public void createContentTermMatcher() throws ParserConfigurationException, SAXException, IOException{
		HashSet<String> candidateLeafs = getLeafsParent();
		TokensCreator tokenCreator = new TokensCreator();
		
		WebTextRetriever webTextRetriever = new WebTextRetriever();
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		db.setErrorHandler(new MyXMLErrorHandler());
		Document doc = db.parse(instance);
		doc.normalize();
		NodeList nodeList=doc.getElementsByTagName("*");
		for (int i=0;i<=nodeList.getLength();i++){
			if 	(nodeList.item(i)!=null){
				String termParentName =nodeList.item(i).getParentNode().getNodeName();
				String termId = null;
				if (termParentName.equals("#document")){
					termId = nodeList.item(i).getNodeName();
				}
				else{
					termId = nodeList.item(i).getParentNode().getNodeName()+delimiter+nodeList.item(i).getNodeName();
				}
				termInstanceCounter.put(termId, (double) 0);
			}
		}
		for (int i=0;i<=nodeList.getLength();i++){
			if 	(nodeList.item(i)!=null){
				String termParentName =nodeList.item(i).getParentNode().getNodeName();
				String termId = null;
				if (termParentName.equals("#document")){
					termId = nodeList.item(i).getNodeName();
				}
				else{
					termId = nodeList.item(i).getParentNode().getNodeName()+delimiter+nodeList.item(i).getNodeName();
				}
				
				if(nodeList.item(i).getFirstChild()==null || 
						nodeList.item(i).getFirstChild().getNodeType() != Node.TEXT_NODE||!candidateLeafs.contains(termId)){
					continue;
				}
				else{
					String termValue = nodeList.item(i).getFirstChild().getNodeValue();
					if (termId!=null && termValue!=null && !termValue.equals("")){
						ArrayList<String> values = (ArrayList<String>) tokenCreator.createTokens(termValue,termId);
						Double oldCount =  termInstanceCounter.get(termId);
						termInstanceCounter.put(termId, oldCount+1);
						ArrayList<ArrayList<String>> oldValue = new ArrayList<ArrayList<String>>();
						if (termFullInstance.get(termId)!=null){
							oldValue = termFullInstance.get(termId);
							oldValue.add(values);
							termFullInstance.put(termId, oldValue);
							}
						else {
							oldValue.add(values);
							termFullInstance.put(termId, oldValue);
						}
						
						if (termInstanceBag.get(termId)==null){
							termInstanceBag.put(termId, new ArrayList<String>());
						}
						for (String word : values){
							word = word.toLowerCase();
							termInstanceBag.get(termId).add(word);
						}
						if (!termInstanceBag.get(termId).isEmpty()&&!visitedTermSet.contains(termId)){
							termInstanceBag.get(termId).add(tokenCreator.getStemmer().stemWord(termId.split(delimiter)[1].toLowerCase()));
							visitedTermSet.add(termId);
						}
					}
				}
			}
		}

	}
}

/**
 * creates a copy of HashMap<String,ArrayList<String>>
 */
private HashMap<String,ArrayList<String>> copyMyMap(HashMap<String,ArrayList<String>> myMap) {
	HashMap<String,ArrayList<String>> newMap = new HashMap<>();
	for(String key : myMap.keySet()) {
		ArrayList<String> list = new ArrayList<>();
		list.addAll(myMap.get(key));
		newMap.put(key, list);
	}
	return newMap;
}

/**
 * creates a histogram for each term of the number of times each word of appears in a specific term 
 */
private HashMap<String, HashMap<String, Double>> createHistogramForEachA(HashMap<String,ArrayList<String>> o1)
{
	HashMap<String, HashMap<String, Double>> histogramForEachA = new HashMap<String, HashMap<String, Double>> ();
	for(String A: o1.keySet())
	{
		HashMap<String, Double> Ahistogram = new HashMap<String, Double>();
		for(String wordInA:o1.get(A))
		{
			Double wordInACounter = Ahistogram.get(wordInA);
			if(wordInACounter!= null)
			{
				Ahistogram.put(wordInA, wordInACounter+1);
			}
			else
			{
				Ahistogram.put(wordInA, (double)1);
			}
			
		}
		histogramForEachA.put(A,Ahistogram);
	}
	return histogramForEachA;
}

/**
 * the class which implements the bayes learner for all the algorithms
 */	
class BayesLearner {
	private HashMap<String,ArrayList<String>> o1;
	private HashMap<String,Double> termInstanceCounter;
	private Double sum;
	private Integer learnerFlag;
	private HashMap<String, HashMap<String, Double>> histogramForEachA;
	public BayesLearner(HashMap<String,ArrayList<String>> o1, HashMap<String,Double> counter, Double sum,Integer flag){
		
		this.o1=copyMyMap(o1);
		this.termInstanceCounter=copyCounter(counter);
		this.sum=Double.valueOf(sum);
		this.learnerFlag = Integer.valueOf(flag);

		this.histogramForEachA = createHistogramForEachA(this.o1);
	}
	
	private HashMap<String, Double> copyCounter(HashMap<String, Double> counter) {
		HashMap<String,Double> newMap = new HashMap<>();

		for(String key : counter.keySet()) {
			newMap.put(key, Double.valueOf(counter.get(key)));
		}
		return newMap;
	}

	/**
	 * determines if the instance d belongs to the term A
	 * and if so, adds the word to A's bag of words
	 * @param d - instance from the ontology O2
	 * @param A - a specific term from O1 
	 * @param sizeO1O2 - the size of all the words in both ontologies
	 * @return the decision
	 */	
	public Boolean classify(ArrayList<String> d, String A, Integer sizeO1O2){
		ArrayList<String> instancesA = o1.get(A);	
		HashMap<String,Double> histogramOfA = histogramForEachA.get(A);
		double Pa=termInstanceCounter.get(A)/sum; //P(A)
		double PdA=1; //P(d|A)
		for (String word: d){
			Double countWord=(double) 0;
			countWord = histogramOfA.get(word);
			if(countWord == null)
			{
				countWord=(double)0;
			}

			if(word.matches("[0-9]+") && countWord>0)
			{
				countWord = (double)1;
			}
			double PwA  = (countWord+1)/(termInstanceCounter.get(A)+sizeO1O2); //P(w|A)
			PdA= PdA*PwA;			
		}
		double min =  (1)/(termInstanceCounter.get(A)+sizeO1O2);
		double PAd =Pa*PdA; //P(A|d)=P(A)*P(d|A). ignore P(d) -constant
		Integer power;
		double res =0;
		if(learnerFlag == 0)
		{
			if(d.size()-2>0)
			{
				power = d.size()-2;
				res = Math.pow(min, power);
				res = res*((2)/(termInstanceCounter.get(A)+sizeO1O2))*((2)/(termInstanceCounter.get(A)+sizeO1O2));
			}
			else
			{
				power = d.size();
				res = Math.pow(min, power);
			}
		}
		else
		{
			power = d.size();
			res = Math.pow(min, power);
		}
		
		if (PdA>res ){
			for(String word:d)
			{
				instancesA.add(word);
				Double wordCounter = histogramOfA.get(word);
				if(wordCounter!= null)
				{
					histogramOfA.put(word, wordCounter+1);
				}
				else
				{
					histogramOfA.put(word, (double)1);
				}
			}
			Double oldSumA= termInstanceCounter.get(A);
			termInstanceCounter.put(A, oldSumA+1);
			sum = sum + 1;
			return true;
		}
		return false;
	}
}

/**
 * Auxiliary class for creating the content matcher 
 */	
class TokensCreator{
	private PorterStemmer stemmer;
	
	public TokensCreator(){
		stemmer = new PorterStemmer();
	}
	
	public PorterStemmer getStemmer() {
		return stemmer;
	}
	public List<String>  createTokens(String content,String nodeName){
		TokenCreator tokenizer = new TokenCreator();
		List<String> tokens = new ArrayList<String>();
		List<String> result = new ArrayList<String>();
		result = tokenizer.parseKeywords(nodeName, content);
		for (String word:result){
			try{
				tokens.add(stemmer.stemWord(word));
			}catch (Throwable e){
				tokens.add(word);
			}
		}
		return tokens;
	}
}
}	
}
	


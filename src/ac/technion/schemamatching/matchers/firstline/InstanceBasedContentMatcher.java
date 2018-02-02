/**
 * @author Dror Brook
 * @author Alon Yeshurun
 * This first line instance-content matcher is implementation based on Instance Matching with COMA++ article
 */

package ac.technion.schemamatching.matchers.firstline;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.soap.Node;






import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.sparql.pfunction.library.container;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;
import info.debatty.java.stringsimilarity.*;

/**
 * This enum is for specify all the possible string-similarity methods"
  										Normalized?	Metric?	Cost
	Levenshtein				distance	No			Yes		O(m*n) 
	Normalized Levenshtein	distance
							similarity	Yes			No		O(m*n) 
	Weighted Levenshtein	distance	No			No		O(m*n) 
	Damerau-Levenshtein 	distance	No			Yes		O(m*n) 
	OptimalStringAlignment 	distance	No			No		O(m*n) 
	Jaro-Winkler			similarity
							distance	Yes			No		O(m*n)
	LongestCommonSubseq		distance	No			No		O(m*n) 
	MetricLngCommonSubseq	distance	Yes			Yes		O(m*n)
	N-Gram					distance	Yes			No		O(m*n)
	Q-Gram					distance	No			No		O(m+n)
	Cosine similarity		similarity
							distance	Yes			No		O(m+n)
	Jaccard index			similarity
							distance	Yes			Yes		O(m+n)
	Sorensen-Dice coeff		similarity
							distance	Yes			No		O(m+n)
 */

enum Similarity_type {
	LEVENSHTEIN, 
	NORMALIZED_LEVENSHTEIN, 
	DAMERAU_LEVENSHTEIN, 
	OPTIMAL_STRING_ALIGNMENT,
	JARO_WINKLER, 
	LONGEST_COMMON_SUBSEQUENCE, 
	METRIC_LONGEST_COMMON_SUBSEQUENCE, 
	N_GRAM, 
	Q_GRAM, 
	COSINE_SIMILARITY, 
	JACCARD_INDEX, 
	SORENSEN_DICE_COEFFICIENT;
}

/* 
############################################################################################################# 
##################				this class is the main instance based matcher					#############
############################################################################################################# 
*/

public class InstanceBasedContentMatcher implements FirstLineMatcher {
	String delimiter = "@@@";
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getName()
	 */
	@Override
	public String getName() {
		return "Instance Based Content Matcher";
	}
	
	/* ############################################################################################################# */
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#hasBinary()
	 */
	@Override
	public boolean hasBinary() {
		return false;
	}
	
	/* ############################################################################################################# */
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getConfig()
	 */
	@Override
	public String getConfig() {
		return "no configurable parameters";
	}

	/* ############################################################################################################# */
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getType()
	 */
	@Override
	public MatcherType getType() {
		return MatcherType.INSTANCE;
	}
	
	/* ############################################################################################################# */
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getDBid()
	 */
	@Override
	public int getDBid() {
		return 28;
	}
	
	/* ############################################################################################################# */
	
	 /**
	 * Main method of the matcher. 
	 * The content-based matcher determines the similarity of two elements by executing a
	 * pair-wise comparison of instance values using a similarity function. 
	 * The result is a similarity matrix with each dimension representing the instances of one element.
	 * This matrix is aggregated to one value that defines the similarity of the instance sets and thus the elements.
	 * This aggregation is done applying the following formula where n is the number of instances of e1
	 * and m is the number of instances of e2 and sim is the used string similarity function
	 * @param candidate [Ontology / Schema to be matched]
	 * @param target [Ontology / Schema to be matched]
	 * @param binary [If the algorithm can return a binary matrix then 
	 *               	setting this parameter to true will cause it to do so.]
	 * @return 		 [a MatchInformation object containing the similarity matrix created]
	 */
	@Override
	public MatchInformation match(Ontology candidate, Ontology target, boolean binary) {

		Vector<Term> candidateTerms = candidate.getTerms(true);
		File candidateFile = candidate.getFile();
		System.out.println("working on candidate file "+candidateFile.getName());
		String candidate_dir_name = candidateFile.getParent();
		String candidate_xml_name = candidate.getName().substring(0,candidate.getName().length()+(candidate.getName().contains(".xsd")?-4:0))+".xml";
		File candidateInstanceFile = new File(candidate_dir_name, candidate_xml_name);
		SchemaIndexCreator parsed_candidate = parse_file(candidateInstanceFile,candidateTerms, delimiter);
		
		Vector<Term> targetTerms = target.getTerms(true);
		File targetFile=target.getFile();
		System.out.println("working on target file "+targetFile.getName());
		String target_dir_name=targetFile.getParent();
		String target_xml_name = target.getName().substring(0,target.getName().length()+(target.getName().contains(".xsd")?-4:0))+".xml";
		File targetInstanceFile= new File(target_dir_name, target_xml_name);
		SchemaIndexCreator parsed_target = parse_file(targetInstanceFile,targetTerms, delimiter);
		
 		System.out.println("Starting calculate similarity matrix for matching based on: " + getName());
		MatchInformation res = new MatchInformation(candidate,target); 
		
		ArrayList<Match> similarity_matches = CalculateSimilarityMatch(candidateTerms, targetTerms, parsed_candidate, parsed_target);
		if (similarity_matches==null){
			System.out.println("ERROR occured while retrieving matches arraylist. about to exit...");
			System.exit(2);
		}
		
		for (Match match : similarity_matches){
			res.updateMatch(match.getTargetTerm(), match.getCandidateTerm(), match.getEffectiveness());
			System.out.println("Term Match:" + match );
		}

		return res;
	}
	
	/* ############################################################################################################# */
	
	/**
	 * [CalculateSimilarityMatch - this method is calculating the similarity matrix]
	 * @param  candidateTerms   [container that holds all the terms from the candidate table]
	 * @param  targetTerms      [container that holds all the terms from the target table]
	 * @param  parsed_candidate [hashmap that holds all the attributes and their instances in candidate table. e.g. {Height: 1.85, 2.05, 4.1}]
	 * @param  parsed_target    [hashmap that holds all the attributes and their instances in target table. e.g. {Height: 1.85, 2.05, 4.1}]
	 * @return                  [array list of the similarity matches]
	 */
	public ArrayList<Match> CalculateSimilarityMatch(Vector<Term> candidateTerms, Vector<Term> targetTerms, 
													SchemaIndexCreator parsed_candidate, SchemaIndexCreator parsed_target)
	{
		ArrayList<Match> matches = new ArrayList<Match>();
		try {
			HashMap<String, ArrayList<String>> candidate_index = parsed_candidate.getIndex();
			HashMap<String, ArrayList<String>> target_index = parsed_target.getIndex();
			for (Term candidateTerm : candidateTerms){
				Term candidateParent = candidateTerm.getParent();
				if (candidateParent==null){
					continue;
				}
				for (Term targetTerm : targetTerms){
					boolean empty_flag = false;
					Term targetParent = targetTerm.getParent();
					if ((candidateParent!=null) && (targetParent!=null)){
						String candidateParentTermName = candidateParent.getName();
						String candidateTermName = candidateTerm.getName();
						ArrayList<String> candidate_instance = candidate_index.get(candidateParentTermName+delimiter+candidateTermName);
					
						String targetParentTermName = targetParent.getName();
						String targetTermName = targetTerm.getName();
						ArrayList<String> target_instance = target_index.get(targetParentTermName+delimiter+targetTermName);
						if  (candidate_instance == null ||  candidate_instance.isEmpty() || target_instance == null || target_instance.isEmpty())
						{
							empty_flag = true;
						}
						double sim_value = 0;
						if (empty_flag){
							Match emptyMatch = new Match(targetTerm, candidateTerm, sim_value);
							matches.add(emptyMatch);
							continue;
						}
						else{
							sim_value = CalculateAttributeSimilarity(candidate_instance, target_instance);
							Match newMatch = new Match(targetTerm,candidateTerm,sim_value);
							matches.add(newMatch);
						}
					}
				}
			}
		} catch (Exception error) {
			System.out.println("Problem occured while calculating the similarity matrix ...");
			System.out.println("Error is: " + error);
			System.out.println("Traceback: ");
			error.printStackTrace();
			System.out.println("Exiting...");
			System.exit(1);
		}
		return matches;
	}
	
	
	/* ############################################################################################################# */
	
	/**
	 * [CalculateAttributeSimilarity - this method is calculating the similarity between 2 array lists of strings
	 *  - as shown in the formula from the article]
	 * @reference [https://dbs.cs.uni-duesseldorf.de/BTW2007/EngmannMassmann.pdf] [page 4]
	 * @param  e1 [first array list to compare]
	 * @param  e2 [second array list to compare]
	 * @return    [similarity value between 2 array list of strings]
	 */
	public double CalculateAttributeSimilarity(ArrayList<String> e1, ArrayList<String> e2){
		double sim_value = 0;
		try {
			double max_sim = 0;
			double sum_sim_e1 = 0; 
			double sum_sim_e2 = 0;
			double denominator = e1.size() + e2.size();
			Similarity_type similarity_method = Similarity_type.NORMALIZED_LEVENSHTEIN;
			// calculating first stage sum of similarity for e1
			for (String inst_i: e1){
				for (String inst_j: e2){
					 sim_value = string_similarity(inst_i, inst_j, similarity_method , true);
					 max_sim = Math.max(sim_value, max_sim);
				}
				sum_sim_e1 += max_sim;
			}
			// calculating second stage sum of similarity for e2
			for (String inst_j: e2){
				for (String inst_i: e1){
					 sim_value = string_similarity(inst_j, inst_i, similarity_method, true);
					 max_sim = Math.max(sim_value, max_sim);
				}
				sum_sim_e2 += max_sim;
			}
			// calculating final similarity function
			sim_value = (sum_sim_e1 + sum_sim_e2) / denominator;
			
		} catch (Exception error) {
			System.out.println("Problem occured while calculating similarity between two attributes...");
			System.out.println("Error is: " + error);
			System.out.println("Traceback: ");
			error.printStackTrace();
			System.out.println("Exiting...");
			System.exit(1);
		}
		return sim_value;
	}
	
	/* ############################################################################################################# */

	/**
	 * [parse_file - the method is parsing an xml file into a SchemaIndexCreator object]
	 * @param  file      [xml file to parse]
	 * @param  terms     [attribute of a table]
	 * @param  delimiter [string to split the Term child from his Term parent (from the xml file) ]
	 * @return           [schema index creatoe objects]
	 */
	public SchemaIndexCreator parse_file(File file, Vector<Term> terms, String delimiter){
		SchemaIndexCreator sic_obj = new SchemaIndexCreator(file, terms, delimiter);
		try {
			sic_obj.createIndex();
		} catch (ParserConfigurationException | SAXException | IOException error) {
			System.out.println("Problem occured while parsing xml file...");
			System.out.println("Error is: " + error);
			System.out.println("Traceback: ");
			error.printStackTrace();
			System.out.println("Exiting...");
			System.exit(1);
		}
		return sic_obj;
	}
	
	/* ############################################################################################################# */
	
	
	/**
	 * [string_similarity - This method return for 2 string the similarity value according to similarity_type.]
	 * @param  str1      [The first string to compare.]
	 * @param  str2      [The second string to compare.]
	 * @param  sim_type  [the similarity type. e.g (levenshtein/jaro_winkler/damerau_levenshtein etc...)]
	 * @param  normalize [True if we want to return a normalize similarity value, false otherwise.]
	 * @Note   [for normalize algorithms (cosine similarity, Jaccard index and more) it will always return normalize result.]
	 * @return           [the similarity value. if it's normalize it will be a value in between [0,1] ]
	 */
	private double string_similarity(String str1, String str2, Similarity_type sim_type, boolean normalize ){
		double res = 0;
		switch (sim_type) {
		case LEVENSHTEIN:
			Levenshtein lev = new Levenshtein();
			res = lev.distance(str1, str2);
			if (normalize == true)
				res = 1 - (res / Math.max(str1.length(), str2.length()));
			break;
		case DAMERAU_LEVENSHTEIN:
			 Damerau dam_lev = new Damerau();
			res = dam_lev.distance(str1, str2);
			if (normalize == true)
				res = 1 - (res / Math.max(str1.length(), str2.length()));
			break;
		case OPTIMAL_STRING_ALIGNMENT:
			OptimalStringAlignment osa = new OptimalStringAlignment();
			res = osa.distance(str1, str2);
			if (normalize == true)
				res = 1 - ( res / Math.max(str1.length(), str2.length()));
			break;
		case JARO_WINKLER: //always return normalize result
			JaroWinkler jw = new JaroWinkler();
			res = jw.similarity(str1, str2);
			break;
		case LONGEST_COMMON_SUBSEQUENCE:
			LongestCommonSubsequence lcs = new LongestCommonSubsequence();
			res = lcs.distance(str1, str2);
			if (normalize == true)
				res = 1 - (res / Math.max(str1.length(), str2.length()));
			break;
		case METRIC_LONGEST_COMMON_SUBSEQUENCE:
			MetricLCS metric_lcs = new info.debatty.java.stringsimilarity.MetricLCS();
			res = metric_lcs.distance(str1, str2);
			if (normalize == true)
				res = 1 - (res / Math.max(str1.length(), str2.length()));
			break;
		case N_GRAM:
			NGram n_gram = new NGram(2);
			res = n_gram.distance(str1, str2);
			if (normalize == true)
				res = 1 - (res / Math.max(str1.length(), str2.length()));
			break;
		case Q_GRAM:
			QGram dig = new QGram(2);
			res = dig.distance(str1, str2);
			if (normalize == true)
				res = 1 - (res / Math.max(str1.length(), str2.length()));
			break;
		case COSINE_SIMILARITY: //always return normalize result
			Cosine cos = new Cosine();
			res = cos.similarity(str1, str2);
			break;
		case JACCARD_INDEX: //always return normalize result
			Jaccard jac = new Jaccard();
			res = jac.similarity(str1, str2);
			break;
		case SORENSEN_DICE_COEFFICIENT: //always return normalize result
			SorensenDice sor_dice = new SorensenDice();
			res = sor_dice.similarity(str1, str2);
			break;
		default: // The default is normalized_levenshtein
			NormalizedLevenshtein normal_lev = new NormalizedLevenshtein();
			res = normal_lev.similarity(str1, str2);
			break;
		}
		return res;
	}
}

/* 
############################################################################################################# 
#############		this class creates a lexicon for each term in the Schema onthology 			#############
############################################################################################################# 
*/

/**
 * @description	object that represent a dictionary of a parsed file
 * @varaible	index => hashmap (like dictionary in python) that holds the all the parsed data for the schema in the following format:
 * key => schema's attributes.
 * values => all the instances of this attribute.
 */
class SchemaIndexCreator{

	/**
	 * class varaiables
	 */
	private File instance;
	private HashMap<String,ArrayList<String>> index;
	private Vector<Term> SchemaTerms;
	private String delimiter;

	/**
	 * [constructor]
	 */
	public SchemaIndexCreator(File file,Vector<Term> SchemaTerms, String delimiter){
		this.index = new HashMap<String, ArrayList<String>>();
		this.instance = file; 
		this.SchemaTerms = SchemaTerms;
		this.delimiter = delimiter;
	}
	
	/* ############################################################################################################# */
	/**
	 * brings back a set of all leafs in ontology - terms with values we can work with
	 */
	private HashSet<String> getLeafs(){
		HashSet<String> leafs = new HashSet<String>();
		for (Term term:SchemaTerms){
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
	
	/* ############################################################################################################# */
	/**
	 * Creates index for Schema ontology 
	 * Parameter "factor" is for increasing the frequency of the term name it self
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void createIndex() throws ParserConfigurationException, SAXException, IOException{
		HashSet<String> SchemaLeafs = getLeafs();
		
		//Get Document Builder
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		   
		//Build Document, parse xml
		Document doc = builder.parse(instance);
		   
		//Normalize the XML Structure
		doc.normalize();
		   
		//Here comes the root node
		System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
		
		//Get all elements
		NodeList nodeList=doc.getElementsByTagName("*");
		
		for (int i=0;i<=nodeList.getLength();i++){
			Node iNode = nodeList.item(i);
			if 	(iNode!=null){
				String termParentName =iNode.getParentNode().getNodeName();
				String termId = null;
				if (termParentName.equals("#document")){
					termId = iNode.getNodeName();
				}
				else{
					termId = iNode.getParentNode().getNodeName()+delimiter+iNode.getNodeName();
				}
				if(iNode.getFirstChild()==null || 
						iNode.getFirstChild().getNodeType() != Node.TEXT_NODE||!SchemaLeafs.contains(termId)){
					continue;
				}
				else{
					String termValue = iNode.getFirstChild().getNodeValue();
					System.out.println("Working on attribute: " + iNode.getNodeName() + "		" + "on instance: " + termValue);
					if (termId!=null && termValue!=null && !termValue.equals("")){
						if (index.get(termId)==null){
							System.out.println(index.put(termId, new ArrayList<String>()));
						}
						termValue = termValue.toLowerCase();
						index.get(termId).add(termValue);
					}
				}
			}
		}
		
		
	}
	
	/* ############################################################################################################# */
	
	/**
	 * [getIndex]
	 * @return [hashmap of the parsed file]
	 */
	public HashMap<String, ArrayList<String>> getIndex() {
		return index;
	}	
}


package ac.technion.schemamatching.matchers.firstline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;
/**
 * @author Anna Margolin
 * Instance based matcher, based on entity resolution. 
 */
public class InstanceMatcher implements FirstLineMatcher {
	double theta=0.5;
	
	/**
	 * Default constructor sets threshold to 0.5;
	 */
	public InstanceMatcher()
	{}
	/**
	 * constructor creates new matcher instance
	 * @param t threshold
	 */
	public InstanceMatcher(double t){
		theta=t;
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getName()
	 */
	public String getName() {
		return "Instance Matcher";
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#hasBinary()
	 */
	public boolean hasBinary() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#match(ac.technion.iem.ontobuilder.core.ontology.Ontology, ac.technion.iem.ontobuilder.core.ontology.Ontology, boolean)
	 */
	public MatchInformation match(Ontology candidate, Ontology target,
			boolean binary) {
		//get candidate and target terms
		Vector<Term> cTerms = candidate.getTerms(true);
		Vector<Term> tTerms = target.getTerms(true);
		
		//get candidate and target data files
		File f = candidate.getFile();
		String fileDir = f.getParent();
		File candidateInstancefile = 
			new File(fileDir,candidate.getName().substring(0,candidate.getName().length()+(candidate.getName().contains(".xsd")?-4:0))+".xml");
		
		
		File targetFile=target.getFile();
		String targetDir=targetFile.getParent();
		File targetInstanceFile= new File(targetDir,target.getName().substring(0,target.getName().length()+(candidate.getName().contains(".xsd")?-4:0))+".xml");
		
		//create and run Instance Matcher
		IM im=new IM(cTerms,candidateInstancefile,tTerms,targetInstanceFile,theta);
		ArrayList<Match> algoRes = im.run();
		
		//fill result object
		MatchInformation res = new MatchInformation(candidate,target);
		for (Match mt : algoRes)
		{
			res.updateMatch(mt.getTargetTerm(), mt.getCandidateTerm(), mt.getEffectiveness());
		}
		
		return res;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getConfig()
	 */
	public String getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getType()
	 */
	public MatcherType getType() {
		return MatcherType.INSTANCE;
	}

	/* (non-Javadoc)
	 * @see ac.technion.schemamatching.matchers.firstline.FirstLineMatcher#getDBid()
	 */
	public int getDBid() {
		return 13;
	}
	

}
/**
 * 
 * @author Anna Margolin
 * class implementing TFIDF structure
 */
class TFIDF{
	
	Map<String,Integer> tf=new HashMap<String,Integer>();//term frequency
	Map<String,Integer> df=new HashMap<String,Integer>();//document(node) frequency
	Map<String,double[]> matrix=new HashMap<String,double[]>();//map of words frequencies
	Vector<Double> nodeWeights=new Vector<Double>();//vector of nodes weights
	
	int nnodes=0;//nodes number
	/**
	 * constructor 
	 * @param recs number of records
	 * @param cols number of columns
	 */
	public TFIDF(int recs,int cols){
		nnodes=recs*cols;
	}
	/**
	 * gets the tfidf score
	 * @param ai array of strings containing the string ta
	 * @param ta searched string
	 * @param nodeNum node's number
	 * @return tfidf score
	 */
	double get(String[] ai,String ta,int nodeNum){
			int wtf=0;
			//calculate word's frequency in the node value
			for(String a:ai)
				if(a==ta)wtf++;
			//get word's frequency in all nodes
			int wdf=df.get(ta);
			//get normalization coefficient
			double len=nodeWeights.get(nodeNum);
			if(len==0)
				return 0.0;
			else
				return score(wtf,wdf)/len;
	}
	/**
	 * calculates tfidf score
	 * @param wtf word's frequency in node
	 * @param wdf number of nodes with word
	 * @return tfidf score of the word
	 */
	double score(int wtf,int wdf){
		return (Math.log((double)wtf+1.0)*Math.log((double)nnodes/wdf + 1.0));
	}
	/**
	 * adds word to the structure
	 * @param word word to add
	 * @param count number of times the word appears
	 */
	void addCountWord(String word,int count){
		//add to the term frequency
		addToMap(tf,word,count);
		//add to doc(node) frequency
		addToMap(df,word,1);
	}
	/**
	 * adds words of a node to the structure
	 * @param words array of node's words
	 */
	void addCountWords(String[] words){
		Map<String,Integer> m=new HashMap<String,Integer>();
		//add word to map to calculate frequency
		for(String word:words){
			if(word==null||word.length()==0)continue;
			addToMap(m, word,1);
		}
		Iterator<Map.Entry<String, Integer>> it=m.entrySet().iterator();
		//iterate on map to add word's frequency to the structure
		while(it.hasNext()){
			Map.Entry<String, Integer> e=it.next();
			addCountWord((String)e.getKey(),(Integer)e.getValue());
		}
	}
	/**
	 * add word with frequency to map 
	 * @param m map
	 * @param word word
	 * @param count count to add
	 */
	private void addToMap(Map<String, Integer> m, String word,int count) {
		if(m.containsKey(word)){
			int num=m.get(word);
			m.put(word, num+count);
		}else{
			m.put(word, 1);
		}
	}
	/**
	 * loads XML doc to the structure
	 * @param oDoc xml document to process
	 */
	void loadCountWords(Document oDoc){
		NodeList nd=oDoc.getElementsByTagName("*");
		for(int i=0;i<nd.getLength();i++){
			if(nd.item(i).getFirstChild()==null || 
			   nd.item(i).getFirstChild().getNodeType() != Node.TEXT_NODE)continue;
			String[] words=nd.item(i).getFirstChild().getNodeValue().split(" ");
			addCountWords(words);
		}
	}
	/**
	 * adds word to the structure
	 * @param word the word to add
	 * @param fldtf word frequency
	 * @param col column(node's) number in tfidf matrix
	 */
	void addWord(String word,int fldtf,int col){
		
		if(matrix.containsKey(word)){
			//if the word is present just add frequency
			matrix.get(word)[col]+=fldtf;
		}else{
			//if the word does not present create new array and add it to the structure
			double[] arrWeightes=new double[nnodes];
			for(int w=0;w<nnodes;w++)
				arrWeightes[w]=0;
			
			arrWeightes[col]=fldtf;
			matrix.put(word,arrWeightes);
		}
	}
	/**
	 * adds new node to the structure
	 * calculates tftidf weight of the node
	 * @param m map of words frequencies
	 * @param col column(node's) number
	 */
	void addNodeWords(Map<String,Integer> m,int col){
		Iterator<Map.Entry<String, Integer>> it=m.entrySet().iterator();
		double sum=0;
		//iterate over the words map
		while(it.hasNext()){
			Map.Entry<String, Integer> e=(Map.Entry<String, Integer>)it.next();
			//add word to the structure
			addWord((String)e.getKey(),(Integer)e.getValue(),col);
			//sum+=Math.pow((Integer)e.getValue(), 2);
			//calculate tfidf score and add it to the sum
			double s=score((Integer)e.getValue(),df.get((String)e.getKey()));
			sum+=Math.pow(s, 2);
		}
		//add normalization weight to the structure
		nodeWeights.add(Math.sqrt(sum));
	}
	/**
	 * adds node of words to the structure
	 * @param ai field's words
	 * @param col column(Node's) number
	 */
	void addNodeWords(String[] ai,int col){
		Map<String,Integer> cmp=new HashMap<String,Integer>();
		for(String v:ai){
			if(v==null || v.length()==0)continue;
			addToMap(cmp, v, 1);
		}
		addNodeWords(cmp,col);
	}
	/**
	 * adds empty node to the structure
	 * @param col column(Node) number
	 */
	void addEmptyNode(int col){
		nodeWeights.add(0.0);
	}
}
/**
 * Implements SoftTFIDF
 * @author anna
 *
 */
class IM{
	double theta=0.5;//threshold
	TFIDF cTFIDF=null;//candidate tfidf structure
	TFIDF tTFIDF=null;//target tfidf structure
	Vector<Term> cTerms;//candidate terms
	Vector<Term> tTerms;//target terms
	ArrayList<String> candidateFieldList = new ArrayList<String>();//candidate fields
	ArrayList<String> targetFieldList = new ArrayList<String>();//target fields
	String cRecName=null;//candidate record element's name
	String tRecName=null;//target record element's name
	Document cDoc=null;//candidate document
	Document tDoc =null;//target document
	
	/**
	 * gets xml node level 
	 * @param nodeName node's name
	 * @return level of the node
	 */
	public int getLevel(String nodeName){
		return nodeName.split("\\.").length;
	}
	/**
	 * gets xml node name
	 * @param termName xml node's patg
	 * @return element name
	 */
	public String getElementName(String termName){
		return termName.substring(termName.lastIndexOf(".")+1);
	}
	/**
	 * Loads new xml to Document  
	 * @param f xml file 
	 * @return Document loaded from file
	 */
	public Document getXmlDoc(File f){
		Document doc=null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			doc = db.parse(f);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	/**
	 * IM constructor
	 * @param vcTerms terms form candidate schema
	 * @param candidateInstancefile candidate instance file  
	 * @param vtTerms terms from target schema
	 * @param targetInstancefile target instance file
	 * @param th threshold
	 */
	public IM(Vector<Term> vcTerms,File candidateInstancefile,Vector<Term> vtTerms,File targetInstancefile,double th)
	{
		theta=th;//initiate the threshold
		cTerms=vcTerms;//initiate candidate terms
		tTerms=vtTerms;//initiate target terms
		
		//initiate candidate fields list and record's name
		for (Term ct : cTerms)
		{
			String cProvenance = ct.getProvenance();
			int level=getLevel(cProvenance);
			if(level==2)
				cRecName=getElementName(cProvenance);
			else if(level==3){
				System.out.println(cProvenance);
				candidateFieldList.add(getElementName(cProvenance));
			}
		}
		//initiate target fields list and record's name
		System.out.println("******************");
		for (Term tt : tTerms)
		{
			String tProvenance = tt.getProvenance();
			int level=getLevel(tProvenance);
			if(level==2)
				tRecName=getElementName(tProvenance);
			else if(level==3){
				System.out.println(tProvenance);
				targetFieldList.add(getElementName(tProvenance));
			}
		}
		//initiate candidate and target xml documents
		cDoc=getXmlDoc(candidateInstancefile);
		tDoc =getXmlDoc(targetInstancefile);
	}
	/**
	 * Adds new node
	 * @param r parent element 
	 * @param fName element name to add
	 * @param tfidf TFIDF structure
	 * @param col column(Node's number)
	 * @return array of node's string or null if node is empty
	 */
	public String[] addNode(Element r,String fName,TFIDF tfidf,int col){
		//look for the node named fName
		NodeList cfs=r.getElementsByTagName(fName);
		//if the node is not present pr empty add empty node to keep table structure
		if(cfs.getLength()==0 || cfs.item(0).getFirstChild()==null||cfs.item(0).getFirstChild().getNodeValue()==null){
			tfidf.addEmptyNode(col);
			return null;
		}else{
			//if the node is present split it's value to words and add to tfidf structure
			String[] ai=cfs.item(0).getFirstChild().getNodeValue().split(" ");
			tfidf.addNodeWords(ai,col);
			return ai;
		}
	}
	/**
	 * runs the SoftTFIDF algorithm
	 * @return matches result
	 */
	public ArrayList<Match> run(){
		int m=candidateFieldList.size();
		int n=targetFieldList.size();
		
		//Prepare data structure 
		NodeList cRecs=cDoc.getElementsByTagName(cRecName);
		cTFIDF=new TFIDF(cRecs.getLength(),m);
		NodeList tRecs=tDoc.getElementsByTagName(tRecName);
		tTFIDF=new TFIDF(tRecs.getLength(),n);
		
		cTFIDF.loadCountWords(cDoc);
		tTFIDF.loadCountWords(tDoc);
		
		
		//Iterate over all combinations of instances from candidate and target schema and 
		// create a similarity matrix for each instance combination
		ArrayList<double[][]> Mk = calcSimilarityByInstances(m, n, cRecs, tRecs);
		
		//Aggregate results of instances using average by field
		ArrayList<Match> algoRes = aggregateArrayListofMatrices(m, n, Mk);
		return algoRes;
	}
	
	/**
	 * calculates similarity matrixes from xml
	 * @param m candidate fields number 
	 * @param n target fields number
	 * @param cRecs candidate records
	 * @param tRecs target records
	 * @return list of similarity matrixes
	 */
	private ArrayList<double[][]> calcSimilarityByInstances(int m, int n,
			NodeList cRecs, NodeList tRecs) {
		//Mk := List of similarity matrices for each instance
		ArrayList<double[][]> Mk=new ArrayList<double[][]>();
		
		for(int ri=0;ri<cRecs.getLength();ri++){
			Element r=(Element)cRecs.item(ri);
			for(int sj=0;sj<tRecs.getLength();sj++){
				Element s=(Element)tRecs.item(sj);
				
				double[][] sim = initWithZero(m, n); //Init similarity matrix
			
				//Iterate over all field combinations and fill sim with result of fieldSim
				for(int rii=0;rii<m;rii++){
					String[] ai=addNode(r,candidateFieldList.get(rii),cTFIDF,ri*m+rii); //Word array for candidate field created by taking words from all instances 
					if(ai==null)continue;
					for(int sjj=0;sjj<n;sjj++){
						String[] bj=addNode(s,targetFieldList.get(sjj),tTFIDF,sj*n+sjj); //Word array for target field
						if(bj!=null)
							sim[rii][sjj]=fieldSim(ai,bj,ri*m+rii,sj*n+sjj);
						
					}
				}
				Mk.add(sim);
			}
		}
		return Mk;
	}
	
	/**
	 * performs aggregation of list of similarity matrixes
	 * @param m number of rows
	 * @param n number of columns
	 * @param Mk list of similarity matrixes
	 * @return list of matches
	 */
	private ArrayList<Match> aggregateArrayListofMatrices(int m, int n,
			ArrayList<double[][]> Mk) {
		double[][] M = initWithZero(m, n);
	
		for(double[][] mk:Mk){
			for(int i=0;i<m;i++)
				for(int j=0;j<n;j++)
					M[i][j]+=mk[i][j];
		}
		for(int i=0;i<m;i++)
			for(int j=0;j<n;j++)
				M[i][j]=M[i][j]/Mk.size();
		ArrayList<Match> algoRes = new ArrayList<Match>();
		for(int i=0;i<m;i++){
			for(int j=0;j<n;j++){
				if(M[i][j]>0){
					Match mt=new Match(tTerms.get(j+2),cTerms.get(i+2),M[i][j]);
					algoRes.add(mt);
					System.out.println(candidateFieldList.get(i)+" "+targetFieldList.get(j)+" "+M[i][j]);
				}
			}
		}
		//root nodes match
		algoRes.add(new Match(tTerms.get(0),cTerms.get(0),1));
		//record nodes match
		algoRes.add(new Match(tTerms.get(1),cTerms.get(1),1));
		return algoRes;
	}
	/**
	 * creates and initiates matrix with zeros
	 * @param m rows number
	 * @param n columns number
	 * @return initiated matrix
	 */
	private double[][] initWithZero(int m, int n) {
		double[][] sim=new double[m][n];
		for(int ii=0;ii<m;ii++)
			for(int jj=0;jj<n;jj++)
				sim[ii][jj]=0;
		return sim;
	}
	/**
	 * calculates 2 nodes similarity
	 * @param ai candidate node's strings
	 * @param bj target nodes strings
	 * @param cNode candidate node index
	 * @param tNode target node index
	 * @return filed similarity number
	 */
	double fieldSim(String[] ai,String[] bj,int cNode,int tNode){
		ArrayList<String> close=new ArrayList<String>();
		
		//calculate close list by term similarity
		for(String ta:ai){
			if(ta==null|| ta.length()==0)continue;
			for(String tb:bj){
				if(tb==null||tb.length()==0)continue;
				double tsim=termsim(ta,tb);
				if(tsim>theta && !close.contains(ta)){
					close.add(ta);
				}
			}
		}
	
		double fsim=0;
		//calculate field similarity of words from close
		for(String ta:close){
			for(String tb:bj){
				if(tb==null||tb.length()==0)continue;
				fsim+=termsim(ta,tb)*cTFIDF.get(ai,ta,cNode)*tTFIDF.get(bj,tb,tNode);
			}
		}
		return fsim;
	}
/**
 * calculates 2 strings similarity
 * @param ta candidate string
 * @param tb target string
 * @return term similarity score
 */
double termsim(String ta,String tb){
	return 1-(double)LevenshteinDistance.computeLevenshteinDistance(ta, tb)/Math.max(ta.length(), tb.length());
}
}
class LevenshteinDistance {
	/**
	 * computes minimum from 3 numbers
	 * @param a 1 number
	 * @param b 2 number
	 * @param c 3 number
	 * @return minimum of 3 numbers
	 */
    private static int minimum(int a, int b, int c) {
            return Math.min(Math.min(a, b), c);
    }
    /**
     * computes edit distance between two strings
     * @param str1 first string
     * @param str2 second string
     * @return edit distance between str1 and str2
     */
    public static int computeLevenshteinDistance(CharSequence str1,
                    CharSequence str2) {
            int[][] distance = new int[str1.length() + 1][str2.length() + 1];

            for (int i = 0; i <= str1.length(); i++)
                    distance[i][0] = i;
            for (int j = 0; j <= str2.length(); j++)
                    distance[0][j] = j;

            for (int i = 1; i <= str1.length(); i++)
                    for (int j = 1; j <= str2.length(); j++)
                            distance[i][j] = minimum(
                                            distance[i - 1][j] + 1,
                                            distance[i][j - 1] + 1,
                                            distance[i - 1][j - 1]
                                                            + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
                                                                            : 1));

            return distance[str1.length()][str2.length()];
    }
}

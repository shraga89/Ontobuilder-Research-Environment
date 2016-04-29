package ac.technion.schemamatching.matchers.firstline;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.schemamatching.matchers.MatcherType;
import edu.cmu.lti.ws4j.util.PorterStemmer;

public class InstanceBasedBayesianMatcher implements FirstLineMatcher {

	@Override
	public String getName() {
		return "Instance Based Bayesian Matcher";
	}

	@Override
	public boolean hasBinary() {
		return true;
	}
	
	@Override
	public MatchInformation match(Ontology candidate, Ontology target,boolean binary) {
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
		InstanceBasedBayesMatcher matcher = new InstanceBasedBayesMatcher(candidateTerms,targetTerms,candidateInstanceFile,targetInstanceFile);
		ArrayList<Match> matches = matcher.calculateProbabilities();
		if (matches==null){
			System.out.println("ERROR OCCURED WHILE RETRIEVING MATCHES ARRAYLIST ABOUT TO EXIT...");
			System.exit(2);
		}
		HashMap<Term,Term> normalizedTerm = normalizeMatches(matches);
		for (Match match : matches){
			double effectiveness = (normalizedTerm.get(match.getCandidateTerm()).equals(match.getTargetTerm())) ? 1.0 : 0.0;
			match.setEffectiveness(effectiveness);
			result.updateMatch(match.getTargetTerm(), match.getCandidateTerm(), match.getEffectiveness());
			if (match.getEffectiveness()==1.0){
				System.out.println("Term Match:" + match );
			}
		}
		return result;
	}
	/**
	 * This function sets the effectiveness scores of matches into binary score.
	 * If maximal score - set to 1.0 else - set to 0.0 
	 * @param matches
	 * @return
	 */
	private HashMap<Term,Term> normalizeMatches(ArrayList<Match> matches) {
		HashMap<Term,Term> normalizedTerm = new HashMap<>();
		HashMap<Term,Double> normalizedMaxValue = new HashMap<>();
		for(Match match :matches) {
			if(normalizedTerm.get(match.getCandidateTerm())==null) {
				normalizedTerm.put(match.getCandidateTerm(), match.getTargetTerm());
				normalizedMaxValue.put(match.getCandidateTerm(),match.getEffectiveness());
			}
			if(match.getEffectiveness() > normalizedMaxValue.get(match.getCandidateTerm())){
				normalizedMaxValue.put(match.getCandidateTerm(),match.getEffectiveness());
				normalizedTerm.put(match.getCandidateTerm(), match.getTargetTerm());
			}
		}
		return normalizedTerm;
		
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
		return 20;
	}

}


/** 
 * this class is in charge of making the matching via naive bayes decision rule
 * it learns the target onthology instance by creating a "document" for each term and saving and creating an index  
 * after that it calculates P(I1|ei2) for each term in candidate onthology
 * */
class InstanceBasedBayesMatcher{
	public File targetFile;
	public File candidateFile;
	public Vector<Term> candidateTerms;
	public Vector<Term> targetTerms;
	private final String delimiter = "@@@";
	private final int factor =10;
	private final double gamma = 0.1;
	public InstanceBasedBayesMatcher(Vector<Term> cTerms,Vector<Term> tTerms,File candidateFile,File targetFile){
		this.targetFile = targetFile;
		this.candidateFile = candidateFile;
		this.candidateTerms = cTerms;
		this.targetTerms= tTerms;
	}
	
	/**
	 * create the statistics for target schema
	 * @return
	 */
	private LearningIndexMaker learnTarget(){
		LearningIndexMaker indexMaker = new LearningIndexMaker(targetFile,factor,targetTerms,delimiter);
		try {
			indexMaker.createIndex();
		} catch (ParserConfigurationException | SAXException | IOException e) { 
			System.out.println("PRPBLEM OCCURED WHILE PARSING TARGET XML FILE....EXITING...");
			System.exit(1);
		}
		return indexMaker;
	}
	
	/**
	 * pretty self explanatory :)
	 * @return
	 */
	private CandidateIndexCreator createCandidateBagOfWords(){
		CandidateIndexCreator candidateIndexCreator = new CandidateIndexCreator(candidateFile,candidateTerms,delimiter);
		try {
			candidateIndexCreator.createIndex();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.out.println("PRPBLEM OCCURED WHILE PARSING CANDIDATE XML FILE....EXITING...");
			System.exit(1);
		}
		return candidateIndexCreator;
	}
	
	
	/**
	 * Calcuates P(Cj|candidate term) - uses LOG function to prevent 
	 * double under flow.
	 * Parameter gamma - is meant to be a factor that prevents overflow (-infinity) : 0 < gamma <= 1
	 * @return
	 */
	public ArrayList<Match> calculateProbabilities(){
		LearningIndexMaker learningIndex = learnTarget();
		CandidateIndexCreator candidateIndex =createCandidateBagOfWords();
		ArrayList<Match> matches = new ArrayList<Match>();
		for (Term candidateTerm : candidateTerms){
			boolean emptyFlag =false;
			String candidateTermName = candidateTerm.getName();
			Term  candidateParent = candidateTerm.getParent();
			ArrayList<String> bagOfWords =new ArrayList<String>();
			if (candidateParent!=null){
				String candidateParentTermName = candidateParent.getName();
				bagOfWords = candidateIndex.getIndex().get(candidateParentTermName+delimiter+candidateTermName);
				if (bagOfWords==null||bagOfWords.isEmpty()){
					emptyFlag =true;
				}
			}
			else{
				continue;
			}
			for (Term targetTerm : targetTerms){
				double result = 0.0;
				String targetTermName = targetTerm.getName();
				Term termParent =targetTerm.getParent();
				if (termParent==null){
					continue;
				}
				if (emptyFlag){
					Match emptyMatch = new Match(targetTerm,candidateTerm,result);
					matches.add(emptyMatch);
					continue;
				}
				String targetParentTermName = termParent.getName();
				if (learningIndex.getIndex().get(targetParentTermName+delimiter+targetTermName)!=null){
					for (String word : bagOfWords){
						result = result+gamma*Math.log(calculateLikelihood(learningIndex,word,targetParentTermName+delimiter+targetTermName));
					}
					result = result+ gamma*Math.log(calaculatePrior(learningIndex, targetParentTermName+delimiter+targetTermName)); 
					Match newMatch = new Match(targetTerm,candidateTerm,result);
					matches.add(newMatch);
				}
			}
		}
		return matches;
	}
	
	/**
	 * CalculateLikelihood gives us the estimate of P(w|Cj)= (tf(w in Cj))/(sigma(tf(w' in Cj)).
	 * Uses add 1 smoothing
	 */
	private double calculateLikelihood(LearningIndexMaker learningIndex,String word,String termName){
		HashMap<String,HashMap<String,Integer>> index =learningIndex.getIndex();
		double wordFrequencyInTerm = 0;
		double totalNumberOfWordsPerTerm =0;
		if((index.get(termName)!=null)){//decided that if cannot retrieve termName - all term frequencies will zero!
			if ((index.get(termName).get(word))!=null){
				wordFrequencyInTerm = index.get(termName).get(word);
			}
		}
		if (learningIndex.getTotalNumberOfWordsPerTerm().get(termName)!=null){
			 totalNumberOfWordsPerTerm = learningIndex.getTotalNumberOfWordsPerTerm().get(termName);
		}
		else{
			System.out.println("problem with frac");
			throw new LikelihoodCalculationException("Unable To Calcualte Likelihood - Throwing Program");
		}
		//zero probability fixing
		wordFrequencyInTerm = wordFrequencyInTerm+1;
		totalNumberOfWordsPerTerm = totalNumberOfWordsPerTerm + learningIndex.getVocabularySize();
		double likelihood = (wordFrequencyInTerm/totalNumberOfWordsPerTerm);
		return likelihood;
	}
	
	
	/**
	 * calaculatePrior give us an estimate for P(Cj) = (# of appearances of term Cj)/(total appearances of term in target file)
	 */
	private double calaculatePrior(LearningIndexMaker learningIndex,String termName){
		if (learningIndex.getTotalTermCount() == 0||learningIndex.getTermCount().get(termName)==null){
			System.out.println("problem with prior");
			throw new PriorCalculationException("Unable To Calcualte Prior - Throwing Program");
		}
		return ((double)learningIndex.getTermCount().get(termName)/learningIndex.getTotalTermCount());
	}
}
	
/**
 * @author ggoren:
 *class that is responsible on learning the target onthology
 */
class LearningIndexMaker{
	private HashSet<String> visitedTermSet;
	private File instance;
	private HashMap<String,Integer> totalNumberOfWordsPerTerm;
	private HashMap<String,Integer> termCount;
	private HashMap<String,HashMap<String,Integer>> index;
	private int totalTermCount ;
	private HashSet<String> lexicon = new HashSet<String>();
	private int factor;
	private Vector<Term> targetTerms;
	private String delimiter;
	public LearningIndexMaker(File file,int factor,Vector<Term> targetTerms,String delimiter){
		instance = file;
		totalNumberOfWordsPerTerm = new HashMap<String, Integer>();
		termCount = new HashMap<String, Integer>();
		index =new HashMap<String, HashMap<String,Integer>>();
		totalTermCount = 0;
		visitedTermSet = new HashSet<String>();
		this.factor = factor;
		this.targetTerms = targetTerms;
		this.delimiter=delimiter;
	}
	
	/**
	 * getLeafs() brings back a set of all leafs in ontology - terms with values we can work with
	 * @return
	 */
	private HashSet<String> getLeafs(){
		HashSet<String> leafs = new HashSet<String>();
		for (Term term:targetTerms){
			term.getTerms();
			if (term.getAllChildren().isEmpty()){
				String termParent = term.getParent().getName();
				leafs.add(termParent+delimiter+term.getName());
			}
		}
		return leafs;
	}
	

	
	/**
	 * createIndex() goes over the target file and gathering statistics
	 * on : tf(w in Cj) , # of appearnces of Cj, total number of term count, sigma(tf(w' in Cj)).
	 * 
	 * if Cj contains URLs then the function will send "GET" request on HTTP and add response to the index of Cj 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void createIndex() throws ParserConfigurationException, SAXException, IOException{
		HashSet<String> termLeafsData =  getLeafs(); 
		Normalizer normalizer = new Normalizer();
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
				if(nodeList.item(i).getFirstChild()==null || 
						nodeList.item(i).getFirstChild().getNodeType() != Node.TEXT_NODE||!termLeafsData.contains(termId)){
					continue;
				}
				else{
					String termValue = nodeList.item(i).getFirstChild().getNodeValue();
					ArrayList<URL> links = webTextRetriever.getLinks(termValue);
					if (links!=null && !links.isEmpty()){
						for (URL link:links){
							System.out.println("working on URL: "+link);
							String textValueOfHTML = webTextRetriever.getTextOfHTML(link);
							updateIndex(normalizer, termId, textValueOfHTML,true);
							termValue.replaceAll(link.toString(),"");
						}
					}
					if (termId != null && termValue != null && !termValue.equals("")){
						updateIndex(normalizer, termId, termValue,false);
					}
				}
			}
		}
	}
	
	
	/**
	 * updateIndex is the function that actually updates statistics
	 * @param normalizer
	 * @param termId
	 * @param termValue
	 * @param isWebRetrieval
	 */
	private void updateIndex(Normalizer normalizer, String termId,
			String termValue,boolean isWebRetrieval) {
		if (termValue == null||termValue.isEmpty()||termValue.equals("")){
			return;
		}
		ArrayList<String> values = new ArrayList<String>();
		values = (ArrayList<String>) normalizer.normalize(termValue,termId);
		if (!values.isEmpty()){
			if (termCount.get(termId)==null){
				termCount.put(termId,0);
			}
			termCount.put(termId, termCount.get(termId)+1);
			totalTermCount++;
			if (totalNumberOfWordsPerTerm.get(termId)==null){
				totalNumberOfWordsPerTerm.put(termId, 0);
			}
			if (index.get(termId)==null){
				index.put(termId,new HashMap<String,Integer>());
			}
			for (String word:values){
				word = word.toLowerCase();
				if (index.get(termId).get(word) == null){
					index.get(termId).put(word,0);	
				}
				index.get(termId).put(word, index.get(termId).get(word)+1);
				totalNumberOfWordsPerTerm.put(termId,totalNumberOfWordsPerTerm.get(termId)+1);
				lexicon.add(word);
			}
			if (!isWebRetrieval&&!visitedTermSet.contains(termId)){
				String normalizedNodeName = normalizer.getStemmer().stemWord(termId.split(delimiter)[1].toLowerCase());
				if (index.get(termId).get(normalizedNodeName)==null){
					index.get(termId).put(normalizedNodeName,0);
				}
				int oldValue =index.get(termId).get(normalizedNodeName);
				int newValue = (oldValue+1)*factor; 
				int delta = newValue - oldValue;
				index.get(termId).put(normalizedNodeName,newValue);
				totalNumberOfWordsPerTerm.put(termId,totalNumberOfWordsPerTerm.get(termId)+delta);
				lexicon.add(normalizedNodeName);
				visitedTermSet.add(termId);
			}
		}
	}
	public File getInstance() {
		return instance;
	}

	public HashMap<String, Integer> getTotalNumberOfWordsPerTerm() {
		return totalNumberOfWordsPerTerm;
	}

	public HashMap<String, Integer> getTermCount() {
		return termCount;
	}

	public HashMap<String, HashMap<String, Integer>> getIndex() {
		return index;
	}

	public int getTotalTermCount() {
		return totalTermCount;
	}

	public HashSet<String> getLexicon() {
		return lexicon;
	}

	public int getFactor() {
		return factor;
	}
	
	public int getVocabularySize(){
		return lexicon.size();
	}
}

/**
 * @author ggoren
 *this class is responsible on normalizing the instance sentences
 *it starts with tokenization then removes stop words and finally uses porter stammer for stemming 
 */
class Normalizer{
	private PorterStemmer stemmer;
	
	

	public Normalizer(){
		stemmer = new PorterStemmer();
	}
	
	public PorterStemmer getStemmer() {
		return stemmer;
	}
	
	
	public List<String>  normalize(String content,String nodeName){
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

/**
 * class that is responsible on token creation
 * @author ggoren
 *
 */
class TokenCreator {
	
	private  Analyzer analyzer;
	
	public TokenCreator(){
		 analyzer = new StandardAnalyzer(Version.LUCENE_20);
	}

    public  List<String> parseKeywords( String field, String keywords) {
        List<String> result = new ArrayList<String>();
        TokenStream stream  = analyzer.tokenStream(field, new StringReader(keywords));

        try {
            while(stream.incrementToken()) {
                result.add(stream.getAttribute(TermAttribute.class).term());
            }
        }
        catch(IOException e) {
        	e.printStackTrace();
        }
        return result;
    }  
}

/**
 * @author ggoren
 *this class creates a lexicon for each term in the candidate onthology
 */
class CandidateIndexCreator{
	private HashSet<String> visitedTermSet;
	private File instance;
	private HashMap<String,ArrayList<String>> index;
	private Vector<Term> candidateTerms;
	private String delimiter;
	public CandidateIndexCreator(File file,Vector<Term> candidateTerms, String delimiter){
		this.index = new HashMap<String, ArrayList<String>>();
		this.instance = file; 
		this.candidateTerms = candidateTerms;
		this.delimiter = delimiter;
		this.visitedTermSet = new HashSet<String>();
	}
	
	/**
	 * getLeafs() brings back a set of all leafs in ontology - terms with values we can work with
	 * @return
	 */
	private HashSet<String> getLeafs(){
		HashSet<String> leafs = new HashSet<String>();
		for (Term term:candidateTerms){
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
	 * Creates index for candidate ontology 
	 * Parameter "factor" is for increasing the frequency of the term name it self
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void createIndex() throws ParserConfigurationException, SAXException, IOException{
		HashSet<String> candidateLeafs = getLeafs();
		Normalizer normalizer = new Normalizer();
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
				if(nodeList.item(i).getFirstChild()==null || 
						nodeList.item(i).getFirstChild().getNodeType() != Node.TEXT_NODE||!candidateLeafs.contains(termId)){
					continue;
				}
				else{
					String termValue = nodeList.item(i).getFirstChild().getNodeValue();
					ArrayList<URL> links = webTextRetriever.getLinks(termValue);
					if (links!=null && !links.isEmpty()){
						for (URL link:links){
							System.out.println("working on candidate link "+link);
							String textValueOfHTML = webTextRetriever.getTextOfHTML(link);
							ArrayList<String> tokens = new ArrayList<String>(normalizer.normalize(textValueOfHTML, termId));
							if (index.get(termId)==null){
								index.put(termId, new ArrayList<String>());
							}
							for (String word:tokens){
								word = word.toLowerCase();
								index.get(termId).add(word);
							}
							termValue.replaceAll(link.toString(),"");
						}
					}
					if (termId!=null && termValue!=null && !termValue.equals("")){
						ArrayList<String> values = (ArrayList<String>) normalizer.normalize(termValue,termId);
						if (index.get(termId)==null){
							index.put(termId, new ArrayList<String>());
						}
						for (String word : values){
							word = word.toLowerCase();
							index.get(termId).add(word);
						}
						if (!index.get(termId).isEmpty()&&!visitedTermSet.contains(termId)){
							index.get(termId).add(normalizer.getStemmer().stemWord(termId.split(delimiter)[1].toLowerCase()));
							visitedTermSet.add(termId);
						}
					}
				}
			}
		}
		
		
	}
	
	public HashMap<String, ArrayList<String>> getIndex() {
		return index;
	}
	
}


/**
 * this class is responsible of retrieving all data that is inside URLs in the termValues - in order to 
 * enrich the index
 */
class WebTextRetriever{
	
	/**
	 * getLinks looks at a specific termValue and retrieves all URL "hidden" in this value via regex
	 * @param content
	 * @return
	 */
	public ArrayList<URL> getLinks(String content){
		ArrayList<URL> links = new ArrayList<URL>();
		String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		while(m.find()) {
			String urlStrSafe = null;
			String urlStr = m.group();
			if (urlStr.startsWith("(") && urlStr.endsWith(")"))
			{
				urlStr = urlStr.substring(1, urlStr.length() - 1);
			}
			if (urlStr.startsWith("www")){
				String temp = urlStr;
				urlStr = "http://"+urlStr;
				urlStrSafe = "https://"+temp;
			}
			URL url=null;
			URL urlSafe = null;
			try {
				url = new URL(urlStr);
				System.out.println(urlStr);
				if (urlStrSafe!=null){
					System.out.println(urlStrSafe);
					urlSafe = new URL(urlStrSafe);
				}
			} catch (MalformedURLException e) {
				System.out.println("problem with parsing in url");
				e.printStackTrace();
			}
			links.add(url);
			if (urlSafe!=null){
				links.add(urlSafe);
			}
		}
		return links;
	}
	
	/**
	 * getTextOfHTML creates an HTTP GET request for a specific URL and return its text content as string
	 * uses jsoup api for this mission
	 * @param url
	 * @return
	 */
	public String getTextOfHTML(URL url){
		try {
			org.jsoup.nodes.Document doc = Jsoup.connect(url.toString()).get();
			String text = doc.body().text();
			return text;
			
		}catch (Throwable e){
			System.out.println("problem with parsing URL "+url);
		}
		return "";
	}
}

/**
 * Our own XML error handler - prints out the severity of Error and 
 * when warning happens - no exception thrown
 * @author ggoren
 *
 */
class MyXMLErrorHandler implements ErrorHandler{

	@Override
	public void error(SAXParseException arg0) throws SAXException{
		System.out.println("Error Occured While Parsing");
	}

	@Override
	public void fatalError(SAXParseException arg0) throws SAXException{
		System.out.println("Fatal Error Occured While Parsing");
		
	}

	@Override
	public void warning(SAXParseException arg0){
		System.out.println("Warning Occured While Parsing");
		
	}
}

/**
 * when mechane of Prior is zero - something is not well with XML parsing
 * we throw the program 
 * @author ggoren
 *
 */
class PriorCalculationException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public PriorCalculationException(String message){
		super(message);
	}
}	


/**
 * when mechane of likelihood is zero - something is not well with XML parsing
 * we throw the program 
 * @author ggoren
 *
 */
class LikelihoodCalculationException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public LikelihoodCalculationException(String message){
		super(message);
	}
}	
	

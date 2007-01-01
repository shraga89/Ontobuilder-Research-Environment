package schemamatchings.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import schemamatchings.meta.match.AbstractMapping;
import schemamatchings.meta.match.MatchedAttributePair;
import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.test.MatchedPairData;
import schemamatchings.topk.wrapper.SchemaMatchingsException;
import schemamatchings.topk.wrapper.SchemaMatchingsWrapper;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.util.MappingAlgorithms;

import com.modica.ontology.OntologyUtilities;
import com.modica.ontology.Term;
import com.modica.ontology.match.Match;
import com.modica.ontology.match.MatchInformation;

/**
 * <p>Title: SchemaTranslator </p>
 * <p>Description: translates a given attribute to its matched one </p>
 * @author Haggai Roitman
 * @version 1.1
 */

public class SchemaTranslator extends AbstractMapping{
  /** used when attribute has no translation*/
  public static final String NO_TRANSLATION = "No Translation";
 
  /**hashCode holder*/
  private int hashCode;
  private int configuration;   
  public static HashMap hm = new HashMap();   
  int numberOfAvailableMatchers =  MatchingAlgorithms.ALL_ALGORITHM_NAMES.length;
  int numberOfAvailableMatchers2 = MappingAlgorithms.ALL_ALGORITHM_NAMES.length;
  /**
   * empty constructor
   */
  public SchemaTranslator(){
    super();
  }

  public SchemaTranslator(MatchInformation info){
    	this(info, false);
  }
  
  public SchemaTranslator(MatchInformation info, boolean removeIds){
  	  this(info, removeIds, false);
  }
 

  public SchemaTranslator(MatchInformation info, boolean removeIds, boolean vs){

	  ArrayList matches = info.getMatches();
	  ArrayList temp = new ArrayList();
	 
	  Iterator it = matches.iterator();
	  int i=0;
	  String candTerm;
	  String targetTerm;
	  while (it.hasNext()){
		  Match match = (Match)it.next();
		  if (match.getTargetTerm() == null || match.getCandidateTerm() == null){
		  	continue;
		  }
		  targetTerm = (vs ? match.getTargetTerm().toStringVs2() : match.getTargetTerm().toString());
		  candTerm = (vs ? match.getCandidateTerm().toStringVs2() :  match.getCandidateTerm().toString());
		  if (removeIds){
		  	targetTerm = OntologyUtilities.oneIdRemoval(targetTerm);
		  	candTerm = OntologyUtilities.oneIdRemoval(candTerm);
		  }
		  temp.add(new MatchedAttributePair(targetTerm, candTerm, match.getEffectiveness()));
	  }
	  
	  schemaPairs = new MatchedAttributePair[temp.size()];
	  
	  it = temp.iterator();
	  while (it.hasNext()){
	  	schemaPairs[i++] = (MatchedAttributePair)it.next();
	  }
	  removeIds();
  }
  
  public void importIdsFromMatchInfo(MatchInformation info, boolean vs){
	  ArrayList matches = info.getMatches();
	  ArrayList temp = new ArrayList();
	 
	  Iterator it = matches.iterator();
	  int i=0;
	  String candTerm;
	  String targetTerm;
	  long id1,id2;
	  while (it.hasNext()){
		  Match match = (Match)it.next();
		  if (match.getTargetTerm() == null || match.getCandidateTerm() == null){
		  	continue;
		  }
		  id1 = match.getTargetTerm().getId();
		  id2 = match.getCandidateTerm().getId();
		  targetTerm = (vs ? match.getTargetTerm().toStringVs2() : match.getTargetTerm().toString());
		  candTerm = (vs ? match.getCandidateTerm().toStringVs2() :  match.getCandidateTerm().toString());
		  MatchedAttributePair pair = new MatchedAttributePair(targetTerm, candTerm, match.getEffectiveness());
		  pair.id1 = id1;
		  pair.id2 = id2;
		  temp.add(pair);
	  }
	  
	  schemaPairs = new MatchedAttributePair[temp.size()];
	  
	  it = temp.iterator();
	  while (it.hasNext()){
	  	schemaPairs[i++] = (MatchedAttributePair)it.next();
	  }
  }
  
  private void removeIds(){
	  for (int i=0;i<schemaPairs.length;i++){
		  MatchedAttributePair pair = schemaPairs[i];
		  pair.setAttribute1(OntologyUtilities.oneIdRemoval(pair.getAttribute1()));
		  pair.setAttribute2(OntologyUtilities.oneIdRemoval(pair.getAttribute2())); 
	  }
  }
  
  public ArrayList getMatches(){
	  ArrayList matches = new ArrayList(schemaPairs.length);
	  for (int i=0;i<schemaPairs.length;i++){
		  matches.add(schemaPairs[i]);
	  }
	  return matches;
  }
  
  /**
   * empty constructor
   */
  public SchemaTranslator(MatchedAttributePair[] schemaPairs){
    super(schemaPairs);
    removeIds();
    hashCode = calcHashCode(schemaPairs);
  }

  /**
  * empty constructor
  */
 public SchemaTranslator(MatchedAttributePair[] schemaPairs,boolean calcHashCode){
   super(schemaPairs);
   removeIds();
   if (calcHashCode)
      hashCode = calcHashCode(schemaPairs);
  }

  /**
   * translates a given attribute name
   * @param attribute to translate
   * @return translation or "NO TRANSLATION" when attribute was not match to any of the other schema attributes
   */
  public String translateAttribute(String attribute){
    for (int i=0;i<schemaPairs.length;i++){
      String result = schemaPairs[i].getAttributeTranslation(attribute);
      if (!(result.equals(NO_TRANSLATION)))
        return result;
    }
    return NO_TRANSLATION;
  }


  /**
   * maps a given term
   * @param term
   * @param smw
   * @return
   */
  public Term translateTerm(Term term,SchemaMatchingsWrapper smw){
     MatchMatrix matchMatrix = smw.getMatchMatrix();
     String translation  = translateAttribute(term.toString());
     if (!translation.equals(NO_TRANSLATION)){
       if (matchMatrix.isCandTerm(term)){
          return matchMatrix.getTermByName(translation,matchMatrix.getTargetTerms());
       }
       else {
          return matchMatrix.getTermByName(translation,matchMatrix.getCandidateTerms());
       }
     }
     else
       return null;
  }


  /**
   * release resources
   */
  public void nullify(){
    for(int i=0;i<schemaPairs.length;i++)
      schemaPairs[i].nullify();
  }


  /**
   *
   * @param attribute for the translation
   * @return the weight of the translation
   */
  public double getTranslationWeight(String attribute){
    for (int i=0;i<schemaPairs.length;i++){
      String result = schemaPairs[i].getAttributeTranslation(attribute);
      if (!(result.equals(NO_TRANSLATION)))
        return schemaPairs[i].getMatchedPairWeight();
    }
    return 0;
  }



  public ArrayList toOntoBuilderMatchList(SchemaMatchingsWrapper smw) {
      MatchMatrix matchMatrix = smw.getMatchMatrix();
      return toOntoBuilderMatchList(matchMatrix);
  }
  
  
  public ArrayList toOntoBuilderMatchList(MatchMatrix matchMatrix) {
    ArrayList matches = new ArrayList();
    for (int i=0;i<schemaPairs.length;i++) {
        Term targetTerm = matchMatrix.getTermByName(schemaPairs[i].getAttribute2(),matchMatrix.getTargetTerms());
        Term candidateTerm = matchMatrix.getTermByName(schemaPairs[i].getAttribute1(),matchMatrix.getCandidateTerms());
        matches.add(new Match(targetTerm,candidateTerm,schemaPairs[i].getMatchedPairWeight()));
    }
    return matches;
  }
  
  
  public MatchInformation getMatchInfromation(MatchMatrix matrix){
  	MatchInformation matchInfo = new MatchInformation();
  	for (int i=0;i<schemaPairs.length;i++) {
  		Term targetTerm = matrix.getTermByName(schemaPairs[i].getAttribute2(),matrix.getTargetTerms());
  		Term candidateTerm = matrix.getTermByName(schemaPairs[i].getAttribute1(),matrix.getCandidateTerms());
  		matchInfo.addMatch(new Match(targetTerm,candidateTerm,schemaPairs[i].getMatchedPairWeight()));
  	}
  	return matchInfo;	
  }
  


  /**
   * @return
   */
  public double getTotalMatchWeight(){
    double weight = 0;
    for (int i=0;i< schemaPairs.length;i++)
      weight += schemaPairs[i].getMatchedPairWeight();
    return weight;
  }


  /**
   *
   * @param pairs
   */
  public void setSchemaPairs(MatchedAttributePair[] pairs){
    schemaPairs = pairs;
    removeIds();
    hashCode = calcHashCode(pairs);
  }

  /*
   * calculates HashCode for SchemaTranslator
   */
  private int calcHashCode(MatchedAttributePair[] pairs){
    int hashCode = 0;
    for (int i=0;i<pairs.length;i++){
      if (i%2 == 0)
         hashCode += pairs[i].hashCode();
      else
         hashCode -= pairs[i].hashCode();
    }
    return hashCode;
  }


  /**
   * saves a match into xml representation
   * @param matchIndex
   * @param candSchemataName
   * @param targetSchemataName
   * @param filePath
   * @throws SchemaMatchingsException
   */
  public void saveMatchToXML(int matchIndex,String candSchemataName,String targetSchemataName,String filePath) throws SchemaMatchingsException{
    try{
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setIgnoringComments(true);
      DocumentBuilder docBuilder = dbf.newDocumentBuilder();
      Document doc = docBuilder.newDocument();
      Comment comment = doc.createComment("This is the "+matchIndex+" Best matching\n"+
                                         "Use topkmatch.xsd to parse and validate the information (XMLSchema)");
      doc.appendChild(comment);
      Element bestMatch = doc.createElement("BestMatch");
      bestMatch.setAttribute("MatchIndex",Integer.toString(matchIndex));
      bestMatch.setAttribute("MatchWeight",Double.toString((getGlobalScore() != -1 ?getGlobalScore(): getTotalMatchWeight())));
      bestMatch.setAttribute("CandidateOntology",candSchemataName);
      bestMatch.setAttribute("TargetOntology",targetSchemataName);
      doc.appendChild(bestMatch);
      Element matchedPair = null;
      for (int i=0;i<schemaPairs.length;i++){
        matchedPair = doc.createElement("MatchedTerms");
        matchedPair.setAttribute("Weight",Double.toString(schemaPairs[i].getMatchedPairWeight()));
        Element candidateTerm = doc.createElement("CandidateTerm");
        Text text = doc.createTextNode(schemaPairs[i].getAttribute1());
        candidateTerm.appendChild(text);
        if (schemaPairs[i].id2 != -1)
            candidateTerm.setAttribute("id", Long.toString(schemaPairs[i].id2));
        Element targetTerm = doc.createElement("TargetTerm");
        text = doc.createTextNode(schemaPairs[i].getAttribute2());
        targetTerm.appendChild(text);
        if (schemaPairs[i].id1 != -1)
           targetTerm.setAttribute("id", Long.toString(schemaPairs[i].id1));
        matchedPair.appendChild(candidateTerm);
        matchedPair.appendChild(targetTerm);
        bestMatch.appendChild(matchedPair);
      }
      comment = doc.createComment("For any questions regarding the Top K Framework please send to:ontobuilder@ie.technion.ac.il");
      doc.appendChild(comment);
      File xmlfile = new File(filePath+ (filePath.indexOf(".xml") == -1 ? ".xml" : ""));
      // Use a Transformer for output
      TransformerFactory tFactory = TransformerFactory.newInstance();
      Transformer transformer = tFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new FileOutputStream(xmlfile));
      transformer.transform(source, result);
    }catch(Throwable e){
      throw new SchemaMatchingsException(e.getMessage());
    }
  }






  private String getPrintableString(){
    String toReturn = "Translation contents:\n";
    for (int i=0;i<schemaPairs.length;i++){
      toReturn += schemaPairs[i].getAttribute1()+" -> "+schemaPairs[i].getAttribute2()+" weight: "+schemaPairs[i].getMatchedPairWeight()+"\n";
    }
    return toReturn;
  }

  /**
   * prints the mappings into standard output
   */
  public void printTranslations(){
    System.out.println("Translation contents:\n");
    for (int i=0;i<schemaPairs.length;i++){
      System.out.println(schemaPairs[i].getAttribute1()+" -> "+schemaPairs[i].getAttribute2());
    }
  }

  /**
   * for testing - checks diff between pairs
   * @param toTranslate
   * @return
   */
  public boolean isExist(MatchedAttributePair pair){
    for (int i=0;i<schemaPairs.length;i++)
      if (schemaPairs[i].getAttribute1().equals(pair.getAttribute1()) && schemaPairs[i].getAttribute2().equals(pair.getAttribute2()))
        return true;
    return false;
  }

  //for AbstractMapping
  public int hashCode(){
     return hashCode;
  }

  public double getGlobalScore(){
    if (globalScore != -1)
      return globalScore;
    else
      return getTotalMatchWeight();
  }

public int getConfigurationNum() {
	return configuration;
}

public String getConfiguration() {
	return hm.get(configuration).toString();
}


}
package ac.technion.schemamatching.util;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 * @deprecated unknown usage for this class
 */
public class Matcher {
  MatchingAlgorithmsNamesEnum m_sMatcherName;
  OntoBuilderWrapper m_OntoBuilderWrapper;
  XmlFileHandler xfh;
  MatchInformation m_Match;
  Ontology target;
  Ontology candidate;

  public Matcher(MatchingAlgorithmsNamesEnum sMatcherName) {
    super();
    m_OntoBuilderWrapper = new OntoBuilderWrapper();
    xfh = new XmlFileHandler();
    m_sMatcherName = sMatcherName;
    m_Match = null;
    target = null;
    candidate = null;
  }

  public Matcher() {
    super();
    m_OntoBuilderWrapper = new OntoBuilderWrapper();
    m_sMatcherName = null;
    m_Match = null;
    target = null;
    candidate = null;
  }


  public String getName(){
    return m_sMatcherName.getName();
  }

  public void setAlgorithm(MatchingAlgorithmsNamesEnum sMatcherName){
    m_sMatcherName = sMatcherName;
  }

  public void match(Ontology candidateIn, Ontology targetIn){
    try {
      candidateIn.normalize();
      targetIn.normalize();
      m_Match = m_OntoBuilderWrapper.loadMatchAlgorithm(m_sMatcherName).match(targetIn, candidateIn);
      target = targetIn;
      candidate = candidateIn;
    }
    catch (Exception e) {
    }
  }

  public void match(String sXmlCandidateFile, String sXmlTargetFile){

    try {
      Ontology oCandidate = xfh.readOntologyXMLFile(sXmlCandidateFile);
      oCandidate.normalize();
      Ontology oTarget = xfh.readOntologyXMLFile(sXmlTargetFile);
      oTarget.normalize();
      m_Match = m_OntoBuilderWrapper.loadMatchAlgorithm(m_sMatcherName).match(oTarget, oCandidate);
      target = oTarget;
      candidate = oCandidate;
    }
    catch (Exception e) {
    }
  }



  public double[][] getSimilarityMatrix(){
    try {
      if(m_Match != null)
        return m_Match.getMatrix().getMatchMatrix();
      return null;
    }
    catch (Exception e) {
      return null;
    }
  }

  public int getRowCount(){
    if(m_Match != null)
      return m_Match.getMatrix().getRowCount();
    return -1;
  }

  public int getColCount(){
    if(m_Match != null)
      return m_Match.getMatrix().getColCount();
    return -1;
  }

  public MatchMatrix getMatchMatrix(){
    if(m_Match != null)
      return m_Match.getMatrix();
    return null;
  }

  public Ontology getTargetOntology(){
    return target;
  }

  public Ontology getCandidateOntology(){
    return candidate;
  }



  public static void main(String[] args) {
    //Matcher matcher = new Matcher();
  }
}

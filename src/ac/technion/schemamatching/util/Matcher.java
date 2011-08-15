package ac.technion.schemamatching.util;

import com.modica.ontology.*;
import com.modica.ontology.match.*;
import schemamatchings.ontobuilder.*;

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
  String m_sMatcherName;
  OntoBuilderWrapper m_OntoBuilderWrapper;
  MatchInformation m_Match;
  Ontology target;
  Ontology candidate;

  public Matcher(String sMatcherName) {
    super();
    m_OntoBuilderWrapper = new OntoBuilderWrapper();
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
    return m_sMatcherName;
  }

  public void setName(String sMatcherName){
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
      Ontology oCandidate = m_OntoBuilderWrapper.readOntologyXMLFile(sXmlCandidateFile);
      oCandidate.normalize();
      Ontology oTarget = m_OntoBuilderWrapper.readOntologyXMLFile(sXmlTargetFile);
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

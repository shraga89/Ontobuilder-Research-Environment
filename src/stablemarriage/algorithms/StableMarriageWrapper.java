package stablemarriage.algorithms;

import java.util.*;


import com.modica.ontology.*;
import com.modica.ontology.match.*;
import schemamatchings.ontobuilder.*;
import schemamatchings.util.*;
import schemamatchings.meta.match.*;



public class StableMarriageWrapper {
  private OntoBuilderWrapper m_OntoBuilderWrapper;

  private Ontology m_CandidateOntology;
  private Ontology m_TargetOntology;

  private MatchInformation m_MatchInformation;
  private MatchMatrix m_MatchMatrix;
  private Hashtable m_MenSet;
  private Hashtable m_WomenSet;

  private StableMarriage m_StableMarriage;
  private static double EPSILON = 0.0000000000001;
  private String m_sAlgorithmName;


  public StableMarriageWrapper() {

    m_OntoBuilderWrapper = new OntoBuilderWrapper();
    m_CandidateOntology = null;
    m_TargetOntology = null;
    m_MatchInformation = null;
    m_MatchMatrix = null;
    m_MenSet = new Hashtable();
    m_WomenSet = new Hashtable();
    m_StableMarriage = new StableMarriage();
    m_sAlgorithmName = MatchingAlgorithms.TERM;

  }
  public void SetAlgorithmName(String sAlgorithmName){
    m_sAlgorithmName = sAlgorithmName;
  }

  public SchemaTranslator runAlgorithm(Ontology obTargetOntology,Ontology obCandidateOntology){
    m_TargetOntology = obTargetOntology;
    m_CandidateOntology = obCandidateOntology;
    if((m_TargetOntology == null) || (m_CandidateOntology == null)){
      return null;
    }
    Employ();
    if(m_alMatchingResult == null){
      return null;
    }
    SchemaTranslator obSchemaTranslator = new SchemaTranslator();
    int iArraySize = m_alMatchingResult.size();

     ArrayList m_alFilteredMatchingResult = new ArrayList();
    for(int i = 0 ; i < iArraySize; ++i){
      Man man = (Man)m_alMatchingResult.get(i);
      if((man == null) || (man.GetPartner() == null)){
        continue;
      }
      else{
        MatchedAttributePair map = new MatchedAttributePair(man.GetPartner().GetName(),man.GetName(),1.0);
        m_alFilteredMatchingResult.add(map);
      }
    }
    MatchedAttributePair[] obArrayMatchedPair = new MatchedAttributePair[m_alFilteredMatchingResult.size()];
    obArrayMatchedPair = (MatchedAttributePair[] )m_alFilteredMatchingResult.toArray(obArrayMatchedPair);

    obSchemaTranslator.setSchemaPairs(obArrayMatchedPair);
    return obSchemaTranslator;
  }

  public SchemaTranslator runAlgorithm(MatchMatrix matchMatrix){

      m_MatchMatrix = matchMatrix;
      ReadStableMarriagePlayers();
      SetPreferences();
      Run();
      if(m_alMatchingResult == null){
        return null;
      }
      SchemaTranslator obSchemaTranslator = new SchemaTranslator();
      int iArraySize = m_alMatchingResult.size();

       ArrayList m_alFilteredMatchingResult = new ArrayList();
      for(int i = 0 ; i < iArraySize; ++i){
        Man man = (Man)m_alMatchingResult.get(i);
        if((man == null) || (man.GetPartner() == null)){
          continue;
        }
        else{
          MatchedAttributePair map = new MatchedAttributePair(man.GetPartner().GetName(),man.GetName(),1.0);
          m_alFilteredMatchingResult.add(map);
        }
      }
      MatchedAttributePair[] obArrayMatchedPair = new MatchedAttributePair[m_alFilteredMatchingResult.size()];
      obArrayMatchedPair = (MatchedAttributePair[] )m_alFilteredMatchingResult.toArray(obArrayMatchedPair);

      obSchemaTranslator.setSchemaPairs(obArrayMatchedPair);
      return obSchemaTranslator;
  }

  private boolean MatchOntologies() {
    if ( (m_CandidateOntology == null) || (m_TargetOntology == null)) {
      return false;
    }
    try {

      //m_MatchInformation = m_OntoBuilderWrapper.matchOntologies(m_TargetOntology, m_CandidateOntology, m_sAlgorithmName);
      m_MatchInformation = m_OntoBuilderWrapper.loadMatchAlgorithm(m_sAlgorithmName).match(m_TargetOntology,m_CandidateOntology);
      m_MatchMatrix = m_MatchInformation.getMatrix();
    }
    catch (Exception e) {
      System.out.println(e.toString());
      return false;
    }
    return true;
  }

  private boolean ReadStableMarriagePlayers() {
    if (m_MatchMatrix == null) {
      return false;
    }
    String[] womenTerms = m_MatchMatrix.getCandidateTermNames();
    String[] menTerms = m_MatchMatrix.getTargetTermNames();
    int iMenSize = m_MatchMatrix.getTargetTerms().size();
    int iWomenSize = m_MatchMatrix.getCandidateTerms().size();

    m_StableMarriage.SetSize(iMenSize, iWomenSize);
    for (int i = 0; i < iMenSize; ++i) {
      String manName = menTerms[i];
      m_MenSet.put(new Man(iWomenSize, manName), new TreeMap());
    }
    for (int j = 0; j < iWomenSize; ++j) {
      String womanName = (String) womenTerms[j];
      m_WomenSet.put(new Woman(iMenSize, womanName), new TreeMap());
    }
    return true;
  }

  private boolean SetPreferences() {

    for (Enumeration eMan = m_MenSet.keys(); eMan.hasMoreElements(); ) {
      Man man = (Man) (eMan.nextElement());
      for (Enumeration eWoman = m_WomenSet.keys(); eWoman.hasMoreElements(); ) {
        Woman woman = (Woman) (eWoman.nextElement());
        TreeMap manTree = (TreeMap) m_MenSet.get(man);
        TreeMap womanTree = (TreeMap) m_WomenSet.get(woman);
        double d1 = m_MatchMatrix.getMatchConfidenceByAttributeNames(woman.
            GetName(), man.GetName());
        Double manWomanCon = new Double(d1);
        Double womanManCon = new Double(d1);
        while (manTree.containsKey(manWomanCon)) {
          manWomanCon = new Double(manWomanCon.doubleValue() + EPSILON);
        }
        manTree.put(manWomanCon, woman);
        while (womanTree.containsKey(womanManCon)) {
          womanManCon = new Double(womanManCon.doubleValue() + EPSILON);
        }
        womanTree.put(womanManCon, man);
      }
    }

    for (Enumeration eMan = m_MenSet.keys(); eMan.hasMoreElements(); ) {
      Man man = (Man) eMan.nextElement();
      TreeMap manTree = (TreeMap) m_MenSet.get(man);
      int size = manTree.size();
      int rank = 0;
      while ((!(manTree.isEmpty()))/* && (rank < m_iThreshold)*/) {
        Woman woman = (Woman) manTree.remove( (Double) (manTree.lastKey()));
        //size = manTree.size();
        man.AddRankedPartner(woman, rank++);
      }
      m_StableMarriage.AddMan(man);
    }

    for (Enumeration eWoman = m_WomenSet.keys(); eWoman.hasMoreElements(); ) {
      Woman woman = (Woman) eWoman.nextElement();
      TreeMap womanTree = (TreeMap) m_WomenSet.get(woman);
      int rank = 0;
      while ( (!(womanTree.isEmpty()))/* && (rank < m_iThreshold)*/) {
        Man man = (Man) womanTree.remove( (Double) womanTree.lastKey());
        //womanTree.remove( (Double) womanTree.lastKey());
        woman.AddRankedPartner(man, rank++);
      }
      m_StableMarriage.AddWoman(woman);
    }
    return true;
  }

  private void Run() {

    HashSet stableMarriageMatch = m_StableMarriage.GetStableMarriage();
    m_alMatchingResult = new ArrayList();
    for (Iterator iter = stableMarriageMatch.iterator(); iter.hasNext(); ) {
      Man man = (Man) (iter.next());
      m_alMatchingResult.add(man);
    }
  }

  public void Employ(){
    MatchOntologies();
    ReadStableMarriagePlayers();
    SetPreferences();
    Run();
  }

  ArrayList m_alMatchingResult;
  //private static int m_iThreshold = 5;
}

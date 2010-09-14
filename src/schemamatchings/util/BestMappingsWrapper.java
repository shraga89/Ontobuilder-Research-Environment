package schemamatchings.util;

import schemamatchings.meta.match.MatchedAttributePair;

import java.util.*;
import stablemarriage.algorithms.*;
import schemamatchings.ontobuilder.*;
import schemamatchings.topk.wrapper.*;
import schemamatchings.util.*;
import schemamatchings.meta.match.*;
//import JSci.maths.statistics.BetaDistribution;
import com.modica.ontology.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class BestMappingsWrapper {

  public BestMappingsWrapper() {
  }

  public static SchemaTranslator GetBestMapping(String mappingAlgoNameIn) {
    if (mappingAlgoNameIn == MappingAlgorithms.MAX_WEIGHT_BIPARTITE_GRAPH) {
      return GetBestMappingByMwbg();
    }
    if (mappingAlgoNameIn == MappingAlgorithms.STABLE_MARRIAGE) {
      return GetBestMappingBySm();
    }
    if (mappingAlgoNameIn == MappingAlgorithms.DOMINANTS) {
      return GetBestMappingByDom();
    }
    if (mappingAlgoNameIn ==
        MappingAlgorithms.MAX_WEIGHT_BIPARTITE_GRAPH_STABLE_MARRIAGE_INTERSECTION) {
      return GetBestMappingByIntersection();
    }
    if (mappingAlgoNameIn ==
        MappingAlgorithms.MAX_WEIGHT_BIPARTITE_GRAPH_STABLE_MARRIAGE_UINION) {
      return GetBestMappingByUnion();
    }/*
    if (mappingAlgoNameIn == MappingAlgorithms.BETA_DIST_MODEL_MAPPING) {
      return GetBestMappingByBetaDist();
    }
    if (mappingAlgoNameIn == MappingAlgorithms.FILTERED_MAX_WEIGHT_MAPPING) {
      return GetBestMappingByFilteredMw();
    }*/
    return null;
  }

  public static SchemaTranslator GetBestMapping(String mappingAlgoNameIn, Ontology candidate, Ontology target){

//    if(mappingAlgoNameIn == MappingAlgorithms.NAIVE_BAYES) {
//      NaiveBayesMapping nbm = new NaiveBayesMapping();
//      return nbm.getBestMapping(candidate,target);
//    }
    return null;
  }


  public static SchemaTranslator GetBestMappingTopK(int kIn){
    try {
      SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(matchMatrix);
      return smw.getKthBestMatching(kIn);
    }
    catch (Exception e) {
      return null;
    }

  }

  private static SchemaTranslator  GetBestMappingByMwbg(){
    try {
      SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(matchMatrix);
      return smw.getBestMatching();
    }
    catch (Exception e) {
      return null;
    }
  }

  private static SchemaTranslator  GetBestMappingByFilteredMw(){
    SchemaTranslator bmByUnion = GetBestMappingByUnion();
    SchemaTranslator bmByDominants = GetBestMappingByDom();
    Filter(null,bmByUnion);
    try {
      SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(matchMatrix);
      SchemaTranslator bmByFilteredMw = smw.getBestMatching();
      return bmByFilteredMw;
      //return BestMappingsWrapper.GetBestMappingByUnion(bmByDominants,bmByFilteredMw);
    }
    catch (Exception e) {
      return null;
    }
  }





  private static SchemaTranslator GetBestMappingBySm(){
    StableMarriageWrapper m_StableMarriageWrapper = new StableMarriageWrapper();
    return m_StableMarriageWrapper.runAlgorithm(matchMatrix);
  }

  private static SchemaTranslator GetBestMappingByDom(){
    return CalculateDominantPairs(matchMatrix);
  }

  private static SchemaTranslator GetBestMappingByIntersection(){
    AbstractMapping em = GetBestMappingByMwbg();
    AbstractMapping tkm = GetBestMappingBySm();
    ArrayList m = intersectMappings(em, tkm);
    MatchedAttributePair[] obArrayMatchedPair = new MatchedAttributePair[m.size()];
    obArrayMatchedPair = (MatchedAttributePair[]) m.toArray(obArrayMatchedPair);
    SchemaTranslator obSchemaTranslator = new SchemaTranslator();
    obSchemaTranslator.setSchemaPairs(obArrayMatchedPair);
    return obSchemaTranslator;

  }

  private static SchemaTranslator GetBestMappingByUnion(){
    SchemaTranslator sTranslator1 = GetBestMappingByMwbg();
    SchemaTranslator sTranslator2 = GetBestMappingBySm();
    ArrayList m1 = null;
    ArrayList m2 = null;
    ArrayList m = null;
    if(sTranslator1 != null)
      m1 = getAttributePairsAsArrayList(sTranslator1);
    if(sTranslator2 != null)
      m2 = getAttributePairsAsArrayList(sTranslator2);
    if(m1 == null)
      m = m2;
    else if(m2 == null)
      m = m1;
    else
      m = plus(m1, m2);
    MatchedAttributePair[] obArrayMatchedPair = new MatchedAttributePair[m.size()];
    obArrayMatchedPair = (MatchedAttributePair[]) m.toArray(obArrayMatchedPair);
    SchemaTranslator obSchemaTranslator = new SchemaTranslator();
    obSchemaTranslator.setSchemaPairs(obArrayMatchedPair);

    return obSchemaTranslator;

  }

  private static SchemaTranslator GetBestMappingByUnion(SchemaTranslator sTranslator1, SchemaTranslator sTranslator2){
    ArrayList m1 = null;
    ArrayList m2 = null;
    ArrayList m = null;
    if(sTranslator1 != null)
      m1 = getAttributePairsAsArrayList(sTranslator1);
    if(sTranslator2 != null)
      m2 = getAttributePairsAsArrayList(sTranslator2);
    if(m1 == null)
      m = m2;
    else if(m2 == null)
      m = m1;
    else
      m = plus(m1, m2);
    MatchedAttributePair[] obArrayMatchedPair = new MatchedAttributePair[m.size()];
    obArrayMatchedPair = (MatchedAttributePair[]) m.toArray(obArrayMatchedPair);
    SchemaTranslator obSchemaTranslator = new SchemaTranslator();
    obSchemaTranslator.setSchemaPairs(obArrayMatchedPair);

    return obSchemaTranslator;

  }


  private static SchemaTranslator GetBestMappingByBetaDist(){
    SchemaTranslator mapping = new SchemaTranslator();
    double a_beta_param_neg = 2.6452;
    double b_beta_param_neg = 16.3139;
    int negSampleSize = 104503;

    double a_beta_param_pos = 3.2205;
    double b_beta_param_pos = 2.3844;
    int posSampleSize = 1821;
    double simDegree;
    double maxValue = 0;
    double probabilityToBePos;
    double probabilityToBeNeg;
    double eps = 2.2204e-016;
    MatchedAttributePair map;
    int negSize = 104503;
    int posSize = 1821;
    ArrayList alFilteredMatchingResult = new ArrayList();
//    BetaDistribution betaDistPos = new BetaDistribution(a_beta_param_pos,b_beta_param_pos);
//    BetaDistribution betaDistNeg = new BetaDistribution(a_beta_param_neg,b_beta_param_neg);
    int row = matchMatrix.getRowCount();
    int col = matchMatrix.getColCount();
    for (int r = 0; r < row; ++r) {
      for (int c = 0; c < col; ++c) {
        maxValue = Math.max(maxValue,matchMatrix.getMatchConfidenceAt(r,c));
      }
    }

    String[] candTerms =  matchMatrix.getCandidateTermNames();
    String[] targetTerms = matchMatrix.getTargetTermNames();
    for (int r = 0; r < targetTerms.length; ++r) {
      for (int c = 0; c < candTerms.length; ++c) {
        simDegree = matchMatrix.getMatchConfidenceByAttributeNames(candTerms[c],targetTerms[r]);

        if((simDegree/maxValue) >= 0.975){
          map = new MatchedAttributePair(candTerms[c], targetTerms[r], 1.0);
          alFilteredMatchingResult.add(map);
        }else{
          if(simDegree == 0.0)
            simDegree += eps;
//          probabilityToBeNeg = betaDistNeg.probability(simDegree);
//          probabilityToBePos = betaDistPos.probability(simDegree/maxValue);
//          if(probabilityToBePos >= probabilityToBeNeg){
//            map = new MatchedAttributePair(candTerms[c], targetTerms[r], 1.0);
//            alFilteredMatchingResult.add(map);
//          }
        }
      }
    }
    MatchedAttributePair[] obArrayMatchedPair = new MatchedAttributePair[alFilteredMatchingResult.size()];
    obArrayMatchedPair = (MatchedAttributePair[]) alFilteredMatchingResult.toArray(obArrayMatchedPair);
    mapping.setSchemaPairs(obArrayMatchedPair);

    return mapping;
  }

  private static ArrayList minus(ArrayList a, ArrayList b) {
    ArrayList minus = new ArrayList();
    Iterator it = a.iterator();
    while (it.hasNext()) {
      MatchedAttributePair p = (MatchedAttributePair) it.next();
      if (!b.contains(p)) {
        minus.add(p);
      }
    }
    return minus;
  }

  private static ArrayList plus(ArrayList a, ArrayList b) {
    ArrayList plus = new ArrayList();
    Iterator it = a.iterator();
    while (it.hasNext()) {
      MatchedAttributePair p = (MatchedAttributePair) it.next();
      if (!plus.contains(p)) {
        plus.add(p);
      }
    }
    it = b.iterator();
    while (it.hasNext()) {
      MatchedAttributePair p = (MatchedAttributePair) it.next();
      if (!plus.contains(p)) {
        plus.add(p);
      }
    }
    return plus;
  }

  private static ArrayList intersectMappings(AbstractMapping stNext, AbstractMapping stPrevious) {
    ArrayList a = new ArrayList(), b = new ArrayList();

    int iStNextSize = stNext.getMatchedAttributesPairsCount();
    for (int i = 0; i < iStNextSize; i++) {
      a.add(stNext.getMatchedAttributePair(i));
    }

    int iStPreviousSize = stPrevious.getMatchedAttributesPairsCount();
    for (int j = 0; j < iStPreviousSize; j++) {
      b.add(stPrevious.getMatchedAttributePair(j));
    }
    return minus(a, minus(a, b));
  }
/*
  private double CalculateRecall(AbstractMapping em, AbstractMapping tkm) {
    double b = intersectMappings(em, tkm).size();
    double a = em.getMatchedAttributesPairsCount();
    if (a != 0) {
      return (b / a);
    }
    else {
      return 1;
    }

  }

  private double CalculatePrecision(AbstractMapping em, AbstractMapping tkm) {
    double b = intersectMappings(em, tkm).size();
    double c = tkm.getMatchedAttributesPairsCount();
    if (c != 0) {
      return (b / c);
    }
    else {
      return 1;
    }

  }
*/
  private static ArrayList getAttributePairsAsArrayList(AbstractMapping stNext) {
    ArrayList a = new ArrayList();
    int iStNextSize = stNext.getMatchedAttributesPairsCount();
    for (int i = 0; i < iStNextSize; i++) {
      a.add(stNext.getMatchedAttributePair(i));
    }
    return a;
  }

  private static SchemaTranslator CalculateDominantPairs(MatchMatrix matrix) {

    SchemaTranslator mapping = new SchemaTranslator();
    ArrayList m_alFilteredMatchingResult = new ArrayList();

    Hashtable hMaxInTarget = GetMaxInTargetTerms(matrix);
    Hashtable hMaxInCandidate = GetMaxInCandidateTerms(matrix);

    String[] candidateTerms = matrix.getCandidateTermNames();
    String[] targetTerms = matrix.getTargetTermNames();
    int iTargetSize = matrix.getTargetTerms().size();
    int iCandidateSize = matrix.getCandidateTerms().size();

    for (int i = 0; i < iTargetSize; ++i) {
      String targetName = targetTerms[i];
      for (int j = 0; j < iCandidateSize; ++j) {
        String candidateName = (String) candidateTerms[j];
        double dConfidence = matrix.getMatchConfidenceByAttributeNames(
            candidateName, targetName);
        Double d1 = (Double) (hMaxInTarget.get(targetName));
        double d1value = d1.doubleValue();
        Double d2 = (Double) (hMaxInCandidate.get(candidateName));
        double d2value = d2.doubleValue();
        if ( (dConfidence == d1value) && (dConfidence == d2value)) {
          MatchedAttributePair map = new MatchedAttributePair(candidateName,
              targetName, 1.0);
          m_alFilteredMatchingResult.add(map);
        }
      }
    }
    MatchedAttributePair[] obArrayMatchedPair = new MatchedAttributePair[
        m_alFilteredMatchingResult.size()];
    obArrayMatchedPair = (MatchedAttributePair[]) m_alFilteredMatchingResult.
        toArray(obArrayMatchedPair);

    mapping.setSchemaPairs(obArrayMatchedPair);

    return mapping;
  }

  private static Hashtable GetMaxInTargetTerms(MatchMatrix matrix) {
    Hashtable hash = new Hashtable();
    String[] candidateTerms = matrix.getCandidateTermNames();
    String[] targetTerms = matrix.getTargetTermNames();
    int iTargetSize = matrix.getTargetTerms().size();
    int iCandidateSize = matrix.getCandidateTerms().size();

    for (int i = 0; i < iTargetSize; ++i) {
      String targetName = targetTerms[i];
      double max = 0.0;
      for (int j = 0; j < iCandidateSize; ++j) {
        String candidateName = (String) candidateTerms[j];
        double dConfidence = matrix.getMatchConfidenceByAttributeNames(
            candidateName, targetName);
        if (dConfidence > max) {
          max = dConfidence;
        }
      }
      hash.put(targetName, new Double(max));
    }
    return hash;
  }

  private static Hashtable GetMaxInCandidateTerms(MatchMatrix matrix) {
    Hashtable hash = new Hashtable();
    String[] candidateTerms = matrix.getCandidateTermNames();
    String[] targetTerms = matrix.getTargetTermNames();
    int iTargetSize = matrix.getTargetTerms().size();
    int iCandidateSize = matrix.getCandidateTerms().size();

    for (int i = 0; i < iCandidateSize; ++i) {
      String candidateName = candidateTerms[i];
      double max = 0;
      for (int j = 0; j < iTargetSize; ++j) {
        String targetName = (String) targetTerms[j];
        double dConfidence = matrix.getMatchConfidenceByAttributeNames(
            candidateName, targetName);
        if (dConfidence > max) {
          max = dConfidence;
        }
      }
      hash.put(candidateName, new Double(max));
    }
    return hash;
  }

  private static void Filter(SchemaTranslator mappingsToBeOned, SchemaTranslator mappingsToBeZeroed){
    //MatchedAttributePair[] matchedPairs = mappingsToBeOned.getMatchedPairs();
    MatchedAttributePair map;
    String[] candAttrs = matchMatrix.getCandidateAttributeNames();
    String[] targetAttrs = matchMatrix.getTargetAttributeNames();
    for(int i = 0 ; i < targetAttrs.length; ++i){
      for(int j = 0 ; j < candAttrs.length; ++j){
        map = new MatchedAttributePair(candAttrs[j],targetAttrs[i],1.0);
        /*if(mappingsToBeOned.isExist(map)){
          matchMatrix.setMatchConfidenceAt(i,j,1.0);
        }*/
        //else{
        if(!mappingsToBeZeroed.isExist(map)){
          matchMatrix.setMatchConfidenceAt(i,j,0.0);
        }
        //}
      }
    }
  }

  public static MatchMatrix matchMatrix = null;

  public static void main(String[] args) {
  }

}

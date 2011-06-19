package ac.technion.schemamatching.test;

import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;

import com.modica.ontology.Ontology;
import com.modica.ontology.algorithm.CombinedAlgorithm;
import com.modica.ontology.algorithm.GraphAlgorithm;
import com.modica.ontology.algorithm.PrecedenceAlgorithm;
import com.modica.ontology.algorithm.TermAlgorithm;
import com.modica.ontology.algorithm.TermValueAlgorithm;
import com.modica.ontology.match.MatchInformation;

public class WeightsTest {

  public static void main(String[] args){
    try{

      OntoBuilderWrapper ob = new OntoBuilderWrapper();
      Ontology candidate = ob.readOntologyXMLFile("www.eservus.com.xml",true);
      Ontology target = ob.readOntologyXMLFile("www.theatremania.org.xml",true);
      TermAlgorithm term = (TermAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithms.TERM);
      //tune term weights
      term.setLabelWeights(0.5,0.5);
      term.setNameWeights(0.5,0.5);

      PrecedenceAlgorithm precedence = (PrecedenceAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithms.PRECEDENCE);
      //tune precedence weights
      precedence.setPrecedeWeight(0.5);
      precedence.setSucceedWeight(0.5);

      GraphAlgorithm graph = (GraphAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithms.COMPOSITION);
      //tune graph weights
      graph.setParentsWeight(0.5);
      graph.setSiblingsWeight(0.5);

      TermValueAlgorithm term_value = (TermValueAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithms.TERM_VALUE_COMBINED);
      double[] algorithmsWeights1 = {0.5,0.5};
      //tune Term and Value weights
      term_value.configureAlgorithmsWeights(algorithmsWeights1);

      //new match version
      MatchInformation match1 = term_value.match(candidate,target);

      CombinedAlgorithm combined = (CombinedAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithms.TERM_VALUE_PRECEDENCE_COMPOSITION_COMBINED);
      double[]  algorithmsWeights2 = {0.25,0.25,0.25,0.25};
      //tune combined weights
      combined.configureAlgorithmsWeights(algorithmsWeights2);

      //new match version
      MatchInformation match2 = combined.match(candidate,target);

    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
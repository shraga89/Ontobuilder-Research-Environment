package ac.technion.schemamatching.test;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.precedence.PrecedenceAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.CombinedAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.GraphAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermValueAlgorithm;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;

public class WeightsTest {

  public static void main(String[] args){
    try{

      OntoBuilderWrapper ob = new OntoBuilderWrapper();
      XmlFileHandler xhf = new XmlFileHandler();
      Ontology candidate = xhf.readOntologyXMLFile("www.eservus.com.xml",true);
      Ontology target = xhf.readOntologyXMLFile("www.theatremania.org.xml",true);
      TermAlgorithm term = (TermAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithmsNamesEnum.TERM);
      //tune term weights
      term.setLabelWeights(0.5,0.5);
      term.setNameWeights(0.5,0.5);

      PrecedenceAlgorithm precedence = (PrecedenceAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithmsNamesEnum.PRECEDENCE);
      //tune precedence weights
      precedence.setPrecedeWeight(0.5);
      precedence.setSucceedWeight(0.5);

      GraphAlgorithm graph = (GraphAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithmsNamesEnum.COMPOSITION);
      //tune graph weights
      graph.setParentsWeight(0.5);
      graph.setSiblingsWeight(0.5);

      TermValueAlgorithm term_value = (TermValueAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithmsNamesEnum.TERM_VALUE_COMBINED);
      double[] algorithmsWeights1 = {0.5,0.5};
      //tune Term and Value weights
      term_value.configureAlgorithmsWeights(algorithmsWeights1);

      //new match version
      @SuppressWarnings("unused")
	MatchInformation match1 = term_value.match(candidate,target);

      CombinedAlgorithm combined = (CombinedAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithmsNamesEnum.TERM_VALUE_PRECEDENCE_COMPOSITION_COMBINED);
      double[]  algorithmsWeights2 = {0.25,0.25,0.25,0.25};
      //tune combined weights
      combined.configureAlgorithmsWeights(algorithmsWeights2);

      //new match version
      @SuppressWarnings("unused")
	MatchInformation match2 = combined.match(candidate,target);

    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
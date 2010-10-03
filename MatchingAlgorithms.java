package schemamatchings.ontobuilder;


public interface MatchingAlgorithms{
  public static  String TERM = "Term Match";
  public static  String VALUE = "Value Match";
  public static  String TERM_VALUE_COMBINED = "Term and Value Match";
  public static  String TERM_VALUE_PRECEDENCE_COMPOSITION_COMBINED = "Combined Match";
  public static  String PRECEDENCE = "Precedence Match";
  public static  String COMPOSITION = "Graph Match";
  //public static  String NEW_PRECEDENCE = "New Precedence Match";
  public static String SIMILARITY_FLOODING = "Similarity Flooding Algorithm";
  public static  String[] ALL_ALGORITHM_NAMES = {TERM,VALUE,TERM_VALUE_COMBINED,TERM_VALUE_PRECEDENCE_COMPOSITION_COMBINED,PRECEDENCE,COMPOSITION,SIMILARITY_FLOODING};
}
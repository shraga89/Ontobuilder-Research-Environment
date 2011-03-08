package schemamatchings.util;

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
 */
public interface MappingAlgorithms {
  public static  String MAX_WEIGHT_BIPARTITE_GRAPH = "Max Weighted Bipartite Graph";
  public static  String STABLE_MARRIAGE = "Stable Marriage";
  public static  String DOMINANTS = "Dominants";
  public static  String MAX_WEIGHT_BIPARTITE_GRAPH_STABLE_MARRIAGE_INTERSECTION = "Intersection";
  public static  String MAX_WEIGHT_BIPARTITE_GRAPH_STABLE_MARRIAGE_UINION = "Union";
  /*public static  String BETA_DIST_MODEL_MAPPING = "Beta Distribution Model";
  public static  String FILTERED_MAX_WEIGHT_MAPPING = "Filtered Max Weighted Bipartite Graph";
  public static  String NAIVE_BAYES = "Naive Bayes"; */
  public static  String[] ALL_ALGORITHM_NAMES = {MAX_WEIGHT_BIPARTITE_GRAPH,
                                                 STABLE_MARRIAGE,
                                                 DOMINANTS,
                                                 MAX_WEIGHT_BIPARTITE_GRAPH_STABLE_MARRIAGE_INTERSECTION,
                                                 MAX_WEIGHT_BIPARTITE_GRAPH_STABLE_MARRIAGE_UINION /*,
                                                 NAIVE_BAYES,
                                                 BETA_DIST_MODEL_MAPPING,FILTERED_MAX_WEIGHT_MAPPING,NAIVE_BAYES*/};

}

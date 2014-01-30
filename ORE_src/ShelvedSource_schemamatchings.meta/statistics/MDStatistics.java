package schemamatchings.meta.statistics;

import schemamatchings.meta.algorithms.AbstractMetaAlgorithm;

public class MDStatistics extends MetaAlgorithmStatistics{

  protected MDStatistics(AbstractMetaAlgorithm algorithm,String candidateSchemaName,String targetSchemaName) {
    super (algorithm,candidateSchemaName,targetSchemaName);
  }
}
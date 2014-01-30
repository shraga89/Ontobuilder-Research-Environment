package schemamatchings.meta.testing;

import schemamatchings.meta.agr.AverageGlobalAggregator;
import schemamatchings.meta.agr.AverageLocalAggregator;
import schemamatchings.meta.algorithms.MatchAlgorithm;
import schemamatchings.meta.algorithms.MatrixDirectWithBoundingAlgorithm;
import schemamatchings.meta.algorithms.MetaAlgorithmNames;
import schemamatchings.meta.algorithms.MetaAlgorithmsFactory;
import schemamatchings.meta.algorithms.Schema;
import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.util.SchemaMatchingAlgorithmsRunner;


public class MDB_Test {

  //test meta algorithm
  public static void main(String args[]){
    try{
      int k = 20;
      Object[] params = new Object[6];
      OntoBuilderWrapper ob = new OntoBuilderWrapper();
      Schema candidate = ob.readOntologyXMLFile("www.datemeister.com.xml",true);
      Schema target = ob.readOntologyXMLFile("www.amerciansingles.com.xml",true);
      MatchAlgorithm[] matchAlgorithms = new MatchAlgorithm[2];
      matchAlgorithms[0] = ob.loadMatchAlgorithm(MatchingAlgorithms.TERM);
      matchAlgorithms[1] = ob.loadMatchAlgorithm(MatchingAlgorithms.VALUE);
//      matchAlgorithms[2] = ob.loadMatchAlgorithm(MatchingAlgorithms.PRECEDENCE);
//      matchAlgorithms[3] = ob.loadMatchAlgorithm(MatchingAlgorithms.COMPOSITION);
      params[0] = new Integer(k);
      params[1] = new AverageGlobalAggregator();//F
      params[2] = new AverageLocalAggregator(0.25);//f
      params[3] = new AverageGlobalAggregator();//H
      params[4] = new AverageLocalAggregator();//h
      params[5] = new MatchMatrix();
      MatrixDirectWithBoundingAlgorithm mdb = (MatrixDirectWithBoundingAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNames.MATRIX_DIRECT_WITH_BOUNDING_ALGORITHM,params);
      mdb.init(candidate,target,matchAlgorithms.length,matchAlgorithms,new SchemaMatchingAlgorithmsRunner());
      mdb.useStatistics();
      mdb.normalizeMatrixes();
      mdb.runAlgorithm();
//   for (int i=1;i<k+1;i++)
//     ((SchemaTranslator)mdb.getKthBestMapping(i)).saveMatchToXML(i,"Mevo_Jerusalem.xml","Neptune_Eilat.xml","MDB/MDB_MAPPING"+i);
      mdb.getStatistics().printStatistics();
      System.exit(0);
    }catch(Throwable e){
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
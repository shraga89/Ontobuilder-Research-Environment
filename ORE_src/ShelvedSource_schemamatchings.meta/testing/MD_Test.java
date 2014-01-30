package schemamatchings.meta.testing;


import schemamatchings.meta.agr.AverageGlobalAggregator;
import schemamatchings.meta.agr.AverageLocalAggregator;
import schemamatchings.meta.algorithms.MatrixDirectAlgorithm;
import schemamatchings.meta.algorithms.MetaAlgorithmNames;
import schemamatchings.meta.algorithms.MetaAlgorithmsFactory;
import schemamatchings.meta.algorithms.Schema;
import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.util.SchemaMatchingAlgorithmsRunner;
import schemamatchings.util.SchemaTranslator;

import com.modica.ontology.algorithm.AbstractAlgorithm;

public class MD_Test {


  //test meta algorithm
 public static void main(String args[]){
   try{
     int k = 100;
     Object[] params = new Object[4];
     OntoBuilderWrapper ob = new OntoBuilderWrapper();
     Schema candidate = ob.readOntologyXMLFile("D:/OntoBuilder2.0/book_shops/book_shops/www.amazon.com.xml",true);
     Schema target = ob.readOntologyXMLFile("D:/OntoBuilder2.0/book_shops/book_shops/www.eastbaybooks.com.xml",true);
     AbstractAlgorithm[] matchAlgorithms = new AbstractAlgorithm[2];
     matchAlgorithms[0] = ob.loadMatchAlgorithm(MatchingAlgorithms.TERM);
     matchAlgorithms[1] = ob.loadMatchAlgorithm(MatchingAlgorithms.VALUE);
//     matchAlgorithms[2] = ob.loadMatchAlgorithm(MatchingAlgorithms.PRECEDENCE);
//     matchAlgorithms[3] = ob.loadMatchAlgorithm(MatchingAlgorithms.COMPOSITION);
     params[0] = new Integer(k);
     params[1] = new AverageGlobalAggregator();
     params[2] = new AverageLocalAggregator();
     params[3] = new MatchMatrix();
     MatrixDirectAlgorithm mda = (MatrixDirectAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNames.MATRIX_DIRECT_ALGORITHM,params);
     mda.init(candidate,target,matchAlgorithms.length,matchAlgorithms,new SchemaMatchingAlgorithmsRunner());
   // mda.setPreprocessMatrixes(MatrixPreprocessor.TEMPLATE_PREPROCESSING,1);
     mda.useStatistics();
     mda.normalizeMatrixes();
     mda.runAlgorithm();
     for (int i=1;i<k+1;i++)
       System .out.println(i+"->"+((SchemaTranslator)mda.getKthBestMapping(i)).getGlobalScore());/*.saveMatchToXML(i,"Mevo_Jerusalem.xml","Neptune_Eilat.xml","MD/MD_MAPPING"+i)*/;
     mda.getStatistics().printStatistics();
     //mda.getStatistics().getIterationsPlot().printGraph();
     System.exit(0);
   }catch(Throwable e){
     System.out.println(e.getMessage());
     e.printStackTrace();
   }
  }
}
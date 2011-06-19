package schemamatchings.meta.testing;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Haggai Roitman
 * @version 1.1
 */
import schemamatchings.meta.agr.AverageGlobalAggregator;
import schemamatchings.meta.agr.AverageLocalAggregator;
import schemamatchings.meta.algorithms.CrossThresholdAlgorithm;
import schemamatchings.meta.algorithms.MatchAlgorithm;
import schemamatchings.meta.algorithms.MetaAlgorithmNames;
import schemamatchings.meta.algorithms.MetaAlgorithmsFactory;
import schemamatchings.meta.algorithms.Schema;
import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.util.SchemaMatchingAlgorithmsRunner;

public class NonUniformHybrid_Test {

  //test meta algorithm
public static void main(String args[]){
 try{
   int k = 20;
   Object[] params = new Object[6];
   OntoBuilderWrapper ob = new OntoBuilderWrapper();
   Schema candidate = ob.readOntologyXMLFile("onto_exact/Mevo_Jerusalem.xml",true);
   Schema target = ob.readOntologyXMLFile("onto_exact/Neptune_Eilat.xml",true);
   byte nonUniformVersion = 1;
   Thread myThread = Thread.currentThread();
   MatchAlgorithm[] matchAlgorithms = new MatchAlgorithm[2];
   matchAlgorithms[0] = ob.loadMatchAlgorithm(MatchingAlgorithms.TERM);
   matchAlgorithms[1] = ob.loadMatchAlgorithm(MatchingAlgorithms.PRECEDENCE);
   //matchAlgorithms[2] = ob.loadMatchAlgorithm(MatchingAlgorithms.VALUE);
   //matchAlgorithms[3] = ob.loadMatchAlgorithm(MatchingAlgorithms.COMPOSITION);
   params[0] = new Integer(k);
   params[1] = new AverageGlobalAggregator();//F
   params[2] = new AverageLocalAggregator(0.25);//f
   params[3] = new AverageGlobalAggregator();//H
   params[4] = new AverageLocalAggregator();//h
   params[5] = new MatchMatrix();
   CrossThresholdAlgorithm hybrid = (CrossThresholdAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNames.HYBRID_ALGORITHM,params);
   hybrid.init(candidate,target,matchAlgorithms.length,matchAlgorithms,SchemaMatchingAlgorithmsRunner.class);
   hybrid.useStatistics();
   hybrid.normalizeMatrixes();
   hybrid.setNonUniform(true,nonUniformVersion);
   hybrid.setDebugMode(true);
   hybrid.runAlgorithm();
   while (!hybrid.isAlgorithmRunFinished()){
     try{
           Thread.sleep(100);
       }catch(InterruptedException e){}
   }
//   for (int i=1;i<k+1;i++){
//     //((SchemaTranslator)hybrid.getKthBestMapping(i)).saveMatchToXML(i,"Mevo_Jerusalem.xml","Neptune_Eilat.xml","mevo-neptune"+i);
//   }
   hybrid.printDebugString();
   hybrid.getStatistics().printStatistics();
   hybrid.getStatistics().getMappingsPlot().printGraph();
   }catch(Throwable e){
   System.out.println(e.getMessage());
   e.printStackTrace();
 }
}
}

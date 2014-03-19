package schemamatchings.meta.testing;

import java.util.Vector;

import schemamatchings.meta.agr.AverageGlobalAggregator;
import schemamatchings.meta.agr.AverageLocalAggregator;
import schemamatchings.meta.algorithms.CrossThresholdAlgorithm;
import schemamatchings.meta.algorithms.MatchAlgorithm;
import schemamatchings.meta.algorithms.MetaAlgorithmNames;
import schemamatchings.meta.algorithms.MetaAlgorithmsFactory;
import schemamatchings.meta.algorithms.Schema;
import schemamatchings.meta.match.AbstractMapping;
import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.util.SchemaMatchingAlgorithmsRunner;

public class Hybrid_Test {

  //test meta algorithm
public static void main(String args[]){
 try{
   int k = 20;
   Object wait = new Object();
   Object[] params = new Object[6];
   OntoBuilderWrapper ob = new OntoBuilderWrapper();
   Schema candidate = ob.readOntologyXMLFile("D:/OntoBuilder2.0/book_shops/book_shops/www.amazon.com.xml",true);
   Schema target = ob.readOntologyXMLFile("D:/OntoBuilder2.0/book_shops/book_shops/www.eastbaybooks.com.xml",true);
   Thread myThread = Thread.currentThread();
   MatchAlgorithm[] matchAlgorithms = new MatchAlgorithm[4];
   matchAlgorithms[0] = ob.loadMatchAlgorithm(MatchingAlgorithms.TERM);
   matchAlgorithms[1] = ob.loadMatchAlgorithm(MatchingAlgorithms.VALUE);
   matchAlgorithms[2] = ob.loadMatchAlgorithm(MatchingAlgorithms.PRECEDENCE);
   matchAlgorithms[3] = ob.loadMatchAlgorithm(MatchingAlgorithms.COMPOSITION);
   params[0] = new Integer(k);
   params[1] = new AverageGlobalAggregator();//F
   params[2] = new AverageLocalAggregator();//f
   params[3] = new AverageGlobalAggregator();//H
   params[4] = new AverageLocalAggregator();//h
   params[5] = new MatchMatrix();
   CrossThresholdAlgorithm hybrid = (CrossThresholdAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNames.HYBRID_ALGORITHM,params);
   hybrid.init(candidate,target,matchAlgorithms.length,matchAlgorithms,SchemaMatchingAlgorithmsRunner.class);
   hybrid.useStatistics();
   hybrid.normalizeMatrixes();
   hybrid.runAlgorithm();
   
   synchronized(wait){
    while (!hybrid.isAlgorithmRunFinished()){
        try{
        	wait.wait(1000);
        }catch(InterruptedException e){}
     }
 }
//   for (int i=1;i<k+1;i++){
//     //((SchemaTranslator)hybrid.getKthBestMapping(i)).saveMatchToXML(i,"Mevo_Jerusalem.xml","Neptune_Eilat.xml","mevo-neptune"+i);
//   }
   Vector kmaps = hybrid.getAllKBestMappings();
   for (int i=Math.min(k,kmaps.size());i>0;i--){
   	System .out.println(((Math.min(k,kmaps.size())-i+1))+": "+((AbstractMapping)kmaps.get(i-1)).getGlobalScore());
   }
   hybrid.getStatistics().printStatistics();
   //hybrid.getStatistics().getMappingsPlot().printGraph();
   }catch(Throwable e){
   System.out.println(e.getMessage());
   e.printStackTrace();
 }
}
}
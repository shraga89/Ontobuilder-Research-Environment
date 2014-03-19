package schemamatchings.meta.testing;



import schemamatchings.meta.agr.SumGlobalAggregator;
import schemamatchings.meta.agr.SumLocalAggregator;
import schemamatchings.meta.algorithms.MatchAlgorithm;
import schemamatchings.meta.algorithms.MetaAlgorithmNames;
import schemamatchings.meta.algorithms.MetaAlgorithmsFactory;
import schemamatchings.meta.algorithms.SMThersholdAlgorithm;
import schemamatchings.meta.algorithms.Schema;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.util.SchemaMatchingAlgorithmsRunner;
import schemamatchings.util.SchemaMatchingsUtilities;


public class TA_Test {

  //test meta algorithm
public static void main(String args[]){
  SMThersholdAlgorithm ta = null;
  Object wait = new Object();
 try{
   int k = Integer.parseInt("2");
   Object[] params = new Object[3];
   OntoBuilderWrapper ob = new OntoBuilderWrapper();
   Schema candidate = ob.readOntologyXMLFile("1-time.xml_2-surfer.xml_EXACT/1-time.xml",true);
   Schema target = ob.readOntologyXMLFile("1-time.xml_2-surfer.xml_EXACT/2-surfer.xml",true);
   Thread myThread = Thread.currentThread();
   MatchAlgorithm[] matchAlgorithms = new MatchAlgorithm[2];
   matchAlgorithms[0] = ob.loadMatchAlgorithm(MatchingAlgorithms.TERM);
   matchAlgorithms[1] = ob.loadMatchAlgorithm(MatchingAlgorithms.PRECEDENCE);
   params[0] = new Integer(k);
   params[1] = new SumGlobalAggregator();//F
   params[2] = new SumLocalAggregator();//f
//   params[3] = new AverageGlobalAggregator();//H
//   params[4] = new AverageLocalAggregator();//h
//   params[5] = new MatchMatrix();
   ta = (SMThersholdAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNames.THERSHOLD_ALGORITHM,params);
   ta.init(candidate,target,matchAlgorithms.length,matchAlgorithms,SchemaMatchingAlgorithmsRunner.class);
   //ta.setPreprocessMatrixes(MatrixPreprocessor.TEMPLATE_PREPROCESSING,1);
   ta.setThreshold(0);
   ta.normalizeMatrixes();
   ta.setRecallRun(true);
   ta.setExactMapping(SchemaMatchingsUtilities.readXMLBestMatchingFile("1-time.xml_2-surfer.xml_EXACT/1-time.xml_2-surfer.xml_EXACT.xml"));
   ta.useStatistics();
   ta.setNonUniform(false,(byte)1);
   ta.setDebugMode(true);
   ta.runAlgorithm();
   
   synchronized(wait){
      while (!ta.isAlgorithmRunFinished()){
          try{
          	wait.wait(1000);
          }catch(InterruptedException e){}
       }
   }
   ta.getStatistics().printRecallInformation();
   ta.getStatistics().getIterationsPlot().printGraph();
   System.exit(0);
 }catch(Throwable e){
   e.printStackTrace();
   if (ta != null)
      ta.getStatistics().getIterationsCount();
   System.out.println(e.getMessage());
 }
  }
}

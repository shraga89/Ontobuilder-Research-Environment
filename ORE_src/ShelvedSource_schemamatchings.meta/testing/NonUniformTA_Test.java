

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
import schemamatchings.meta.algorithms.MatchAlgorithm;
import schemamatchings.meta.algorithms.MetaAlgorithmNames;
import schemamatchings.meta.algorithms.MetaAlgorithmsFactory;
import schemamatchings.meta.algorithms.SMThersholdAlgorithm;
import schemamatchings.meta.algorithms.Schema;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.util.SchemaMatchingAlgorithmsRunner;
import schemamatchings.util.SchemaMatchingsUtilities;

public class NonUniformTA_Test {

  //test meta algorithm
public static void main(String args[]){
 try{
   int k = 1000;
   Object[] params = new Object[3];
   OntoBuilderWrapper ob = new OntoBuilderWrapper();
   Schema candidate = ob.readOntologyXMLFile("onto_exact/www.postmaster.co.uk.xml",true);
   Schema target = ob.readOntologyXMLFile("onto_exact/www.myfunnymail.com.xml",true);
   byte nonUniformVersion = 1;
   Thread myThread = Thread.currentThread();
   MatchAlgorithm[] matchAlgorithms = new MatchAlgorithm[2];
   matchAlgorithms[0] = ob.loadMatchAlgorithm(MatchingAlgorithms.VALUE);
   matchAlgorithms[1] = ob.loadMatchAlgorithm(MatchingAlgorithms.PRECEDENCE);
//   matchAlgorithms[2] = ob.loadMatchAlgorithm(MatchingAlgorithms.VALUE);
//   matchAlgorithms[3] = ob.loadMatchAlgorithm(MatchingAlgorithms.COMPOSITION);
   params[0] = new Integer(k);
   params[1] = new AverageGlobalAggregator();//F
   params[2] = new AverageLocalAggregator();//f
   SMThersholdAlgorithm ta = (SMThersholdAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNames.THERSHOLD_ALGORITHM,params);
   ta.init(candidate,target,matchAlgorithms.length,matchAlgorithms,SchemaMatchingAlgorithmsRunner.class);
   ta.useStatistics();
   ta.setThreshold(0);
   ta.setExactMapping(SchemaMatchingsUtilities.readXMLBestMatchingFile("onto_exact/www.myfunnymail.co-www.postmaster.co.u1.xml"));
   ta.normalizeMatrixes();
   ta.setRecallRun(true);
   ta.setNonUniform(true,nonUniformVersion);
   ta.setDebugMode(true);
   ta.runAlgorithm();
   while (!ta.isAlgorithmRunFinished()){
     try{
           Thread.sleep(100);
       }catch(InterruptedException e){}
   }
   ta.printDebugString();
   //ta.getStatistics().printStatistics();
   ta.getStatistics().printRecallInformation();
   //ta.getStatistics().printIterationsPlot();
   }catch(Throwable e){
   System.out.println(e.getMessage());
   e.printStackTrace();
 }
}
}

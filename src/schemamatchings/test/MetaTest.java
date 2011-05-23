package schemamatchings.test;

import schemamatchings.meta.agr.SumGlobalAggregator;
import schemamatchings.meta.agr.SumLocalAggregator;
import schemamatchings.meta.algorithms.MatchAlgorithm;
import schemamatchings.meta.algorithms.MatrixDirectAlgorithm;
import schemamatchings.meta.algorithms.MetaAlgorithmNames;
import schemamatchings.meta.algorithms.MetaAlgorithmsFactory;
import schemamatchings.meta.algorithms.SMThersholdAlgorithm;
import schemamatchings.meta.algorithms.Schema;
import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.util.SchemaMatchingAlgorithmsRunner;
import schemamatchings.util.SchemaMatchingsUtilities;
import schemamatchings.util.SchemaTranslator;


public class MetaTest {

  public static void main(String args[]){
 try{
   int k = 20;
   Object[] params = new Object[3];
   OntoBuilderWrapper ob = new OntoBuilderWrapper();
   Schema candidate = ob.readOntologyXMLFile("ontologies/Aviation/www.elal.co.il.xml",true);
   Schema target = ob.readOntologyXMLFile("ontologies/Aviation/www.thy.com.xml",true);
   Thread myThread = Thread.currentThread();
   MatchAlgorithm[] matchAlgorithms = new MatchAlgorithm[2];
   matchAlgorithms[0] = ob.loadMatchAlgorithm(MatchingAlgorithms.TERM);
   matchAlgorithms[1] = ob.loadMatchAlgorithm(MatchingAlgorithms.VALUE);
//   matchAlgorithms[2] = ob.loadMatchAlgorithm(MatchingAlgorithms.TERM);
//   matchAlgorithms[3] = ob.loadMatchAlgorithm(MatchingAlgorithms.COMPOSITION);
   params[0] = new Integer(k);
   params[1] = new SumGlobalAggregator();
   params[2] = new SumLocalAggregator();
   SMThersholdAlgorithm ta = (SMThersholdAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNames.THERSHOLD_ALGORITHM,params);
   ta.init(candidate,target,matchAlgorithms.length,matchAlgorithms,SchemaMatchingAlgorithmsRunner.class);
   //ta.setPreprocessMatrixes(MatrixPreprocessor.TEMPLATE_PREPROCESSING,1);
   ta.runAlgorithm();
   while (!ta.isAlgorithmRunFinished()){
     try{
       Thread.sleep(100);
       }catch(InterruptedException e){}
   }
   params = new Object[4];
   matchAlgorithms[0] = ob.loadMatchAlgorithm(MatchingAlgorithms.TERM);
   matchAlgorithms[1] = ob.loadMatchAlgorithm(MatchingAlgorithms.VALUE);
//   matchAlgorithms[2] = ob.loadMatchAlgorithm(MatchingAlgorithms.PRECEDENCE);
//   matchAlgorithms[3] = ob.loadMatchAlgorithm(MatchingAlgorithms.COMPOSITION);
   params[0] = new Integer(k);
   params[1] = new SumGlobalAggregator();
   params[2] = new SumLocalAggregator();
   params[3] = new MatchMatrix();
   MatrixDirectAlgorithm mda = (MatrixDirectAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNames.MATRIX_DIRECT_ALGORITHM,params);
   mda.init(candidate,target,matchAlgorithms.length,matchAlgorithms,new SchemaMatchingAlgorithmsRunner());
   mda.runAlgorithm();
   for (int i=1;i<k+1;i++){
    SchemaTranslator stTA =  ((SchemaTranslator)ta.getKthBestMapping(i));
    boolean flag = false;
    //for (int j=1;j<k+1;j++){
      SchemaTranslator stMD =  ((SchemaTranslator)mda.getKthBestMapping(i));
      if (SchemaMatchingsUtilities.isSameTotalMappingWeight(stTA,stMD,7)){
        System.out.println("TA Mapping:"+i+" is like MD Mapping:"+i);
//        flag = true;
//        break;
      }

//    }
//    if (!flag)
//         System.out.println("TA Mapping:"+i+" has no equal mapping in MD");

   }
 }catch(Throwable e){
   System.out.println(e.getMessage());
   e.printStackTrace();
 }
  }
}
package ac.technion.schemamatching.test;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.Algorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithmNamesEnum;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.meta.MetaAlgorithmsFactory;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.MatrixDirectAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.misc.SMThersholdAlgorithm;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.SumGlobalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.aggregators.SumLocalAggregator;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchMatrix;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingAlgorithmsRunner;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingsUtilities;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;


public class MetaTest {

  public static void main(String args[]){
 try{
   int k = 20;
   Object[] params = new Object[3];
   OntoBuilderWrapper ob = new OntoBuilderWrapper();
   XmlFileHandler xfh = new XmlFileHandler();
   Ontology candidate = xfh.readOntologyXMLFile("ontologies/Aviation/www.elal.co.il.xml",true);
   Ontology target = xfh.readOntologyXMLFile("ontologies/Aviation/www.thy.com.xml",true);
   Algorithm[] matchAlgorithms = new Algorithm[2];
   matchAlgorithms[0] = ob.loadMatchAlgorithm(MatchingAlgorithmsNamesEnum.TERM);
   matchAlgorithms[1] = ob.loadMatchAlgorithm(MatchingAlgorithmsNamesEnum.VALUE);
//   matchAlgorithms[2] = ob.loadMatchAlgorithm(MatchingAlgorithms.TERM);
//   matchAlgorithms[3] = ob.loadMatchAlgorithm(MatchingAlgorithms.COMPOSITION);
   params[0] = new Integer(k);
   params[1] = new SumGlobalAggregator();
   params[2] = new SumLocalAggregator();
   SMThersholdAlgorithm ta = (SMThersholdAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNamesEnum.THERSHOLD_ALGORITHM,params);
   ta.init(candidate,target,matchAlgorithms.length,matchAlgorithms,new SchemaMatchingAlgorithmsRunner());
   //ta.setPreprocessMatrixes(MatrixPreprocessor.TEMPLATE_PREPROCESSING,1);
   ta.runAlgorithm();
   while (!ta.isAlgorithmRunFinished()){
     try{
       Thread.sleep(100);
       }catch(InterruptedException e){}
   }
   params = new Object[4];
   matchAlgorithms[0] = ob.loadMatchAlgorithm(MatchingAlgorithmsNamesEnum.TERM);
   matchAlgorithms[1] = ob.loadMatchAlgorithm(MatchingAlgorithmsNamesEnum.VALUE);
//   matchAlgorithms[2] = ob.loadMatchAlgorithm(MatchingAlgorithms.PRECEDENCE);
//   matchAlgorithms[3] = ob.loadMatchAlgorithm(MatchingAlgorithms.COMPOSITION);
   params[0] = new Integer(k);
   params[1] = new SumGlobalAggregator();
   params[2] = new SumLocalAggregator();
   params[3] = new MatchMatrix();
   MatrixDirectAlgorithm mda = (MatrixDirectAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNamesEnum.MATRIX_DIRECT_ALGORITHM,params);
   mda.init(candidate,target,matchAlgorithms.length,matchAlgorithms,new SchemaMatchingAlgorithmsRunner());
   mda.runAlgorithm();
   for (int i=1;i<k+1;i++){
	   MatchInformation stTA =  ta.getKthBestMapping(i);
    //boolean flag = false;
    //for (int j=1;j<k+1;j++){
	   MatchInformation stMD =  mda.getKthBestMapping(i);
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
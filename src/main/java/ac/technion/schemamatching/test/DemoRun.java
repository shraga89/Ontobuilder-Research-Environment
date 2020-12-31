
package ac.technion.schemamatching.test;

import java.util.Comparator;
import java.util.Map;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.operator.NGramStringOperator;
import ac.technion.iem.ontobuilder.core.utils.files.XmlFileHandler;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.common.MatchingAlgorithmsNamesEnum;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.TermAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line1.term.ValueAlgorithm;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper.SchemaMatchingsWrapper;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.wrapper.OntoBuilderWrapper;
import ac.technion.schemamatching.curpos.CurposTerm;

/**
 * @author haggai
 *
 */
public class DemoRun {

	public class EntryMaptFitnessComparator implements Comparator<Map.Entry<Double,CurposTerm>>{
		 
	    @Override
	    public int compare(Map.Entry<Double,CurposTerm> o1, Map.Entry<Double,CurposTerm> o2) {
	        return (o1.getKey()>o2.getKey() ? 1 : (o1.getKey()==o2.getKey() ? 0 : -1));
	    }
	} 
	
	public void EyalTest(){
		String str1 = "Test";
        String str2 = "tssssssssst";
        int n = 2;
        double result = new NGramStringOperator().compare(str1, str2, n);
        System.out.println("The outcome is:" + result);
	}
	
	public static void main(String[] args){
		
		DemoRun run = new DemoRun();
		run.EyalTest();
		return;
		
	}
	
	public void origTest(){
		
		   OntoBuilderWrapper ob = new OntoBuilderWrapper();
		   XmlFileHandler xfh = new XmlFileHandler();
		   try {
			Ontology target = xfh.readOntologyXMLFile("d:/demo/MevoJerusalem.xml",true);
			Ontology candidate = xfh.readOntologyXMLFile("d:/demo/NeptuneEilat.xml",true);
//			MatchAlgorithm[] matchAlgorithms = new MatchAlgorithm[2];
			TermAlgorithm term = (TermAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithmsNamesEnum.TERM);
			ValueAlgorithm value = (ValueAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithmsNamesEnum.VALUE);
			MatchInformation tm = term.match(candidate,target);
			MatchInformation vm = value.match(candidate,target);
			SchemaMatchingsWrapper smwt = new SchemaMatchingsWrapper(tm);
			SchemaMatchingsWrapper smwv = new SchemaMatchingsWrapper(vm);
			MatchInformation stt,stv;
			for (int i=1;i<=10;i++){
				stt = smwt.getNextBestMatching();
				stv = smwv.getNextBestMatching();
				System.out.println("Top-"+i);
				System.out.println("Term: "+stt.getTotalMatchWeight());
				System.out.println("Value: "+stv.getTotalMatchWeight());
				
			}
//			 = smw.getBestMatching();
//			MatchedAttributePair[] pairs = st.getMatchedPairs();
//			double sum = 0;
//			Term candTerm, tarTerm;
//			for (int i=0;i<pairs.length;i++){
//				candTerm = tm.getMatrix().getTermByName(pairs[i].getAttribute1(),tm.getMatrix().getCandidateTerms());
//				tarTerm = tm.getMatrix().getTermByName(pairs[i].getAttribute2(),tm.getMatrix().getTargetTerms());
//				sum += vm.getMatrix().getMatchConfidence(candTerm,tarTerm);
//			}
//			System.out.println(sum/(double)pairs.length);
		   } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		//**********************
//		 try{
//		    int k = 10;
//		    Object wait = new Object();
//		    Object[] params = new Object[4];
//		    OntoBuilderWrapper ob = new OntoBuilderWrapper();
//		    Ontology candidate = ob.readOntologyXMLFile("d:/demo/NeptuneEilat.xml",true);
//		    Ontology target = ob.readOntologyXMLFile("d:/demo/MevoJerusalem.xml",true);
//		    Thread myThread = Thread.currentThread();
//		    MatchAlgorithm[] matchAlgorithms = new MatchAlgorithm[2];
//		    matchAlgorithms[0] = ob.loadMatchAlgorithm(MatchingAlgorithms.TERM);
//		    matchAlgorithms[1] = ob.loadMatchAlgorithm(MatchingAlgorithms.VALUE);
//		    //matchAlgorithms[2] = ob.loadMatchAlgorithm(MatchingAlgorithms.VALUE);
//		    //matchAlgorithms[3] = ob.loadMatchAlgorithm(MatchingAlgorithms.COMPOSITION);
//		    params[0] = new Integer(k);
//		    params[1] = new SumGlobalAggregator();//F
//		    params[2] = new SumLocalAggregator();//f
////		    params[3] = new AverageGlobalAggregator();//H
////		    params[4] = new AverageLocalAggregator();//h
//		    params[3] = new MatchMatrix();
//		    MatrixDirectAlgorithm hybrid = (MatrixDirectAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNames.MATRIX_DIRECT_ALGORITHM,params);
//		    hybrid.init(candidate,target,matchAlgorithms.length,matchAlgorithms,new SchemaMatchingAlgorithmsRunner());
//		    hybrid.useStatistics();
//		    hybrid.normalizeMatrixes();
//		    hybrid.runAlgorithm();
//		    
//		    synchronized(wait){
//		     while (!hybrid.isAlgorithmRunFinished()){
//		         try{
//		         	wait.wait(1000);
//		         }catch(InterruptedException e){}
//		      }
//		  }
////		    for (int i=1;i<k+1;i++){
////		      //((SchemaTranslator)hybrid.getKthBestMapping(i)).saveMatchToXML(i,"Mevo_Jerusalem.xml","Neptune_Eilat.xml","mevo-neptune"+i);
////		    }
//		    hybrid.getStatistics().printStatistics();
//		    hybrid.getStatistics().getMappingsPlot().printGraph();
//		    for (int j=1;j<=k;j++){
//		    SchemaTranslator best = (SchemaTranslator)hybrid.getKthBestMapping(j);
//		    TermAlgorithm term = (TermAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithms.TERM);
//			ValueAlgorithm value = (ValueAlgorithm)ob.loadMatchAlgorithm(MatchingAlgorithms.VALUE);
//			MatchInformation tm = term.match(candidate,target);
//			MatchInformation vm = value.match(candidate,target);
//			MatchedAttributePair[] pairs = best.getMatchedPairs();
//			double sumTerm = 0;
//			double sumValue = 0;
//			Term candTerm, tarTerm;
//			for (int i=0;i<pairs.length;i++){
//				candTerm = tm.getMatrix().getTermByName(pairs[i].getAttribute1(),tm.getMatrix().getCandidateTerms());
//				tarTerm = tm.getMatrix().getTermByName(pairs[i].getAttribute2(),tm.getMatrix().getTargetTerms());
//				sumTerm += tm.getMatrix().getMatchConfidence(candTerm,tarTerm);
//				sumValue +=  vm.getMatrix().getMatchConfidence(candTerm,tarTerm);
//			}
//			System.out.println("Top-"+j);
//			System.out.println("Term: "+sumTerm);///(double)pairs.length);
//			System.out.println("Value: "+sumValue);///(double)pairs.length);
//		    }
//		    
//		    //best.printTranslations();
//		    }catch(Throwable e){
//		    System.out.println(e.getMessage());
//		    e.printStackTrace();
//		  }
////		  **********************
		   
	}
}

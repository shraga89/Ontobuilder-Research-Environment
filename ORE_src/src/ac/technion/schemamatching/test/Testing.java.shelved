package ac.technion.schemamatching.test;

import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingAlgorithmsRunner;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

public class Testing {

  long[] rightNames = {11,12,13};
  long[] leftNames = {21,22,23};
  double[][] adjMatrix = {{0.1,0.3,0.6},
                          {0.2,0.9,0.5},
                          {0.4,0.5,0.8}};

  public Testing() {
    try{
      SchemaMatchingAlgorithmsRunner smar = new SchemaMatchingAlgorithmsRunner();
      smar.setInitialSchema(leftNames);
      try{
        smar.setMatchedSchema(rightNames,adjMatrix);
      }catch(Exception e){
        e.printStackTrace();
        System.exit(1);
      }
      SchemaTranslator st;
      st = smar.getNextBestMatching(true);
      st.printTranslations();
      //prevSt = st;
//      st.printTranslations();
//      st = SchemaMatchingsUtilities.getSTwithThresholdSensitivity(st,0.1);
//      st.printTranslations();
      //st.saveMatchToXML(1,"candSchemata","targetSchemata","match");
      st = smar.getSecondBestMatching(false,true);
      st.printTranslations();
      //SchemaMatchingsUtilities.saveDiffBestMatchesToXML(prevSt,st,1,2,"candSchemata","targetSchemata","bestMatchDiff1_2");
//      st.printTranslations();
//      st = SchemaMatchingsUtilities.getSTwithThresholdSensitivity(st,0.1);
//      st.printTranslations();
      //st.saveMatchToXML(2,"candSchemata","targetSchemata","match");
     // SchemaMatchingsUtilities.printDiffBestMatches(prevSt,st);
     // prevSt = st;
      st = smar.getNextBestMatching(true);
      st.printTranslations();
      //      st.printTranslations();
//      st = SchemaMatchingsUtilities.getSTwithThresholdSensitivity(st,0.1);
//      st.printTranslations();
      //st.saveMatchToXML(3,"candSchemata","targetSchemata","match");
      //SchemaMatchingsUtilities.printDiffBestMatches(prevSt,st);
      //prevSt = st;
      st = smar.getNextBestMatching(true);
      st.printTranslations();
      //      st.printTranslations();
//      st = SchemaMatchingsUtilities.getSTwithThresholdSensitivity(st,0.1);
//      st.printTranslations();
      //st.saveMatchToXML(4,"candSchemata","targetSchemata","match");
      //SchemaMatchingsUtilities.printDiffBestMatches(prevSt,st);
     // prevSt = st;
      st = smar.getNextBestMatching(true);
      st.printTranslations();
      //      st.printTranslations();
//      st = SchemaMatchingsUtilities.getSTwithThresholdSensitivity(st,0.1);
//      st.printTranslations();
//      st.saveMatchToXML(5,"cand","target","newMatch.xml");
      //st.saveMatchToXML(5,"candSchemata","targetSchemata","match");
      //SchemaMatchingsUtilities.printDiffBestMatches(prevSt,st);
      System.exit(0);
    }catch(Throwable e){
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void main(String args[]){
    new Testing();
  }
}
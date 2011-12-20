package schemamatchings.meta.experiments;

import java.io.*;
import java.util.*;
import schemamatchings.ontobuilder.*;
import schemamatchings.meta.algorithms.AbstractMetaAlgorithm;

public class BatchedExperiments {

  public static String[][] ontologies = {
//   {"www.postmaster.co.uk.xml","www.myfunnymail.com.xml","www.myfunnymail.co-www.postmaster.co.u1.xml"},
//    {"212.59.22.112.xml","194.176.43.120.xml","194.176.43.12-212.59.22.111-EXACT.xml"},
//   {"www.datemeister.com.xml","www.amerciansingles.com.xml","www.amerciansingles.co-www.datemeister.co1-EXACT.xml"},
//   {"www.experienced-people.co.uk.xml","www.bbltamex.com.xml","www.bbltamex.co-www.experienced-people.co.u1-EXACT.xml"},
//   {"www.linuxmail.com.xml","www.boardermail.com.xml","www.boardermail.com.xml_www.linuxmail.com.xml_EXACT.xml"},
//  {"Galei_Eilat.xml","www.danielhotel.com.xml","www.danielhotel.co-Galei_Eilat-EXACT.xml"},
//    {"www.date.com.xml","www.cybersuitors.com.xml","www.cybersuitors.co-www.date.co1-EXACT.xml"},
 {"www.theatremania.org.xml","www.eservus.com.xml","www.eservus.co-www.theatremania.or1-EXACT.xml"},
//    {"Neptune_Eilat.xml","Mevo_Jerusalem.xml","Mevo_Jerusale-Neptune_Eila1.xml"},
//   {"search.scotsman.com.xml","search.sky.com.xml","search.sky.co-search.scotsman.co1-EXACT.xml"},
//    {"www1522.boca15-verio.com.xml","taut.securesites.com.xml","taut.securesites.co-www1522.boca15-verio.co1-EXACT.xml"},
//    {"www.hotellocators.com.xml","www.motels.com.xml","www.motels.co-www.hotellocators.co-EXACT.xml"},
  };

  public static double thresholds[] = {/*0.0,0.025,0.05/*,0.1,*/0.15/*,0.2,0.25,0.5,0.75*/};

  /**
   *
   * @param args
   *  args[0] := candidate ontology file path
   *  args[1] := target ontology file path
   *  args[2] := threshold (0.0 | 0.25 | 0.5 | 0.75)
   *  args[3] := exact mapping file path (not in use in our setting - pass it "NA")
   *  args[4] := non uniform flag (true: non uniform , false: regular)
   *  args[5] := non uniform version (1 | 2)
   *  args[6] := Local f aggregator
   *  args[7] := Global F aggregator
   *  args[8] := Local h aggregator
   *  args[9] := Global H aggregator
   */
  public static void main(String[] args){
    try{
//      PrintWriter taNUMatrix = new PrintWriter(new FileOutputStream("analysis/taNUMatrix.txt"));
//      PrintWriter hybNUMatrix = new PrintWriter(new FileOutputStream("analysis/hybNUMatrix.txt"));
      OntoBuilderWrapper ob = new OntoBuilderWrapper();
      String candOntology = args[0];
      String targetOntology = args[1];
      boolean nonUniform = false;
      int type = AbstractMetaAlgorithm.NON_UNIFORM_MAX;
      byte nonUniformVersion = 1;
      Experiment[] experiments = new Experiment[5];
      boolean[] metaAlgorithms = new boolean[4];
      metaAlgorithms[0] = true;//TA
      metaAlgorithms[1] = false;//MD
      metaAlgorithms[2] = true;//MDB
      metaAlgorithms[3] = true;//HYBRID
      String[] schemas = new String[2];
      schemas[0] = (String)candOntology;
      schemas[1] = (String)targetOntology;
      String[][] matchAlgComb = {{MatchingAlgorithms.TERM,MatchingAlgorithms.VALUE}/*,
      {MatchingAlgorithms.TERM,MatchingAlgorithms.PRECEDENCE}*//*,
      {MatchingAlgorithms.TERM,MatchingAlgorithms.COMPOSITION},
      {MatchingAlgorithms.VALUE,MatchingAlgorithms.PRECEDENCE},
      {MatchingAlgorithms.VALUE,MatchingAlgorithms.COMPOSITION}/*
          {MatchingAlgorithms.TERM,MatchingAlgorithms.VALUE,MatchingAlgorithms.PRECEDENCE,MatchingAlgorithms.COMPOSITION}*/};

           String[] filenameSuffix = {/*"T_V_P_C","T_V","T_P",*/"T_C"/*,"V_P","V_C"*/};
           String[][] arrgregators = {/*{"Sum","Sum","Sum","Sum"},*/
           {"Average","Average","Average","Average"}//,
               /*{"Average","Min","Average","Average"}*/};

//           new File("statistics_2_judges_small_th").mkdir();
//           new File("statistics_2_judges_small_th_object").mkdir();
           for (int j=0;j<ontologies.length;j++){
             for (int k=0;k<thresholds.length;k++){
               schemas[0] = ontologies[j][0];
               schemas[1] = ontologies[j][1];
               for (int l=0;l<arrgregators.length;l++)
                 for (int i=0;i<filenameSuffix.length;i++){
               if (!arrgregators[l][1].equals("Average") && thresholds[k] > 0) continue;
               experiments[i] = new Experiment(ob,null);
//               experiments[i].setTANUMatrixFile(taNUMatrix);
//               experiments[i].setHYBNUMatrixFile(hybNUMatrix);
               experiments[i].setRunAlgorithms(metaAlgorithms);
               experiments[i].setSchemas(schemas);
               //if (!args[3].equals("NA"))
               experiments[i].loadExactMapping(ontologies[j][2]);
               experiments[i].setK(20,0);
               experiments[i].setRecallRun(false);
               experiments[i].setMatchAlgorithms(matchAlgComb[i]);
               experiments[i].setFilenameSuffix(arrgregators[l][0]+"("+thresholds[k]+")_"+arrgregators[l][1]+"_"+filenameSuffix[i]+"TA_MDB_Hyb");
               experiments[i].setAggregators(arrgregators[l]);
               experiments[i].setLocalAvgAggrThreshold(thresholds[k]);
               experiments[i].setGlobalAvgAggrThreshold(0);
               experiments[i].setNonUniform(nonUniform,nonUniformVersion);
               experiments[i].setNonUniformType(type);
               experiments[i].setOutputDir("zig");
               experiments[i].setDebugMode(false);
               experiments[i].runExperiment();
               //experiments[i].saveStatisticsToObjectFile("2_judges_new");
              experiments[i].saveStatisticsToTXTFile();
                 }
             }
           }




    }catch(Throwable e){
      e.printStackTrace();
    }
  }
}
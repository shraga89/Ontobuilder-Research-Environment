package schemamatchings.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import schemamatchings.meta.match.MatchedAttributePair;
import schemamatchings.ontobuilder.MatchMatrix;
import schemamatchings.ontobuilder.MatchingAlgorithms;
import schemamatchings.ontobuilder.OntoBuilderWrapper;
import schemamatchings.topk.algorithms.TopKAlgorithm;
import schemamatchings.topk.wrapper.SchemaMatchingsWrapper;
import schemamatchings.util.SchemaMatchingsUtilities;
import schemamatchings.util.SchemaTranslator;

import com.modica.ontobuilder.ApplicationParameters;
import com.modica.ontology.Ontology;
import com.modica.ontology.algorithm.SimilarityFloodingAlgorithm;
import com.modica.ontology.match.Match;
import com.modica.ontology.match.MatchInformation;
/**
 * <p>Title: Methods for running tests on matching algorithms</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author amir
 * @version 1.0
 */


public class OntoBuilderTest {
/*
  private static final String exactFileName[] = {
        "xml2/cms.lufthansa.com.xml_www.britishairways.com.xml_EXACT.xml",
        "xml2/www.americanairlines.com.xml_www.continental.com.xml_EXACT.xml",
        "xml2/www.elal.co.il.xml_www.thy.com.xml_EXACT.xml",
        "xml2/www.delta.com.xml_www.ual.com.xml_EXACT.xml",
        "xml2/www.amazon.com.xml_www.powells.com.xml_EXACT.xml",
        "xml2/www.audible.com.xml_www.sagebrushcorp.com.xml_EXACT.xml",
        "xml2/www.resqrentacar.co.za.xml_www.autoboutiquerental.com.xml_EXACT.xml"
  };

   private static final String targetFileName[] = {
       "xml2/www.britishairways.com.xml",
        "xml2/www.continental.com.xml",
        "xml2/www.thy.com.xml",
        "xml2/www.ual.com.xml",
        "xml2/www.powells.com.xml",
        "xml2/www.sagebrushcorp.com.xml",
        "xml2/www.autoboutiquerental.com.xml"
  };

   private static final String candidateFileName[] = {
        "xml2/cms.lufthansa.com.xml",
        "xml2/www.americanairlines.com.xml",
        "xml2/www.elal.co.il.xml",
        "xml2/www.delta.com.xml",
        "xml2/www.amazon.com.xml",
        "xml2/www.audible.com.xml",
        "xml2/www.resqrentacar.co.za.xml"
   };

*/


    private static String exactFileName[] = {
      "xml/www.motels.co-www.hotellocators.co-EXACT.xml",
      "xml/www.bbltamex.co-www.experienced-people.co.u1-EXACT.xml",
      "xml/www.eservus.co-www.theatremania.or1-EXACT.xml",
      "xml/www.cybersuitors.com.xml_www.date.com.xml_EXACT.xml",
      "xml/www.amerciansingles.co-www.datemeister.co1-EXACT.xml",
      "xml/taut.securesites.co-www1522.boca15-verio.co1-EXACT.xml",
      "xml/194.176.43.12-212.59.22.111-EXACT.xml",
      "xml/www.myfunnymail.co-www.postmaster.co.u1.xml",
      "xml/Mevo_Jerusale-Neptune_Eila1.xml",
      "xml/search.sky.co-search.scotsman.co1-EXACT.xml"
  } ;




 private static  String targetFileName[] = {
     "xml/www.hotellocators.com.xml" ,
     "xml/www.experienced-people.co.uk.xml",
     "xml/www.eservus.com.xml",
     "xml/www.date.com.xml",
     "xml/www.datemeister.com.xml",
     "xml/www1522.boca15-verio.com.xml",
     "xml/212.59.22.112.xml",
     "xml/www.postmaster.co.uk.xml",
     "xml/Neptune_Eilat.xml",
     "xml/search.scotsman.com.xml"
  };

  private static String candidateFileName[] = {
      "xml/www.motels.com.xml",
      "xml/www.bbltamex.com.xml",
      "xml/www.theatremania.org.xml",
      "xml/www.cybersuitors.com.xml",
      "xml/www.amerciansingles.com.xml",
      "xml/taut.securesites.com.xml",
      "xml/194.176.43.120.xml",
      "xml/www.myfunnymail.com.xml",
      "xml/Mevo_Jerusalem.xml",
      "xml/search.sky.com.xml"
  }  ;

  private static final String algorithmIds[] = {MatchingAlgorithms.TERM,MatchingAlgorithms.TERM_VALUE_COMBINED};
  private static final int NUM_OF_Ks = 10;

  public static final int EXP_4_PRINT_TYPE = kResult.PRINT_ALL;

  public static void main(String args[]){

    // some dtds access work differently when using a back test access.
    //ApplicationParameters.is_gui = false;
    int num_of_args = args.length;
    try{
      OntoBuilderWrapper ob = new OntoBuilderWrapper();

      for ( int j = 0 ; j < args.length ; j++)
        if (args[j].equals("-loadDir")){
          loadDir(args[j + 1]);
          num_of_args = num_of_args - 2;
        }

      // Prints a list for each file/algo combo - all the terms in the order of their first appearance.
      if (args[0].equals("test6")){
              TestResult test4result = test6(ob,fromFile(num_of_args,args),toFile(num_of_args,args));
            printDb("FINISHED RUNNING!!! ");
            ((TestResult4)test4result).printToFileChanges("Experiments-Changes");
          }

      /* Same as test4, only that ensures a 1-1 matching - this is used for exp1 and exp2*/
      /*  parameters: fileIndex
        or
                 startFileIndex, endFileIndex
       -rand     randomize the match table before creating matches
       -group    print 8 different files, separated according to term/combined and the ratio between
       the number of correct matches in the first threshold of k=10 to the number of candidate terms.
       */
      if (args[0].equals("test5")){
        boolean useRand = false, printGroups=false,printOnlyLast=false;
        for ( int j = 0 ; j < args.length ; j++)
          if (args[j].equals("-rand")){
            useRand = true;
            num_of_args--;
          }
        for ( int j = 0 ; j < args.length ; j++)
          if (args[j].equals("-group")){
            printGroups = true;
            num_of_args--;
          }
        for ( int j = 0 ; j < args.length ; j++)
            if (args[j].equals("-last")){
              printOnlyLast = true;
              num_of_args--;
            }

        TestResult[] test5result = new TestResult[5] ;
        int fromFileIndex = fromFile(num_of_args,args);
        int toFileIndex = toFile(num_of_args,args);
        TestResult[][] allRandTests = new TestResult[5][10] ;
         if (useRand){

           for (int q = 1 ; q<6 ; q++)
             for (int counter= 0 ; counter<10 ; counter++){
               print("Q = 0." + q + ": " + counter);
               allRandTests[q-1][counter] = test5(ob, fromFileIndex, toFileIndex, useRand,( (double) q) / (double) 10);
             }
      /*     test5result[0] = test5(ob, fromFileIndex, toFileIndex,useRand,0.1);
           test5result[1] = test5(ob, fromFileIndex, toFileIndex,useRand,0.2);
           test5result[2] = test5(ob, fromFileIndex, toFileIndex,useRand,0.3);
           test5result[3] = test5(ob, fromFileIndex, toFileIndex,useRand,0.4);
           test5result[4] = test5(ob, fromFileIndex, toFileIndex,useRand,0.5);*/
         }
         else
           test5result[0] = test5(ob, fromFileIndex, toFileIndex,useRand,0.1);
         printDb("FINISHED RUNNING ");
         if (useRand){
           printCombinedResult(allRandTests[0],"Experiments-0.1",printOnlyLast);
           printCombinedResult(allRandTests[1],"Experiments-0.2",printOnlyLast);
           printCombinedResult(allRandTests[2],"Experiments-0.3",printOnlyLast);
           printCombinedResult(allRandTests[3],"Experiments-0.4",printOnlyLast);
           printCombinedResult(allRandTests[4],"Experiments-0.5",printOnlyLast);
   /*        ((TestResult4)test5result[0]).printToFileThreshold("Experiments-0.1");
           ((TestResult4)test5result[1]).printToFileThreshold("Experiments-0.2");
           ((TestResult4)test5result[2]).printToFileThreshold("Experiments-0.3");
           ((TestResult4)test5result[3]).printToFileThreshold("Experiments-0.4");
           ((TestResult4)test5result[4]).printToFileThreshold("Experiments-0.5");*/
         }
         else
           if (!printGroups)
             ((TestResult4)test5result[0]).printToFileThreshold("Experiments",printOnlyLast);
           else{
             ((TestResult4)test5result[0]).printToFileThreshold("Experiments-Term-00_25",true,0,true,printOnlyLast);
             ((TestResult4)test5result[0]).printToFileThreshold("Experiments-Combined-00_25",true,0,false,printOnlyLast);
             ((TestResult4)test5result[0]).printToFileThreshold("Experiments-Term-25_50",true,1,true,printOnlyLast);
             ((TestResult4)test5result[0]).printToFileThreshold("Experiments-Combined-25_50",true,1,false,printOnlyLast);
             ((TestResult4)test5result[0]).printToFileThreshold("Experiments-Term-50_75",true,2,true,printOnlyLast);
             ((TestResult4)test5result[0]).printToFileThreshold("Experiments-Combined-50_75",true,2,false,printOnlyLast);
             ((TestResult4)test5result[0]).printToFileThreshold("Experiments-Term-75_100",true,3,true,printOnlyLast);
             ((TestResult4)test5result[0]).printToFileThreshold("Experiments-Combined-75_100",true,3,false,printOnlyLast);
           }
         //test4result.printToFile("Experiments");
       //  ((TestResult4)test5result[0]).printToFile("exp5_Result_2_5",true);
       }


  /*   When Running test4 - for each file, and each k, calculates how many unchanged
    matches  were found - split by correct and incorrect.
      test4result can either produce a summation of the results (results1), or a detail
   of all the attributes for each k (results2).
   To get full result, the parameter true should be supplied as the second parameter
   to test4result.printToFile .

   parameters:   fileIndex
   or
                 startFileIndex, endFileIndex
   */
      if (args[0].equals("test4")){

        TestResult test4result = test4(ob,fromFile(num_of_args,args),toFile(num_of_args,args));
         printDb("FINISHED RUNNING!!! ");
         ((TestResult4)test4result).printToFileThreshold("Experiments");
         test4result.printToFile("Experiments");
         ((TestResult4)test4result).printToFile("exp4_Result_2n",true);
       }

       //   When Running test3
       if (args[0].equals("test3")){
         TestResult test3result = null ;
         if (num_of_args > 1){
           try{
             int fileIndex = Integer.parseInt(args[1]);
             test3result = test2(ob,fileIndex,fileIndex,false);
           } catch (NumberFormatException e) {}

         }
         else
           test3result = test2(ob,false);
         printDb("FINISHED RUNNING!!! ");
         test3result.printToFile("exp3_Result.0");
       }

   //   When Running test2

        if (args[0].equals("test2")){
          TestResult test2result = null ;
          if (num_of_args > 1){
            try{
              int fileIndex = Integer.parseInt(args[1]);
              test2result = test2(ob,fileIndex,fileIndex,true);
            } catch (NumberFormatException e) {}

          }
          else
            test2result = test2(ob,true);
          printDb("FINISHED RUNNING!!! ");
          test2result.printToFile("exp2_Result.2");
        }

 //    When Running test1
    if (args[0].equals("test1")){
      TestResult test1result = test1(ob);
      printDb("FINISHED RUNNING!!! ");
      //print(test1result.printResult());
      test1result.printToFile("exp1_Result");
    }

//       For Debug purposes - prints a specific xml match, given the files, algorithm, and k
   //parameters:  fileIndex, algorithmIndex, k
    if (args[0].equals("specific"))
      printSpecific(ob,Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]),"temps/");

// same as above, only with specific file names.
   //parameters:  target filename, candidate file name, algorithmIndex, k
    if (args[0].equals("specificFN"))
      printSpecificByFileName(ob,args[1],args[2],Integer.parseInt(args[3]),Integer.parseInt(args[4]),"temps/");

      //    For a given k, algorithm, and file, prints all the matches found and their weight in a neat form
        //parameters:  fileIndex, algorithmIndex, k, fileName
         if (args[0].equals("edgePrinter")){
           if (num_of_args == 5)
           printEdges(ob,Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]),args[4]);
           else
             printEdgesSpecify(ob,args[1],args[2],Integer.parseInt(args[3]),Integer.parseInt(args[4]),args[5]);
         }

   //    For a given file and algorithm, prints all the weights between any two candidate/target terms
     //parameters:  fileIndex, algorithmIndex,output file Name prefix
     // or : candidateFileName, TargetFileName,algorithmIndex,Output file Name prefix
      if (args[0].equals("printAllMatrixEdges")){
        if (num_of_args == 4)
          printAllMatrixEdges(ob,Integer.parseInt(args[1]),Integer.parseInt(args[2]),args[3]);
        else
          printAllMatrixEdgesSpecify(ob, args[1], args[2],
                                     Integer.parseInt(args[3]), args[4]);
      }

      //   used to check the TopKAlgorithm
      // parameters: fileIndex, k, threshold
      if (args[0].equals("checkMe"))
        checkMe(ob, Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                Double.parseDouble(args[3]));

        //   used to check the SubSchemaAlgorithm
      if (args[0].equals("checkme2"))
        checkMe(ob, Integer.parseInt(args[1]));

        // will print the exact matches as mentioned in the xml file specified
      if (args[0].equals("printExact"))
           printExact(args[1]);

   }catch(Throwable e){
      e.printStackTrace();
    }
   }

  private static TestResult test1(OntoBuilderWrapper ob) throws Exception {
      Ontology candidate,target ;
      SchemaMatchingsWrapper smw = null;
      MatchInformation match;
      SchemaTranslator k_translator, exactTranslator;
      int totalFilesCount = exactFileName.length,totalMatchedCorrect=0,totalExactMatches;
      Vector kResults;
      TestResult1 testResult = new TestResult1();
      for (int algorithm = 0 ; algorithm < algorithmIds.length ; algorithm++)
        for (int fileCounter = 0; fileCounter < totalFilesCount && fileCounter<=9; fileCounter++) {

          // initialize file specific ontologies and translator
          candidate = ob.readOntologyXMLFile(candidateFileName[fileCounter], false);
          target = ob.readOntologyXMLFile(targetFileName[fileCounter], false);
          exactTranslator = SchemaMatchingsUtilities.readXMLBestMatchingFile(
              exactFileName[fileCounter]);

          // match according to the algorithm the candidate with the target
          match = ob.loadMatchAlgorithm(algorithmIds[algorithm]).match(target,
              candidate);

          //this Object wraps all the top k upper functionality
          if (fileCounter == 0)
            smw = new SchemaMatchingsWrapper(match.getMatrix());
          else
            smw.reset(match.getMatrix());
            //this object represents a mapping and also is used to translate mapped terms..

          totalExactMatches = exactTranslator.getMatchedAttributesPairsCount();
          kResults = new Vector(NUM_OF_Ks);
          for (int k = 1; k <= NUM_OF_Ks; k++) {
            // get the next k best match matrix
            k_translator = smw.getNextBestMatching();
            // find how many of the exact matches were found in the algo match
            totalMatchedCorrect = howManyMatched(k_translator,exactTranslator);
            // match.saveToXML(new File("xml/myTestResponse.xml"));
            kResults.add(new kResult(k,totalMatchedCorrect));
              //  k_translator.saveMatchToXML(k,candidateFileName,targetFileName,"xml/myTestResponse"+k);
              //  print("For k = " + k + ", got " + totalMatchedCorrect + " out of " +  totalCorrectMatches);
          }

          // add the results of the current file
          testResult.addFileResult(TestResult1.convertType(algorithmIds[algorithm]),
                                   candidateFileName[fileCounter],
                                   targetFileName[fileCounter],
                                   totalExactMatches,
                                   kResults);
        }
    return testResult ;
  }

  private static TestResult test2(OntoBuilderWrapper ob, boolean checkCorrect) throws
      Exception {
    return test2(ob, 0, 9, checkCorrect);
  }
/* Calculates for each file, algorithm type, and K value the number of
      */
  private static TestResult test2(OntoBuilderWrapper ob,int startWithIndex,int endWithIndex,boolean checkCorrect) throws Exception {
    printDb("test2 from " + startWithIndex + " till " + endWithIndex + "\n");
        Ontology candidate,target ;
        SchemaMatchingsWrapper smw = null;
        MatchInformation match;
        SchemaTranslator k_translator, exactTranslator;
        int totalFilesCount = exactFileName.length,totalMatches=0;
        Vector kResults;
        kResult kresult ;
        TestResult2 testResult = new TestResult2();
        for (int algorithm = 0 ; algorithm < algorithmIds.length ; algorithm++)
          for (int fileCounter = startWithIndex; fileCounter < totalFilesCount && fileCounter<=endWithIndex; fileCounter++) {
            kResults = new Vector() ;
            // initialize file specific ontologies and translator
            candidate = ob.readOntologyXMLFile(candidateFileName[fileCounter], false);
            target = ob.readOntologyXMLFile(targetFileName[fileCounter], false);
            exactTranslator = SchemaMatchingsUtilities.readXMLBestMatchingFile(
                exactFileName[fileCounter]);

            // match according to the algorithm the candidate with the target
            match = ob.loadMatchAlgorithm(algorithmIds[algorithm]).match(target,
                candidate);
           //this Object wraps all the top k upper functionality
            if (smw == null)
              smw = new SchemaMatchingsWrapper(match.getMatrix());
            else
              smw.reset(match.getMatrix());

            kResults = new Vector(NUM_OF_Ks);
            for (int k = 1; k <= NUM_OF_Ks; k++) {

              // get the next k best match matrix
              k_translator = smw.getNextBestMatching();


              // in case we're looking for Correct matches, this will be the total of EXACT
              //   matches, otherwise(looking for total incorrect matches) - is will be the
              //   total of MATCHED matches.
            if (checkCorrect)
              totalMatches = exactTranslator.getMatchedAttributesPairsCount();
            else
              totalMatches = k_translator.getMatchedAttributesPairsCount();

              // generate a set of all the weights of the correct matches.
              if (checkCorrect)
                kresult = correctMatchesByWeight(k_translator,exactTranslator);
              else
                kresult = inCorrectMatchesByWeight(k_translator,exactTranslator);

              kResults.add(kresult);
         }

            // add the results of the current file
            testResult.addFileResult(TestResult1.convertType(algorithmIds[algorithm]),
                                     candidateFileName[fileCounter],
                                     targetFileName[fileCounter],
                                     totalMatches,
                                     kResults);
          }
      return testResult ;
    }

/*  Test4
     For each file and algorithm, runs on all (10) the K mappings, and collects
     for each k, and each mapping:
       1. How many times did this mapping appear in all the k's up to this one
       2. Is this a true mapping
   To do this, the method maintains two vectors FOR EACH K, and places them in a
 kResult object. The first one contains all the true mappings, and the second -

 */
  private static TestResult test4(OntoBuilderWrapper ob,int startWithIndex,int endWithIndex) throws Exception {
    Ontology candidate,target ;


    //this Object wraps all the top k upper functionality
    SchemaMatchingsWrapper smw = null;

    // match according to the algorithm the candidate with the target
    MatchInformation match;

    // used to hold the exact and current mappings
    SchemaTranslator k_translator, exactTranslator;

    // used to keep the complete test result
    TestResult4 testResult = new TestResult4();

    // used to hold the results for each file/algorithm
    Vector correctAttributeMappings,incorrectAttributeMappings;

    // used to hold for each file the kResult s objects related to it
    Vector kresultsVec ;

    // other variables
    MatchedAttributePair matchedPairs[];
    MatchedPairData mpd ;
    boolean found ;
    kResult kresult ;

      // run on all the files
    for (int fileCounter = startWithIndex; fileCounter < exactFileName.length && fileCounter<=endWithIndex; fileCounter++) {
      // initialize all the file specific variables
      candidate = ob.readOntologyXMLFile(candidateFileName[fileCounter], false);
      target = ob.readOntologyXMLFile(targetFileName[fileCounter], false);
      exactTranslator = SchemaMatchingsUtilities.readXMLBestMatchingFile(
        exactFileName[fileCounter]);

             // for each algorithm
      for (int algorithm = 0; algorithm < algorithmIds.length; algorithm++) {
        correctAttributeMappings = new Vector();
        incorrectAttributeMappings = new Vector();
        kresultsVec = new Vector();

        // create the algorithm match
        match = ob.loadMatchAlgorithm(algorithmIds[algorithm]).match(target,
          candidate);
        // smw is recyclable
        if (smw == null)
          smw = new SchemaMatchingsWrapper(match.getMatrix());
        else
          smw.reset(match.getMatrix());

          // calculate the statistics
        for (int k = 1; k <= NUM_OF_Ks; k++) {
          correctAttributeMappings = kResult.cloneMatchedPairData(correctAttributeMappings);
          incorrectAttributeMappings = kResult.cloneMatchedPairData(incorrectAttributeMappings);
          // get the next k best match matrix
          k_translator = smw.getNextBestMatching();

          matchedPairs = k_translator.getMatchedPairs();

        /* runs over all the k mappings, checks for each one - first if it is
           true or not. Then, according to the appropriate vector, checks
           if we have encountered this mapping before.
             if we have - then the method increments its counter in the appropriate vector.
             otherwise - it adds it with a 1 counter to the said vector.
        */
          for (int i = 0 ; i < matchedPairs.length ; i++){
            found = false ;
            if (isAnExactMapping(matchedPairs[i], exactTranslator) ){

              for (int j = 0; j < correctAttributeMappings.size(); j++) {
                mpd = (MatchedPairData) correctAttributeMappings.get(j);
                if (mpd.isSamePair(matchedPairs[i])) {
                  mpd.incrementNumberAppeared();
                  found = true;
                }
              }
              if (!found) {
                correctAttributeMappings.add(new MatchedPairData(matchedPairs[i]));
              }

            }
            else {
              for (int j = 0; j < incorrectAttributeMappings.size(); j++) {
                 mpd = (MatchedPairData) incorrectAttributeMappings.get(j);
                 if (mpd.isSamePair(matchedPairs[i])) {
                   mpd.incrementNumberAppeared();
                   found = true;
                 }
               }
               if (!found) {
                 incorrectAttributeMappings.add(new MatchedPairData(matchedPairs[i]));
               }
             }

         }
 //    printDb("adding corr = " + correctAttributeMappings + "\n");
         kresultsVec.add(new kResult(k,correctAttributeMappings,incorrectAttributeMappings));

      }

        // add the results of the current file
        testResult.addFileResult(TestResult1.convertType(algorithmIds[algorithm]),
                                 candidateFileName[fileCounter],
                                 targetFileName[fileCounter],
                                 exactTranslator.getMatchedAttributesPairsCount(),
                                 kresultsVec,
                                 match.getMatrix().getCandidateAttributeNames().length,
                               match.getMatrix().getTargetAttributeNames().length,false);

      }
    }
    return testResult ;
  }

/** Very similar to test4, with two differences:
*  1. the use - the results of test5 are meant to run with the print method printToFileThreshold
* which calculates for each K and each threshold (minimum nubmer of times a match needs to appear in
* order to be included in a match) the recall (ratio of correct matches found to total matches found )
* and precision (ratio of correct matches found to total correct matches).
* 2. the use of 1:1 matchings- meaning that the matches stored in the vector which is used to build kResult
* is different from the one that is used to build and calculate the matchings in that it will not contain matchesthat share the
* same candidate attribute or target attribute. Since matches which contracdict 1:1 matchings still can affect the
* vector (in that, when a conflict arrises between two matches, the one with the highest appearance is taken, and
* then the one that appeared first), both sets of vectors are kept and updated, but only the 'update' vectors
* (verses the 'work' vectors) are used for building kResult.
*
   * @param ob Wrapper for Ontobuilder object
   * @param startWithIndex index in the internal table from which to read xml file names
   * @param endWithIndex index in the internal table up to which to read xml file names
   * @param useRandom boolean value - if passed true, then the function generates its own random match
   * @param q the degree of randomness generates based on the original matrix - each cell in the original matrix
   * will get a new value of +-q relative to its original value.
   * matrix instead of using one based on the xml files.
   * @return a TestResult object of type TestResult4 with all the information collected
   * @see TestResult4
   * @throws Exception - an exception
  **/
  private static TestResult test5(OntoBuilderWrapper ob,int startWithIndex,int endWithIndex,boolean useRandom,double q) throws Exception {
   Ontology candidate,target ;


   //this Object wraps all the top k upper functionality
   SchemaMatchingsWrapper smw = null;

   // match according to the algorithm the candidate with the target
   MatchInformation match;

   // used to hold the exact and current mappings
   SchemaTranslator k_translator, exactTranslator;

   // used to keep the complete test result
   TestResult4 testResult = new TestResult4();

   // used to hold the results for each file/algorithm
   Vector correctAttributeMappings,incorrectAttributeMappings,correctAttributesToSave,incorrectAttributesToSave;

   // used to hold for each file the kResult s objects related to it
   Vector kresultsVec ;

   // other variables
   MatchedAttributePair matchedPairs[];
   MatchedPairData mpd ;
   boolean found ;

     // run on all the files
   for (int fileCounter = startWithIndex; fileCounter < exactFileName.length && fileCounter<=endWithIndex; fileCounter++) {
     // initialize all the file specific variables

     print (fileCounter + ") " + exactFileName[fileCounter] + " : " + candidateFileName[fileCounter] +
            " ; " + targetFileName[fileCounter]);
 candidate = ob.readOntologyXMLFile(candidateFileName[fileCounter], false);
     target = ob.readOntologyXMLFile(targetFileName[fileCounter], false);
         MatchMatrix Mtrx = new MatchMatrix();

     exactTranslator = SchemaMatchingsUtilities.readXMLBestMatchingFile(exactFileName[fileCounter]);
            // for each algorithm
     for (int algorithm = 0; algorithm < algorithmIds.length; algorithm++) {
       correctAttributeMappings = new Vector();
       incorrectAttributeMappings = new Vector();
       correctAttributesToSave = new Vector();
       incorrectAttributesToSave = new Vector() ;
       kresultsVec = new Vector();

       // create the algorithm match

       match = ob.loadMatchAlgorithm(algorithmIds[algorithm]).match(target,candidate);
       if (useRandom){
         int totalCands = match.getMatrix().getCandidateAttributeNames().length;
         int totalTargs = match.getMatrix().getTargetAttributeNames().length;
         /*   Vector terms = candidate.getModel().getTerms();
           for (int i = 0 ; i < terms.size() ; i++)
             System.out.print(i + ": "+terms.get(i)+"\t");
          print("");
           terms = target.getModel().getTerms();
           for (int i = 0 ; i < terms.size() ; i++)
             System.out.print(i + ": "+terms.get(i)+"\t");*/

     //    print("use Random. " + totalCands + " candidates; " + totalTargs +" targets");
       //  print("real matrix has " + match.getMatrix().getColCount() + " and " + match.getMatrix().getRowCount());
         ArrayList candRandTerms = match.getMatrix().getCandidateTerms(),
           targRandTerms = match.getMatrix().getTargetTerms();
         Mtrx = new MatchMatrix(totalCands, totalTargs, candRandTerms,targRandTerms);
         double[][] confMatrix = new double[totalTargs][totalCands];
         double[][] realMatrix = match.getMatrix().getMatchMatrix();
//         print("Matrix: ");
         NumberFormat nf = NumberFormat.getInstance();
         nf.setMaximumFractionDigits(3);
    /*     for (int i = 0; i < totalTargs; i++) {
           for (int j = 0; j < totalCands; j++)
             System.out.print(nf.format(realMatrix[i][j]) + "\t");
           System.out.print("\n");
         }*/
      //   print("\nMatrix " +(algorithm==0 ? "Term " : "Combined ") + q);

         for (int i = 0; i < totalTargs; i++) {
           for (int j = 0; j < totalCands; j++) {
          /*   if (realMatrix[i][j] == 0 )
               confMatrix[i][j] = 0 ;
             else*/
             confMatrix[i][j] = Math.max(0.0, realMatrix[i][j] + 2*q*Math.random()-q);
      //       System.out.print(nf.format(confMatrix[i][j]) + "\t");
           }
     //      System.out.print("\n");
         }


         Mtrx.setMatchMatrix(confMatrix);
       }
       else
         Mtrx = match.getMatrix();

       // smw is recyclable

       if (smw == null)
         smw = new SchemaMatchingsWrapper(Mtrx);
       else
         smw.reset(Mtrx);

         // calculate the statistics
       for (int k = 1; k <= NUM_OF_Ks; k++) {
 //        correctAttributeMappings = kResult.cloneMatchedPairData(correctAttributeMappings);
   //      incorrectAttributeMappings = kResult.cloneMatchedPairData(incorrectAttributeMappings);
         correctAttributesToSave = kResult.cloneMatchedPairData(correctAttributesToSave);
         incorrectAttributesToSave = kResult.cloneMatchedPairData(incorrectAttributesToSave);
         // get the next k best match matrix
         k_translator = smw.getNextBestMatching();

         matchedPairs = k_translator.getMatchedPairs();

       /* runs over all the k mappings, checks for each one - first if it is
          true or not. Then, according to the appropriate vector, checks
          if we have encountered this mapping before.
            if we have - then the method increments its counter in the appropriate vector.
            otherwise - it adds it with a 1 counter to the said vector.
       */
         for (int i = 0 ; i < matchedPairs.length ; i++){
           found = false ;
           if (isAnExactMapping(matchedPairs[i], exactTranslator) ){

             for (int j = 0; j < correctAttributeMappings.size(); j++) {
               mpd = (MatchedPairData) correctAttributeMappings.get(j);
               if (mpd.isSamePair(matchedPairs[i])) {
                 mpd.incrementNumberAppeared();
                 incrementToSaveVector(correctAttributesToSave,correctAttributeMappings,incorrectAttributeMappings,matchedPairs[i],mpd.getCountNumberAppeared());
                 found = true;
               }
             }
             if (!found) {
               correctAttributeMappings.add(new MatchedPairData(matchedPairs[i]));
               addToSaveVector(correctAttributesToSave,correctAttributeMappings,incorrectAttributeMappings,matchedPairs[i]);

             }

           }
           else {
             for (int j = 0; j < incorrectAttributeMappings.size(); j++) {
                mpd = (MatchedPairData) incorrectAttributeMappings.get(j);
                if (mpd.isSamePair(matchedPairs[i])) {
                  mpd.incrementNumberAppeared();
                  incrementToSaveVector(incorrectAttributesToSave,correctAttributeMappings,incorrectAttributeMappings,matchedPairs[i],mpd.getCountNumberAppeared());
                 found = true;
                }
              }
              if (!found) {
                incorrectAttributeMappings.add(new MatchedPairData(matchedPairs[i]));
                addToSaveVector(incorrectAttributesToSave,correctAttributeMappings,incorrectAttributeMappings,matchedPairs[i]);
              }
            }

        }
//    printDb("adding corr = " + correctAttributeMappings + "\n");
//        kresultsVec.add(new kResult(k,correctAttributeMappings,incorrectAttributeMappings));
        kresultsVec.add(new kResult(k,correctAttributesToSave,incorrectAttributesToSave));

     }

       // add the results of the current file
       testResult.addFileResult(TestResult1.convertType(algorithmIds[algorithm]),
                                candidateFileName[fileCounter],
                                targetFileName[fileCounter],
                                exactTranslator.getMatchedAttributesPairsCount(),
                                kresultsVec,
                                match.getMatrix().getCandidateAttributeNames().length,
                                match.getMatrix().getTargetAttributeNames().length,true);
     }
   }
   return testResult ;
 }


 /**
  * Same as regular test5, only without the last boolean value. Basically calls test5 with
  * false as the last parameter
  * @param ob OntoBuilderWrapper
  * @param startWithIndex int
  * @param endWithIndex int
  * @throws Exception
  * @return TestResult
  */
 private static TestResult test5(OntoBuilderWrapper ob,int startWithIndex,int endWithIndex) throws Exception {
   return test5(ob,startWithIndex, endWithIndex,false,0);
 }

/**
* For each file and algorithm, runs on all (10) the K mappings, and collects
* changes between one K match and another - all matches that were not found
* in the current match and not in any of the previous ones. It then signifies whether
* these changes are correct or not.
 *
 * @param ob OntoBuilderWrapper
 * @param startWithIndex int
 * @param endWithIndex int
 * @throws Exception
 * @return TestResult
 */
private static TestResult test6(OntoBuilderWrapper ob,int startWithIndex,int endWithIndex) throws Exception {
  Ontology candidate,target ;


//this Object wraps all the top k upper functionality
  SchemaMatchingsWrapper smw = null;

// match according to the algorithm the candidate with the target
  MatchInformation match;

// used to hold the exact and current mappings
  SchemaTranslator k_translator, exactTranslator;

// used to keep the complete test result
  TestResult4 testResult = new TestResult4();

// used to hold the results for each file/algorithm
  Vector firstMatches;
  LinkedList termChanges ;
 // Vector correctAttributeMappings,incorrectAttributeMappings;

// used to hold for each file the kResult s objects related to it
  Vector kresultsVec ;

// other variables
  MatchedAttributePair matchedPairs[];
  MatchedPairData mpd ;
  boolean found ;

 // run on all the files
  for (int fileCounter = startWithIndex; fileCounter < exactFileName.length && fileCounter<=endWithIndex; fileCounter++) {
   // initialize all the file specific variables
    candidate = ob.readOntologyXMLFile(candidateFileName[fileCounter], false);
    target = ob.readOntologyXMLFile(targetFileName[fileCounter], false);
    exactTranslator = SchemaMatchingsUtilities.readXMLBestMatchingFile(
    exactFileName[fileCounter]);

   // for each algorithm
    for (int algorithm = 0; algorithm < algorithmIds.length; algorithm++) {
      firstMatches = new Vector();
      termChanges = new LinkedList();
      kresultsVec = new Vector();

    // create the algorithm match
      match = ob.loadMatchAlgorithm(algorithmIds[algorithm]).match(target,
        candidate);
    // smw is recyclable
      if (smw == null)
        smw = new SchemaMatchingsWrapper(match.getMatrix());
      else
        smw.reset(match.getMatrix());

      // calculate the statistics
      for (int k = 1; k <= NUM_OF_Ks; k++) {
        // get the next k best match matrix
        k_translator = smw.getNextBestMatching();

        matchedPairs = k_translator.getMatchedPairs();

      // initialize the first matches - so they wont be considered 'new'
        if (k==1)
          for (int i = 0 ; i < matchedPairs.length ; i++)
            firstMatches.add(new MatchedPairData(matchedPairs[i],
                                                 isAnExactMapping(matchedPairs[i], exactTranslator)));
        else {
          for (int i = 0 ; i < matchedPairs.length ; i++){
            // check first that they did not appear in the first matches
            found = false;
            for (int j = 0 ; j<firstMatches.size() ; j++){
              mpd =  (MatchedPairData) firstMatches.get(j);
              if (mpd.isSamePair(matchedPairs[i]))
                found = true;
            }

          // now check if we already encountered it since the first match
            for (int j = 0 ; j<termChanges.size() ; j++){
              mpd =  (MatchedPairData) termChanges.get(j);
              if (mpd.isSamePair(matchedPairs[i]))
                found = true;
            }

            if (!found)
            // it is a new term, need to add it to the list of new matched, with
            // the appropriate tag - correct or not
              termChanges.add(new MatchedPairData(matchedPairs[i],
                                                  isAnExactMapping(matchedPairs[i], exactTranslator)));
          }
        }
      }

    // add the results to those of the current file result object - TestResult4 overloads this
    // method from TestReult .
      testResult.addFileResult(TestResult1.convertType(algorithmIds[algorithm]),
                               candidateFileName[fileCounter],
                               targetFileName[fileCounter],
                               exactTranslator.getMatchedAttributesPairsCount(),
                               firstMatches,
                               termChanges,
                               match.getMatrix().getCandidateAttributeNames().length,
                               match.getMatrix().getTargetAttributeNames().length);
    }
  }
  return testResult ;
}


/**
 * checks SimilarityFloodingAlgorithm by running the ontology in fileindex and checking the output
 * @param ob OntoBuilderWrapper
 * @param fileIndex int
 */

private static void checkMe(OntoBuilderWrapper ob,int fileIndex) {
  try {
     Ontology candidate = ob.readOntologyXMLFile(candidateFileName[fileIndex], false);
     Ontology target = ob.readOntologyXMLFile(targetFileName[fileIndex], false);
     print ("OBT: cand has " +candidate.getTermsCount()+" or " + candidate.getModel().getTerms().size());
     SimilarityFloodingAlgorithm subs = new SimilarityFloodingAlgorithm();
     MatchInformation info = subs.match(candidate, target);

     FileWriter file = new FileWriter("checkMe.xls");
     file.write("Check Me got matching information for " +
                candidateFileName[fileIndex] + " - " +
                targetFileName[fileIndex] + "\n");
     ArrayList matches = info.getMatches();
     file.write("matches are : \n" + matches+"\n");
    for (int i = 0 ; i <  matches.size() ; i++)
      file.write(((Match)matches.get(i)).getCandidateTerm().toString() +"\t"+((Match)matches.get(i)).getTargetTerm().toString()+"\n");
     file.write("\n\n");
     double[][] mm = info.getMatchMatrix();
     StringBuffer buffer = new StringBuffer();

     /*
     file.write("match matrix[][]: \n");
     if (mm != null){
     for (int i = 0; i < info.getTargetOntologyTermsTotal(); i++) {
       for (int j = 0; j < info.getCandidateOntologyTermsTotal(); j++)
         buffer.append("\t" + mm[i][j]);
       buffer.append("\n");
     }
     file.write(buffer.toString());
     file.write("\n\n");
     }
     else
       file.write("match matrix null");
     */
     MatchMatrix mmx = info.getMatrix();
if (mmx != null){
       file.write("candidate Attribute Terms from getMatrix\n");
       for (int i = 0; i < mmx.getCandidateAttributeNames().length; i++)
         file.write(mmx.getCandidateAttributeNames()[i] + "\n");
       file.write("\n\n");
       file.write("target Attribute Terms from getMatrix\n");
       for (int i = 0; i < mmx.getTargetAttributeNames().length; i++)
         file.write(mmx.getTargetAttributeNames()[i] + "\n");
     }else file.write("mmx null");
     file.write("\n\nMismatched Candidate\n");
     for (int i = 0;i< info.getMismatchesCandidateOntology().size(); i++)
          file.write(info.getMismatchesCandidateOntology().get(i).toString() + "\n");
     file.write("\n\nMismatched Target\n");
     for (int i = 0;i< info.getMismatchesTargetOntology().size(); i++)
    file.write(info.getMismatchesTargetOntology().get(i).toString() + "\n");

        file.write("\n\nOriginal Candidate\n");
        for (int i = 0;i< info.getOriginalCandidateTerms().size(); i++)
      file.write(info.getOriginalCandidateTerms().get(i).toString() + "\n");

       file.write("\nOriginal Target\n");
       for (int i = 0;i< info.getOriginalTargetTerms().size(); i++)
         file.write(info.getOriginalTargetTerms().get(i).toString() + "\n");

         file.write("\nTotal Matches:\t"+ info.getTotalMatches()+"\n");

         file.write("\nCandidate Terms from mmx\n" );
         if (mmx != null){

           for (int i = 0; i < mmx.getCandidateTerms().size(); i++)
             file.write(mmx.getCandidateTerms().get(i).toString() + "\n");

           file.write("\nTarget Terms from mmx\n");
           for (int i = 0; i < mmx.getTargetTerms().size(); i++)
             file.write(mmx.getTargetTerms().get(i).toString() + "\n");

           file.write("\nCandidate Terms Names from mmx\n");
           for (int i = 0; i < mmx.getCandidateTermNames().length; i++)
             file.write(mmx.getCandidateTermNames()[i] + "\n");

           file.write("\nTarget Terms Names from mmx\n");
           for (int i = 0; i < mmx.getTargetTermNames().length; i++)
             file.write(mmx.getTargetTermNames()[i] + "\n");

           mm = mmx.getMatchMatrix();
           file.write("match matrix from mmx: ");
           buffer = new StringBuffer();
           for (int i = 0; i < mmx.getRowCount(); i++) {
             for (int j = 0; j < mmx.getColCount(); j++)
               buffer.append("\t" + mm[i][j]);
             buffer.append("\n");
           }
         }
     file.write(buffer.toString() + "\n\n");
     file.close();

   }
   catch (Exception e) {
     e.printStackTrace();
   }
   print ("finished checking!!!");
}


 /**
  * checks TopKAlgorithm
  * @param ob OntoBuilderWrapper
  * @param fileIndex int
  * @param k int
  * @param threshold double
  */
 private static void checkMe(OntoBuilderWrapper ob,int fileIndex,int k,double threshold) {
    try {
      Ontology candidate = ob.readOntologyXMLFile(candidateFileName[fileIndex], false);
      Ontology target = ob.readOntologyXMLFile(targetFileName[fileIndex], false);
      TopKAlgorithm topkAlgo = new TopKAlgorithm();
      FileWriter file = new FileWriter("checkMe.xls");
      MatchInformation info = topkAlgo.match(candidate, target, k, threshold);
      file.write("Check Me got matching information for " +
                 candidateFileName[fileIndex] + " - " +
                 targetFileName[fileIndex] + ". K= " + k + ", threshold = " +
                 threshold + "\n");
      ArrayList matches = info.getMatches();
      file.write("matches are : \n" + matches+"\n");
     for (int i = 0 ; i <  matches.size() ; i++)
       file.write(((Match)matches.get(i)).getCandidateTerm().toString() +"\t"+((Match)matches.get(i)).getTargetTerm().toString()+"\n");
      file.write("\n\n");
      double[][] mm = info.getMatchMatrix();
      file.write("match matrix[][]: \n");
      StringBuffer buffer = new StringBuffer();
      for (int i = 0; i < info.getCandidateOntologyTermsTotal(); i++) {
        for (int j = 0; j < info.getTargetOntologyTermsTotal(); j++)
          buffer.append("\t" + mm[i][j]);
        buffer.append("\n");
      }
      file.write(buffer.toString());
      file.write("\n\n");

      MatchMatrix mmx = info.getMatrix();

      file.write("candidate Attribute Terms from getMatrix\n");
      for (int i = 0; i < mmx.getCandidateAttributeNames().length; i++)
        file.write(mmx.getCandidateAttributeNames()[i] + "\n");
      file.write("\n\n");
      file.write("target Attribute Terms from getMatrix\n");
      for (int i = 0; i < mmx.getTargetAttributeNames().length; i++)
        file.write(mmx.getTargetAttributeNames()[i] + "\n");
      file.write("\n\nMismatched Candidate\n");
      for (int i = 0;i< info.getMismatchesCandidateOntology().size(); i++)
           file.write(info.getMismatchesCandidateOntology().get(i).toString() + "\n");
      file.write("\n\nMismatched Target\n");
      for (int i = 0;i< info.getMismatchesTargetOntology().size(); i++)
     file.write(info.getMismatchesTargetOntology().get(i).toString() + "\n");

         file.write("\n\nOriginal Candidate\n");
         for (int i = 0;i< info.getOriginalCandidateTerms().size(); i++)
       file.write(info.getOriginalCandidateTerms().get(i).toString() + "\n");

        file.write("\nOriginal Target\n");
        for (int i = 0;i< info.getOriginalTargetTerms().size(); i++)
          file.write(info.getOriginalTargetTerms().get(i).toString() + "\n");

          file.write("\nTotal Matches:\t"+ info.getTotalMatches()+"\n");

          file.write("\nCandidate Terms from mmx\n" );

          for (int i = 0;i< mmx.getCandidateTerms().size(); i++)
              file.write(mmx.getCandidateTerms().get(i).toString() + "\n");


          file.write("\nTarget Terms from mmx\n");
          for (int i = 0;i< mmx.getTargetTerms().size(); i++)
            file.write(mmx.getTargetTerms().get(i).toString() + "\n");

          file.write("\nCandidate Terms Names from mmx\n");
          for (int i = 0;i< mmx.getCandidateTermNames().length; i++)
           file.write(mmx.getCandidateTermNames()[i] + "\n");

         file.write("\nTarget Terms Names from mmx\n" );
                    for (int i = 0;i< mmx.getTargetTermNames().length; i++)
                     file.write(mmx.getTargetTermNames()[i] + "\n");


        mm = mmx.getMatchMatrix();
      file.write("match matrix from mmx: ");
      buffer = new StringBuffer();
      for (int i = 0; i < mmx.getRowCount(); i++) {
        for (int j = 0; j < mmx.getColCount(); j++)
          buffer.append("\t" + mm[i][j]);
        buffer.append("\n");
      }
      file.write(buffer.toString() + "\n\n");
      file.close();

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


  private static int howManyMatched(SchemaTranslator k_translator,SchemaTranslator exactTranslator){
   int totalCorrectMatches = exactTranslator.getMatchedAttributesPairsCount();
   int totalMatchedPairs = k_translator.getMatchedAttributesPairsCount();
  // printDb("correctMatches:" + totalCorrectMatches + ". matched: " + totalMatchedPairs);

   MatchedAttributePair correctPair;
   int totalMatchedCorrect = 0 ;
   for(int i=0 ; i< totalCorrectMatches ; i++){
      correctPair = exactTranslator.getMatchedAttributePair(i);
    //  System.out.print("matching " + i + "("+correctPair.getAttribute1()+")....  ");
      if (k_translator.isExist(correctPair))
        totalMatchedCorrect++ ;
  //    print("got it!");
   }
   return totalMatchedCorrect;

  }



  /*  Create Match
   */
    public static kResult createMatch(Ontology candidate,Ontology target,int UpToK,String algorithm) throws Exception {
      OntoBuilderWrapper ob = new OntoBuilderWrapper();

      // match according to the algorithm the candidate with the target
      MatchInformation match;

      // used to hold the exact and current mappings
      SchemaTranslator k_translator;

     // other variables
      MatchedAttributePair matchedPairs[];
      MatchedPairData mpd ;
      boolean found ;
      kResult kresult ;
      Vector attributeMappings = new Vector();
          // create the algorithm match
      match = ob.loadMatchAlgorithm(algorithm).match(target,candidate);
      //this Object wraps all the top k upper functionality
      SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(match.getMatrix());

            // calculate the statistics
          for (int k = 1; k <= UpToK; k++) {
            attributeMappings = kResult.cloneMatchedPairData(attributeMappings);

            // get the next k best match matrix
            k_translator = smw.getNextBestMatching();

            matchedPairs = k_translator.getMatchedPairs();

          /* runs over all the k mappings, checks for each one - first if it is
             true or not. Then, according to the appropriate vector, checks
             if we have encountered this mapping before.
               if we have - then the method increments its counter in the appropriate vector.
               otherwise - it adds it with a 1 counter to the said vector.
          */
            for (int i = 0 ; i < matchedPairs.length ; i++){
              found = false ;

                for (int j = 0; j < attributeMappings.size(); j++) {
                  mpd = (MatchedPairData) attributeMappings.get(j);
                  if (mpd.isSamePair(matchedPairs[i])) {
                    mpd.incrementNumberAppeared();
                    found = true;
                  }
                }
                if (!found) {
                  attributeMappings.add(new MatchedPairData(matchedPairs[i]));
                }

           }

        }
        kResult result = new kResult(UpToK,attributeMappings,null) ;
        result.setMatchInfo(match);
        return result;
    }



  /*  Returns a kResult with a Set of Doubles containing the weight of all correct matchings found by k_translator,
      ordered by weight
      For each exact translation, the method runs over all the matches, and sees if they are the same as
        the exact translation. If they are - the method adds a Double with the weight of the matched
        Pair to the set.
   */
  private static kResult correctMatchesByWeight(SchemaTranslator k_translator,SchemaTranslator exactTranslator){
    MatchedAttributePair correctPair,matchedPairs[];

    int totalCorrectMatches = exactTranslator.getMatchedAttributesPairsCount();
    LinkedList weightList = new LinkedList() ;
    kResult result = new kResult() ;
    int totalMatchedCorrectly = 0 ;
    matchedPairs = k_translator.getMatchedPairs() ;
    for (int i = 0; i < totalCorrectMatches; i++) {
      correctPair = exactTranslator.getMatchedAttributePair(i) ;
      for (int j=0;j<matchedPairs.length;j++){
        if(isSamePair(correctPair,matchedPairs[j])){
          totalMatchedCorrectly++ ;
      //    printDb("found same pair: " + correctPair.getAttribute1() + ", " + correctPair.getAttribute2());
          addToListInOrder(weightList,new Double(matchedPairs[j].getMatchedPairWeight()));
          break;
        }
     }
    }
    result.setTotalCorrect(totalMatchedCorrectly);
    result.setWeightList(weightList);
  //  printDb("finished generating kResult:\n"+result.printSet());
    return result;
  }

  private static kResult inCorrectMatchesByWeight(SchemaTranslator k_translator,SchemaTranslator exactTranslator){
      MatchedAttributePair matchedPairs[];
      boolean found = false;

      int totalCorrectMatches = exactTranslator.getMatchedAttributesPairsCount();
      LinkedList weightList = new LinkedList() ;
      kResult result = new kResult() ;
      int totalMatchedInCorrectly = 0 ;
      matchedPairs = k_translator.getMatchedPairs() ;
      for (int j=0;j<matchedPairs.length;j++){
        found = false ;
        for (int i = 0; i < totalCorrectMatches; i++)
          if(isSamePair(exactTranslator.getMatchedAttributePair(i),matchedPairs[j]))
            found = true;
        if (!found)  {
          totalMatchedInCorrectly++;
          addToListInOrder(weightList,
                           new Double(matchedPairs[j].getMatchedPairWeight()));
        }

      }
      result.setTotalCorrect(totalMatchedInCorrectly);
      result.setWeightList(weightList);
    //  printDb("finished generating kResult:\n"+result.printSet());
      return result;
    }

  private static void addToListInOrder(LinkedList weightList,Double value){
    if (weightList.isEmpty()) {
      weightList.add(value);
      return ;
    }
    for ( int i = 0; i < weightList.size() ; i++ )
      if (value.doubleValue() < ((Double)weightList.get(i)).doubleValue()){
        weightList.add(i, value);
        return;
      }
//    if (value.doubleValue() > ((Double)weightList.getLast()).doubleValue())
    weightList.addLast(value);
  }


  private static void  print(String s){
    System.out.println(s);
  }

  private static void printDb(String s){
    System.out.println("OntoBuilderTest: "+s);
  }

  private static void printSpecific(OntoBuilderWrapper ob,int fileIndex,int algorithmIndex,int k,String outputFileNamePath){
     MatchInformation match;
     SchemaTranslator k_translator;
     String fileName = outputFileNamePath + targetFileName[fileIndex].split("/",2)[1] + "-" +
         candidateFileName[fileIndex].split("/",2)[1] +
         "-" + algorithmIds[algorithmIndex] + "-" + k + ".xml" ;
     try{
         Ontology candidate = ob.readOntologyXMLFile(candidateFileName[fileIndex], false);
         Ontology target = ob.readOntologyXMLFile(targetFileName[fileIndex], false);
         match = ob.loadMatchAlgorithm(algorithmIds[algorithmIndex]).match(target,candidate);
         SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(match.getMatrix());
         k_translator = smw.getKthBestMatching(k);
         k_translator.saveMatchToXML(k,candidateFileName[fileIndex],targetFileName[fileIndex],fileName);
     }
     catch (Exception e) {
       e.printStackTrace()  ;
     }
     //fixXml(outputFileName);

  }

  private static void printSpecificByFileName(OntoBuilderWrapper ob,String targetfilename,String candidateFileName,int algorithmIndex,int k,String outputFileNamePath){
     MatchInformation match;
     SchemaTranslator k_translator;
     String fileName = outputFileNamePath + targetfilename.substring(targetfilename.lastIndexOf("/")+1) + "-" +
         candidateFileName.substring(candidateFileName.lastIndexOf("/")+1) +
         "-" + algorithmIds[algorithmIndex] + "-" + k + ".xml" ;
      String fileName2 = fileName.substring(0,fileName.length()-4)+".txt";
     try{
         Ontology candidate = ob.readOntologyXMLFile(candidateFileName, false);
         Ontology target = ob.readOntologyXMLFile(targetfilename, false);
         match = ob.loadMatchAlgorithm(algorithmIds[algorithmIndex]).match(target,candidate);
         SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(match.getMatrix());
         k_translator = smw.getKthBestMatching(k);
         MatchedAttributePair[] matches = k_translator.getMatchedPairs();
         FileWriter out = new FileWriter(fileName2);
         for (int i = 0 ; i < matches.length ; i++){
           out.write(matches[i].getAttribute1() + "\t" + matches[i].getAttribute2()+"\n");
         }
         out.close();
         k_translator.saveMatchToXML(k,candidateFileName,targetfilename,fileName);
     }
     catch (Exception e) {
       e.printStackTrace()  ;
     }
     //fixXml(outputFileName);

  }



  private static void printEdges(OntoBuilderWrapper ob,int fileIndex,int algorithmIndex,int k,String outputFileName){
    printEdgesSpecify(ob,candidateFileName[fileIndex],targetFileName[fileIndex],algorithmIndex,k,outputFileName);
  }

  /**
   * Prints all the edges of a match between two XML ontologies, for a given K and algorithm
   * @param ob OntoBuilderWrapper object
   * @param canFileName Name of the candidate xml file name
   * @param tarFileName Name of the target file name
   * @param algorithmIndex Index in algorithmIds array of the algorithm to use
   * @param k Integer - the K matching to generate
   * @param outputFileName Name of the output file name
   */
  private static void printEdgesSpecify(OntoBuilderWrapper ob,String canFileName,String tarFileName,int algorithmIndex,int k,String outputFileName){
     MatchInformation match;
     SchemaTranslator k_translator;
     String fileName = outputFileName + canFileName.split("/",2)[1] + "-" +
         tarFileName.split("/",2)[1] +
         "-" + algorithmIds[algorithmIndex] + "-" + k ;
     try{
         Ontology candidate = ob.readOntologyXMLFile(canFileName, false);
         Ontology target = ob.readOntologyXMLFile(tarFileName, false);
         match = ob.loadMatchAlgorithm(algorithmIds[algorithmIndex]).match(target,candidate);
         SchemaMatchingsWrapper smw = new SchemaMatchingsWrapper(match.getMatrix());
         k_translator = smw.getKthBestMatching(k);
         MatchedAttributePair[] map = k_translator.getMatchedPairs();
         FileWriter file = new FileWriter(fileName + ".xls");

         for (int i = 0 ; i < map.length ; i++ )
           file.write(map[i].getAttribute1()+"\t"+map[i].getAttribute2()+"\t"+map[i].getMatchedPairWeight()+"\n") ;

         file.close();
        }
     catch (Exception e) {
       e.printStackTrace()  ;
     }

  }

  private static void printAllMatrixEdges(OntoBuilderWrapper ob,int fileIndex,int algorithmIndex,String outputFileName){
    printAllMatrixEdgesSpecify(ob,candidateFileName[fileIndex],targetFileName[fileIndex],algorithmIndex,outputFileName);
  }

  /**
   * Prints all the edges of the matrix between two XML ontologies.
   * @param ob OntoBuilderWrapper
   * @param canFileName String
   * @param tarFileName String
   * @param algorithmIndex int
   * @param outputFileName String
   */
  private static void printAllMatrixEdgesSpecify(OntoBuilderWrapper ob,String canFileName,String tarFileName,int algorithmIndex,String outputFileName){
     MatchInformation match;
     String fileName = outputFileName + canFileName.split("/",2)[1] + "-" +
         tarFileName.split("/",2)[1] +
         "-" + algorithmIds[algorithmIndex] ;
     try{
         Ontology candidate = ob.readOntologyXMLFile(canFileName, false);
         Ontology target = ob.readOntologyXMLFile(tarFileName, false);
         match = ob.loadMatchAlgorithm(algorithmIds[algorithmIndex]).match(target,candidate);
         String candidateTerms[] = match.getMatrix().getCandidateAttributeNames();
         String targetTerms[] = match.getMatrix().getTargetAttributeNames();
         double weights[][] =match.getMatrix().getMatchMatrix();

         FileWriter file = new FileWriter(fileName + ".xls");
         for (int i = 0 ; i < candidateTerms.length; i++ )
           file.write("\t"+candidateTerms[i]);
        file.write("\n");

        for (int i = 0 ; i < targetTerms.length ; i++ ){
          file.write( targetTerms[i]);
          for (int j = 0; j < candidateTerms.length; j++)
            file.write( "\t" + weights[i][j]);
          file.write("\n");
        }

         file.close();
        }
     catch (Exception e) {
       e.printStackTrace()  ;
     }

  }


  /**
   * Prints all the edges of the exact matrix between two ontologies.
   *  @param exactXmlFileName String
   */
  private static void printExact(String exactXmlFileName){
     MatchInformation match;
     String fileName ;

     if (exactXmlFileName.indexOf("/")!= -1)
       fileName = exactXmlFileName.substring(exactXmlFileName.indexOf("/")+1,exactXmlFileName.lastIndexOf("."));
       else
         fileName = exactXmlFileName.substring(0,exactXmlFileName.lastIndexOf("."));
     try{
       FileWriter file = new FileWriter(fileName + ".xls");
       SchemaTranslator exactTranslator = SchemaMatchingsUtilities.readXMLBestMatchingFile(exactXmlFileName);
       int totalCorrectMatches = exactTranslator.getMatchedAttributesPairsCount();
       file.write("Total Terms:\t"+totalCorrectMatches+"\n");
       file.write("Candidate Term\tTarget Term\n");
      for (int j=0;j<totalCorrectMatches;j++){
        // print ("match " + j + " : " + exactTranslator.getMatchedAttributePair(j));
        file.write(exactTranslator.getMatchedAttributePair(j).getAttribute1() + "\t"+
                      exactTranslator.getMatchedAttributePair(j).getAttribute2() + "\n");
      }
        file.close();
        }
     catch (Exception e) {
       e.printStackTrace()  ;
     }

  }



  private static void fixXml(String fileName) {
    int charRead= -1, prevRead= -1;
    String fixedFileName ;
    try{
      fixedFileName = fileName.split(".")[1] + "-fixed.xml";
    } catch (Exception e) {
      fixedFileName = fileName + "-fixed.xml";
    }
    try{
      FileReader input = new FileReader(fileName);
      FileWriter output = new FileWriter(fixedFileName);

      output.write(input.read());
      prevRead = input.read();
      while ( (charRead = input.read()) != -1) {
        if (prevRead == '<' && charRead != '/')
          output.write('\n');
        output.write(prevRead);
        prevRead = charRead;
      }
      output.write(prevRead);
      output.close();
      input.close();
    } catch (IOException e){
      printDb("fixXml: while processing " + fileName+ " ("+ prevRead + "," + charRead + ")");
      e.printStackTrace();
    }
  }

  private static boolean isSamePair(MatchedAttributePair first,MatchedAttributePair second){
//    printDb("checking \"" + first.getAttribute2() + "\" against \"" + second.getAttribute2()+ "\"");
  //  print("first = " + first + " ; second = " + second);
    if (first.getAttribute1().equals(second.getAttribute1()) &&
        first.getAttribute2().equals(second.getAttribute2()))
      return true ;
    return false;
  }

  private static boolean isAnExactMapping(MatchedAttributePair match,SchemaTranslator exactTranslator){
    int totalCorrectMatches = exactTranslator.getMatchedAttributesPairsCount();
  //  print ("isAnExactMapping got " + totalCorrectMatches +
    //       " matches. comparing to " + match.getAttribute1()+"->"+match.getAttribute2());
    for (int j=0;j<totalCorrectMatches;j++){
     // print ("match " + j + " : " + exactTranslator.getMatchedAttributePair(j));
      if (isSamePair(exactTranslator.getMatchedAttributePair(j), match))
        return true;
    }
    return false;
  }

/* for test5: When incrementing a match in a work vector, the update vector needs to
  be updated accordingly. The use of work and update vectors is done to ensure that the
 matchings will be 1:1
 The algorithm is as follows:
 1. check if the match to update exists in the update vector. if so - increment it and end
 2. (the match doesnt exist in the update vector) check in the work vectors to see if there is
    a match which appeared as many times at least as the new one, and violates 1:1 matching with
    the new match. if so - end
 3. (the new match doesnt exist, and it should be added)
    check in the work vectors if there exists a match which violates
    1:1 matchings, but has a lower appearance count than the one to add
          if so - then find the corresponding match in the update vector and replace it with the new match
      (there must be one, otherwise the match would already have appeared in the update vector)
    */
  private static void incrementToSaveVector(Vector updateVector,Vector correctMatchings,Vector incorrectMatchings,MatchedAttributePair pairToIncremenet,int newCount){
    MatchedPairData tempMpd,tempMpd2,newMpd;
    boolean should_add = true ;
 // check update vector for existing match
    for (int i = 0 ; i<updateVector.size(); i++) {
       tempMpd = (MatchedPairData) updateVector.get(i);
       if (tempMpd.attribute1.equals(pairToIncremenet.getAttribute1()) &&
           tempMpd.attribute2.equals(pairToIncremenet.getAttribute2())   ) {
         tempMpd.incrementNumberAppeared();
         return;
       }
    }

 // check if there are matches which prevent adding this match
    for (int i = 0 ; i<correctMatchings.size() ; i++){
      tempMpd = (MatchedPairData) correctMatchings.get(i);
      if (tempMpd.getCountNumberAppeared() >= newCount  &&
           ( tempMpd.attribute1.equals(pairToIncremenet.getAttribute1()) &&
             !tempMpd.attribute2.equals(pairToIncremenet.getAttribute2())     ) ||
           ( !tempMpd.attribute1.equals(pairToIncremenet.getAttribute1()) &&
             tempMpd.attribute2.equals(pairToIncremenet.getAttribute2())      )
         )
         return;
    }

    for (int i = 0 ; i<incorrectMatchings.size() ; i++){
      tempMpd = (MatchedPairData) incorrectMatchings.get(i);
      if (tempMpd.getCountNumberAppeared() >= newCount  &&
           ( tempMpd.attribute1.equals(pairToIncremenet.getAttribute1()) &&
             !tempMpd.attribute2.equals(pairToIncremenet.getAttribute2())     ) ||
           ( !tempMpd.attribute1.equals(pairToIncremenet.getAttribute1()) &&
             tempMpd.attribute2.equals(pairToIncremenet.getAttribute2())      )
         )
         return;
    }


// check work vectors for matches which need to be replaced by this new match
    for (int i = 0 ; i<correctMatchings.size(); i++) {
       tempMpd = (MatchedPairData) correctMatchings.get(i);
       if (tempMpd.getCountNumberAppeared() < newCount  &&
            ( tempMpd.attribute1.equals(pairToIncremenet.getAttribute1()) &&
              !tempMpd.attribute2.equals(pairToIncremenet.getAttribute2())     ) ||
            ( !tempMpd.attribute1.equals(pairToIncremenet.getAttribute1()) &&
              tempMpd.attribute2.equals(pairToIncremenet.getAttribute2())      )
          ) {
         for (int j=0; j<updateVector.size() ; j++){
           tempMpd2 = (MatchedPairData) updateVector.get(j);
           if (tempMpd.attribute1.equals(tempMpd2.attribute1) &&
               tempMpd.attribute2.equals(tempMpd2.attribute2)    ){
             updateVector.remove(j);
             newMpd = new MatchedPairData(pairToIncremenet);
             newMpd.setCountNumberAppeared(newCount);
             updateVector.add(newMpd);
             return;
           }
         }
       }
    }

    for (int i = 0 ; i<incorrectMatchings.size(); i++) {
        tempMpd = (MatchedPairData) incorrectMatchings.get(i);
        if (tempMpd.getCountNumberAppeared() < newCount  &&
            ( tempMpd.attribute1.equals(pairToIncremenet.getAttribute1()) &&
              !tempMpd.attribute2.equals(pairToIncremenet.getAttribute2())     ) ||
            ( !tempMpd.attribute1.equals(pairToIncremenet.getAttribute1()) &&
              tempMpd.attribute2.equals(pairToIncremenet.getAttribute2())      )
          ) {
          for (int j=0; j<updateVector.size() ; j++){
            tempMpd2 = (MatchedPairData) updateVector.get(j);
            if (tempMpd.attribute1.equals(tempMpd2.attribute1) &&
                tempMpd.attribute2.equals(tempMpd2.attribute2)    ){
              updateVector.remove(j);
              newMpd = new MatchedPairData(pairToIncremenet);
              newMpd.setCountNumberAppeared(newCount);
              updateVector.add(newMpd);
              return;
            }
          }
        }
     }

  }

/* used for test5.
  When adding a new match to the work vector, the update vector needs to be updated accordingly.
 The method checks if a match which contradicts 1:1 matchings exists in the work vectors, and adds
 the match to the update vector only if none are found.
  */
  private static void addToSaveVector(Vector updateVector,Vector correctMatchings,Vector incorrectMatchings,MatchedAttributePair pairToAdd){
    MatchedPairData tempMpd;
    boolean exists = false;
    for (int i = 0 ; i<correctMatchings.size(); i++) {
      tempMpd = (MatchedPairData) correctMatchings.get(i);
      if ( ( tempMpd.attribute1.equals(pairToAdd.getAttribute1()) &&
             !tempMpd.attribute2.equals(pairToAdd.getAttribute2())   ) ||
           ( !tempMpd.attribute1.equals(pairToAdd.getAttribute1()) &&
             tempMpd.attribute2.equals(pairToAdd.getAttribute2())   )
         )
        exists = true;
    }

    for (int i = 0 ; i<incorrectMatchings.size(); i++) {
      tempMpd = (MatchedPairData) incorrectMatchings.get(i);
      if ( ( tempMpd.attribute1.equals(pairToAdd.getAttribute1()) &&
             !tempMpd.attribute2.equals(pairToAdd.getAttribute2())   ) ||
           ( !tempMpd.attribute1.equals(pairToAdd.getAttribute1()) &&
             tempMpd.attribute2.equals(pairToAdd.getAttribute2())   )
         )
        exists = true;
    }

    if (!exists)
      updateVector.add(new MatchedPairData(pairToAdd));

  }

  /* This function allows for dynamic addition of xml files.
  it recieves as a parameter the directory name where all the exact xml files will be.
It assumes then that candidate files are in a subdirectory under the given root directory
name 'candidates' and that the targets are under 'targets'.
It reads the xml files one by one to discern the filenames of the candidates and targets
and updates the fields exactFileName, targetFileName and candidateFileName accordingly.
It DOES NOT check to see if the candidate and target files actually exist under the given names,
since this check will be done anyhow once the test is run.
    */
  private static void loadDir(String dirName) throws Exception{
    File exactsDir = new File(dirName);
   String[] exacts = exactsDir.list();

    exactFileName = new String[exacts.length - 2];
    targetFileName = new String[exacts.length - 2];
    candidateFileName = new String[exacts.length - 2];
    BufferedReader exact;
    String temp;
    String[] sections;
    int counter = 0 ;
    for (int i = 0; i < exacts.length ; i++) {
      if (!exacts[i].endsWith(".xml"))
        continue;
       exact = new BufferedReader(new FileReader(dirName.concat("\\" +
        exacts[i])));
      temp = exact.readLine();
      while (true) {
        if (temp != null && (temp.indexOf("CandidateOntology") != -1))
          break;
        temp = exact.readLine();
      }
      sections = temp.substring(temp.indexOf("CandidateOntology")).split("\"");
      exactFileName[counter] = dirName + "/" + exacts[i];
      candidateFileName[counter] = dirName + "/candidates/" + sections[1];
      targetFileName[counter++] = dirName + "/targets/" + sections[7];

    }
    if (counter < exacts.length){
      exactFileName = copyStringArray(exactFileName,counter);
      candidateFileName = copyStringArray(candidateFileName,counter);
      targetFileName = copyStringArray(targetFileName,counter);
    }
    print("Loaded " + counter + " files from " + dirName);

  }

  private static String[] copyStringArray(String[] in,int length){
    String[] result = new String[length];
    for (int i = 0 ; i < length ; i++)
      result[i] = in[i];
    return result;

  }

  private static int fromFile(int num_of_args,String[] args) throws Exception {
    int from = 0;
    if (num_of_args > 1)
          from = Integer.parseInt(args[1]);
    return from ;
  }

  private static int toFile(int num_of_args,String[] args) throws Exception {
    int to = exactFileName.length-1;
    if (num_of_args > 1) {
      if (num_of_args == 2)
        to = Integer.parseInt(args[1]);
      else
        to = Integer.parseInt(args[2]);
    }
    return to;
  }

  /**
   * Prints a 'threshold print' similar to TestResult4.printToFileThreshold() ,only it mimics the entire
   * method and its calls, and it basically averages the results of 10 different runs. It does so by running
   * on all Ks, and for each - over all thresholds, and for each value, it runs over all 10 inputs, and averages
   * the correct unchanged attribute mappings, the incorrect unchanged, the precision, and the recall
   * It calculates its own precision change (change relative to the first precision in the K), and recall
   * change (similar). The results are written to the file with the specified filename
   * @param inputs TestResult[] array of 10 TestResult4 objects, generated by 10 runs of 'tests' (assumably test5)
   * @param fileName String Name of output file
   * @param printOnlyLast boolean Should it print only K=10 or all Ks from K=1 till 10
   * @throws Exception If something goes wrong
   */
  private static void printCombinedResult(TestResult[] inputs,String fileName,boolean printOnlyLast) throws Exception{
    FileWriter file;
    file = new FileWriter(fileName + ".xls");
    file.write("Candidate File\tTarget File\tTotal Exacts\n");
    Vector termKey  = (Vector)inputs[0].termResults.keys().nextElement();
    String candName = (String)termKey.get(0);
    String targName = (String)termKey.get(1);
    Test1FileResult currentFileRes ;
    LinkedList attributeCount ;
    int totalExact =((Test1FileResult)inputs[0].termResults.elements().nextElement()).getExactCount();
    file.write(candName+"\t"+targName+"\t"+totalExact+"\n");
    TestResult4 firstTestResult = (TestResult4)inputs[0];
    attributeCount = (LinkedList)firstTestResult.totalAtts.get(termKey);
    file.write(((Integer)attributeCount.get(0)).toString() + "\t" +
                   ((Integer)attributeCount.get(1)).toString() + "\n" );
    kResult tempKRes ;
    for (int algorithm = 0; algorithm < algorithmIds.length; algorithm++) {
      if (algorithm == TestResult.TERM)
        file.write("Term Results:\n" );
      else
        file.write("Combined Results:\n" );
      file.write("\tK Value\tThreshold\tCorrect\tIncorrect\tPrecision\tRecall\tPrecision Change\tRecall Change\n");
      int firstK = 1;
      if (printOnlyLast)
        firstK = 10;
      for (int k = firstK ; k <= 10 ; k++){
        file.write("\t"+k+"\n");
        float precisionChange = 1, recallChange = 1, firstPrecision = 1,
          firstRecall = 1;
        ;
        for (int threshold = 1; threshold <= k; threshold++) {
          int correct = 0, incorrect = 0;
          float precision = 0, recall = 0;
          for (int inputCounter = 0; inputCounter < inputs.length; inputCounter++) {
            if (algorithm == TestResult.TERM)
              // since we are running (assumably) only on one file, the Test1FileResult is obtained
              // from the first element in the vector
              currentFileRes = (Test1FileResult) inputs[inputCounter].termResults.elements().nextElement();
            else
              currentFileRes = (Test1FileResult) inputs[inputCounter].combinedResults.elements().nextElement();
            tempKRes = (kResult) currentFileRes.k_CorrectResults.get(k-1);
            correct += tempKRes.totalCorrectMatchesAboveIntThreshold(threshold);
            incorrect +=
              tempKRes.totalIncorrectMatchesAboveIntThreshold(threshold);
            precision +=
              (float) tempKRes.totalCorrectMatchesAboveIntThreshold(threshold) /
              ( (float) tempKRes.totalCorrectMatchesAboveIntThreshold(threshold) +
               (float) tempKRes.totalIncorrectMatchesAboveIntThreshold(
              threshold));
            recall +=
              (float) tempKRes.totalCorrectMatchesAboveIntThreshold(threshold) /
              (float) currentFileRes.getExactCount();
          }
          correct /= 10;
          incorrect /= 10;
          precision /= 10;
          recall /= 10;

          if (threshold == 1) {
            firstRecall = recall;
            firstPrecision = precision;
          }

          if (precision == 0 && (firstPrecision == 0 || threshold == 1))
            precisionChange = 1;
          else
            precisionChange = precision / firstPrecision;
          if (recall == 0 && (firstRecall == 0 || threshold == 1))
            recallChange = 1;
          else
            recallChange = recall / firstRecall;

          file.write("\t\t" + threshold + "\t" + correct + "\t" + incorrect +
                     "\t" + precision + "\t" + recall + "\t" +
                     precisionChange + "\t" + recallChange + "\n");
        }
      }
    }
    file.close();
  }

  /**
   * Creates a single TestResult object which aggregates the results of an array of result objects
   * The result object will only have 1 file and for that file only result for k=10 (both for term
   * and combined).
   * The method will average out the values of the kResults of the first file in each of the inputs,
   * using only k=10 .
   * @param inputs TestResult[] array of TestResults objects to be averaged out
   * @return TestResult an averaged object
   * /
  private static TestResult combineTests(TestResult[] inputs){
    // currently it uses test4results as output.
    TestResult4 testResult = new TestResult4();

    // will hold the kResult objects to be added to the result object
    Vector kresultsVec = new Vector();

    // will hold the correct and incorrect mappings for a specific K
    Vector correctAttributes=new Vector(), incorrectAttributes = new Vector();
    Test1FileResult currentFileRes ;
    Vector termKey  = (Vector)inputs[0].termResults.keys().nextElement();
    String candName = (String)termKey.get(0);
    String targName = (String)termKey.get(1);
    int totalExact =0;
    for (int algorithm = 0; algorithm < algorithmIds.length; algorithm++) {
      for (int i = 0 ; i < inputs.length ; i++){
        if (algorithm == TestResult.TERM)
          currentFileRes = (Test1FileResult)inputs[i].termResults.elements().nextElement();
        else
          currentFileRes = (Test1FileResult)inputs[i].combinedResults.elements().nextElement();
        totalExact = currentFileRes.getExactCount();
    // add the results of the current file

   }
      kresultsVec.add(new kResult(10,correctAttributes,incorrectAttributes));
  testResult.addFileResult(TestResult1.convertType(algorithmIds[algorithm]),
                         candName,
                         targName,
                         totalExact,
                         kresultsVec,
                         match.getMatrix().getCandidateAttributeNames().length,
                         match.getMatrix().getTargetAttributeNames().length,true);

  }
    return testResult;
  }*/
}

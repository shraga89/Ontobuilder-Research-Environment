package ac.technion.schemamatchings.test;

import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

public class TestResult4 extends TestResult{
  protected Hashtable totalAtts = new Hashtable();
// this function isnt really used
  public void addFileResult(int type, String candidate, String target,
                            int totalCorrect, int totalKs,
                            Vector correctAttributeMappings,
                            Vector incorrectAttributeMappings ) {

    Vector key = new Vector(2);
    key.add(candidate);
    key.add(target);

    if (type == TERM) {
      if (termResults.containsKey(key))
        termResults.remove(key);
      termResults.put(key,
        new TestFileResult2(correctAttributeMappings,incorrectAttributeMappings,
                            totalKs,totalCorrect));
    }
    if (type == COMBINED) {
      if (combinedResults.containsKey(key))
        combinedResults.remove(key);
      combinedResults.put(key,
        new TestFileResult2(correctAttributeMappings,incorrectAttributeMappings,
                            totalKs,totalCorrect));
    }
  }

  /**
   * add another file (term or combined match) to the total run result
   * @param type int algorithm type, either TERM or COMBINED
   * @param candidate String name of candidate file
   * @param target String name of target file
   * @param totalCorrect int total number of exact matches
   * @param correctMatchesFound Vector vector of kResults, each containing results for a different K run
   * @param candidateAttsCount int number of candidate terms
   * @param targetAttsCount int number of target terms
   * @param printChanges boolean when this file will print - should it also include information about how
   * much the precision and recall changed for each threshold level, compared to the recall and precision of
   * the threshold 1
   */
  public void addFileResult(int type, String candidate, String target,
                            int totalCorrect, Vector correctMatchesFound,
                            int candidateAttsCount,
                            int targetAttsCount,boolean printChanges      ) {
    LinkedList termCounts = new LinkedList();
    termCounts.add(0,new Integer(candidateAttsCount) );
    termCounts.add(1,new Integer(targetAttsCount ));

    Vector key = new Vector(2);
    key.add(candidate);
    key.add(target);
    if (!totalAtts.containsKey(termCounts))
      totalAtts.put(key,termCounts);

    if (type == TERM) {
      if (termResults.containsKey(key))
        termResults.remove(key);
      termResults.put(key,
                      new Test1FileResult(totalCorrect, correctMatchesFound,printChanges));
    }
    if (type == COMBINED) {
      if (combinedResults.containsKey(key))
        combinedResults.remove(key);
      combinedResults.put(key,
                          new Test1FileResult(totalCorrect, correctMatchesFound,printChanges));
    }
  }

  public void addFileResult(int type, String candidate, String target,
                            int totalCorrect,
                            Vector firstTerms,
                            LinkedList changes,
                            int candidateAttsCount,
                            int targetAttsCount) {
    LinkedList termCounts = new LinkedList();
    termCounts.add(0,new Integer(candidateAttsCount) );
    termCounts.add(1,new Integer(targetAttsCount ));

    Vector key = new Vector(2);
    key.add(candidate);
    key.add(target);
    if (!totalAtts.containsKey(termCounts))
      totalAtts.put(key,termCounts);

    if (type == TERM) {
      if (termResults.containsKey(key))
        termResults.remove(key);
      termResults.put(key,
                      new Test1FileResult(totalCorrect, firstTerms,changes));
    }
    if (type == COMBINED) {
      if (combinedResults.containsKey(key))
        combinedResults.remove(key);
      combinedResults.put(key,
                          new Test1FileResult(totalCorrect, firstTerms,changes));
    }
  }


  public String printResult() {
    return "" ;
   }
   public void printToFile(String fileName) throws java.io.IOException {
     printToFile(fileName, false);
   }

// but this one is
  public void printToFile(String fileName,boolean fullOutput) throws java.io.IOException {
    FileWriter file;
    file = new FileWriter(fileName + ".xls");
    Enumeration termKeys, termValues, combinedKeys, combinedValues;
    if (!fullOutput)
      file.write("Candidate File\tTarget File\tK Value\tUnchanged Attributes\tCorrect Unchanged\tIncorrect Unchanged\n");
    else
      file.write("Candidate File\tTarget File\n");

    termKeys = termResults.keys();
    termValues = termResults.elements();
    combinedKeys = combinedResults.keys();
    combinedValues = combinedResults.elements();
    Vector termKey,combinedKey;
    LinkedList attributeCount;
    Test1FileResult termFileResult, combinedFileResult;

    while (termKeys.hasMoreElements()) {
      termKey = (Vector)termKeys.nextElement();
      combinedKey = (Vector)combinedKeys.nextElement();
      termFileResult = (Test1FileResult) termValues.nextElement();
      combinedFileResult = (Test1FileResult) combinedValues.nextElement();
      attributeCount = (LinkedList)totalAtts.get(termKey);

      if (((String)termKey.get(0)).equals(combinedKey.get(0)) &&
          ((String)termKey.get(1)).equals(combinedKey.get(1))    ) {
        file.write((String)termKey.get(0) + "\t" + (String)termKey.get(1)+"\t\t\t\t\t"+
                   termFileResult.getExactCount() + "\n");
        file.write(((Integer)attributeCount.get(0)).toString() + "\t" +
                   ((Integer)attributeCount.get(1)).toString() + "\n" );
        if (fullOutput) {
          file.write("Term Results:\n" + termFileResult.printForFile4_2());
          file.write("Combined Results:\n" + combinedFileResult.printForFile4_2() + "\n");
        }
        else {
          file.write("Term Results:\n"+termFileResult.printForFile4());
          file.write("Combined Results:\n"+combinedFileResult.printForFile4()+"\n");
        }
      }
    }
    file.close();
  }

  public void printToFileThreshold(String fileName) throws java.io.IOException {
    printToFileThreshold(fileName,false);
  }
    public void printToFileThreshold(String fileName,boolean printOnlyLast) throws java.io.IOException {
    printToFileThreshold(fileName,false,0,false,printOnlyLast);
  }

  public void printToFileThreshold(String fileName,boolean printOnlyGroup,int group,boolean isTerm) throws java.io.IOException {
    printToFileThreshold(fileName,printOnlyGroup,group,isTerm,false);
  }
    public void printToFileThreshold(String fileName,boolean printOnlyGroup,int group,boolean isTerm,boolean printOnlyLast) throws java.io.IOException {
      FileWriter file;
  file = new FileWriter(fileName + ".xls");
  Enumeration termKeys, termValues, combinedKeys, combinedValues;
  file.write("Candidate File\tTarget File\tTotal Exacts\n");

  termKeys = termResults.keys();
  termValues = termResults.elements();
  combinedKeys = combinedResults.keys();
  combinedValues = combinedResults.elements();
  Vector termKey,combinedKey;
  LinkedList attributeCount;
  Test1FileResult termFileResult, combinedFileResult;
  boolean printTerm,printCombined;
  while (termKeys.hasMoreElements()) {
    printTerm=true;
    printCombined=true;
    termKey = (Vector)termKeys.nextElement();
    combinedKey = (Vector)combinedKeys.nextElement();
    termFileResult = (Test1FileResult) termValues.nextElement();
    combinedFileResult = (Test1FileResult) combinedValues.nextElement();
    attributeCount = (LinkedList)totalAtts.get(termKey);
    if (printOnlyGroup){
      if (isTerm){
        printCombined=false;
        if (!shouldPrint(group,termKey,isTerm))
          printTerm=false;
      }
      else{
        printTerm=false;
        if (!shouldPrint(group,termKey,isTerm))
          printCombined=false;
      }
    }
    if (printTerm || printCombined)
      if (((String)termKey.get(0)).equals(combinedKey.get(0)) &&
          ((String)termKey.get(1)).equals(combinedKey.get(1))    ) {
        file.write(cleanFileName((String)termKey.get(0))+ "\t" +
                   cleanFileName((String)termKey.get(1)) +"\t"+
                   termFileResult.getExactCount() +"\t"+
                  ((float)termFileResult.getExactCount()/(float)((Integer)attributeCount.get(0)).intValue()) + "\n");
     //   file.write("Candidate Terms\tTarget Terms\tRatio\n");
       file.write(((Integer)attributeCount.get(0)).toString() + "\t" +
                   ((Integer)attributeCount.get(1)).toString() );
        if(printOnlyGroup)
          file.write("\t"+getRatio(group,termKey,isTerm));
  //      printRatioStats(group,termKey,isTerm,file);
        file.write("\n");
        if (printTerm)
          file.write("Term Results:\n" + termFileResult.printForFile_ThresholdAnalysis(printOnlyLast));
        if (printCombined)
          file.write("Combined Results:\n" + combinedFileResult.printForFile_ThresholdAnalysis(printOnlyLast) + "\n");
      }
  }
  file.close();
}


  public void printToFileChanges(String fileName) throws java.io.IOException {
    FileWriter file;
    file = new FileWriter(fileName + ".xls");
    Enumeration termKeys, termValues, combinedKeys, combinedValues;
    file.write("Candidate File\tTarget File\tTotal Exacts\n");

    termKeys = termResults.keys();
    termValues = termResults.elements();
    combinedKeys = combinedResults.keys();
    combinedValues = combinedResults.elements();
    Vector termKey, combinedKey;
    LinkedList attributeCount;
    Test1FileResult termFileResult, combinedFileResult;

    while (termKeys.hasMoreElements()) {
      termKey = (Vector) termKeys.nextElement();
      combinedKey = (Vector) combinedKeys.nextElement();
      termFileResult = (Test1FileResult) termValues.nextElement();
      combinedFileResult = (Test1FileResult) combinedValues.nextElement();
      attributeCount = (LinkedList) totalAtts.get(termKey);

      if ( ( (String) termKey.get(0)).equals( combinedKey.get(0)) &&
          ( (String) termKey.get(1)).equals( combinedKey.get(1))) {
        file.write(cleanFileName( (String) termKey.get(0)) + "\t" +
                   cleanFileName( (String) termKey.get(1)) + "\t" +
                   termFileResult.getExactCount() + "\n");
        file.write( ( (Integer) attributeCount.get(0)).toString() + "\t" +
                   ( (Integer) attributeCount.get(1)).toString() + "\n");
        file.write("Term Results:\n" + termFileResult.printForFile_Changes());
        file.write("Combined Results:\n" +
                   combinedFileResult.printForFile_Changes() + "\n");
      }
    }
    file.close();
  }


  /**
   * Discerns if the file should be printed
   * @param group int the group we're printing now:
   *    0: 00%-25%
   *    1: 26%-50%
   *    2: 51%-75%
   *    3: 76%-100%
   * @param termKey Vector Vector containing the key of the file - two values are in the vector,
   * both strings. first is the candidate name, second- target name
   * @param term boolean is this fileresult the term or combined match
   * @return boolean does this file fit the specified group
   */
  private boolean shouldPrint(int group,Vector termKey,boolean term){
    float ratio = getRatio(group, termKey, term);
    switch (group) {
      case (0):
        if (ratio >= 0 && ratio <= 0.25)
          return true;
        break;
      case (1):
        if (ratio > 0.25 && ratio <= 0.50)
          return true;
        break;
      case (2):
        if (ratio > 0.50 && ratio <= 0.75)
          return true;
        break;
      case (3):
        if (ratio > 0.75 && ratio <= 1)
          return true;
        break;
    }
    return false;

  }

  private float getRatio(int group, Vector termKey, boolean term) {
    Test1FileResult fileRes;
    if (term)
      fileRes = (Test1FileResult) termResults.get(termKey);
    else
      fileRes = (Test1FileResult) combinedResults.get(termKey);
    LinkedList attributeCount = (LinkedList) totalAtts.get(termKey);
    float ratio = (float)fileRes.getFirstTotalCorrect() /
      (float)( (Integer) attributeCount.get(0)).intValue();
    return ratio;
  }

  private void printRatioStats(int group, Vector termKey, boolean term,FileWriter file) throws java.io.IOException{
    Test1FileResult fileRes;
    if (term)
      fileRes = (Test1FileResult) termResults.get(termKey);
    else
      fileRes = (Test1FileResult) combinedResults.get(termKey);
    LinkedList attributeCount = (LinkedList) totalAtts.get(termKey);
    file.write( "\t"+fileRes.getFirstTotalCorrect() +"\t"+ ((Integer) attributeCount.get(0)).intValue());
  }
  public Hashtable getTotalAtts() {
    return totalAtts;
  }
}

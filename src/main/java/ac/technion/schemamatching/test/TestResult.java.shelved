package ac.technion.schemamatchings.test;

import java.util.Hashtable;
import java.util.Vector;

import schemamatchings.ontobuilder.MatchingAlgorithms;

public abstract class TestResult {

  public abstract String printResult() ;
  public abstract void printToFile(String fileName)throws java.io.IOException;


  protected static final int TERM = 0;
  protected static final int COMBINED = 1;

  protected Hashtable termResults;
  protected Hashtable combinedResults;

  public TestResult() {
    termResults = new Hashtable();
    combinedResults = new Hashtable();
  }

  public void addFileResult(int type, String candidate, String target,
                            int totalCorrect, Vector correctMatchesFound) {
    Vector key = new Vector(2);
    key.add(candidate);
    key.add(target);
    if (type == TERM) {
      if (termResults.containsKey(key))
        termResults.remove(key);
      termResults.put(key,
                      new Test1FileResult(totalCorrect, correctMatchesFound));
    }
    if (type == COMBINED) {
      if (combinedResults.containsKey(key))
        combinedResults.remove(key);
      combinedResults.put(key,
                          new Test1FileResult(totalCorrect, correctMatchesFound));
    }
  }


  public static int convertType(String s) {
    if (s.equals(MatchingAlgorithms.TERM))
      return TERM;
    if (s.equals(MatchingAlgorithms.TERM_VALUE_COMBINED))
      return COMBINED;
    return -1;
  }

  protected String cleanFileName(String input){
    String result = input;
    try{
      String[] inputPathElements = input.split("/");
      result = inputPathElements[inputPathElements.length-1];
    }catch (IllegalArgumentException e){}
    catch (ArrayIndexOutOfBoundsException e){}
    try{
      result = result.substring(0,result.lastIndexOf("."));
    }catch (IllegalArgumentException e){}
    catch (ArrayIndexOutOfBoundsException e){}
    return result;

    }




}

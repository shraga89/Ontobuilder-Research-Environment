package schemamatchings.test;

import java.io.FileWriter;
import java.util.Enumeration;
import java.util.Vector;

public class TestResult2 extends TestResult{

  public String printResult() {
    Enumeration keys, values;
    String result = new String();
    result = result.concat("Term Results:\n");
    keys = termResults.keys();
    Vector key;
    values = termResults.elements();
    System.out.println("TestResult1: got " + termResults.size() + " items");
    while (keys.hasMoreElements()) {
      key = (Vector) keys.nextElement();
      result = result.concat( (String) key.get(0) + "\t" + (String) key.get(1) +
                             ":\n" +
                             ( (Test1FileResult) values.nextElement()).print() +
                             "\n");
    }
    keys = combinedResults.keys();
    values = combinedResults.elements();
    result = result.concat("Combined Results:\n");
    while (keys.hasMoreElements()) {
      key = (Vector) keys.nextElement();
      result = result.concat( (String) key.get(0) + "\t" + (String) key.get(1) +
                             ":\n" +
                             ( (Test1FileResult) values.nextElement()).print() +
                             "\n");
    }
    return result;
  }

  public void printToFile(String fileName) throws java.io.IOException {
    FileWriter file;
    file = new FileWriter(fileName + "0.txt");
    Enumeration termKeys, termValues, combinedKeys, combinedValues;
    file.write("Candidate File\tTarget File\n");
    termKeys = termResults.keys();
    termValues = termResults.elements();
    combinedKeys = combinedResults.keys();
    combinedValues = combinedResults.elements();
    Vector termKey,combinedKey;
    // System.out.println("TestResult1: got " + termResults.size() + " items");
    while (termKeys.hasMoreElements()) {
      termKey = (Vector)termKeys.nextElement();
      combinedKey = (Vector)combinedKeys.nextElement();
      if (((String)termKey.get(0)).equals(combinedKey.get(0)) &&
          ((String)termKey.get(1)).equals(combinedKey.get(1))    ) {
        file.write((String)termKey.get(0) + "\t" + (String)termKey.get(1) + "\n");
        file.write("Term Results:\n"+((Test1FileResult)termValues.nextElement()).printForFile2());
        file.write("Combined Results:\n"+((Test1FileResult)combinedValues.nextElement()).printForFile2()+"\n");
      }
    }
    file.close();
  }
}

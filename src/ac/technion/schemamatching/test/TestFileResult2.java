package ac.technion.schemamatching.test;

import java.util.Vector;

import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;

public class TestFileResult2 {
  private Vector<MatchedAttributePair> correctAttributeMappings,incorrectAttributeMappings;
  private int totalKs,totalCorrectMappings ;

  public TestFileResult2(Vector<MatchedAttributePair> correctAttributeMappings,Vector<MatchedAttributePair> incorrectAttributeMappings,
                         int totalKs,int totalCorrectMappings) {
    this.correctAttributeMappings = correctAttributeMappings ;
    this.incorrectAttributeMappings = incorrectAttributeMappings ;
    this.totalKs = totalKs ;
    this.totalCorrectMappings = totalCorrectMappings;
  }


  public String printForFile() {
    int totalCorrect = correctAttributeMappings.size();
    int totalIncorrect = incorrectAttributeMappings.size();
    return new String("\t\t"+totalCorrectMappings+"\t"+totalKs+"\t"+
                      (totalCorrect+totalIncorrect) + "\t" +
                      totalCorrect + "\t"+ totalIncorrect + "\n");
  }

  /*  Returns a String used for the file output, when this class is used for experiment 2/3
      * /
  public String printForFile2() {
//    String result = new String("\tK Value\tPercentage True\tPercentage Of Total Correct\tWeight Of True Matches\n");
    String result = new String("\tK Value\tPercent Found Correct\tDetail\tWeight Of True Matches\tPercentage Of Total Correct\n");
   for (int i = 0 ; i < this.k_CorrectResults.size() ; i++)
  result = result.concat("\t"+(i+1)+"\t" +floor2(((kResult)(k_CorrectResults.get(i))).getTotalCorrectFloat()/(float)totalExactMatches*100.0) +
                         "\t(" + ((kResult)(k_CorrectResults.get(i))).getTotalCorrect() + "/" + totalExactMatches +
                         ")\n"+((kResult)this.k_CorrectResults.get(i)).printList()  );

   return result;
  }

*/
  public static double floor2(double f){
    double g = f % 0.01 ;
    if (g<0.005)
      return f-g ;
    else
      return f-g+0.01;
  }
}

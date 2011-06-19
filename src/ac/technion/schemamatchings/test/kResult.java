package ac.technion.schemamatchings.test;

/*
This is a generic object used to hold data pertaining to a specific K-best mapping.
different data objects might be used in different experiments.

The object is formed and updated in OntoBuilderTest, and then stored in an object (usually in a vector)
that is passed to a TestFileResult object.
When printing the TestFileResult, this object's print function will be called for each k.
*/
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import com.modica.ontology.match.MatchInformation;

public class kResult{
  private int k;
  private int totalCorrect;
  private LinkedList weightList;

  // used for topK plugin - will hold a single matchInfo
  private MatchInformation matchInfo ;


  public static final int PRINT_ALL = 0;
  public static final int PRINT_CORRECT = 1;
  public static final int PRINT_INCORRECT = 2;


  // for test4 - holds all the matches seen by this K and/or previous ones,
//     and for each one - how many times it appeared in this K and previous.
  private  Vector correctAttributeMappings,incorrectAttributeMappings;

  public kResult() {
  }


  public kResult(int k,int totalCorrect){
    this.k = k ;
    this.totalCorrect = totalCorrect ;
  }

  public kResult(int k, Vector correctMappings,Vector incorrectMappings){
    this.k = k;
    this.correctAttributeMappings = correctMappings ;
    this.incorrectAttributeMappings = incorrectMappings;
  //  System.out.println("kResult() : i am " + this + " and corr is " + correctMappings);
  }

  public int getK() {
    return k;
  }
  public void setK(int k) {
    this.k = k;
  }
  public int getTotalCorrect() {
    return totalCorrect;
  }

 /* public Vector getCorrectAttributeMappings(){
    return correctAttributeMappings;
  }*/

  public void setTotalCorrect(int totalCorrect) {
    this.totalCorrect = totalCorrect;
  }

  public float getTotalCorrectFloat() {
    return (new Integer(totalCorrect)).floatValue();
  }


  public String toString1() {
    return new String(Integer.toString(k) + "\t" +
                      Integer.toString(totalCorrect));
  }
  public LinkedList getWeightSet() {
    return weightList;
  }
  public void setWeightList(LinkedList weightList) {
    this.weightList = weightList;
  }

// used for experiments 2/3
  public String printList() {
    String result= new String("\t\t\t\t0.0\t100\n") ;
    float totalAssurity = 100 ;
    Iterator setIterator = weightList.iterator();
    while (setIterator.hasNext()){
      totalAssurity -= 100.0/weightList.size() ;
      result = result.concat("\t\t\t\t"+ setIterator.next() + "\t" + Test1FileResult.floor2(totalAssurity) +"\n")    ;
    }
    return result;
  }

// used for experiment 4
  public String printList4() {
 //   System.out.println("kResult: print4: i am " + this + ". and correct is " + correctAttributeMappings);
 //   System.out.println("kResult: print4: incorrect is " + incorrectAttributeMappings);
    String result= new String("\t\t"+ k + "\t" +
                               totalUnchanged() + "\t" +
                                totalCorrectUnchanged()+ "\t" +
                               totalInCorrectUnchanged() + "\n")    ;

    return result;
  }

  public String printList4_2() {
     MatchedPairData mpd ;
     String result= new String("")    ;
     if ((OntoBuilderTest.EXP_4_PRINT_TYPE == PRINT_ALL) || (OntoBuilderTest.EXP_4_PRINT_TYPE == PRINT_CORRECT))
       for (int i = 0; i < correctAttributeMappings.size(); i++) {
         mpd = (MatchedPairData) correctAttributeMappings.get(i);
         result = result.concat("\t\t1\t" + mpd.getAttribute1() + "\t" +
                                mpd.getAttribute2() + "\t" +
                                mpd.getCountNumberAppeared()
                                + "\t" +
                                (100 * (float) mpd.getCountNumberAppeared() /
                                 k) + "\n");
       }
     if ((OntoBuilderTest.EXP_4_PRINT_TYPE == PRINT_ALL) || (OntoBuilderTest.EXP_4_PRINT_TYPE == PRINT_INCORRECT))
       for (int i = 0; i < incorrectAttributeMappings.size(); i++) {
         mpd = (MatchedPairData) incorrectAttributeMappings.get(i);
         result = result.concat("\t\t0\t" + mpd.getAttribute1() + "\t" +
                                mpd.getAttribute2() + "\t" +
                                mpd.getCountNumberAppeared()
                                + "\t" +
                                (100 * (float) mpd.getCountNumberAppeared() /
                                 k) + "\n");
       }


     return result;
   }

  private int totalCorrectUnchanged() {
    int result = 0 ;
    for (int i = 0 ; i < correctAttributeMappings.size() ; i++)
      if (((MatchedPairData)correctAttributeMappings.get(i)).getCountNumberAppeared() == k)
        result++ ;
    return result;
  }


  private int totalInCorrectUnchanged() {
    int result = 0 ;
    for (int i = 0 ; i < incorrectAttributeMappings.size() ; i++)
      if (((MatchedPairData)incorrectAttributeMappings.get(i)).getCountNumberAppeared() == k)
        result++ ;
    return result;
  }


  private int totalUnchanged() {
    return totalCorrectUnchanged()+totalInCorrectUnchanged() ;
  }


  /* used by OntoBuilderTest to clone a vector containing MPDs  */
  public static Vector cloneMatchedPairData(Vector input) {
    Vector output = new Vector() ;
    Iterator it = input.iterator();
    while (it.hasNext())
      output.add(((MatchedPairData)it.next()).createClone());
    return output;
  }

/*     Used for topKAlgorithm - returns an array of all the matches above a certain
    threshold */
  public Vector getMatchesAboveThreshold(double threshold) {
   MatchedPairData mpd;
   Vector result  = cloneMatchedPairData(correctAttributeMappings);

   return result;
 }
  public MatchInformation getMatchInfo() {
    return matchInfo;
  }
  public void setMatchInfo(MatchInformation matchInfo) {
    this.matchInfo = matchInfo;
  }

/* returns the number of correct matches which appeared at least threshold times in this k */
  public int totalCorrectMatchesAboveIntThreshold(int threshold){
    MatchedPairData mpd;
    int result = 0;
    for (int i = 0; i < correctAttributeMappings.size(); i++) {
     mpd = (MatchedPairData) correctAttributeMappings.get(i);
     if ( (mpd.getCountNumberAppeared() >= threshold)
          /*&& !hasCandidateAppearedWithThreshold(mpd,threshold) */ )
       result++;
    }
    return result;
  }


  /* returns the number of incorrect matches which appeared at least threshold times in this k */
    protected int totalIncorrectMatchesAboveIntThreshold(int threshold){
      MatchedPairData mpd;
      int result = 0;
      for (int i = 0; i < incorrectAttributeMappings.size(); i++) {
       mpd = (MatchedPairData) incorrectAttributeMappings.get(i);
       if ( mpd.getCountNumberAppeared() >= threshold)
         result++;
      }
      return result;
    }

/* useless function
    private boolean hasCandidateAppearedWithThreshold(MatchedPairData mpd,int threshold){
      MatchedPairData tempMpd;
      for (int i = 0 ; i<correctAttributeMappings.size(); i++) {
        tempMpd = (MatchedPairData) correctAttributeMappings.get(i);
        if (tempMpd.getCountNumberAppeared() >= threshold  &&
            tempMpd.attribute1.equals(mpd.attribute1) &&
            !tempMpd.attribute2.equals(mpd.attribute2)        )
          return true;
      }
      return false;
    }*/



/**
 * used for threshold analysis experiments,
   calculates for each k, a table with each applicable threshold - the precision
 and recall rate for that threshold.
 * @param exactMatches int number of exact matches found
 * @param printChanges boolean should the function print for each threshold the change in precent
 * of the precision and recall compared to the previous threshold
 * @param printOnlyLast boolean should the method print just the last k (default k=10), when false (default)-
 * the method will print the precision and recall for each threshold for every k, starting at 1 till default max K (10)
 * @return String printout string which will be concatonated to the output file.
 */
public String printThresholdAnalysis(int exactMatches,boolean printChanges){
    StringBuffer result = new StringBuffer();
    float precision,recall,previousPrecision=1,previousRecall=1;
    float precisionChange = 1, recallChange = 1;
     for (int i = 1 ; i <= k; i++){
      precision = totalCorrectMatchesAboveIntThreshold(i)/
        ((float)totalCorrectMatchesAboveIntThreshold(i)+
         (float)totalIncorrectMatchesAboveIntThreshold(i));
      recall = (float)totalCorrectMatchesAboveIntThreshold(i)/(float)exactMatches;
      if (i == 1){
        previousRecall = recall ;
        previousPrecision = precision;
      }
      result.append("\t\t"+i+"\t"+totalCorrectMatchesAboveIntThreshold(i)+"\t"+
                           totalIncorrectMatchesAboveIntThreshold(i)
                           + "\t"+precision+"\t"+recall);
      if (printChanges){
        if (precision == 0 && (previousPrecision == 0 || i==1 ))
          precisionChange = 1;
        else
          precisionChange = precision/previousPrecision ;
        if(recall == 0  && (previousRecall == 0 || i==1 ))
          recallChange = 1;
        else
          recallChange = recall / previousRecall ;
    //    previousRecall = recall ;
      //  previousPrecision = precision;
        result.append("\t" + precisionChange + "\t" + recallChange);
      }
      result.append("\n");
    }
    return result.toString();
  }
}

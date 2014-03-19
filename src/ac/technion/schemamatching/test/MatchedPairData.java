package ac.technion.schemamatching.test;

import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;

public class MatchedPairData {
  protected String attribute1 ;
  protected String attribute2 ;
  protected double weight ;
  protected boolean isCorrect =false;

  // for test4 - counts how many times this match appeared in all the Ks up
  //   to and including the one which holds this object.
  protected int countNumberAppeared ;

  public MatchedPairData(MatchedAttributePair pair,int count) {
    attribute1 = pair.getAttribute1();
    attribute2 = pair.getAttribute2();
    weight = pair.getMatchedPairWeight() ;
    countNumberAppeared = count;
  }

  public MatchedPairData(MatchedAttributePair pair) {
    attribute1 = pair.getAttribute1();
    attribute2 = pair.getAttribute2();
    weight = pair.getMatchedPairWeight();
    countNumberAppeared = 1;
  }

  public MatchedPairData(MatchedAttributePair pair,boolean _isCorrect) {
    attribute1 = pair.getAttribute1();
    attribute2 = pair.getAttribute2();
    weight = pair.getMatchedPairWeight();
    countNumberAppeared = 1;
    isCorrect = _isCorrect;
  }

  public MatchedPairData() {
  }

  public boolean isSamePair(MatchedAttributePair other){
    if (this.attribute1.equals(other.getAttribute1()) &&
       this.attribute2.equals(other.getAttribute2())    )
      return true ;
    return false;
  }

  public void incrementNumberAppeared() {
    countNumberAppeared++;
  }

  public int getCountNumberAppeared() {
    return countNumberAppeared;
  }

  public void setCountNumberAppeared(int newCount) {
    this.countNumberAppeared=newCount;
  }

  public MatchedPairData createClone() {
    MatchedPairData result =  new MatchedPairData();
    result.attribute1 = this.attribute1;
    result.attribute2 = this.attribute2 ;
    result.countNumberAppeared = this.countNumberAppeared;
    result.weight = this.weight;
    return result;
  }
  public String getAttribute1() {
    return attribute1;
  }
  public String getAttribute2() {
    return attribute2;
  }

  public double getWeight() {
    return weight ;
  }

  public boolean isCorrect(){
    return isCorrect ;
  }

  public int isCorrectInt(){
    if (isCorrect)
      return 1;
    else
      return 0;
  }
}

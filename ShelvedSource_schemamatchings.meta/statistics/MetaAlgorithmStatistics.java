package schemamatchings.meta.statistics;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import schemamatchings.meta.algorithms.AbstractMetaAlgorithm;
import schemamatchings.meta.algorithms.MetaAlgorithm;
import schemamatchings.meta.experiments.Point;
import schemamatchings.meta.experiments.TopKPlot;

public  class MetaAlgorithmStatistics {

  public static final byte TA_STATISTICS = 0;
  public static final byte MD_STATISTICS = 1;
  public static final byte MDB_STATISTICS = 2;
  public static final byte HYBRID_STATISTICS = 3;

  private int iterations = 0;
  private int mappings = 0;
  private int k = 0;
  private int topKIndexs = 0;
  private long startTimeMilisec = 0;
  private long stopTimeMilisec = 0;
  private double lastThreshold = 0;
  private int totalHighMappings = 0;
  private AbstractMetaAlgorithm algorithm;
  private int step = 0;
  private TopKPlot iterationsPlot;
  private TopKPlot precisionPlot;
  protected TopKPlot mappingsPlot;
  private String candidateSchemaName;
  private String targetSchemaName;
  private String localFAggragatorType;
  private String localHAggragatorType;
  private String globalFAggragatorType;
  private String globalHAggragatorType;
  private int numOfUsefullMappings;
  private PrintWriter out;
  private int lastMappingsCount = 0;
  private int kOf100Recall = -1;
  private int recallIterations = -1;

  public MetaAlgorithmStatistics(AbstractMetaAlgorithm algorithm,String candidateSchemaName,String targetSchemaName){
    this.algorithm = algorithm;
    this.candidateSchemaName = candidateSchemaName;
    this.targetSchemaName = targetSchemaName;
    iterationsPlot = new TopKPlot(algorithm.getAlgorithmName(),"k","iterations");
    mappingsPlot = new TopKPlot(algorithm.getAlgorithmName(),"k","mappingss");
    try{
    out = new PrintWriter(new FileOutputStream(algorithm.getAlgorithmName()+".txt"));
    }catch(IOException io){}
  }

  public PrintWriter getWriter(){
    return out;
  }

  public void setkOf100Recall(int k,int i){
    kOf100Recall = k;
    recallIterations = i;
  }

  public void setNumOfUsefullMappings(int numOfUsefullMappings){
    this.numOfUsefullMappings = numOfUsefullMappings;
  }

  public void setFAggregatorTypes(String lAggr,String gAggr){
    localFAggragatorType = lAggr;
    globalFAggragatorType = gAggr;
  }

  public void setHAggregatorTypes(String lAggr,String gAggr){
    localHAggragatorType = lAggr;
    globalHAggragatorType = gAggr;
  }

  public MetaAlgorithm getMetaAlgorithm(){
    return algorithm;
  }

  public void setPrecisionPlot(TopKPlot precisionPlot){
    this.precisionPlot = precisionPlot;
  }

  public static MetaAlgorithmStatistics getStatisticsInstance(byte type,AbstractMetaAlgorithm algorithm,String candidateSchemaName,String targetSchemaName){
    switch(type){
      case(TA_STATISTICS):
        return new TAStatistics(algorithm,candidateSchemaName,targetSchemaName);
      case(MD_STATISTICS):
        return new MetaAlgorithmStatistics(algorithm,candidateSchemaName,targetSchemaName);
      default: return null;
      }
  }

  public void setCurrentTopKMappings(int cnt){
    if (cnt > topKIndexs){
      int diff = cnt - topKIndexs;
      if (diff != 0){
        for (int i=0;i<diff;i++){
           iterationsPlot.addPlot(new Point(++topKIndexs,iterations));
           mappingsPlot.addPlot(new Point(topKIndexs,mappings/* - lastMappingsCount*/));
        }
        lastMappingsCount = mappings;
      }
    }
  }

  public void printNonUniformMappingsPlot(){
    int points = iterationsPlot.size();
    Point p;
    System.out.println("Non Uniform Mappings per generated Top K");
    if (algorithm.currentGeneratedTopK() !=  algorithm.getK())
      System.out.println("Experiment Terminated without finishing top k mappings"
       +",Iterations when stop:"+iterations);
    for (int i=0;i<points;i++){
      p = iterationsPlot.getPlot(i);
      if (p.y == 1)
         System.out.println(p.x+"\t"+algorithm.getNumOfSchemaMatchers());
      else
         System.out.println(p.x+"\t"+(algorithm.getNumOfSchemaMatchers()+(p.y - 1)));
    }
  }

  public void printIterationsPlot(){
    int points = iterationsPlot.size();
    Point p;
    System.out.println("Total iterations needed per generated Top K");
    for (int i=0;i<points;i++){
      p = iterationsPlot.getPlot(i);
      System.out.println(p.x+"\t"+p.y);
    }
  }

  public void printUniqueMappingsPlot(){
    int points = mappingsPlot.size();
    Point p;
    System.out.println("Total unique mappings needed per generated Top K");
    for (int i=0;i<points;i++){
      p = mappingsPlot.getPlot(i);
      System.out.println(p.x+"\t"+p.y);
    }
  }

  public TopKPlot getMappingsPlot(){
    return mappingsPlot;
  }

  public TopKPlot getIterationsPlot(){
    return iterationsPlot;
  }

  public void setStep(int step){
    this.step = step;
  }

  public void setTotalHighMappings(int totalHighMappings){
    this.totalHighMappings = totalHighMappings;
  }

  public int getTotalHighMappings(){
    return totalHighMappings;
  }

  public void setKParameter(int k){
    this.k = k;
  }

  public int getKParameter(){
    return k;
  }

  public void setLastThreshold(double lastThreshold){
    this.lastThreshold = lastThreshold;
  }

  public double getLastThreshold(){
    return lastThreshold;
  }


  public int getIterationsCount(){
    return iterations;
  }

  public synchronized void increaseIterationsCount(){
    iterations++;
  }

  public void printRecallInformation(){
    if (kOf100Recall != -1)
       System.out.println("Recall reached 100% at k="+kOf100Recall+" iterations="+recallIterations);
    else
       System.out.println("Recall not reached 100% within k="+k);
  }

  public void startTimer(){
    startTimeMilisec = System.currentTimeMillis();
  }

  public void stopTimer(){
    stopTimeMilisec = System.currentTimeMillis();
  }

  public long getExecutionTime(){
    return (stopTimeMilisec - startTimeMilisec);
  }

  public int getTotalMappingsCount(){
    return mappings;
  }


  public synchronized void increaseTotalMappingsCount(){
    mappings++;
    //System.out.println(mappings);
  }

  public void printStatistics(){
    System.out.println(getStatisticsString());
  }

  private String getTimePresentation(long executionTime){
    long hours = executionTime / (1000*60*60);
    long minutes = (executionTime - (hours*60*60*1000)) / (60*1000);
    long seconds = (executionTime - (hours*60*60*1000) - (minutes*60*1000)) / 1000;
    long milisecs = (executionTime - (hours*60*60*1000) - (minutes*60*1000) - (seconds*1000)) % 1000;
    return hours+"h:"+minutes+"m:"+seconds+"sec:"+milisecs+"msec";
  }

  protected String getStatisticsString(){
    String stat = "";
    stat += "Meta Algorithm: "+algorithm.getAlgorithmName()+" Statistics:\n\n";
    stat += "Candidate Schema: "+candidateSchemaName+" Target Schema: "+targetSchemaName+"\n";
    stat += "f local:"+localFAggragatorType+" F global:"+globalFAggragatorType+"\n";
    if (localHAggragatorType != null)
    stat += "h local:"+localHAggragatorType+" H global:"+globalHAggragatorType+"\n";
    stat +="> K Best Mappings Requested: "+getKParameter()+"\n";
    stat +="> Iterations Count: "+getIterationsCount()+"\n";
    stat +="> Total Mappings Count: "+getTotalMappingsCount()+"\n";
    stat +="> Total Usefull Mappings: "+numOfUsefullMappings+"\n";
    stat +="> Execution Time: "+getTimePresentation(getExecutionTime())+"\n";
    return stat;
  }

  protected String getPlotsString(){
    String stat = "";
    stat += "\nPrecision vs. Top K generated mappings:\n\n";
    stat += precisionPlot.getGraphString();
    stat += "\nIterations vs. Top K generated mappings:\n\n";
    stat += iterationsPlot.getGraphString();
    stat += "\nMappingss vs. Top K generated mappings:\n\n";
    stat += mappingsPlot.getGraphString();
    return stat;
  }

  public void saveToTXTFile(PrintWriter out) throws IOException{
     StringBuffer toSave = new StringBuffer(getStatisticsString());
     toSave.append(getPlotsString());
     out.println(toSave.toString());
     out.flush();
  }

  public void saveToTXTFile(PrintWriter out,boolean b) throws IOException{
    StringBuffer toSave = new StringBuffer(getStatisticsString());

  }


}
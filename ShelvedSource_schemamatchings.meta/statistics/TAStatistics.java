package schemamatchings.meta.statistics;

import schemamatchings.meta.algorithms.AbstractMetaAlgorithm;

public class TAStatistics extends MetaAlgorithmStatistics{

  private int threadsCount = 0;
  private int[][] generatedMappingsPerThread;
  private int lastK = 0;


  public TAStatistics(AbstractMetaAlgorithm algorithm,String candidateSchemaName,String targetSchemaName) {
    super (algorithm,candidateSchemaName,targetSchemaName);
  }

  public int getThreadsCount(){
    return threadsCount;
  }

  public void setThreadsCount(int threadsCount){
    this.threadsCount = threadsCount;
    generatedMappingsPerThread = new int[threadsCount][getKParameter()];
  }

  public synchronized void increaseThreadMappingCount(int tid,int k){
    generatedMappingsPerThread[tid][k!=0 ? k-1 : k]++;
//    if (lastK != k){
//       mappingsPlot.addPlot(new Point(k,generatedMappingsPerThread[tid][k!=0 ? k-1 : k]));
//       lastK = k;
//    }
  }

  public void updateMappingsCount(){

  }


  public void printStatistics(){
    super.printStatistics();
    System.out.println("> Threads Count: "+getThreadsCount());
    System.out.println("> New Mappings Generated Per Thread:");
    for (int i=0;i<threadsCount;i++)
      System.out.println("    > Thread "+(i+1)+" Total New Generated Mappings: "+generatedMappingsPerThread[i]);
    System.out.println("> Last Threshold Value: "+getLastThreshold());
    System.out.println("> Total Scored High Mappings : "+getTotalHighMappings());
  }

  public String getStatisticsString(){
    String stat = super.getStatisticsString();
    stat +="> Threads Count: "+getThreadsCount()+"\n";
    stat +="> New Mappings Generated Per Thread:"+"\n";
    for (int i=0;i<threadsCount;i++)
      stat +="    > Thread "+(i+1)+" Total New Generated Mappings: "+generatedMappingsPerThread[i]+"\n";
    stat +="> Last Threshold Value: "+getLastThreshold()+"\n";
    stat +="> Total Scored High Mappings : "+getTotalHighMappings()+"\n";
    return stat;
  }

}
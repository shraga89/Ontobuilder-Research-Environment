package schemamatchings.meta.experiments;

import schemamatchings.meta.algorithms.*;
import schemamatchings.meta.statistics.*;
import schemamatchings.meta.agr.*;
import schemamatchings.meta.analysis.*;
import schemamatchings.util.SchemaTranslator;
import schemamatchings.util.SchemaMatchingAlgorithmsRunner;
import schemamatchings.ontobuilder.*;
import schemamatchings.util.SchemaMatchingsUtilities;


import java.io.*;
import java.util.*;

import javax.swing.JFrame;

public class Experiment {

  private boolean top1Precission = false;
  private boolean topKAvgPrecission = false;
  private boolean avgIterations = false;
  private boolean taAlgorithm = false;
  private boolean mdAlgorithm = false;
  private boolean mdbAlgorithm = false;
  private boolean hybridAlgorithm = false;
  private int k = 0;
  private int step = 0;
  private String candidateSchema = null;
  private String targetSchema = null;
  private String[] matchingAlgorithms = null;
  private String localFAggr = null;
  private String localHAggr = null;
  private String globalFAggr = null;
  private String globalHAggr = null;
  private double localAvgAggrThreshold = 0;
  private double globalAvgAggrThreshold = 0;
  private OntoBuilderWrapper ob;
  private Vector statistics;
  private SMThersholdAlgorithm ta;
  private MatrixDirectAlgorithm md;
  private MatrixDirectWithBoundingAlgorithm mdb;
  private CrossThresholdAlgorithm hybrid;
  private MetaExperimentsGUI experimentGui;
  private SchemaTranslator exactMapping;
  private TopKPlot taPrecisionPlot;
  private TopKPlot mdPrecisionPlot;
  private TopKPlot mdbPrecisionPlot;
  private TopKPlot hybridPrecisionPlot;
  private TopKPlot taIterationsPlot;
  private TopKPlot mdIterationsPlot;
  private TopKPlot mdbIterationsPlot;
  private TopKPlot hybridIterationsPlot;
  private TopKPlot taMappingsPlot;
  private TopKPlot mdMappingsPlot;
  private TopKPlot mdbMappingsPlot;
  private TopKPlot hybridMappingsPlot;
  private int totalPlots = 0;
  private String filenamneSuffix;
  private boolean nonUniform = false;
  private byte nonUniformVersion = 1;
  private boolean recallRun = false;
  private boolean debugMode = false;
  private String outputDirectory;
  private ExperimentAnalayzer ea;
  private  PrintWriter taNUMatrixFile;
  private  PrintWriter hybNUMatrixFile;
  private int nonUniformType;

  public void setHYBNUMatrixFile( PrintWriter f){
    hybNUMatrixFile = f;
  }

  public void setNonUniformType(int type){
    nonUniformType = type;
  }

  public void setTANUMatrixFile( PrintWriter f){
    taNUMatrixFile = f;
  }


  public Experiment(OntoBuilderWrapper ob,MetaExperimentsGUI experimentGui){
    this.ob = ob;
    this.experimentGui = experimentGui;
  }

  public void setRecallRun(boolean rr){
    recallRun = rr;
  }

  public void loadExactMapping(String filepath)throws Throwable{
     exactMapping = SchemaMatchingsUtilities.readXMLBestMatchingFile("onto_exact/"+filepath);
  }

  public void setMatchAlgorithms(String [] matchingAlgorithms){
    this.matchingAlgorithms = matchingAlgorithms;
  }

  public void setOutputDir(String d){
    outputDirectory = d;
  }

  public void setDebugMode(boolean m){
    debugMode = m;
  }

  public void setNonUniform(boolean nonUniform,byte nonUniformVersion){
    this.nonUniform = nonUniform;
    this.nonUniformVersion = nonUniformVersion;
  }

  public void setRunAlgorithms(boolean[] algorithms){
    taAlgorithm = algorithms[0];
    if (taAlgorithm) totalPlots++;
    mdAlgorithm = algorithms[1];
    if (mdAlgorithm) totalPlots++;
    mdbAlgorithm = algorithms[2];
    if (mdbAlgorithm) totalPlots++;
    hybridAlgorithm = algorithms[3];
    if (hybridAlgorithm) totalPlots++;
    statistics = new Vector(totalPlots);
  }

  public void setK(int k,int step){
    this.k = k;
    this.step = step;
  }

  public void setSchemas(String[] schemas){
    candidateSchema = schemas[0];
    targetSchema = schemas[1];
  }

  public void setAggregators(String[] aggr){
    localFAggr = aggr[0];
    globalFAggr = aggr[1];
    if (aggr.length > 2){
      localHAggr = aggr[2];
      globalHAggr = aggr[3];
    }
  }

  public void setFilenameSuffix(String suffix){
    filenamneSuffix = suffix;
  }

  public void resetThresholds(){
    localAvgAggrThreshold = 0;
    globalAvgAggrThreshold = 0;
  }

  public void setLocalAvgAggrThreshold(double threshold){
    this.localAvgAggrThreshold = threshold;
  }

  public void setGlobalAvgAggrThreshold(double threshold){
   this.globalAvgAggrThreshold = threshold;
  }

  public void useStatistics(boolean[] statistics){
    top1Precission = statistics[0];
    topKAvgPrecission = statistics[1];
    avgIterations = statistics[2];
  }

  private byte getAggregators(){
    if (localFAggr.equals("Sum")) return EAWrapper.SUM_SUM;
    if (globalFAggr.equals("Min")) return EAWrapper.AVG_MIN;
    if (localAvgAggrThreshold == 0) return EAWrapper.AVG_0_AVG;
    if (localAvgAggrThreshold == 0.025) return EAWrapper.AVG_0025_AVG;
    if (localAvgAggrThreshold == 0.05) return EAWrapper.AVG_0050_AVG;
    if (localAvgAggrThreshold == 0.10) return EAWrapper.AVG_010_AVG;
    if (localAvgAggrThreshold == 0.15) return EAWrapper.AVG_015_AVG;
    if (localAvgAggrThreshold == 0.20) return EAWrapper.AVG_020_AVG;
    if (localAvgAggrThreshold == 0.25) return EAWrapper.AVG_025_AVG;
    if (localAvgAggrThreshold == 0.5) return EAWrapper.AVG_050_AVG;
    return EAWrapper.AVG_075_AVG;
  }

  private byte getMatchers(){
    if (matchingAlgorithms.length == 4) return ExperimentAnalayzer.ALL;
    if (matchingAlgorithms[0].equals(MatchingAlgorithms.TERM)){
      if (matchingAlgorithms[1].equals(MatchingAlgorithms.PRECEDENCE))
        return  ExperimentAnalayzer.TERM_PRECEDENCE;
      else if (matchingAlgorithms[1].equals(MatchingAlgorithms.COMPOSITION))
        return  ExperimentAnalayzer.TERM_COMPOSITION;
      else
        return ExperimentAnalayzer.TERM_VALUE;
    }
    else{//value
      if (matchingAlgorithms[1].equals(MatchingAlgorithms.PRECEDENCE))
        return  ExperimentAnalayzer.VALUE_PRECEDENCE;
      else
        return ExperimentAnalayzer.VALUE_COMPOSITION;
    }
  }

  public void runExperiment() throws Throwable{
    Object[] params;
    MetaAlgorithmStatistics stat;
    Schema candidate = ob.readOntologyXMLFile("onto_exact/"+candidateSchema,true);
    Schema target = ob.readOntologyXMLFile("onto_exact/"+targetSchema,true);
    Thread myThread = Thread.currentThread();
    MatchAlgorithm[] matchAlgorithms = new MatchAlgorithm[matchingAlgorithms.length];
    /// new analysis object
    ea = new ExperimentAnalayzer();
    ea.setAggregatorsType(getAggregators());
    ea.setCandOntology(candidateSchema);
    ea.setTargetOntology(targetSchema);
    ea.setMatchers(getMatchers());
    ea.setThreshold(localAvgAggrThreshold);
    ea.setType(nonUniform ? ea.NON_UNIFORM : ea.UNIFORM);
    //end new analysis object
    if (taAlgorithm){
       for (int i=0;i<matchingAlgorithms.length;i++)
         matchAlgorithms[i] = ob.loadMatchAlgorithm(matchingAlgorithms[i]);
       params = new Object[3];
       params[0] = new Integer(k);
       params[1] = getGlobalAggregator(globalFAggr);
       params[2] = getLocalAggregator(localFAggr,true);
       ta = (SMThersholdAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNames.THERSHOLD_ALGORITHM,params);
       ta.init(candidate,target,matchAlgorithms.length,matchAlgorithms,SchemaMatchingAlgorithmsRunner.class);
       ta.useStatistics();
       ta.setThreshold(localAvgAggrThreshold);
       ta.setRecallRun(false);
       ta.setExactMapping(exactMapping);
       ta.normalizeMatrixes();
       ta.setNonUniform(nonUniform,nonUniformVersion);
       ta.setDebugMode(debugMode);
       try{
         ta.runAlgorithm();
         while (!ta.isAlgorithmRunFinished()){
           try{
             myThread.sleep(100);
             }catch(InterruptedException e){}
         }
       }catch(Throwable e){

       }
       taPrecisionPlot = new TopKPlot(ta.getAlgorithmName(),"k","precision");
//       if (exactMapping != null)
//       for (int i=0;i<k;i++){
//         if (step== 0 || (step != 0 && i%step == 0)){
//           taPrecisionPlot.addPlot(new Point(i+1,SchemaMatchingsUtilities.calculatePrecision(exactMapping,(SchemaTranslator)ta.getKthBestMapping(i+1))));
//         }
//       }
        stat =  ta.getStatistics();
        //
        ExperimentStatistics es = new ExperimentStatistics();
        es.setIterationsPlot(stat.getIterationsPlot());
        es.setUniqueMappingsPlot(stat.getMappingsPlot());
        if (nonUniform){
          //es.setNonUniformPlot(stat.getNonUniformMappingsPlot());
          es.setStepsIPlot(stat.getStepsIPlot());
          es.setStepsMPlot(stat.getStepsMPlot());
        }
        ea.updateES(es,ea.TA);
        //
        ta = null;
        System.runFinalization();
        stat.setPrecisionPlot(taPrecisionPlot);
        statistics.add(stat);
//        taIterationsPlot = stat.getIterationsPlot();
//        taIterationsPlot.printGraph();
    }
    if (mdAlgorithm){
       for (int i=0;i<matchingAlgorithms.length;i++)
         matchAlgorithms[i] = ob.loadMatchAlgorithm(matchingAlgorithms[i]);
       params = new Object[4];
       params[0] = new Integer(k);
       params[1] = getGlobalAggregator(globalFAggr);
       params[2] = getLocalAggregator(localFAggr,true);
       params[3] = new MatchMatrix();
       md = (MatrixDirectAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNames.MATRIX_DIRECT_ALGORITHM,params);
       md.init(candidate,target,matchAlgorithms.length,matchAlgorithms,new SchemaMatchingAlgorithmsRunner());
       md.useStatistics();
       md.setThreshold(localAvgAggrThreshold);
       md.normalizeMatrixes();
       md.setDebugMode(debugMode);
       try{
          md.runAlgorithm();
       }catch(Throwable e){
         //ignore
       }
       mdPrecisionPlot = new TopKPlot(md.getAlgorithmName(),"k","precision");
 if (exactMapping != null)
        for (int i=0;i<k;i++){
          if (step== 0 || (step != 0 && i%step == 0)){
            mdPrecisionPlot.addPlot(new Point(i+1,SchemaMatchingsUtilities.calculatePrecision(exactMapping,(SchemaTranslator)md.getKthBestMapping(i+1))));
          }
        }
        stat =  md.getStatistics();
        md = null;
        System.runFinalization();
        stat.setPrecisionPlot(mdPrecisionPlot);
        statistics.add(stat);
//        mdIterationsPlot = stat.getIterationsPlot();
//        mdIterationsPlot.printGraph();
    }
    if (mdbAlgorithm){
      for (int i=0;i<matchingAlgorithms.length;i++)
        matchAlgorithms[i] = ob.loadMatchAlgorithm(matchingAlgorithms[i]);
      params = new Object[6];
      params[0] = new Integer(k);
      params[1] = getGlobalAggregator(globalFAggr);
      params[2] = getLocalAggregator(localFAggr,true);
      params[3] = getGlobalAggregator(globalHAggr);
      params[4] = getLocalAggregator(localHAggr,false);
      params[5] = new MatchMatrix();
      mdb = (MatrixDirectWithBoundingAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNames.MATRIX_DIRECT_WITH_BOUNDING_ALGORITHM,params);
      mdb.init(candidate,target,matchAlgorithms.length,matchAlgorithms,new SchemaMatchingAlgorithmsRunner());
      mdb.useStatistics();
      mdb.setThreshold(localAvgAggrThreshold);
      mdb.setRecallRun(false);
      mdb.setExactMapping(exactMapping);
      mdb.normalizeMatrixes();
      mdb.setDebugMode(debugMode);
      try{
         mdb.runAlgorithm();
      }catch(Throwable e){
         e.printStackTrace();
      }
      mdbPrecisionPlot = new TopKPlot(mdb.getAlgorithmName(),"k","precision");
//       if (exactMapping != null)
//      for (int i=0;i<k;i++){
//        if (step== 0 || (step != 0 && i%step == 0)){
//          mdbPrecisionPlot.addPlot(new Point(i+1,SchemaMatchingsUtilities.calculatePrecision(exactMapping,(SchemaTranslator)mdb.getKthBestMapping(i+1))));
//        }
//      }
      stat =  mdb.getStatistics();
      //
        ExperimentStatistics es = new ExperimentStatistics();
        es.setIterationsPlot(stat.getIterationsPlot());
        es.setUniqueMappingsPlot(stat.getMappingsPlot());
        ea.updateES(es,ea.MDB);
        //
      mdb = null;
      System.runFinalization();
      stat.setPrecisionPlot(mdbPrecisionPlot);
      statistics.add(stat);
//      mdbIterationsPlot = stat.getIterationsPlot();
//      mdbIterationsPlot.printGraph();
    }
    if (hybridAlgorithm){
      for (int i=0;i<matchingAlgorithms.length;i++)
        matchAlgorithms[i] = ob.loadMatchAlgorithm(matchingAlgorithms[i]);
      params = new Object[6];
      params[0] = new Integer(k);
      params[1] = getGlobalAggregator(globalFAggr);
      params[2] = getLocalAggregator(localFAggr,true);
      params[3] = getGlobalAggregator(globalHAggr);
      params[4] = getLocalAggregator(localHAggr,false);
      params[5] = new MatchMatrix();
      hybrid = (CrossThresholdAlgorithm)MetaAlgorithmsFactory.getInstance().buildMetaAlgorithm(MetaAlgorithmNames.HYBRID_ALGORITHM,params);
      hybrid.init(candidate,target,matchAlgorithms.length,matchAlgorithms,SchemaMatchingAlgorithmsRunner.class);
      hybrid.useStatistics();
      hybrid.setThreshold(localAvgAggrThreshold);
      hybrid.setRecallRun(false);
      hybrid.setExactMapping(exactMapping);
      hybrid.normalizeMatrixes();
      hybrid.setNonUniform(nonUniform,nonUniformVersion);
      hybrid.setNonUnifornType(nonUniformType);
      hybrid.setDebugMode(debugMode);
      try{
      hybrid.runAlgorithm();
      while (!hybrid.isAlgorithmRunFinished()){
        try{
          myThread.sleep(100);
          }catch(InterruptedException e){}
      }
      }catch(Throwable e){
        //ignore
      }
      hybridPrecisionPlot = new TopKPlot(hybrid.getAlgorithmName(),"k","precision");
//      if (exactMapping != null)
//      for (int i=0;i<k;i++){
//        if (step== 0 || (step != 0 && i%step == 0)){
//          hybridPrecisionPlot.addPlot(new Point(i+1,SchemaMatchingsUtilities.calculatePrecision(exactMapping,(SchemaTranslator)hybrid.getKthBestMapping(i+1))));
//        }
//      }
      stat = hybrid.getStatistics();
      //
        ExperimentStatistics es = new ExperimentStatistics();
        es.setIterationsPlot(stat.getIterationsPlot());
        es.setUniqueMappingsPlot(stat.getMappingsPlot());
        if (nonUniform){
          es.setNonUniformPlot(stat.getNonUniformMappingsPlot());
          es.setStepsIPlot(stat.getStepsIPlot());
          es.setStepsMPlot(stat.getStepsMPlot());
        }
        ea.updateES(es,ea.HYB);
        //
      hybrid= null;
      System.runFinalization();
      stat.setPrecisionPlot(hybridPrecisionPlot);
      statistics.add(stat);
//      hybridIterationsPlot = stat.getIterationsPlot();
//      hybridIterationsPlot.printGraph();
    }
    if (experimentGui != null)
        showPrecisionDialog();
  }

  private String getMetaString(){
    return candidateSchema+"_"+targetSchema+"_"+localFAggr+"("+localAvgAggrThreshold+")/"+globalFAggr+"_"+getMatchersAb();
  }

  private void outputSteps(PrintWriter out,TopKPlot steps,String instance){
    println(out,instance);
    for (int i=0;i<steps.size();i++){
      printTab(out,steps.getPlot(i).getSy());
    }
    println(out,"");
  }

  private void printTab(PrintWriter out,String s){
    out.print(s+"\t");
    out.flush();
  }

  private void println(PrintWriter out,String s){
    out.println(s);
    out.flush();
  }

  private String getMatchersAb(){
    String s = "";
    for (int i=0;i<matchingAlgorithms.length;i++){
      if (matchingAlgorithms[i].equals(MatchingAlgorithms.TERM)){ s+= "T"; continue;}
      if (matchingAlgorithms[i].equals(MatchingAlgorithms.VALUE)){ s+= "V"; continue;}
      if (matchingAlgorithms[i].equals(MatchingAlgorithms.PRECEDENCE)){ s+= "P"; continue;}
      if (matchingAlgorithms[i].equals(MatchingAlgorithms.COMPOSITION)){ s+= "C"; continue;}
    }
    return s;
  }

  public void showPrecisionDialog(){
    ArrayList tempPrecisionPlots = new ArrayList(totalPlots);
    ArrayList tempIterationsPlots = new ArrayList(totalPlots);
    TopKPlot[] precisionPlots = new TopKPlot[totalPlots];
    TopKPlot[] iterationsPlots = new TopKPlot[totalPlots];
    if (taAlgorithm){
      tempPrecisionPlots.add(taPrecisionPlot);
      tempIterationsPlots.add(taIterationsPlot);
    }
    if (mdAlgorithm){
     tempPrecisionPlots.add(mdPrecisionPlot);
     tempIterationsPlots.add(mdIterationsPlot);
    }
    if (mdbAlgorithm){
      tempPrecisionPlots.add(mdbPrecisionPlot);
      tempIterationsPlots.add(mdbIterationsPlot);
    }
    if (hybridAlgorithm){
      tempPrecisionPlots.add(hybridPrecisionPlot);
      tempIterationsPlots.add(hybridIterationsPlot);
    }
    for (int i=0;i<totalPlots;i++){
      precisionPlots[i] = (TopKPlot)tempPrecisionPlots.get(i);
      iterationsPlots[i] = (TopKPlot)tempIterationsPlots.get(i);

    }
    new StatisticsDialog(experimentGui,true,this,precisionPlots,iterationsPlots).show();
  }

  public AbstractLocalAggregator getLocalAggregator(String aggeratorType,boolean useThreshold){
    if (aggeratorType.equals(LocalAggregatorTypes.ALL[0]))
      return new SumLocalAggregator();
    else if (aggeratorType.equals(LocalAggregatorTypes.ALL[1]))
      return new ProductLocalAggregator();
    else
      return new AverageLocalAggregator(useThreshold ? localAvgAggrThreshold : 0);
  }
  public void saveStatisticsToObjectFile(String dir) throws IOException{
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dir+"/statistics_"+candidateSchema+"_"+targetSchema+"_"+filenamneSuffix+".txt"));
    out.writeObject(ea);
    out.flush();
    out.close();
  }

  public void saveStatisticsToTXTFile() throws IOException{
    System.setOut(new PrintStream(new FileOutputStream(outputDirectory+"/statistics_"+candidateSchema+"_"+targetSchema+"_"+filenamneSuffix+".txt")));
    Iterator it = statistics.iterator();
    while (it.hasNext()){
      MetaAlgorithmStatistics stat = (MetaAlgorithmStatistics)it.next();
      stat.printStatistics();
      if (nonUniform){
        stat.printNonUniformMappingsPlot();
      }
      else{
        if (recallRun){
           stat.printRecallInformation();
        }
        stat.printIterationsPlot();
        stat.printUniqueMappingsPlot();
      }
    }
  }




  public AbstractGlobalAggregator getGlobalAggregator(String aggeratorType){
    if (aggeratorType.equals(GlobalAggregatorTypes.ALL[0]))
      return new SumGlobalAggregator();
    else if (aggeratorType.equals(GlobalAggregatorTypes.ALL[1]))
      return new MinimumGlobalAggregator();
    else
      return new AverageGlobalAggregator(globalAvgAggrThreshold);
  }

}

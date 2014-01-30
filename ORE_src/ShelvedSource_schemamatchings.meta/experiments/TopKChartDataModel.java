package schemamatchings.meta.experiments;

import com.klg.jclass.chart.ChartDataModel;


public class TopKChartDataModel implements ChartDataModel {

  private TopKPlot[] plots;

  public TopKChartDataModel(TopKPlot[] plots) {
    this.plots = plots;
  }

  //Retrieves the name for the data source.
  public String getDataSourceName(){
    return "Top K Iterations";
  }

  //Retrieves the number of data series.
  public int getNumSeries(){
    return plots.length;
  }

  //Retrieves the labels to be used for each point in a particular data series.
  public String[] getPointLabels(){
    return null;
  }

  //Retrieves the labels to be used for each data series.
  public String[] getSeriesLabels(){
    String[] labels = new String[plots.length];
    for (int i=0;i<plots.length;i++)
      labels[i] = plots[i].algorithmName();
    return labels;
  }



  //Retrieves the x values of the specified data series.
  public  double[] getXSeries(int index){
    return plots[index].getXSeries();
  }

  //Retrieves the y values of the specified data series.
  public double[] getYSeries(int index){
     return plots[index].getYSeries();
  }



}
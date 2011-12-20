package schemamatchings.meta.experiments;

import java.awt.*;
import javax.swing.JPanel;
import com.klg.jclass.chart.beans.*;
import com.klg.jclass.chart.ChartDataView;
import com.klg.jclass.chart.ChartDataViewSeries;


public class TopKStatisticsPanel extends JPanel {

  public static final byte ITERATIONS = 0;
  public static final byte PRECISION = 1;

  private SimpleChart chart = new SimpleChart();
  ChartDataView view = new ChartDataView();
  private TopKPlot[] plots;
  private byte type;

  public TopKStatisticsPanel(TopKPlot[] plots,byte type) {
    try {
      this.type = type;
      this.plots = plots;
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    this.setLayout(null);
    chart.setBounds(new Rectangle(0, 0, 400, 303));
    chart.setXAxisTitleText("k");
    chart.setAlignmentX(1);
    switch(type){
      case(PRECISION):
        chart.setYAxisTitleText("Precision");
        chart.setChartType(SimpleChart.BAR);
        break;
      case(ITERATIONS):
        chart.setYAxisTitleText("Iterations");
        break;
    }
    chart.getDataView(0).setDataSource(new TopKChartDataModel(plots));
    this.add(chart, null);
  }
}
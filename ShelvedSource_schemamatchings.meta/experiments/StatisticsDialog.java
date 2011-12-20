package schemamatchings.meta.experiments;

import java.awt.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.modica.gui.AbsoluteConstraints;
import com.modica.gui.AbsoluteLayout;
//import hplus.gui.*;
//import hplus.util.ExceptionsHandeler;

public class StatisticsDialog extends JDialog implements ActionListener {
  private JPanel panel1 = new JPanel();
  private Experiment experiment;
 // private ExceptionsHandeler exHandeler = new ExceptionsHandeler();

  public StatisticsDialog(MetaExperimentsGUI frame,boolean modal,Experiment experiment,TopKPlot[] plots1,TopKPlot[] plots2) {
    super(frame, modal);
    this.experiment  = experiment;
    try {
      getContentPane().setLayout(new AbsoluteLayout());
      getContentPane().add(new TopKStatisticsPanel(plots1,TopKStatisticsPanel.PRECISION) ,new AbsoluteConstraints(20,50,400,300));
      JLabel metaLabel = new JLabel("MD");
      metaLabel.setFont(new Font("Ariel",Font.BOLD,12));
      metaLabel.setForeground(Color.red);
      //getContentPane().add(metaLabel,new AbsoluteConstraints(20,355,-1,-1));
      metaLabel = new JLabel("TA");
      metaLabel.setFont(new Font("Ariel",Font.BOLD,12));
      metaLabel.setForeground(Color.red);
      //getContentPane().add(metaLabel,new AbsoluteConstraints(30,355,-1,-1));
      metaLabel = new JLabel("MDB");
      metaLabel.setFont(new Font("Ariel",Font.BOLD,12));
      metaLabel.setForeground(Color.red);
      //getContentPane().add(metaLabel,new AbsoluteConstraints(40,355,-1,-1));
      metaLabel = new JLabel("Hybrid");
      metaLabel.setFont(new Font("Ariel",Font.BOLD,12));
      metaLabel.setForeground(Color.red);
      //getContentPane().add(metaLabel,new AbsoluteConstraints(60,355,-1,-1));
      JLabel precisionLabel = new JLabel("Precision vs. Top K Generated Mapping");
      precisionLabel.setFont(new Font("Ariel",Font.BOLD,16));
      getContentPane().add(precisionLabel,new AbsoluteConstraints(70,30,-1,-1));
      getContentPane().add(new TopKStatisticsPanel(plots2,TopKStatisticsPanel.ITERATIONS),new AbsoluteConstraints(420,50,400,300));
      JLabel iterationsLabel = new JLabel("Iterations vs. Top K Generated Mapping");
      iterationsLabel.setFont(new Font("Ariel",Font.BOLD,16));
      getContentPane().add(iterationsLabel,new AbsoluteConstraints(470,30,-1,-1));
      optionsPanel = new javax.swing.JPanel();
      saveButton = new javax.swing.JButton();
      saveButton.setActionCommand("save");
      saveButton.addActionListener(this);
      optionsPanel.setLayout(new AbsoluteLayout());
      optionsPanel.setBorder(new javax.swing.border.TitledBorder(null, "Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12)));
      saveButton.setIcon(new javax.swing.ImageIcon("C:\\Documents and Settings\\hag\\Desktop\\images\\saveontology.gif"));
      saveButton.setToolTipText("");
      optionsPanel.add(saveButton, new AbsoluteConstraints(70, 20, 50, 30));
      getContentPane().add(optionsPanel, new AbsoluteConstraints(175, 520, 490, 60));
      setTitle("Meta Experiment Statistics Dialog");
      pack();
      java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
      setSize(new java.awt.Dimension(860, 640));
      setLocation((screenSize.width-860)/2,(screenSize.height-640)/2);
      setResizable(false);
    }
    catch(Exception ex) {
      ex.printStackTrace();
      //exHandeler.handleException(false,this,ex.getMessage(),"Error");
    }
  }

  public void actionPerformed(ActionEvent action){
      System.out.println("action:"+action.getActionCommand());
      if (action.getActionCommand().equals("save")){
        try{
           experiment.saveStatisticsToTXTFile();
        }catch(Throwable e){
          e.printStackTrace();
          //exHandeler.handleException(false,this,e.getMessage(),"Error");
        }
      }
  }

  private JPanel optionsPanel;
  private JButton saveButton;
}
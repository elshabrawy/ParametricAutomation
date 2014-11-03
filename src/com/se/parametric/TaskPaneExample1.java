package com.se.parametric;
import java.awt.*;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.*;

import org.jdesktop.swingx.*;
import org.jdesktop.swingx.painter.*;

import com.se.parametric.commonPanel.FilterPanel;
public class TaskPaneExample1 {

/** simple main driver for this class */
public static void main(String[] args) {
  SwingUtilities.invokeLater(new Runnable() {
    public void run() {
      new TaskPaneExample1();
    }
  });
}
public TaskPaneExample1() {
  JFrame frame = new JFrame("TaskPane Example 1");
  frame.add(doInit());
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  frame.setLocationRelativeTo(null);
  frame.pack();
  frame.setVisible(true);
}

/** creates a JXLabel and attaches a painter to it. */
private Component doInit() {
  JXPanel panel = new JXPanel();
  panel.setLayout(new BorderLayout());
  final JXLabel label = new JXLabel();
  label.setFont(new Font("Segoe UI", Font.BOLD, 14));
  label.setText("task pane item 1 : a label");
  label.setHorizontalAlignment(JXLabel.LEFT);
  label.setBackgroundPainter(getPainter());
   String[] titleOfCombobox=null;
	 ArrayList<Object[]> list=null;
  changeUIdefaults();

  JXTaskPaneContainer taskpanecontainer = new JXTaskPaneContainer();

  JXTaskPane taskpane = new JXTaskPane();
  taskpane.setTitle("My Tasks");

JPanel p=new JPanel();

  p.add(label);
  p.add(new JButton("OK"));
//  taskpane.add(new AbstractAction() {
//    {
//      putValue(Action.NAME, "task pane item 2 : an action");
//      putValue(Action.SHORT_DESCRIPTION, "perform an action");
////      putValue(Action.SMALL_ICON, Images.NetworkConnected.getIcon(32, 32));
//    }
//    public void actionPerformed(ActionEvent e) {
//      label.setText("an action performed");
//    }
//  });
  int width = Toolkit.getDefaultToolkit().getScreenSize().width;
	int height = Toolkit.getDefaultToolkit().getScreenSize().height;
	
	titleOfCombobox=new String[1];
	titleOfCombobox[0]="part";
	list=new ArrayList<Object[]>();
	Object []row=new Object[1];
	row[0]="1";
	list.add(row);
	FilterPanel filter=new FilterPanel(titleOfCombobox, list, true);
		taskpane.add(filter);
  // add the task pane to the taskpanecontainer
  taskpanecontainer.add(taskpane);

  // set the transparency of the JXPanel to 50% transparent
  panel.setAlpha(0.7f);

  panel.add(taskpanecontainer, BorderLayout.CENTER);
  panel.setPreferredSize(new Dimension(250, 200));

  return panel;
}
private void changeUIdefaults() {
  // JXTaskPaneContainer settings (developer defaults)
  /* These are all the properties that can be set (may change with new version of SwingX)
    "TaskPaneContainer.useGradient",
    "TaskPaneContainer.background",
    "TaskPaneContainer.backgroundGradientStart",
    "TaskPaneContainer.backgroundGradientEnd",
    etc.
  */

  // setting taskpanecontainer defaults
  UIManager.put("TaskPaneContainer.useGradient", Boolean.FALSE);
  UIManager.put("TaskPaneContainer.background", Colors.LightGray.color(0.5f));

  // setting taskpane defaults
  UIManager.put("TaskPane.font", new FontUIResource(new Font("Verdana", Font.BOLD, 16)));
  UIManager.put("TaskPane.titleBackgroundGradientStart", Colors.White.color());
  UIManager.put("TaskPane.titleBackgroundGradientEnd", Colors.LightBlue.color());
}

/** this painter draws a gradient fill
     * @return  */
public Painter getPainter() {
  int width = 100;
  int height = 100;
  Color color1 = Colors.White.color(0.5f);
  Color color2 = Colors.Gray.color(0.5f);

  LinearGradientPaint gradientPaint =
      new LinearGradientPaint(0.0f, 0.0f, width, height,
                              new float[]{0.0f, 1.0f},
                              new Color[]{color1, color2});
  MattePainter mattePainter = new MattePainter(gradientPaint);
  return mattePainter;
}

}//end class TaskPaneExample1

package com.se.parametric;

import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jdesktop.swingx.JXPanel;

import com.se.parametric.commonPanel.FilterPanel;

public class TestTaskPane
{
	static String[] titleOfCombobox=null;
	static ArrayList<Object[]> list=null;
public static void main(String args[]){
	
	JFrame frame=new JFrame("Task Pane");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	int width = Toolkit.getDefaultToolkit().getScreenSize().width;
	int height = Toolkit.getDefaultToolkit().getScreenSize().height;
	frame.setSize(width, height);
	titleOfCombobox=new String[1];
	titleOfCombobox[0]="part";
	list=new ArrayList<Object[]>();
	Object []row=new Object[1];
	row[0]="1";
	list.add(row);
	FilterPanel filter=new FilterPanel(titleOfCombobox, list, true);
	JXPanel panel=new JXPanel();
	panel.add(filter);
	frame.getContentPane().add(panel);
	frame.show();
}
}

package com.se.parametric.commonPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.FlowLayout;

public class ButtonsPanel extends JPanel
{
	JButton buttons[];

	public JButton[] getButtons()
	{
		return buttons;
	}

	public void setButtons(JButton[] buttons)
	{
		this.buttons = buttons;
	}

	public ButtonsPanel(ArrayList<String> buttonLabels)
	{
		buttons = new JButton[buttonLabels.size()];
		setBackground(new Color(211, 211, 211));
		setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnNewButton = new JButton("New button");
		add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("New button");
		add(btnNewButton_1);
		for(int i = 0; i < buttonLabels.size(); i++)
		{
			buttons[i] = new JButton(buttonLabels.get(i));
			buttons[i].setForeground(new Color(25, 25, 112));
			buttons[i].setFont(new Font("Tahoma", Font.BOLD, 11));
			this.add(buttons[i]);
		}
	}

}

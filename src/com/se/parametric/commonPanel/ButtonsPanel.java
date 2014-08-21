package com.se.parametric.commonPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

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
		this.setLayout(new GridLayout(buttonLabels.size(), 1, 0, 5));
		setBackground(new Color(211, 211, 211));
		setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		for(int i = 0; i < buttonLabels.size(); i++)
		{
			buttons[i] = new JButton(buttonLabels.get(i));
			buttons[i].setForeground(new Color(25, 25, 112));
			buttons[i].setFont(new Font("Tahoma", Font.BOLD, 11));
			this.add(buttons[i]);
		}
	}

}

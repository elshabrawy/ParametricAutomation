package com.se.users;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
public abstract class UserType 
{
	public void drawtoolbar(List<Object> buttons, List<Object> iconsurl, int wstart, int Bwidth,JToolBar toolBar){
		try
		{
			Image img;
			toolBar.addSeparator();
			for(int i = 0; i < buttons.size(); i++)
			{
				((JButton)(buttons.get(i))).setBounds(wstart + ((i + 1) * Bwidth), 5, Bwidth - 5, 32);
				((JButton)(buttons.get(i))).setForeground(new Color(25, 25, 112));
				// tlunApprovedBu.setBackground(new Color(255, 255, 255));
				((JButton)(buttons.get(i))).setFont(new Font("Herman", Font.PLAIN, 11));
//				buttons.get(i).addActionListener(this);
				((JButton)(buttons.get(i))).setToolTipText(((JButton)(buttons.get(i))).getText());
				// buttons.get(i).setOpaque(false);
				// buttons.get(i).setBorder(BorderFactory.createEmptyBorder());
				((JButton)(buttons.get(i))).setVerticalTextPosition(SwingConstants.BOTTOM);
				((JButton)(buttons.get(i))).setHorizontalTextPosition(SwingConstants.CENTER);
				img = ImageIO.read(getClass().getResource((String)iconsurl.get(i)));
				((JButton)(buttons.get(i))).setIcon(new ImageIcon(img));
				((JButton)(buttons.get(i))).setIconTextGap(5);
				toolBar.add(((JButton)(buttons.get(i))));
				toolBar.addSeparator();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public abstract ArrayList<ArrayList<String>> doFilter();
	public abstract ArrayList<ArrayList<Object>> creatTabs();
}

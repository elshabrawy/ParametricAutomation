package com.se.GUI;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import com.se.parametric.dto.GrmUserDTO;
public abstract class UserType 
{
	
	JTabbedPane tabbedPane;
	List<JButton> buttonlist;
	List<String> iconsurl;
	JToolBar toolBar;
	public void drawtoolbar( int wstart, int Bwidth,JToolBar toolBar){
		try
		{
			Image img;
			toolBar.addSeparator();
			for(int i = 0; i < buttonlist.size(); i++)
			{
				buttonlist.get(i).setBounds(wstart + ((i + 1) * Bwidth), 5, Bwidth - 5, 32);
				buttonlist.get(i).setForeground(new Color(25, 25, 112));
				// tlunApprovedBu.setBackground(new Color(255, 255, 255));
				buttonlist.get(i).setFont(new Font("Herman", Font.PLAIN, 11));
//				buttons.get(i).addActionListener(this);
				buttonlist.get(i).setToolTipText((buttonlist.get(i)).getText());
				// buttons.get(i).setOpaque(false);
				// buttons.get(i).setBorder(BorderFactory.createEmptyBorder());
				buttonlist.get(i).setVerticalTextPosition(SwingConstants.BOTTOM);
				buttonlist.get(i).setHorizontalTextPosition(SwingConstants.CENTER);
				img = ImageIO.read(getClass().getResource(iconsurl.get(i)));
				buttonlist.get(i).setIcon(new ImageIcon(img));
				buttonlist.get(i).setIconTextGap(5);
				toolBar.add(buttonlist.get(i));
				toolBar.addSeparator();
			}
		}catch(Exception e)
		{
			// System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	public abstract ArrayList<ArrayList<String>> doFilter();
	public abstract ArrayList<Object> creatTabs(GrmUserDTO userDTO);
}

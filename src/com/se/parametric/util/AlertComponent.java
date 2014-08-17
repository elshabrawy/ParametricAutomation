package com.se.parametric.util;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class AlertComponent extends JPanel
{
	private JLabel txtLbl;
	private CircleLable countLbl;
	private JLabel emptySpaceLbl;

	public AlertComponent(String txt, String count)
	{
		super();
		this.txtLbl = new JLabel(txt);
		this.countLbl = new CircleLable(count);
		this.emptySpaceLbl = new JLabel();
		init();
	}

	private void init()
	{
		setBackground(Color.LIGHT_GRAY);
		setOpaque(true);

		txtLbl.setForeground(Color.BLACK);
		txtLbl.setFont(new Font("Arial Black", Font.BOLD, 12));
		txtLbl.setHorizontalTextPosition(SwingConstants.LEFT);

		countLbl.setFont(new Font("Arial Black", Font.BOLD, 12));
		countLbl.setForeground(Color.WHITE);

		this.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(51, 51, 51), 2));
		this.setLayout(new GridBagLayout());

		GridBagConstraints bc = new GridBagConstraints();
		int x = txtLbl.getText().length() * 4;
		bc.ipadx = x;
		bc.gridx = 0;
		bc.gridy = 0;
		this.add(emptySpaceLbl, bc);

		bc = new GridBagConstraints();
		bc.gridx = 1;
		bc.gridy = 0;
		this.add(countLbl, bc);

		bc = new GridBagConstraints();
		bc.gridx = 0;
		bc.gridy = 1;
		bc.gridwidth = 2;
		bc.anchor = GridBagConstraints.LINE_START;
		this.add(txtLbl, bc);
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(new AlertComponent("ppxxtt", "123"));
		frame.pack();
		frame.setVisible(true);
	}
}

class CircleLable extends JLabel
{

	public CircleLable(String text)
	{
		super(text);
		init();
	}

	protected void init()
	{
		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);
		setBorder(new EmptyBorder(1, 3, 1, 3));
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		final ImageIcon icon = new ImageIcon(getClass().getResource("/Resources/lbl-bgr.png"));
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), null);
		super.paintComponent(g2d);
		g2d.dispose();
	}
}

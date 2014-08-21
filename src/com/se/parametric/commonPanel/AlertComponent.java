package com.se.parametric.commonPanel;

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
import javax.swing.border.EmptyBorder;

public class AlertComponent extends JPanel
{
	private JLabel txtLbl;
	private CircleLable countLbl;

	public AlertComponent(String txt)
	{
		super();
		this.txtLbl = new JLabel(txt);
		this.countLbl = new CircleLable();
		init();
	}

	private void init()
	{
		txtLbl.setForeground(Color.BLACK);
		txtLbl.setFont(new Font("Arial", Font.PLAIN, 12));

		countLbl.setFont(new Font("Arial Black", Font.BOLD, 12));
		countLbl.setForeground(Color.WHITE);

		this.setLayout(new GridBagLayout());

		GridBagConstraints bc = new GridBagConstraints();
		bc.gridx = 0;
		bc.gridy = 0;
		this.add(countLbl, bc);

		bc = new GridBagConstraints();
		bc.gridx = 0;
		bc.gridy = 1;
		this.add(txtLbl, bc);
	}

	public void setCount(String count)
	{
		countLbl.setText(count);
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(new AlertComponent("ppxxtt"));
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

	public CircleLable()
	{
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

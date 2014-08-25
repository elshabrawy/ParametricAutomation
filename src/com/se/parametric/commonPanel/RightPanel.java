package com.se.parametric.commonPanel;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RightPanel extends JPanel{

    private BufferedImage image;

    public RightPanel(String path) {
    	ImageIcon image = new ImageIcon(path);
    	JLabel label = new JLabel("", image, JLabel.CENTER);
    	JPanel panel = new JPanel(new BorderLayout());
    	panel.add(label);
    }
}

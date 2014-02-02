package com.se.parametric.dev;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.se.parametric.commonPanel.TablePanel;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.GrmUserDTO;
import com.toedter.calendar.JDateChooser;

public class ComponentExporterPanel extends JPanel implements ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GrmUserDTO userDto;
	private int width, height;
	JCheckBox checkDate;
	JDateChooser jDateChooser1, jDateChooser2;
	JButton exportBtn;
	JButton refreshBtn;
	JComboBox<String> userPLS;

	public ComponentExporterPanel(GrmUserDTO userDto)
	{
		this.userDto = userDto;
		width = Toolkit.getDefaultToolkit().getScreenSize().width;
		height = Toolkit.getDefaultToolkit().getScreenSize().height;
		setSize(width, height);
		setLayout(null);
		JPanel datePanel = new JPanel();
		// date panel actual width = 677
		int dateX = (int) (width - 677) / 2;
		datePanel.setBackground(new Color(255, 240, 245));
		datePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		datePanel.setBounds(0, 0, width, 60);
		add(datePanel);
		jDateChooser1 = new JDateChooser();
		jDateChooser2 = new JDateChooser();
		jDateChooser1.setBounds(dateX + 114, 21, 91, 20);
		jDateChooser1.setDate(new java.util.Date());
		jDateChooser2.setBounds(dateX + 355, 21, 91, 20);
		jDateChooser2.setDate(new java.util.Date());
		datePanel.setLayout(null);
		jDateChooser1.setEnabled(false);
		jDateChooser2.setEnabled(false);
		datePanel.add(jDateChooser1);
		datePanel.add(jDateChooser2);
		JLabel lblNewLabel = new JLabel("From : ");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel.setBounds(dateX, 27, 73, 14);
		datePanel.add(lblNewLabel);
		JLabel lblNewLabel_1 = new JLabel("To : ");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setBounds(dateX + 261, 27, 46, 14);
		datePanel.add(lblNewLabel_1);
		checkDate = new JCheckBox();
		checkDate = new JCheckBox("Select Period");
		checkDate.setFont(new Font("Tahoma", Font.BOLD, 11));
		checkDate.setBounds(dateX + 560, 18, 117, 23);
		checkDate.addActionListener(this);
		datePanel.add(checkDate);

		JLabel label = new JLabel("Export Parts Approved By Team Leader");
		label.setForeground(new Color(25, 25, 112));
		label.setFont(new Font("Tahoma", Font.BOLD, 14));
		label.setBounds((width - 280) / 2, 140, 280, 25);

		int plX = (int) (width - 340) / 2;

		JLabel choosePlLabel = new JLabel("Please Select PL : ");
		choosePlLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		choosePlLabel.setBounds(plX, 220, 120, 30);

		userPLS = new JComboBox<String>();
		userPLS.setFont(new Font("Tahoma", Font.BOLD, 11));
		ComboBoxModel<String> model = getPLComboModel(null, null);
		userPLS.setModel(model);
		userPLS.setBounds(plX + 140, 220, 200, 30);

		exportBtn = new JButton("Export");
		int exportX = (int) (width - 90) / 2;
		exportBtn.setForeground(new Color(25, 25, 112));
		exportBtn.setFont(new Font("Tahoma", Font.BOLD, 11));
		exportBtn.setBounds(exportX, 290, 90, 30);
		// exportBtn.setIcon(new ImageIcon(ComponentExporterPanel.class.getResource("/Resources/icon-export.png")));
		exportBtn.addActionListener(this);

		refreshBtn = new JButton("Refresh");
		refreshBtn.setForeground(new Color(25, 25, 112));
		refreshBtn.setFont(new Font("Tahoma", Font.BOLD, 11));
		refreshBtn.setBounds(exportX, 325, 90, 30);
		refreshBtn.addActionListener(this);

		add(label);
		add(datePanel);
		add(choosePlLabel);
		add(userPLS);
		add(exportBtn);
		add(refreshBtn);

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == checkDate)
		{
			// if ( jDateChooser1.isEnabled() ) {
			if(checkDate.isSelected())
			{
				jDateChooser1.setEnabled(true);
				jDateChooser2.setEnabled(true);
			}
			else
			{
				jDateChooser1.setEnabled(false);
				jDateChooser2.setEnabled(false);
			}

		}
		else if(e.getSource() == exportBtn)
		{
			Date startDate = null;
			Date endDate = null;
			if(checkDate.isSelected())
			{
				startDate = jDateChooser1.getDate();
				endDate = jDateChooser2.getDate();
			}
			String plName = userPLS.getSelectedItem().toString();
			System.out.println(plName);
			ParaQueryUtil.exportParts(plName, userDto, startDate, endDate);
			removePl(plName);
			JOptionPane.showMessageDialog(null, "Done.\nCheck Export File(s) at C:\\Reports\\AutomationReports", "Exported", JOptionPane.INFORMATION_MESSAGE);
		}
		else if(e.getSource() == refreshBtn)
		{
			Date startDate = null;
			Date endDate = null;
			if(checkDate.isSelected())
			{
				startDate = jDateChooser1.getDate();
				endDate = jDateChooser2.getDate();
			}
			ComboBoxModel<String> model = getPLComboModel(startDate, endDate);
			userPLS.setModel(model);
		}
	}

	public void removePl(String plName)
	{
		DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) userPLS.getModel();
		model.removeElement(plName);
	}

	public ComboBoxModel<String> getPLComboModel(Date startDate, Date endDate)
	{
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		List<String> plNames = ParaQueryUtil.getEngExportablePLNames(userDto.getId(), startDate, endDate);
		for(int i = 0; i < plNames.size(); i++)
		{
			model.addElement(plNames.get(i).toString());
		}
		return model;
	}

	public static void main(String[] args)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height - 50;
		frame.setSize(width, height);
		frame.setTitle("Export");
		GrmUserDTO uDTO = new GrmUserDTO();
		uDTO.setId(370);
		uDTO.setFullName("karim essam");
		uDTO.setEmail("ahmad_rahim@siliconexpert.com");
		// uDTO.setId(117);
		// uDTO.setFullName("abeer");
		// uDTO.setEmail("abeer@siliconexpert.com");
		// uDTO.setId(123);
		// uDTO.setFullName("ahmed_adel");
		// uDTO.setEmail("ahmed_adel@siliconexpert.com");
		ComponentExporterPanel fbPanel = new ComponentExporterPanel(uDTO);
		frame.getContentPane().add(fbPanel);
		frame.show();
	}

}

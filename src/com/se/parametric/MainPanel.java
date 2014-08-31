package com.se.parametric;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.se.Quality.QAChecks;
import com.se.Quality.QAException;
import com.se.Quality.QAFeedBack;
import com.se.Quality.QAReviewData;
import com.se.Quality.QualityUnApprovedValue;
import com.se.parametric.dev.ComponentExporterPanel;
import com.se.parametric.dev.Developement;
import com.se.parametric.dev.Update;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.fb.EngFeedBack;
import com.se.parametric.fb.ExceptionFB;
import com.se.parametric.fb.TLFeedBack;
import com.se.parametric.review.TLReviewData;
import com.se.parametric.unappValue.EngUnApprovedValueFeedback;
import com.se.parametric.unappValue.TLUnApprovedValue;

public class MainPanel extends JPanel implements ActionListener
{

	/**
	 * Create the panel.
	 */
	EngFeedBack engfeedBack;
	TLFeedBack tlfeedBack;
	TLUnApprovedValue tlunApprovedPanel = null;
	EngUnApprovedValueFeedback engunApprovedPanel = null;
	TLReviewData reviewData;
	Developement developement;
	QAReviewData qaReviewData;
	QAFeedBack qaFeedBack;
	QualityUnApprovedValue qUnApproved;
	GrmUserDTO userDTO;
	QAException qaexception;
	ExceptionFB exceptionfb;
	QAChecks qachecks;
	Update update;
	ComponentExporterPanel exportPanel;
	JButton engfeedBackBu;
	JButton tlfeedBackBu;
	JButton tlunApprovedBu;
	JButton engunApprovedBu;
	JButton tlreviewDataBu;
	JButton developementBu;
	JButton qaReviewDataBu;
	JButton qaFeedBackBu;
	JButton qUnApprovedBu;
	JButton qaexceptionBu;
	JButton exceptionfbBu;
	JButton qachecksBu;
	JButton updateBu;
	JButton exportPanelBu;
	// static int width;
	// static int height;
	// JPanel mainpnl;
	// JPanel tabspanel;
	static JTabbedPane tabbedPane;
	List<JButton> buttonlist;
	List<String> iconsurl;
	JToolBar toolBar;
	static JPopupMenu menu;

	public MainPanel(GrmUserDTO userDTO)
	{
		this.userDTO = userDTO;
		// setLayout(null);
		BorderLayout borderLayout = new BorderLayout();
		this.setLayout(borderLayout);
		long userRole = userDTO.getGrmRole().getId();
		long userGroup = userDTO.getGrmGroup().getId();
		// this.setBounds(0, 0, width, height);

		// mainpnl = new JPanel();
		// mainpnl.setLayout(null);
		// mainpnl.setBorder(BorderFactory.createEmptyBorder());
		// mainpnl.setBounds(0, 5, width - 10, height - 930);

		// tabspanel = new JPanel();
		// tabspanel.setLayout(null);
		// tabspanel.setBorder(BorderFactory.createEmptyBorder());
		// tabspanel.setBounds(0, height - 948, width - 10, height - 30);

		tabbedPane = new JTabbedPane();
		tabbedPane.addMouseListener(new PopupListener());
		createMenu();
		// tabbedPane.setBounds(0, height - 980, width - 10, height - 30);
		// tabbedPane.setBorder(BorderFactory.createEmptyBorder());
		// tabspanel.add(tabbedPane);

		toolBar = new JToolBar("");
		toolBar.setFloatable(false);
		// toolBar.setBounds(0, 0, width - 5, height - 935);

		// role 3 eng , role 1 tl
		if(userRole == 1 && userGroup == 1)
		{
			int wstart = 10;
			int Bwidth = 170;
			buttonlist = new ArrayList<>();
			iconsurl = new ArrayList<>();

			tlreviewDataBu = new JButton("TL Data Review");
			iconsurl.add("/Resources/reviews.jpg");
			tlfeedBackBu = new JButton("TL Data FeedBack");
			iconsurl.add("/Resources/feedback-icon.png");
			tlunApprovedBu = new JButton("UnApproved Value");
			iconsurl.add("/Resources/approved.png");
			buttonlist.add(tlreviewDataBu);
			buttonlist.add(tlfeedBackBu);
			buttonlist.add(tlunApprovedBu);
			drawtoolbar(buttonlist, iconsurl, wstart, Bwidth);
		}
		else if(userRole == 3 && userGroup == 1)
		{
			// parametric eng
			int wstart = 10;
			int Bwidth = 170;
			buttonlist = new ArrayList<>();
			iconsurl = new ArrayList<>();
			developementBu = new JButton("Developement");
			iconsurl.add("/Resources/development-icon.png");
			updateBu = new JButton("NPI Update");
			iconsurl.add("/Resources/update-icon.png");
			engfeedBackBu = new JButton("Eng Data Feedback");
			iconsurl.add("/Resources/feedback-icon.png");
			engunApprovedBu = new JButton("Eng UnApproved Value");
			iconsurl.add("/Resources/approved.png");
			qachecksBu = new JButton("QA Checks");
			iconsurl.add("/Resources/check-icon.png");
			exceptionfbBu = new JButton("QA ExceptionFeedback");
			iconsurl.add("/Resources/exception.png");
			exportPanelBu = new JButton("Export");
			iconsurl.add("/Resources/export.png");

			buttonlist.add(developementBu);
			buttonlist.add(updateBu);
			buttonlist.add(engfeedBackBu);
			buttonlist.add(engunApprovedBu);
			buttonlist.add(qachecksBu);
			buttonlist.add(exceptionfbBu);
			buttonlist.add(exportPanelBu);

			drawtoolbar(buttonlist, iconsurl, wstart, Bwidth);
		}
		else if(userRole == 3 && userGroup == 23)
		{
			// Q eng
			// System.out.println("Quality Eng");
			// tlfeedBack = new TLFeedBack(userDTO);
			//
			// qaReviewData = new QAReviewData(userDTO);
			// qaFeedBack = new QAFeedBack(userDTO);
			// qUnApproved = new QualityUnApprovedValue(userDTO);
			// qaexception = new QAException(userDTO);
			//
			// tabbedPane.addTab("Quality Data Review", null, qaReviewData, null);
			// tabbedPane.addTab("Quality Feedback", null, qaFeedBack, null);
			// tabbedPane.addTab("Quality UnApproved", null, qUnApproved, null);
			// tabbedPane.addTab("Quality Exception", null, qaexception, null);
			int wstart = 10;
			int Bwidth = 170;
			buttonlist = new ArrayList<>();
			iconsurl = new ArrayList<>();

			qaReviewDataBu = new JButton("Quality Data Review");
			iconsurl.add("/Resources/reviews.jpg");
			qaFeedBackBu = new JButton("Quality Feedback");
			iconsurl.add("/Resources/feedback-icon.png");
			qUnApprovedBu = new JButton("Quality UnApproved");
			iconsurl.add("/Resources/approved.png");
			qaexceptionBu = new JButton("Quality Exception");
			iconsurl.add("/Resources/exception.png");
			buttonlist.add(qaReviewDataBu);
			buttonlist.add(qaFeedBackBu);
			buttonlist.add(qUnApprovedBu);
			buttonlist.add(qaexceptionBu);
			drawtoolbar(buttonlist, iconsurl, wstart, Bwidth);
		}
		add(toolBar, BorderLayout.PAGE_START);
		// add(mainpnl);
		add(tabbedPane, BorderLayout.CENTER);
		this.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0)
			{
			}

			@Override
			public void focusGained(FocusEvent arg0)
			{
				if(null != tabbedPane.getSelectedComponent())
				{
					tabbedPane.getSelectedComponent().requestFocusInWindow();
				}
			}
		});
	}

	private void drawtoolbar(List<JButton> buttons, List<String> iconsurl, int wstart, int Bwidth)
	{
		try
		{
			Image img;
			toolBar.addSeparator();
			for(int i = 0; i < buttons.size(); i++)
			{
				buttons.get(i).setBounds(wstart + ((i + 1) * Bwidth), 5, Bwidth - 5, 32);
				buttons.get(i).setForeground(new Color(25, 25, 112));
				// tlunApprovedBu.setBackground(new Color(255, 255, 255));
				buttons.get(i).setFont(new Font("Herman", Font.PLAIN, 11));
				buttons.get(i).addActionListener(this);
				buttons.get(i).setToolTipText(buttons.get(i).getText());
				// buttons.get(i).setOpaque(false);
				// buttons.get(i).setBorder(BorderFactory.createEmptyBorder());
				buttons.get(i).setVerticalTextPosition(SwingConstants.BOTTOM);
				buttons.get(i).setHorizontalTextPosition(SwingConstants.CENTER);

				img = ImageIO.read(getClass().getResource(iconsurl.get(i)));
				buttons.get(i).setIcon(new ImageIcon(img));
				buttons.get(i).setIconTextGap(5);
				toolBar.add(buttons.get(i));
				toolBar.addSeparator();
			}
		}catch(Exception e)
		{
			// System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void updateFlags()
	{
		// TL Screens
		System.out.println("here");
		if(tlfeedBack != null)
			tlfeedBack.updateFlags();
		if(reviewData != null)
			reviewData.updateFlags();
		if(tlunApprovedPanel != null)
			tlunApprovedPanel.updateFlags();
		// Eng Screens
		if(engfeedBack != null)
			engfeedBack.updateFlags();
		if(engunApprovedPanel != null)
			engunApprovedPanel.updateFlags();
		if(developement != null)
			developement.updateFlags();
		if(qachecks != null)
			qachecks.updateFlags();
		if(exceptionfb != null)
			exceptionfb.updateFlags();
		// QA Screens
		if(qaReviewData != null)
			qaReviewData.updateFlags();
		if(qaFeedBack != null)
			qaFeedBack.updateFlags();
		if(qUnApproved != null)
			qUnApproved.updateFlags();
		if(qaexception != null)
			qaexception.updateFlags();

	}

	public void clearOfficeResources()
	{
		// TL Screens
		if(tlfeedBack != null)
			tlfeedBack.clearOfficeResources();
		if(reviewData != null)
			reviewData.clearOfficeResources();
		if(tlunApprovedPanel != null)
			tlunApprovedPanel.clearOfficeResources();
		// Eng Screens
		if(engfeedBack != null)
			engfeedBack.clearOfficeResources();
		if(engunApprovedPanel != null)
			engunApprovedPanel.clearOfficeResources();
		if(developement != null)
			developement.clearOfficeResources();
		if(qachecks != null)
			qachecks.clearOfficeResources();
		if(exceptionfb != null)
			exceptionfb.clearOfficeResources();
		// QA Screens
		if(qaReviewData != null)
			qaReviewData.clearOfficeResources();
		if(qaFeedBack != null)
			qaFeedBack.clearOfficeResources();
		if(qUnApproved != null)
			qUnApproved.clearOfficeResources();
		if(qaexception != null)
			qaexception.clearOfficeResources();
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		LongRunProcess longRunProcess = new LongRunProcess(event);
		longRunProcess.execute();
	}

	class LongRunProcess extends SwingWorker<Object, Object>
	{
		ActionEvent event = null;

		LongRunProcess(ActionEvent event)
		{
			this.event = event;
		}

		@Override
		protected Object doInBackground() throws Exception
		{
			MainWindow.glass.setVisible(true);
			if(event.getSource() == tlfeedBackBu)
			{
				if(tabbedPane.indexOfComponent(tlfeedBack) == -1)
				{

					tlfeedBack = new TLFeedBack(userDTO);
					tabbedPane.addTab("TL Feedback", tlfeedBack);
					int index = tabbedPane.indexOfComponent(tlfeedBack);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			if(event.getSource() == tlreviewDataBu)
			{
				if(tabbedPane.indexOfComponent(reviewData) == -1)
				{

					reviewData = new TLReviewData(userDTO);
					tabbedPane.addTab("TL Review", reviewData);
					int index = tabbedPane.indexOfComponent(reviewData);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			if(event.getSource() == tlunApprovedBu)
			{
				if(tabbedPane.indexOfComponent(tlunApprovedPanel) == -1)
				{

					tlunApprovedPanel = new TLUnApprovedValue(userDTO);
					tabbedPane.addTab("TL UnApproved Value", tlunApprovedPanel);
					int index = tabbedPane.indexOfComponent(tlunApprovedPanel);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}

			if(event.getSource() == developementBu)
			{
				if(tabbedPane.indexOfComponent(developement) == -1)
				{

					developement = new Developement(userDTO);
					tabbedPane.addTab("Development", developement);
					int index = tabbedPane.indexOfComponent(developement);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			if(event.getSource() == updateBu)
			{
				if(tabbedPane.indexOfComponent(update) == -1)
				{

					update = new Update(userDTO);
					tabbedPane.addTab("NPI Update", update);
					int index = tabbedPane.indexOfComponent(update);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			if(event.getSource() == engfeedBackBu)
			{
				if(tabbedPane.indexOfComponent(engfeedBack) == -1)
				{

					engfeedBack = new EngFeedBack(userDTO);
					tabbedPane.addTab("Eng Data Feedback", engfeedBack);
					int index = tabbedPane.indexOfComponent(engfeedBack);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			if(event.getSource() == engunApprovedBu)
			{
				if(tabbedPane.indexOfComponent(engunApprovedPanel) == -1)
				{

					engunApprovedPanel = new EngUnApprovedValueFeedback(userDTO);
					tabbedPane.addTab("Eng UnApproved Value", engunApprovedPanel);
					int index = tabbedPane.indexOfComponent(engunApprovedPanel);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			if(event.getSource() == qachecksBu)
			{
				if(tabbedPane.indexOfComponent(qachecks) == -1)
				{

					qachecks = new QAChecks(userDTO);
					tabbedPane.addTab("QA Checks", qachecks);
					int index = tabbedPane.indexOfComponent(qachecks);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			if(event.getSource() == exceptionfbBu)
			{
				if(tabbedPane.indexOfComponent(exceptionfb) == -1)
				{

					exceptionfb = new ExceptionFB(userDTO);
					tabbedPane.addTab("QA ExceptionFeedback", exceptionfb);
					int index = tabbedPane.indexOfComponent(exceptionfb);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			if(event.getSource() == exportPanelBu)
			{
				if(tabbedPane.indexOfComponent(exportPanel) == -1)
				{

					exportPanel = new ComponentExporterPanel(userDTO);
					tabbedPane.addTab("QA Checks", exportPanel);
					int index = tabbedPane.indexOfComponent(exportPanel);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			if(event.getSource() == qaReviewDataBu)
			{
				if(tabbedPane.indexOfComponent(qaReviewData) == -1)
				{

					qaReviewData = new QAReviewData(userDTO);
					tabbedPane.addTab("Quality Data Review", qaReviewData);
					int index = tabbedPane.indexOfComponent(qaReviewData);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			if(event.getSource() == qaFeedBackBu)
			{
				if(tabbedPane.indexOfComponent(qaFeedBack) == -1)
				{

					qaFeedBack = new QAFeedBack(userDTO);
					tabbedPane.addTab("Quality Feedback", qaFeedBack);
					int index = tabbedPane.indexOfComponent(qaFeedBack);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			if(event.getSource() == qUnApprovedBu)
			{
				if(tabbedPane.indexOfComponent(qUnApproved) == -1)
				{

					qUnApproved = new QualityUnApprovedValue(userDTO);
					tabbedPane.addTab("Quality UnApproved", qUnApproved);
					int index = tabbedPane.indexOfComponent(qUnApproved);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			if(event.getSource() == qaexceptionBu)
			{
				if(tabbedPane.indexOfComponent(qaexception) == -1)
				{

					qaexception = new QAException(userDTO);
					tabbedPane.addTab("Quality Exception", qaexception);
					int index = tabbedPane.indexOfComponent(qaexception);
					tabbedPane.setTabComponentAt(index, new ButtonTabComponent(tabbedPane));
					tabbedPane.setSelectedIndex(index);
				}
			}
			MainWindow.glass.setVisible(false);
			return null;
		}
	}

	private static JMenuItem createMenuItem(String s)
	{
		JMenuItem item = new JMenuItem(s);
		item.setActionCommand(s);
		item.addActionListener(listener);
		return item;
	}

	private static void createMenu()
	{
		menu = new JPopupMenu();
		menu.add(createMenuItem("Close"));
		menu.add(createMenuItem("CloseAll"));
		menu.add(createMenuItem("CloseOther"));
	}

	private static Action listener = new AbstractAction() {

		public void actionPerformed(ActionEvent e)
		{
			JMenuItem item = (JMenuItem) e.getSource();
			String ac = item.getActionCommand();
			int remIndex = tabbedPane.getSelectedIndex();
			if(ac.equals("Close"))
			{
				if(remIndex < 0)
				{
					// JOptionPane.showMessageDialog(rootPane, "No tab available to close");
					System.err.println("there is no tab to close");
				}
				else
				{
					System.err.println("close tab no :" + remIndex);
					tabbedPane.remove(remIndex);
					tabbedPane.revalidate();
				}
			}
			if(ac.equals("CloseAll"))
			{
				System.err.println("close All");
				tabbedPane.removeAll();
				tabbedPane.revalidate();
			}
			if(ac.equals("CloseOther"))
			{
				System.err.println("close all unless tab no :" + remIndex);
				int count = tabbedPane.getComponentCount();
				for(int i = 0; i < count - 1; i++)
				{
					if(tabbedPane.getComponentAt(i) != tabbedPane.getComponentAt(remIndex))
					{
						tabbedPane.remove(i);
					}
				}
				tabbedPane.revalidate();
			}

		}
	};

	private static class PopupListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			checkForPopup(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			checkForPopup(e);
		}

		public void mouseClicked(MouseEvent e)
		{
			checkForPopup(e);
		}

		private void checkForPopup(MouseEvent e)
		{
			if(e.isPopupTrigger())
			{
				Component c = e.getComponent();
				menu.show(c, e.getX(), e.getY());
			}
		}
	}
}

package com.se.parametric.dev;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.StringType;

import com.se.automation.db.SessionUtil;
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.parametric.StatusName;
import com.se.parametric.commonPanel.TablePanel;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.FeatureDTO;
import com.se.parametric.dto.GrmUserDTO;
import com.se.parametric.excel.ExcelHandler2003;
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
			exportParts(plName, userDto, startDate, endDate);
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
		uDTO.setId(128);
		uDTO.setFullName("amr_elshaer");
		uDTO.setEmail("amr_elshaer@siliconexpert.com");
		// uDTO.setId(117);
		// uDTO.setFullName("abeer");
		// uDTO.setEmail("abeer@siliconexpert.com");
		// uDTO.setId(123);
		// uDTO.setFullName("ahmed_adel");
		// uDTO.setEmail("ahmed_adel@siliconexpert.com");
		ComponentExporterPanel fbPanel = new ComponentExporterPanel(uDTO);
		List<String> plList=new ArrayList<String>();
		plList.add("Cables");
		plList.add("Fittings");
		plList.add("Fittings");
		plList.add("Tapes");
		plList.add("Epoxy Adhesives");
		plList.add("Misc Products");
		for(int j = 0; j < plList.size(); j++)
		{
			
		exportParts(plList.get(j),uDTO,null,null);
		}
		System.out.println("Finished");
//		frame.getContentPane().add(fbPanel);
//		frame.show();
	}
	
	
	public static void exportParts(String plName, GrmUserDTO userDto, Date startDate, Date endDate)
	{
		ExcelHandler2003 xlsHandler = null;
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
					
			Pl pl = ParaQueryUtil.getPlByPlName(session,plName );
			Pl plTypeObj = ParaQueryUtil.getPLType(pl);
			List<String> headerList = new ArrayList<String>();
			headerList.add("PRODUCT_NAME");
			headerList.add("PRODUCT_EXTERNAL_DATASHEET");
			headerList.add("Description");
			headerList.add("Family");
			if("Semiconductor".equalsIgnoreCase(plTypeObj.getName()))
			{
				headerList.add("Family Cross");
				headerList.add("Generic");
			}

			headerList.add("Mask");
			headerList.add("Vendor");
			headerList.add("Vendor Code");
			headerList.add("ROHS");
			headerList.add("Supplier Package");
			headerList.add("Pin Count");
			headerList.add("Life Cycle");

			List<FeatureDTO> fets = ParaQueryUtil.getPlFeautres(pl, true);
			for(FeatureDTO fet : fets)
			{
				headerList.add(fet.getFeatureName());
			}
			List<Map<String, Object>> components = new ArrayList<Map<String, Object>>();
			String queryString = "select com_id from part_component where document_id in (select distinct T.DOCUMENT_ID from PARAMETRIC_REVIEW_DATA d,TRACKING_PARAMETRIC t where t.id=D.TRACKING_PARAMETRIC_ID and T.TRACKING_TASK_STATUS_ID=32 and not exists (select 1 from parts_parametric p where p.com_id=d.com_id)) "
//					+ "and pl_id="
//					+ pl.getId()
					+ " and supplier_pl_id in ( select id from supplier_pl where pl_id="
					+ pl.getId() + ")";
//			if((startDate != null) && (endDate != null))
//			{
//				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy HH:mm:ss");
//				queryString += " AND STORE_DATE between TO_DATE('" + sdf.format(startDate)
//						+ "', 'MM/DD/YYYY HH24:MI:SS') and TO_DATE('" + sdf.format(endDate)
//						+ "', 'MM/DD/YYYY HH24:MI:SS')";
//			}

			SQLQuery query = session.createSQLQuery(queryString);
			List<BigDecimal> comIds = query.list();
			for(int i = 0; i < comIds.size(); i++)
			{
				Map<String, Object> fetsMap = new HashMap<String, Object>();
				long comId = comIds.get(i).longValue();
				query = session
						.createSQLQuery("select c.part_number, GET_PDF_SEURL(GET_PDFIDBYDOCID(c.document_id)), "
								+ " f.name family_name, c.description, Get_GENERIC_Name (C.GENERIC_ID) generic_Nam, GET_MSK_Value (c.MASK_ID, C.PART_NUMBER) MASK, s.name vendor_name, S.CODE, Get_family_crossName (C.FAMILY_CROSS_ID) family_Cross "
								+ " from part_component c, family f, supplier_pl spl, supplier s where c.com_id="
								+ comId
								+ " and c.family_id=f.id(+) and c.supplier_pl_id=spl.id and spl.supplier_id=s.id");

				Object[] mainFets = (Object[]) query.uniqueResult();
				fetsMap.put("PRODUCT_NAME", mainFets[0]);
				fetsMap.put("PRODUCT_EXTERNAL_DATASHEET", mainFets[1]);
				fetsMap.put("Family", mainFets[2]);
				fetsMap.put("Description", mainFets[3]);
				fetsMap.put("Generic", mainFets[4]);
				fetsMap.put("Mask", mainFets[5]);
				fetsMap.put("Vendor", mainFets[6]);
				fetsMap.put("Vendor Code", mainFets[7]);
				fetsMap.put("Family Cross", mainFets[8]);
				fetsMap.put("ROHS", "");
				fetsMap.put("Supplier Package", "");
				fetsMap.put("Pin Count", "");
				fetsMap.put("Life Cycle", "");

				query = session
						.createSQLQuery("select fet.name fet_name, g.group_full_value from parametric_review_data review,"
								+ " pl_feature_unit plFet, feature fet, PARAMETRIC_APPROVED_GROUP g where review.com_id="
								+ comId
								+ " and review.pl_feature_id=plfet.id and plfet.fet_id=fet.id and review.group_approved_value_id=g.id(+)");
				List<Object[]> paramFets = query.list();
				for(int k = 0; k < paramFets.size(); k++)
				{
					Object[] paramFet = paramFets.get(k);
					String fetName = paramFet[0].toString();
					Object fetVal = paramFet[1];
					fetsMap.put(fetName, fetVal);
				}
				components.add(fetsMap);

			}

			// System.out.println(components);
			xlsHandler = new ExcelHandler2003();
			String fileName = plName;
			fileName = fileName.replaceAll("/", "$");
			String userEmail = userDto.getEmail();
			userEmail = userEmail.substring(0, userEmail.indexOf('@'));
			fileName += "@" + userEmail;
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			fileName += "@" + sdf.format(new Date()) + "@Insert";
			fileName += ".xls";

			xlsHandler.writeExcelFile(headerList.toArray(new String[headerList.size()]),
					components, fileName);
			exportNPIParts(plName, userDto, startDate, endDate);
//			query = session
//					.createSQLQuery("update tracking_parametric set tracking_task_status_id=GETTASKSTATUSID('"
//							+ StatusName.finshed
//							+ "') "
//							+ " where user_id="
//							+ userDto.getId()
//							+ " and tracking_task_status_id=GETTASKSTATUSID('"
//							+ StatusName.cmTransfere + "') and pl_id=GETPLID('" + plName + "')");
//			// Transaction tx = session.beginTransaction();
//			int x = query.executeUpdate();
//			// tx.commit();
		
		}catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error", "Error!", JOptionPane.ERROR_MESSAGE);
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
	}
	
	public static void exportNPIParts(String plName, GrmUserDTO userDto, Date startDate,
			Date endDate)
	{
		Session session = null;
		ExcelHandler2003 xlsHandler = null;
		try
		{
			session = SessionUtil.getSession();
			Pl pl = ParaQueryUtil.getPlByPlName(session, plName);
			String queryString = "select '"
					+ userDto.getFullName()
					+ "' eng_name, c.part_number, GETSUPPLIERBYDOC(c.document_id) sup_name, "
					+ " GETPDFURLBYDOCID(document_id) pdf_url, GETNPINewsPDFURL (c.DOCUMENT_ID) news_link from part_component c where npi_flag=1 and document_id "
					+ " in (select document_id from tracking_parametric where user_id="
					+ userDto.getId() + " and tracking_task_status_id=32 and pl_id=" + pl.getId()
					+ ") " + " and supplier_pl_id in (select id from supplier_pl where pl_id="
					+ pl.getId() + ")";
			if((startDate != null) && (endDate != null))
			{
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy HH:mm:ss");
				queryString += " AND STORE_DATE between TO_DATE('" + sdf.format(startDate)
						+ "', 'MM/DD/YYYY HH24:MI:SS') and TO_DATE('" + sdf.format(endDate)
						+ "', 'MM/DD/YYYY HH24:MI:SS')";
			}
			SQLQuery query = session.createSQLQuery(queryString);
			query.addScalar("eng_name", StringType.INSTANCE);
			query.addScalar("part_number", StringType.INSTANCE);
			query.addScalar("sup_name", StringType.INSTANCE);
			query.addScalar("pdf_url", StringType.INSTANCE);
			query.addScalar("news_link", StringType.INSTANCE);
			List<Object[]> npiComponents = query.list();
			if(npiComponents.size() > 0)
			{
				xlsHandler = new ExcelHandler2003();
				String[] header = { "Eng Name", "Part Number", "Vendor Name", "Offline Datasheet",
						"News Link" };
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
				String fileName = userDto.getFullName() + "@" + formatter.format(new Date())
						+ "@NPI.xls";
				xlsHandler.writeExcelFile(header, new ArrayList<Object[]>(npiComponents), fileName);

			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if((session != null) && (session.isOpen()))
			{
				session.close();
			}
		}
	}

}

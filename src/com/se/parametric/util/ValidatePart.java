package com.se.parametric.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.se.automation.db.SessionUtil;
import com.se.automation.db.client.mapping.Document;
import com.se.automation.db.client.mapping.PartComponent;
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.client.mapping.QaChecksInDependentFeature;
import com.se.automation.db.client.mapping.Supplier;
import com.se.automation.db.client.mapping.SupplierPl;
import com.se.parametric.dba.DataDevQueryUtil;
import com.se.parametric.dba.ParaQueryUtil;
import com.se.parametric.dto.FeatureDTO;
import com.se.parametric.dto.RelatedFeaturesDTO;

public class ValidatePart extends ValidationsCommentsStatus
{

	private List<String> parts = new ArrayList<String>();
	private Document doc;
	private ClientUtil clientutil;

	public ValidatePart(Document doc, ClientUtil clintutil)
	{
		this.doc = doc;
		this.clientutil = clintutil;
	}

	public boolean isAlphaPartNumberFoundOnLUT(String NAN_INPUT_PART, String SE_Man_ID) throws Exception
	{
		if(NAN_INPUT_PART == null || NAN_INPUT_PART.isEmpty() || SE_Man_ID == null)
			throw new Exception("Null Input");
		// ToDo Write the business that need to connect with DB on CM to check on LUT table
		refresh();
		if(ParaQueryUtil.getCM_Part_Lookup_TBL(NAN_INPUT_PART, SE_Man_ID) != null)
		{
			Status = "Reject, Found on LUT Table";
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean isAlphaPartNumberFoundOnAcquisitionTBL(String NAN_INPUT_PART, String SE_Man_ID) throws Exception
	{
		if(NAN_INPUT_PART == null || NAN_INPUT_PART.isEmpty() || SE_Man_ID == null)
			throw new Exception("Null Input");

		refresh();
		if(ParaQueryUtil.getCM_Acquisition_TBL(NAN_INPUT_PART, SE_Man_ID) != null)
		{
			Status = "Reject, Found on Acquisition Table";
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean isAlphaPartNumnerFoundOnComponentTBL(String NAN_INPUT_PART, Supplier SE_Man_ID) throws Exception
	{
		if(NAN_INPUT_PART == null || NAN_INPUT_PART.isEmpty() || SE_Man_ID == null)
			throw new Exception("Null Input");

		refresh();
		PartComponent component = DataDevQueryUtil.getComponentByPartNumAndSupplier(NAN_INPUT_PART, SE_Man_ID);

		if(component != null)
		{
			// if(doc.getId()== component.getDocument().getId())
			// {
			// return false;
			// }
			// else
			// {
			Status = "Reject, Found Before";
			Comment = component.getDocument().getPdf().getSeUrl();
			Taxonomy = component.getSupplierPl().getPl().getName();
			return true;
			// }
		}
		else
		{
			return false;
		}
	}

	public boolean isPNSupFoundOnComponentTBL(String pn, String supplierName) throws Exception
	{

		boolean found = false;
		try
		{
			refresh();
			// Component component = ParaQueryUtil.getComponentByPartNumberAndSupplierName(pn, supplierName, session);
			// if(component!= null)
			// {
			// Status = "Reject, Found Before";
			// Comment =component.getDocument().getPdf().getSeUrl();
			// Taxonomy = component.getSupplierPl().getPl().getName();
			// found = true;
			// }

			Object[] comp = ParaQueryUtil.getNANAlphaFromComponentTBL(pn, supplierName);
			if(comp != null)
			{
				Status = "Reject, Found Before";
				Comment = (String) comp[2];
				Taxonomy = (String) comp[1];
				found = true;
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		return found;
	}

	public boolean isSpecialCharactarFound(String inputPart) throws Exception
	{
		boolean dissension = false;
		if(inputPart == null || inputPart.isEmpty())
			throw new Exception("Wrong NAN_INPUT_PART CASE");

		try
		{
			refresh();
			// Â¿[ ?Î¼
			// Contains Large Dash â€œâ€“â€œ
			System.out.println("inserted part\t" + inputPart);
			if(inputPart.contains("â") || inputPart.contains("€") || inputPart.contains("œ") || inputPart.contains("�") || inputPart.contains("Î"))
			{
				Comment = "Contains UnExpected Character";
				return dissension = true;
			}
			else
			// if( CharFound('–', NAN_INPUT_PART))
			// {
			// Comment = "Contains Large Dash  –";
			// return dissension =true;
			// }
			// Start with Space
			// else
			if(inputPart.startsWith(" "))
			{
				Comment = "Start with Space";
				return dissension = true;
			}
			// End with Space
			else if(inputPart.endsWith(" "))
			{
				Comment = "End with Space";
				return dissension = true;
			}
			// Contains multi Space
			else if(inputPart.matches(".*\\s{2,}.*"))
			{
				Comment = "Contains multi Space";
				return dissension = true;
			}
			// Start with this character â€œ â€� , This is not space
			else if(CharFound("  ", inputPart) || inputPart.startsWith("â€œ   ") || inputPart.startsWith("â"))
			{
				Comment = "contains character notspace";
				return dissension = true;
			}
			// End with this character â€œ â€œ , This is not space
			else if(CharFound("  ", inputPart))
			{
				Comment = "End with this character  notspace";
				return dissension = true;
			}
			// Contains Character â€œÎ¼â€� , This is not â€œMâ€�
			// else if(CharFound('Μ', NAN_INPUT_PART))
			// {
			// Comment ="Contains Character Μ";
			// return dissension =true;
			// }
			else if(inputPart.contains("μ"))
			{
				Comment = "Contains Character μ";
				return dissension = true;
			}
			else if(inputPart.contains("?"))
			{
				Comment = "Contains Character ?";
				return dissension = true;
			}
			else if(inputPart.contains("}"))
			{
				Comment = "Contains Character }";
				return dissension = true;
			}
			else if(inputPart.contains("{"))
			{
				Comment = "Contains Character {";
				return dissension = true;
			}
			else if(inputPart.contains("]"))
			{
				Comment = "Contains Character ]";
				return dissension = true;
			}
			else if(inputPart.contains("["))
			{
				Comment = "Contains Character [";
				return dissension = true;
			}
			else if(inputPart.contains("|"))
			{
				Comment = "Contains Character |";
				return dissension = true;
			}
			// else if(CharFound('¿', NAN_INPUT_PART) )
			// {
			// Comment ="Contains Character ¿";
			// return dissension =true;
			// }
			else if(inputPart.contains("Â¿") || inputPart.contains("Â") || inputPart.contains("¿"))
			{
				Comment = "Contains Character Â¿";
				return dissension = true;
			}
			else
			{
				return dissension;
			}

		}finally
		{
			if(dissension)
			{
				Status = "Reject, contains unaccepted character In Part Number";
			}
		}
	}

	public void freeDublicationList()
	{
		parts.clear();
	}

	public boolean isDublicated(String partnum)
	{
		if(parts.contains(partnum.trim()))
		{
			refresh();
			Status = "part Dublicated";
			Comment = "Dublication found on this sheet";
			return true;
		}
		else
		{
			parts.add(partnum.trim());
		}
		return false;
	}

	private boolean CharFound(CharSequence characters, String parse)
	{

		for(int i = 0; i < characters.length(); i++)
		{
			if(CharFound(characters.charAt(i), parse))
				return true;
		}

		return false;
	}

	private boolean CharFound(char character, String parse)
	{
		for(int i = 0; i < parse.length(); i++)
		{
			if(((int) character) == ((int) parse.charAt(i)))
				return true;
		}

		return false;
	}

	// validate on partNumber and supplier
	public boolean ValidatePartNumberAndSupplierISRejected(String inputPart, Supplier supplier) throws Exception
	{

		boolean dissension = false;
		try
		{
			String NonAlphaPart;
			if(inputPart == null || inputPart.isEmpty())
				throw new Exception("Null Input partnumber");

			if(supplier == null)
				throw new Exception("Null Supplier");
			// check supplier on cm
			if(ParaQueryUtil.getCm_Man_IdByAutoSupplyerCode(supplier) == null)
				throw new Exception("SE_MAN_ID Not Found on CM");

			// remove Non Alpha Characters
			NonAlphaPart = ParaQueryUtil.getNonAlphaPart(inputPart).trim();
			if(isSpecialCharactarFound(inputPart))
			{
				return dissension = true;
			}
			else if(isAlphaPartNumnerFoundOnComponentTBL(inputPart, supplier))
			{
				return dissension = true;
			}
			else if(isAlphaPartNumberFoundOnLUT(NonAlphaPart, supplier.getName()))
			{
				return dissension = true;
			}
			else if(isAlphaPartNumberFoundOnAcquisitionTBL(NonAlphaPart, supplier.getName()))
			{
				return dissension = true;
			}

			Status = "No Problem";
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return dissension;
	}

	public boolean isRejectedPNAndSupplier(String inputPart, String supplierName) throws Exception
	{

		boolean dissension = false;
		try
		{
			String NonAlphaPart;
			// remove Non Alpha Characters
			NonAlphaPart = ParaQueryUtil.getNonAlphaPart(inputPart).trim();
			if(isSpecialCharactarFound(inputPart))
			{
				return dissension = true;
			}
			else if(isPNSupFoundOnComponentTBL(NonAlphaPart, supplierName))
			{
				return dissension = true;
			}
			else if(isAlphaPartNumberFoundOnLUT(NonAlphaPart, supplierName))
			{
				return dissension = true;
			}
			else if(isAlphaPartNumberFoundOnAcquisitionTBL(NonAlphaPart, supplierName))
			{
				return dissension = true;
			}

			Status = "No Problem";
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return dissension;
	}

	// validate on partNumber and supplier
	public boolean ValidatePartNumberAndSupplierISRejectedForUpdate(String NAN_INPUT_PART, Supplier SE_Man_ID) throws Exception
	{

		boolean dissension = false;
		try
		{
			String AN_INPUT_PART;
			if(NAN_INPUT_PART == null || NAN_INPUT_PART.isEmpty())
				throw new Exception("Null Input partnumber");

			if(SE_Man_ID == null)
				throw new Exception("Null Supplier");
			// check supplier on cm
			if(ParaQueryUtil.getCm_Man_IdByAutoSupplyerCode(SE_Man_ID) == null)
				throw new Exception("SE_MAN_ID Not Found on CM");

			// remove Non Alpha Characters
			AN_INPUT_PART = ParaQueryUtil.getNonAlphaPart(NAN_INPUT_PART).trim();
			if(isSpecialCharactarFound(NAN_INPUT_PART))
			{
				return dissension = true;
			}
			else if(isAlphaPartNumberFoundOnLUT(AN_INPUT_PART, SE_Man_ID.getName()))
			{
				return dissension = true;
			}
			else if(isAlphaPartNumberFoundOnAcquisitionTBL(AN_INPUT_PART, SE_Man_ID.getName()))
			{
				return dissension = true;
			}

			Status = "No Problem";
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return dissension;
	}

	public boolean checkDescription(String description)
	{
		// &|=\\*!^~[]{}@$
		refresh();
		String[] discString = { "&", "=", "\\", "|", "*", "!", "^", "~", "[", "]", "{", "}", "@", "$", "?" };
		for(String s : discString)
		{
			if(description.contains(s))
			{
				Status = "Reject, contains unaccepted character in Description";
				Comment = "Contains Character" + s;
				return true;
			}
		}
		return false;
	}

	public String getDynamicDescription(PartComponent component, String description)
	{
		String dynaDesc = null;
		Session session = null;
		try
		{
			session = SessionUtil.getSession();
			if(description == null || description.trim().isEmpty())
			{
				dynaDesc = ParaQueryUtil.getNewDiscription(component.getSupplierPl().getPl().getId(), component.getComId(), session);
				if(dynaDesc == null || dynaDesc.trim().isEmpty())
				{
					dynaDesc = component.getDocument().getPdf().getTitle();
				}
			}
			else
			{// if(description != null && !description.trim().isEmpty())
				dynaDesc = ParaQueryUtil.getNewDiscription(component.getSupplierPl().getPl().getId(), component.getComId(), session);
				if(dynaDesc == null || dynaDesc.trim().isEmpty())
				{
					boolean b = checkDescription(description);
					if(!b)
						dynaDesc = description;
					else
						dynaDesc = null;
				}
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if(session != null & session.isOpen())
				SessionUtil.closeSession(session);
		}
		return dynaDesc;
	}

	public boolean checkrelatedFeatures(PDDRow pddrow)
	{
		try
		{

			Map fetmap = pddrow.getFeatures();
			Iterator itrat = fetmap.keySet().iterator();
			FeatureDTO fet = null;
			int i = 0;
			List<RelatedFeaturesDTO> listofrelatedFeatures = new ArrayList<RelatedFeaturesDTO>();

			RelatedFeaturesDTO qachek;
			while(itrat.hasNext())
			{
				fet = ((FeatureDTO) fetmap.get(itrat.next()));

				if(!fet.getFeaturevalue().isEmpty())
				{
					qachek = ParaQueryUtil.getQAChecksInDependentFeature(pddrow.getPl(), fet);

					if(qachek != null)
					{
						qachek.setSheetrow(pddrow);
						ConditionValidator cond = new ConditionValidator();
						boolean v = cond.Validat(qachek);
						Comment = cond.getComment();
						return !v;
						// System.out.println(fet.getFeatureName()+"\t"+qachek.getValue()+"\t"+qachek.getQaCheckRelatedFunctionsesForIndepFunctionId().size());
					}

				}
			}

		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}

	public boolean isSpecificCustomerFeature(Pl pl, String featurename)
	{
		try
		{
			return ParaQueryUtil.checkCustomerFeatureFlag(pl, featurename);
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}

}

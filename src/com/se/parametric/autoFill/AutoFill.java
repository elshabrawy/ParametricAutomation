package com.se.parametric.autoFill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.SysexMessage;
import javax.swing.JOptionPane;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.se.automation.db.QueryUtil;
import com.se.automation.db.SessionUtil;
import com.se.automation.db.client.mapping.AutofillRules;
import com.se.automation.db.client.mapping.AutofillRulesDetails;
import com.se.automation.db.client.mapping.Pl;
import com.se.automation.db.client.mapping.PlFetConverter;
import com.se.automation.db.client.mapping.Supplier;
import com.se.parametric.dba.ParaQueryUtil;
import com.sun.star.table.XCell;
import com.sun.star.table.XCellRange;

import osheet.Cell;
import osheet.SheetPanel;
import osheet.WorkingSheet;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class AutoFill
{

	private String userName;
	private SheetPanel sheetpanel;
	private ArrayList<ArrayList<String>> readData;
	private WorkingSheet workingSheet;
	private Pl pl;
	private Supplier supplier;
	private ArrayList<String> normalFlagData = new ArrayList<String>();
	private ArrayList<String> formulationFlagData = new ArrayList<String>();
	private ArrayList<String> rangeFlagData = new ArrayList<String>();
	private ArrayList<String> converterFlagData = new ArrayList<String>();

	public enum Units
	{
		K, M, G

	};

	public AutoFill(SheetPanel sheetpanel, String userName, WorkingSheet workingSheet, String plName, String manName)
	{
		this.userName = userName;
		this.sheetpanel = sheetpanel;
		this.workingSheet = workingSheet;

		// System.err.println("current sheet " + workingSheet.getSelectedCellValue());
		setPl(plName);
		setSupplier(manName);
	}

	public WorkingSheet getAutoFillProcess()
	{
		int lastRow = (workingSheet.getLastRow() + 2);
		// XCellRange xcellrange = null;
		// XCellRange partRange = null;
		// ArrayList<String> mapRow = null;
		// String lastColumn = workingSheet.getColumnName(workingSheet.getHeader().size() + 1);
		// String partColumn = getColumnName(PartCell + 1);
		readData = readAutoFillData();
		Cell cell = null;
		try
		{
			// workingSheet.getSheet().getCellByPosition(0, 0).setFormula("");
			// xcellrange = workingSheet.getSheet().getCellRangeByName("A" + 2 + ":" + lastColumn + lastRow);
			// partRange = sheet.getCellRangeByName("A3:A" + lastRow);
			// partRange = sheet.getCellRangeByName(partColumn + "3:" + partColumn + lastRow);

			String fetval = "";
			ArrayList<String> allFet = new ArrayList<String>();
			System.err.println("start write" + new Date());

			for(int j = 0; j < readData.size(); j++)
			{
				int writtenRow = j + 2;
				HashMap<String, String> changedFetsMap = new HashMap<String, String>();
				String changedFetsString = "";
				// String changedFetsVal="";
				for(int i = 8; i < readData.get(j).size(); i++)
				{

					if(j == 0)
					{
						allFet.add(readData.get(j).get(i));
						continue;
					}
					System.out.println(readData.get(j).get(i));
					cell = workingSheet.getCellByPosission(i, writtenRow);
					fetval = readData.get(j).get(i);
					if(!fetval.equals(""))
					{
						changedFetsMap.put(allFet.get(i - 8), fetval);
						changedFetsString += allFet.get(i - 8) + "&";
					}

				}// cols for loop

				/*
				 * Normal Flag
				 */
				boolean normalFlagRoleApplied = true;
				String tempRolefet;
				String tempRolefetVal;
				String[] tempFetArr = null;
				int fetslength = 0;
				for(int i = 0; i < normalFlagData.size(); i++)
				{// loop on roles
					normalFlagRoleApplied = true;
					for(int u = 0; u < normalFlagData.get(i).split("}")[0].split("&").length - 1; u++)// loop on fets in each role
					{
						fetslength = normalFlagData.get(i).split("}")[1].split("&").length;
						tempRolefet = normalFlagData.get(i).split("}")[0].split("&")[u]; // each fet in each role in table
						tempRolefetVal = normalFlagData.get(i).split("}")[1].split("&")[u];
						if((!changedFetsString.contains(tempRolefet + "&")) || changedFetsString.equals(""))
						{
							normalFlagRoleApplied = false;
							break;
						}
						else
						{
							if(!changedFetsMap.get(tempRolefet).equals(tempRolefetVal))
							{
								normalFlagRoleApplied = false;
								break;
							}
						}
					}
					if(normalFlagRoleApplied)
					{
						cell = workingSheet.getCellByPosission(allFet.indexOf(normalFlagData.get(i).split("}")[0].split("&")[fetslength - 1]) + 8, j + 1);
						cell.setText(normalFlagData.get(i).split("}")[1].split("&")[fetslength - 1]);
						cell.setCellColore(0xff6760);
					}

				}// normal roles loop

				/*
				 * Formulation Flag case at least only one fet =N/A rule not applied case all fet =N/A then output fet=N/A case no n/a role applied
				 */
				ScriptEngineManager mgr = new ScriptEngineManager();
				ScriptEngine engine = mgr.getEngineByName("JavaScript");
				String tempFormula = "";
				boolean allHaveNAFlag = true;
				boolean formulaFlagApplied = true;
				for(int i = 0; i < formulationFlagData.size(); i++)
				{// loop on roles
					formulaFlagApplied = true;
					for(int k = 0; k < formulationFlagData.get(i).split("}")[0].split("&").length - 1; k++)
					{
						fetslength = formulationFlagData.get(i).split("}")[0].split("&").length;
						tempRolefet = formulationFlagData.get(i).split("}")[0].split("&")[k]; // each fet in each role in table
						tempRolefetVal = formulationFlagData.get(i).split("}")[1];
						if((!changedFetsString.contains(tempRolefet + "&")) || changedFetsString.equals(""))
						{
							formulaFlagApplied = false;
							allHaveNAFlag = false;
							break;
						}
						else
						{
							// if(!changedFetsMap.get(tempRolefet).equals(tempRolefetVal))
							// {// inequal proper value

							if(!changedFetsMap.get(tempRolefet).equals("N/A") && !changedFetsMap.get(tempRolefet).equals("N/R"))
							{// if it's val not (N/A or N/R) and not in the given role
								allHaveNAFlag = false;
								// formulaFlagApplied=false;
								// break;

								// }

							}

						}

					}

					System.out.println(" formulation " + formulaFlagApplied);
					if(allHaveNAFlag)
					{

						tempFormula = "N/A";
						cell = workingSheet.getCellByPosission(allFet.indexOf(normalFlagData.get(i).split("}")[0].split("&")[fetslength - 1]) + 8, j + 1);
						cell.setText(tempFormula);
						cell.setCellColore(0x87ea00);
						continue;
					}
					if(formulaFlagApplied) // roles applied but allHaveNAFlag = true
					{
						if((changedFetsMap.values().contains("N/A") && changedFetsMap.values().size() == 1) || (changedFetsMap.values().contains("N/R") && changedFetsMap.values().size() == 1))
						{// roles applied but all are not N/A or N/R
							// allHaveNAFlag=false;
							continue;
						}

						tempFormula = "";
						tempRolefet = formulationFlagData.get(i).split("}")[0].split("&")[fetslength - 1];
						tempRolefetVal = formulationFlagData.get(i).split("}")[1].split("&")[0];
						tempFetArr = tempRolefetVal.split("@");

						for(int ii = 0; ii < tempFetArr.length; ii++)
						{
							for(int k = 0; k < formulationFlagData.get(i).split("}")[0].split("&").length - 1; k++)
							{
								tempFetArr[ii] = tempFetArr[ii].replace(formulationFlagData.get(i).split("}")[0].split("&")[k], changedFetsMap.get(formulationFlagData.get(i).split("}")[0].split("&")[k]));
								// if(formulationFlagData.get(i).split("}")[0].contains(changedFetsMap.get(tempFetArr[ii])+"}"))
							}
							if(tempFetArr[ii].matches("^[A-Za-z0-9\\s ]+")) // ensure it's operand not operator
							{
								// if all Fetures N/A => N/A
								// if one N/A role will not applied

								if(changedFetsMap.get(tempFetArr[ii]).matches("^[A-Za-z_/]+"))// here check value if it's characters or number for
								{ // surround with '' in character in java Script
									tempFormula += "'" + changedFetsMap.get(tempFetArr[ii]) + "'";

								}
								else
									tempFormula += changedFetsMap.get(tempFetArr[ii]);

								System.out.println(tempFormula);
								continue;
							}
							tempFormula += tempFetArr[ii];
						}
						System.err.println(" length " + tempFormula + "+''");

						// if(!allHaveNAFlag)
						tempFormula = engine.eval(tempFormula + "+''").toString();
						// else
						// tempFormula="N/A";
						System.err.println(formulationFlagData.get(i).split("}")[0].split("&")[fetslength - 1] + " :: " + allFet.indexOf(formulationFlagData.get(i).split("}")[0].split("&")[fetslength - 1]));
						cell = workingSheet.getCellByPosission(allFet.indexOf(formulationFlagData.get(i).split("}")[0].split("&")[fetslength - 1]) + 8, j + 1);
						cell.setText(tempFormula);
						cell.setCellColore(0x87ea00);

					}

				}// formulation roles

				/*
				 * Range
				 */
				boolean rangeFlagApplied = true;
				String tempRangeString = "";
				String finalResult = "";
				String tempVal[];
				for(int i = 0; i < rangeFlagData.size(); i++)
				{// loop on roles

					rangeFlagApplied = true;
					// fetslength=rangeFlagData.get(i).split("}")[1].split("&").length;
					tempRolefet = rangeFlagData.get(i).split("}")[0].split("&")[0]; // each fet in each role in table
					// tempRolefetVal=rangeFlagData.get(i).split("}")[1].split("&")[0];
					if((!changedFetsString.contains(tempRolefet + "&")) || changedFetsString.equals(""))
					{
						rangeFlagApplied = false;
						// break;
					}
					else
					{
						tempFetArr = rangeFlagData.get(i).split("}")[1].split("&");
						System.out.println(rangeFlagData.get(i).split("}")[0]);
						if(changedFetsMap.get(tempRolefet).equals("N/A") || changedFetsMap.get(tempRolefet).equals("N/R"))
						{
							cell = workingSheet.getCellByPosission(allFet.indexOf(rangeFlagData.get(i).split("}")[0].split("&")[1]) + 8, j + 1);
							cell.setText("N/A, N/R");
							cell.setCellColore(0x87ea00);
							continue;
						}

						tempVal = changedFetsMap.get(tempRolefet).split("\\|");
						finalResult = "";
						for(int l = 0; l < tempVal.length; l++)
						{
							engine.eval("value = " + tempVal[l]);

							for(int k = 0; k < tempFetArr.length; k++)
							{

								System.out.println(tempFetArr[k]);

								tempRangeString = "";
								if(tempFetArr[k].contains("to"))
								{
									tempRangeString = tempFetArr[k].replace("to", "<=value && value<=");
								}
								else
								{
									if(tempFetArr[k].split("<").length == 2)// like (<value)
										tempRangeString = "value " + tempFetArr[k];
									else
										// like (value<)
										tempRangeString = tempFetArr[k] + "value ";
								}

								Boolean bol = (Boolean) engine.eval(tempRangeString);
								if(bol)
								{
									if(!finalResult.equals(""))
										finalResult += "|";
									finalResult += tempFetArr[k];
									break;
								}

							}
						}
						cell = workingSheet.getCellByPosission(allFet.indexOf(rangeFlagData.get(i).split("}")[0].split("&")[1]) + 8, j + 1);
						cell.setText(finalResult);
						cell.setCellColore(0xb2b2f7);
					}
				}// roles in range

				/*
				 * converter
				 */
				boolean converterFlagApplied = true;
				String tempconverterUnitString = "";
				String tempconverterNumString = "";
				String result = "";
				for(int i = 0; i < converterFlagData.size(); i++)
				{// loop on roles
					converterFlagApplied = true;
					tempconverterUnitString = "";
					tempconverterNumString = "";
					result = "";
					tempRolefet = converterFlagData.get(i).split("}")[0]; // only first fet in each role in table
					if((!changedFetsString.contains(tempRolefet + "&")) || changedFetsString.equals(""))
					{
						converterFlagApplied = false;
						// break;
					}
					if(converterFlagApplied)
					{
						// get Unit Data from DataBase
						Session session = SessionUtil.getSession();
						Map<String, Long> fetPlUnitsMap = ParaQueryUtil.getPLFetUnitsVal(session, getPl().getName(), tempRolefet);
						session.close();
						// tempRolefetVal=converterFlagData.get(i).split("}")[1];
						tempRolefetVal = changedFetsMap.get(tempRolefet);
						tempconverterUnitString = tempRolefetVal.replaceAll("[\\d.]", "");
						tempconverterNumString = tempRolefetVal.replaceAll("[^\\d.]", "");
						result = tempconverterNumString;
						if(!tempconverterUnitString.equals("") && !fetPlUnitsMap.isEmpty())// if it has unit and assigned units in DB
						{// has unit
							if(fetPlUnitsMap.keySet().contains(tempconverterUnitString.toUpperCase()))
							{
								result += "* " + fetPlUnitsMap.get(tempconverterUnitString.toUpperCase()).toString();
								result = engine.eval(result + "+''") + "";
							}
							// try
							// {
							//
							// switch((tempconverterUnitString.toUpperCase())){
							// case K:
							// result += "* 1024";
							// break;
							// case M:
							// result += "* 1024 *1024";
							// break;
							// case G:
							// result += "* 1024 *1024*1024";
							// break;
							// default:
							// // System.out.println("The students grade is unknown.");
							// break;
							// }
							//
							// result = engine.eval(result + "+''") + "";
							// }catch(IllegalArgumentException e) // throwen from Units.valueOf( tempconverterUnitString) when
							// // tempconverterUnitString is not in enum Units
							// {// unit not in my enum
							// result = tempRolefetVal;
							// }
						}
						// System.err.println(allFet.indexOf(converterFlagData.get(i).split("}")[1]));
						cell = workingSheet.getCellByPosission(allFet.indexOf(converterFlagData.get(i).split("}")[1]) + 8, j + 1);
						cell.setText(result);
						cell.setCellColore(0xf7b3b3);

					}

				}

			}// rows for loop
			System.err.println("end write" + new Date());

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return workingSheet;
	}

	public ArrayList<ArrayList<String>> readAutoFillData()
	{

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		XCellRange xcellrange = null;
		int lastRow = (workingSheet.getLastRow());
		String lastColumn = workingSheet.getColumnName(workingSheet.getHeader().size() + 1);
		String celldata;
		ArrayList<String> row;
		try
		{
			workingSheet.getSheet().getCellByPosition(0, 0).setFormula("");
			xcellrange = workingSheet.getSheet().getCellRangeByName("A" + 2 + ":" + "AZ" + lastRow + 1);
			System.out.println(workingSheet.getEndParametricFT());
			// row = new ArrayList<String>();
			for(int j = 0; j < lastRow; j++)
			{
				row = new ArrayList<String>();
				for(int i = 0; i < workingSheet.getEndParametricFT(); i++)
				{
					XCell cell = null;
					String fetName = "";
					try
					{

						cell = xcellrange.getCellByPosition(i, j);
						celldata = workingSheet.getCellText(cell).getString().trim();
						row.add(celldata);

					}catch(Exception ex)
					{
						ex.printStackTrace();
						System.out.println(ex.getMessage());
					}

				}
				result.add(row);
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

	public Pl getPl()
	{
		return pl;
	}

	public void setPl(String plName)
	{
		Session session = SessionUtil.getSession();
		try
		{

			this.pl = QueryUtil.getPlByExactName(plName, session);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
	}

	public Supplier getSupplier()
	{
		return supplier;
	}

	public void setSupplier(String supplier)
	{
		Session session = SessionUtil.getSession();

		try
		{

			this.supplier = QueryUtil.getSupplierByExactName(supplier, session);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
	}

	public void normalFlag()
	{

	}

	public ArrayList<String> getNormalFlagData()
	{
		return normalFlagData;
	}

	public ArrayList<String> getFormulationFlagData()
	{
		return formulationFlagData;
	}

	public void setNormalFlagData(ArrayList<String> rules)
	{
		/*
		 * man}}outfet}}out_fet_val}}all_in_fets separated by )) }} all_from_val separated by )) }} all_to_val separated by ))
		 */

		String tempString;
		String tempOutFet;
		String tempOutFetVal;
		String tempInFet;
		String tempFromVal;
		for(int i = 0; i < rules.size(); i++)
		{
			tempString = rules.get(i);
			tempOutFet = tempString.split("}}")[1];
			tempOutFetVal = tempString.split("}}")[2];
			tempInFet = join(tempString.split("}}")[3].split("~"), "&");
			tempFromVal = join(tempString.split("}}")[4].split("~"), "&");
			tempString = tempInFet + "&" + tempOutFet + "}" + tempFromVal + "&" + tempOutFetVal;
			normalFlagData.add(tempString);
		}
		// normalFlagData.add("Manufacturer Type&Number of Channels per Chip&Type&Minimum Operating Frequency&}carbon&7&chemical&r&");
		// normalFlagData.add("Manufacturer Type&Number of Channels per Chip&Type&Maximum Input VSWR&}a&3&b&c&");
		this.normalFlagData = normalFlagData;

	}

	public void setFormulationFlagData(ArrayList<String> rules)
	{
		String tempString;
		String tempOutFet;
		String tempOutFetVal;
		String tempInFet;
		// String tempFromVal;
		for(int i = 0; i < rules.size(); i++)
		{
			tempString = rules.get(i);
			tempOutFet = tempString.split("}}")[1];
			// tempOutFetVal=tempString.split("}}")[2].replaceAll("(\\+{1})\\s*'\\s*([A-Za-z0-9\\s]*)\\s*'\\s*", "&$2");
			tempOutFetVal = tempString.split("}}")[2];
			tempInFet = join(tempString.split("}}")[3].split("~"), "&");
			tempString = tempInFet + "&" + tempOutFet + "}" + tempOutFetVal;
			formulationFlagData.add(tempString);
		}
		// formulationFlagData.add("Manufacturer Type&Number of Channels per Chip&Type&Minimum Operating Frequency&}4&5&N/A&Manufacturer Type@+@''@+@(@Number of Channels per Chip@+@Type@)&");
		//
		// formulationFlagData.add("Manufacturer Type&Number of Channels per Chip&Type&Maximum Input VSWR&}7&8&9&Manufacturer Type@*@Number of Channels per Chip@+@Type&");

		this.formulationFlagData = formulationFlagData;
	}

	public void setRangeFlagData(ArrayList<String> rules)
	{
		/*
		 * man}}outfet}}out_fet_val}}all_in_fets separated by )) }} all_from_val separated by )) }} all_to_val separated by ))
		 */

		String tempString;
		String tempOutFet;
		String tempInFet;
		String[] tempFromVal;
		String[] tempToVal;
		String tempVal = "";
		String[] tempFinalVal;
		for(int i = 0; i < rules.size(); i++)
		{
			tempString = rules.get(i);
			tempOutFet = tempString.split("}}")[1];
			tempInFet = tempString.split("}}")[3].split("~")[0];
			tempFromVal = tempString.split("}}")[4].split("~");
			tempToVal = tempString.split("}}")[5].split("~");

			tempFinalVal = new String[tempFromVal.length];
			for(int j = 0; j < tempToVal.length; j++)
			{
				tempVal = "";
				tempVal += ((tempFromVal[j].equals("null")) ? "<" : (tempFromVal[j].trim())) + ((!tempFromVal[j].equals("null") && !tempToVal[j].equals("null")) ? " to " : "") + ((tempToVal[j].equals("null")) ? "<" : (tempToVal[j].trim()));
				tempFinalVal[j] = tempVal;

			}

			tempString = tempInFet + "&" + tempOutFet + "}" + join(tempFinalVal, "&");
			rangeFlagData.add(tempString);
		}
		System.out.println(rangeFlagData);
		this.rangeFlagData = rangeFlagData;
	}

	public void setConverterFlagData(ArrayList<String> rules)
	{
		/*
		 * man}}outfet}}out_fet_val}}all_in_fets separated by )) }} all_from_val separated by )) }} all_to_val separated by ))
		 */

		String tempString;
		String tempOutFet;
		String tempInFet;
		for(int i = 0; i < rules.size(); i++)
		{
			tempString = rules.get(i);
			tempOutFet = tempString.split("}}")[1];
			tempInFet = tempString.split("}}")[3].split("~")[0];
			converterFlagData.add(tempInFet + "}" + tempOutFet);
		}
		System.err.println(converterFlagData);
		// rangeFlagData.add("Typical Power Gain&Typical Gain Flatness}<5 & 1 to 10 & 10 to 50 & 50 to 100 & >100");
		this.converterFlagData = converterFlagData;
	}

	public void getRules()
	{
		Session session = SessionUtil.getSession();
		String manName;
		String outFet;
		String outFetVal;
		String flagName;
		String inFet;
		String fromVal;
		String toVal;
		AutofillRulesDetails autofillRulesDetails;
		HashMap<String, ArrayList<String>> allRules = new HashMap<String, ArrayList<String>>();
		String tempInFet = "";
		String tempToVal = "";
		String tempFromVal = "";
		String tempDetail = "";
		try
		{
			Criteria cr = session.createCriteria(AutofillRules.class);
			cr.setFetchMode("AutofillRulesDetails", FetchMode.JOIN);
			cr.createAlias("autofillRulesDetailses", "details");
			cr.createCriteria("pl").add(Restrictions.eq("name", getPl().getName()));
			cr.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			cr.addOrder(Order.asc("details.id"));
			Set<AutofillRulesDetails> myDetails = null;
			List dataList = cr.list();
			for(AutofillRules mainRule : (List<AutofillRules>) dataList)
			{
				// }} separator for data in rule
				// ~ separator for infets , fromval,toval in rule
				tempInFet = "";
				tempToVal = "";
				tempFromVal = "";
				tempDetail = "";
				// check man name
				if(mainRule.getSupplier() != null)
				{
					manName = mainRule.getSupplier().getName();

					if(getSupplier() != null)
					{// user choosen man
						if(!getSupplier().getName().equals(mainRule.getSupplier().getName()))
						{
							continue;
						}
					}
				}
				else
				{
					manName = "";
				}

				outFet = mainRule.getFeature().getName();
				outFetVal = mainRule.getOutFetVal();
				flagName = mainRule.getFlag();
				myDetails = mainRule.getAutofillRulesDetailses();
				for(AutofillRulesDetails oneDetails : myDetails)
				{
					tempInFet += oneDetails.getFeature().getName() + "~";
					tempFromVal += oneDetails.getFromVal() + "~";
					tempToVal += oneDetails.getToVal() + "~";
				}
				tempDetail += manName + "}}" + outFet + "}}" + outFetVal + "}}" + tempInFet + "}}" + tempFromVal + "}}" + tempToVal;
				if(allRules.get(flagName) == null)
				{
					ArrayList<String> tempArrayList = new ArrayList<String>();
					tempArrayList.add(tempDetail);
					allRules.put(flagName, tempArrayList);
				}
				else
				{
					allRules.get(flagName).add(tempDetail);
				}

			}
			System.out.println(allRules);
			if(allRules.get("Converter") != null)
				setConverterFlagData(allRules.get("Converter"));
			if(allRules.get("Normal") != null)
				setNormalFlagData(allRules.get("Normal"));
			if(allRules.get("Range") != null)
				setRangeFlagData(allRules.get("Range"));
			if(allRules.get("Formula") != null)
				setFormulationFlagData(allRules.get("Formula"));

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			session.close();
		}
	}

	public static void main(String args[])
	{
		// AutoFill autoFillProcess = new AutoFill(null, null, null, "Gates", "");
		// autoFillProcess.getRules();
		// PlFetConverter plFetConverter=new PlFetConverter();
		// Session session=SessionUtil.getSession();
		// System.out.println(((PlFetConverter)session.createCriteria(PlFetConverter.class).list().get(0)).getPl().getName());
		// session.close();

	}

	public static String join(String[] arr, String delim)
	{
		List<String> list = Arrays.asList(arr);
		StringBuilder sb = new StringBuilder();

		String loopDelim = "";

		for(String s : list)
		{

			sb.append(loopDelim);
			sb.append(s);

			loopDelim = delim;
		}

		return sb.toString();
	}

}
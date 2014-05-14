package com.se.parametric;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.se.parametric.dto.TableInfoDTO;

public class Test {
	public Test() {
		System.out.println("user click " + CreateDialog("are u sure", "PDF found"));
	}

	public boolean CreateDialog(String message, String title) {
		int choice = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

		if (choice == JOptionPane.NO_OPTION || choice == JOptionPane.CLOSED_OPTION) {
			return false;
		} else {
			return true;
		}
	}

	public static void main(String args[]) {
		ArrayList<TableInfoDTO> docs = new ArrayList<TableInfoDTO>();
		TableInfoDTO doc = new TableInfoDTO();
		doc.setDevUserName("hatem");
		doc.setPdfUrl("pdf1");
		doc.setStatus("Done");
		doc.setSupplierName("ahmed");
		docs.add(doc);
		doc = new TableInfoDTO();
		doc.setDevUserName("kimat");
		doc.setPdfUrl("pdf2");
		doc.setStatus("inprogress");
		doc.setSupplierName("samtec");
		docs.add(doc);

		String[] header = { "PdfUrl", "SupplierName", "DevUserName", "Status" };

		Method[] m = TableInfoDTO.class.getMethods();
		Method[] mapMethods = new Method[header.length];

		for (int i = 0; i < header.length; i++) {
			for (Method method : m) {
				System.out.println("Method Name:" + method.getName());
				if (method.getName().equals("get" + header[i])) {
					mapMethods[i] = method;
					break;
				}
			}
		}
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		for (int i = 0; i < mapMethods.length; i++) {
			System.out.println("Method Name:" + mapMethods[i].getName());
			try {
				try {
					System.out.println("Method value:" + mapMethods[i].invoke(doc));
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (IllegalAccessException  e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}

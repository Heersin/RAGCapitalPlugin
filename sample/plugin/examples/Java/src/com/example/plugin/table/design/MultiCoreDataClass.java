/**
 * Copyright 2007 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely
 * for internal purposes to serve as example plugin.
 * This code may not be used in a commercial distribution. Recipients may
 * duplicate the code provided that all notices are fully reproduced with
 * and remain in the code. No part of this code may be modified, reproduced,
 * translated, used, distributed, disclosed or provided to third parties
 * without the prior written consent of Mentor Graphics, except as expressly
 * authorized above.
 * <p>
 * THE CODE IS MADE AVAILABLE "AS IS" WITHOUT WARRANTY OR SUPPORT OF ANY KIND.
 * MENTOR GRAPHICS OFFERS NO EXPRESS OR IMPLIED WARRANTIES AND SPECIFICALLY
 * DISCLAIMS ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR WARRANTY OF NON-INFRINGEMENT. IN NO EVENT SHALL MENTOR GRAPHICS OR ITS
 * LICENSORS BE LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING LOST PROFITS OR SAVINGS) WHETHER BASED ON CONTRACT, TORT
 * OR ANY OTHER LEGAL THEORY, EVEN IF MENTOR GRAPHICS OR ITS LICENSORS HAVE BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * <p>
 */
package com.example.plugin.table.design;

import com.mentor.chs.api.IXMulticore;
import com.mentor.chs.api.IXValue;

import java.text.NumberFormat;
import java.text.ParsePosition;

public class MultiCoreDataClass
{

	private IXMulticore this_multicore;
	private IXMulticore parent;
	private String m_Level = "1";
	private String m_Parent_Level = "1";
	private String m_Pitch;
	private String m_Multiplication_factor;

	public MultiCoreDataClass(IXMulticore currMC, IXMulticore parentMC)
	{
		this_multicore = currMC;
		parent = parentMC;
	}

	public String getCustomer_Multicore_Name()
	{

		String Customer_Multicore_Name = this_multicore.getAttribute("CustomerMulticoreName");
		if (Customer_Multicore_Name == null || " ".equals(Customer_Multicore_Name)) {
			Customer_Multicore_Name = " ";
		}

		return Customer_Multicore_Name;
	}

	public String getCustomer_Part_Number()
	{
		String customer_Part_Number = this_multicore.getAttribute("CustomerPartNumber");
		if (customer_Part_Number == null || " ".equals(customer_Part_Number)) {
			customer_Part_Number = " ";
		}
		return customer_Part_Number;
	}

	public String getCut_Back_Change_Type_End1()
	{

		String Cut_Back_Change_Type_End1 = this_multicore.getAttribute("CutBackChangeTypeEnd1");
		if (Cut_Back_Change_Type_End1 == null || " ".equals(Cut_Back_Change_Type_End1)) {
			Cut_Back_Change_Type_End1 = " ";
		}

		return Cut_Back_Change_Type_End1;
	}

	public String getCut_Back_Change_Type_End2()
	{

		String Cut_Back_Change_Type_End2 = this_multicore.getAttribute("CutBackChangeTypeEnd2");
		if (Cut_Back_Change_Type_End2 == null || " ".equals(Cut_Back_Change_Type_End2)) {
			Cut_Back_Change_Type_End2 = " ";
		}

		return Cut_Back_Change_Type_End2;
	}

	public String getCut_Back_Change_Value_End1()
	{

		String Cut_Back_Change_Value_End1 = this_multicore.getAttribute("CutBackChangeValueEnd1");
		if (Cut_Back_Change_Value_End1 == null || " ".equals(Cut_Back_Change_Value_End1)) {
			Cut_Back_Change_Value_End1 = " ";
		}

		return Cut_Back_Change_Value_End1;
	}

	public String getCut_Back_Change_Value_End2()
	{

		String Cut_Back_Change_Value_End2 = this_multicore.getAttribute("CutBackChangeValueEnd2");
		if (Cut_Back_Change_Value_End2 == null || " ".equals(Cut_Back_Change_Value_End2)) {
			Cut_Back_Change_Value_End2 = " ";
		}

		return Cut_Back_Change_Value_End2;
	}

	public String getIncluded_On_BOM()
	{

		String included_On_BOM = this_multicore.getAttribute("IncludeOnBOM");

		if (included_On_BOM == null || " ".equals(included_On_BOM)) {
			included_On_BOM = " ";
		}
		else if ("false".equals(included_On_BOM)) {
			included_On_BOM = "No";
		}
		else if ("true".equals(included_On_BOM)) {
			included_On_BOM = "Yes";
		}
		return included_On_BOM;
	}

	public String getIncluded_on_Cut_Chart()
	{
		String Included_on_Cut_Chart = this_multicore.getAttribute("IncludeOnCutChart");

		if (Included_on_Cut_Chart == null || " ".equals(Included_on_Cut_Chart)) {
			Included_on_Cut_Chart = " ";
		}
		else if ("false".equals(Included_on_Cut_Chart)) {
			Included_on_Cut_Chart = "No";
		}
		else if ("true".equals(Included_on_Cut_Chart)) {
			Included_on_Cut_Chart = "Yes";
		}
		return Included_on_Cut_Chart;
	}

	public String getInner_Cores()
	{
		String Inner_Cores = " ";
		StringBuilder innerCoreNames = new StringBuilder();
		if (!this_multicore.getMulticores().isEmpty()) {
			int count = 0;
			for (IXMulticore child_Core : this_multicore.getMulticores()) {

				innerCoreNames.append(child_Core.getAttribute("Name"));
				++count;
				if (count < this_multicore.getMulticores().size()) {
					innerCoreNames.append(" , ");
				}
			}
			Inner_Cores = innerCoreNames.toString();
		}
		return Inner_Cores;
	}

	public String getInternal_Part_Number()
	{
		String Internal_Part_Number = this_multicore.getAttribute("PartNumber");
		if (Internal_Part_Number == null || " ".equals(Internal_Part_Number)) {
			Internal_Part_Number = " ";
		}

		return Internal_Part_Number;
	}

	public String getLength_Change_Type()
	{
		String Length_Change_Type = this_multicore.getAttribute("LengthChangeType");
		if (Length_Change_Type == null || " ".equals(Length_Change_Type)) {
			Length_Change_Type = " ";
		}
		return Length_Change_Type;
	}

	public String getLength_Change_Value()
	{
		String Length_Change_Value = this_multicore.getAttribute("LengthChangeValue");
		if (Length_Change_Value == null || " ".equals(Length_Change_Value)) {
			Length_Change_Value = " ";
		}
		return Length_Change_Value;
	}

//TODO   //getting the harness level 

	public String getLevel()
	{
		return m_Level;
	}

	public String getMulticore_Description()
	{

		String Multicore_Description = this_multicore.getAttribute("ShortDescription");
		if (Multicore_Description == null || " ".equals(Multicore_Description)) {
			Multicore_Description = " ";
		}

		return Multicore_Description;
	}

	public String getMulticore_Name()
	{
		String multicore_Name = this_multicore.getAttribute("Name");
		if (multicore_Name == null || " ".equals(multicore_Name)) {
			multicore_Name = " ";
		}

		return multicore_Name;
	}

	public String getMulticore_Note()
	{
		String Multicore_Note = this_multicore.getAttribute("MulticoreNote");
		if (Multicore_Note == null || " ".equals(Multicore_Note)) {
			Multicore_Note = " ";
		}
		return Multicore_Note;
	}

	public String getMulticore_Option()
	{

		String multicore_Option = this_multicore.getAttribute("OptionExpression");
		if (multicore_Option == null || " ".equals(multicore_Option)) {
			multicore_Option = " ";
		}
		return multicore_Option;
	}

	public String getMulticore_Short_Description()
	{

		String Multicore_Short_Description = this_multicore.getAttribute("ShortDescription");
		if (Multicore_Short_Description == null || " ".equals(Multicore_Short_Description)) {
			Multicore_Short_Description = " ";
		}

		return Multicore_Short_Description;
	}

	public String getMulticore_Type()
	{
		String Multicore_Type = this_multicore.getAttribute("SheathType");
		if (Multicore_Type == null || " ".equals(Multicore_Type)) {
			Multicore_Type = " ";
		}

		return Multicore_Type;
	}

	public String getMultiplication_factor()
	{

		if ("Twisted".equals(this_multicore.getAttribute("SheathType"))) {
			String spec1 = this_multicore.getAttribute("OSSpec");
			Number num = getNumberFromString(spec1);
			boolean inHouse = (spec1 == null || num != null); //some numerical value or empty
			if (inHouse) {

				if (num == null) {

					m_Multiplication_factor = " ";
				}
				else if (Double.compare(num.doubleValue(), (double) num.intValue()) != 0) {
					m_Multiplication_factor = num.toString();
				}
				else {
					m_Multiplication_factor = " ";
				}
			}
		}
		else {
			m_Multiplication_factor = " ";
		}

		return m_Multiplication_factor;
	}

	public String getOuter_Color()
	{
		String Outer_Color = this_multicore.getAttribute("OSColor");
		if (Outer_Color == null || " ".equals(Outer_Color)) {
			Outer_Color = " ";
		}
		return Outer_Color;
	}

	public String getOuter_Material()
	{
		String Outer_Material = this_multicore.getAttribute("OSMaterial");
		if (Outer_Material == null || " ".equals(Outer_Material)) {
			Outer_Material = " ";
		}

		return Outer_Material;
	}

	public String getOuter_Spec()
	{
		String Outer_Spec = " ";
//		OS SPEC to be displayed only if the type is sheath
		if ("Sheath".equals(this_multicore.getAttribute("SheathType"))) {
			Outer_Spec = this_multicore.getAttribute("OSSpec");

			if (Outer_Spec == null || " ".equals(Outer_Spec) || "-".equals(Outer_Spec)) {

				Outer_Spec = " ";
			}
		}
		return Outer_Spec;
	}

	public String getParent_Level()
	{
		return m_Parent_Level;
	}

	public String getParent_Name()
	{

		if (this_multicore.getAttribute("Name").equals(parent.getAttribute("Name"))) {
			return " ";
		}
		else {
			return parent.getAttribute("Name");
		}
	}

	public String getParent_Option()
	{
		return parent.getAttribute("OptionExpression");
	}

	public String getPitch()
	{

		if ("Twisted".equals(this_multicore.getAttribute("SheathType"))) {
			String spec1 = this_multicore.getAttribute("OSSpec");

			Number num = getNumberFromString(spec1);

			boolean inHouse = (spec1 == null || num != null); //some numerical value or empty
			if (inHouse) {

				if (num == null) {
					m_Pitch = " ";
				}
				else if (Double.compare(num.doubleValue(), (double) num.intValue()) != 0) {
					m_Pitch = " ";
				}
				else {
					m_Pitch = num.toString();
				}
			}
		}
		else {
			m_Pitch = " ";
		}

		return m_Pitch;
	}

	public String getIsInhouse()
	{
		String isInHouse = "NO";
		if ("Twisted".equals(this_multicore.getAttribute("SheathType"))) {
			String spec1 = this_multicore.getAttribute("OSSpec");

			Number num = getNumberFromString(spec1);

			boolean inHouse = (spec1 == null || num != null); //some numerical value or empty
			if (inHouse) {
				isInHouse = "YES";
			}
		}
		return isInHouse;
	}

	public String getProperty()
	{

		StringBuilder Properties = new StringBuilder();
		if (!this_multicore.getProperties().isEmpty()) {

			int count = 0;
			for (IXValue property : this_multicore.getProperties()) {

				Properties.append(property.getName());
				Properties.append(':');
				Properties.append(property.getValue());
				++count;
				if (count < this_multicore.getMulticores().size()) {
					Properties.append(" , ");
				}
			}
		}

		return Properties.toString();
	}

	public String getSupplier_Part_Number()
	{

		String Supplier_Part_Number = this_multicore.getAttribute("SupplierPartNumber");
		if (Supplier_Part_Number == null || " ".equals(Supplier_Part_Number)) {
			Supplier_Part_Number = "  ";
		}
		return Supplier_Part_Number;
	}

	private Number getNumberFromString(String spec)
	{
		if (spec != null) {
			ParsePosition pp = new ParsePosition(0);
            Number num = NumberFormat.getInstance().parse(spec, pp);
            if (pp.getIndex() == spec.length()) {
				return num;
			}
		}
		return null;
	}
}



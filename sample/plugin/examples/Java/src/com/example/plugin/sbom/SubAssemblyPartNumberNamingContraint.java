/*
 * Copyright 2013 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.   
 */
package com.example.plugin.sbom;

import com.example.plugin.constraint.BaseConstraint;
import com.mentor.chs.api.sbom.IXSubAssembly;
import com.mentor.chs.plugin.constraint.IXSubAssemblyPartNumberConstraint;

public class SubAssemblyPartNumberNamingContraint extends BaseConstraint implements IXSubAssemblyPartNumberConstraint
{

	public SubAssemblyPartNumberNamingContraint()
	{
		super("Sub-assembly part number naming constraint",
				"1.0",
				"Sub-assembly part number naming constraint");
	}

	@Override public String getPartNumber(IXSubAssembly subAssembly, long uniqueNumber)
	{
		return "xxx" + Long.toString(uniqueNumber);
	}
}

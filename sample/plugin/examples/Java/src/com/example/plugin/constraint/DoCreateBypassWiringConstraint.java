/*
 * Copyright 2015 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */
package com.example.plugin.constraint;

import com.mentor.chs.api.IXBundle;
import com.mentor.chs.api.IXBundleRegion;
import com.mentor.chs.api.IXSplice;
import com.mentor.chs.plugin.constraint.IXCreateBypassWiringConstraint;
import com.mentor.chs.plugin.constraint.IXDoDontResult;

public class DoCreateBypassWiringConstraint
		extends BaseConstraint
		implements IXCreateBypassWiringConstraint
{
	public DoCreateBypassWiringConstraint()
	{
		super("DoCreateBypassWiringConstraint", "0.1", "Do create bypass wiring");
	}

	public boolean match(IXSplice ixSplice, IXBundle ixBundle, IXDoDontResult ixDoDontResult)
	{
		ixDoDontResult.setValue(IXDoDontResult.DoDont.DO);
		return true;
	}

	public boolean match(IXSplice ixSplice, IXBundleRegion ixBundleRegion, IXDoDontResult ixDoDontResult)
	{
		ixDoDontResult.setValue(IXDoDontResult.DoDont.DO);
		return true;
	}
}

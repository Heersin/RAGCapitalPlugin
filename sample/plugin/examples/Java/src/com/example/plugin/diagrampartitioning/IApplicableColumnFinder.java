/*
 * Copyright 2013 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */

package com.example.plugin.diagrampartitioning;

import com.mentor.chs.api.IXLogicDesign;
import com.mentor.chs.api.IXOption;

import java.util.List;
import java.util.Map;

public interface IApplicableColumnFinder
{

	List<IColumn> getApplicableColumns(IXLogicDesign functionalDesign, Map<String, IXOption> nameToOptionMap);
}

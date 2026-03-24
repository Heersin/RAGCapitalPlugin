/*
 * Copyright 2013 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */

package com.example.plugin.diagrampartitioning.ui;

import com.mentor.chs.api.IXObject;

import java.util.List;

public interface IPartitionCombination<T>
{

	IXObject getSourceObject();

	String getName();

	void setName(String newName);

	boolean getValue(T columnName);

	void setValue(T columnName, boolean value);

	/**
	 * Get the selected columns, order is not guaranteed.
	 *
	 * @return selected columns
	 */
	List<T> getSelectedColumns();
}

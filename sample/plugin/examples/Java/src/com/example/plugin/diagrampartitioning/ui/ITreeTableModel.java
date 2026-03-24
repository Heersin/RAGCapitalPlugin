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

import javax.swing.tree.TreeModel;

public interface ITreeTableModel extends TreeModel
{

	public int getColumnCount();

	public Object getColumnIdentifier(int column);

	public Class getColumnClass(int column);

	public Object getValueAt(Object node, int column);

	public void setValueAt(Object aValue, Object node, int column);

	public boolean isCellEditable(Object node, int column);

	public void renameNode(Object node, String name);
}

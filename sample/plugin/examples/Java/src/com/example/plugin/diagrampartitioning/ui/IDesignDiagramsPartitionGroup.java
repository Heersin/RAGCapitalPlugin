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

import com.mentor.chs.api.IXLogicDesign;

import javax.swing.Icon;

public interface IDesignDiagramsPartitionGroup extends IObjectGroup<IXLogicDesign, IDiagramPartitionGroup>
{

	Icon getIcon();
}

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

import com.example.plugin.diagrampartitioning.IColumn;
import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXLogicDesign;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PartitionHelper
{

	private PartitionHelper()
	{
	}

	private static Map<String, Icon> m_hashedImageIcons = new HashMap<String, Icon>();

	public static IDiagramPartitionGroup createDiagramPartitionGroup(IXDiagram diagram)
	{
		return new DiagramPartitionGroup(diagram);
	}

	public static IDesignDiagramsPartitionGroup createDesignDiagramsPartitionGroup(IXLogicDesign design)
	{
		return new DesignDiagramsPartitionGroup(design);
	}

	static Icon getImageIcon(String imagePath)
	{
		Icon icon = m_hashedImageIcons.get(imagePath);
		if (icon == null) {
			URL url = PartitionHelper.class.getResource(imagePath);
			if (url != null) {
				icon = new ImageIcon(url);
				m_hashedImageIcons.put(imagePath, icon);
			}
		}
		return icon;
	}

	private static class DiagramPartitionGroup extends ObjectGroup<IXDiagram, IPartitionCombination<IColumn>>
			implements IDiagramPartitionGroup
	{

		private DiagramPartitionGroup(IXDiagram diagram)
		{
			super(diagram);
		}

		public String getName()
		{
			return getSource().getAttribute(IXAttributes.Name);
		}
	}

	private static class DesignDiagramsPartitionGroup extends ObjectGroup<IXLogicDesign, IDiagramPartitionGroup>
			implements IDesignDiagramsPartitionGroup
	{

		private DesignDiagramsPartitionGroup(IXLogicDesign design)
		{
			super(design);
		}

		public String getName()
		{
			StringBuilder builder = new StringBuilder();
			IXLogicDesign source = getSource();
			builder.append(source.getAttribute(IXAttributes.Name));
			String revision = source.getAttribute(IXAttributes.Revision);
			if (revision != null && !revision.isEmpty()) {
				builder.append(":").append(revision);
			}
			String shortDesc = source.getAttribute(IXAttributes.ShortDescription);
			if (shortDesc != null && !shortDesc.isEmpty()) {
				builder.append(":").append(shortDesc);
			}
			return builder.toString();
		}

		public Icon getIcon()
		{
			return PartitionHelper.getImageIcon("ico_design.gif");
		}
	}

	private abstract static class ObjectGroup<O, T> implements IObjectGroup<O, T>
	{

		private Set<T> m_partitions = new LinkedHashSet<T>();
		private O m_object;

		protected ObjectGroup(O object)
		{
			m_object = object;
		}

		public O getSource()
		{
			return m_object;
		}

		public void add(T object)
		{
			m_partitions.add(object);
		}

		public void remove(T object)
		{
			m_partitions.remove(object);
		}

		public void removeAll()
		{
			m_partitions.clear();
		}

		public List<T> getObjects()
		{
			return Collections.unmodifiableList(new ArrayList<T>(m_partitions));
		}

		public String toString()
		{
			return getName();
		}
	}
}

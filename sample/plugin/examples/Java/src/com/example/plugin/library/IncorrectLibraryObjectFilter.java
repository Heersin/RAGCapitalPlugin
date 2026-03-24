package com.example.plugin.library;


import com.mentor.chs.api.IXNodeComponent;

import com.mentor.chs.plugin.filter.IXObjectFilter;

import java.util.Collection;
import java.util.Collections;

public class IncorrectLibraryObjectFilter implements IXObjectFilter<IXNodeComponent>
{

	@Override public Collection<IXNodeComponent> apply(Collection<IXNodeComponent> candidateObjects)
	{
		return Collections.emptyList();
	}
}
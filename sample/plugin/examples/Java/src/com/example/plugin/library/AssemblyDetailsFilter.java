package com.example.plugin.library;


import com.mentor.chs.api.IXLibraryAssemblyDetails;

import com.mentor.chs.plugin.filter.IXObjectFilter;

import java.util.Collection;
import java.util.Collections;

public class AssemblyDetailsFilter implements IXObjectFilter<IXLibraryAssemblyDetails>
{

	@Override public Collection<IXLibraryAssemblyDetails> apply(Collection<IXLibraryAssemblyDetails> candidateObjects)
	{
		return Collections.emptyList();
	}
}

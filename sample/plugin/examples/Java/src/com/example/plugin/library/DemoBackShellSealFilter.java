package com.example.plugin.library;


import com.mentor.chs.api.IXLibraryBackshellSeal;

import com.mentor.chs.plugin.filter.IXObjectFilter;

import java.util.Collection;
import java.util.Collections;

public class DemoBackShellSealFilter implements IXObjectFilter<IXLibraryBackshellSeal>
{

	@Override public Collection<IXLibraryBackshellSeal> apply(Collection<IXLibraryBackshellSeal> candidateObjects)
	{
		return Collections.emptyList();
	}
}
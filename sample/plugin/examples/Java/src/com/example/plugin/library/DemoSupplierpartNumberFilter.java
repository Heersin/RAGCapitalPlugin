package com.example.plugin.library;



import com.mentor.chs.api.IXLibrarySupplierPartNumber;

import com.mentor.chs.plugin.filter.IXObjectFilter;

import java.util.Collection;
import java.util.Collections;

public class DemoSupplierpartNumberFilter implements IXObjectFilter<IXLibrarySupplierPartNumber>
{

	@Override public Collection<IXLibrarySupplierPartNumber> apply(Collection<IXLibrarySupplierPartNumber> candidateObjects)
	{
		return Collections.emptyList();
	}
}
package com.example.plugin.library;


import com.mentor.chs.api.IXLibraryClip;
import com.mentor.chs.plugin.filter.IXObjectFilter;

import java.util.Collection;
import java.util.HashSet;

public class ClipFilter implements IXObjectFilter<IXLibraryClip>
{

	@Override public Collection<IXLibraryClip> apply(Collection<IXLibraryClip> libObj)
	{
		Collection<IXLibraryClip> filterCol = new HashSet<IXLibraryClip>();
		for (IXLibraryClip clip : libObj) {
			String partNumber = clip.getAttribute("partnumber");
			if ("PN_Clip".equalsIgnoreCase(partNumber)) {
				continue;
			}
			filterCol.add(clip);
		}
		return filterCol;
	}
}

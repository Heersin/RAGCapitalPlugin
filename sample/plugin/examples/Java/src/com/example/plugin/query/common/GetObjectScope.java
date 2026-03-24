package com.example.plugin.query.common;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.IXMulticore;
import com.mentor.chs.api.IXHarness;

import java.util.Set;

public class GetObjectScope extends BaseCustomResultExpression
{
	private final String wireMCScope = "Wire_MC";
	private final String harnessScope = "Harness";

	public GetObjectScope()
	{
		super("GetObjectScope", "1.0", "Get the scope of the counter to be used");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.Multicore,Context.Wire};
	}

	public Object evaluate(IXObject entity)
	{
		if (entity instanceof IXWire ) {
			IXMulticore mc = ((IXWire)entity).getMulticore();
			if( mc == null){
				return wireMCScope;
			}
			else{
				return harnessScope ;
			}
		}
		else if(entity instanceof IXMulticore){
			return wireMCScope ;
		}
		else{
			return harnessScope ;
		}
	}
}
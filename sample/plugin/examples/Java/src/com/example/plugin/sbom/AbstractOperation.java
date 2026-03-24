/**
 * Copyright 2011 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely 
 * for internal purposes to serve as example plugin.
 * This code may not be used in a commercial distribution. Recipients may 
 * duplicate the code provided that all notices are fully reproduced with 
 * and remain in the code. No part of this code may be modified, reproduced, 
 * translated, used, distributed, disclosed or provided to third parties 
 * without the prior written consent of Mentor Graphics, except as expressly 
 * authorized above. 
 * <p>
 * THE CODE IS MADE AVAILABLE "AS IS" WITHOUT WARRANTY OR SUPPORT OF ANY KIND. 
 * MENTOR GRAPHICS OFFERS NO EXPRESS OR IMPLIED WARRANTIES AND SPECIFICALLY 
 * DISCLAIMS ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, 
 * OR WARRANTY OF NON-INFRINGEMENT. IN NO EVENT SHALL MENTOR GRAPHICS OR ITS 
 * LICENSORS BE LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING LOST PROFITS OR SAVINGS) WHETHER BASED ON CONTRACT, TORT 
 * OR ANY OTHER LEGAL THEORY, EVEN IF MENTOR GRAPHICS OR ITS LICENSORS HAVE BEEN 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * <p>
 */

package com.example.plugin.sbom;

import com.example.plugin.BasePlugin;
import com.mentor.chs.plugin.sbom.IXOperation;
import com.mentor.chs.plugin.sbom.IXOperationUI;
import com.mentor.chs.plugin.sbom.IXOperationParametersSerializer;

import javax.swing.Icon;

/**
 * An abstract SBOM operation class, providing common operation type implementation
 */
public abstract class AbstractOperation extends BasePlugin implements IXOperation
{

	protected AbstractOperation(
			String n,
			String v,
			String d)
	{
		super(n, v, d);
	}

	public enum OperationTypeImpl implements IXOperationType
	{
		WIRE_PREPARATION("Exercise - Wire Preparation", (short) 110),
		CONNNECTOR_LOADING("Exercise - Connector Loading", (short) 120),
		OTHER("Exercise - Other", (short) 310);

		private final String name;
		private final short orderHint;

		OperationTypeImpl(String name, short orderHint)
		{
			this.name = name;
			this.orderHint = orderHint;
		}

		@Override public String getName()
		{
			return name;
		}

		@Override public Icon getIcon()
		{
			return null;
		}

		@Override public short getOrderHint()
		{
			return orderHint;
		}

		@Override public StandardOperationTypeEnum getStandardType()
		{
			return null;
		}
	}

	@Override public IXOperationType getType()
	{
		return OperationTypeImpl.OTHER;
	}

	@Override public IXOperationParametersSerializer getParametersSerializer()
	{
		return null;
	}

	@Override public IXOperationUI getUI()
	{
		return null;
	}

	@Override public void reset()
	{
		// No-Op
	}
}

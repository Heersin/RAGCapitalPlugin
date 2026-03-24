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

import com.mentor.chs.api.sbom.IXSubAssembly;
import com.mentor.chs.plugin.sbom.IXSBOMContext;

import java.util.Set;

/**
 * Example of a greedy operation.
 * Academic only: not to be used in actual flows
 */
public class GreedyOperation extends AbstractOperation
{
    public GreedyOperation()
    {
        super("Example operation: Greedy operation",
                "1.0",
                "Warning: makes the SBOM generation engine fail");
    }

	@Override
	public int getOrderHint()
	{
		return 700;
	}

	@Override
    public boolean runSelector(IXSubAssembly subAssembly, final IXSBOMContext context,
                               Set<IXSubAssembly> combinedObjects)
    {
		// This operation is referred to as greedy because it fires inconditionally
		// and doesn't consume any combined fact.
		// As a result the engine produces a parent with the input sub-assembly as its
		// only child, so each iteration will produce the same number of facts as the
		// previous and the SBOM content will never converge to a single root sub-assembly.
		// The engine will detect this condition and issue a proper error message in
		// the SBOM console.
        return true;
    }
}
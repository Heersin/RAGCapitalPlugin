/**
 * Copyright 2010 Mentor Graphics Corporation. All Rights Reserved.
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
package com.example.plugin.action;

import com.mentor.chs.plugin.action.IXLogicAction;
import com.mentor.chs.plugin.action.IXHarnessAction;
import com.mentor.chs.plugin.action.IXIntegratorAction;
import com.mentor.chs.plugin.action.IXTopologyAction;
import com.mentor.chs.plugin.IXApplicationContext;

import javax.swing.Icon;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A custom action which emulates the Print To PDF File functionality using
 * IXApplicationContext.printToPDF() API.
 */
public class PrintToFileAction implements IXLogicAction, IXIntegratorAction, IXHarnessAction, IXTopologyAction
{
	public String getName()
	{
		return "Print to File...";
	}

    public String getDescription()
    {
        return "Print diagrams to a file using various formats such as PDF or DXF";
    }

    public String getLongDescription()
    {
        return "Print diagrams to a file using various formats such as PDF or DXF";
    }

    public String getVersion()
    {
        return "1.0";
    }

    public Trigger[] getTriggers()
    {
        return new Trigger[] { Trigger.MainMenu };
    }

    public Icon getSmallIcon()
    {
        return null;
    }

    public Integer getMnemonicKey()
    {
        return null;
    }

    public boolean isReadOnly()
    {
        return true;
    }

    public boolean isAvailable(IXApplicationContext context)
    {
        return true;
    }

	public boolean execute(IXApplicationContext context)
	{
		PrintToFileDialog dlg = new PrintToFileDialog(this, context);
		dlg.displayDialog();

		if (dlg.wasSuccessful()) {
			PrintToFileDialog.FormatInfo info = dlg.getSelectedFormatInfo();

			if (info != null) {
				File outFile = dlg.getOutputFile();
				FileOutputStream output = null;
				try {
					output = new FileOutputStream(outFile);
					info.print(output);
					return true;
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (output != null) {
						try {
							output.close();
						}
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return false;
	}
}